package io;

import exception.FileStorageException;
import java.io.File;
import java.io.IOException;

public class FileCreator {
    private FileCreator() {} 
    private final FileCreator INSTANCE = new FileCreator();
    private final String operation = Operation.CREATE.getOperation();

    private final String usersPath = FilePath.USERS_PATH.getFilePath();
    private final String salesPath = FilePath.SALES_PATH.getFilePath();
    private final String medicinePath = FilePath.MEDICINE_PATH.getFilePath();
    private final String auditPath = FilePath.AUDIT_PATH.getFilePath();

    private final File usersFile = new File(usersPath);
    private final File salesFile = new File(salesPath);
    private final File medicineFile = new File(medicinePath);
    private final File auditFile = new File(auditPath);

    public FileCreator getInstance() {
        return INSTANCE;
    }

    public void createUsersFile() {
        try {
            if (usersFile.createNewFile()) {
                
            }
        } catch (IOException e) {
            throw new FileStorageException(operation, usersPath);
        }
    }

    public void createSalesFile() {
        try {
            if (salesFile.createNewFile()) {

            }
        } catch (IOException e) {
            throw new FileStorageException(operation, salesPath);
        }
    }

    public void createMedicineFile() {
        try {
            if (medicineFile.createNewFile()) {

            }
        } catch (IOException e) {
            throw new FileStorageException(operation, medicinePath);
        }
    }

    public void createAuditFile() {
        try {
            if (auditFile.createNewFile()) {

            }
        } catch (IOException e) {
            throw new FileStorageException(operation, auditPath);
        }
    }
}