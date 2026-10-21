package model;

public class Admin extends User{
    public Admin(String userId, String username, String fullName, String passwordHash, AccountStatus status) {
        super(userId, username, fullName, passwordHash, status);
    }

    @Override 
    public Role getRole() {
        return Role.ADMIN;
    }
}
