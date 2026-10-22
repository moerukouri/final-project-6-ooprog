package ui;

public enum MedicineIndex {
    ID(0),
    BATCH_NUMBER(1),
    NAME(2),
    CATEGORY(3),
    UNIT_PRICE(4),
    STOCK(5),
    EXPIRATION_DATE(6);

    private final int index;
    private MedicineIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }
}