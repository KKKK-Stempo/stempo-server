package com.stempo.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stempo.entity.TotpAuthenticatorEntity;
import com.stempo.exception.NotFoundException;
import com.stempo.mapper.TotpAuthenticatorMapper;
import com.stempo.model.TotpAuthenticator;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TotpAuthenticatorRepositoryImplTest {

    @Mock
    private TotpAuthenticatorJpaRepository totpAuthenticatorJpaRepository;

    @Mock
    private TotpAuthenticatorMapper totpAuthenticatorMapper;

    @InjectMocks
    private TotpAuthenticatorRepositoryImpl totpAuthenticatorRepository;

    private TotpAuthenticator totpAuthenticator;
    private TotpAuthenticatorEntity totpAuthenticatorEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        totpAuthenticator = TotpAuthenticator.builder()
                .deviceTag("device123")
                .secretKey("secretKey123")
                .build();

        totpAuthenticatorEntity = TotpAuthenticatorEntity.builder()
                .deviceTag("device123")
                .secretKey("secretKey123")
                .build();
    }

    @Test
    void 디바이스_태그로_인증정보를_조회하면_성공한다() {
        // given
        when(totpAuthenticatorJpaRepository.findById(anyString())).thenReturn(Optional.of(totpAuthenticatorEntity));
        when(totpAuthenticatorMapper.toDomain(any(TotpAuthenticatorEntity.class))).thenReturn(totpAuthenticator);

        // when
        Optional<TotpAuthenticator> result = totpAuthenticatorRepository.findById("device123");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getDeviceTag()).isEqualTo("device123");
        verify(totpAuthenticatorJpaRepository, times(1)).findById("device123");
    }

    @Test
    void 디바이스_태그로_인증정보를_조회할_때_존재하지_않으면_예외가_발생한다() {
        // given
        when(totpAuthenticatorJpaRepository.findById(anyString())).thenReturn(Optional.empty());

        // when, then
        assertThrows(NotFoundException.class, () -> totpAuthenticatorRepository.getById("device123"));
        verify(totpAuthenticatorJpaRepository, times(1)).findById("device123");
    }

    @Test
    void 디바이스_태그로_인증정보_존재여부를_확인하면_성공한다() {
        // given
        when(totpAuthenticatorJpaRepository.existsById(anyString())).thenReturn(true);

        // when
        boolean exists = totpAuthenticatorRepository.existsById("device123");

        // then
        assertThat(exists).isTrue();
        verify(totpAuthenticatorJpaRepository, times(1)).existsById("device123");
    }

    @Test
    void 인증정보를_삭제하면_성공한다() {
        // given
        when(totpAuthenticatorMapper.toEntity(any(TotpAuthenticator.class))).thenReturn(totpAuthenticatorEntity);

        // when
        totpAuthenticatorRepository.delete(totpAuthenticator);

        // then
        verify(totpAuthenticatorJpaRepository, times(1)).delete(totpAuthenticatorEntity);
    }

    @Test
    void 인증정보를_저장하면_성공한다() {
        // given
        when(totpAuthenticatorMapper.toEntity(any(TotpAuthenticator.class))).thenReturn(totpAuthenticatorEntity);

        // when
        totpAuthenticatorRepository.save(totpAuthenticator);

        // then
        verify(totpAuthenticatorJpaRepository, times(1)).save(totpAuthenticatorEntity);
    }
}
