package project.projecte.Controller;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import project.projecte.Model.Manager;
import project.projecte.Model.Supplier;
import project.projecte.View.ManagerView;

public class SupplierController {
    private final Manager manager;
    private final Stage stage;

    public SupplierController(Manager manager, Stage stage) {
        this.manager = manager;
        this.stage = stage;
    }

    public void showSupplierManagementView() {
        // Supplier Management UI
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        // Create input fields for Supplier data
        TextField nameField = new TextField();
        nameField.setPromptText("Supplier Name");
        TextField contactField = new TextField();
        contactField.setPromptText("Contact Information");
        TextField productField = new TextField();
        productField.setPromptText("Products (comma-separated)");

        Button addSupplierButton = createStyledButton("Add Supplier", "#4CAF50");
        addSupplierButton.setOnAction(e -> addSupplier(nameField, contactField, productField));

        Button backButton = createStyledButton("Back", "#9E9E9E");
        backButton.setOnAction(e -> backToDashboard());

        // Add components to layout
        layout.getChildren().addAll(new Label("Manage Suppliers"), nameField, contactField, productField, addSupplierButton, backButton);

        Scene scene = new Scene(layout, 400, 400);
        stage.setScene(scene);
        stage.setTitle("Manage Suppliers");
        stage.show();
    }

    private void addSupplier(TextField nameField, TextField contactField, TextField productField) {
        String name = nameField.getText().trim();
        String contact = contactField.getText().trim();
        String[] products = productField.getText().split(",");
        if (!name.isEmpty() && !contact.isEmpty()) {
            Supplier supplier = new Supplier(name, contact);
            for (String product : products) {
                supplier.addProduct(product.trim());
            }
            manager.addSupplier(supplier);
            manager.saveSuppliers(); // Save to file
            showAlert(Alert.AlertType.INFORMATION, "Success", "Supplier added successfully.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Name and contact information are required.");
        }
    }

    private void backToDashboard() {
        // Go back to the main dashboard
        ManagerView managerView = new ManagerView(null);
        managerView.showManagerDashboard(stage);
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 12px;", color));
        button.setMinWidth(200);
        return button;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
