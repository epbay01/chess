package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
    int row;
    int col;
    ChessPiece piece;

    public ChessPosition(int row, int col, ChessPiece piece) {
        if (row < 1 || col < 1 || row > 8 || col > 8) {
            throw new IllegalArgumentException();
        }
        this.row = row;
        this.col = col;
        this.piece = piece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col, piece);
    }

    public ChessPosition(int row, int col) {
        if (row < 1 || col < 1 || row > 8 || col > 8) {
            throw new IllegalArgumentException();
        }
        this.row = row;
        this.col = col;
        this.piece = null;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
    }

    public ChessPiece getPiece() { return piece; }

    public void setPiece(ChessPiece piece) { this.piece = piece; }

    public boolean hasPiece() { return piece != null; }

    @Override
    public String toString() {
        if (piece == null) {
            return "| |";
        } else {
            return String.format("|%s|", piece.toString());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }

        ChessPosition other;
        if (o.getClass().equals(this.getClass())) {
            other = (ChessPosition)o;
        } else {
            return false;
        }

        return other.getRow() == this.getRow() && other.getColumn() == this.getColumn();
    }
}
