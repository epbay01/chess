package exceptions;

public class BadStatusCodeException extends RuntimeException {
    public int statusCode;
    public BadStatusCodeException(int statusCode, String message) {
      super(message);
      this.statusCode = statusCode;
    }
}
