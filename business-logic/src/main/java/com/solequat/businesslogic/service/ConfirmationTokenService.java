package com.solequat.businesslogic.service;

import java.util.Optional;

import com.core.entity.ConfirmationToken;

public interface ConfirmationTokenService {
    void saveConfirmationToken(ConfirmationToken token);

    Optional<ConfirmationToken> getToken(String token);

    void setConfirmedAt(String token);
}
