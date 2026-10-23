package repository;

import io.FileCreator;
import io.FileHandler;
import io.FilePath;
import io.Operation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import exception.FileStorageException;
import exception.PharmacyException;
import model.Medicine;
import util.Validate;

public class MedicineRepository {
    private static final int FIELD_COUNT = 9;
    private static final String MEDICINE_PATH = FilePath.MEDICINE_PATH.getFilePath();

    public MedicineRepository() {
        FileCreator.getInstance().createMedicineFile();
    }

    public List<Medicine> findAll() {
        List<Medicine> medicines = new ArrayList<>();
        
        for (String line : FileHandler.retrieve(MEDICINE_PATH)) {
            if (line.isBlank() ||line.startsWith("#")) {
                continue;
            }
            
            try {
                medicines.add(parse(line));
            } catch (FileStorageException e) {
                System.err.println("[Warn] Skipping malformed medicine record: " + line);
            }
        }
        
        return medicines;
    }
    
    public Optional<Medicine> findByMedicineId(String medicineId) {
        for (Medicine medicine : findAll()) {
            if (medicine.getMedicineId().equals(medicineId)) {
                return Optional.of(medicine);
            }
        }
        
        return Optional.empty();
    }
    
    public String nextMedicineId() {
        int max = 0;
        
        for (Medicine medicine : findAll()) {
            max = Math.max(max, numericSuffix(medicine.getMedicineId()));
        }
        
        return String.format("M%04d", max + 1);
    }

    public void add(Medicine medicine) {
        FileHandler.write(serialize(medicine), MEDICINE_PATH, true);
    }

    public void update(Medicine medicine) {
        String oldLine = FileHandler.retrieveLine(medicine.getMedicineId(), 0, MEDICINE_PATH);
        Validate.isObjectNull(oldLine, "Old Line");
        
        if (!FileHandler.update(oldLine, serialize(medicine), MEDICINE_PATH)) {
            throw new FileStorageException(Operation.WRITE.getOperation(), MEDICINE_PATH);
        }
    }

    public void removeById(String medicineId) {
        String line = FileHandler.retrieveLine(medicineId, 0, MEDICINE_PATH);
        Validate.isObjectNull(line, "Line");
        
        if (!FileHandler.delete(line, MEDICINE_PATH)) {
            throw new FileStorageException(Operation.WRITE.getOperation(), MEDICINE_PATH);
        }
    }
 
    private static int numericSuffix(String id) {
        try {
            return Integer.parseInt(id.substring(1));
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            throw new PharmacyException("Medicine ID is Invalid.");
        }
    }
 
    private static Medicine parse(String line) {
        String[] f = line.split("\\|", -1);
        
        if (f.length != FIELD_COUNT) {
            throw new FileStorageException(line);
        }
        String id = f[0].trim();
        String batch = f[1].trim();
        String name = f[2].trim();
        String category = f[3].trim();
        
        double unitPrice = Double.parseDouble(f[4].trim());
        int quantity = Integer.parseInt(f[5].trim());
        int minStock = Integer.parseInt(f[6].trim());
        
        LocalDate expiration = LocalDate.parse(f[7].trim());
        boolean active = f[8].trim().equalsIgnoreCase("ACTIVE");
        
        if (id.isBlank() || name.isBlank()) {
            throw new FileStorageException(line);
        }
        
        return new Medicine(id, batch, name, category, unitPrice, quantity, minStock, expiration, active);
    }
 
    private static String serialize(Medicine medicine) {
        return String.join("|", medicine.getMedicineId(), medicine.getBatchNumber(), medicine.getName(), medicine.getCategory(), String.format(Locale.ROOT, "%.2f", medicine.getUnitPrice()), String.valueOf(medicine.getQuantity()), String.valueOf(medicine.getMinStockLevel()), medicine.getExpirationDate().toString(), medicine.isActive() ? "ACTIVE" : "INACTIVE");
    }
}