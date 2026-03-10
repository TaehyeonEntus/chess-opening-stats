package com.chessopeningstats.backend.web.account.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangeNicknameRequest(
        @NotBlank String newNickname
) {
}
