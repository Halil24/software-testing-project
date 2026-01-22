package project.projecte.View;

import project.projecte.Controller.AdminController;
import project.projecte.Model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UserManagementView {

    private final AdminController adminController;

    public UserManagementView(AdminController adminController) {
        this.adminController = adminController;
    }

    public void display(Stage primaryStage) {
        // Main layout
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F1ECFF;");

        // Buttons for managing users
        Button createUserButton = createStyledButton("Create User", "#B085FF");
        Button viewUsersButton = createStyledButton("View Users", "#B085FF");
        Button updateUserButton = createStyledButton("Update User", "#B085FF");
        Button deleteUserButton = createStyledButton("Delete User", "#F44336");
        Button backButton = createStyledButton("Back", "#F6CE46");

        // Button actions
        createUserButton.setOnAction(event -> adminController.addUser(primaryStage));
        viewUsersButton.setOnAction(event -> displayUsers());
        updateUserButton.setOnAction(event -> adminController.updateUser(primaryStage));
        deleteUserButton.setOnAction(event -> adminController.deleteUser(primaryStage));
        backButton.setOnAction(event -> new AdministratorView(adminController).display(primaryStage));

        // Button layout (horizontal)
        HBox buttonLayout = new HBox(15, createUserButton, viewUsersButton, updateUserButton, deleteUserButton, backButton);
        buttonLayout.setAlignment(Pos.CENTER);
        buttonLayout.setPadding(new Insets(15));
        buttonLayout.setStyle("-fx-background-color: #F1ECFF;");

        // Header
        Label header = new Label("User Management");
        header.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");
        VBox headerLayout = new VBox(header);
        headerLayout.setAlignment(Pos.CENTER);
        headerLayout.setPadding(new Insets(10));
        headerLayout.setStyle("-fx-background-color: #F1ECFF;");

        // Add components to the root layout
        root.setTop(headerLayout);
        root.setCenter(buttonLayout);

        // Set up the scene
        Scene scene = new Scene(root, 900, 200); // Larger interface
        primaryStage.setScene(scene);
        primaryStage.setTitle("User Management");
        primaryStage.show();
    }

    private void displayUsers() {
        // Logic for displaying users in a pop-up dialog
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("User List");
        alert.setHeaderText("Registered Users");

        StringBuilder userList = new StringBuilder();
        for (User user : adminController.getUserManager().getUsers()) {
            userList.append(user.getUsername())
                    .append(" - ")
                    .append(user.getRole())
                    .append("\n");
        }

        alert.setContentText(userList.toString());
        alert.showAndWait();
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle(String.format(
                "-fx-background-color: %s; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 5px; -fx-font-size: 16px; -fx-padding: 10px;",
                color));
        button.setMinWidth(150);
        return button;
    }
}
