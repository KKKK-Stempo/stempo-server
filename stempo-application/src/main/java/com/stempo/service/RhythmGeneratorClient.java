package com.stempo.service;

import com.stempo.dto.request.RhythmRequestDto;
import com.stempo.exception.BaseException;
import com.stempo.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class RhythmGeneratorClient {

    private final RestClient restClient;

    public byte[] requestRhythm(RhythmRequestDto requestDto) {
        ResponseEntity<byte[]> responseEntity;

        try {
            responseEntity = restClient.post()
                .uri("/api/v1/rhythm")
                .body(requestDto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ((request, response) -> {
                    throw new BaseException(ErrorCode.RHYTHM_GENERATION_ERROR, "잘못된 요청입니다.");
                }))
                .onStatus(HttpStatusCode::is5xxServerError, ((request, response) -> {
                    throw new BaseException(ErrorCode.RHYTHM_GENERATION_ERROR, "리듬 생성 중 오류가 발생했습니다.");
                }))
                .toEntity(byte[].class);
        } catch (Exception e) {
            throw new BaseException(ErrorCode.RHYTHM_GENERATION_ERROR, "리듬 생성 API 호출 중 오류가 발생했습니다: " + e.getMessage());
        }

        byte[] wavData = responseEntity.getBody();
        if (wavData == null || wavData.length == 0) {
            throw new BaseException(ErrorCode.RHYTHM_GENERATION_ERROR, "리듬 데이터가 비어 있습니다.");
        }

        return wavData;
    }
}
