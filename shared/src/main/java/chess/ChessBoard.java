package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPosition[][] board;

    public ChessBoard() {
        board = new ChessPosition[8][8]; // init array

        resetBoard();
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow() - 1][position.getColumn() - 1].setPiece(piece);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow() - 1][position.getColumn() - 1].piece;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        // empty board
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = new ChessPosition(i, j);
            }
        }

        // pawns
        for (int i = 0; i < 8; i++) {
            board[1][i] = new ChessPosition(0, i,
                    new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN)
            );
            board[6][i] = new ChessPosition(0, i,
                    new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN)
            );
        }

        // rooks
        board[0][0] = new ChessPosition(1,1, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        board[0][7] = new ChessPosition(1,8, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        board[7][0] = new ChessPosition(8,1, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        board[7][7] = new ChessPosition(8,8, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));

        // knights
        board[0][1] = new ChessPosition(1,2, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        board[0][6] = new ChessPosition(1,7, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        board[7][1] = new ChessPosition(8,2, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        board[7][6] = new ChessPosition(8,7, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));

        // bishops
        board[0][2] = new ChessPosition(1,3, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        board[0][5] = new ChessPosition(1,6, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        board[7][2] = new ChessPosition(8,3, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        board[7][5] = new ChessPosition(8,6, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));

        // queens
        board[0][4] = new ChessPosition(1,5, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
        board[7][3] = new ChessPosition(8,4, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));

        // kings
        board[0][4] = new ChessPosition(1,4, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
        board[7][3] = new ChessPosition(8,5, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
    }

    @Override public String toString() {
        StringBuilder boardStr = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                boardStr.append(board[i][j].toString());
            }
        }

        return boardStr.toString();
    }
}
