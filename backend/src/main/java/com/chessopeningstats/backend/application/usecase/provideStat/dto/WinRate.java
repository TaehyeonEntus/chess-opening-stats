package com.chessopeningstats.backend.application.usecase.provideStat.dto;

import com.chessopeningstats.backend.domain.GamePlayerColor;


public record WinRate (GamePlayerColor color, Long wins, Long draws, Long losses){}
