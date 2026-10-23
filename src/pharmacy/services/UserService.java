package services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import model.AccountStatus;
import model.Admin;
import model.Role;
import model.Staff;
import model.User;
import repository.UserRepository;
import strategy.Action;
import ui.AdminMenu;
import ui.Display;
import ui.Table;
import util.Input;
import util.Validate;


public class UserService {
    private final UserRepository users;
    private final AuditService audit;
    private final Input input;

    public UserService(UserRepository users, AuditService audit) {
        this.users = users;
        this.audit = audit;
        this.input = Input.getInstance();
    }

    public void menu(User user) {
        Validate.authorization(user, Action.VIEW_ALL_USERS);

        boolean running = true;
        while(running) {
            new AdminMenu().manageUsers();
            int choice = input.readChoice(0, 5);

            switch (choice) {
                case 1 -> viewAllUsers(user);
                case 2 -> addUser(user);
                case 3 -> updateUser(user);
                case 4 -> deleteUser(user);
                case 5 -> searchUser(user);
                case 0 -> running = false;
            }
        }
    }

    public void viewAllUsers(User user) {
        Validate.authorization(user, Action.VIEW_ALL_USERS);

        List<User> all = users.findAll();
        if(all.isEmpty()) {
            Display.feedback("No users registered yet.");
            return;
        }

        Display.title("ALL USERS");
        Table.users(all);
    }

    private void addUser(User user) {
        Validate.authorization(user, Action.ADD_USER);
        Display.title("ADD USER");

        String username = input.readUsername();

        if (users.findByUsername(username).isPresent()) {
            Display.feedback("Username \"" + username + "\" is already taken.");
            return;
        }

        String fullName = input.readFullName();
        String password = readPassword();
        
        Display.title("ROLES");
        Display.println("1. Administrator");
        Display.println("2. Staff");
        int roleChoice = input.readChoice(1, 2);
        Role role = roleChoice == 1 ? Role.ADMIN : Role.STAFF;

        String userId = users.nextUserId();
        User newUser = role == Role.ADMIN ? new Admin(userId, username, fullName, password, AccountStatus.ACTIVE) : new Staff(userId, username, fullName, password, AccountStatus.ACTIVE);
        users.add(newUser);
        Display.feedback("User added successfully. \nUser ID: " + userId + "\nUsername: " + username + "\nRole: " + role.getLabel());
    }

    private void updateUser(User admin) {
        Validate.authorization(admin, Action.UPDATE_USER);
        Display.title("UPDATE USER");

        String id = input.readText("Input User ID: ");
        Optional<User> found = users.findById(id);

        if (found.isEmpty()) {
            Display.feedback("User ID not found.");
            return;
        }

        User target = found.get();
        showDetails(target);
        Display.println("1. Change Full Name");
        Display.println("2. Change Password");
        Display.println("3. Change Role");
        Display.println("4. Activate / Deactivate Account");
        Display.println("0. Back");

        User updated = target;   // replaced only if the role changes
        String message;

        switch (input.readChoice(0, 4)) {
            case 1 -> {
                target.setFullName(input.readFullName());
                message = "Full name updated.";
            }
            case 2 -> {
                target.setPassword(readPassword());
                message = "Password updated.";
            }
            case 3 -> {
                updated = changeRole(target);
                message = "Role updated to " + updated.getRole().getLabel() + ".";
            }
            case 4 -> {
                target.setStatus(target.isActive() ? AccountStatus.INACTIVE : AccountStatus.ACTIVE);
                message = "Account is now " + target.getStatus().getLabel() + ".";
            }
            case 0 -> {
                return;
            }
            default -> {
                message = "Invalid choice.";
            }
        }

        users.replace(id, updated);
        Display.feedback(message);
    }

    private User changeRole(User target) {
        Display.println("1. Admin");
        Display.println("2. Staff");

        boolean admin = input.readChoice(1, 2) == 1;

        return admin ? new Admin(target.getUserId(), target.getUsername(), target.getFullName(), target.getPassword(), target.getStatus()) : new Staff(target.getUserId(), target.getUsername(), target.getFullName(), target.getPassword(), target.getStatus());
    }

    private void deleteUser(User admin) {
        Validate.authorization(admin, Action.DELETE_USER);
        Display.title("DELETE USER");

        String id = input.readText("Input User ID: ");
        
        if (id.equals(admin.getUserId())) {
            Display.feedback("You cannot delete your own account.");
            return;
        }

        User target = users.findById(id).orElse(null);

        if (target == null) {
            Display.feedback("User ID not found.");
            return;
        }

        showDetails(target);

        String confirm = input.readText("Are you sure you want to delete this user? (y/n)");
        if (!confirm.equalsIgnoreCase("y")) {
            Display.feedback("Delete cancelled.");
            return;
        }

        users.removeById(id);
        audit.log("User " + id + " (" + target.getUsername() + ") deleted by " + admin.getUserId() + ".");
        Display.feedback("User " + id + " has been deleted.");
    }

    private void searchUser(User admin) {
        Validate.authorization(admin, Action.SEARCH_USER);
        Display.title("SEARCH USER");

        String username = input.readUsername();
        User target = users.findByUsername(username).orElse(null);

        if (target == null) {
            Display.feedback("No user found with username \"" + username + "\".");
            return;
        }

        List<User> result = new ArrayList<>();
        result.add(target);
        Table.users(result);
    }

    public void changeOwnPassword(User user) {
        Validate.authorization(user, Action.CHANGE_OWN_PASSWORD);
        Display.title("CHANGE MY PASSWORD");

        String current = input.readPassword("Input Current Password");

        if (!user.checkPassword(current)) {
            Display.feedback("Current password is incorrect.");
            return;
        }

        String newPassword = readPassword();
        String confirm = input.readPassword("Confirm New Password");

        if (!newPassword.equals(confirm)) {
            Display.feedback("New passwords do not match.");
            return;
        }

        user.setPassword(newPassword);
        users.replace(user.getUserId(), user);
        Display.feedback("Your password has been changed.");
    }

    private String readPassword() {
        String password = promptPassword();

        while (password.length() < 4) {
            Display.feedback("Password must be at least 4 characters long.");
            password = promptPassword();
        }

        return password;
    }

    private String promptPassword() {
        return input.readPassword("Input Password");
    }

    private void showDetails(User user) {
        Display.title("USER DETAILS");
        Display.println("User ID:   " + user.getUserId());
        Display.println("Username:  " + user.getUsername());
        Display.println("Full Name: " + user.getFullName());
        Display.println("Role:      " + user.getRole().getLabel());
        Display.println("Status:    " + user.getStatus().getLabel());
    }
}
