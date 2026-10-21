package model;

public class Admin extends User{
    public Admin(String userId, String username, String passwordHash, AccountStatus status) {
        super(userId, username, passwordHash, status);
    }

    @Override 
    public Role getRole() {
        return Role.ADMIN;
    }
}
