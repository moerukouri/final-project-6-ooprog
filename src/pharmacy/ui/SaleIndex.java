package ui;

public enum SaleIndex {
    ID(0),
    TIMESTAMP(1),
    CASHIER_ID(2),
    TOTAL_AMOUNT(3);

    private final int index;
    private SaleIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }
}