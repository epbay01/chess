package chess.moves;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;

public class BishopMoveCalc extends MoveCalc {
    public BishopMoveCalc(ChessPiece piece, ChessBoard board, ChessPosition position) {
        super(piece, board, position);

        checkDirection(-1, -1);
        checkDirection(-1, 1);
        checkDirection(1, 1);
        checkDirection(1, -1);
    }
}
