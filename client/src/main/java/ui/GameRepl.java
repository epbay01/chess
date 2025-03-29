package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.security.InvalidParameterException;
import java.util.Scanner;

public class GameRepl {
    private Repl parentRepl;
    private ChessGame.TeamColor color;
    private ChessGame game;
    private String gameId;

    public GameRepl(Repl parentRepl, ChessGame.TeamColor color, String id) {
        this.parentRepl = parentRepl;
        this.color = color;
        this.game = null;
        if (parentRepl.validateGameId(id)) {
            this.gameId = id;
        } else {
            throw new InvalidParameterException("Bad game id");
        }
    }

    private void gameRepl() {
        boolean loop = true;

        while(loop) {
            ChessBoard board = game.getBoard();
            printBoard(board);
            String[] inputs = getInput();
            loop = false;
        }
    }

    private void observeRepl() {
        boolean loop = true;

        while (loop) {
            getInput();
            loop = false;
        }
    }

    protected void observe() {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "Observing game " + parentRepl.getGameName(gameId));
        observeRepl();
    }

    protected void game() {
        connect();
        gameRepl();
    }

    private void printBoard(ChessBoard board) {
        System.out.print(Repl.RESET_ALL + EscapeSequences.ERASE_SCREEN);

        System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREEN + EscapeSequences.SET_TEXT_BOLD
                + EscapeSequences.SET_TEXT_COLOR_WHITE);
        System.out.print(EscapeSequences.EMPTY);
        char column;
        for (int k = 0; k < 8; k++) {
            if (color == ChessGame.TeamColor.WHITE) {
                column = (char) ('a' + k);
            } else {
                column = (char) ('a' + (7 - k));
            }
            System.out.print(" " + column + " ");
        }
        System.out.print(Repl.RESET_ALL + "\n");

        boolean currentlyWhite = true;
        for(int i = 0; i < 8; i++) {
            int y = i + 1;
            if (color == ChessGame.TeamColor.WHITE) {
                y = 8 - i;
            }

            System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREEN + EscapeSequences.SET_TEXT_BOLD
                    + EscapeSequences.SET_TEXT_COLOR_WHITE);
            System.out.print(" " + y + " ");

            for (int j = 0; j < 8; j++) {
                System.out.print(Repl.RESET_ALL);
                int x = j + 1;
                if (color == ChessGame.TeamColor.BLACK) {
                    x = 8 - j;
                }

                if (currentlyWhite) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + getChessPiece(board, y, x));
                } else {
                    System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY + getChessPiece(board, y, x));
                }

                if (j != 7) {
                    currentlyWhite = !currentlyWhite;
                }
            }
            System.out.print(Repl.RESET_ALL + "\n");
        }
    }

    private String getChessPiece(ChessBoard board, int y, int x) {
        ChessPiece piece = board.getPiece(new ChessPosition(y, x));
        if (piece == null) {
            return EscapeSequences.EMPTY;
        }

        return switch (piece.getPieceType()) {
            case KNIGHT -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ?
                    EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            case KING -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ?
                    EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
            case ROOK -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ?
                    EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
            case BISHOP -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ?
                    EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            case QUEEN -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ?
                    EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
            case PAWN -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ?
                    EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
        };
    }

    private String[] getInput() {
        System.out.print(Repl.RESET_ALL);
        if (color == ChessGame.TeamColor.WHITE) {
            System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
            System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.SET_TEXT_BOLD + parentRepl.getGameName(gameId)
                    + Repl.RESET_ALL + " | ");
            System.out.print(EscapeSequences.SET_TEXT_ITALIC + EscapeSequences.SET_TEXT_COLOR_WHITE
                    + parentRepl.authData.username());
            System.out.print(Repl.RESET_ALL + " >> ");
        } else {
            System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
            System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.SET_TEXT_BOLD + parentRepl.getGameName(gameId)
                    + Repl.RESET_ALL + " | ");
            System.out.print(EscapeSequences.SET_TEXT_ITALIC + EscapeSequences.SET_TEXT_COLOR_BLACK
                    + parentRepl.authData.username());
            System.out.print(Repl.RESET_ALL + " >> ");
        }
        Scanner in = new Scanner(System.in);
        return in.nextLine().split(" ");
    }

    // will connect to websocket and init game state
    private void connect() {
        this.game = new ChessGame();
    }
}
