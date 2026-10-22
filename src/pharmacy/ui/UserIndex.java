package ui;

public enum UserIndex {
    ID(0),
    USERNAME(1),
    FULL_NAME(2),
    ROLE(3),
    STATUS(4);

    private final int index;
    private UserIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }
}