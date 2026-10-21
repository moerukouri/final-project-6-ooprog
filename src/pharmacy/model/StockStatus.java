package model;

public enum StockStatus {
    INACTIVE("Inactive"),
    EXPIRED("Expired"),
    OUT_OF_STOCK("Out of stock"),
    LOW_STOCK("Low stock"),
    IN_STOCK("In stock");

    private final String label;

    StockStatus(String label) { this.label = label; }

    public String getLabel() { return label; }
}
