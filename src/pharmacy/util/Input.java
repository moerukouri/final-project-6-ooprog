package util;

import java.time.LocalDate;
import java.util.Scanner;

import exception.InvalidInputException;
import ui.Display;

public class Input {
    private Input() {} // Prevents Instantiation
    
    private static Input INSTANCE = new Input();
    private final Scanner sc = new Scanner(System.in);

    public static Input getInstance() {
        return INSTANCE;
    }

    public int readChoice(int min, int max) {
        boolean isValid = false;
        int choice = -1;

        do {
            try {
                Display.print("Input Choice: ");
                String rawInput = sc.nextLine().trim();

                Validate.choice(rawInput, min, max);
                choice = Integer.parseInt(rawInput);

                isValid = true;
            } catch (InvalidInputException e) {
                Display.feedback(e.getMessage());
            }
        } while (!isValid);

        return choice;
    }

    public String readUserId() {
        boolean isValid = false;
        String id = null;

        do {
            try {
                Display.print("Input ID: ");
                id = sc.nextLine().trim();

                Validate.userId(id);
                isValid = true;
            } catch (InvalidInputException e) {
               Display.feedback(e.getMessage());
            }
        } while (!isValid);

        return id;
    }

    public String readUsername() {
        boolean isValid = false;
        String username = null;

        do {
            try {
                Display.print("Input Username: ");
                username = sc.nextLine().trim();

                Validate.isValidString(username, "Username cannot be empty or null.");
                isValid = true;
            } catch (InvalidInputException e) {
                Display.feedback(e.getMessage());
            }
        } while (!isValid);

        return username;
    }

    public String readPassword() {
        boolean isValid = false;
        String password = null;

        do {
            try {
                 Display.print("Input Password: ");
                password = sc.nextLine().trim();

                Validate.password(password);
                isValid = true;
            } catch (InvalidInputException e) {
                Display.feedback(e.getMessage());
            }
        } while (!isValid);

        return password;
    }

    public String readFullName() {
        boolean isValid = false;
        String fullName = null;

        do {
            try {
                 Display.print("Input Full Name: ");
                fullName = sc.nextLine().trim();

                Validate.isValidString(fullName, "Full Name cannot be empty or null.");
                isValid = true;
            } catch (InvalidInputException e) {
                Display.feedback(e.getMessage());
            }
        } while (!isValid);

        return fullName;
    }

    public String readMedicineName() {
        boolean isValid = false;
        String medicineName = null;

        do {
            try {
                Display.print("Input Medicine Name: ");
                medicineName = sc.nextLine().trim();

                Validate.isValidString(medicineName, "Medicine Name cannot be empty or null.");
                isValid = true;
            } catch (InvalidInputException e) {
                Display.feedback(e.getMessage());
            }
        } while (!isValid);

        return medicineName;
    }

    public LocalDate readBatchNumber() {
        boolean isValid = false;
        LocalDate batchNumber = null;

        do {
            try {
                Display.print("Input Batch Number: ");
                String rawInput = sc.nextLine().trim();
            
                Validate.date(rawInput);
                batchNumber = LocalDate.parse(rawInput);
                
                isValid = true;
            } catch (InvalidInputException e) {
                Display.feedback(e.getMessage());
            }
        } while (!isValid);

        return batchNumber;
    }

    public String readCategory() {
        boolean isValid = false;
        String category = null;

        do {
            try {
                Display.print("Input Category: ");
                category = sc.nextLine().trim();

                Validate.isValidString(category, "Category cannot be empty or null.");
                isValid = true;
            } catch (InvalidInputException e) {
                Display.feedback(e.getMessage());
            }
        } while (!isValid);

        return category;
    }

    public double readUnitPrice() {
        boolean isValid = false;
        double unitPrice = -1;

        do {
            try {
                Display.print("Input Unit Price: ");
                String rawInput = sc.nextLine().trim();

                Validate.isValidDouble(rawInput, "Unit Price");
                unitPrice = Double.parseDouble(rawInput);
                
                isValid = true;
            } catch (InvalidInputException e) {
                Display.feedback(e.getMessage());
            }
        } while (!isValid);

        return unitPrice;
    }

     public int readQuantity() {
        boolean isValid = false;
        int quantity = -1;

        do {
            try {
               Display.print("Input Initial Quantity: ");
                String rawInput = sc.nextLine().trim();

                Validate.isValidInteger(rawInput, "Quantity");
                quantity = Integer.parseInt(rawInput);
                
                isValid = true;
            } catch (InvalidInputException e) {
                Display.feedback(e.getMessage());
            }
        } while (!isValid);

        return quantity;
     }

     public int readMinimumStockLevel() {
        boolean isValid = false;
        int minStockLevel = -1;

        do {
            try {
               Display.print("Input Minimum Stock for Medicine: ");
                String rawInput = sc.nextLine().trim();

                Validate.isValidInteger(rawInput, "Minimum Stock Level");
                minStockLevel = Integer.parseInt(rawInput);
                isValid = true;
            } catch (InvalidInputException e) {
                Display.feedback(e.getMessage());
            }
        } while (!isValid);

        return minStockLevel;
     }

     public LocalDate readExpirationDate() {
        boolean isValid = false;
        LocalDate expirationDate = null;

        do {
            try {
                Display.print("Input Expiration Date of Medicine: ");
                String rawInput = sc.nextLine().trim();
            
                Validate.date(rawInput);
                expirationDate = LocalDate.parse(rawInput);
                
                isValid = true;
            } catch (InvalidInputException e) {
                Display.feedback(e.getMessage());
            }
        } while (!isValid);

        return expirationDate;
     }
}   