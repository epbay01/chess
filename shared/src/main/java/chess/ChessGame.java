package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor turn;
    private boolean whiteCheck;
    private boolean blackCheck;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        turn = TeamColor.WHITE;
        whiteCheck = false;
        blackCheck = false;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        // no piece, null
        if (board.getPiece(startPosition) == null) {
            return null;
        }
        // not your turn, empty set
        if (board.getPiece(startPosition).getTeamColor() != turn) {
            return new HashSet<ChessMove>();
        }

        HashSet<ChessMove> moveSet = (HashSet<ChessMove>) board.getPiece(startPosition).pieceMoves(board, startPosition);
        // move must:
        // - be the right team color (it's their turn)
        // - in the board state after the move, doesn't leave the king in check (regardless of if he starts in check)
        // - be in the possible set of moves
        return moveSet;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        // check if move is valid
        if (!validMoves(move.getStartPosition()).contains(move)) {
            throw new InvalidMoveException(move + " is not a valid move");
        }

        // modify the board

        // check for check

        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        if (teamColor == TeamColor.WHITE) {
            return whiteCheck;
        } else {
            return blackCheck;
        }
    }

    public void setCheck(TeamColor teamColor, boolean check) {
        if (teamColor == TeamColor.WHITE) {
            whiteCheck = check;
        } else {
            blackCheck = check;
        }
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        // can't be in checkmate if not in check
        if (!isInCheck(teamColor)) {
            // would then call isInStalemate in caller
            return false;
        }

        // for each piece check for valid moves
        // if empty, return true, else false
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        // for each piece in that color
        // if valid moves is empty, true
        // else return false
        throw new RuntimeException("Not implemented");
    }

    protected void checkForCheck(ChessMove move, TeamColor teamColor) {
        // iterate through the next possible valid moves after this
        for (ChessMove m : validMoves(move.getEndPosition())) {
            setCheck(teamColor, false);
            ChessPiece end = board.getPiece(m.getEndPosition());

            // if the end has a king, we already checked it's a different color in the move calculator
            if(end != null && end.getPieceType() == ChessPiece.PieceType.KING) {
                setCheck(teamColor, true);
            }
        }
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
