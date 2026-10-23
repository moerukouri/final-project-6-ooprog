package services;

import exception.ExpiredMedicineException;
import exception.InsufficientStockException;
import exception.InvalidInputException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
            int choice = input.readChoice(0, 6);

            switch (choice) {
                case 1 -> viewAllSales(user);
                case 2 -> viewOwnSales(user);
                case 3 -> searchSale(user, false);
                case 4 -> reports.menu(user);
                case 5 -> recordTransaction(user);
                case 6 -> voidSale(user);
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

        List<Sale> own = sales.findAll().stream()
                .filter(sale -> sale.getCashierId().equals(user.getUserId()))
                .collect(Collectors.toList());

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

        ArrayList<SaleItem> items = collectItems();

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

        adjustStock(sale, -1);
        sales.add(sale);
        printSale(sale);
        Display.feedback("Sale recorded successfully.");
    }

    public void voidSale(User user) {
        Validate.authorization(user, Action.VOID_SALE);

        Display.title("VOID SALE");
        String id = input.readText("Input Sale ID: ");
        Sale sale = sales.findById(id).orElse(null);

        if (sale == null) {
            Display.feedback("Sale not found.");
            return;
        }

        printSale(sale);

        if (sale.isVoided()) {
            Display.feedback("Sale " + sale.getSaleId() + " has already been voided.");
            return;
        }

        String confirm = input.readText("Void this sale? (y/n): ");
        if (!confirm.equalsIgnoreCase("y")) {
            Display.feedback("Void cancelled.");
            return;
        }

        sales.markVoided(sale.getSaleId());
        adjustStock(sale, 1);

        audit.log("Sale " + sale.getSaleId() + " voided by " + user.getUserId() + " (" + user.getUsername() + "). Stock restored.");
        Display.feedback("Sale " + sale.getSaleId() + " has been voided and its stock was restored.\nAn audit record was saved.");
    }

    private ArrayList<SaleItem> collectItems() {
        ArrayList<SaleItem> items = new ArrayList<>();
        boolean adding = true;

        while (adding) {
            Display.title("RECORD TRANSACTION");
            printCart(items);
            Display.println("1. Add Item");
            Display.println("0. Finish Transaction");

            adding = input.readChoice(0, 1) == 1;
            if (adding) {
                tryAddItem(items);
            }
        }

        return items;
    }

    private void tryAddItem(ArrayList<SaleItem> items) {
        String query = input.readText("Input Medicine Name or ID: ");
        Medicine medicine = findMedicine(query);

        if (medicine == null) {
            Display.feedback("Medicine not found.");
            return;
        }

        if (!medicine.isActive()) {
            Display.feedback(medicine.getName() + " is inactive and cannot be sold.");
            return;
        }

        try {
            checkNotExpired(medicine);
        } catch (ExpiredMedicineException e) {
            Display.feedback(e.getMessage());
            return;
        }

        int available = medicine.getQuantity() - quantityAlreadyAdded(items, medicine.getMedicineId());
        int quantity = readQuantity();
        try {
            checkStock(medicine, quantity, available);
        } catch (InsufficientStockException e) {
            Display.feedback(e.getMessage());
            return;
        }

        items.add(new SaleItem(medicine.getMedicineId(), medicine.getName(), medicine.getUnitPrice(), quantity));
    }

    private void adjustStock(Sale sale, int direction) {
        for (SaleItem item : sale.getItems()) {
            medicines.findByMedicineId(item.getMedicineId()).ifPresent(medicine -> {
                medicine.setQuantity(medicine.getQuantity() + direction * item.getQuantity());
                medicines.update(medicine);
            });
        }
    }

    private void checkStock(Medicine medicine, int requested, int available) {
        if (requested > available || available <= 0) {
            throw new InsufficientStockException(medicine.getName(), requested, available);
        }
    }

    private void checkNotExpired(Medicine medicine) {
        if (medicine.isExpired()) {
            throw new ExpiredMedicineException(medicine.getName(), medicine.getExpirationDate());
        }
    }

    private int readQuantity() {
        Integer quantity = null;

        while (quantity == null) {
            quantity = parseQuantity(input.readText("Input Quantity: "));
        }

        return quantity;
    }

    private Integer parseQuantity(String raw) {
        try {
            return Validate.isValidInteger(raw, "Quantity");
        } catch (InvalidInputException e) {
            Display.feedback(e.getMessage());
            return null;
        }
    }

    private void printCart(List<SaleItem> items) {
        if (items.isEmpty()) {
            Display.println("No items added yet.");
            Display.println();
            return;
        }

        items.forEach(item -> Display.println("- " + item.getMedicineName() + " (" + item.getMedicineId() + ") x" + item.getQuantity() + "  Subtotal: " + money(item.getSubtotal())));

        double total = items.stream().mapToDouble(SaleItem::getSubtotal).sum();
        Display.println("Current Total: " + money(total));
        Display.println();
    }

    private void printSale(Sale sale) {
        Display.title("SALE DETAILS - " + sale.getSaleId());
        Display.println("Date:    " + sale.getTimestamp().format(TIME_FORMAT));
        Display.println("Cashier: " + sale.getCashierId());
        Display.println("Status:  " + (sale.isVoided() ? "VOIDED" : "Completed"));
        Display.println();

        for (SaleItem item : sale.getItems()) {
            Display.println(item.getMedicineName() + " (" + item.getMedicineId() + ") x" + item.getQuantity() + "  " + money(item.getSubtotal()));
        }

        Display.println();
        Display.println("TOTAL: " + money(sale.getTotalAmount()));
        Display.println();
    }

    private Medicine findMedicine(String query) {
        return medicines.findAll().stream().filter(m -> m.getMedicineId().equalsIgnoreCase(query) || m.getName().equalsIgnoreCase(query)).findFirst().orElse(null);
    }

    private int quantityAlreadyAdded(List<SaleItem> items, String medicineId) {
        return items.stream().filter(item -> item.getMedicineId().equals(medicineId)).mapToInt(SaleItem::getQuantity).sum();
    }

    private static String money(double amount) {
        return String.format("P%.2f", amount);
    }
}