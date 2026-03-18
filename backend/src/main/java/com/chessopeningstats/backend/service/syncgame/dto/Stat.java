package com.chessopeningstats.backend.service.syncgame.dto;

public record Stat(
        long win,
        long draw,
        long lose
) {
    public long total() {
        return win + draw + lose;
    }

    public double winRate() {
        return total() == 0 ? 0 : (double) win / total();
    }
}