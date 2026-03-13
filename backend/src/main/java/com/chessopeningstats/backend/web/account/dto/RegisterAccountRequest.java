package com.chessopeningstats.backend.web.account.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterAccountRequest(
        @NotBlank String username,
        @NotBlank String nickname,
        @NotBlank String password,
        @NotBlank String passwordConfirm
) {
}
