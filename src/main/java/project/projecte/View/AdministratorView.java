package project.projecte.View;

import project.projecte.Controller.AdminController;
import project.projecte.Controller.EmployeeController;
import project.projecte.Controller.EmployeeManager;
import project.projecte.Controller.LoginController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdministratorView {

    private final AdminController adminController;

    public AdministratorView(AdminController adminController) {
        this.adminController = adminController;
    }

    public void display(Stage primaryStage) {
        // Create UI elements
        Button manageUsersButton = createStyledButton("Users");
        Button manageEmployeesButton = createStyledButton("Employees");
        Button statisticsButton = createStyledButton("Statistics");
        Button backButton = createStyledButton("Log Out");

        // Button actions
        manageUsersButton.setOnAction(event -> new UserManagementView(adminController).display(primaryStage));
        EmployeeController ec = new EmployeeController(adminController.getEmployeeManager(), adminController.getUserManager());
        manageEmployeesButton.setOnAction(event -> new EmployeeView(ec, adminController).display(primaryStage));
        statisticsButton.setOnAction(event -> adminController.viewFinancials(primaryStage));
        backButton.setOnAction(event -> navigateBack(primaryStage));

        // Layout setup
        VBox layout = new VBox(20, manageUsersButton, manageEmployeesButton, statisticsButton, backButton);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #F1ECFF; -fx-border-radius: 10; -fx-effect: innershadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0, 0, 5);");

        // Scene setup
        Scene scene = new Scene(layout, 700, 450);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Administrator Dashboard");
        primaryStage.show();
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: #B085FF; -fx-text-fill: white; -fx-padding: 10 20;");
        button.setMinWidth(200);
        return button;
    }

    private void navigateBack(Stage primaryStage) {
        // Implement the navigation logic for "Back" button
    	LoginController login = new LoginController(primaryStage);
        new LoginView(primaryStage, login);
    }
}
