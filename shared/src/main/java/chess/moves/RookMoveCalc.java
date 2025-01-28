package chess.moves;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;

public class RookMoveCalc extends MoveCalc {
    public RookMoveCalc(ChessPiece piece, ChessBoard board, ChessPosition position) {
        super(piece, board, position);

        checkDirection(-1, 0);
        checkDirection(1, 0);
        checkDirection(0, 1);
        checkDirection(0, -1);
    }
}
