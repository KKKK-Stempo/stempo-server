package com.stempo.api.domain.application.service;

import com.stempo.api.domain.application.exception.DirectoryCreationException;
import com.stempo.api.domain.application.exception.RhythmGenerationException;
import com.stempo.api.domain.domain.model.UploadedFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RhythmServiceImpl implements RhythmService {

    private final UploadedFileService uploadedFileService;
    private final FileService fileService;

    @Value("${python.venv.path}")
    private String venvPath;

    @Value("${python.script.path}")
    private String scriptPath;

    @Override
    public String createRhythm(int bpm) {
        try {
            validateBpmRange(bpm);
            String outputFilename = "rhythm_" + bpm + "_bpm.wav";

            Optional<UploadedFile> uploadedFile = uploadedFileService.getUploadedFileByOriginalFileName(outputFilename);
            if (uploadedFile.isPresent()) {
                return uploadedFile.get().getUrl();
            }

            Path outputFilePath = generateRhythmFile(bpm, outputFilename);
            return saveGeneratedFile(outputFilePath);
        } catch (Exception e) {
            throw new RhythmGenerationException("Error generating rhythm: " + e.getMessage(), e);
        }
    }

    private void validateBpmRange(int bpm) {
        int minBpm = 10;
        int maxBpm = 200;
        if (bpm < minBpm || bpm > maxBpm) {
            throw new RhythmGenerationException("BPM must be between " + minBpm + " and " + maxBpm);
        }
    }

    private Path generateRhythmFile(int bpm, String outputFilename) throws Exception {
        Path projectRoot = Paths.get("").toAbsolutePath();
        Path venvPythonPath = projectRoot.resolve(venvPath);
        Path scriptAbsolutePath = projectRoot.resolve(scriptPath);
        Path outputDir = projectRoot.resolve("generated");

        createOutputDirectoryIfNotExists(outputDir);
        runPythonScript(bpm, venvPythonPath, scriptAbsolutePath, projectRoot);
        return outputDir.resolve(outputFilename);
    }

    private void createOutputDirectoryIfNotExists(Path outputDir) {
        if (!outputDir.toFile().exists()) {
            boolean dirCreated = outputDir.toFile().mkdirs();
            if (!dirCreated) {
                throw new DirectoryCreationException("Failed to create output directory: " + outputDir.toString());
            }
        }
    }

    private void runPythonScript(int bpm, Path venvPythonPath, Path scriptAbsolutePath, Path projectRoot) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(venvPythonPath.toString(), scriptAbsolutePath.toString(), String.valueOf(bpm));
        pb.directory(projectRoot.toFile());
        Process process = pb.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RhythmGenerationException("Python script exited with code: " + exitCode);
        }
    }

    private String saveGeneratedFile(Path outputFilePath) {
        if (outputFilePath.toFile().exists()) {
            try {
                return fileService.saveFile(outputFilePath.toFile());
            } catch (Exception e) {
                log.error("Error saving generated file: " + e.getMessage(), e);
                throw new RhythmGenerationException("Error saving generated file: " + e.getMessage(), e);
            }
        } else {
            throw new RhythmGenerationException("Generated rhythm file does not exist: " + outputFilePath.toString());
        }
    }
}
