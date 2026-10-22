package model;

import strategy.AccessStrategy;

public abstract class User {
    private final String userId;
    private String username;
    private String fullName;
    private String passwordHash;
    private String salt;
    private AccountStatus status;

    protected User(String userId, String username, String fullName, String passwordHash, String salt, AccountStatus status) {
        this.userId = userId;
        setUsername(username);
        setFullName(fullName);
        setPasswordHash(passwordHash);
        setSalt(salt);
        setStatus(status);
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return username;
    }

    public String passwordHash() {
        return passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public final void setUsername(String username) {
        this.username = username;
    }

    public final void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public final void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public final void setSalt(String salt) {
        this.salt = salt;
    }

    public final void setStatus(AccountStatus status) {
        this.status = status == null ? AccountStatus.ACTIVE : status;
    }

    public boolean isActive() {
        return status == AccountStatus.ACTIVE;
    }

    public boolean checkPassword(String candidate) {
        return candidate != null && PasswordHasher.verify(candidate, salt, passwordHash);
    }

    public abstract Role getRole();

    public abstract AccessStrategy accessStrategy();

    @Override 
    public String toString() {
        return fullName + " - " + username + " (" + getRole() + ", " + ")";
    }
}
