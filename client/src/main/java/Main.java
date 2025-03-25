import chess.*;

import client.ServerFacade;
import ui.Repl;

public class Main {
    private static Repl repl;
    private static ServerFacade serverFacade;

    public static void main(String[] args) {
        serverFacade = new ServerFacade();
        repl = new Repl(serverFacade);
        repl.replMain();
    }
}