package exception;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException() {
        super("Error: You do not have permission to perform this action.");
    }

    public AccessDeniedException(String username, String operation) {
        super(String.format("Error: %s cannot access %s", username, operation));
    }
}