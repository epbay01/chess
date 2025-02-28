package requestresult;

import model.GameData;
import java.util.List;
import java.util.Objects;

public class ListGamesResult extends Result {
    List<GameData> games;

    public ListGamesResult(List<GameData> games) {
        this.games = games;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ListGamesResult that = (ListGamesResult) o;
        return Objects.equals(games, that.games);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(games);
    }
}
