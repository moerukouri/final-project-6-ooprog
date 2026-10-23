package services;

import exception.InvalidInputException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import model.Medicine;
import model.Sale;
import model.SaleItem;
import model.User;
import repository.MedicineRepository;
import repository.SaleRepository;
import strategy.Action;
import ui.AdminMenu;
import ui.Display;
import ui.Table;
import util.Input;
import util.Validate;

public class SaleService {
    private final SaleRepository sales;
    private final MedicineRepository medicines;
    private final ReportService reports;
    private final AuditService audit;
    private final Input input;

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public SaleService(SaleRepository sales, MedicineRepository medicines, ReportService reports, AuditService audit) {
        this.sales = sales;
        this.medicines = medicines;
        this.reports = reports;
        this.audit = audit;
        this.input = Input.getInstance();
    }

    public void menu(User user) {
        Validate.authorization(user, Action.VIEW_ALL_SALES);

        boolean running = true;
        while (running) {
            new AdminMenu().manageSales();
            int choice = input.readChoice(0, 5);

            switch (choice) {
                case 1 -> viewAllSales(user);
                case 2 -> viewOwnSales(user);
                case 3 -> searchSale(user, false);
                case 4 -> reports.menu(user);
                case 5 -> recordTransaction(user);
                case 0 -> running = false;
            }
        }
    }

    public void viewAllSales(User user) {
        Validate.authorization(user, Action.VIEW_ALL_SALES);

        List<Sale> all = sales.findAll();
        if (all.isEmpty()) {
            Display.feedback("No sales recorded yet.");
            return;
        }

        Display.title("ALL SALES");
        Table.sales(all);
    }

    public void viewOwnSales(User user) {
        Validate.authorization(user, Action.VIEW_OWN_SALES);

        List<Sale> own = new ArrayList<>();

        for (Sale sale : sales.findAll()) {
            if (sale.getCashierId().equals(user.getUserId())) {
                own.add(sale);
            }
        }

        if (own.isEmpty()) {
            Display.feedback("You have no sales recorded yet.");
            return;
        }

        Display.title("MY SALES");
        Table.sales(own);
    }

    public void searchSale(User user, boolean ownOnly) {
        Validate.authorization(user, ownOnly ? Action.SEARCH_OWN_SALE : Action.SEARCH_SALE);

        Display.title("SEARCH SALE");
        String id = input.readText("Input Sale ID: ");
        Sale sale = sales.findById(id).orElse(null);

        if (sale == null || (ownOnly && !sale.getCashierId().equals(user.getUserId()))) {
            Display.feedback("Sale not found.");
            return;
        }

        printSale(sale);
    }

    public void recordTransaction(User user) {
        Validate.authorization(user, Action.RECORD_TRANSACTION);

        ArrayList<SaleItem> items = new ArrayList<>();

        while (true) {
            Display.title("RECORD TRANSACTION");
            printCart(items);
            Display.println("1. Add Item");
            Display.println("0. Finish Transaction");
            int choice = input.readChoice(0, 1);

            if (choice == 0) {
                break;
            }

            String query = input.readText("Input Medicine Name or ID: ");
            Medicine medicine = findMedicine(query);

            if (medicine == null) {
                Display.feedback("Medicine not found.");
                continue;
            }

            if (medicine.isExpired()) {
                Display.feedback("Cannot sell " + medicine.getName() + ". It expired on " + medicine.getExpirationDate() + ".");
                continue;
            }

            int available = medicine.getQuantity() - quantityAlreadyAdded(items, medicine.getMedicineId());
            if (available <= 0) {
                Display.feedback("Insufficient stock for " + medicine.getName() + ". Available: " + available + ".");
                continue;
            }

            int quantity = readQuantity();
            if (quantity > available) {
                Display.feedback("Insufficient stock for " + medicine.getName() + ". Available: " + available + ".");
                continue;
            }

            items.add(new SaleItem(medicine.getMedicineId(), medicine.getName(), medicine.getUnitPrice(), quantity));
        }

        if (items.isEmpty()) {
            Display.feedback("No items added. Transaction cancelled.");
            return;
        }

        Display.title("REVIEW TRANSACTION");
        printCart(items);

        String confirm = input.readText("Complete this sale? (y/n): ");
        if (!confirm.equalsIgnoreCase("y")) {
            Display.feedback("Transaction cancelled.");
            return;
        }

        Sale sale = new Sale(sales.nextSaleId(), LocalDateTime.now(), user.getUserId(), items);

        for (SaleItem item : sale.getItems()) {
            Medicine medicine = medicines.findByMedicineId(item.getMedicineId()).orElse(null);

            if (medicine != null) {
                medicine.setQuantity(medicine.getQuantity() - item.getQuantity());
                medicines.update(medicine);
            }
        }

        sales.add(sale);
        printSale(sale);
        Display.feedback("Sale recorded successfully.");
    }

