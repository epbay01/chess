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
}
