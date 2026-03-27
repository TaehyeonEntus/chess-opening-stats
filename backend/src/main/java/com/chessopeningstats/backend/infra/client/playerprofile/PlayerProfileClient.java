package com.chessopeningstats.backend.infra.client.playerprofile;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.infra.client.playerprofile.dto.PlayerProfile;

/**
 * Platform으로부터 유저 유무를 확인할 수 있는 클라이언트
 */
public interface PlayerProfileClient{
    Platform platform();

    String uri(String username);

    PlayerProfile fetchPlayerProfile(String username);
}
