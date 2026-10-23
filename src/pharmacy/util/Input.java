package util;

import exception.InvalidInputException;
import java.time.LocalDate;
import java.util.Scanner;
import ui.Display;

public class Input {
    private Input() {} // Prevents Instantiation
    
    private static final Input INSTANCE = new Input();
    private final Scanner sc = new Scanner(System.in);

    public static Input getInstance() {
        return INSTANCE;
    }

    public String readText(String prompt) {
        boolean isValid = false;
        String text = null;

        do {
            try {
                Display.print(prompt);
                text = sc.nextLine().trim();

                Validate.isValidString(text, "Text cannot be empty or null.");
                isValid = true;
            } catch (InvalidInputException e) {
               Display.feedback(e.getMessage());
            }
        } while (!isValid);

        return text;
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

    public boolean readYesNo(String prompt) {
        boolean isValid = false;
        boolean output = false;
        
        while (!isValid) {
            Display.print(prompt);
            String answer = sc.nextLine().trim();

            if (answer.equalsIgnoreCase("y")) {
                output = true;
                isValid = true;
            }

            if (answer.equalsIgnoreCase("n")) {
                output = false;
                isValid = true;
            }
            else {
                Display.feedback("Please enter only y or n.");
            }
        }

        return output;
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

    public String readPassword(String prompt) {
        boolean isValid = false;
        String password = null;

        do {
            try {
                 Display.print(prompt);
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

    public String readBatchNumber() {
        boolean isValid = false;
        String batchNumber = null;

        do {
            try {
                Display.print("Input Batch Number: ");
                batchNumber = sc.nextLine().trim();

                Validate.isValidString(batchNumber, "Batch Number cannot be empty or null.");
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
        return readQuantity("Input Initial Quantity: ");
     }

     public int readQuantity(String prompt) {
        boolean isValid = false;
        int quantity = -1;

        do {
            try {
               Display.print(prompt);
                String rawInput = sc.nextLine().trim();

                Validate.isValidNonNegativeInteger(rawInput, "Quantity");
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

                Validate.isValidNonNegativeInteger(rawInput, "Minimum Stock Level");
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
            
                Validate.notPastDate(rawInput);
                expirationDate = LocalDate.parse(rawInput);
                
                isValid = true;
            } catch (InvalidInputException e) {
                Display.feedback(e.getMessage());
            }
        } while (!isValid);

        return expirationDate;
     }
}