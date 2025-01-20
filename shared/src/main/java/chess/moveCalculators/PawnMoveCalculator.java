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

        checkPawnCapture(new ChessMove(position, position1), board);
        if (!board.hasPiece(position1)) {
            possibleMoves.add(new ChessMove(position, position1));
            checkPromotion(new ChessMove(position, position1));

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
            }
        }
    }

    // checks diagonal moves using regular check and calculations
    void checkPawnCapture(ChessMove move, ChessBoard board) {
        ChessPosition diagLeft;
        try {
            diagLeft = new ChessPosition(move.getEndPosition().getRow(), move.getEndPosition().getColumn() - 1);
            ChessMove move1 = new ChessMove(move.getStartPosition(), diagLeft);
            if (super.checkCapture(move1, board)) {
                checkPromotion(move1);
            }
        } catch (IllegalArgumentException ignored) {}

        try {
            ChessPosition diagRight = new ChessPosition(move.getEndPosition().getRow(), move.getEndPosition().getColumn() + 1);
            ChessMove move2 = new ChessMove(move.getStartPosition(), diagRight);
            if (super.checkCapture(move2, board)) {
                checkPromotion(move2);
            }
        } catch (IllegalArgumentException ignored) {}
    }

    void checkPromotion(ChessMove move) {
        if (move.getEndPosition().getRow() == 1 || move.getEndPosition().getRow() == 8) {
            possibleMoves.remove(move);
            possibleMoves.add(new ChessMove(move.getStartPosition(), move.getEndPosition(), ChessPiece.PieceType.QUEEN));
            possibleMoves.add(new ChessMove(move.getStartPosition(), move.getEndPosition(), ChessPiece.PieceType.KNIGHT));
            possibleMoves.add(new ChessMove(move.getStartPosition(), move.getEndPosition(), ChessPiece.PieceType.ROOK));
            possibleMoves.add(new ChessMove(move.getStartPosition(), move.getEndPosition(), ChessPiece.PieceType.BISHOP));
        }
    }
}
