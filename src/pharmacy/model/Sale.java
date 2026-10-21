package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Sale {
    private final String saleId;
    private final LocalDateTime timestamp;
    private final String cashierId;
    private final List<SaleItem> items = new ArrayList<>();
    private double totalAmount;
    private double storedTotal;

    private Sale(String saleId, LocalDateTime timestamp, String cashierId) {
        this.saleId = saleId;
        this.timestamp = timestamp;
        this.cashierId = cashierId;
        this.totalAmount = 0;
    }

    public Sale(String saleId, LocalDateTime timestamp, String cashierId, List<SaleItem> items) {
        this(saleId, timestamp, cashierId);
        for (SaleItem item : items) {
            addItem(item);
        }
    }

    public static Sale fromFile(String saleId, LocalDateTime timestamp, String cashierId, double storedTotal) {
        Sale sale = new Sale(saleId, timestamp, cashierId);
        sale.storedTotal = storedTotal;
        return sale;
    }

    public final void addItem(SaleItem item) {
        items.add(item);
        double total = 0;
        for(SaleItem i : items) {
            total += i.getSubtotal();
        }
        this.totalAmount = total;
    }

    public double storedTotal() {
        return storedTotal;
    }

    public String getSaleId() {
        return saleId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getCashierId() {
        return cashierId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public List<SaleItem> getItems() {
        return Collections.unmodifiableList(items);
    }
}