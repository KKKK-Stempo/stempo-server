package com.stempo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.stempo.dto.request.RhythmRequestDto;
import com.stempo.exception.BaseException;
import com.stempo.exception.ErrorCode;
import com.stempo.model.UploadedFile;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RhythmServiceImplTest {

    @Mock
    private RhythmGeneratorClient rhythmGeneratorClient;

    @Mock
    private UploadedFileService uploadedFileService;

    @Mock
    private FileService fileService;

    @InjectMocks
    private RhythmServiceImpl rhythmServiceImpl;

    private RhythmRequestDto validRequestDto;
    private RhythmRequestDto invalidRequestDto;

    @BeforeEach
    void setUp() {
        validRequestDto = new RhythmRequestDto();
        validRequestDto.setBpm(120);
        validRequestDto.setBit(4);

        invalidRequestDto = new RhythmRequestDto();
        invalidRequestDto.setBpm(5);
        invalidRequestDto.setBit(10);
    }

    @Test
    void 이미_파일이_존재하면_기존_URL을_반환한다() {
        // given
        String outputFilename = "rhythm_120_4_bpm.wav";
        String existingUrl = "http://example.com/rhythm_120_4_bpm.wav";

        UploadedFile existingFile = UploadedFile.create(outputFilename, "saveFileName", "savedPath", existingUrl, 100L);
        existingFile.setOriginalFileName(outputFilename);
        existingFile.setUrl(existingUrl);

        when(uploadedFileService.getUploadedFileByOriginalFileName(outputFilename))
            .thenReturn(Optional.of(existingFile));

        // when
        String resultUrl = rhythmServiceImpl.createRhythm(validRequestDto);

        // then
        assertThat(resultUrl).isEqualTo(existingUrl);

        // verify
        verify(uploadedFileService, times(1)).getUploadedFileByOriginalFileName(outputFilename);
        verifyNoMoreInteractions(uploadedFileService, rhythmGeneratorClient, fileService);
    }

    @Test
    void 파일이_존재하지_않으면_새로운_파일을_생성하고_URL을_반환한다() {
        // given
        String outputFilename = "rhythm_120_4_bpm.wav";
        String newUrl = "http://example.com/rhythm_120_4_bpm.wav";
        byte[] wavData = "mock-wav-data".getBytes();

        when(uploadedFileService.getUploadedFileByOriginalFileName(outputFilename))
            .thenReturn(Optional.empty());

        when(rhythmGeneratorClient.requestRhythm(validRequestDto))
            .thenReturn(wavData);

        when(fileService.saveRhythmFile(wavData, outputFilename))
            .thenReturn(newUrl);

        // when
        String resultUrl = rhythmServiceImpl.createRhythm(validRequestDto);

        // then
        assertThat(resultUrl).isEqualTo(newUrl);

        // verify
        verify(uploadedFileService, times(1)).getUploadedFileByOriginalFileName(outputFilename);
        verify(rhythmGeneratorClient, times(1)).requestRhythm(validRequestDto);
        verify(fileService, times(1)).saveRhythmFile(wavData, outputFilename);
        verifyNoMoreInteractions(uploadedFileService, rhythmGeneratorClient, fileService);
    }

    @Test
    void RhythmGeneratorClient가_예외를_던지면_예외를_전파한다() {
        // given
        String outputFilename = "rhythm_120_4_bpm.wav";

        when(uploadedFileService.getUploadedFileByOriginalFileName(outputFilename))
            .thenReturn(Optional.empty());

        when(rhythmGeneratorClient.requestRhythm(validRequestDto))
            .thenThrow(new BaseException(ErrorCode.RHYTHM_GENERATION_ERROR, "리듬 생성 중 오류가 발생했습니다."));

        // when & then
        BaseException exception = assertThrows(BaseException.class, () -> {
            rhythmServiceImpl.createRhythm(validRequestDto);
        });

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RHYTHM_GENERATION_ERROR);
        assertThat(exception.getMessage()).isEqualTo("리듬 생성 중 오류가 발생했습니다.");

        // verify
        verify(uploadedFileService, times(1)).getUploadedFileByOriginalFileName(outputFilename);
        verify(rhythmGeneratorClient, times(1)).requestRhythm(validRequestDto);
        verifyNoMoreInteractions(uploadedFileService, rhythmGeneratorClient, fileService);
    }

    @Test
    void FileService가_예외를_던지면_예외를_전파한다() {
        // given
        String outputFilename = "rhythm_120_4_bpm.wav";
        byte[] wavData = "mock-wav-data".getBytes();

        when(uploadedFileService.getUploadedFileByOriginalFileName(outputFilename))
            .thenReturn(Optional.empty());

        when(rhythmGeneratorClient.requestRhythm(validRequestDto))
            .thenReturn(wavData);

        when(fileService.saveRhythmFile(wavData, outputFilename))
            .thenThrow(new BaseException(ErrorCode.RHYTHM_GENERATION_ERROR, "리듬 생성 중 오류가 발생했습니다."));

        // when & then
        BaseException exception = assertThrows(BaseException.class, () -> {
            rhythmServiceImpl.createRhythm(validRequestDto);
        });

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RHYTHM_GENERATION_ERROR);
        assertThat(exception.getMessage()).isEqualTo("리듬 생성 중 오류가 발생했습니다.");

        // verify
        verify(uploadedFileService, times(1)).getUploadedFileByOriginalFileName(outputFilename);
        verify(rhythmGeneratorClient, times(1)).requestRhythm(validRequestDto);
        verify(fileService, times(1)).saveRhythmFile(wavData, outputFilename);
        verifyNoMoreInteractions(uploadedFileService, rhythmGeneratorClient, fileService);
    }

    @Test
    void Rhythm_데이터가_비어있으면_BaseException을_던진다() {
        // given
        String outputFilename = "rhythm_120_4_bpm.wav";
        byte[] emptyWavData = new byte[0];

        when(uploadedFileService.getUploadedFileByOriginalFileName(outputFilename))
            .thenReturn(Optional.empty());

        when(rhythmGeneratorClient.requestRhythm(validRequestDto))
            .thenReturn(emptyWavData);

        when(fileService.saveRhythmFile(emptyWavData, outputFilename))
            .thenThrow(new BaseException(ErrorCode.RHYTHM_GENERATION_ERROR, "리듬 데이터가 비어 있습니다."));

        // when & then
        BaseException exception = assertThrows(BaseException.class, () -> {
            rhythmServiceImpl.createRhythm(validRequestDto);
        });

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RHYTHM_GENERATION_ERROR);
        assertThat(exception.getMessage()).isEqualTo("리듬 데이터가 비어 있습니다.");

        // verify
        verify(uploadedFileService, times(1)).getUploadedFileByOriginalFileName(outputFilename);
        verify(rhythmGeneratorClient, times(1)).requestRhythm(validRequestDto);
        verify(fileService, times(1)).saveRhythmFile(emptyWavData, outputFilename);
        verifyNoMoreInteractions(uploadedFileService, rhythmGeneratorClient, fileService);
    }
}
