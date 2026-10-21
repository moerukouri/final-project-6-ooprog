package exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException() {
        super("Error: Insufficient stock detected for requested medicine.");
    }

    public InsufficientStockException(String medicineName, int requested, int available) {
        super(String.format("Error: Insufficient stock detected for %s. Requested: %d, Available: %d.", medicineName, requested, available));
    }
}