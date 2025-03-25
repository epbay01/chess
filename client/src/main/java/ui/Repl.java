package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import client.ServerFacade;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.List;
import java.util.Scanner;

public class Repl {
    private final ServerFacade serverFacade;
    private AuthData authData;
    private boolean loggedIn;
    private static final String RESET_ALL = EscapeSequences.RESET_TEXT_COLOR
            + EscapeSequences.RESET_TEXT_ITALIC
            + EscapeSequences.RESET_TEXT_BOLD_FAINT
            + EscapeSequences.RESET_BG_COLOR
            + EscapeSequences.RESET_TEXT_BLINKING
            + EscapeSequences.RESET_TEXT_UNDERLINE;

    public Repl(ServerFacade server) {
        this.serverFacade = server;
        this.authData = null;
        this.loggedIn = false;
    }

    public void replMain() {
        boolean repeat = true;

        // init text
        System.out.print(EscapeSequences.ERASE_SCREEN + RESET_ALL);
        System.out.println(EscapeSequences.SET_TEXT_COLOR_YELLOW + EscapeSequences.WHITE_QUEEN
                + EscapeSequences.SET_TEXT_COLOR_RED + " Welcome to Chess! "
                + EscapeSequences.SET_TEXT_COLOR_YELLOW + EscapeSequences.BLACK_QUEEN);
        System.out.println(RESET_ALL + "Type 'help' for help.");
        System.out.print("\n");


        while (repeat) {
            if (!loggedIn) {
                repeat = unregistered();
            } else {
                repeat = registered();
            }
        }
    }

    // UNREGISTERED FUNCTIONS

    // REPL
    private boolean unregistered() {
        System.out.print(RESET_ALL);
        switch (getInput("unregistered")[0]) {
            case "quit", "q":
                return false;
            case "help", "h":
                help();
                break;
            case "login", "l":
                loggedIn = login(false);
                break;
            case "register", "r":
                loggedIn = login(true);
                break;
            default:
                invalid();
                break;
        }
        return true;
    }

    private void help() {
        System.out.print(EscapeSequences.ERASE_SCREEN + "\n\n");
        System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "\nAny time:");
        System.out.println(
                EscapeSequences.SET_TEXT_ITALIC + EscapeSequences.SET_TEXT_COLOR_BLUE + "help, h: "
                + RESET_ALL + "Displays all available commands."
        );
        System.out.println(
                EscapeSequences.SET_TEXT_ITALIC + EscapeSequences.SET_TEXT_COLOR_BLUE + "quit, q: "
                + RESET_ALL + "Quits the client."
        );

