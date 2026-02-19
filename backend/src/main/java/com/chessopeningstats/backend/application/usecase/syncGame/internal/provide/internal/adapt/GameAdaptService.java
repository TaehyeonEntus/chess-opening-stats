package com.chessopeningstats.backend.application.usecase.syncGame.internal.provide.internal.adapt;

import com.chessopeningstats.backend.application.usecase.syncGame.internal.provide.internal.adapt.dto.NormalizedGameDto;
import com.chessopeningstats.backend.domain.Player;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public interface GameAdaptService<T> {

    default List<NormalizedGameDto> adaptAll(
            @NotNull Player Player,
            @NotNull List<@NotNull @Valid T> rawGameDtos
    ) {
        return rawGameDtos.parallelStream()
                .map(rawGameDto -> adaptOne(Player, rawGameDto))
                .toList();
    }

    NormalizedGameDto adaptOne(@NotNull Player Player, @NotNull @Valid T rawGameDto);

}
