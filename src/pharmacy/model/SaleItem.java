package model;

import util.Validate;

public class SaleItem {
    private final String medicineId;
    private final String medicineName;
    private final double unitPrice;
    private final int quantity;

    public SaleItem(String medicineId, String medicineName, double unitPrice, int quantity) {
        this.medicineId = medicineId;
        this.medicineName = Validate.requireText(medicineName, "Medicine name");
        this.unitPrice = Validate.requireNonNegativeFinite(unitPrice, "Unit price");
        this.quantity = Validate.requirePositiveInt(quantity, "Sale quantity");
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