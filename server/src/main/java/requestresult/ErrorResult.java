package requestresult;

import java.util.Objects;

public class ErrorResult extends Result {
    String message;

    public ErrorResult(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ErrorResult that = (ErrorResult) o;
        return Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(message);
    }
}
