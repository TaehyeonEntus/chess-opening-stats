package com.chessopeningstats.backend.application.usecase.linkPlayer.dto;

import com.chessopeningstats.backend.domain.Platform;
import lombok.Data;

@Data
public class AddPlayerRequest {
    private String username;
    private Platform platform;
}
