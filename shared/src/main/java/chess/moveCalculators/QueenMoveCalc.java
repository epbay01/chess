package chess.moveCalculators;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;

public class QueenMoveCalc extends MoveCalc {
    public QueenMoveCalc(ChessPiece piece, ChessBoard board, ChessPosition position) {
        super(piece, board, position);

        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                System.out.println("checking direction " + i + " " + j);
                checkDirection(i,j);
            }
        }
    }
}
