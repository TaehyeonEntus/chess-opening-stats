package com.chessopeningstats.backend.application.usecase.syncGame.internal.analysis;

import com.chessopeningstats.backend.domain.Opening;
import com.chessopeningstats.backend.infra.storage.OpeningStorage;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.move.MoveList;
import com.github.bhlangonijr.chesslib.pgn.PgnHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpeningAnalyzeService {
    private final OpeningStorage openingStorage;

    public List<Opening> analyzeOpening(String pgn) {
        PgnHolder pgnHolder = new PgnHolder();
        pgnHolder.loadPgn(pgn);

        Game game = pgnHolder.getGames().getFirst();

        Board board = new Board();

        if(game.getFen() != null)
            board.loadFromFen(game.getFen());

        MoveList moves = game.getHalfMoves();

        List<String> epds = new ArrayList<>();

        for (int i = 0; i < Math.min(moves.size(), 30); i++) {
            board.doMove(moves.get(i));
            epds.add(board.getFen(false, true));
        }

        return openingStorage.loadOpeningsByEpds(epds);
    }
}
