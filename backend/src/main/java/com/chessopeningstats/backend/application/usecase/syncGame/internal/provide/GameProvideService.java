package com.chessopeningstats.backend.application.usecase.syncGame.internal.provide;

import com.chessopeningstats.backend.application.usecase.syncGame.internal.provide.internal.adapt.dto.NormalizedGameDto;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.domain.Platform;

import java.util.List;

public interface GameProvideService {
    Platform platform();

    List<NormalizedGameDto> provideGames(Player Player);
}
