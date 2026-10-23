package ui;

public enum TableField {
    USERS(new String[] {"USER ID", "USERNAME", "FULL NAME", "ROLE", "STATUS"}),
    MEDICINE(new String[]{"MEDICINE ID", "BATCH NUMBER", "NAME", "CATEGORY", "UNIT PRICE", "STOCK", "EXPIRATION DATE", "STATUS"}),
    SALES(new String[]{"SALE ID", "TIMESTAMP", "CASHIER ID", "TOTAL AMOUNT"});

    private final String[] fields;
    private TableField(String[] fields) {
        this.fields = fields;
    }

    public String[] getFields() {
        return this.fields;
    }
}