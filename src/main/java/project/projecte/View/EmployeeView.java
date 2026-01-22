package project.projecte.View;

import project.projecte.Controller.EmployeeController;
import project.projecte.Controller.LoginController;
import project.projecte.Model.Employee;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import project.projecte.Controller.AdminController;
import java.time.LocalDate;
import java.util.List;

public class EmployeeView {

    private final EmployeeController employeeController;
    private final AdminController adminController;

    public EmployeeView(EmployeeController employeeController, AdminController adminController) {
        this.employeeController = employeeController;
        this.adminController = adminController;
    }

    public void display(Stage primaryStage) {
        // Buttons for CRUD operations
        Button addButton = createStyledButton("Add Employee");
        Button updateButton = createStyledButton("Update Employee");
        Button deleteButton = createStyledButton("Delete Employee");
        Button viewButton = createStyledButton("View Employees");
        Button backButton = createStyledButton("Back");

        // Button actions
        addButton.setOnAction(event -> displayAddEmployee(primaryStage));
        updateButton.setOnAction(event -> displayUpdateEmployee(primaryStage));
        deleteButton.setOnAction(event -> displayDeleteEmployee(primaryStage));
        viewButton.setOnAction(event -> displayAllEmployees(primaryStage));
        backButton.setOnAction(event -> new AdministratorView(adminController).display(primaryStage));
        
        // Layout setup
        VBox layout = new VBox(20, addButton, updateButton, deleteButton, viewButton,backButton);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #F1ECFF;");

        // Set up the scene
        Scene scene = new Scene(layout, 700, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Employee Management");
        primaryStage.show();
    }

    private void displayAddEmployee(Stage primaryStage) {
        Stage addStage = new Stage();
        addStage.setTitle("Add Employee");

        TextField nameField = createStyledTextField("Name");
        TextField usernameField = createStyledTextField("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle("-fx-font-size: 14px;");
        DatePicker dobPicker = new DatePicker();
        dobPicker.setPromptText("Date of Birth");
        TextField phoneField = createStyledTextField("Phone Number");
        TextField emailField = createStyledTextField("Email");
        TextField salaryField = createStyledTextField("Salary");
        TextField accessLevelField = createStyledTextField("Access Level (Cashier/Manager/Admin)");

        Button submitButton = createStyledButton("Submit");
        submitButton.setOnAction(event -> {
            try {
                String name = nameField.getText();
                String username = usernameField.getText();
                String password = passwordField.getText();
                LocalDate dob = dobPicker.getValue();
                String phone = phoneField.getText();
                String email = emailField.getText();
                double salary = Double.parseDouble(salaryField.getText());
                String accessLevel = accessLevelField.getText();

                employeeController.addEmployee(name, username, password, dob, phone, email, salary, accessLevel);
                addStage.close();
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Salary must be a valid number.");
            }
        });

        VBox layout = new VBox(15, nameField, usernameField, passwordField, dobPicker, phoneField, emailField, salaryField, accessLevelField, submitButton);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #F1ECFF;");
        addStage.setScene(new Scene(layout, 500, 600));
        addStage.show();
    }

    private void displayUpdateEmployee(Stage primaryStage) {
        Stage updateStage = new Stage();
        updateStage.setTitle("Update Employee");

        TextField currentNameField = createStyledTextField("Current Name");
        TextField newNameField = createStyledTextField("New Name");
        TextField newUsernameField = createStyledTextField("New Username");
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password");
        newPasswordField.setStyle("-fx-font-size: 14px;");
        DatePicker newDobPicker = new DatePicker();
        newDobPicker.setPromptText("New Date of Birth");
        TextField newPhoneField = createStyledTextField("New Phone Number");
        TextField newEmailField = createStyledTextField("New Email");
        TextField newSalaryField = createStyledTextField("New Salary");
        TextField newAccessLevelField = createStyledTextField("New Access Level (Cashier/Manager/Admin)");

        Button submitButton = createStyledButton("Update");
        submitButton.setOnAction(event -> {
            try {
                String currentName = currentNameField.getText();
                String newName = newNameField.getText();
                String newUsername = newUsernameField.getText();
                String newPassword = newPasswordField.getText();
                LocalDate newDob = newDobPicker.getValue();
                String newPhone = newPhoneField.getText();
                String newEmail = newEmailField.getText();
                double newSalary = Double.parseDouble(newSalaryField.getText());
                String newAccessLevel = newAccessLevelField.getText();

                employeeController.updateEmployee(currentName, newName, newUsername, newPassword, newDob, newPhone, newEmail, newSalary, newAccessLevel);
                updateStage.close();
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Salary must be a valid number.");
            }
        });

        VBox layout = new VBox(15, currentNameField, newNameField, newUsernameField, newPasswordField, newDobPicker, newPhoneField, newEmailField, newSalaryField, newAccessLevelField, submitButton);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #F1ECFF;");
        updateStage.setScene(new Scene(layout, 500, 700));
        updateStage.show();
    }

    private void displayDeleteEmployee(Stage primaryStage) {
        Stage deleteStage = new Stage();
        deleteStage.setTitle("Delete Employee");

        TextField nameField = createStyledTextField("Name");

        Button deleteButton = createStyledButton("Delete");
        deleteButton.setOnAction(event -> {
            String name = nameField.getText();
            employeeController.deleteEmployee(name);
            deleteStage.close();
        });

        VBox layout = new VBox(15, nameField, deleteButton);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #F1ECFF;");
        deleteStage.setScene(new Scene(layout, 400, 200));
        deleteStage.show();
    }

    private void displayAllEmployees(Stage primaryStage) {
        Stage viewStage = new Stage();
        viewStage.setTitle("All Employees");

        ListView<String> employeeList = new ListView<>();
        List<Employee> employees = employeeController.getEmployeeManager().getEmployees();
        for (Employee employee : employees) {
            employeeList.getItems().add(employee.getName() + " - " + employee.getAccessLevel());
        }

        VBox layout = new VBox(15, new Label("Employees:"), employeeList);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #F1ECFF;");
        viewStage.setScene(new Scene(layout, 500, 400));
        viewStage.show();
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: #B085FF; -fx-text-fill: white; -fx-padding: 10 20;");
        button.setMinWidth(200);
        return button;
    }

    private TextField createStyledTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.setStyle("-fx-font-size: 14px;");
        return textField;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void navigateBack(Stage primaryStage) {
        // Implement the navigation logic for "Back" button
        LoginController login = new LoginController(primaryStage);
        new LoginView(primaryStage, login);
    }
}
