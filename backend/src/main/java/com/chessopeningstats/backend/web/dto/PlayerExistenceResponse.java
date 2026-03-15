package com.chessopeningstats.backend.web.dto;

public record PlayerExistenceResponse (
        String image_url,
        Long last_online
){
}
