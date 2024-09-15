package com.stempo.api.domain.application.service;

import com.stempo.api.domain.application.exception.DirectoryCreationException;
import com.stempo.api.domain.application.exception.RhythmGenerationException;
import com.stempo.api.domain.domain.model.UploadedFile;
import com.stempo.api.domain.presentation.dto.request.RhythmRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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
    public String createRhythm(RhythmRequestDto requestDto) {
        int bpm = requestDto.getBpm();
        int bit = requestDto.getBit();

        try {
            String outputFilename = "rhythm_" + bpm + "_" + bit + "_bpm.wav";

            Optional<UploadedFile> uploadedFile = uploadedFileService.getUploadedFileByOriginalFileName(outputFilename);
            if (uploadedFile.isPresent()) {
                return uploadedFile.get().getUrl();
            }

            Path outputFilePath = generateRhythmFile(bpm, bit, outputFilename);
            return saveGeneratedFile(outputFilePath);
        } catch (Exception e) {
            throw new RhythmGenerationException("Error generating rhythm: " + e.getMessage(), e);
        }
    }

    private Path generateRhythmFile(int bpm, int bit, String outputFilename) throws Exception {
        Path projectRoot = Paths.get("").toAbsolutePath();
        Path venvPythonPath = projectRoot.resolve(venvPath);
        Path scriptAbsolutePath = projectRoot.resolve(scriptPath);
        Path outputDir = projectRoot.resolve("generated");

        createOutputDirectoryIfNotExists(outputDir);
        runPythonScript(bpm, bit, venvPythonPath, scriptAbsolutePath, projectRoot);
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

    private void runPythonScript(int bpm, int bit, Path venvPythonPath, Path scriptAbsolutePath, Path projectRoot) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(venvPythonPath.toString(), scriptAbsolutePath.toString(), String.valueOf(bpm), String.valueOf(bit));
        pb.directory(projectRoot.toFile());
        pb.redirectErrorStream(true); // 표준 오류를 표준 출력으로 병합
        Process process = pb.start();

        // 프로세스의 출력 로그 확인
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.error(line);
            }
        }

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
