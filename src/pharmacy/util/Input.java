package util;

import java.time.LocalDate;
import java.util.Scanner;
import ui.Display;

public class Input {
    private Input() {} // Prevents Instantiation
    
    private Input INSTANCE = new Input();
    private final Scanner sc = new Scanner(System.in);

    public Input getInstance() {
        return this.INSTANCE;
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
            Display.print("Input Username: ");
            username = sc.nextLine().trim();

            // Validator here
            isValid = true;
        } while (!isValid);

        return username;
    }

    public String readPassword() {
        boolean isValid = false;
        String password = null;

        do {
            Display.print("Input Password: ");
            password = sc.nextLine().trim();

            // Validator here
            isValid = true;
        } while (!isValid);

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