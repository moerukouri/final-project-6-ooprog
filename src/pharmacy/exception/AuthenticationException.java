package exception;

public class AuthenticationException extends RuntimeException {
    public AuthenticationException() {
        super("Error: Invalid username or password.");
    }

    public AuthenticationException(String username) {
        super(String.format("Error: Authentication failed for %s. Please check your credentials or status.", username));
    }
}