package com.stempo.repository;

import com.stempo.model.TotpAuthenticator;

import java.util.Optional;

public interface TotpAuthenticatorRepository {

    Optional<TotpAuthenticator> findById(String deviceTag);

    TotpAuthenticator getById(String deviceTag);

    boolean existsById(String deviceTag);

    void delete(TotpAuthenticator authenticator);

    void save(TotpAuthenticator authenticator);
}
