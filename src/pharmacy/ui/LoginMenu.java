package ui;

import strategy.MenuStrategy;

public class LoginMenu implements MenuStrategy {
    @Override 
    public void dashboard() {
        Display.title("PHARMACY MANAGEMENT SYSTEM");

        Display.println("1. Login");
        Display.println("0. Exit Program");
    }
}