package model;

public abstract class User {
    private final String userId;
    private String username;
    private String fullName;
    private String passwordHash;
    private AccountStatus status;

    protected User(String userId, String username, String fullName, String passwordHash, AccountStatus status) {
        this.userId = userId;
        setUsername(username);
        setFullName(fullName);
        setPasswordHash(passwordHash);
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

    public String passwordHash() {
        return passwordHash;
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

    public final void setStatus(AccountStatus status) {
        this.status = status == null ? AccountStatus.ACTIVE : status;
    }

    public boolean isActive() {
        return status == AccountStatus.ACTIVE;
    }

    public abstract Role getRole();

    @Override 
    public String toString() {
        return username + " (" + getRole() + ", " + ")";
    }
}
