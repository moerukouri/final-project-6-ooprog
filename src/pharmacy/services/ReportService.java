package services;

import exception.InvalidInputException;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
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
        Display.feedback("Summary for " + date + ":\nTotal sales: " + daySales.size() + "\nTotal amount: " + money(sumTotal(daySales)));
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
        Display.feedback("Summary from " + start + " to " + end + ":\nTotal sales: " + periodSales.size() + "\nTotal amount: " + money(sumTotal(periodSales)));
    }

    private void inventoryReport() {
        Display.title("INVENTORY REPORT");
        List<Medicine> all = medicines.findAll();

        if (all.isEmpty()) {
            Display.feedback("No medicines in inventory.");
            return;
        }

        List<Medicine> active = filter(all, Medicine::isActive);

        long expired = all.stream().filter(Medicine::isExpired).count();
        long activeExpired = active.stream().filter(Medicine::isExpired).count();
        long outOfStock = active.stream().filter(m -> !m.isExpired() && m.isOutOfStock()).count();
        long lowStock = active.stream().filter(m -> !m.isExpired() && !m.isOutOfStock() && m.isLowStock()).count();
        long inStock = active.size() - activeExpired - outOfStock - lowStock;

        double inventoryValue = all.stream().mapToDouble(m -> m.getUnitPrice() * m.getQuantity()).sum();

        Display.println("Total medicines:       " + all.size());
        Display.println("In stock:              " + inStock);
        Display.println("Low stock:             " + lowStock);
        Display.println("Out of stock:          " + outOfStock);
        Display.println("Expired:               " + expired);
        Display.println("Total inventory value: " + money(inventoryValue));
        Display.println();
    }

    private void stockAlerts() {
        Display.title("LOW STOCK AND EXPIRED MEDICINES");

        List<Medicine> allMedicines = medicines.findAll();
        List<Medicine> active = filter(allMedicines, Medicine::isActive);

        List<Medicine> expired = filter(allMedicines, Medicine::isExpired);
        List<Medicine> nearExpiry = filter(active, m -> !m.isExpired() && m.isNearExpiry(Medicine.EXPIRY_WINDOW_DAYS));
        List<Medicine> outOfStock = filter(active, Medicine::isOutOfStock);
        List<Medicine> lowStock = filter(active, m -> !m.isOutOfStock() && m.isLowStock());

        if (expired.isEmpty() && nearExpiry.isEmpty() && lowStock.isEmpty() && outOfStock.isEmpty()) {
            Display.feedback("No low-stock or expired medicines.");
            return;
        }

        printSection("EXPIRED", expired, m -> " expired on " + m.getExpirationDate());
        printSection("EXPIRING WITHIN " + Medicine.EXPIRY_WINDOW_DAYS + " DAYS", nearExpiry, m -> " expires on " + m.getExpirationDate());
        printSection("OUT OF STOCK", outOfStock, m -> "");
        printSection("LOW STOCK", lowStock, m -> " - Stock: " + m.getQuantity() + ", Min: " + m.getMinStockLevel());

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

    private void printSection(String heading, List<Medicine> list, Function<Medicine, String> detail) {
        if (list.isEmpty()) {
            return;
        }

        Display.println(heading + " (" + list.size() + "):");
        list.forEach(m -> Display.println("  - " + m.getName() + " (" + m.getMedicineId() + ")" + detail.apply(m)));
    }

    private List<Medicine> filter(List<Medicine> list, java.util.function.Predicate<Medicine> condition) {
        return list.stream().filter(condition).collect(Collectors.toList());
    }

    private LocalDate readDate(String prompt) {
        LocalDate date = null;

        while (date == null) {
            date = parseDate(input.readText(prompt));
        }

        return date;
    }

    private LocalDate parseDate(String raw) {
        try {
            Validate.date(raw);
            return LocalDate.parse(raw);
        } catch (InvalidInputException e) {
            Display.feedback(e.getMessage());
            return null;
        }
    }

    private double sumTotal(List<Sale> salesList) {
        return salesList.stream().mapToDouble(Sale::getTotalAmount).sum();
    }

    private static String money(double amount) {
        return String.format("P%.2f", amount);
    }
}