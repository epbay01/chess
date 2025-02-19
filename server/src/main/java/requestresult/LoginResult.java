package requestresult;

public class LoginResult extends Result {
    String authToken;
    String username;

    public LoginResult(String authToken, String username) {
        this.authToken = authToken;
        this.username = username;
    }
}
