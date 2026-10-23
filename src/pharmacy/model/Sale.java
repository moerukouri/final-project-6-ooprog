package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import util.Validate;

public class Sale {
    private final String saleId;
    private final LocalDateTime timestamp;
    private final String cashierId;
    private final List<SaleItem> items = new ArrayList<>();
    private double totalAmount;
    private double storedTotal;

    private Sale(String saleId, LocalDateTime timestamp, String cashierId) {
        this.saleId = Validate.requireText(saleId, "Sale ID");
        this.timestamp = Validate.requireNonNull(timestamp, "Sale timestamp");
        this.cashierId = Validate.requireText(cashierId, "Cashier ID");
        this.totalAmount = 0;
    }

    public Sale(String saleId, LocalDateTime timestamp, String cashierId, List<SaleItem> items) {
        this(saleId, timestamp, cashierId);
        if (items.isEmpty()) {
            throw new IllegalArgumentException("A sale needs at least one item.");
        }
        for (SaleItem item : items) {
            addItem(item);
        }
    }

    public static Sale fromFile(String saleId, LocalDateTime timestamp, String cashierId, double storedTotal) {
        Sale sale = new Sale(saleId, timestamp, cashierId);
        sale.setStoredTotal(storedTotal);
        return sale;
    }

    public final void addItem(SaleItem item) {
        items.add(Validate.requireNonNull(item, "Sale item"));
        double total = 0;
        for(SaleItem i : items) {
            total += i.getSubtotal();
        }
        this.totalAmount = total;
    }

    public void setStoredTotal(double storedTotal) {
        this.storedTotal = Validate.requireNonNegativeFinite(storedTotal, "Stored total");
    }

    public double getStoredTotal() {
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