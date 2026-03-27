package com.chessopeningstats.backend.infra.client.playerprofile.dto;

public record PlayerProfile(
        String image_url,
        Long last_online
) {
}
