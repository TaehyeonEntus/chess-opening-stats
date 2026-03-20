package com.chessopeningstats.backend.service.syncgame.impl;

import com.chessopeningstats.backend.infra.cache.OpeningCache;
import com.chessopeningstats.backend.service.syncgame.GameAnalyzeService;
import com.chessopeningstats.backend.service.syncgame.dto.AnalyzedGame;
import com.chessopeningstats.backend.service.syncgame.dto.NormalizedGame;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.move.MoveList;
import com.github.bhlangonijr.chesslib.pgn.PgnHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameAnalyzeServiceImpl implements GameAnalyzeService {
    private final OpeningCache openingCache;
    private static final int MAX_OPENING_MOVES = 25;

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
            //PGN 로드
            PgnHolder pgnHolder = new PgnHolder();
            pgnHolder.loadPgn(pgn);

            //게임 추출
            Game game = pgnHolder.getGames().getFirst();
            Board board = new Board();
            if (game.getFen() != null)
                board.loadFromFen(game.getFen());

            //착수 정보 추출
            MoveList moves = game.getHalfMoves();

            for (int i = 0; i < Math.min(moves.size(), MAX_OPENING_MOVES); i++) {
                board.doMove(moves.get(i));
                keys.add(board.getIncrementalHashKey());
            }
        } catch (Exception e) {
        }

        return openingCache.getOpeningsIdsByKeys(keys);
    }
}