    public void voidSale(User user) {
        Validate.authorization(user, Action.VIEW_ALL_SALES);

        Display.title("VOID SALE");
        String id = input.readText("Input Sale ID: ");
        Sale sale = sales.findById(id).orElse(null);

        if (sale == null) {
            Display.feedback("Sale not found.");
            return;
        }

        printSale(sale);

        String confirm = input.readText("Void this sale? (y/n): ");
        if (!confirm.equalsIgnoreCase("y")) {
            Display.feedback("Void cancelled.");
            return;
        }

        for (SaleItem item : sale.getItems()) {
            Medicine medicine = medicines.findByMedicineId(item.getMedicineId()).orElse(null);

            if (medicine != null) {
                medicine.setQuantity(medicine.getQuantity() + item.getQuantity());
                medicines.update(medicine);
            }
        }

        audit.log("Sale " + sale.getSaleId() + " voided by " + user.getUserId() + " (" + user.getUsername() + "). Stock restored.");
        Display.feedback("Sale " + sale.getSaleId() + " has been voided and its stock was restored.\nAn audit record was saved.");
    }

    private int readQuantity() {
        while (true) {
            String raw = input.readText("Input Quantity: ");

            try {
                return Validate.isValidInteger(raw, "Quantity");
            } catch (InvalidInputException e) {
                Display.feedback(e.getMessage());
            }
        }
    }

    private void printCart(ArrayList<SaleItem> items) {
        if (items.isEmpty()) {
            Display.println("No items added yet.");
        } else {
            double total = 0;

            for (SaleItem item : items) {
                total += item.getSubtotal();
                Display.println("- " + item.getMedicineName() + " (" + item.getMedicineId() + ") x"
                        + item.getQuantity() + "  Subtotal: " + money(item.getSubtotal()));
            }

            Display.println("Current Total: " + money(total));
        }

        Display.println();
    }

    private void printSale(Sale sale) {
        Display.title("SALE DETAILS - " + sale.getSaleId());
        Display.println("Date:    " + sale.getTimestamp().format(TIME_FORMAT));
        Display.println("Cashier: " + sale.getCashierId());
        Display.println();

        for (SaleItem item : sale.getItems()) {
            Display.println(item.getMedicineName() + " (" + item.getMedicineId() + ") x"
                    + item.getQuantity() + "  " + money(item.getSubtotal()));
        }

        Display.println();
        Display.println("TOTAL: " + money(sale.getTotalAmount()));
        Display.println();
    }

    private Medicine findMedicine(String query) {
        for (Medicine medicine : medicines.findAll()) {
            if (medicine.getMedicineId().equalsIgnoreCase(query)
                    || medicine.getName().equalsIgnoreCase(query)) {
                return medicine;
            }
        }

        return null;
    }

    private int quantityAlreadyAdded(ArrayList<SaleItem> items, String medicineId) {
        int total = 0;

        for (SaleItem item : items) {
            if (item.getMedicineId().equals(medicineId)) {
                total += item.getQuantity();
            }
        }

        return total;
    }

    private static String money(double amount) {
        return String.format(Locale.ROOT, "%.2f", amount);
    }
}