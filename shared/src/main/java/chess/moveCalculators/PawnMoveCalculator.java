package chess.moveCalculators;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Set;

public class PawnMoveCalculator {
    Set<ChessMove> possibleMoves;
    PawnMoveCalculator(ChessPosition position, ChessBoard board) {


        // get team color to determine direction
        if (board.getPiece(position).getTeamColor() == ChessGame.TeamColor.BLACK) {
            ChessPosition position1 = new ChessPosition(position.getRow() - 1, position.getColumn());
            if (board.getPiece(position1) != null) {
                possibleMoves.add(new ChessMove(position, position1));
            }
        } else {

        }
    }
}
