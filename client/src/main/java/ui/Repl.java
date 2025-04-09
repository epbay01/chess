package ui;

import chess.ChessGame;
import client.ServerFacade;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Repl {
    protected final ServerFacade serverFacade;
    protected AuthData authData;
    private boolean loggedIn;
    private HashMap<Integer, Integer> idMap;
    protected static final String RESET_ALL = EscapeSequences.RESET_TEXT_COLOR
            + EscapeSequences.RESET_TEXT_ITALIC
            + EscapeSequences.RESET_TEXT_BOLD_FAINT
            + EscapeSequences.RESET_BG_COLOR
            + EscapeSequences.RESET_TEXT_BLINKING
            + EscapeSequences.RESET_TEXT_UNDERLINE;

    public Repl(ServerFacade server) {
        this.serverFacade = server;
        this.authData = null;
        this.loggedIn = false;
        this.idMap = new HashMap<>();
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
                EscapeSequences.SET_TEXT_ITALIC + EscapeSequences.SET_TEXT_COLOR_BLUE + "logout, lo: "
                + RESET_ALL + "Logout of the server."
        );
        System.out.println(
                EscapeSequences.SET_TEXT_ITALIC + EscapeSequences.SET_TEXT_COLOR_BLUE + "list, l: "
                + RESET_ALL + "Lists all games and ids."
        );
        System.out.println(
                EscapeSequences.SET_TEXT_ITALIC + EscapeSequences.SET_TEXT_COLOR_BLUE + "join, j [game id]: "
                + RESET_ALL + "Joins the given game."
        );
        System.out.println(
                EscapeSequences.SET_TEXT_ITALIC + EscapeSequences.SET_TEXT_COLOR_BLUE + "observe, o [game id]: "
                + RESET_ALL + "Observes the given game."
        );
        System.out.println(
                EscapeSequences.SET_TEXT_ITALIC + EscapeSequences.SET_TEXT_COLOR_BLUE + "create, c [game name]: "
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

        String email = "";
        if (newUser) {
            System.out.print("Email: ");
            email = scanner.nextLine();
            email = email.strip();
        }

        var user = new UserData(username, password, email);
        try {
            authData = (newUser) ? serverFacade.register(user) : serverFacade.login(user);

            List<GameData> games = serverFacade.listGames(authData);
            for (GameData game : games) {
                idMap.put(games.indexOf(game) + 1, game.gameID());
            }

            return true;
        } catch (Exception e) {
            System.out.print(EscapeSequences.SET_TEXT_COLOR_RED);
            System.out.println(e.getMessage());
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
            case "logout", "lo":
                loggedIn = !logout();
                break;
            case "list", "l":
                list();
                break;
            case "join", "j":
                if (inp.length < 2) {
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_RED
                            + "Error: Didn't input correct arguments.");
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.SET_TEXT_ITALIC
                            + "Correct usage:"
                            + RESET_ALL + " join [game id] | j [game id]");
                    break;
                }
                join(inp[1]);
                break;
            case "create", "c":
                if (inp.length < 2) {
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_RED
                            + "Error: Didn't input correct arguments.");
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.SET_TEXT_ITALIC
                            + "Correct usage:"
                            + RESET_ALL + " create [game name] | c [game name]");
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
                try {
                    var gameRepl = new GameRepl(this, ChessGame.TeamColor.WHITE, inp[1]);
                    gameRepl.observe();
                } catch (Exception e) {
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Error: " + e.getMessage());
                }
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
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + e.getMessage());
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

        idMap = new HashMap<>();

        for (GameData game : games) {
            String white = (game.whiteUsername() != null ? game.whiteUsername() : empty);
            String black = (game.blackUsername() != null ? game.blackUsername() : empty);

            idMap.put(games.indexOf(game) + 1, game.gameID());

            System.out.println(
                    EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_YELLOW + (games.indexOf(game) + 1)
                            + ":" + RESET_ALL + " "
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
        idMap.put(idMap.size() + 1, id);
        System.out.println("Successfully created game " + name + " with id "
                + EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_YELLOW + idMap.size());
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
            var gameRepl = new GameRepl(this, color, id);
            serverFacade.joinGame(authData, idMap.get(Integer.parseInt(id)), color);
            System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "Successfully joined game!");
            gameRepl.game();
        } catch (Exception e) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Error: " + e.getMessage());
        }
    }


    protected boolean validateGameId(String id) {
        try {
            if (!idMap.containsKey(Integer.parseInt(id))) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    protected void invalid() {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Invalid input. Type \"help\" or \"h\" for help.");
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

    protected void waitForQ() {
        System.out.println("Enter q to quit.");
        Scanner scanner = new Scanner(System.in);
        boolean loop = true;
        while (loop) {
            if (scanner.next().equals("q")) {
                loop = false;
            }
        }
    }

    protected String getGameName(String id) {
        List<GameData> games = serverFacade.listGames(authData);
        return games.get(Integer.parseInt(id) - 1).gameName();
    }

    protected int getGameID(String id) {
        return idMap.get(Integer.parseInt(id));
    }

    protected void notifyWin(String winner, String gameName, String loser, ChessGame.TeamColor winnerColor) {
        System.out.print(RESET_ALL);
        System.out.println(EscapeSequences.SET_TEXT_COLOR_YELLOW + EscapeSequences.BLACK_QUEEN);
        if (winnerColor == ChessGame.TeamColor.WHITE) {
            System.out.print(
                    EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.SET_TEXT_COLOR_WHITE
                    + " " + winner + " won the game " + gameName + "!!\n" +
                    EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_BLACK
                    + "Better luck next time, " + loser + RESET_ALL
            );
        } else if (winnerColor == ChessGame.TeamColor.BLACK) {
            System.out.println(
                    EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_BLACK
                            + " " + winner + " won the game " + gameName + "!!\n" +
                            EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.SET_TEXT_COLOR_WHITE
                            + "Better luck next time, " + loser + RESET_ALL
            );
        } else {
            System.out.println(
                    EscapeSequences.SET_BG_COLOR_DARK_GREEN + EscapeSequences.SET_TEXT_COLOR_WHITE
                    + "Stalemate!" + RESET_ALL
            );
        }
        System.out.println(EscapeSequences.SET_TEXT_COLOR_YELLOW + EscapeSequences.WHITE_QUEEN);

        System.out.print(EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_BLUE + "chess"
                + RESET_ALL + " | "
                + EscapeSequences.SET_TEXT_COLOR_GREEN + authData.username()
                + RESET_ALL + " >> ");
    }
}
