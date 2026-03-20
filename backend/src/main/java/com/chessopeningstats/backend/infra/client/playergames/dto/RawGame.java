package com.chessopeningstats.backend.infra.client.playergames.dto;

import com.chessopeningstats.backend.domain.Player;

public interface RawGame {
    void setPlayer(Player player);
    RawGame withPlayer(Player player);
}