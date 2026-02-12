package com.chessopeningstats.backend.infra.client.checkAccountClient;

import com.chessopeningstats.backend.domain.Platform;

public interface CheckAccountClient {
    Platform platform();
    boolean checkAccount(String username);
}
