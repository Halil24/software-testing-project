package project.projecte.View;

import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import project.projecte.Controller.LoginController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LoginView {
    private final TextField usernameField;
    private final PasswordField passwordField;
    private final Label errorLabel;

    public LoginView(Stage primaryStage, LoginController loginController) {
        // Create UI elements
        usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.setMaxWidth(400); // Increase width
        usernameField.setStyle("-fx-font-size: 16px;"); // Larger text

        passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setMaxWidth(400); // Increase width
        passwordField.setStyle("-fx-font-size: 16px;");// Larger text
        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 14px;");

        Button loginButton = new Button("Log in");
        loginButton.setStyle(
                "-fx-background-color: #F6CE46; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                 "-fx-font-size: 16px;" + // Larger text
                "-fx-padding: 10 20 10 20;" // Increase padding
        );

        loginButton.setOnAction(event -> loginController.handleLogin(
                usernameField.getText(),
                passwordField.getText(),
                this // Pass the current view for error handling
        ));

        // Create layout
        VBox layout = new VBox(20); // Increase spacing
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: #C9B2FF; -fx-border-radius: 10; -fx-padding: 20;");

        Label titleLabel = new Label("Login");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF;"); // Larger title
        Label userLabel =new Label("Username:");
        Label passLabel = new Label("Password:");
        userLabel.setStyle("-fx-text-fill: #FFFFFF;");
        userLabel.setFont(Font.font("Arial",FontWeight.BOLD,14));
        passLabel.setStyle("-fx-text-fill: #FFFFFF;");
        passLabel.setFont(Font.font("Arial",FontWeight.BOLD,14));
        layout.getChildren().addAll(
                titleLabel,userLabel, usernameField,passLabel,passwordField, loginButton, errorLabel);

        // Wrap in BorderPane for centering
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F1ECFF;");
        root.setCenter(layout);

        // Create Scene
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    // Display an error message
    public void showError(String message) {
        errorLabel.setText(message);
    }
}
