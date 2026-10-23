import exception.AuthenticationException;
import model.AccountStatus;
import model.Admin;
import model.Role;
import model.User;
import repository.MedicineRepository;
import repository.SaleRepository;
import repository.UserRepository;
import services.AuthService;
import services.AuditService;
import services.MedicineService;
import services.ReportService;
import services.SaleService;
import services.UserService;
import ui.AdminMenu;
import ui.Display;
import ui.LoginMenu;
import ui.StaffMenu;
import util.Input;

public class PharmacySystem {

    private final UserRepository users;
    private final MedicineRepository medicines;
    private final SaleRepository sales;
    private final AuditService audit;
    private final AuthService authService;
    private final UserService userService;
    private final MedicineService medicineService;
    private final SaleService saleService;
    private final Input input;

    public PharmacySystem() {
        this.users = new UserRepository();
        this.medicines = new MedicineRepository();
        this.sales = new SaleRepository();

        this.audit = new AuditService();
        this.authService = new AuthService(users);
        this.userService = new UserService(users, audit);
        this.medicineService = new MedicineService(medicines);
        this.saleService = new SaleService(sales, medicines, new ReportService(medicines, sales), audit);

        this.input = Input.getInstance();
    }

    public static void main(String[] args) {
        PharmacySystem system = new PharmacySystem();
        system.run();
    }

    private void run() {
        seedDefaultAdminIfEmpty();

        boolean running = true;
        while (running) {
            new LoginMenu().dashboard();
            int choice = input.readChoice(0, 1);

            if (choice == 0) {
                Display.feedback("Goodbye!");
                running = false;
                continue;
            }

            User user = login();
            if (user == null) {
                continue;
            }
            runDashboard(user);
        }
    }

    private User login() {
        Display.title("LOGIN");
        String username = input.readUsername();
        String password = input.readPassword("Input Password: ");

        try {
            User user = authService.login(username, password);
            Display.feedback("Welcome, " + user.getFullName() + " (" + user.getRole().getLabel() + ")!");
            return user;
        } catch (AuthenticationException e) {
            Display.feedback(e.getMessage());
            return null;
        }
    }

    private void runDashboard(User user) {
        boolean logged = true;
        while (logged) {
            logged = (user.getRole() == Role.ADMIN) ? adminDashboard(user) : staffDashboard(user);
        }
    }

    private boolean adminDashboard(User user) {
        new AdminMenu().dashboard();
        int choice = input.readChoice(0, 5);

        switch (choice) {
            case 1 -> userService.menu(user);
            case 2 -> medicineService.menu(user);
            case 3 -> saleService.menu(user);
            case 4 -> saleService.recordTransaction(user);
            case 5 -> userService.changeOwnPassword(user);
            case 0 -> Display.feedback("You have been logged out. Goodbye, " + user.getUsername() + "!");
        }
        return choice != 0;
    }

    private boolean staffDashboard(User user) {
        new StaffMenu().dashboard();
        int choice = input.readChoice(0, 6);

        switch (choice) {
            case 1 -> saleService.recordTransaction(user);
            case 2 -> medicineService.search(user);
            case 3 -> medicineService.viewAll(user);
            case 4 -> saleService.searchSale(user, true);
            case 5 -> saleService.viewOwnSales(user);
            case 6 -> userService.changeOwnPassword(user);
            case 0 -> Display.feedback("You have been logged out. Goodbye, " + user.getUsername() + "!");
        }
        return choice != 0;
    }

    private void seedDefaultAdminIfEmpty() {
        if (!users.findAll().isEmpty()) {
            return;
        }
        Admin admin = new Admin(users.nextUserId(), "admin", "Default Administrator", "admin123", AccountStatus.ACTIVE);
        users.add(admin);
        Display.feedback(
                "No users found. A default administrator account was created.\n"
                + "Username: admin\n"
                + "Password:  admin123\n"
                + "\n"
                + "Please log in and change this password."
        );
    }
}