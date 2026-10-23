package services;

import java.util.ArrayList;
import java.util.List;
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
            int choice = input.readChoice(0, 5);

            switch (choice) {
                case 1 -> viewAll(user);
                case 2 -> addMedicine(user);
                case 3 -> updateMedicine(user);
                case 4 -> deleteMedicine(user);
                case 5 -> search(user);
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
        List<Medicine> matches = new ArrayList<>();

        for (Medicine medicine : medicines.findAll()) {
            if (medicine.getName().toLowerCase().contains(query.toLowerCase())
                    || medicine.getCategory().toLowerCase().contains(query.toLowerCase())) {
                matches.add(medicine);
            }
        }

        if (matches.isEmpty()) {
            Display.feedback("No medicine found for \"" + query + "\".");
        } else {
            Table.medicines(matches);
        }
    }

    private void addMedicine(User user) {
        Validate.authorization(user, Action.ADD_MEDICINE);
        Display.title("ADD MEDICINE");

        Medicine medicine = new Medicine(
                medicines.nextMedicineId(),
                input.readText("Input Batch Number: "),
                input.readMedicineName(),
                input.readCategory(),
                input.readUnitPrice(),
                input.readQuantity(),
                input.readMinimumStockLevel(),
                input.readExpirationDate(),
                true
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
                input.readText("Input Batch Number: "),
                input.readMedicineName(),
                input.readCategory(),
                input.readUnitPrice(),
                input.readQuantity(),
                input.readMinimumStockLevel(),
                input.readExpirationDate()
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

    private Medicine findByMedicineId(String id) {
        return medicines.findByMedicineId(id).orElse(null);
    }

    private void showDetails(Medicine medicine) {
        Display.title("MEDICINE DETAILS");
        Display.println("Medicine ID:     " + medicine.getMedicineId());
        Display.println("Name:            " + medicine.getName());
        Display.println("Batch Number:    " + medicine.getBatchNumber());
        Display.println("Category:        " + medicine.getCategory());
        Display.println("Unit Price:      " + String.format("%.2f", medicine.getUnitPrice()));
        Display.println("Stock:           " + medicine.getQuantity());
        Display.println("Min Stock Level: " + medicine.getMinStockLevel());
        Display.println("Expiration Date: " + medicine.getExpirationDate());
        Display.println("Status:          " + medicine.availabilityStatus().getLabel());
    }

    private void showStockAlerts() {
        List<Medicine> expired = new ArrayList<>();
        List<Medicine> nearExpiry = new ArrayList<>();
        List<Medicine> outOfStock = new ArrayList<>();
        List<Medicine> lowStock = new ArrayList<>();

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

        Display.title("STOCK AND EXPIRATION ALERTS");

        if (expired.isEmpty() && nearExpiry.isEmpty() && outOfStock.isEmpty() && lowStock.isEmpty()) {
            Display.feedback("No alerts. All medicines are in good condition.");
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
}