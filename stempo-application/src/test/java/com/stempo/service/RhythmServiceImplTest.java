package com.stempo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.stempo.dto.request.RhythmRequestDto;
import com.stempo.exception.RhythmGenerationException;
import com.stempo.model.UploadedFile;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class RhythmServiceImplTest {

    @Mock
    private UploadedFileService uploadedFileService;

    @Mock
    private FileService fileService;

    @InjectMocks
    private RhythmServiceImpl rhythmService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(rhythmService, "venvPath", "venv/bin/python");
        ReflectionTestUtils.setField(rhythmService, "scriptPath", "scripts/generate_rhythm.py");
    }

    @Test
    void 이미_파일이_존재하면_기존_URL을_반환한다() {
        // given
        int bpm = 120;
        int bit = 4;
        String outputFilename = "rhythm_" + bpm + "_" + bit + "_bpm.wav";
        String existingUrl = "http://example.com/files/" + outputFilename;

        RhythmRequestDto requestDto = new RhythmRequestDto();
        requestDto.setBpm(bpm);
        requestDto.setBit(bit);

        UploadedFile existingFile = UploadedFile.builder()
                .url(existingUrl)
                .originalFileName(outputFilename)
                .build();

        when(uploadedFileService.getUploadedFileByOriginalFileName(outputFilename))
                .thenReturn(Optional.of(existingFile));

        // when
        String resultUrl = rhythmService.createRhythm(requestDto);

        // then
        assertThat(resultUrl).isEqualTo(existingUrl);
        verify(uploadedFileService).getUploadedFileByOriginalFileName(outputFilename);
        verifyNoMoreInteractions(uploadedFileService, fileService);
    }

    @Test
    void 파일이_존재하지_않으면_새로운_파일을_생성하고_URL을_반환한다() throws Exception {
        // given
        int bpm = 120;
        int bit = 4;
        String outputFilename = "rhythm_" + bpm + "_" + bit + "_bpm.wav";
        String generatedUrl = "http://example.com/files/" + outputFilename;

        RhythmRequestDto requestDto = new RhythmRequestDto();
        requestDto.setBpm(bpm);
        requestDto.setBit(bit);

        when(uploadedFileService.getUploadedFileByOriginalFileName(outputFilename))
                .thenReturn(Optional.empty());

        // Mock Paths.get() 및 관련 메서드
        try (MockedStatic<Paths> mockedPaths = mockStatic(Paths.class)) {
            Path projectRoot = mock(Path.class);
            Path venvPythonPath = mock(Path.class);
            Path scriptAbsolutePath = mock(Path.class);
            Path outputDir = mock(Path.class);
            Path outputFilePath = mock(Path.class);

            mockedPaths.when(() -> Paths.get("")).thenReturn(projectRoot);
            when(projectRoot.toAbsolutePath()).thenReturn(projectRoot);
            when(projectRoot.resolve("venv/bin/python")).thenReturn(venvPythonPath);
            when(projectRoot.resolve("scripts/generate_rhythm.py")).thenReturn(scriptAbsolutePath);
            when(projectRoot.resolve("generated")).thenReturn(outputDir);
            when(outputDir.resolve(outputFilename)).thenReturn(outputFilePath);

            // outputDir.toFile().exists() 모의화
            File outputDirFile = mock(File.class);
            when(outputDir.toFile()).thenReturn(outputDirFile);
            when(outputDirFile.exists()).thenReturn(true);

            // outputFilePath.toFile().exists() 모의화
            File outputFile = mock(File.class);
            when(outputFilePath.toFile()).thenReturn(outputFile);
            when(outputFile.exists()).thenReturn(true);

            // ProcessBuilder와 Process 모의화
            Process process = mock(Process.class);
            when(process.waitFor()).thenReturn(0);
            when(process.getInputStream()).thenReturn(new ByteArrayInputStream("Process output".getBytes()));

            try (MockedConstruction<ProcessBuilder> mockedProcessBuilder = mockConstruction(ProcessBuilder.class,
                    (processBuilder, context) -> when(processBuilder.start()).thenReturn(process))) {

                // fileService.saveFile() 모의화
                when(fileService.saveFile(outputFile)).thenReturn(generatedUrl);

                // when
                String resultUrl = rhythmService.createRhythm(requestDto);

                // then
                assertThat(resultUrl).isEqualTo(generatedUrl);
                verify(uploadedFileService).getUploadedFileByOriginalFileName(outputFilename);
                verify(fileService).saveFile(outputFile);
            }
        }
    }

    @Test
    void 생성된_파일이_존재하지_않으면_RhythmGenerationException을_던진다() throws Exception {
        // given
        int bpm = 120;
        int bit = 4;
        String outputFilename = "rhythm_" + bpm + "_" + bit + "_bpm.wav";

        RhythmRequestDto requestDto = new RhythmRequestDto();
        requestDto.setBpm(bpm);
        requestDto.setBit(bit);

        when(uploadedFileService.getUploadedFileByOriginalFileName(outputFilename))
                .thenReturn(Optional.empty());

        // Mock Paths.get() 및 관련 메서드
        try (MockedStatic<Paths> mockedPaths = mockStatic(Paths.class)) {
            Path projectRoot = mock(Path.class);
            Path venvPythonPath = mock(Path.class);
            Path scriptAbsolutePath = mock(Path.class);
            Path outputDir = mock(Path.class);
            Path outputFilePath = mock(Path.class);

            mockedPaths.when(() -> Paths.get("")).thenReturn(projectRoot);
            when(projectRoot.toAbsolutePath()).thenReturn(projectRoot);
            when(projectRoot.resolve("venv/bin/python")).thenReturn(venvPythonPath);
            when(projectRoot.resolve("scripts/generate_rhythm.py")).thenReturn(scriptAbsolutePath);
            when(projectRoot.resolve("generated")).thenReturn(outputDir);
            when(outputDir.resolve(outputFilename)).thenReturn(outputFilePath);

            // outputDir.toFile().exists() 모의화
            File outputDirFile = mock(File.class);
            when(outputDir.toFile()).thenReturn(outputDirFile);
            when(outputDirFile.exists()).thenReturn(true);

            // outputFilePath.toFile().exists() 모의화
            File outputFile = mock(File.class);
            when(outputFilePath.toFile()).thenReturn(outputFile);
            when(outputFile.exists()).thenReturn(false);

            // ProcessBuilder와 Process 모의화
            Process process = mock(Process.class);
            when(process.waitFor()).thenReturn(0);
            when(process.getInputStream()).thenReturn(new ByteArrayInputStream("Process output".getBytes()));

            try (MockedConstruction<ProcessBuilder> mockedProcessBuilder = mockConstruction(ProcessBuilder.class,
                    (processBuilder, context) -> when(processBuilder.start()).thenReturn(process))) {

                // when, then
                assertThatThrownBy(() -> rhythmService.createRhythm(requestDto))
                        .isInstanceOf(RhythmGenerationException.class)
                        .hasMessageContaining("Generated rhythm file does not exist");

                verify(uploadedFileService).getUploadedFileByOriginalFileName(outputFilename);
            }
        }
    }
}
