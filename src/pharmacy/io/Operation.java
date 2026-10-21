package io;

public enum Operation {
    READ("read"),
    WRITE("write"),
    RETRIEVE("retrieve"),
    CREATE("create");

    private final String operation;
    private Operation(String operation) {
        this.operation = operation;
    }

    public String getOperation() {
        return this.operation;
    }
}
