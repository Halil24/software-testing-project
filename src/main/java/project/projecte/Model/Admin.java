package project.projecte.Model;

import java.util.ArrayList;
import java.util.List;

public class Admin extends User {
    private List<User> users; // List of all users managed by the admin
    private Inventory inventory; // Reference to the inventory for financial calculations
    private static final double DEFAULT_SALARY = 3000.0; // Default salary for demonstration

    public Admin(String username, String password, Inventory inventory) {
        super(username, password, "Administrator");
        this.users = new ArrayList<>();
        this.inventory = inventory;
    }

    // Add a new user (Cashier or Manager)
    public void addUser(User user) {
        users.add(user);
        System.out.println("User added: " + user.getUsername());
    }

    // Remove a user by username
    public void removeUser(String username) {
        User userToRemove = findUserByUsername(username);
        if (userToRemove != null) {
            users.remove(userToRemove);
            System.out.println("User removed: " + username);
        } else {
            System.out.println("User not found: " + username);
        }
    }

    // Find a user by username
    private User findUserByUsername(String username) {
        return users.stream()
                .filter(user -> user.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
    }

    // Display all users
    public void displayUsers() {
        System.out.println("Users:");
        for (User user : users) {
            System.out.println("- " + user.getUsername() + " (" + user.getRole() + ")");
        }
    }

    // Calculate total income from sales
    public double calculateTotalIncome() {
        return inventory.getItems().stream()
                .mapToDouble(item -> (item.getSellingPrice() - item.getPurchasePrice()) * (item.getStockLevel()))
                .sum();
    }

    // Calculate total costs (items + user salaries)
    public double calculateTotalCosts() {
        double totalItemCosts = inventory.getItems().stream()
                .mapToDouble(item -> item.getPurchasePrice() * item.getStockLevel())
                .sum();

        double totalSalaryCosts = users.size() * DEFAULT_SALARY;
        return totalItemCosts + totalSalaryCosts;
    }


}
