package com.chessopeningstats.backend.application.usecase.syncGame.internal.provide;

import com.chessopeningstats.backend.application.usecase.syncGame.internal.provide.internal.adapt.dto.NormalizedGameDto;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.domain.Platform;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Stream;

public interface GameProvideService {
    Platform platform();

    Flux<NormalizedGameDto> provideGames(Player Player);
}
