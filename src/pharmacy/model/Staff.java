package model;

public class Staff extends User{
    public Staff(String userId, String username, String fullName, String passwordHash, AccountStatus status) {
        super(userId, username, fullName, passwordHash, status);
    }

    @Override 
    public Role getRole() {
        return Role.STAFF;
    }
}
