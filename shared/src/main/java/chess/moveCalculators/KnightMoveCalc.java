package chess.moveCalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

public class KnightMoveCalc extends MoveCalc {
    final int[][] POSSIBLE_DIFF = {
            {-2, -1}, {-2, 1}, {2, -1}, {2, 1}, {-1, -2}, {1, -2}, {-1, 2}, {1, 2}
    };

    public KnightMoveCalc(ChessPiece piece, ChessBoard board, ChessPosition position) {
        super(piece, board, position);

        for (int[] pos : POSSIBLE_DIFF) {
            try {
                ChessPosition newPosition = new ChessPosition(position.getRow() + pos[0], position.getColumn() + pos[1]);
                ChessMove move = new ChessMove(position, newPosition);
                if (!checkMove(move)) {
                    checkCapture(move);
                }
            } catch (Exception ignored) {}
        }
    }
}
