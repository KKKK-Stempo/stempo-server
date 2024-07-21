package com.stempo.api.domain.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Slf4j
public class RhythmServiceImpl implements RhythmService {

    private final FileService fileService;

    @Value("${python.venv.path}")
    private String venvPath;

    @Value("${python.script.path}")
    private String scriptPath;

    public String createRhythm(int bpm) {
        try {
            Path projectRoot = Paths.get("").toAbsolutePath();
            Path venvPythonPath = projectRoot.resolve(venvPath);
            Path scriptAbsolutePath = projectRoot.resolve(scriptPath);
            Path outputDir = projectRoot.resolve("generated");

            if (!outputDir.toFile().exists()) {
                boolean dirCreated = outputDir.toFile().mkdirs();
                if (!dirCreated) {
                    throw new RuntimeException("Failed to create output directory");
                }
            }

            ProcessBuilder pb = new ProcessBuilder(venvPythonPath.toString(), scriptAbsolutePath.toString(), String.valueOf(bpm));
            pb.directory(projectRoot.toFile());
            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new RuntimeException("Script exited with code: " + exitCode);
            }

            String outputFilename = "rhythm_" + bpm + "_bpm.wav";
            Path outputFilePath = outputDir.resolve(outputFilename);

            if (outputFilePath.toFile().exists()) {
                return fileService.saveFile(outputFilePath.toFile());
            } else {
                throw new RuntimeException("Generated rhythm file does not exist");
            }
        } catch (Exception e) {
            log.error("Error in createRhythm: " + e.getMessage(), e);
            throw new RuntimeException("Error in createRhythm: " + e.getMessage(), e);
        }
    }
}
