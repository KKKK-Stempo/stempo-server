package com.stempo.service;

import com.stempo.dto.request.RhythmRequestDto;
import com.stempo.model.UploadedFile;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RhythmServiceImpl implements RhythmService {

    private final RhythmGeneratorClient rhythmGeneratorClient;
    private final UploadedFileService uploadedFileService;
    private final FileService fileService;

    @Override
    @Transactional
    public String createRhythm(RhythmRequestDto requestDto) {
        int bpm = requestDto.getBpm();
        int bit = requestDto.getBit();

        String outputFilename = "rhythm_" + bpm + "_" + bit + "_bpm.wav";

        Optional<UploadedFile> uploadedFile = uploadedFileService.getUploadedFileByOriginalFileName(outputFilename);
        if (uploadedFile.isPresent()) {
            return uploadedFile.get().getUrl();
        }

        byte[] wavData = rhythmGeneratorClient.createRhythm(requestDto);

        return fileService.saveRhythmFile(wavData, outputFilename);
    }
}
