package chess.moveCalculators;

import chess.*;

public class PawnMoveCalculator extends MoveCalculator {

    public PawnMoveCalculator(ChessPosition position, ChessBoard board, boolean firstMove) {
        super(position, board);

        ChessPosition position1;
        try {
            position1 = new ChessPosition(position.getRow() + direction, position.getColumn());
        } catch (IllegalArgumentException e) {
            // if regular move is off the board, so will capture
            return;
        }

        if (!board.hasPiece(position1)) {
            possibleMoves.add(new ChessMove(position, position1));

            // possible two spaces if not blocked on first one
            ChessPosition position2;
            try {
                position2 = new ChessPosition(position.getRow() + (2 * direction), position.getColumn());
            } catch (IllegalArgumentException e) {
                // don't have to check capture because row doesn't change for pawn caps
                return;
            }

            if (firstMove) {
                if (!board.hasPiece(position2)) {
                    possibleMoves.add(new ChessMove(position, position2));
                }
                checkCapture(new ChessMove(position, position2), board);
            }
            checkCapture(new ChessMove(position, position1), board);
        }
    }

    // checks diagonal moves using regular check and calculations
    @Override
    void checkCapture(ChessMove move, ChessBoard board) {
        ChessPosition diagLeft;
        try {
            diagLeft = new ChessPosition(move.getEndPosition().getRow(), move.getEndPosition().getColumn() - 1);
            ChessMove move1 = new ChessMove(move.getStartPosition(), diagLeft);
            super.checkCapture(move1, board);
        } catch (IllegalArgumentException ignored) {}

        try {
            ChessPosition diagRight = new ChessPosition(move.getEndPosition().getRow(), move.getEndPosition().getColumn() + 1);
            ChessMove move2 = new ChessMove(move.getStartPosition(), diagRight);
            super.checkCapture(move2, board);
        } catch (IllegalArgumentException ignored) {}
    }
}
