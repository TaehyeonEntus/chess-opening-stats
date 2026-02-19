package com.chessopeningstats.backend.application.usecase.syncGame.internal.provide.internal.filter;

import com.chessopeningstats.backend.application.usecase.syncGame.internal.provide.internal.adapt.dto.NormalizedGameDto;
import com.chessopeningstats.backend.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameFilterService {
    public List<NormalizedGameDto> filterNormalGames(Collection<NormalizedGameDto> dtos){
        return dtos.parallelStream().filter(this::isValid).toList();
    }

    public boolean isValid(NormalizedGameDto dto){
        if(dto.getGameType() == GameType.UNKNOWN || dto.getGameType() == GameType.ETC)
            return false;
        else if(dto.getGamePlayerColor() == GamePlayerColor.UNKNOWN)
            return false;
        else if(dto.getGameType()!=GameType.STANDARD)
            return false;
        else if(dto.getGameTime() == GameTime.UNKNOWN)
            return false;
        else if(dto.getPgn().isBlank())
            return false;
        else
            return true;
    }
}
