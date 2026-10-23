package util;

import java.time.LocalDate;
import java.util.Scanner;
import ui.Display;

public class Input {    
    private static final Input INSTANCE = new Input();
    private final Scanner sc = new Scanner(System.in);

    private Input() {} // Prevents Instantiation

    public static Input getInstance() {
        return INSTANCE;
    }

    public int readChoice(int min, int max) {
        boolean isValid = false;
        int choice = min - 1;

        do {
            Display.print("Enter your choice: ");
            String raw = sc.nextLine().trim();

            try {
                choice = Integer.parseInt(raw);
                isValid = choice >= min && choice <= max;
                if (!isValid) {
                    Display.feedback("Invalid choice. Please enter a number from " + min + " to " + max + ".");
                }
            } catch (NumberFormatException e) {
                Display.feedback("Invalid input. Please enter a number.");
            }
        } while (!isValid);

        return choice;
    }

    public String readText(String prompt) {
        boolean isValid = false;
        String value = null;

        do { 
            Display.print(prompt);
            value = sc.nextLine().trim();
            if (value.isBlank() || value.isEmpty()){
                Display.feedback("This field cannot be empty.");
            }
        } while (!isValid);

        return value;
    }

    public String readId() {
        boolean isValid = false;
        String id = null;

        do {
            Display.print("Input ID: ");
            id = sc.nextLine().trim();

            // Validator here
            isValid = true;
        } while (!isValid);

        return id;
    }

    public String readUsername() {
        boolean isValid = false;
        String username = null;

        do {
            username = readText("Input Username: ");
            Validate.validateUsername(username);
            isValid = true;
        } while (!isValid);

        return username;
    }

    public String readPassword() {
        boolean isValid = false;
        String password = null;

        do {
            password = readText("Input Password: ");
            Validate.validatePassword(password);
            isValid = true;
        } while (!isValid);

        return password;
    }

    public String readPassword(String prompt) {
        boolean isValid = false;
        String password = null;
        do {
            password = readText(prompt);
            Validate.validatePassword(password);
            isValid = true;
        } while(!isValid);

        return password;
    }

    public String readFullName() {
        boolean isValid = false;
        String fullName = null;

        do {
            Display.print("Input Full Name: ");
            fullName = sc.nextLine().trim();

            // Validator here
            isValid = true;
        } while (!isValid);

        return fullName;
    }

    public String readMedicineName() {
        boolean isValid = false;
        String medicineName = null;

        do {
            Display.print("Input Medicine Name: ");
            medicineName = sc.nextLine().trim();

            // Validator here
            isValid = true;
        } while (!isValid);

        return medicineName;
    }

    public LocalDate readBatchNumber() {
        boolean isValid = false;
        LocalDate batchNumber = null;

        do {
            Display.print("Input Batch Number: ");
            String rawInput = sc.nextLine().trim();

            // Validator here
            batchNumber = LocalDate.parse(rawInput);
            isValid = true;
        } while (!isValid);

        return batchNumber;
    }

    public String readCategory() {
        boolean isValid = false;
        String category = null;

        do {
            Display.print("Input Category: ");
            category = sc.nextLine().trim();

            // Validator here
            isValid = true;
        } while (!isValid);

        return category;
    }

    public double readUnitPrice() {
        boolean isValid = false;
        double unitPrice = -1;

        do {
            Display.print("Input Unit Price: ");
            String rawInput = sc.nextLine().trim();

            // Validator Here
            unitPrice = Double.parseDouble(rawInput);
            isValid = true;
        } while (!isValid);

        return unitPrice;
    }

     public int readQuantity() {
        boolean isValid = false;
        int quantity = -1;

        do {
            Display.print("Input Initial Quantity: ");
            String rawInput = sc.nextLine().trim();

            // Validator here
            quantity = Integer.parseInt(rawInput);
            isValid = true;
        } while (!isValid);

        return quantity;
     }

     public int readLowStockLevel() {
        boolean isValid = false;
        int lowStockLevel = -1;

        do {
            Display.print("Input Minimum Stock for Medicine: ");
            String rawInput = sc.nextLine().trim();

            // Validator here
            lowStockLevel = Integer.parseInt(rawInput);
            isValid = true;
        } while (!isValid);

        return lowStockLevel;
     }

     public LocalDate readExpirationDate() {
        boolean isValid = false;
        LocalDate expirationDate = null;

        do {
            Display.print("Input Expiration Date of Medicine: ");
            String rawInput = sc.nextLine().trim();

            // Validator here
            expirationDate = LocalDate.parse(rawInput);
            isValid = true;
        } while (!isValid);

        return expirationDate;
     }
}   