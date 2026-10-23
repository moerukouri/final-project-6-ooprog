package services;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import model.Medicine;
import model.User;
import repository.MedicineRepository;
import strategy.Action;
import ui.AdminMenu;
import ui.Display;
import ui.Table;
import util.Input;
import util.Validate;

public class MedicineService {
    private final MedicineRepository medicines;
    private final Input input;

    public MedicineService(MedicineRepository medicines) {
        this.medicines = medicines;
        this.input = Input.getInstance();
    }

    public void menu(User user) {
        Validate.authorization(user, Action.ADD_MEDICINE);

        boolean running = true;
        while (running) {
            new AdminMenu().manageMedicine();
            int choice = input.readChoice(0, 6);

            switch (choice) {
                case 1 -> viewAll(user);
                case 2 -> addMedicine(user);
                case 3 -> updateMedicine(user);
                case 4 -> deleteMedicine(user);
                case 5 -> search(user);
                case 6 -> changeStatus(user);
                case 0 -> running = false;
            }
        }
    }

    public void viewAll(User user) {
        Validate.authorization(user, Action.VIEW_ALL_MEDICINE);
        List<Medicine> all = medicines.findAll();

        if (all.isEmpty()) {
            Display.feedback("No medicines in inventory yet.");
            return;
        }

        Display.title("MEDICINE INVENTORY");
        Table.medicines(all);
        showStockAlerts();
    }

    public void search(User user) {
        Validate.authorization(user, Action.SEARCH_MEDICINE);
        Display.title("SEARCH MEDICINE");

        String query = input.readText("Input Medicine Name or Category: ");
        String needle = query.toLowerCase();
        List<Medicine> matches = filter(medicines.findAll(), m -> m.getName().toLowerCase().contains(needle) || m.getCategory().toLowerCase().contains(needle));

        if (matches.isEmpty()) {
            Display.feedback("No medicine found for \"" + query + "\".");
            return;
        }

        Table.medicines(matches);
    }

    private void addMedicine(User user) {
        Validate.authorization(user, Action.ADD_MEDICINE);
        Display.title("ADD MEDICINE");

        Medicine medicine = new Medicine(
                medicines.nextMedicineId(), input.readText("Input Batch Number: "), input.readMedicineName(), input.readCategory(), input.readUnitPrice(), input.readQuantity(), input.readMinimumStockLevel(), input.readExpirationDate(), true
        );

        medicines.add(medicine);
        Display.feedback("Medicine added successfully.\nMedicine ID: " + medicine.getMedicineId() + "\nName: " + medicine.getName());
    }

    private void updateMedicine(User user) {
        Validate.authorization(user, Action.UPDATE_MEDICINE);
        Display.title("UPDATE MEDICINE");

        String id = input.readText("Input Medicine ID: ");
        Medicine medicine = findByMedicineId(id);

        if (medicine == null) {
            Display.feedback("Medicine ID not found.");
            return;
        }

        showDetails(medicine);

        medicine.updateDetails(
                input.readText("Input Batch Number: "), input.readMedicineName(), input.readCategory(), input.readUnitPrice(), input.readQuantity(), input.readMinimumStockLevel(), input.readExpirationDate()
        );

        medicines.update(medicine);
        Display.feedback("Medicine " + medicine.getMedicineId() + " updated successfully.");
    }

    private void deleteMedicine(User user) {
        Validate.authorization(user, Action.DELETE_MEDICINE);
        Display.title("DELETE MEDICINE");

        String id = input.readText("Input Medicine ID: ");
        Medicine medicine = findByMedicineId(id);

        if (medicine == null) {
            Display.feedback("Medicine ID not found.");
            return;
        }

        showDetails(medicine);

        String confirm = input.readText("Are you sure you want to delete this medicine? (y/n): ");
        if (!confirm.equalsIgnoreCase("y")) {
            Display.feedback("Delete cancelled.");
            return;
        }

        medicines.removeById(id);
        Display.feedback("Medicine " + id + " has been deleted.");
    }

    private void changeStatus(User user) {
        Validate.authorization(user, Action.UPDATE_MEDICINE);
        Display.title("ACTIVATE / DEACTIVATE MEDICINE");

        String id = input.readText("Input Medicine ID: ");
        Medicine medicine = findByMedicineId(id);

        if (medicine == null) {
            Display.feedback("Medicine ID not found.");
            return;
        }

        showDetails(medicine);

        String verb = medicine.isActive() ? "deactivate" : "activate";
        String confirm = input.readText("Are you sure you want to " + verb + " this medicine? (y/n): ");
        if (!confirm.equalsIgnoreCase("y")) {
            Display.feedback("Cancelled. No changes were made.");
            return;
        }

        boolean activating = !medicine.isActive();
        medicine.setActive(activating);
        medicines.update(medicine);

        Display.feedback("Medicine " + id + " is now " + (activating ? "active" : "inactive") + ".");
    }

    private Medicine findByMedicineId(String id) {
        return medicines.findByMedicineId(id).orElse(null);
    }

    private void showDetails(Medicine medicine) {
        Display.title("MEDICINE DETAILS");
        Display.println("Medicine ID:     " + medicine.getMedicineId());
        Display.println("Name:            " + medicine.getName());
        Display.println("Batch Number:    " + medicine.getBatchNumber());
        Display.println("Category:        " + medicine.getCategory());
        Display.println("Unit Price:      " + String.format("P%.2f", medicine.getUnitPrice()));
        Display.println("Stock:           " + medicine.getQuantity());
        Display.println("Min Stock Level: " + medicine.getMinStockLevel());
        Display.println("Expiration Date: " + medicine.getExpirationDate());
        Display.println("Status:          " + medicine.availabilityStatus().getLabel());
    }

    private void showStockAlerts() {
        List<Medicine> active = filter(medicines.findAll(), Medicine::isActive);

        List<Medicine> expired = filter(active, Medicine::isExpired);
        List<Medicine> nearExpiry = filter(active, m -> !m.isExpired() && m.isNearExpiry(Medicine.EXPIRY_WINDOW_DAYS));
        List<Medicine> outOfStock = filter(active, Medicine::isOutOfStock);
        List<Medicine> lowStock = filter(active, m -> !m.isOutOfStock() && m.isLowStock());

        Display.title("STOCK AND EXPIRATION ALERTS");

        if (expired.isEmpty() && nearExpiry.isEmpty() && outOfStock.isEmpty() && lowStock.isEmpty()) {
            Display.feedback("No alerts. All medicines are in good condition.");
            return;
        }

        printSection("EXPIRED", expired, m -> " expired on " + m.getExpirationDate());
        printSection("EXPIRING WITHIN " + Medicine.EXPIRY_WINDOW_DAYS + " DAYS", nearExpiry, m -> " expires on " + m.getExpirationDate());
        printSection("OUT OF STOCK", outOfStock, m -> "");
        printSection("LOW STOCK", lowStock, m -> " - Stock: " + m.getQuantity() + ", Min: " + m.getMinStockLevel());

        Display.println();
    }

    private void printSection(String heading, List<Medicine> list, Function<Medicine, String> detail) {
        if (list.isEmpty()) {
            return;
        }

        Display.println(heading + " (" + list.size() + "):");
        list.forEach(m -> Display.println("  - " + m.getName() + " (" + m.getMedicineId() + ")" + detail.apply(m)));
    }

    private List<Medicine> filter(List<Medicine> list, Predicate<Medicine> condition) {
        return list.stream().filter(condition).collect(Collectors.toList());
    }
}