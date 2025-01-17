package chess.moveCalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

public class PawnMoveCalculator extends MoveCalculator {

    public PawnMoveCalculator(ChessPosition position, ChessBoard board, boolean firstMove) {
        super(position, board);

        ChessPosition position1 = new ChessPosition(position.getRow() + direction, position.getColumn());

        if (!board.hasPiece(position1)) {
            possibleMoves.add(new ChessMove(position, position1));

            // possible two spaces if not blocked on first one
            if (firstMove) {
                ChessPosition position2 = new ChessPosition(position.getRow() + (2 * direction), position.getColumn());
                if (!board.hasPiece(position2)) {
                    possibleMoves.add(new ChessMove(position, position2));
                }
            }
        }
    }
}
