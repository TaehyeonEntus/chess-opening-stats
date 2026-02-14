package com.chessopeningstats.backend.application.stat;

import com.chessopeningstats.backend.application.stat.dto.Stat;
import com.chessopeningstats.backend.infra.repository.GamePlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatService {
    private final GamePlayerRepository gamePlayerRepository;

    public List<Stat> getAllStats(){
        return gamePlayerRepository.findAllStats();
    }

    public List<Stat> getStatsByPlayerId(long playerId) {
        return gamePlayerRepository.findAllStatsByPlayerId(playerId);
    }
}
