package com.chessopeningstats.backend.web.account.dto;

import com.chessopeningstats.backend.domain.Platform;
import lombok.Data;

@Data
public class AddAccountRequest {
    private String username;
    private Platform platform;
}
