package com.chessopeningstats.backend.application.syncGame.internal.provide;

import com.chessopeningstats.backend.application.syncGame.internal.provide.internal.adapt.dto.NormalizedGameDto;
import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Account;
import com.chessopeningstats.backend.util.LogExecutionTime;

import java.util.List;

public interface GameProvideService {
    Platform platform();

    List<NormalizedGameDto> provideGames(Account account);
}
