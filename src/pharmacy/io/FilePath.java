package io;

public enum FilePath {
    USERS_PATH("data/users.txt"),
    SALES_PATH("data/sales.txt"),
    MEDICINE_PATH("data/medicine.txt"),
    AUDIT_PATH("data/audit.txt");

    private final String filePath;
    private FilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return this.filePath;
    }
}