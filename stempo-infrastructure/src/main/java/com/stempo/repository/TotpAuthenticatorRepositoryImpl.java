package com.stempo.repository;

import com.stempo.entity.TotpAuthenticatorEntity;
import com.stempo.exception.NotFoundException;
import com.stempo.mapper.TotpAuthenticatorMapper;
import com.stempo.model.TotpAuthenticator;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TotpAuthenticatorRepositoryImpl implements TotpAuthenticatorRepository {

    private final TotpAuthenticatorJpaRepository repository;
    private final TotpAuthenticatorMapper mapper;

    @Override
    public Optional<TotpAuthenticator> findById(String deviceTag) {
        return repository.findById(deviceTag)
                .map(mapper::toDomain);
    }

    @Override
    public TotpAuthenticator getById(String deviceTag) {
        return repository.findById(deviceTag)
                .map(mapper::toDomain)
                .orElseThrow(() -> new NotFoundException("[TotpAuthenticator] deviceTag: " + deviceTag + " not found"));
    }

    @Override
    public boolean existsById(String deviceTag) {
        return repository.existsById(deviceTag);
    }

    @Override
    public void delete(TotpAuthenticator authenticator) {
        TotpAuthenticatorEntity entity = mapper.toEntity(authenticator);
        repository.delete(entity);
    }

    @Override
    public void save(TotpAuthenticator authenticator) {
        TotpAuthenticatorEntity entity = mapper.toEntity(authenticator);
        repository.save(entity);
    }
}
