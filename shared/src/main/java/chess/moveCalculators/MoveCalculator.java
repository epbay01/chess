package chess.moveCalculators;

import chess.*;

import java.util.HashSet;

public class MoveCalculator {
    HashSet<ChessMove> possibleMoves;
    int direction;

    public MoveCalculator(ChessPosition position, ChessBoard board) {
        possibleMoves = new HashSet<>();
        // get team color to determine direction
        if (board.getPiece(position).getTeamColor() == ChessGame.TeamColor.BLACK) {
            direction = 1;
        } else {
            direction = -1;
        }
    }

    public HashSet<ChessMove> getPossibleMoves() {
        return possibleMoves;
    }

    // useful for queen, rook, bishop
    void checkAlongDirection(ChessPosition position, ChessBoard board, int rowChange, int columnChange) {
        boolean clearPath = true;
        int distance = 1;

        while (clearPath) {
            // takes direction vector and multiplies by number of iterations (distance)
            try {
                ChessPosition newPosition = new ChessPosition(position.getRow() + (distance * rowChange),
                        position.getColumn() + (distance * columnChange));

                if (!board.hasPiece(newPosition)) { // no piece
                    possibleMoves.add(new ChessMove(position, newPosition));
                    distance++;
                } else { // break and move to next direction
                    checkCapture(new ChessMove(position, newPosition), board);
                    clearPath = false;
                }

            } catch (IllegalArgumentException e) { // reached end of board
                clearPath = false;
            }
        }
    }

    void checkCapture(ChessMove move, ChessBoard board) {
        if (board.getPiece(move.getEndPosition()) != null) {
            ChessGame.TeamColor thisColor = board.getPiece(move.getStartPosition()).getTeamColor();
            if (thisColor != board.getPiece(move.getEndPosition()).getTeamColor()) {
                possibleMoves.add(move);
            }
        }
    }
}
