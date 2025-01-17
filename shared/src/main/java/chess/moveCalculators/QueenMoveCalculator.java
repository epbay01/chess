package chess.moveCalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

public class QueenMoveCalculator extends MoveCalculator {
    public QueenMoveCalculator(ChessPosition position, ChessBoard board) {
        super(position, board);

        // there are 8 directions to go (not including 0,0), and you can go while there is a clear path
        // start at direction (-1,-1) and loop in that direction until edge/piece, then do other directions
        for (int rowChange = -1; rowChange <= 1; rowChange++) {
            for (int columnChange = -1; columnChange <= 1; columnChange++) {
                checkAlongDirection(position, board, rowChange, columnChange);
            }
        }
    }
}