        System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "\nWhen logged out:");
        System.out.println(
                EscapeSequences.SET_TEXT_ITALIC + EscapeSequences.SET_TEXT_COLOR_BLUE + "login, l: "
                + RESET_ALL + "Login to the server."
        );
        System.out.println(
                EscapeSequences.SET_TEXT_ITALIC + EscapeSequences.SET_TEXT_COLOR_BLUE + "register, r: "
                + RESET_ALL + "Register a new user."
        );

        System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "\nWhen logged in:");
        System.out.println(
                EscapeSequences.SET_TEXT_ITALIC + EscapeSequences.SET_TEXT_COLOR_BLUE + "logout, l: "
                + RESET_ALL + "Logout of the server."
        );
        System.out.println(
                EscapeSequences.SET_TEXT_ITALIC + EscapeSequences.SET_TEXT_COLOR_BLUE + "list, lg: "
                + RESET_ALL + "Lists all games and ids."
        );
        System.out.println(
                EscapeSequences.SET_TEXT_ITALIC + EscapeSequences.SET_TEXT_COLOR_BLUE + "join, jg [game id]: "
                + RESET_ALL + "Joins the given game."
        );
        System.out.println(
                EscapeSequences.SET_TEXT_ITALIC + EscapeSequences.SET_TEXT_COLOR_BLUE + "observe, o [game id]: "
                + RESET_ALL + "Observes the given game."
        );
        System.out.println(
                EscapeSequences.SET_TEXT_ITALIC + EscapeSequences.SET_TEXT_COLOR_BLUE + "create, cg [game name]: "
                + RESET_ALL + "Creates a new game, does not join it."
        );

        waitForQ();
    }

    private boolean login(boolean newUser) {
        System.out.print("Username: ");
        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine();
        username = username.strip();

        System.out.print("Password: ");
        String password = scanner.nextLine();
        password = password.strip();

        System.out.print("Email: ");
        String email = scanner.nextLine();
        email = email.strip();

        var user = new UserData(username, password, email);
        try {
            authData = (newUser) ? serverFacade.register(user) : serverFacade.login(user);
            return true;
        } catch (Exception e) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Error: " + e.getMessage());
            return false;
        }
    }

    // REGISTERED FUNCTIONS

    // REPL
    private boolean registered() {
        System.out.print(RESET_ALL);
        if (authData == null) {
            loggedIn = false;
            System.out.print(EscapeSequences.SET_TEXT_COLOR_RED + "Error: Something went wrong with login.");
            return true;
        }

        var inp = getInput(authData.username());
        switch (inp[0]) {
            case "quit", "q":
                loggedIn = !logout();
                return false;
            case "help", "h":
                help();
                break;
            case "logout", "l":
                loggedIn = !logout();
                break;
            case "list", "lg":
                list();
                break;
            case "join", "jg":
                if (inp.length < 2) {
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_RED
                            + "Error: Didn't input correct arguments.");
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.SET_TEXT_ITALIC
                            + "Correct usage:"
                            + RESET_ALL + " join [game id] | jg [game id]");
                    break;
                }
                join(inp[1]);
                break;
            case "create", "cg":
                if (inp.length < 2) {
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_RED
                            + "Error: Didn't input correct arguments.");
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.SET_TEXT_ITALIC
                            + "Correct usage:"
                            + RESET_ALL + " create [game name] | cg [game name]");
                    break;
                }
                create(inp[1]);
                break;
            case "observe", "o":
                if (inp.length < 2) {
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_RED
                            + "Error: Didn't input correct arguments.");
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.SET_TEXT_ITALIC
                            + "Correct usage:"
                            + RESET_ALL + " observe [game id] | o [game id]");
                    break;
                }
                observe(inp[1]);
                break;
            default:
                invalid();
                break;
        }
        return true;
    }

    private boolean logout() {
        System.out.print(RESET_ALL);
        try {
            serverFacade.logout(authData);
            authData = null;
            System.out.println(EscapeSequences.RESET_TEXT_ITALIC + "Successfully logged out.");
            return true;
        } catch (Exception e) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Error: " + e.getMessage());
            return false;
        }
    }

    private void list() {
        System.out.print(RESET_ALL);
        System.out.print(EscapeSequences.ERASE_SCREEN);
        List<GameData> games = serverFacade.listGames(authData);
        final String empty = EscapeSequences.SET_TEXT_UNDERLINE + EscapeSequences.SET_TEXT_ITALIC
                + EscapeSequences.SET_TEXT_BOLD + "empty" + EscapeSequences.RESET_TEXT_ITALIC
                + EscapeSequences.RESET_TEXT_UNDERLINE + EscapeSequences.RESET_TEXT_BOLD_FAINT;

        for (GameData game : games) {
            String white = (game.whiteUsername() != null ? game.whiteUsername() : empty);
            String black = (game.blackUsername() != null ? game.blackUsername() : empty);


            System.out.println(
                    EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_YELLOW + game.gameID() + ":"
                    + RESET_ALL + " "
                    + EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.SET_TEXT_UNDERLINE + game.gameName()
                    + RESET_ALL + " " + EscapeSequences.SET_BG_COLOR_LIGHT_GREY
                    + EscapeSequences.SET_TEXT_COLOR_WHITE + "[" + white + "] "
                    + EscapeSequences.SET_TEXT_COLOR_BLACK + "[" + black + "]"
                    + RESET_ALL
            );
        }
        waitForQ();
    }

    private void create(String name) {
        int id = serverFacade.createGame(authData, name);
        System.out.println("Successfully created game " + name + " with id "
                + EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_YELLOW + id);
    }

    private void join(String id) {
        Scanner scanner = new Scanner(System.in);
        boolean loop = true;
        ChessGame.TeamColor color = null;

        while (loop) {
            System.out.print(RESET_ALL + "Enter w/white or b/black: ");
            String inp = scanner.nextLine();
            if (inp.equals("b") || inp.equals("black")) {
                color = ChessGame.TeamColor.BLACK;
                loop = false;
            } else if (inp.equals("w") || inp.equals("white")) {
                color = ChessGame.TeamColor.WHITE;
                loop = false;
            } else {
                System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Invalid input, enter w, white, b, or black.");
            }
        }

        try {
            serverFacade.joinGame(authData, Integer.parseInt(id), color);
            System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "Successfully joined game!");
            game(color);
        } catch (Exception e) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Error: " + e.getMessage());
        }
    }

    private void observe(String id) {
        System.out.println("Observing game " + id);
        // functionality requires websocket
    }

    private void invalid() {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Invalid input. Type \"help\" or \"h\" for help.");
    }


    private void game(ChessGame.TeamColor color) {
        ChessGame game = new ChessGame(); // temporary
        ChessBoard board = game.getBoard();

        printBoard(board, color);

        // here will implement gameplay/call gameplay class

        waitForQ();
    }

    private void printBoard(ChessBoard board, ChessGame.TeamColor color) {
        System.out.print(RESET_ALL + EscapeSequences.ERASE_SCREEN);

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
        System.out.print(RESET_ALL + "\n");

        boolean currentlyWhite = true;
        for(int i = 0; i < 8; i++) {
            int x = i + 1;
            if (color == ChessGame.TeamColor.BLACK) {
                x = 8 - i;
            }

            System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREEN + EscapeSequences.SET_TEXT_BOLD
                + EscapeSequences.SET_TEXT_COLOR_WHITE);
            System.out.print(" " + x + " ");

            for (int j = 0; j < 8; j++) {
                System.out.print(RESET_ALL);
                int y = j + 1;
                if (color == ChessGame.TeamColor.BLACK) {
                    y = 8 - j;
                }

                if (currentlyWhite) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + getChessPiece(board, x, y));
                } else {
                    System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY + getChessPiece(board, x, y));
                }

                if (j != 7) {
                    currentlyWhite = !currentlyWhite;
                }
            }
            System.out.print(RESET_ALL + "\n");
        }
    }

    private String getChessPiece(ChessBoard board, int x, int y) {
        ChessPiece piece = board.getPiece(new ChessPosition(x, y));
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


    private String[] getInput(String user) {
        System.out.print(EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_BLUE + "chess"
                + RESET_ALL + " | "
                + EscapeSequences.SET_TEXT_COLOR_GREEN + user
                + RESET_ALL + " >> ");
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        return line.split(" ");
    }

    private void waitForQ() {
        System.out.println("Enter q to quit.");
        Scanner scanner = new Scanner(System.in);
        boolean loop = true;
        while (loop) {
            if (scanner.next().equals("q")) {
                loop = false;
            }
        }
    }
}
