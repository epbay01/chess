package chess.moveCalculators;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public class MoveCalculator {
    HashSet<ChessMove> possibleMoves;
    int direction;

    public MoveCalculator(ChessPosition position, ChessBoard board) {
        possibleMoves = new HashSet<>();
        // get team color to determine direction
        if (board.getPiece(position).getTeamColor() == ChessGame.TeamColor.BLACK) {
            direction = -1;
        } else {
            direction = 1;
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

                if (board.getPiece(newPosition) == null) { // no piece
                    possibleMoves.add(new ChessMove(position, newPosition));
                    distance++;
                } else { // break and move to next direction
                    clearPath = false;
                }

            } catch (IllegalArgumentException e) { // reached end of board
                clearPath = false;
            }
        }
    }
}
