package repository;

import io.FileCreator;
import io.FilePath;
import model.Sale;
import model.SaleItem;
import io.FileHandler;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class SaleRepository {
    private static final String SALES_PATH = FilePath.SALES_PATH.getFilePath();
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final String SALE_TYPE = "SALE";
    private static final String ITEM_TYPE = "ITEM";
    private static final int SALE_FIELDS = 5;
    private static final int ITEM_FIELDS = 7;
    private static final double TOTAL_TOLERANCE = 0.005;

    public SaleRepository() {
        FileCreator.getInstance().createSalesFile();
    }

    public List<Sale> findAll() {
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
                        if(current == null ) {
                            throw new IllegalArgumentException("Item found without a sale");
                        }
                        SaleItem item = parseItem(f, current);
                        current.addItem(item);
                    }
                    default -> throw new IllegalArgumentException("Unknown record type: " + f[0]); //ADD EXCEPTION
                }
            } catch (Exception | DateTimeParseException e) { //ADD EXCEPTION
                System.err.println("[warn] Skipping malformed sale record: " + line);
                current = null;
            }
        }

        for (Sale sale : sales) {
            if (Math.abs(sale.getStoredTotal() - sale.getTotalAmount()) > TOTAL_TOLERANCE) {
                System.err.println("[warn] Sale " + sale.getSaleId() + ": stored total " + money(sale.getStoredTotal()) + " differs from computed total " + money(sale.getTotalAmount()) + "; using computed value."); // ADD EXCEPTION MAYBEEEE
            }
        }
        return sales;
    }

    public Optional<Sale> findById(String saleId) {
        for (Sale sale : findAll()) {
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
            if(!day.isBefore(start) && !day.isAfter(end)){
                result.add(sale);
            }
        }
        return result;
    }

    public String nextSaleId() {
        int max = 0;
        for (Sale sale : findAll()) {
            max = Math.max(max, numericSuffix(sale.getSaleId()));
        }
        return String.format("S%04d", max + 1);
    }

    public void add(Sale sale) {
        FileHandler.write(serialize(sale), SALES_PATH, true);
        for (SaleItem item : sale.getItems()){
            FileHandler.write(serialize(sale.getSaleId(), item), SALES_PATH, true);
        }
    }

    private static int numericSuffix(String id) {
        try {
            return Integer.parseInt(id.substring(1));
        } catch (Exception e) { // ADD EXCEPTION
            return 0;
        }
    }

    private static Sale parseSale(String[] f) {
        if (f.length != SALE_FIELDS) {
            throw new IllegalArgumentException("SALE expected " + SALE_FIELDS + " fields, got" + f.length); // ADD EXCEPTION
        }
        return Sale.fromFile(f[1], LocalDateTime.parse(f[2], TS), f[3], Double.parseDouble(f[4]));
    }

    private static SaleItem parseItem(String[] f, Sale current) {
        if (f.length != ITEM_FIELDS) {
            throw new IllegalArgumentException("ITEM expected " + ITEM_FIELDS + " fields, got " + f.length); // ADD EXCEPTION
        }
        if (current == null || !current.getSaleId().equals(f[1])) {
            throw new IllegalArgumentException("ITEM line without a matching SALE line. "); // ADD EXCEPTION
        }
        return new SaleItem(f[2], f[3], Double.parseDouble(f[4]), Integer.parseInt(f[5]));
    }

    private static String serialize(Sale sale) {
        return String.join("|", SALE_TYPE, sale.getSaleId(), sale.getTimestamp().format(TS), sale.getCashierId(), money(sale.getTotalAmount()));
    }

    private static String serialize(String saleId, SaleItem item) {
        return String.join("|", ITEM_TYPE, saleId, item.getMedicineId(), item.getMedicineName(), money(item.getUnitPrice()), String.valueOf(item.getQuantity()), money(item.getSubtotal()));
    }

    private static String money (double amount) {
        return String.format(Locale.ROOT, "%.2f", amount);
    }
}
