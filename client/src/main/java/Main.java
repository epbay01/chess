import client.ServerFacade;
import ui.Repl;

public class Main {
    public static void main(String[] args) {
        ServerFacade serverFacade = new ServerFacade();
        Repl repl = new Repl(serverFacade);
        repl.replMain();
    }
}