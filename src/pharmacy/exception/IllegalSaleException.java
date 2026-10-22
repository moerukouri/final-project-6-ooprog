package exception;

public class IllegalSaleException extends RuntimeException {
    public IllegalSaleException() {
        super("Error: Sale action encountered a problem.");
    }

    public IllegalSaleException(String record, String line) {
        super(String.format("Error: Unknown record type %s at %s", record, line));
    }

    public IllegalSaleException(String message) {
        super(message);
    }
}