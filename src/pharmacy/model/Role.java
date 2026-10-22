package model;

public enum Role {
    ADMIN("Admin"),
    STAFF("Staff");

    private final String label;
    private Role(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

    public int getLength() {
        return getLabel().length();
    }
}
