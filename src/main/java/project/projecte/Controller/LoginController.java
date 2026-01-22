package project.projecte.Controller;

import project.projecte.Model.*;
import project.projecte.View.*;
import project.projecte.Controller.*;
import javafx.stage.Stage;
import java.util.List;

public class LoginController {
    private final UserManager userManager;
    private final Stage primaryStage;

    public LoginController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.userManager = new UserManager();
        this.userManager.loadUsers();
    }

    public void handleLogin(String username, String password, LoginView loginView) {
        User user = userManager.findUserByUsername(username);

        if (user != null && user.getPassword().equals(password)) {
            loginView.showError(""); // Clear error message
            navigateToDashboard(user);
        } else {
            loginView.showError("Invalid login credentials.");
        }
    }

    private void navigateToDashboard(User user) {
        switch (user.getRole()) {
            case "Administrator" -> {
                AdminController adminController = new AdminController();
                new AdministratorView(adminController).display(primaryStage);
            }
            case "Manager" -> {
                // Load inventory
                Inventory inventory = new Inventory();
                inventory.loadInventory();
                System.out.println("DEBUG LoginController: Loaded " + inventory.getItems().size() + " items");
                
                // Load all cashiers from the user manager
                List<Cashier> cashiers = userManager.getUsers().stream()
                        .filter(u -> u.getRole().equals("Cashier"))
                        .map(u -> (Cashier) u)
                        .toList();
                
                System.out.println("DEBUG LoginController: Found " + cashiers.size() + " cashiers");

                // Load all bills and distribute them to the cashiers
                BillManager billManager = new BillManager();
                List<Bill> allBills = billManager.getBills();
                System.out.println("DEBUG LoginController: Loaded " + allBills.size() + " bills");
                
                for (Cashier cashier : cashiers) {
                    cashier.getBills().clear();
                    List<Bill> cashierBills = allBills.stream()
                            .filter(b -> b.getCashierUsername() != null
                                    && b.getCashierUsername().equalsIgnoreCase(cashier.getUsername()))
                            .toList();
                    cashier.getBills().addAll(cashierBills);
                    System.out.println("DEBUG LoginController: Cashier " + cashier.getUsername() + " has " + cashierBills.size() + " bills");
                }

                Manager manager = new Manager(user.getUsername(), user.getPassword(), inventory, cashiers, null);
                new ManagerView(manager).showManagerDashboard(primaryStage);
            }
            case "Cashier" -> {
                Inventory inventory = new Inventory();
                inventory.loadInventory();
                System.out.println("DEBUG LoginController: Cashier " + user.getUsername() + " logging in");
                new CashierView(inventory, user.getUsername()).showCashierDashboard(primaryStage);
            }
        }
    }
}
