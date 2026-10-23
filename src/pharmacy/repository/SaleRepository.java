package repository;

import exception.FileStorageException;
import exception.PharmacyException;
import io.FileCreator;
import io.FileHandler;
import io.FilePath;
import io.Operation;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import model.Sale;
import model.SaleItem;
import ui.Display;

public class SaleRepository {
    private static final String SALES_PATH = FilePath.SALES_PATH.getFilePath();
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final String SALE_TYPE = "SALE";
    private static final String ITEM_TYPE = "ITEM";
    private static final int SALE_FIELDS = 5;
    private static final int SALE_FIELDS_WITH_STATUS = 6;
    private static final String ACTIVE_STATUS = "ACTIVE";
    private static final String VOIDED_STATUS = "VOIDED";
    private static final int ITEM_FIELDS = 7;
    private static final double TOTAL_TOLERANCE = 0.005;

    public SaleRepository() {
        FileCreator.getInstance().createSalesFile();
    }

    public List<Sale> findAll() {
        return loadAll().stream().filter(sale -> !sale.isVoided()).collect(Collectors.toList());
    }

    private List<Sale> loadAll() {
        List<Sale> sales = new ArrayList<>();
        Sale current = null;

        for (String line :FileHandler.retrieve(SALES_PATH)) {
            if(line.isBlank() || line.startsWith("#")) {
                continue;
            }
            
            try {
                String[] f = line.split("\\|", -1);
                
                switch(f[0]) {
                    case SALE_TYPE -> {
                        current = parseSale(f);
                        sales.add(current);
                    }
                    case ITEM_TYPE -> {
                        if (current == null ) {
                            throw new PharmacyException("Item found without a sale");
                        }
                        SaleItem item = parseItem(f, current);
                        current.addItem(item);
                    }
                    default -> throw new FileStorageException(line);
                }
            } catch (FileStorageException | DateTimeParseException e) { 
                Display.println("[Warn] Skipping malformed sale record: " + line);
                current = null;
            }
        }
        for (Sale sale : sales) {
            if (Math.abs(sale.getStoredTotal() - sale.getTotalAmount()) > TOTAL_TOLERANCE) {
                System.err.println("[Warn] Sale " + sale.getSaleId() + ": stored total " + money(sale.getStoredTotal()) + " differs from computed total " + money(sale.getTotalAmount()) + "; using computed value.");
            }
        }
        
        return sales;
    }

    public Optional<Sale> findById(String saleId) {
        for (Sale sale : loadAll()) {
            if (sale.getSaleId().equals(saleId)) {
                return Optional.of(sale);
            }
        }
        
        return Optional.empty();
    }

    public List<Sale> findByDate(LocalDate date) {
        return findByDateRange(date, date);
    }

    public List<Sale> findByDateRange(LocalDate start, LocalDate end) {
        List<Sale> result = new ArrayList<>();
        
        for(Sale sale : findAll()){
            LocalDate day = sale.getTimestamp().toLocalDate();
            
            if (!day.isBefore(start) && !day.isAfter(end)){
                result.add(sale);
            }
        }
        
        return result;
    }

    public String nextSaleId() {
        int max = 0;
        
        for (Sale sale : loadAll()) {
            max = Math.max(max, numericSuffix(sale.getSaleId()));
        }
        
        return String.format("S%04d", max + 1);
    }

    public void add(Sale sale) {
        List<String> records = new ArrayList<>();

        records.add(serialize(sale));

        for (SaleItem item : sale.getItems()) {
            records.add(serialize(sale.getSaleId(), item));
        }

        FileHandler.write(
            String.join(System.lineSeparator(), records),
            SALES_PATH,
            true
        );
    }

    public void removeById(String saleId) {
        ArrayList<String> lines = FileHandler.retrieve(SALES_PATH);

        String salePrefix = SALE_TYPE + "|" + saleId + "|";
        String itemPrefix = ITEM_TYPE + "|" + saleId + "|";

        boolean removed = lines.removeIf(line ->
            line.startsWith(salePrefix)
                || line.startsWith(itemPrefix)
        );

        if (removed) {
            FileHandler.writeAll(lines, SALES_PATH);
        }
    }

    public void markVoided(String saleId) {
        String prefix = SALE_TYPE + "|" + saleId + "|";

        String oldLine = FileHandler.retrieve(SALES_PATH).stream().filter(line -> line.startsWith(prefix)).findFirst().orElseThrow(() -> new PharmacyException("Sale " + saleId + " was not found in storage."));

        String[] f = oldLine.split("\\|", -1);
        String newLine = String.join("|", f[0], f[1], f[2], f[3], f[4], VOIDED_STATUS);

        if (!FileHandler.update(oldLine, newLine, SALES_PATH)) {
            throw new FileStorageException(Operation.WRITE.getOperation(), SALES_PATH);
        }
    }

    private static int numericSuffix(String id) {
        try {
            return Integer.parseInt(id.substring(1));
        } catch (NumberFormatException e) {
            throw new PharmacyException("Sale ID is Invalid.");
        }
    }

    private static Sale parseSale(String[] f) {
        if (f.length != SALE_FIELDS && f.length != SALE_FIELDS_WITH_STATUS) {
            throw new FileStorageException(f);
        }

        boolean voided = f.length == SALE_FIELDS_WITH_STATUS && VOIDED_STATUS.equalsIgnoreCase(f[5].trim());

        return Sale.fromFile(f[1], LocalDateTime.parse(f[2], TS), f[3], Double.parseDouble(f[4]), voided);
    }

    private static SaleItem parseItem(String[] f, Sale current) {
        if (f.length != ITEM_FIELDS) {
            throw new FileStorageException(f);
        }
        
        if (current == null || !current.getSaleId().equals(f[1])) {
            throw new PharmacyException("ITEM line without a matching SALE line. ");
        }
       
        return new SaleItem(f[2], f[3], Double.parseDouble(f[4]), Integer.parseInt(f[5]));
    }

    private static String serialize(Sale sale) {
        return String.join("|", SALE_TYPE, sale.getSaleId(), sale.getTimestamp().format(TS), sale.getCashierId(), money(sale.getTotalAmount()), sale.isVoided() ? VOIDED_STATUS : ACTIVE_STATUS);
    }

    private static String serialize(String saleId, SaleItem item) {
        return String.join("|", ITEM_TYPE, saleId, item.getMedicineId(), item.getMedicineName(), money(item.getUnitPrice()), String.valueOf(item.getQuantity()), money(item.getSubtotal()));
    }

    private static String money (double amount) {
        return String.format(Locale.ROOT, "%.2f", amount);
    }
}