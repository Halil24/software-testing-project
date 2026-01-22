package project.projecte.Main;

import project.projecte.Controller.*;
import project.projecte.View.*;
import javafx.application.Application;
import javafx.stage.Stage;

public class AppProgram extends Application {
	private static final String appName = "Electronic Store Management System";
    @Override
    public void start(Stage primaryStage) {
        // Create the main view for the application
        primaryStage.setTitle(appName);

        // Start the login screen as the first step of the app
        showLoginScreen(primaryStage);
    }

    // Method to show login screen
    private void showLoginScreen(Stage primaryStage) {
        // Create a new LoginController instance
    	LoginController loginController = new LoginController(primaryStage);
        new LoginView(primaryStage, loginController);
    }
    
 // Entry point for the JavaFX application
    public static void main(String[] args) {
        launch(args);
    }
}
