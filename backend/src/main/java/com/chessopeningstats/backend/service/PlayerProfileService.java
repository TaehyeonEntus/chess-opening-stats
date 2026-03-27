package com.chessopeningstats.backend.service;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.infra.client.playerprofile.dto.PlayerProfile;
import com.chessopeningstats.backend.infra.client.playerprofile.PlayerProfileClientRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlayerProfileService {
    private final PlayerProfileClientRegistry registry;

    public PlayerProfile fetchPlayerProfile(Platform platform, String username) {
        return registry.getClient(platform).fetchPlayerProfile(username);
    }
}