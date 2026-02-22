package com.chessopeningstats.backend.application.usecase.syncGame.dto;

import lombok.Data;

@Data(staticConstructor = "of")
public class RunningStatusResponse {
    private final boolean running;
}
