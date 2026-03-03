package com.chessopeningstats.backend.application.usecase.syncGame.internal.provide;

import com.chessopeningstats.backend.application.usecase.syncGame.internal.provide.internal.adapt.LichessGameAdaptService;
import com.chessopeningstats.backend.application.usecase.syncGame.internal.provide.internal.adapt.dto.NormalizedGameDto;
import com.chessopeningstats.backend.application.usecase.syncGame.internal.provide.internal.fetch.LichessGameFetchService;
import com.chessopeningstats.backend.application.usecase.syncGame.internal.provide.internal.filter.GameFilterService;
import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.util.LogExecutionTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class LichessGameProvideService implements GameProvideService {
    private final LichessGameFetchService fetchService;
    private final LichessGameAdaptService adaptService;
    private final GameFilterService gameFilterService;

    @Override
    public Platform platform() {
        return Platform.LICHESS;
    }

    @Override
    @LogExecutionTime
    public Flux<NormalizedGameDto> provideGames(Player Player) {
        return gameFilterService.filterNormalGames(adaptService.adaptAll(Player, fetchService.fetch(Player)));
    }
}
