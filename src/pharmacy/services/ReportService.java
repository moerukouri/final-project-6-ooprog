package services;

import exception.InvalidInputException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import model.Medicine;
import model.Sale;
import model.User;
import repository.MedicineRepository;
import repository.SaleRepository;
import strategy.Action;
import ui.Display;
import ui.Table;
import util.Input;
import util.Validate;

public class ReportService {
    private final MedicineRepository medicines;
    private final SaleRepository sales;
    private final Input input;

    public ReportService(MedicineRepository medicines, SaleRepository sales) {
        this.medicines = medicines;
        this.sales = sales;
        this.input = Input.getInstance();
    }

    public void menu(User user) {
        Validate.authorization(user, Action.GENERATE_REPORTS);

        boolean running = true;
        while (running) {
            Display.title("SALES AND INVENTORY REPORTS");
            Display.println("1. Daily Sales Summary");
            Display.println("2. Sales for a Period");
            Display.println("3. Inventory Report");
            Display.println("4. Low Stock and Expired Medicines");
            Display.println("5. Transaction History");
            Display.println("0. Back");

            int choice = input.readChoice(0, 5);

            switch (choice) {
                case 1 -> dailySalesSummary();
                case 2 -> salesForPeriod();
                case 3 -> inventoryReport();
                case 4 -> stockAlerts();
                case 5 -> transactionHistory();
                case 0 -> running = false;
            }
        }
    }

    private void dailySalesSummary() {
        Display.title("DAILY SALES SUMMARY");
        LocalDate date = readDate("Input Date (YYYY-MM-DD): ");
        List<Sale> daySales = sales.findByDate(date);

        if (daySales.isEmpty()) {
            Display.feedback("No sales recorded on " + date + ".");
            return;
        }

        Table.sales(daySales);
        Display.feedback("Summary for " + date + ":\nTotal sales: " + daySales.size()
                + "\nTotal amount: " + money(sumTotal(daySales)));
    }

    private void salesForPeriod() {
        Display.title("SALES FOR A PERIOD");
        LocalDate start = readDate("Input Start Date (YYYY-MM-DD): ");
        LocalDate end = readDate("Input End Date (YYYY-MM-DD): ");

        if (start.isAfter(end)) {
            Display.feedback("Start date cannot be after end date.");
            return;
        }

        List<Sale> periodSales = sales.findByDateRange(start, end);

        if (periodSales.isEmpty()) {
            Display.feedback("No sales recorded from " + start + " to " + end + ".");
            return;
        }

        Table.sales(periodSales);
        Display.feedback("Summary from " + start + " to " + end + ":\nTotal sales: " + periodSales.size()
                + "\nTotal amount: " + money(sumTotal(periodSales)));
    }

    private void inventoryReport() {
        Display.title("INVENTORY REPORT");
        List<Medicine> all = medicines.findAll();

        if (all.isEmpty()) {
            Display.feedback("No medicines in inventory.");
            return;
        }

        int inStock = 0;
        int lowStock = 0;
        int outOfStock = 0;
        int expired = 0;
        double inventoryValue = 0;

        for (Medicine medicine : all) {
            inventoryValue += medicine.getUnitPrice() * medicine.getQuantity();

            if (!medicine.isActive()) {
                continue;
            }

            if (medicine.isExpired()) {
                expired++;
            } else if (medicine.isOutOfStock()) {
                outOfStock++;
            } else if (medicine.isLowStock()) {
                lowStock++;
            } else {
                inStock++;
            }
        }

        Display.feedback("Inventory summary:\nTotal medicines: " + all.size()
                + "\nIn stock: " + inStock
                + "\nLow stock: " + lowStock
                + "\nOut of stock: " + outOfStock
                + "\nExpired: " + expired
                + "\nTotal inventory value: " + money(inventoryValue));
        Table.medicines(all);
    }

    private void stockAlerts() {
        Display.title("LOW STOCK AND EXPIRED MEDICINES");

        List<Medicine> expired = new ArrayList<>();
        List<Medicine> nearExpiry = new ArrayList<>();
        List<Medicine> lowStock = new ArrayList<>();
        List<Medicine> outOfStock = new ArrayList<>();

        for (Medicine medicine : medicines.findAll()) {
            if (!medicine.isActive()) {
                continue;
            }

            if (medicine.isExpired()) {
                expired.add(medicine);
            } else if (medicine.isNearExpiry(Medicine.EXPIRY_WINDOW_DAYS)) {
                nearExpiry.add(medicine);
            }

            if (medicine.isOutOfStock()) {
                outOfStock.add(medicine);
            } else if (medicine.isLowStock()) {
                lowStock.add(medicine);
            }
        }

        if (expired.isEmpty() && nearExpiry.isEmpty() && lowStock.isEmpty() && outOfStock.isEmpty()) {
            Display.feedback("No low-stock or expired medicines.");
            return;
        }

        if (!expired.isEmpty()) {
            Display.println("EXPIRED (" + expired.size() + "):");
            for (Medicine medicine : expired) {
                Display.println("  - " + medicine.getName() + " (" + medicine.getMedicineId() + ") expired on " + medicine.getExpirationDate());
            }
        }

        if (!nearExpiry.isEmpty()) {
            Display.println("EXPIRING WITHIN " + Medicine.EXPIRY_WINDOW_DAYS + " DAYS (" + nearExpiry.size() + "):");
            for (Medicine medicine : nearExpiry) {
                Display.println("  - " + medicine.getName() + " (" + medicine.getMedicineId() + ") expires on " + medicine.getExpirationDate());
            }
        }

        if (!outOfStock.isEmpty()) {
            Display.println("OUT OF STOCK (" + outOfStock.size() + "):");
            for (Medicine medicine : outOfStock) {
                Display.println("  - " + medicine.getName() + " (" + medicine.getMedicineId() + ")");
            }
        }

        if (!lowStock.isEmpty()) {
            Display.println("LOW STOCK (" + lowStock.size() + "):");
            for (Medicine medicine : lowStock) {
                Display.println("  - " + medicine.getName() + " (" + medicine.getMedicineId() + ") - Stock: " + medicine.getQuantity() + ", Min: " + medicine.getMinStockLevel());
            }
        }

        Display.println();
    }

    private void transactionHistory() {
        Display.title("TRANSACTION HISTORY");
        List<Sale> all = sales.findAll();

        if (all.isEmpty()) {
            Display.feedback("No sales recorded yet.");
            return;
        }

        Table.sales(all);
        Display.feedback("Total transactions: " + all.size() + "\nTotal amount: " + money(sumTotal(all)));
    }

    private LocalDate readDate(String prompt) {
        while (true) {
            String raw = input.readText(prompt);

            try {
                Validate.date(raw);
                return LocalDate.parse(raw);
            } catch (InvalidInputException e) {
                Display.feedback(e.getMessage());
            }
        }
    }

    private double sumTotal(List<Sale> salesList) {
        double total = 0;

        for (Sale sale : salesList) {
            total += sale.getTotalAmount();
        }

        return total;
    }

    private static String money(double amount) {
        return String.format(Locale.ROOT, "%.2f", amount);
    }
}