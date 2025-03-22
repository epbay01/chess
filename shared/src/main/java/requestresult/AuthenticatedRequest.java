package requestresult;

import java.util.Objects;

public final class AuthenticatedRequest {
    private final transient String authToken;

    public AuthenticatedRequest(String authToken) {
        this.authToken = authToken;
    }

    public String authToken() {
        return authToken;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (AuthenticatedRequest) obj;
        return Objects.equals(this.authToken, that.authToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authToken);
    }

    @Override
    public String toString() {
        return "AuthenticatedRequest[" +
                "authToken=" + authToken + ']';
    }

}
