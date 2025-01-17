package chess.moveCalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

public class KingMoveCalculator extends MoveCalculator {
    public KingMoveCalculator(ChessPosition position, ChessBoard board) {
        super(position, board);

        for (int row = position.getRow() - 1; row <= position.getRow() + 1; row++) {
            for (int column = position.getColumn() - 1; column <= position.getColumn() + 1; column++) {
                ChessPosition current = new ChessPosition(row, column);
                if (!current.equals(position) && !board.hasPiece(current)) {
                    possibleMoves.add(new ChessMove(position, current));
                }
            }
        }
    }
}
