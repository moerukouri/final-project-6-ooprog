package model;

public class PurchaseRequest {
    private final String medicineId;
    private final int quantity;

    public PurchaseRequest(String medicineId, int quantity) {
        this.medicineId = medicineId;
        this.quantity = quantity;
    }

    public String getMedicineId() {
        return medicineId;
    }

    public int getQuantity() {
        return quantity;
    }
}
