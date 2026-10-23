package model;

import strategy.AccessStrategy;
import strategy.StaffAccess;

public class Staff extends User{
    public Staff(String userId, String username, String fullName, String password, AccountStatus status) {
        super(userId, username, fullName, password, status);
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
