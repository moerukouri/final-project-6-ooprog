package ui;

public class Menu {
    public static void admin() {
        Display.title("ADMIN DASHBOARD");
        
        Display.println("1. Manage User Accounts");
        Display.println("2. Manage Inventory");
        Display.println("3. Manage Sales");
        Display.println("4. Record Transaction");
        Display.println("5. Change My Password");
        Display.println("6. Logout");
    }

    public static void staff() {
        Display.title("STAFF DASHBOARD");

        Display.println("1. Record Transaction");
        Display.println("2. Search Medicine");
        Display.println("3. View All Medicine");
        Display.println("4. Search Own Sale");
        Display.println("5. View Own Sales");
        Display.println("6. Change My Password");
        Display.println("7. Logout");
    }

    public static void manageUsers() {
        Display.title("MANAGE USER ACCOUNTS");

        Display.println("1. View All Users");
        Display.println("2. Add User");
        Display.println("3. Update User");
        Display.println("4. Delete User");
        Display.println("5. Search User");
        Display.println("6. Update My Account");
        Display.println("7. Back");
    }

    public static void manageMedicine() {
        Display.title("MANAGE INVENTORY");

        Display.println("1. View All Medicine");
        Display.println("2. Add Medicine");
        Display.println("3. Update Medicine");
        Display.println("4. Delete Medicine");
        Display.println("5. Search Medicine");
        Display.println("6. Back");
    }

    public static void manageSales() {
        Display.title("MANAGE SALES");

        Display.println("1. View All Sales");
        Display.println("2. View Own Sales");
        Display.println("3. Search Sale");
        Display.println("4. Generate Report");
        Display.println("5. Record Transaction");
        Display.println("6. Back");
    }
}