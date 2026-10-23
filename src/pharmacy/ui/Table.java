package ui;

import java.util.List;
import model.Medicine;
import model.Sale;
import model.User;

public class Table {
    private Table() {} // Prevents Instantiation
    
    public static void users(List<User> users) {
        String[] fields = TableField.USERS.getFields();
        int[] widths = calculateUserColumnWidths(users, fields);
        
        printTableHeader(fields, widths, 16);
        printUsersData(users, widths);
        printTableFooter(widths, 16);
    }

    public static void medicines(List<Medicine> medicines) {
        String[] fields = TableField.MEDICINE.getFields();
        int[] widths = calculateMedicineColumnWidths(medicines, fields);
        
        printTableHeader(fields, widths, 22);
        printMedicinesData(medicines, widths);
        printTableFooter(widths, 22);
    }

    public static void sales(List<Sale> sales) {
        String[] fields = TableField.SALES.getFields();
        int[] widths = calculateSaleColumnWidths(sales, fields);
        
        printTableHeader(fields, widths, 13);
        printSalesData(sales, widths);
        printTableFooter(widths, 13);
    }
    
    private static int[] calculateUserColumnWidths(List<User> users, String[] fields) {
        int idLength = fields[0].length();
        int usernameLength = fields[1].length();
        int fullNameLength = fields[2].length(); 
        int roleLength = fields[3].length();
        int statusLength = fields[4].length();

        for (User user : users) {
            idLength = Math.max(idLength, user.getUserId().length());
            usernameLength = Math.max(usernameLength, user.getUsername().length());
            fullNameLength = Math.max(fullNameLength, user.getFullName().length());
            roleLength = Math.max(roleLength, user.getRole().getLabel().length());
            statusLength = Math.max(statusLength, user.getStatus().getLabel().length());
        }

        return new int[]{idLength, usernameLength, fullNameLength, roleLength, statusLength};
    }
    
    private static int[] calculateMedicineColumnWidths(List<Medicine> medicines, String[] fields) {
        int idLength = fields[0].length();
        int batchNumberLength = fields[1].length();
        int nameLength = fields[2].length();
        int categoryLength = fields[3].length();
        int unitPriceLength = fields[4].length();
        int stockLength = fields[5].length();
        int expirationLength = fields[6].length();

        for (Medicine medicine : medicines) {
            idLength = Math.max(idLength, medicine.getMedicineId().length());
            batchNumberLength = Math.max(batchNumberLength, medicine.getBatchNumber().length());
            nameLength = Math.max(nameLength, medicine.getName().length());
            categoryLength = Math.max(categoryLength, medicine.getCategory().length());
            unitPriceLength = Math.max(unitPriceLength, String.valueOf(medicine.getUnitPrice()).length());
            stockLength = Math.max(stockLength, String.valueOf(medicine.getQuantity()).length());
            expirationLength = Math.max(expirationLength, String.valueOf(medicine.getExpirationDate()).length());
        }

        return new int[]{idLength, batchNumberLength, nameLength, categoryLength, unitPriceLength, stockLength, expirationLength};
    }
    
    private static int[] calculateSaleColumnWidths(List<Sale> sales, String[] fields) {
        int idLength = fields[0].length();
        int timestampLength = fields[1].length();
        int cashierIDLength = fields[2].length();
        int totalAmountLength = fields[3].length();

        for (Sale sale : sales) {
            idLength = Math.max(idLength, sale.getSaleId().length());
            timestampLength = Math.max(timestampLength, String.valueOf(sale.getTimestamp()).length());
            cashierIDLength = Math.max(cashierIDLength, sale.getCashierId().length());
            totalAmountLength = Math.max(totalAmountLength, String.valueOf(sale.getTotalAmount()).length());
        }

        return new int[]{idLength, timestampLength, cashierIDLength, totalAmountLength};
    }
    
    private static int calculateTotalWidth(int[] widths) {
        int total = 0;
        
        for (int width : widths) {
            total += width;
        }
        
        return total;
    }

    private static void printUsersData(List<User> users, int[] widths) {
        for (User user : users) {
            StringBuilder row = new StringBuilder("|");
           
            row.append(String.format(" %-" + widths[0] + "s |", user.getUserId()));
            row.append(String.format(" %-" + widths[1] + "s |", user.getUsername()));
            row.append(String.format(" %-" + widths[2] + "s |", user.getFullName()));
            row.append(String.format(" %-" + widths[3] + "s |", user.getRole().getLabel()));
            row.append(String.format(" %-" + widths[4] + "s |", user.getStatus().getLabel()));
            
            Display.println(row.toString());
        }
    }
    
    private static void printMedicinesData(List<Medicine> medicines, int[] widths) {
        for (Medicine medicine : medicines) {
            StringBuilder row = new StringBuilder("|");
            
            row.append(String.format(" %-" + widths[0] + "s |", medicine.getMedicineId()));
            row.append(String.format(" %-" + widths[1] + "s |", medicine.getBatchNumber()));
            row.append(String.format(" %-" + widths[2] + "s |", medicine.getName()));
            row.append(String.format(" %-" + widths[3] + "s |", medicine.getCategory()));
            row.append(String.format(" %-" + widths[4] + "s |", String.valueOf(medicine.getUnitPrice())));
            row.append(String.format(" %-" + widths[5] + "s |", String.valueOf(medicine.getQuantity())));
            row.append(String.format(" %-" + widths[6] + "s |", String.valueOf(medicine.getExpirationDate())));
            
            Display.println(row.toString());
        }
    }
    
    private static void printSalesData(List<Sale> sales, int[] widths) {
        for (Sale sale : sales) {
            StringBuilder row = new StringBuilder("|");
            
            row.append(String.format(" %-" + widths[0] + "s |", sale.getSaleId()));
            row.append(String.format(" %-" + widths[1] + "s |", String.valueOf(sale.getTimestamp())));
            row.append(String.format(" %-" + widths[2] + "s |", sale.getCashierId()));
            row.append(String.format(" %-" + widths[3] + "s |", String.valueOf(sale.getTotalAmount())));
            
            Display.println(row.toString());
        }
    }
    
    private static void printTableHeader(String[] fields, int[] widths, int spaces) {
        int totalWidth = calculateTotalWidth(widths);
        Display.println("=".repeat(totalWidth + spaces));
        
        StringBuilder header = new StringBuilder("|");
        for (int i = 0; i < fields.length; i++) {
            header.append(String.format(" %-" + widths[i] + "s |", fields[i]));
        }

        Display.println(header.toString());
        Display.println("=".repeat(totalWidth + spaces));
    }

    private static void printTableFooter(int[] widths, int spaces) {
        int totalWidth = calculateTotalWidth(widths);
        Display.println("=".repeat(totalWidth + spaces));
    }
}