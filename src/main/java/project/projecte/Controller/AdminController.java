package project.projecte.Controller;

import project.projecte.Model.*;
import java.time.LocalDate;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AdminController {

    private final UserManager userManager;
    private final Inventory inventory;
    private final EmployeeManager employeeManager;

    public AdminController() {
        this.userManager = new UserManager();
        this.inventory = new Inventory();
        this.employeeManager = new EmployeeManager();
        userManager.loadUsers();
        inventory.loadInventory();
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public EmployeeManager getEmployeeManager() {
        return employeeManager;
    }

    // 1. Add User
    public void addUser(Stage stage) {
        Stage addUserWindow = new Stage();
        addUserWindow.setTitle("Add User");

        Label header = createHeader("Add a New User");

        TextField usernameField = createTextField("Enter username");
        PasswordField passwordField = createPasswordField("Enter password");
        TextField roleField = createTextField("Enter role (Cashier, Manager, Admin)");
        
        // Employee information fields
        TextField nameField = createTextField("Enter full name");
        DatePicker dobPicker = new DatePicker();
        dobPicker.setPromptText("Date of Birth");
        TextField phoneField = createTextField("Enter phone number");
        TextField emailField = createTextField("Enter email");
        TextField salaryField = createTextField("Enter salary");

        Button submitButton = createButton("Submit", "#4CAF50");
        submitButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String role = roleField.getText();
            String name = nameField.getText();
            java.time.LocalDate dob = dobPicker.getValue();
            String phone = phoneField.getText();
            String email = emailField.getText();
            String salaryText = salaryField.getText();

            if (username.isEmpty() || password.isEmpty() || role.isEmpty() || name.isEmpty() || 
                dob == null || phone.isEmpty() || email.isEmpty() || salaryText.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Missing Information", "All fields are required.");
                return;
            }

            // Check if username already exists
            if (userManager.findUserByUsername(username) != null) {
                showAlert(Alert.AlertType.ERROR, "Username Exists", "A user with this username already exists.");
                return;
            }

            try {
                double salary = Double.parseDouble(salaryText);

                User newUser = switch (role.toLowerCase()) {
                    case "cashier" -> new Cashier(username, password, "DefaultSector");
                    case "manager" -> new Manager(username, password, inventory, null, null);
                    case "admin" -> new Admin(username, password, inventory);
                    default -> null;
                };

                if (newUser != null) {
                    userManager.addUser(newUser);
                    userManager.saveUsers();
                    
                    // Create corresponding Employee record
                    Employee newEmployee = new Employee(name, username, dob, phone, email, salary, role);
                    employeeManager.addEmployee(newEmployee);
                    
                    showAlert(Alert.AlertType.INFORMATION, "User Added", "User and employee record created successfully.");
                    addUserWindow.close();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Invalid Role", "Enter a valid role (Cashier, Manager, or Admin).");
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Salary must be a valid number.");
            }
        });

        VBox layout = new VBox(15, header, usernameField, passwordField, roleField, nameField, dobPicker, phoneField, emailField, salaryField, submitButton);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #F1ECFF;");
        addUserWindow.setScene(new Scene(layout, 500, 600));
        addUserWindow.show();
    }

    // 2. Update User
    public void updateUser(Stage stage) {
        Stage updateUserWindow = new Stage();
        updateUserWindow.setTitle("Update User");

        Label header = createHeader("Update User Details");

        TextField usernameField = createTextField("Enter current username");
        TextField newUsernameField = createTextField("Enter new username");
        PasswordField newPasswordField = createPasswordField("Enter new password");
        TextField newRoleField = createTextField("Enter new role (Cashier, Manager, Admin)");
        
        // Employee information fields
        TextField newNameField = createTextField("Enter new full name");
        DatePicker newDobPicker = new DatePicker();
        newDobPicker.setPromptText("New Date of Birth");
        TextField newPhoneField = createTextField("Enter new phone number");
        TextField newEmailField = createTextField("Enter new email");
        TextField newSalaryField = createTextField("Enter new salary");

        Button updateButton = createButton("Update", "#C9B2FF");
        updateButton.setOnAction(event -> {
            String username = usernameField.getText();
            String newUsername = newUsernameField.getText();
            String newPassword = newPasswordField.getText();
            String newRole = newRoleField.getText();
            String newName = newNameField.getText();
            java.time.LocalDate newDob = newDobPicker.getValue();
            String newPhone = newPhoneField.getText();
            String newEmail = newEmailField.getText();
            String newSalaryText = newSalaryField.getText();

            if (username.isEmpty() || newUsername.isEmpty() || newPassword.isEmpty() || newRole.isEmpty() ||
                newName.isEmpty() || newDob == null || newPhone.isEmpty() || newEmail.isEmpty() || newSalaryText.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Missing Information", "All fields are required.");
                return;
            }

            // Check if new username already exists (and it's not the current user's username)
            if (!username.equals(newUsername) && userManager.findUserByUsername(newUsername) != null) {
                showAlert(Alert.AlertType.ERROR, "Username Exists", "A user with this username already exists.");
                return;
            }

            User user = userManager.findUserByUsername(username);
            if (user != null) {
                try {
                    double newSalary = Double.parseDouble(newSalaryText);
                    
                    // Update User
                    userManager.removeUser(user);
                    User updatedUser = switch (newRole.toLowerCase()) {
                        case "cashier" -> new Cashier(newUsername, newPassword, "DefaultSector");
                        case "manager" -> new Manager(newUsername, newPassword, inventory, null, null);
                        case "admin" -> new Admin(newUsername, newPassword, inventory);
                        default -> null;
                    };
                    
                    if (updatedUser != null) {
                        userManager.addUser(updatedUser);
                        userManager.saveUsers();
                        
                        // Update corresponding Employee
                        Employee employee = employeeManager.findEmployeeByUsername(username);
                        if (employee != null) {
                            employee.setUsername(newUsername);
                            employee.setName(newName);
                            employee.setDateOfBirth(newDob);
                            employee.setPhoneNumber(newPhone);
                            employee.setEmail(newEmail);
                            employee.setSalary(newSalary);
                            employee.setAccessLevel(newRole);
                            employeeManager.saveEmployees();
                        }
                        
                        showAlert(Alert.AlertType.INFORMATION, "User Updated", "User and employee record updated successfully.");
                        updateUserWindow.close();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Invalid Role", "Enter a valid role (Cashier, Manager, or Admin).");
                    }
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input", "Salary must be a valid number.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "User Not Found", "No user found with the specified username.");
            }
        });

        VBox layout = new VBox(15, header, usernameField, newUsernameField, newPasswordField, newRoleField, 
                               newNameField, newDobPicker, newPhoneField, newEmailField, newSalaryField, updateButton);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #F1ECFF;");
        updateUserWindow.setScene(new Scene(layout, 500, 700));
        updateUserWindow.show();
    }

    // 3. Delete User
    public void deleteUser(Stage stage) {
        Stage deleteUserWindow = new Stage();
        deleteUserWindow.setTitle("Delete User");

        Label header = createHeader("Delete a User");

        TextField usernameField = createTextField("Enter username to delete");

        Button deleteButton = createButton("Delete", "#F44336");
        deleteButton.setOnAction(event -> {
            String username = usernameField.getText();
            User user = userManager.findUserByUsername(username);

            if (user != null) {
                // Delete corresponding Employee record
                Employee employee = employeeManager.findEmployeeByUsername(username);
                if (employee != null) {
                    employeeManager.removeEmployee(employee);
                }
                
                // Delete User
                userManager.removeUser(user);
                userManager.saveUsers();
                showAlert(Alert.AlertType.INFORMATION, "User Deleted", "User and employee record removed successfully.");
                deleteUserWindow.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "User Not Found", "No user found with the specified username.");
            }
        });

        VBox layout = new VBox(15, header, usernameField, deleteButton);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #F1ECFF;");
        deleteUserWindow.setScene(new Scene(layout, 400, 200));
        deleteUserWindow.show();
    }

    // 4. View Financials
    public void viewFinancials(Stage stage) {
        Stage financialsWindow = new Stage();
        financialsWindow.setTitle("Financial Overview");

        Label header = createHeader("Financial Overview");

        // Ensure inventory is loaded
        inventory.loadInventory();
        
        System.out.println("DEBUG AdminController.viewFinancials: Inventory has " + inventory.getItems().size() + " items");

        double totalIncome = inventory.getItems().stream()
                .mapToDouble(item -> item.getSellingPrice() * item.getStockLevel())
                .sum();
        double totalCosts = inventory.getItems().stream()
                .mapToDouble(item -> item.getPurchasePrice() * item.getStockLevel())
                .sum();
        double profit = totalIncome - totalCosts;
        
        System.out.println("DEBUG AdminController.viewFinancials: Income=$" + totalIncome + " Costs=$" + totalCosts + " Profit=$" + profit);

        // Create table with lambda expressions instead of PropertyValueFactory
        TableView<FinancialController> tableView = new TableView<>();
        
        TableColumn<FinancialController, String> metricColumn = new TableColumn<>("Metric");
        metricColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getMetric()));
        metricColumn.setPrefWidth(200);
        
        TableColumn<FinancialController, String> amountColumn = new TableColumn<>("Amount ($)");
        amountColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAmount()));
        amountColumn.setPrefWidth(200);
        
        tableView.getColumns().addAll(metricColumn, amountColumn);

        tableView.getItems().addAll(
                new FinancialController("Total Income", totalIncome),
                new FinancialController("Total Costs", totalCosts),
                new FinancialController("Profit", profit)
        );
        
        System.out.println("DEBUG AdminController.viewFinancials: Table has " + tableView.getItems().size() + " rows");

        VBox layout = new VBox(20, header, tableView);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #F1ECFF;");
        financialsWindow.setScene(new Scene(layout, 500, 400));
        financialsWindow.show();
    }

    // Helper Methods
    private Label createHeader(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        return label;
    }

    private TextField createTextField(String placeholder) {
        TextField textField = new TextField();
        textField.setPromptText(placeholder);
        return textField;
    }

    private PasswordField createPasswordField(String placeholder) {
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(placeholder);
        return passwordField;
    }

    private Button createButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px;", color));
        return button;
    }

    // Removed createColumn method - no longer needed with lambda expressions
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null); // Nuk ka header
        alert.setContentText(message);
        alert.showAndWait();
    }

}
