package io;

import exception.FileStorageException;
import java.io.File;
import java.io.IOException;

public class FileCreator {
    private FileCreator() {} 
    private static final FileCreator INSTANCE = new FileCreator();
    private final String operation = Operation.CREATE.getOperation();

    private final String usersPath = FilePath.USERS_PATH.getFilePath();
    private final String salesPath = FilePath.SALES_PATH.getFilePath();
    private final String medicinePath = FilePath.MEDICINE_PATH.getFilePath();
    private final String auditPath = FilePath.AUDIT_PATH.getFilePath();

    private final File usersFile = new File(usersPath);
    private final File salesFile = new File(salesPath);
    private final File medicineFile = new File(medicinePath);
    private final File auditFile = new File(auditPath);

    public static FileCreator getInstance() {
        return INSTANCE;
    }

    public void createUsersFile() {
        try {
            usersFile.getParentFile().mkdirs();
            usersFile.createNewFile();
        } catch (IOException e) {
            throw new FileStorageException(operation, usersPath);
        }
    }

    public void createSalesFile() {
        try {
            salesFile.getParentFile().mkdirs();
            salesFile.createNewFile();
        } catch (IOException e) {
            throw new FileStorageException(operation, salesPath);
        }
    }

    public void createMedicineFile() {
        try {
            medicineFile.getParentFile().mkdirs();
            medicineFile.createNewFile();
        } catch (IOException e) {
            throw new FileStorageException(operation, medicinePath);
        }
    }

    public void createAuditFile() {
        try {
            auditFile.getParentFile().mkdirs();
            auditFile.createNewFile();
        } catch (IOException e) {
            throw new FileStorageException(operation, auditPath);
        }
    }
}