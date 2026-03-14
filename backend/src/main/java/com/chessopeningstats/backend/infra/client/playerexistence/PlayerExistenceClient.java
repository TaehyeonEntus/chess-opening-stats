package com.chessopeningstats.backend.infra.client.playerexistence;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.infra.client.playerexistence.dto.PlayerExistenceDto;
import org.springframework.web.client.RestClient;

/**
 * Platform으로부터 유저 여부를 확인할 수 있는 클라이언트
 */
public interface PlayerExistenceClient {
    Platform platform();

    RestClient client();

    String uri(String username);

    PlayerExistenceDto existsUsername(String username);
}
