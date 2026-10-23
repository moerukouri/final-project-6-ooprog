package services;

import java.time.format.DateTimeFormatter;
import io.FileCreator;
import io.FileHandler;
import io.FilePath;
import java.time.LocalDateTime;

public class AuditService {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public AuditService() {
        FileCreator.getInstance().createAuditFile();
    }

    public void log(String entry) {
        String line =  LocalDateTime.now().format(TIME_FORMAT) + "|" + entry;
        FileHandler.write(line, FilePath.AUDIT_PATH.getFilePath(), true);
    }
}
