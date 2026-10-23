package util;

import exception.AccessDeniedException;
import exception.InvalidInputException;
import exception.PharmacyException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import model.User;
import strategy.Action;

public class Validate {
    private Validate() {} // Prevents Instantiation

    private static final String INTEGER_REGEX = "^[1-9][0-9]*$";
    private static final String NON_NEGATIVE_INTEGER_REGEX = "^(0|[1-9][0-9]*)$";
    private static final String DOUBLE_REGEX = "(?:0|[1-9][0-9]*)(?:\\.[0-9]{1,2})?";
    private static final String PASSWORD_REGEX = "^[a-zA-Z0-9]{8,}$";

    private static final String DATE_FORMAT = "uuuu-MM-dd";
    private static final String USER_ID_FORMAT = "^U[0-9]{4}$";


    public static void authorization(User user, Action action) {
        isObjectNull(action, "Action");
        isObjectNull(user, "User");

        if (!user.accessStrategy().isAllowed(action)) {
            throw new AccessDeniedException(user.getRole().getLabel(), action.getLabel());
        }
    }

    public static void choice(String rawInput, int min, int max) {
        int choice = isValidNonNegativeInteger(rawInput, "Choice");

        if (!(choice >= min) || !(choice <= max)) {
            throw new InvalidInputException("The number is not within the range of choices.");
        }
    }

    public static void userId(String id) {
        isValidString(id, "User ID is empty.");

        if (!id.matches(USER_ID_FORMAT)) {
            throw new InvalidInputException("User ID has invalid format.");
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
        isValidString(integer, numberName + " is empty.");

        if (!integer.matches(INTEGER_REGEX)) {
            throw new InvalidInputException(numberName + " is not properly formatted or a negative integer.");
        }

        try {
            return Integer.parseInt(integer);
        } catch (NumberFormatException e) {
            throw new InvalidInputException(numberName + " is not an integer.");
        }
    }

    public static int isValidNonNegativeInteger(String integer, String numberName) {
        isValidString(integer, numberName + " is empty.");

        if (!integer.matches(NON_NEGATIVE_INTEGER_REGEX)) {
            throw new InvalidInputException(numberName + " is not properly formatted or is a negative integer.");
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

    public static String requireText(String value, String field) {
        isValidString(value, field + " cannot be empty.");
        return value;
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
}