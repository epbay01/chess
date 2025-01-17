package chess.moveCalculators;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public class PawnMoveCalculator extends MoveCalculator {

    public PawnMoveCalculator(ChessPosition position, ChessBoard board) {
        super(position, board);

        ChessPosition position1 = new ChessPosition(position.getRow() + direction, position.getColumn());
        ChessPosition position2 = new ChessPosition(position.getRow() + (2 * direction), position.getColumn());
        if (board.getPiece(position1) != null) {
            possibleMoves.add(new ChessMove(position, position1));
        }
        if (board.getPiece(position2) != null) {
            possibleMoves.add(new ChessMove(position, position2));
        }
    }
}
