package repository;

import io.FileCreator;
import io.FileHandler;
import io.FilePath;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import model.AccountStatus;
import model.Admin;
import model.Role;
import model.Staff;
import model.User;

public class UserRepository {
    private static final int FIELD_COUNT = 7;
    private static final String USERS_PATH = FilePath.USERS_PATH.getFilePath();

    public UserRepository() {
        FileCreator.getInstance().createUsersFile();
    }

    public List<User> findAll() { 
        List<User> users = new ArrayList<>();
        for(String line : FileHandler.retrieve(USERS_PATH)) {
            if (line.isBlank() || line.startsWith("#")){
                continue;
            }
            try {
                users.add(parse(line));
            } catch (Exception e) { // ADD EXCEPTION
                System.err.println("[warn] Skipping malformed user record: " + line);
            }
        }
        return users;
    }

    public Optional<User> findById(String userId) {
        String line = FileHandler.retrieveLine(userId, 0, USERS_PATH);
        return line == null ? Optional.empty() : Optional.of(parse(line));
    }

    public Optional<User> findByUsername(String username) {
        for (User user : findAll()) {
            if (user.getUsername().equalsIgnoreCase(username.trim())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public String nextUserId() {
        int max = 0;
        for (User user : findAll()){
            max = Math.max(max, numericSuffix(user.getUserId()));
        }
        return String.format("U%04d", max + 1);
    }

    public void add (User user) {
        FileHandler.write(serialize(user), USERS_PATH, true);
    }

    public void replace(String userId, User replacement) {
        String oldLine = FileHandler.retrieveLine(userId, 0, USERS_PATH);
        if  (oldLine == null || !FileHandler.update(oldLine, serialize(replacement), USERS_PATH)){
            throw new IllegalArgumentException("User" + userId + "not found."); // ADD EXCEPTION
        }
    }

    private static int numericSuffix(String id) {
        try {
            return Integer.parseInt(id.substring(1));
        } catch (Exception e) { //ADD EXCEPTION
            return 0;
        }
    }

    private static User parse(String line) {
        String[] f = line.split("\\|", -1);
        if(f.length != FIELD_COUNT) {
            throw new IllegalArgumentException("Expected " + FIELD_COUNT + " fields, got " + f.length); // ADD EXCEPTION
        }
        String userId = f[0].trim();
        String username = f[1].trim();
        String fullName = f[2].trim();
        Role role = Role.valueOf(f[3].trim().toUpperCase());
        AccountStatus status = AccountStatus.valueOf(f[4].trim().toUpperCase());
        String passwordHash = f[5];
        String salt = f[6];
        if (userId.isBlank() || username.isBlank() || fullName.isBlank() || passwordHash.isBlank() || salt.isBlank()) {
            throw new IllegalArgumentException("Empty required fields in user record."); // ADD EXCEPTION
        }
        return role == Role.ADMIN ? new Admin(userId, username, fullName, passwordHash, salt, status) : new Staff(userId, username, fullName, passwordHash, salt, status);
    }

    private static String serialize(User user) {
        return String.join("|", user.getUserId(), user.getUsername(), user.getFullName(), user.getRole().getLabel(), user.getStatus().getLabel(), user.passwordHash(), user.getSalt());
    }
}
