package chess;

import chess.moves.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

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
//        System.out.println("getting valid moves for position " + startPosition);
        // no piece, null
        if (board.getPiece(startPosition) == null) {
            return null;
        }

        HashSet<ChessMove> moveSet = (HashSet<ChessMove>) board.getPiece(startPosition).pieceMoves(board, startPosition);
        HashSet<ChessMove> validMoveSet = (HashSet<ChessMove>) moveSet.clone();
        // move must:
        // this algorithm takes the moveset for the piece
        // then iterates thru, removing any ones resulting is self check
        for (ChessMove move : moveSet) {
            if (checkForSelfCheck(move, board.getPiece(move.getStartPosition()).getTeamColor())) {
                validMoveSet.remove(move);
                // System.out.println(move + " is not valid");
            }
        }

        return validMoveSet;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        System.out.println("attempting move " + move.prettyPrint() + " on " + turn + "'s turn");

        // space check
        if (board.getPiece(move.getStartPosition()) == null) {
            throw new InvalidMoveException("space has no piece");
        }

        // turn check
        if (board.getPiece(move.getStartPosition()).getTeamColor() != turn) {
            throw new InvalidMoveException("not your turn or piece");
        }

        // move validity check
        if (!validMoves(move.getStartPosition()).contains(move)) {
            throw new InvalidMoveException(move + " is not a valid move");
        }

        // end of game check
        if (getWinner() != null) {
            throw new InvalidMoveException("game is over, " + getWinner() + " won");
        }

        // modify the board
        ChessPiece movingPiece = board.getPiece(move.getStartPosition());
        board.addPiece(move.getStartPosition(), null);
        // promote if necessary and change value of firstMove
        if (move.getPromotionPiece() != null) {
            board.addPiece(move.getEndPosition(), new ChessPiece(turn, move.getPromotionPiece(), false));
        } else {
            board.addPiece(move.getEndPosition(), movingPiece);
        }
        board.getPiece(move.getEndPosition()).setFirstMove(false);

        // check for enemy check due to this move and change turn
        if (turn == TeamColor.WHITE) {
            blackCheck = checkForEnemyCheck(move);
            turn = TeamColor.BLACK;
        } else {
            whiteCheck = checkForEnemyCheck(move);
            turn = TeamColor.WHITE;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        // given the position of the king, checks if the team is in check
        ChessPosition king = board.findKing(teamColor);
        // System.out.println(king + " is king for " + teamColor);
        setCheck(teamColor, checkForSelfCheck(new ChessMove(king, king), teamColor));
        // System.out.println("white check: " + whiteCheck + ", black check: " + blackCheck);

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
        // can't be in checkmate if not in check, that is a stalemate
        // System.out.print(board + "\n");
        if (!isInCheck(teamColor)) {
            return false;
        }

        // for each piece check for valid moves
        HashSet<ChessMove> allMoves = getUnionTeamMoves(teamColor);
        // if empty, return true, else false
        return allMoves.isEmpty();
    }

    private HashSet<ChessMove> getUnionTeamMoves(TeamColor teamColor) {
        HashSet<ChessMove> unionValidMoves = new HashSet<>();
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                // unions all valid moves of pieces
                ChessPosition curr = new ChessPosition(i,j);
                if (board.getPiece(curr) != null && board.getPiece(curr).getTeamColor() == teamColor) {
                    HashSet<ChessMove> set = (HashSet<ChessMove>) validMoves(curr);
                    // if (set != null) System.out.println("for (" + i + "," + j + "), valid moves are: " + set);
                    if (set != null) { unionValidMoves.addAll(set); }
                }
            }
        }
        return unionValidMoves;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        // same conditions as checkmate (no possible moves) but not in check
        // System.out.print(board + "\n");
        if (isInCheck(teamColor)) {
            return false;
        }
        // for each piece check for valid moves
        HashSet<ChessMove> allMoves = getUnionTeamMoves(teamColor);
        // if empty, return true, else false
        return allMoves.isEmpty();
    }

    protected boolean checkForEnemyCheck(ChessMove move) {
        // iterate through the next possible valid moves after this
        boolean checkFromThis = false;

        for (ChessMove m : validMoves(move.getEndPosition())) {
            ChessPiece end = board.getPiece(m.getEndPosition());

            // if the end has a king, we already checked it's a different color in the move calculator
            if(end != null && end.getPieceType() == ChessPiece.PieceType.KING) {
                checkFromThis = true;
            }
        }

        return checkFromThis;
    }

    protected boolean checkForSelfCheck(ChessMove move, TeamColor teamColor) {
        ChessBoard newBoard = new ChessBoard(board);
        ChessPiece movingPiece = board.getPiece(move.getStartPosition());
        newBoard.addPiece(move.getStartPosition(), null);
        newBoard.addPiece(move.getEndPosition(), movingPiece);

        // System.out.print("checking for self check. board after move " + move + ":\n" + newBoard);

        /*
        we act as if the king is a queen, rook, etc. and check if any captures end in a matching piece type
        two identical pieces can always capture each other, thus if the king acts as another piece and can
        capture it, the other piece is able to capture the king, and we are in check
         */
        ChessPosition kingPosition = newBoard.findKing(teamColor);
        for (ChessPiece.PieceType type : ChessPiece.PieceType.values()) {
            if (checkMoveCalc(type, kingPosition, newBoard)) {
                System.out.println(move + " puts self in check");
                return true;
            }
        }
        return false;
    }

    protected boolean checkMoveCalc(ChessPiece.PieceType pieceType, ChessPosition kingPosition, ChessBoard newBoard) {
        // System.out.println("checking for " + pieceType + " on king at " + kingPosition);
        MoveCalc moveCalc;
        moveCalc = switch (pieceType) {
            case PAWN -> new PawnMoveCalc(newBoard.getPiece(kingPosition), newBoard, kingPosition);
            case QUEEN -> new QueenMoveCalc(newBoard.getPiece(kingPosition), newBoard, kingPosition);
            case ROOK -> new RookMoveCalc(newBoard.getPiece(kingPosition), newBoard, kingPosition);
            case BISHOP -> new BishopMoveCalc(newBoard.getPiece(kingPosition), newBoard, kingPosition);
            case KING -> new KingMoveCalc(newBoard.getPiece(kingPosition), newBoard, kingPosition);
            case KNIGHT -> new KnightMoveCalc(newBoard.getPiece(kingPosition), newBoard, kingPosition);
        };

        // for all the moves, if one results in a matching piece it is a capture and they can capture the king
        for (ChessMove move : moveCalc.getMoves()) {
            ChessPiece endPiece = newBoard.getPiece(move.getEndPosition());
            if (endPiece != null && endPiece.getPieceType() == pieceType) {
                // System.out.println("self check, reverse move is " + move);
                return true;
            }
        }
        return false;
    }

    /**
     * Sets turn to null so all moves throw an exception. Winner is stored as check boolean.
     */
    public void resign(TeamColor color) throws InvalidMoveException {
        System.out.println("resign called with " + color + " and game state " + this);
        if (getWinner() == null) {
            this.turn = null;
            this.whiteCheck = (color == TeamColor.WHITE);
            this.blackCheck = (color == TeamColor.BLACK);
        } else {
            throw new InvalidMoveException("game is already over, " + getWinner() + " won");
        }
    }

    /**
     * If game is over, returns the winner.
     * @return Winner or null if no winner yet or stalemate.
     */
    public String getWinner() {
        if (turn == null && (whiteCheck ^ blackCheck)) { // xor check bools means one team won
            System.out.println("getWinner() returns winner");
            return ((whiteCheck) ? TeamColor.WHITE : TeamColor.BLACK).toString();
        } else if (turn == null) { // both checks are same but turn is null then stalemate
            System.out.println("getWinner() returns stalemate");
            return "STALEMATE";
        } else { // if turn != null game is not over
            System.out.println("getWinner() returns null");
            return null;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return whiteCheck == chessGame.whiteCheck &&
                blackCheck == chessGame.blackCheck &&
                Objects.equals(board, chessGame.board) && turn == chessGame.turn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, turn, whiteCheck, blackCheck);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "turn=" + turn +
                ", whiteCheck=" + whiteCheck +
                ", blackCheck=" + blackCheck +
                ", board=\n" + board +
                '}';
    }
}
