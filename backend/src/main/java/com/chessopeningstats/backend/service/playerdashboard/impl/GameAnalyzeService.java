package com.chessopeningstats.backend.service.playerdashboard.impl;

import com.chessopeningstats.backend.infra.storage.OpeningStorage;
import com.chessopeningstats.backend.service.playerdashboard.dto.AnalyzedGame;
import com.chessopeningstats.backend.service.playerdashboard.dto.NormalizedGame;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.move.MoveList;
import com.github.bhlangonijr.chesslib.pgn.GameLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameAnalyzeService{
    private final OpeningStorage openingStorage;
    private static final int MAX_OPENING_MOVES = 30;

    public AnalyzedGame analyze(NormalizedGame normalizedGame) {
        List<Long> openingIds = getOpeningIds(normalizedGame.pgn());
        return new AnalyzedGame(
                normalizedGame.color(),
                normalizedGame.result(),
                openingIds,
                openingIds.isEmpty()
                        ? null
                        : openingIds.getLast(),
                normalizedGame.player()
        );
    }

    /**
     * 첫번째 부터 N번째 수까지의 포지션에 해당하는 오프닝 ID를 반환
     */
    private List<Long> getOpeningIds(String pgn) {
        List<Long> keys = new ArrayList<>();

        try {
            Board board = new Board();
            MoveList moves = GameLoader.loadNextGame(Arrays.asList(pgn.split("\n")).iterator()).getHalfMoves();
            for (int i = 0; i < Math.min(moves.size(), MAX_OPENING_MOVES); i++) {
                board.doMove(moves.get(i));
                keys.add(board.getIncrementalHashKey());
            }
        } catch (Exception e) {
        }

        return openingStorage.getOpeningsIdsByKeys(keys);
    }
}



