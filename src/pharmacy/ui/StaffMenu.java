package ui;

import strategy.MenuStrategy;

public class StaffMenu implements MenuStrategy {
    @Override 
    public void dashboard() {
        Display.title("STAFF DASHBOARD");

        Display.println("1. Record Transaction");
        Display.println("2. Search Medicine");
        Display.println("3. View All Medicine");
        Display.println("4. Search Own Sale");
        Display.println("5. View Own Sales");
        Display.println("6. Change My Password");
        Display.println("0. Logout");
    }
}