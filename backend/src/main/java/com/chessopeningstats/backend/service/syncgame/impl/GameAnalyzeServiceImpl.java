package com.chessopeningstats.backend.service.syncgame.impl;

import com.chessopeningstats.backend.domain.Opening;
import com.chessopeningstats.backend.infra.cache.OpeningCache;
import com.chessopeningstats.backend.service.syncgame.GameAnalyzeService;
import com.chessopeningstats.backend.service.syncgame.dto.AnalyzedGame;
import com.chessopeningstats.backend.service.syncgame.dto.NormalizedGame;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.move.MoveList;
import com.github.bhlangonijr.chesslib.pgn.PgnHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.ParallelFlux;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GameAnalyzeServiceImpl implements GameAnalyzeService {
    private final OpeningCache openingCache;
    private static final int MAX_OPENING_MOVES = 25;

    @Override
    public ParallelFlux<AnalyzedGame> analyze(ParallelFlux<NormalizedGame> sanitizedGames) {
        return sanitizedGames.map(this::analyzeOne).filter(analyzedGame -> Objects.nonNull(analyzedGame.lastOpeningId()));
    }

    public AnalyzedGame analyzeOne(NormalizedGame normalizedGame) {
        List<Long> openingIds = getOpeningIds(normalizedGame.pgn());
        return new AnalyzedGame(
                normalizedGame.color(),
                normalizedGame.result(),
                openingIds,
                openingIds.isEmpty()
                        ? null
                        : openingIds.getLast()
        );
    }

    /**
     * 첫번째 부터 N번째 수까지의 포지션에 해당하는 오프닝 ID를 반환
     */
    private List<Long> getOpeningIds(String pgn) {
        List<String> epds = new ArrayList<>();
        try {
            PgnHolder pgnHolder = new PgnHolder();
            pgnHolder.loadPgn(pgn);
            com.github.bhlangonijr.chesslib.game.Game game = pgnHolder.getGames().getFirst();

            Board board = new Board();
            if (game.getFen() != null)
                board.loadFromFen(game.getFen());

            MoveList moves = game.getHalfMoves();

            for (int i = 0; i < Math.min(moves.size(), MAX_OPENING_MOVES); i++) {
                board.doMove(moves.get(i));
                epds.add(board.getFen(false, true));
            }
        } catch (Exception e) {
        }

        return openingCache.getOpeningsByEpds(epds).stream().map(Opening::id).toList();
    }
}



