package project.projecte.Controller;

import project.projecte.Model.Employee;
import project.projecte.Model.User;
import project.projecte.Model.UserManager;
import project.projecte.Model.Cashier;
import project.projecte.Model.Manager;
import project.projecte.Model.Admin;
import javafx.scene.control.Alert;

import java.time.LocalDate;

public class EmployeeController {

    private final EmployeeManager employeeManager;
    private final UserManager userManager;

    public EmployeeController(EmployeeManager employeeManager, UserManager userManager) {
        this.employeeManager = employeeManager;
        this.userManager = userManager;
    }

    public EmployeeManager getEmployeeManager() {
        return employeeManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public void addEmployee(String name, String username, String password, LocalDate dateOfBirth, String phoneNumber, String email, double salary, String accessLevel) {
        if (name.isEmpty() || username.isEmpty() || password.isEmpty() || dateOfBirth == null || phoneNumber.isEmpty() || email.isEmpty() || accessLevel.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Missing Information", "All fields are required.");
            return;
        }

        // Check if username already exists
        if (userManager.findUserByUsername(username) != null) {
            showAlert(Alert.AlertType.ERROR, "Username Exists", "A user with this username already exists.");
            return;
        }

        // Create Employee
        Employee employee = new Employee(name, username, dateOfBirth, phoneNumber, email, salary, accessLevel);
        employeeManager.addEmployee(employee);

        // Create corresponding User based on access level
        User newUser = createUserFromAccessLevel(username, password, accessLevel);
        if (newUser != null) {
            userManager.addUser(newUser);
            showAlert(Alert.AlertType.INFORMATION, "Employee Added", "Employee and corresponding user account created successfully.");
        } else {
            // If user creation fails, remove the employee to maintain consistency
            employeeManager.removeEmployee(employee);
            showAlert(Alert.AlertType.ERROR, "Invalid Access Level", "Please enter a valid access level (Cashier, Manager, or Admin).");
        }
    }

    private User createUserFromAccessLevel(String username, String password, String accessLevel) {
        return switch (accessLevel.toLowerCase()) {
            case "cashier" -> new Cashier(username, password, "DefaultSector");
            case "manager" -> new Manager(username, password, null, null, null);
            case "admin" -> new Admin(username, password, null);
            default -> null;
        };
    }

    public void updateEmployee(String currentName, String newName, String newUsername, String newPassword, LocalDate newDateOfBirth, String newPhoneNumber, String newEmail, double newSalary, String newAccessLevel) {
        Employee employee = employeeManager.findEmployeeByName(currentName);
        if (employee == null) {
            showAlert(Alert.AlertType.ERROR, "Employee Not Found", "No employee found with the specified name.");
            return;
        }

        // Get the old user linked to this employee
        User oldUser = userManager.findUserByUsername(employee.getUsername());
        
        // Check if new username already exists (and it's not the current user's username)
        if (!employee.getUsername().equals(newUsername) && userManager.findUserByUsername(newUsername) != null) {
            showAlert(Alert.AlertType.ERROR, "Username Exists", "A user with this username already exists.");
            return;
        }

        // Update Employee
        employee.setName(newName);
        employee.setUsername(newUsername);
        employee.setDateOfBirth(newDateOfBirth);
        employee.setPhoneNumber(newPhoneNumber);
        employee.setEmail(newEmail);
        employee.setSalary(newSalary);
        employee.setAccessLevel(newAccessLevel);
        employeeManager.saveEmployees();

        // Update corresponding User
        if (oldUser != null) {
            // Remove old user
            userManager.removeUser(oldUser);
            
            // Create new user with updated information
            User newUser = createUserFromAccessLevel(newUsername, newPassword, newAccessLevel);
            if (newUser != null) {
                userManager.addUser(newUser);
                showAlert(Alert.AlertType.INFORMATION, "Employee Updated", "Employee and user account updated successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Invalid Access Level", "Please enter a valid access level (Cashier, Manager, or Admin).");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "User Not Found", "Employee updated but no corresponding user account was found.");
        }
    }

    public void deleteEmployee(String name) {
        Employee employee = employeeManager.findEmployeeByName(name);
        if (employee == null) {
            showAlert(Alert.AlertType.ERROR, "Employee Not Found", "No employee found with the specified name.");
            return;
        }

        // Find and remove corresponding user
        User user = userManager.findUserByUsername(employee.getUsername());
        if (user != null) {
            userManager.removeUser(user);
        }

        // Remove employee
        employeeManager.removeEmployee(employee);
        showAlert(Alert.AlertType.INFORMATION, "Employee Deleted", "Employee and corresponding user account removed successfully.");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
