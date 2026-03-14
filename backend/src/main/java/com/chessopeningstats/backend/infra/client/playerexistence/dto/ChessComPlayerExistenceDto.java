package com.chessopeningstats.backend.infra.client.playerexistence.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChessComPlayerExistenceDto{
    private String avatar;
    private Long last_online;
}
