package com.stempo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stempo.dto.request.RhythmRequestDto;
import com.stempo.exception.BaseException;
import com.stempo.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@ExtendWith(MockitoExtension.class)
class RhythmGeneratorClientTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.RequestBodySpec requestBodySpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private RhythmGeneratorClient rhythmGeneratorClient;

    @BeforeEach
    void setup() {
        rhythmGeneratorClient = new RhythmGeneratorClient(restClient);
    }

    /**
     * 성공적인 응답을 모킹하기 위한 헬퍼 메서드.
     */
    private void mockSuccessfulResponse(RhythmRequestDto requestDto, byte[] responseBody) {
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/api/v1/rhythm")).thenReturn(requestBodySpec);
        when(requestBodySpec.body(requestDto)).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);

        // 동일한 responseSpec을 반환하도록 두 onStatus 호출을 모두 모킹
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.toEntity(byte[].class)).thenReturn(new ResponseEntity<>(responseBody, HttpStatus.OK));
    }

    /**
     * 예외 응답을 모킹하기 위한 헬퍼 메서드.
     */
    private void mockExceptionResponse(RhythmRequestDto requestDto, Exception exception) {
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/api/v1/rhythm")).thenReturn(requestBodySpec);
        when(requestBodySpec.body(requestDto)).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);

        // 동일한 responseSpec을 반환하도록 두 onStatus 호출을 모두 모킹
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.toEntity(byte[].class)).thenThrow(exception);
    }

    @Test
    void 리듬_생성_요청하면_생성된_리듬을_반환한다() {
        // given
        RhythmRequestDto requestDto = new RhythmRequestDto();
        requestDto.setBit(4);
        requestDto.setBpm(120);

        byte[] expectedResponse = "mock-wav-data".getBytes();

        mockSuccessfulResponse(requestDto, expectedResponse);

        // when
        byte[] result = rhythmGeneratorClient.requestRhythm(requestDto);

        // then
        assertThat(result).isEqualTo(expectedResponse);

        verify(restClient).post();
        verify(requestBodyUriSpec).uri("/api/v1/rhythm");
        verify(requestBodySpec).body(requestDto);
        verify(requestBodySpec).retrieve();
        verify(responseSpec, times(2)).onStatus(any(), any()); // Two onStatus calls
        verify(responseSpec).toEntity(byte[].class);
    }

    @Test
    void 잘못된_요청일_경우_예외를_발생시킨다() {
        // given
        RhythmRequestDto requestDto = new RhythmRequestDto();
        requestDto.setBit(10);
        requestDto.setBpm(5);

        mockExceptionResponse(requestDto, new BaseException(ErrorCode.RHYTHM_GENERATION_ERROR, "잘못된 요청입니다."));

        // when & then
        BaseException exception = assertThrows(BaseException.class,
            () -> rhythmGeneratorClient.requestRhythm(requestDto));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RHYTHM_GENERATION_ERROR);
        assertThat(exception.getMessage()).contains("잘못된 요청입니다.");

        verify(restClient).post();
        verify(requestBodyUriSpec).uri("/api/v1/rhythm");
        verify(requestBodySpec).body(requestDto);
        verify(requestBodySpec).retrieve();
        verify(responseSpec, times(2)).onStatus(any(), any());
        verify(responseSpec).toEntity(byte[].class);
    }

    @Test
    void 서버_오류가_발생할_경우_예외를_발생시킨다() {
        // given
        RhythmRequestDto requestDto = new RhythmRequestDto();
        requestDto.setBit(4);
        requestDto.setBpm(120);

        mockExceptionResponse(requestDto, new BaseException(ErrorCode.RHYTHM_GENERATION_ERROR, "리듬 생성 중 오류가 발생했습니다."));

        // when & then
        BaseException exception = assertThrows(BaseException.class,
            () -> rhythmGeneratorClient.requestRhythm(requestDto));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RHYTHM_GENERATION_ERROR);
        assertThat(exception.getMessage()).contains("리듬 생성 중 오류가 발생했습니다.");

        verify(restClient).post();
        verify(requestBodyUriSpec).uri("/api/v1/rhythm");
        verify(requestBodySpec).body(requestDto);
        verify(requestBodySpec).retrieve();
        verify(responseSpec, times(2)).onStatus(any(), any());
        verify(responseSpec).toEntity(byte[].class);
    }

    @Test
    void 응답이_비어있을_경우_예외를_발생시킨다() {
        // given
        RhythmRequestDto requestDto = new RhythmRequestDto();
        requestDto.setBit(4);
        requestDto.setBpm(120);

        mockSuccessfulResponse(requestDto, new byte[0]);

        // when & then
        BaseException exception = assertThrows(BaseException.class,
            () -> rhythmGeneratorClient.requestRhythm(requestDto));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RHYTHM_GENERATION_ERROR);
        assertThat(exception.getMessage()).contains("리듬 데이터가 비어 있습니다.");

        verify(restClient).post();
        verify(requestBodyUriSpec).uri("/api/v1/rhythm");
        verify(requestBodySpec).body(requestDto);
        verify(requestBodySpec).retrieve();
        verify(responseSpec, times(2)).onStatus(any(), any());
        verify(responseSpec).toEntity(byte[].class);
    }

    @Test
    void API_호출_실패시_예외를_발생시킨다() {
        // given
        RhythmRequestDto requestDto = new RhythmRequestDto();
        requestDto.setBit(4);
        requestDto.setBpm(120);

        mockExceptionResponse(requestDto, new RestClientException("API 호출 실패"));

        // when & then
        BaseException exception = assertThrows(BaseException.class,
            () -> rhythmGeneratorClient.requestRhythm(requestDto));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RHYTHM_GENERATION_ERROR);
        assertThat(exception.getMessage()).contains("리듬 생성 API 호출 중 오류가 발생했습니다");

        verify(restClient).post();
        verify(requestBodyUriSpec).uri("/api/v1/rhythm");
        verify(requestBodySpec).body(requestDto);
        verify(requestBodySpec).retrieve();
        verify(responseSpec, times(2)).onStatus(any(), any());
        verify(responseSpec).toEntity(byte[].class);
    }
}
