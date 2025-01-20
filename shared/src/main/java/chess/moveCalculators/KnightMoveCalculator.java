package chess.moveCalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

public class KnightMoveCalculator extends MoveCalculator {
    int[][] relativeKnightLocations = {
            {2, 1}, {2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}, {-2, 1}, {-2, -1}
    };

    public KnightMoveCalculator(ChessPosition position, ChessBoard board) {
        super(position, board);

        for (int[] l : relativeKnightLocations) {
            try {
                ChessPosition newPosition = new ChessPosition(position.getRow() + l[0], position.getColumn() + l[1]);
                if (board.hasPiece(newPosition)) {
                    checkCapture(new ChessMove(position, newPosition), board);
                } else {
                    possibleMoves.add(new ChessMove(position, newPosition));
                }
            } catch (IllegalArgumentException ignored) {}
        }
    }
}
