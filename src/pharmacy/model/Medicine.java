package model;

import java.time.LocalDate;

public class Medicine {
    public static final int EXPIRY_WINDOW_DAYS = 30;

    private final String medicineId; 
    private String batchNumber;
    private String name;
    private String category;
    private double unitPrice;
    private int quantity;
    private int minStockLevel;
    private LocalDate expirationDate;
    private boolean active;

    public Medicine(String medicineId, String batchNumber, String name, String category, double unitPrice, int quantity, int minStockLevel, LocalDate expirationDate, boolean active) {
        this.medicineId = medicineId;
        setBatchNumber(batchNumber);
        setName(name);
        setCategory(category);
        setUnitPrice(unitPrice);
        setQuantity(quantity);
        setMinStockLevel(minStockLevel);
        setExpirationDate(expirationDate);
        this.active = active;
    }
    
    public final void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public final void setCategory(String category) {
        this.category = category;
    }

    public final void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public final void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public final void setMinStockLevel(int minStockLevel) {
        this.minStockLevel = minStockLevel;
    }

    public final void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void updateDetails(String batchNumber, String name, String category, double unitPrice, int quantity, int minStockLevel, LocalDate expirationDate) {
        setBatchNumber(batchNumber);
        setName(name);
        setCategory(category);
        setUnitPrice(unitPrice);
        setQuantity(quantity);
        setMinStockLevel(minStockLevel);
        setExpirationDate(expirationDate);
    }

    public void deactivate() {
        this.active = false;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isExpired() {
        return expirationDate.isBefore(LocalDate.now());
    }

    public boolean isSellable() {
        return active && quantity > 0 && !isExpired();
    }

    public boolean isOutOfStock() {
        return active && quantity == 0;
    }

    public boolean isLowStock() {
        return active && quantity > 0 && quantity < minStockLevel;
    }

    public boolean isNearExpiry(int windowDays) {
        return active && !isExpired() && !expirationDate.isAfter(LocalDate.now().plusDays(windowDays));
    }

    public StockStatus availabilityStatus() {
        if(!active) return StockStatus.INACTIVE;
        if(isExpired()) return StockStatus.EXPIRED;
        if(isOutOfStock()) return StockStatus.OUT_OF_STOCK;
        if(isLowStock()) return StockStatus.LOW_STOCK;
        return StockStatus.IN_STOCK;
    }

    public String getMedicineId() {
        return medicineId;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getMinStockLevel() {
        return minStockLevel;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }
}
