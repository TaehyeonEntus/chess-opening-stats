package com.chessopeningstats.backend.application.usecase.syncGame.internal.provide;

import com.chessopeningstats.backend.application.usecase.syncGame.internal.provide.internal.adapt.ChessComGameAdaptService;
import com.chessopeningstats.backend.application.usecase.syncGame.internal.provide.internal.adapt.dto.NormalizedGameDto;
import com.chessopeningstats.backend.application.usecase.syncGame.internal.provide.internal.fetch.ChessComGameFetchService;
import com.chessopeningstats.backend.application.usecase.syncGame.internal.provide.internal.filter.GameFilterService;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.util.LogExecutionTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChessComGameProvideService implements GameProvideService {
    private final ChessComGameFetchService fetchService;
    private final ChessComGameAdaptService adaptService;
    private final GameFilterService gameFilterService;

    @Override
    public Platform platform() {
        return Platform.CHESS_COM;
    }

    @Override
    @LogExecutionTime
    public List<NormalizedGameDto> provideGames(Player Player) {
        return gameFilterService.filterNormalGames(adaptService.adaptAll(Player, fetchService.fetch(Player)).stream()
                .toList());
    }
}
