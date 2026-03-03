package com.chessopeningstats.backend.application.usecase.syncGame.internal.provide.internal.adapt;

import com.chessopeningstats.backend.application.usecase.syncGame.internal.provide.internal.adapt.dto.NormalizedGameDto;
import com.chessopeningstats.backend.domain.Player;
import reactor.core.publisher.Flux;

public interface GameAdaptService<T> {
    default Flux<NormalizedGameDto> adaptAll(Player Player, Flux<T> rawGameDtos) {
        return rawGameDtos.map(rawGameDto -> adaptOne(Player, rawGameDto));
    }

    NormalizedGameDto adaptOne(Player Player, T rawGameDto);
}
