package chess;

import java.util.Collection;
import java.util.Objects;

import chess.moves.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final PieceType pieceType;
    private final ChessGame.TeamColor teamColor;
    private boolean firstMove;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceType = type;
        this.teamColor = pieceColor;
        this.firstMove = false;
    }

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type, boolean firstMove) {
        pieceType = type;
        this.teamColor = pieceColor;
        this.firstMove = firstMove;
    }

    public boolean isFirstMove() {
        return firstMove;
    }

    public void setFirstMove(boolean firstMove) {
        this.firstMove = firstMove;
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
        return teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        MoveCalc mc;
        mc = switch (pieceType) {
            case PAWN -> new PawnMoveCalc(this, board, myPosition);
            case QUEEN -> new QueenMoveCalc(this, board, myPosition);
            case ROOK -> new RookMoveCalc(this, board, myPosition);
            case BISHOP -> new BishopMoveCalc(this, board, myPosition);
            case KING -> new KingMoveCalc(this, board, myPosition);
            case KNIGHT -> new KnightMoveCalc(this, board, myPosition);
            default -> new MoveCalc(this, board, myPosition);
        };
        return mc.getMoves();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return firstMove == that.firstMove && pieceType == that.pieceType && teamColor == that.teamColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceType, teamColor, firstMove);
    }

    @Override
    public String toString() {
        char typeChar = switch (pieceType) {
            case KING -> 'k';
            case QUEEN -> 'q';
            case PAWN -> 'p';
            case ROOK -> 'r';
            case BISHOP -> 'b';
            case KNIGHT -> 'n';
            default -> ' ';
        };

        if (teamColor == ChessGame.TeamColor.WHITE) {
            typeChar = Character.toUpperCase(typeChar);
        }

        return Character.toString(typeChar);
    }

    @Override
    public ChessPiece clone() {
        return new ChessPiece(this.teamColor, this.pieceType, this.firstMove);
    }
}
