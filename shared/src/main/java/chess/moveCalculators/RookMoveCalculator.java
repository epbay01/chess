package chess.moveCalculators;

import chess.ChessBoard;
import chess.ChessPosition;

public class RookMoveCalculator extends MoveCalculator {
    public RookMoveCalculator(ChessPosition position, ChessBoard board) {
        super(position, board);

        checkAlongDirection(position, board, 1, 0);
        checkAlongDirection(position, board, -1, 0);
        checkAlongDirection(position, board, 0, 1);
        checkAlongDirection(position, board, 0, -1);
    }
}
