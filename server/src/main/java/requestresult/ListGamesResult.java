package requestresult;

import model.GameData;
import java.util.List;

public class ListGamesResult extends Result {
    List<GameData> games;

    public ListGamesResult(List<GameData> games) {
        this.games = games;
    }
}
