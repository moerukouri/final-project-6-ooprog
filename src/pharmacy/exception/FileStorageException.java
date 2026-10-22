package exception;

public class FileStorageException extends RuntimeException {
    public FileStorageException() {
        super("Error: A problem occured while performing data storage actions.");
    }

    public FileStorageException(String operation, String filePath) {
        super(String.format("Error: Failed to %s file at %s.", operation, filePath));
    }

    public FileStorageException(String lineForFields) {
        super("Error: Invalid fields detected in " + lineForFields);
    }

    public FileStorageException(String[] fields) {
        super(String.format("Error: Invalid number of fields (%d).", fields.length));
    }
}