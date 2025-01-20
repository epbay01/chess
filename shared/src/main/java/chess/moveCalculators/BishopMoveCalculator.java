package chess.moveCalculators;

import chess.ChessBoard;
import chess.ChessPosition;

public class BishopMoveCalculator extends MoveCalculator {
    public BishopMoveCalculator(ChessPosition position, ChessBoard board) {
        super(position, board);

        checkAlongDirection(position, board, -1, -1);
        checkAlongDirection(position, board, -1, 1);
        checkAlongDirection(position, board, 1, 1);
        checkAlongDirection(position, board, 1, -1);
    }
}
