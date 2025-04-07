package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import client.WebsocketFacade;

import java.security.InvalidParameterException;
import java.util.Scanner;

public class GameRepl {
    private final Repl parentRepl;
    private final ChessGame.TeamColor color;
    private final String gameId;
    private final WebsocketFacade websocketFacade;
    public ChessGame game;

    public GameRepl(Repl parentRepl, ChessGame.TeamColor color, String id) {
        this.parentRepl = parentRepl;
        this.color = color;
        this.game = new ChessGame(); // TODO: this is temporary
        if (parentRepl.validateGameId(id)) {
            this.gameId = id;
        } else {
            throw new InvalidParameterException("invalid game id");
        }

        try {
            this.websocketFacade = new WebsocketFacade(parentRepl.serverFacade, this);
        } catch (Exception e) {
            throw new RuntimeException("Unable to connect to server");
        }
    }

    protected void observe() {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "Observing game " + parentRepl.getGameName(gameId));
        connect();
        observeRepl();
    }

    protected void game() {
        connect();
        gameRepl();
    }

    private void gameRepl() {
        boolean loop = true;
        connect();
        updateGame();
        printBoard(game.getBoard());

        while(loop) {
            String[] inputs = getInput();
            switch (inputs[0]) {
                case "help", "h":
                    help();
                    System.out.print(EscapeSequences.ERASE_SCREEN);
                    printBoard(game.getBoard());
                    break;
                case "quit", "leave", "q":
                    if (confirm()) {
                        loop = false;
                    }
                    break;
                default:
                    parentRepl.invalid();
                    break;
            }
        }
    }

    private void observeRepl() {
        boolean loop = true;
        printBoard(game.getBoard());
        connect();

        while (loop) {
            String[] inputs = getInputObserver();
            switch (inputs[0]) {
                case "quit", "leave", "q":
                    loop = false;
                    break;
                case "help", "h":
                    help();
                    printBoard(game.getBoard());
                    break;
                case "redraw", "r":
                    printBoard(game.getBoard());
                    break;
                default:
                    parentRepl.invalid();
                    break;
            }
        }
    }

    private void help() {
        System.out.print(Repl.RESET_ALL + EscapeSequences.ERASE_SCREEN + "\n");
        String command = Repl.RESET_ALL + EscapeSequences.SET_TEXT_ITALIC + EscapeSequences.SET_TEXT_COLOR_BLUE;

        System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "When observer or player:");
        System.out.println(command + "quit, leave, q: " + Repl.RESET_ALL + "Quits back to main client.");
        System.out.println(command + "help, h: " + Repl.RESET_ALL + "Displays help text.");
        System.out.println(command + "redraw, r: " + Repl.RESET_ALL + "Redraws the board, updating from the server.");

        System.out.println("\n" + EscapeSequences.SET_TEXT_COLOR_GREEN + "When playing:");
        System.out.println(command + "move, m [start letter][start number] [end letter][end number]: " + Repl.RESET_ALL
                + "Moves the piece at the given start position to the end position if valid.");
        System.out.println(command + "highlight, show, s: " + Repl.RESET_ALL + "Highlights all legal moves.");
        System.out.println(command + "resign: " + Repl.RESET_ALL + "Resigns the game." + "\n");

        parentRepl.waitForQ();
    }

    private void printBoard(ChessBoard board) {
        updateGame();

        System.out.print(EscapeSequences.ERASE_SCREEN + Repl.RESET_ALL);

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
        System.out.print(EscapeSequences.EMPTY + Repl.RESET_ALL + "\n");

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
            System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREEN + EscapeSequences.EMPTY);
            System.out.print(Repl.RESET_ALL + "\n");
        }
        System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREEN);
        for (int i = 0; i < 10; i++) System.out.print(EscapeSequences.EMPTY);

        System.out.print(Repl.RESET_ALL + "\n\n");
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
                    + Repl.RESET_ALL + EscapeSequences.SET_BG_COLOR_DARK_GREY + " | ");
            System.out.print(EscapeSequences.SET_TEXT_ITALIC + EscapeSequences.SET_TEXT_COLOR_WHITE
                    + parentRepl.authData.username());
            System.out.print(Repl.RESET_ALL + " >> ");
        } else {
            System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
            System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.SET_TEXT_BOLD + parentRepl.getGameName(gameId)
                    + Repl.RESET_ALL + EscapeSequences.SET_BG_COLOR_LIGHT_GREY + " | ");
            System.out.print(EscapeSequences.SET_TEXT_ITALIC + EscapeSequences.SET_TEXT_COLOR_BLACK
                    + parentRepl.authData.username());
            System.out.print(Repl.RESET_ALL + " >> ");
        }
        Scanner in = new Scanner(System.in);
        return in.nextLine().split(" ");
    }

    private String[] getInputObserver() {
        System.out.print(Repl.RESET_ALL);
        System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREEN + EscapeSequences.SET_TEXT_COLOR_BLUE
                + EscapeSequences.SET_TEXT_BOLD);
        System.out.print(parentRepl.getGameName(gameId) + Repl.RESET_ALL + EscapeSequences.SET_BG_COLOR_DARK_GREEN
                + EscapeSequences.SET_TEXT_COLOR_WHITE + " | observer >>" + Repl.RESET_ALL + " ");
        Scanner in = new Scanner(System.in);
        return in.nextLine().split(" ");
    }

    private boolean confirm() {
        while (true) {
            System.out.print(Repl.RESET_ALL + EscapeSequences.SET_TEXT_COLOR_BLUE);
            System.out.print("Are you sure? ");
            Scanner in = new Scanner(System.in);
            String input = in.nextLine();
            if (input.equals("yes") || input.equals("y")) {
                return true;
            } else if (input.equals("no") || input.equals("n")) {
                return false;
            }
        }
    }

    // will connect to websocket and init game state
    private void connect() {
        websocketFacade.connect(parentRepl.authData, Integer.parseInt(gameId), color);
    }

    // will get the game from the server via websocket
    private void updateGame() {}

    public void notify(String msg) {
        System.out.print(Repl.RESET_ALL + "\n");
        System.out.println(EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_BLUE + msg);
    }

    public void error(String msg) {
        System.out.print(Repl.RESET_ALL + "\n");
        System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + msg);
    }
}
