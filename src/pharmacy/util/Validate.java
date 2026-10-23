package util;

import exception.AccessDeniedException;
import exception.InvalidInputException;
import exception.PharmacyException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import model.Admin;
import model.Medicine;
import model.PurchaseRequest;
import model.Role;
import model.Sale;
import model.SaleItem;
import model.Staff;
import model.User;
import strategy.Action;

public class Validate {
    private Validate() {} // Prevents Instantiation

    private static final String INTEGER_REGEX = "^[1-9][0-9]*$";
    private static final String DOUBLE_REGEX = "(?:0|[1-9][0-9]*)(?:\\.[0-9]{1,2})?";
    private static final String PASSWORD_REGEX = "^[a-zA-Z0-9]{8,}$";
    
    private static final String DATE_FORMAT = "uuuu-MM-dd";
    private static final String USER_ID_FORMAT = "^U[0-9]{4}$";
    private static final String MEDICINE_ID_FORMAT = "^M[0-9]{4}$";
    private static final String SALE_ID_FORMAT = "^S[0-9]{4}$";


    public static void authorization(User user, Action action) {
        isObjectNull(action, "Action");
        isObjectNull(user, "User");

        if (!user.accessStrategy().isAllowed(action)) {
            throw new AccessDeniedException(user.getRole().getLabel(), action.getLabel());
        }
    }

    public static void choice(String rawInput, int min, int max) {
        int choice = isValidInteger(rawInput, "Choice");

        if (!(choice >= min) && !(choice <= max)) {
            throw new InvalidInputException("The number is not within the range of choices.");
        }
    }

    public static void userId(String id) {
        isValidString(id, "User ID is empty.");

        if (!id.matches(USER_ID_FORMAT)) {
            throw new InvalidInputException("User ID has invalid format.");
        }
    }

    public static void medicineId(String id) {
        isValidString(id, "Medicine ID is empty.");

        if (!id.matches(MEDICINE_ID_FORMAT)) {
            throw new InvalidInputException("Medicine ID has invalid format.");
        }
    }

    public static void saleId(String id) {
        isValidString(id, "Sale ID is empty.");

        if (!id.matches(SALE_ID_FORMAT)) {
            throw new InvalidInputException("Sale ID has invalid format.");
        }
    }

    public static void password(String password) {
        isValidString(password, "Password is empty.");

        if (password.length() < 8) {
            throw new InvalidInputException("Password should be 8+ characters.");
        }

        if (!password.matches(PASSWORD_REGEX)) {
            throw new InvalidInputException("Password should be alphanumeric.");
        }
    }

    public static void date(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT).withResolverStyle(ResolverStyle.STRICT);
        
