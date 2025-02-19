package chess.moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.HashSet;

public class MoveCalc {
    protected ChessPiece piece;
    protected ChessBoard board;
    protected ChessPosition position;
    protected HashSet<ChessMove> possibleMoves;

    public MoveCalc(ChessPiece piece, ChessBoard board, ChessPosition position) {
        // System.out.print("board:\n" + board);

        this.piece = piece;
        this.board = board;
        this.position = position;
        this.possibleMoves = new HashSet<>();
    }

    public HashSet<ChessMove> getMoves() {
        return possibleMoves;
    }

    protected boolean checkMove(ChessMove move) {
        if (board.getPiece(move.getEndPosition()) == null) {
            // System.out.println("adding move to " + move.getEndPosition());
            return possibleMoves.add(move);
        }
        return false;
    }

    protected boolean checkCapture(ChessMove move) {
        if (board.getPiece(move.getEndPosition()) != null) {
            if (board.getPiece(move.getEndPosition()).getTeamColor() != piece.getTeamColor()) {
                // System.out.println("adding capture to " + move.getEndPosition());
                return possibleMoves.add(move);
            }
        }
        return false;
    }

    protected void checkDirection(int rowDir, int colDir) {
        checkDirectionHelper(rowDir, colDir, position);
    }

    private void checkDirectionHelper(int rowDir, int colDir, ChessPosition prev) {
        try {
            ChessPosition newPosition = new ChessPosition(prev.getRow() + rowDir, prev.getColumn() + colDir);
            ChessMove newMove = new ChessMove(position, newPosition);
            if (!checkMove(newMove)) {
                checkCapture(newMove);
                return;
            }
            checkDirectionHelper(rowDir, colDir, newPosition);
        } catch (IllegalArgumentException ignored) {}
    }
}
