package com.chessopeningstats.backend.application.syncGame.internal.provide.internal.adapt;

import com.chessopeningstats.backend.application.syncGame.internal.provide.internal.adapt.dto.NormalizedGameDto;
import com.chessopeningstats.backend.domain.Account;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public interface GameAdaptService<T> {

    default List<NormalizedGameDto> adaptAll(
            @NotNull Account account,
            @NotNull List<@NotNull @Valid T> rawGameDtos
    ) {
        return rawGameDtos.parallelStream()
                .map(rawGameDto -> adaptOne(account, rawGameDto))
                .toList();
    }

    NormalizedGameDto adaptOne(@NotNull Account account, @NotNull @Valid T rawGameDto);

}
