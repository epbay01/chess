package requestresult;

public record JoinGameRequest(String authToken,
                              String gameId,
                              String playerColor) {
}
