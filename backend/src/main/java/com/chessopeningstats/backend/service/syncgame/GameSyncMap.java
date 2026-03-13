package com.chessopeningstats.backend.service.syncgame;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 작업대기 큐 + 현재 진행중인 동기화 과정에 있는 ID를 기록합니다!!
 */
@Component
public class GameSyncMap {
    private static final Map<Long, Boolean> chessComMap = new ConcurrentHashMap<>();
    private static final Map<Long, Boolean> lichessMap = new ConcurrentHashMap<>();

    public void addChessComPlayer(long playerId) {
        chessComMap.put(playerId, true);
    }

    public void addLichessPlayer(long playerId){
        lichessMap.put(playerId, true);
    }

    public void removeChessComPlayer(long playerId) {
        chessComMap.remove(playerId);
    }

    public void removeLichessPlayer(long playerId) {
        lichessMap.remove(playerId);
    }

    public boolean isStillSync(long playerId) {
        return chessComMap.containsKey(playerId) || lichessMap.containsKey(playerId);
    }
}
