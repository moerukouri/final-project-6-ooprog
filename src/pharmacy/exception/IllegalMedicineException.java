package exception;

public class IllegalMedicineException extends RuntimeException {
    public IllegalMedicineException() {
        super("Error: Medicine action encountered a problem.");
    }

    public IllegalMedicineException(String id) {
        super("Error: Medicine ID was not found.");
    }
}