package chess.moveCalculators;

import chess.*;

import java.util.HashSet;

public class PawnMoveCalc extends MoveCalc {
    public PawnMoveCalc(ChessPiece piece, ChessBoard board, ChessPosition position) {
        super(piece, board, position);

        int direction = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? 1 : -1;

        try {
            ChessPosition end1 = new ChessPosition(position.getRow() + direction, position.getColumn());
            ChessMove move1 = new ChessMove(position, end1);

            checkCaptures(direction);
            if (checkMove(move1)) {
                if (piece.isFirstMove()) {
                    try {
                        ChessPosition end2 = new ChessPosition(position.getRow() + (2 * direction), position.getColumn());
                        ChessMove move2 = new ChessMove(position, end2);
                        checkMove(move2);
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            }
        } catch (Exception ignored) {}

        checkForPromotion();
    }

    private void checkCaptures(int direction) {
        try {
            ChessPosition end1 = new ChessPosition(position.getRow() + direction, position.getColumn() + 1);
            ChessMove move1 = new ChessMove(position, end1);
            checkCapture(move1);
        } catch (IllegalArgumentException ignored) {}

        try {
            ChessPosition end2 = new ChessPosition(position.getRow() + direction, position.getColumn() - 1);
            ChessMove move2 = new ChessMove(position, end2);
            checkCapture(move2);
        } catch (IllegalArgumentException ignored) {}
    }

    private void checkForPromotion() {
        HashSet<ChessMove> newSet = (HashSet<ChessMove>) possibleMoves.clone();
        for (ChessMove move : possibleMoves) {
            if (move.getEndPosition().getRow() == 8 || move.getEndPosition().getRow() == 1) {
                ChessMove q = new ChessMove(position, move.getEndPosition(), ChessPiece.PieceType.QUEEN);
                ChessMove r = new ChessMove(position, move.getEndPosition(), ChessPiece.PieceType.ROOK);
                ChessMove n = new ChessMove(position, move.getEndPosition(), ChessPiece.PieceType.KNIGHT);
                ChessMove b = new ChessMove(position, move.getEndPosition(), ChessPiece.PieceType.BISHOP);
                newSet.remove(move);
                newSet.add(q);
                newSet.add(r);
                newSet.add(n);
                newSet.add(b);
            }
        }
        possibleMoves = newSet;
    }
}
