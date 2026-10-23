package util;

import exception.AccessDeniedException;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import model.Admin;
import model.Medicine;
import model.PurchaseRequest;
import model.Role;
import model.Sale;
import model.SaleItem;
import model.Staff;
import model.User;
import strategy.AccessStrategy;
import strategy.Action;

public class Validate {

    private Validate() {} // Prevents Instantiation

    public static void authorization(User user, Action action) {
        if (action == null) {
            throw new IllegalArgumentException("Action cannot be null.");
        }

        if (user == null || !user.accessStrategy().isAllowed(action)) {
            String roleName = (user == null || user.getRole() == null)
                    ? "Anonymous"
                    : user.getRole().getLabel();
            throw new AccessDeniedException(roleName, action.getLabel());
        }
    }
        //MEDICINE VALIDATION
    public static void validate(Medicine medicine) {
        if (medicine == null) {
            throw new IllegalArgumentException("Medicine cannot be null.");
        }

        requireText(medicine.getMedicineId(), "Medicine ID");
        requireText(medicine.getBatchNumber(), "Batch number");
        requireText(medicine.getName(), "Medicine name");
        requireText(medicine.getCategory(), "Medicine category");

        if (!Double.isFinite(medicine.getUnitPrice())
                || medicine.getUnitPrice() < 0) {
            throw new IllegalArgumentException(
                "Unit price must be a finite, non-negative number."
            );
        }

        if (medicine.getQuantity() < 0) {
            throw new IllegalArgumentException(
                "Quantity cannot be negative."
            );
        }

        if (medicine.getMinStockLevel() < 0) {
            throw new IllegalArgumentException(
                "Minimum stock level cannot be negative."
            );
        }

        LocalDate expiry = medicine.getExpirationDate();

        if (expiry == null) {
            throw new IllegalArgumentException(
                "Expiration date is required."
            );
        }
    }
    public static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                field + " cannot be empty."
            );
        }

        return value;
    }

        //PURCHASE REQUEST VALIDATION
    public static void validate(PurchaseRequest request) {
        if (request == null) {
            throw new IllegalArgumentException(
                "Purchase request cannot be null."
            );
        }

        if (request.getMedicineId() == null
                || request.getMedicineId().isBlank()) {
            throw new IllegalArgumentException(
                "Medicine ID is required."
            );
        }

        if (request.getQuantity() <= 0) {
            throw new IllegalArgumentException(
                "Requested quantity must be greater than zero."
            );
        }
    }
        //SALE ITEM VALIDATION
    public static void validate(SaleItem item) {
        if (item == null) {
            throw new IllegalArgumentException(
                "Sale item cannot be null."
            );
        }

        if (item.getMedicineId() == null
                || item.getMedicineId().isBlank()) {
            throw new IllegalArgumentException(
                "Medicine ID is required."
            );
        }

        if (item.getMedicineName() == null
                || item.getMedicineName().isBlank()) {
            throw new IllegalArgumentException(
                "Medicine name is required."
            );
        }

        if (!Double.isFinite(item.getUnitPrice())
                || item.getUnitPrice() < 0) {
            throw new IllegalArgumentException(
                "Unit price must be a finite, non-negative number."
            );
        }

        if (item.getQuantity() <= 0) {
            throw new IllegalArgumentException(
                "Sale quantity must be greater than zero."
            );
        }

        if (!Double.isFinite(item.getSubtotal())) {
            throw new IllegalArgumentException(
                "Sale subtotal must be finite."
            );
        }

    }

    // SALE VALIDATION
    public static void validate(Sale sale) {
        if (sale == null) {
            throw new IllegalArgumentException(
                "Sale cannot be null."
            );
        }

        requireText(sale.getSaleId(), "Sale ID");
        requireText(sale.getCashierId(), "Cashier ID");

        if (sale.getTimestamp() == null) {
            throw new IllegalArgumentException(
                "Sale timestamp is required."
            );
        }

        if (sale.getItems() == null) {
            throw new IllegalArgumentException(
                "Sale items cannot be null."
            );
        }

        for (SaleItem item : sale.getItems()) {
            validate(item);
        }

        if (!Double.isFinite(sale.getTotalAmount())
                || sale.getTotalAmount() < 0) {
            throw new IllegalArgumentException(
                "Sale total must be finite and non-negative."
            );
        }

        if (!Double.isFinite(sale.getStoredTotal())
                || sale.getStoredTotal() < 0) {
            throw new IllegalArgumentException(
                "Stored sale total must be finite and non-negative."
            );
        }
    }
    //USER VALIDATION
    public static void validate(User user) {
        if (user == null) {
            throw new IllegalArgumentException(
                "User cannot be null."
            );
        }

        requireText(user.getUserId(), "User ID");
        requireText(user.getUsername(), "Username");
        requireText(user.getFullName(), "Full name");
        requireText(user.getPassword(), "Password");

        if (user.getStatus() == null) {
            throw new IllegalArgumentException(
                "Account status cannot be null."
            );
        }

        if (user.getRole() == null) {
            throw new IllegalArgumentException(
                "User role cannot be null."
            );
        }
    }

    // ADMIN VALIDATION
    public static void validate(Admin admin) {
        if (admin == null) {
            throw new IllegalArgumentException(
                "Admin cannot be null."
            );
        }

        validate((User) admin);

        if (admin.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException(
                "User must have the administrator role."
            );
        }
    }
    //STAFF VALIDATION
    public static void validate(Staff staff) {
        if (staff == null) {
            throw new IllegalArgumentException(
                "Staff cannot be null."
            );
        }

        validate((User) staff);

        if (staff.getRole() != Role.STAFF) {
            throw new IllegalArgumentException(
                "User must have the staff role."
            );
        }
    }

    public static void validateUsers(ArrayList<User> users) {
        if (users == null) {
            throw new IllegalArgumentException(
                "User list cannot be null."
            );
        }

        for (User user : users) {
            if (user == null) {
                throw new IllegalArgumentException(
                    "User list cannot contain null entries."
                );
            }

            if (user.getUserId() == null
                    || user.getUsername() == null
                    || user.getFullName() == null
                    || user.getRole() == null
                    || user.getStatus() == null) {
                throw new IllegalArgumentException(
                    "User contains missing display data."
                );
            }
        }
    }

    public static void validateMedicines(
            ArrayList<Medicine> medicines) {
        if (medicines == null) {
            throw new IllegalArgumentException(
                "Medicine list cannot be null."
            );
        }

        for (Medicine medicine : medicines) {
            if (medicine == null) {
                throw new IllegalArgumentException(
                    "Medicine list cannot contain null entries."
                );
            }

            if (medicine.getMedicineId() == null
                    || medicine.getBatchNumber() == null
                    || medicine.getName() == null
                    || medicine.getCategory() == null
                    || medicine.getExpirationDate() == null) {
                throw new IllegalArgumentException(
                    "Medicine contains missing display data."
                );
            }
        }
    }
    //TABLE VALIDATION
    public static void validateSales(ArrayList<Sale> sales) {
        if (sales == null) {
            throw new IllegalArgumentException(
                "Sales list cannot be null."
            );
        }

        for (Sale sale : sales) {
            if (sale == null) {
                throw new IllegalArgumentException(
                    "Sales list cannot contain null entries."
                );
            }

            if (sale.getSaleId() == null
                    || sale.getTimestamp() == null
                    || sale.getCashierId() == null) {
                throw new IllegalArgumentException(
                    "Sale contains missing display data."
                );
            }
        }
    }
        //DISPLAY VALIDATION
    public static void validateText(String text) {
        if (text == null) {
            throw new IllegalArgumentException(
                "Display text cannot be null."
            );
        }
    }

    public static void validateTitle(String title) {
        validateText(title);

        if (title.isBlank()) {
            throw new IllegalArgumentException(
                "Title cannot be empty."
            );
        }
    }

    public static void validateFeedback(String feedback) {
        validateText(feedback);

        if (feedback.isBlank()) {
            throw new IllegalArgumentException(
                "Feedback cannot be empty."
            );
        }
    }
                        //INPUT VALIDATION
    public static int requirePositiveInt(int value, String fieldName) {
        if (value <= 0) {
            throw new IllegalArgumentException(
                fieldName + " must be greater than zero."
            );
        }

        return value;
    }

    public static double requireNonNegativeFinite(
            double value, String fieldName) {
        if (!Double.isFinite(value) || value < 0) {
            throw new IllegalArgumentException(
                fieldName + " must be a finite, non-negative number."
            );
        }

        return value;
    }

    public static String requireValidChoice(
            String choice, String[] allowedChoices) {
        requireText(choice, "Choice");

        if (allowedChoices == null || allowedChoices.length == 0) {
            throw new IllegalArgumentException(
                "Allowed choices must be provided."
            );
        }

        for (String allowed : allowedChoices) {
            if (allowed != null && allowed.equalsIgnoreCase(choice)) {
                return choice;
            }
        }

        throw new IllegalArgumentException(
            "Invalid choice: " + choice
        );
    }
    //PASSWORD VALIDATION
    public static void validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException(
                "Password cannot be null or empty."
            );
        }
    }

    //REPOSITORY VALIDATION
    public static void validateUserId(String userId) {
        requireText(userId, "User ID");

        if (userId.contains("|")) {
            throw new IllegalArgumentException(
                "User ID cannot contain '|'."
            );
        }
    }

    public static void validateUsername(String username) {
        validateField(username, "Username");
    }

    public static void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException(
                "User cannot be null."
            );
        }

        validateField(user.getUserId(), "User ID");
        validateField(user.getUsername(), "Username");
        validateField(user.getFullName(), "Full name");

        if (user.getRole() == null) {
            throw new IllegalArgumentException(
                "User role cannot be null."
            );
        }

        if (user.getStatus() == null) {
            throw new IllegalArgumentException(
                "User status cannot be null."
            );
        }

        validateField(user.getPassword(), "Password");
    }

    public static void validateRecord(String line) {
        requireText(line, "User record");

        String[] fields = line.split("\\|", -1);

        if (fields.length != 7) {
            throw new IllegalArgumentException(
                "A user record must contain exactly 7 fields."
            );
        }

        for (int i = 0; i < fields.length; i++) {
            if (fields[i].isBlank()) {
                throw new IllegalArgumentException(
                    "User record field " + (i + 1)
                    + " cannot be empty."
                );
            }
        }
    }

    public static void validateDifferentUsers(
            String existingUserId, User replacement) {
        validateUserId(existingUserId);
        validateUser(replacement);
    }

    private static void validateField(
            String value, String fieldName) {
        requireText(value, fieldName);

        if (value.contains("|")) {
            throw new IllegalArgumentException(
                fieldName + " cannot contain '|'."
            );
        }
    }

    //FILEIO VALIDATION
    public static void validateFilePath(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException(
                "File path cannot be empty."
            );
        }
    }

    public static void validateId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(
                "ID cannot be empty."
            );

        }

        if (id.contains("|")) {
            throw new IllegalArgumentException(
                "ID cannot contain '|'."
            );
        }
    }

    public static void validateLine(String line) {
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException(
                "Line cannot be empty."
            );
        }
    }

    public static void validateFieldIndex(int index) {
        if (index < 0) {
            throw new IllegalArgumentException(
                "Field index cannot be negative."
            );
        }
    }

    public static void validateWrite(
            String input, String filePath) {
        validateFilePath(filePath);
        validateLine(input);

        if (input.contains("\n") || input.contains("\r")) {
            throw new IllegalArgumentException(
                "Input must contain only one line."
            );
        }
    }

    public static void validateLines(
            ArrayList<String> lines, String filePath) {
        validateFilePath(filePath);

        if (lines == null) {
            throw new IllegalArgumentException(
                "Lines list cannot be null."
            );
        }

        for (String line : lines) {
            if (line == null) {
                throw new IllegalArgumentException(
                    "Lines list cannot contain null entries."
                );
            }

            if (line.contains("\n") || line.contains("\r")) {
                throw new IllegalArgumentException(
                    "Each entry must contain only one line."
                );
            }
        }
    }

    public static void validateUpdate(
            String oldLine, String newLine, String filePath) {
        validateFilePath(filePath);
        validateLine(oldLine);
        validateWrite(newLine, filePath);
    }

    public static void validateRetrieveLine(
            String value, int index, String filePath) {
        validateFilePath(filePath);
        validateId(value);
        validateFieldIndex(index);
    }

    public static void validateDelete(
            String line, String filePath) {
        validateFilePath(filePath);
        validateLine(line);
    }
//FILECREATOR VALIDATION
    public static void validateFile(File file) {
        if (file == null) {
            throw new IllegalArgumentException(
                "File cannot be null."
            );
        }

        if (file.getPath().isBlank()) {
            throw new IllegalArgumentException(
                "File path cannot be empty."
            );
        }

        File parent = file.getAbsoluteFile().getParentFile();

        if (parent == null) {
            throw new IllegalArgumentException(
                "File must have a valid parent directory."
            );
        }

        if (file.isDirectory()) {
            throw new IllegalArgumentException(
                "Expected a file, but received a directory."
            );
        }
    }
    //STRATEGRY VALIDATION
    public static void validateAction(Action action) {
        if (action == null) {
            throw new IllegalArgumentException(
                "Action cannot be null."
            );
        }
    }

    public static void validateAccessStrategy(
            AccessStrategy accessStrategy) {
        if (accessStrategy == null) {
            throw new IllegalArgumentException(
                "Access strategy cannot be null."
            );
        }
    }

    public static boolean validatePermission(
            AccessStrategy accessStrategy, Action action) {
        validateAccessStrategy(accessStrategy);
        validateAction(action);

        return accessStrategy.isAllowed(action);
    }
}