        try {
            LocalDate.parse(date, formatter);
        } catch (DateTimeParseException e) {
            throw new InvalidInputException("Date is not formatted like YYYY-MM-DD.");
        }
    }

    public static int isValidInteger(String integer, String numberName) {
        isValidString(integer, numberName + "is empty.");

        if (!integer.matches(INTEGER_REGEX)) {
            throw new InvalidInputException(numberName + "is not properly formatted or a negative integer.");
        }

        try {
            return Integer.parseInt(integer);
        } catch (NumberFormatException e) {
            throw new InvalidInputException(numberName + " is not an integer.");
        }
    }

    public static double isValidDouble(String decimal, String numberName) {
        isValidString(decimal, "Decimal Number cannot be empty.");
        isValidString(numberName, "Number Name cannot be empty.");

        if (!decimal.matches(DOUBLE_REGEX)) {
            throw new InvalidInputException(numberName + " is not properly formatted or a negative decimal number.");
        }


        try {
            double number = Double.parseDouble(decimal);

            if (!Double.isFinite(number)) {
                throw new InvalidInputException(numberName + " is not finite.");
            }

            return number;
        } catch (NumberFormatException e) {
            throw new InvalidInputException(numberName + " is not a decimal number.");
        }
    } 

    public static void isValidString(String string, String errorMessage) {
        string = string.trim().strip();

        if (string.isBlank() || string.isEmpty()) {
            throw new InvalidInputException(errorMessage);
        }
    }

    public static void isObjectNull(Object object, String objectName) {
        if (object == null) {
            throw new PharmacyException(objectName + " cannot be null.");
        }
    }
    
    public static void medicineObject(Medicine medicine) {
        isObjectNull(medicine, "Medicine");

        requireText(medicine.getMedicineId(), "Medicine ID");
        requireText(medicine.getBatchNumber(), "Batch number");
        requireText(medicine.getName(), "Medicine name");
        requireText(medicine.getCategory(), "Medicine category");

        isValidDouble(String.valueOf(medicine.getUnitPrice()), "Unit Price");
        isValidInteger(String.valueOf(medicine.getQuantity()), "Quantity");
        isValidInteger(String.valueOf(medicine.getMinStockLevel()), "Minimum Stock Level");

        isObjectNull(medicine.getExpirationDate(), "Expiration Date");
    }

    public static String requireText(String value, String field) {
        isValidString(value, field + "cannot be empty.");
        return value;
    }

    public static void purchaseRequest(PurchaseRequest request) {
        isObjectNull(request, "Purchase Request");
        medicineId(request.getMedicineId());
        isValidInteger(String.valueOf(request.getQuantity()), "Quantity");
    }

    public static void saleItem(SaleItem item) {
        isObjectNull(item, "Sale Item");
        medicineId(item.getMedicineId());
        isValidString(item.getMedicineName(), "Medicine Name is empty.");

        isValidDouble(String.valueOf(item.getUnitPrice()), "Unit Price");
        isValidInteger(String.valueOf(item.getQuantity()), "Sale Quantity");
        isValidDouble(String.valueOf(item.getSubtotal()), "Sale Subtotal");
    }

    public static void saleObject(Sale sale) {
        isObjectNull(sale, "Sale");

        requireText(sale.getSaleId(), "Sale ID");
        requireText(sale.getCashierId(), "Cashier ID");

        isObjectNull(sale.getTimestamp(), "Sale timestamp is empty.");
        isObjectNull(sale.getItems(), "Sale items cannot be null");

        for (SaleItem item : sale.getItems()) {
            saleItem(item);
        }

        isValidDouble(String.valueOf(sale.getTotalAmount()), "Sale Total");
        isValidDouble(String.valueOf(sale.getStoredTotal()), "Stored Sale Total");
    }

    public static void userObject(User user) {
        isObjectNull(user, "User");

        requireText(user.getUserId(), "User ID");
        requireText(user.getUsername(), "Username");
        requireText(user.getFullName(), "Full name");
        requireText(user.getPassword(), "Password");

        isObjectNull(user.getStatus(), "Account Status");
        isObjectNull(user.getRole(), "User Role");
    }

    public static void adminObject(Admin admin) {
        isObjectNull(admin, "Admin");
        userObject((User) admin);

        if (admin.getRole() != Role.ADMIN) {
            throw new PharmacyException("Admin role does not match with the object.");
        }
    }

    public static void staffObject(Staff staff) {
        isObjectNull(staff, "Staff");
        userObject((User) staff);

        if (staff.getRole() != Role.STAFF) {
            throw new PharmacyException("Staff role does not match with the object.");
        }
    }

    public static void usersList(List<User> users) {
        isObjectNull(users, "Users List");

        for (User user : users) {
            userObject(user);
        }
    }

    public static void medicinesList(List<Medicine> medicines) {
        isObjectNull(medicines, "Medicines List");

        for (Medicine medicine : medicines) {
            medicineObject(medicine);
        }
    }

    public static void salesList(List<Sale> sales) {
        isObjectNull(sales, "Sales List");

        for (Sale sale : sales) {
            saleObject(sale);
        }
    }

    public static void validateTitle(String title) {
        isValidString(title, "Title is empty.");
    }

    public static void validateFeedback(String feedback) {
        isValidString(feedback, "Feedback is empty.");
    }
    
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

    public static int requireNonNegativeInt(int value, String fieldName) {
        if (value < 0) {
            throw new IllegalArgumentException(
                fieldName + " cannot be negative."
            );
        }
 
        return value;
    }

    public static <T> T requireNonNull(T value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(
                fieldName + " is required."
            );
        }
 
        return value;
    }

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
        isObjectNull(user, "User");

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

    private static void validateField(String value, String fieldName) {
        requireText(value, fieldName);

        if (value.contains("|")) {
            throw new IllegalArgumentException(
                fieldName + " cannot contain '|'."
            );
        }
    }
}