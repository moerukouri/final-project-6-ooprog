package exception;

public class IllegalUserException extends RuntimeException {
    public IllegalUserException() {
        super("Error: User action encountered a problem.");
    }

    public IllegalUserException(String id) {
        super("Error: User ID was not found.");
    }
}