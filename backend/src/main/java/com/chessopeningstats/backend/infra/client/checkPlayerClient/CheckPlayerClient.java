package com.chessopeningstats.backend.infra.client.checkPlayerClient;

import com.chessopeningstats.backend.domain.Platform;

public interface CheckPlayerClient {
    Platform platform();
    boolean checkPlayer(String username);
}
