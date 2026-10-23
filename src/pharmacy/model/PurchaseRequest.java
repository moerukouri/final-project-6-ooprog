package model;

import util.Validate;

public class PurchaseRequest {
    private final String medicineId;
    private final int quantity;

    public PurchaseRequest(String medicineId, int quantity) {
        this.medicineId = medicineId;
        this.quantity = Validate.requirePositiveInt(quantity, "Requested quantity");
    }

    public String getMedicineId() {
        return medicineId;
    }

    public int getQuantity() {
        return quantity;
    }
}
