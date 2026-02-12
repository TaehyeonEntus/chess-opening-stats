package com.chessopeningstats.backend.application.syncGame.internal.analysis;

import com.chessopeningstats.backend.domain.Opening;
import com.chessopeningstats.backend.infra.storage.OpeningStorage;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveList;
import com.github.bhlangonijr.chesslib.pgn.PgnHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OpeningAnalyzeService {
    private final OpeningStorage openingStorage;

    public Set<Opening> analyzeOpening(String pgn) {
        PgnHolder pgnHolder = new PgnHolder();
        pgnHolder.loadPgn(pgn);
        Game game = pgnHolder.getGames().getFirst();

        Board board = new Board();
        MoveList moves = game.getHalfMoves();

        Set<String> epds = new HashSet<>();
        for (int i = 0; i < Math.min(game.getHalfMoves().size(), 30); i++) {
            Move move = moves.get(i);

            if(!board.isMoveLegal(move, true))
                break;

            board.doMove(move);
            epds.add(board.getFen(false));
        }

        return openingStorage.loadOpeningsByEpds(epds);
    }
}
