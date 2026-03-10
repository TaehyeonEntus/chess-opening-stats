package com.chessopeningstats.backend.service;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.exception.PlayerNotFoundException;
import com.chessopeningstats.backend.infra.repository.PlayerRepository;
import com.chessopeningstats.backend.infra.repository.batch.PlayerBatchRepository;
import com.chessopeningstats.backend.web.view.dto.home.ColorRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final PlayerBatchRepository playerBatchRepository;

    public Player save(Player Player) {
        return playerRepository.save(Player);
    }

    public Player get(long playerId) {
        return playerRepository.findById(playerId).orElseThrow(PlayerNotFoundException::new);
    }

    public Player getByUsernameAndPlatform(String username, Platform platform) {
        return playerRepository.findByUsernameAndPlatform(username, platform).orElseThrow(PlayerNotFoundException::new);
    }

    public boolean existsByUsernameAndPlatform(String username, Platform platform) {
        return playerRepository.existsByUsernameAndPlatform(username, platform);
    }

    public List<ColorRecord> getRecordsByPlayerIds(List<Long> playerIds) {
        return !playerIds.isEmpty() ? playerRepository.findRecordsByPlayerIds(playerIds) : List.of();
    }

    @Transactional
    public void garbageCollect() {
        List<Long> orphanPlayerIds = playerRepository.findOrphanPlayerIds();
        if (!orphanPlayerIds.isEmpty())
            playerBatchRepository.deleteBatch(orphanPlayerIds);
    }
}
