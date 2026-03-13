package com.chessopeningstats.backend.web.account.dto;

public record SyncGameResponse(
        long chess_com,
        long lichess
){
}
