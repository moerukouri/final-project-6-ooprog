package exception;

import java.time.LocalDate;

public class ExpiredMedicineException extends RuntimeException {
    public ExpiredMedicineException() {
        super("Error: Cannot dispense or sell expired medicine.");
    }

    public ExpiredMedicineException(String medicineName, LocalDate expiryDate) {
        super(String.format("Error: Cannot dispense or sell %s. Expired on: %s.", medicineName, expiryDate));
    }
}