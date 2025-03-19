package requestresult;

public record CreateGameRequest(String authToken,
                                String gameName) {
}
