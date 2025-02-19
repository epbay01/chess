package requestresult;

public class JoinGameResult extends Result {
    String playerColor;
    String gameId;

    public JoinGameResult(String playerColor, String gameId) {
        this.playerColor = playerColor;
        this.gameId = gameId;
    }
}
