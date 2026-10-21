package model;

public class Staff extends User{
    public Staff(String userId, String username, String passwordHash, AccountStatus status) {
        super(userId, username, passwordHash, status);
    }

    @Override 
    public Role getRole() {
        return Role.STAFF;
    }
}
