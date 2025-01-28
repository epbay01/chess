package chess.moveCalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

public class KingMoveCalc extends MoveCalc {
    public KingMoveCalc(ChessPiece piece, ChessBoard board, ChessPosition position) {
        super(piece, board, position);

        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                //System.out.println("checking direction " + i + " " + j);
                try {
                    ChessPosition newPosition = new ChessPosition(position.getRow() + i, position.getColumn() + j);
                    ChessMove move = new ChessMove(position, newPosition);
                    if (!checkMove(move)) {
                        checkCapture(move);
                    }
                }
                catch (Exception ignored) {}
            }
        }
    }
}
