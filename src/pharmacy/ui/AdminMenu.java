package ui;

import strategy.MenuStrategy;

public class AdminMenu implements MenuStrategy {
    @Override 
    public void dashboard() {
        Display.title("ADMIN DASHBOARD");
        
        Display.println("1. Manage User Accounts");
        Display.println("2. Manage Inventory");
        Display.println("3. Manage Sales");
        Display.println("4. Record Transaction");
        Display.println("5. Change My Password");
        Display.println("0. Logout");
    }

    public void manageUsers() {
        Display.title("MANAGE USER ACCOUNTS");

        Display.println("1. View All Users");
        Display.println("2. Add User");
        Display.println("3. Update User");
        Display.println("4. Delete User");
        Display.println("5. Search User");
        Display.println("0. Back");
    }

    public void manageMedicine() {
        Display.title("MANAGE INVENTORY");

        Display.println("1. View All Medicine");
        Display.println("2. Add Medicine");
        Display.println("3. Update Medicine");
        Display.println("4. Delete Medicine");
        Display.println("5. Search Medicine");
        Display.println("6. Activate / Deactivate Medicine");
        Display.println("0. Back");
    }

    public void manageSales() {
        Display.title("MANAGE SALES");

        Display.println("1. View All Sales");
        Display.println("2. View Own Sales");
        Display.println("3. Search Sale");
        Display.println("4. Generate Report");
        Display.println("5. Record Transaction");
        Display.println("6. Void Sale");
        Display.println("0. Back");
    }
}