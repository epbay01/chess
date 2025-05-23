package ui;

import chess.*;
import client.WebsocketFacade;
import websocket.commands.UserGameCommand;

import java.security.InvalidParameterException;
import java.util.HashSet;
import java.util.Scanner;

public class GameRepl {
    private final Repl parentRepl;
    private final ChessGame.TeamColor color;
    private final String gameId;
    private final WebsocketFacade websocketFacade;
    public boolean observing;
    public ChessGame game;

    public GameRepl(Repl parentRepl, ChessGame.TeamColor color, String id) {
        this.parentRepl = parentRepl;
        this.color = color;
        this.game = null;
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
        observing = true;
        connect();
        observeRepl();
    }

    protected void game() {
        observing = false;
        connect();
        gameRepl();
    }

    private void gameRepl() {
        boolean loop = true;
        if (game != null) {
            printBoard(game.getBoard());
        }

        while(loop) {
            try {
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
                            leave();
                        }
                        break;
                    case "redraw", "r":
                        System.out.print(EscapeSequences.ERASE_SCREEN);
                        printBoard(game.getBoard());
                        break;
                    case "move", "m":
                        if (inputs.length < 3) {
                            parentRepl.invalid();
                            break;
                        }
                        String[] args = new String[]{inputs[1], inputs[2]};
                        move(args);
                        break;
                    case "resign":
                        if (confirm()) {
                            loop = false;
                            resign();
                        }
                        break;
                    case "highlight", "show", "s":
                        if (inputs.length != 2) {
                            parentRepl.invalid();
                            break;
                        }
                        highlight(inputs[1]);
                        break;
                    default:
                        parentRepl.invalid();
                        break;
                }
            }  catch (Exception e) {
                error(e.getMessage());
                loop = true;
            }
        }
    }

    private void observeRepl() {
        boolean loop = true;
        if (game != null) {
            printBoard(game.getBoard());
        }

        while (loop) {
            try {
                String[] inputs = getInputObserver();
                switch (inputs[0]) {
                    case "quit", "leave", "q":
                        loop = false;
                        leave();
                        break;
                    case "help", "h":
                        help();
                        if (game != null) {
                            printBoard(game.getBoard());
                        }
                        break;
                    case "redraw", "r":
                        if (game != null) {
                            printBoard(game.getBoard());
                        }
                        break;
                    default:
                        parentRepl.invalid();
                        break;
                }
            } catch (Exception e) {
                error(e.getMessage());
                loop = true;
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
        System.out.println(command + "highlight, show, s [letter][number]: " + Repl.RESET_ALL
                + "Highlights all legal moves for the given piece.");
        System.out.println(command + "resign: " + Repl.RESET_ALL + "Resigns the game." + "\n");

        parentRepl.waitForQ();
    }

    public void printBoard(ChessBoard board) { printBoard(board, null); }
    public void printBoard(ChessBoard board, ChessPosition highlight) {
        HashSet<ChessPosition> positionsToHighlight = new HashSet<>();

        if (game == null) {
            return;
        }

        if (highlight != null) {
            this.game.validMoves(highlight).forEach(
                    (item) -> positionsToHighlight.add(item.getEndPosition())
            );
            positionsToHighlight.add(highlight);
        }

        System.out.println(EscapeSequences.ERASE_SCREEN + Repl.RESET_ALL);

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

                if (positionsToHighlight.contains(new ChessPosition(y, x))) {
                    highlightPosition(y, x, currentlyWhite);
                } else {
                    if (currentlyWhite) {
                        System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + getChessPiece(board, y, x));
                    } else {
                        System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY + getChessPiece(board, y, x));
                    }
                }

                if (j != 7) {
                    currentlyWhite = !currentlyWhite;
                }
            }
            System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREEN + EscapeSequences.EMPTY);
            System.out.print(Repl.RESET_ALL + "\n");
        }
        System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREEN);
        for (int i = 0; i < 10; i++) { System.out.print(EscapeSequences.EMPTY); }

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

    private void highlightPosition(int y, int x, boolean light) {
        String piece = getChessPiece(game.getBoard(), y, x);
        String background = (light) ? EscapeSequences.SET_BG_COLOR_RED : EscapeSequences.SET_BG_COLOR_MAGENTA;
        System.out.print(Repl.RESET_ALL + background + piece);
    }

    private void move(String[] inp) {
        if (inp.length > 2) {
            parentRepl.invalid();
            return;
        }

        try {
            var inp1 = inp[0].toCharArray();
            var inp2 = inp[1].toCharArray();
            ChessPiece.PieceType promotion = null;

            var pos1 = new ChessPosition(Integer.parseInt(inp[0].substring(1)),
                    (inp1[0] - 'a' + 1));
            var pos2 = new ChessPosition(Integer.parseInt(inp[1].substring(1)),
                    (inp2[0] - 'a' + 1));

            if (pos2.getRow() == 1 && color == ChessGame.TeamColor.BLACK
                || pos2.getRow() == 8 && color == ChessGame.TeamColor.WHITE) {
                if (game.getBoard().getPiece(pos1) != null
                        && game.getBoard().getPiece(pos1).getPieceType() == ChessPiece.PieceType.PAWN) {
                    System.out.print(Repl.RESET_ALL + "What piece would you like to promote to? ");

                    boolean loop = false;
                    do {
                        Scanner in = new Scanner(System.in);
                        promotion = switch (in.nextLine().toLowerCase()) {
                            case "pawn" -> ChessPiece.PieceType.PAWN;
                            case "knight" -> ChessPiece.PieceType.KNIGHT;
                            case "rook" -> ChessPiece.PieceType.ROOK;
                            case "bishop" -> ChessPiece.PieceType.BISHOP;
                            case "queen" -> ChessPiece.PieceType.QUEEN;
                            case "king" -> ChessPiece.PieceType.KING;
                            default -> null;
                        };
                        if (promotion == null) {
                            loop = true;
                        }
                    } while (loop);
                }
            }

            sendMove(new ChessMove(pos1, pos2, promotion));
        } catch (Exception e) {
            error("Error: Invalid move input");
        }
    }

    private String[] getInput() {
        getInputPrint();
        Scanner in = new Scanner(System.in);
        return in.nextLine().split(" ");
    }

    public void getInputPrint() {
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
    }

    private String[] getInputObserver() {
        getInputObserverPrint();
        Scanner in = new Scanner(System.in);
        return in.nextLine().split(" ");
    }

    public void getInputObserverPrint() {
        System.out.print(Repl.RESET_ALL);
        System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREEN + EscapeSequences.SET_TEXT_COLOR_BLUE
                + EscapeSequences.SET_TEXT_BOLD);
        System.out.print(parentRepl.getGameName(gameId) + Repl.RESET_ALL + EscapeSequences.SET_BG_COLOR_DARK_GREEN
                + EscapeSequences.SET_TEXT_COLOR_WHITE + " | observer >>" + Repl.RESET_ALL + " ");
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

    private void highlight(String inp) {
        var inp1 = inp.toCharArray();
        var pos = new ChessPosition(Integer.parseInt(inp.substring(1)),
                (inp1[0] - 'a' + 1));

        printBoard(game.getBoard(), pos);
    }

    // websocket facade functions
    private void connect() {
        if (observing) {
            websocketFacade.command(UserGameCommand.CommandType.CONNECT,
                    parentRepl.authData,
                    parentRepl.getGameID(gameId), null);
        } else {
            websocketFacade.command(UserGameCommand.CommandType.CONNECT,
                    parentRepl.authData,
                    parentRepl.getGameID(gameId),
                    color);
        }
    }

    private void sendMove(ChessMove move) {
        websocketFacade.command(UserGameCommand.CommandType.MAKE_MOVE,
                parentRepl.authData,
                parentRepl.getGameID(gameId),
                color,
                move);
    }

    private void leave() {
        websocketFacade.command(UserGameCommand.CommandType.LEAVE,
                parentRepl.authData,
                parentRepl.getGameID(gameId),
                color);
    }

    private void resign() {
        websocketFacade.command(UserGameCommand.CommandType.RESIGN,
                parentRepl.authData,
                parentRepl.getGameID(gameId),
                color);
    }

    public void notify(String msg, String[] winData) {
        System.out.print(Repl.RESET_ALL + "\n");
        System.out.println(EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_BLUE + msg);
        if (msg.contains("WON") || msg.contains("stalemate")) {
            if (winData != null && winData.length == 4) {
                var c = (winData[3].isEmpty()) ? null : ChessGame.TeamColor.valueOf(winData[3]);
                parentRepl.notifyWin(winData[0], winData[1], winData[2], c);
            }
        }
        if (observing) {
            getInputObserverPrint();
        } else {
            getInputPrint();
        }
    }

    public void error(String msg) {
        System.out.print(Repl.RESET_ALL + "\n");
        System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + msg);
        if (observing) {
            getInputObserverPrint();
        } else {
            getInputPrint();
        }
    }
}
