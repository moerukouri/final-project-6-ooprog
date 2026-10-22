package model;

import strategy.AccessStrategy;
import strategy.AdminAccess;

public class Admin extends User{
    public Admin(String userId, String username, String fullName, String passwordHash, String salt, AccountStatus status) {
        super(userId, username, fullName, passwordHash, salt, status);
    }

    @Override 
    public Role getRole() {
        return Role.ADMIN;
    }

    @Override
    public AccessStrategy accessStrategy() {
        return new AdminAccess();
    }
}
