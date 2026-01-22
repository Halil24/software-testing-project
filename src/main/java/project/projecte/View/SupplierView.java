package project.projecte.View;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import project.projecte.Model.Manager;
import project.projecte.Model.Supplier;

public class SupplierView {

    private final Manager manager;

    public SupplierView(Manager manager) {
        this.manager = manager;
    }

    public void showSupplierView(Stage stage) {
        Stage supplierStage = new Stage();
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        TableView<Supplier> supplierTable = new TableView<>();
        TableColumn<Supplier, String> nameColumn = new TableColumn<>("Supplier Name");
        TableColumn<Supplier, String> contactColumn = new TableColumn<>("Contact Information");

        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        contactColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getContactInfo()));

        supplierTable.getColumns().add(nameColumn);
        supplierTable.getColumns().add(contactColumn);
        supplierTable.setItems((ObservableList<Supplier>) manager.getSuppliers()); // assuming manager.getSuppliers() returns an ObservableList<Supplier>

        TextField nameField = new TextField();
        nameField.setPromptText("Supplier Name");

        TextField contactField = new TextField();
        contactField.setPromptText("Contact Information");

        Button addSupplierButton = createStyledButton("Add Supplier", "#4CAF50");
        addSupplierButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            String contact = contactField.getText().trim();
            if (!name.isEmpty() && !contact.isEmpty()) {
                Supplier supplier = new Supplier(name, contact);
                manager.addSupplier(supplier);
                supplierTable.setItems((ObservableList<Supplier>) manager.getSuppliers()); // Refresh table
                showAlert(Alert.AlertType.INFORMATION, "Success", "Supplier added successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Name and contact information are required.");
            }
        });

        layout.getChildren().addAll(new Label("Manage Suppliers"), supplierTable, nameField, contactField, addSupplierButton);

        Scene scene = new Scene(layout, 600, 400);
        supplierStage.setScene(scene);
        supplierStage.setTitle("Manage Suppliers");
        supplierStage.show();
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px;", color));
        return button;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
