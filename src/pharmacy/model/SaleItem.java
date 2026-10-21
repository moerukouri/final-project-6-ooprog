package model;

public class SaleItem {
    private final String medicineId;
    private final String medicineName;
    private final double unitPrice;
    private final int quantity;

    public SaleItem(String medicineId, String medicineName, double unitPrice, int quantity) {
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public String getMedicineId() {
        return medicineId;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getSubtotal() {
        return unitPrice * quantity;
    }
}