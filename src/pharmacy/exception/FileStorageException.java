package exception;

public class FileStorageException extends RuntimeException {
    public FileStorageException() {
        super("Error: An error occured while performing data storage actions.");
    }

    public FileStorageException(String operation, String filePath) {
        super(String.format("Error: Failed to %s file at %s.", operation, filePath));
    }
}