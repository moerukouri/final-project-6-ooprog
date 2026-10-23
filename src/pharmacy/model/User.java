package model;

import strategy.AccessStrategy;
import util.Validate;

public abstract class User {
    private final String userId;
    private String username;
    private String fullName;
    private String password;
    private AccountStatus status;

    protected User(String userId, String username, String fullName, String password, AccountStatus status) {
        this.userId = userId;
        setUsername(username);
        setFullName(fullName);
        setPassword(password);
        setStatus(status);
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPassword() {
        return password;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public final void setUsername(String username) {
        this.username = Validate.requireText(username, "Username");
    }

    public final void setFullName(String fullName) {
        this.fullName = Validate.requireText(fullName, "Full name");
    }

    public final void setPassword(String password) {
        this.password = Validate.requireText(password, "Password");
    }

    public final void setStatus(AccountStatus status) {
        this.status = status == null ? AccountStatus.ACTIVE : status;
    }

    public boolean isActive() {
        return status == AccountStatus.ACTIVE;
    }

    public boolean checkPassword(String candidate) {
        return candidate != null && candidate.equals(password);
    }

    public abstract Role getRole();

    public abstract AccessStrategy accessStrategy();

    @Override 
    public String toString() {
        return fullName + " - " + username + " (" + getRole() + ", " + ")";
    }
}
