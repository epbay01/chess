package requestresult;

import java.util.Objects;

public class LoginResult extends Result {
    String authToken;
    String username;

    public LoginResult(String authToken, String username) {
        this.authToken = authToken;
        this.username = username;
    }

    public String getAuthToken() { return authToken; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LoginResult that = (LoginResult) o;
        return Objects.equals(authToken, that.authToken) && Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authToken, username);
    }
}
