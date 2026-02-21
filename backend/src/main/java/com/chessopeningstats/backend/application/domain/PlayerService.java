package com.chessopeningstats.backend.application.domain;

import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.exception.PlayerNotFoundException;
import com.chessopeningstats.backend.infra.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepository playerRepository;

    public Player savePlayer(Player Player) {
        return playerRepository.save(Player);
    }

    public Player getPlayer(long playerId) {
        return playerRepository.findById(playerId).orElseThrow(PlayerNotFoundException::new);
    }


    public Player getPlayerByUsernameAndPlatform(String username, Platform platform) {
        return playerRepository.findByUsernameAndPlatform(username, platform).orElseThrow(PlayerNotFoundException::new);
    }

    public boolean existsByUsernameAndPlatform(String username, Platform platform) {
        return playerRepository.existsByUsernameAndPlatform(username, platform);
    }
}
