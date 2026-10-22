package model;

import strategy.AccessStrategy;
import strategy.StaffAccess;

public class Staff extends User{
    public Staff(String userId, String username, String fullName, String passwordHash, String salt, AccountStatus status) {
        super(userId, username, fullName, passwordHash, salt, status);
    }

    @Override 
    public Role getRole() {
        return Role.STAFF;
    }

    @Override
    public AccessStrategy accessStrategy() {
        return new StaffAccess();
    }
}
