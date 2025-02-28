package requestresult;

import java.util.Objects;

public class CreateGameResult extends Result {
    int gameID;

    public CreateGameResult(int gameID) {
        this.gameID = gameID;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CreateGameResult that = (CreateGameResult) o;
        return gameID == that.gameID;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(gameID);
    }
}
