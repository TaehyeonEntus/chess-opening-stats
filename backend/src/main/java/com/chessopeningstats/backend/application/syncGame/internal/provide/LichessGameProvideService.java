package com.chessopeningstats.backend.application.syncGame.internal.provide;

import com.chessopeningstats.backend.application.syncGame.internal.provide.internal.adapt.dto.NormalizedGameDto;
import com.chessopeningstats.backend.application.syncGame.internal.provide.internal.adapt.LichessGameAdaptService;
import com.chessopeningstats.backend.application.syncGame.internal.provide.internal.fetch.LichessGameFetchService;
import com.chessopeningstats.backend.application.syncGame.internal.provide.internal.filter.GameFilterService;
import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Account;
import com.chessopeningstats.backend.util.LogExecutionTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public List<NormalizedGameDto> provideGames(Account account) {
        return gameFilterService.filterNormalGames(adaptService.adaptAll(account, fetchService.fetch(account)).stream()
                .toList());
    }
}
