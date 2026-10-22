package model;

public enum AccountStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive");

    private final String label;
    private AccountStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

    public int getLength() {
        return getLabel().length();
    }
}
