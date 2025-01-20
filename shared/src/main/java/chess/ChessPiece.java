package chess;

import chess.moveCalculators.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    PieceType type;
    ChessGame.TeamColor color;
    boolean firstMove; // for pawns only

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.type = type;
        this.color = pieceColor;
        //if (type == PieceType.PAWN) {
        //    firstMove = true;
        //} else {
            firstMove = false;
        //}
    }

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type, boolean firstMove) {
        this.type = type;
        this.color = pieceColor;
        if (type != PieceType.PAWN && firstMove) {
            this.firstMove = false; // doesn't matter if not a pawn
        } else {
            this.firstMove = firstMove;
        }
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    public void setFirstMove(boolean firstMove) {
        this.firstMove = firstMove;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return type == that.type && color == that.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, color, firstMove);
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

        switch (type) {
            case PAWN:
                PawnMoveCalculator pawnMoveCalculator = new PawnMoveCalculator(myPosition, board, firstMove);
                return pawnMoveCalculator.getPossibleMoves();
            case KING:
                KingMoveCalculator kingMoveCalculator = new KingMoveCalculator(myPosition, board);
                return kingMoveCalculator.getPossibleMoves();
            case QUEEN:
                QueenMoveCalculator queenMoveCalculator = new QueenMoveCalculator(myPosition, board);
                return queenMoveCalculator.getPossibleMoves();
            case BISHOP:
                BishopMoveCalculator bishopMoveCalculator = new BishopMoveCalculator(myPosition, board);
                return bishopMoveCalculator.getPossibleMoves();
            case KNIGHT:
                KnightMoveCalculator knightMoveCalculator = new KnightMoveCalculator(myPosition, board);
                return knightMoveCalculator.getPossibleMoves();
            case ROOK:
                RookMoveCalculator rookMoveCalculator = new RookMoveCalculator(myPosition, board);
                return rookMoveCalculator.getPossibleMoves();
        }
        return new HashSet<ChessMove>();
    }

    @Override
    public String toString() {
        switch (type) {
            case KING:
                if (color == ChessGame.TeamColor.BLACK) {
                    return "k";
                } else {
                    return "K";
                }
            case QUEEN:
                if (color == ChessGame.TeamColor.BLACK) {
                    return "q";
                } else {
                    return "Q";
                }
            case BISHOP:
                if (color == ChessGame.TeamColor.BLACK) {
                    return "b";
                } else {
                    return "B";
                }
            case KNIGHT:
                if (color == ChessGame.TeamColor.BLACK) {
                    return "n";
                } else {
                    return "N";
                }
            case ROOK:
                if (color == ChessGame.TeamColor.BLACK) {
                    return "r";
                } else {
                    return "R";
                }
            case PAWN:
                if (color == ChessGame.TeamColor.BLACK) {
                    return "p";
                } else {
                    return "P";
                }
            default:
                return " ";
        }
    }
}
