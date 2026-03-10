package com.chessopeningstats.backend.web.account.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
        @NotBlank String oldPassword,
        @NotBlank String newPassword,
        @NotBlank String newPasswordConfirm
) {
}