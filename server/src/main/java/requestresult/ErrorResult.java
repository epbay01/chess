package requestresult;

public class ErrorResult extends Result {
    String message;

    public ErrorResult(String message) {
        this.message = message;
    }
}
