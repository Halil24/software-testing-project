package project.projecte.Controller;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import project.projecte.Model.Inventory;
import project.projecte.Model.Item;
import project.projecte.Model.Manager;
import project.projecte.Model.Supplier;

public class ManagerController {

    private final Manager manager;

    public ManagerController(Manager manager) {
        this.manager = manager;
    }

    public void start(Stage primaryStage) {
        VBox layout = new VBox(15);

        // Buttons for manager actions
        Button addItemButton = new Button("Add Item");
        addItemButton.setOnAction(event -> addItem());

        Button modifyItemButton = new Button("Modify Item");
        modifyItemButton.setOnAction(event -> modifyItem());

        Button restockItemButton = new Button("Restock Item");
        restockItemButton.setOnAction(event -> restockItem());

        Button lowStockButton = new Button("Low Stock Alerts");
        lowStockButton.setOnAction(event -> viewLowStock());

        Button generateStatisticsButton = new Button("Statistics");
        generateStatisticsButton.setOnAction(event -> generateStatistics());

        Button manageSuppliersButton = new Button("Suppliers");
        manageSuppliersButton.setOnAction(event -> manageSuppliers());

        // Add all buttons to the layout
        layout.getChildren().addAll(
                addItemButton,
                modifyItemButton,
                restockItemButton,
                lowStockButton,
                generateStatisticsButton,
                manageSuppliersButton
        );

        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Manager Dashboard");
        primaryStage.show();
    }

    private void addItem() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add New Item");
        dialog.setHeaderText("Enter item details (Name, Stock, Purchase Price, Selling Price):");

        dialog.showAndWait().ifPresent(input -> {
            String[] parts = input.split(",");
            if (parts.length == 5) {
                try {
                    String name = parts[0].trim();
                    int stock = Integer.parseInt(parts[1].trim());
                    double purchasePrice = Double.parseDouble(parts[2].trim());
                    double sellingPrice = Double.parseDouble(parts[3].trim());
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Item added successfully.");
                } catch (NumberFormatException ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid numeric values for stock or prices.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Please enter all details in the correct format.");
            }
        });
    }

    private void modifyItem() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Modify Item");
        dialog.setHeaderText("Enter item name to modify:");

        dialog.showAndWait().ifPresent(name -> {
            Item item = manager.getInventory().findItemByName(name.trim());
            if (item != null) {
                modifyItemDetails(item);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Item not found.");
            }
        });
    }

    private void modifyItemDetails(Item item) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Modify Item Details");
        dialog.setHeaderText("Enter new stock and selling price (Stock, Selling Price):");

        dialog.showAndWait().ifPresent(input -> {
            String[] parts = input.split(",");
            if (parts.length == 2) {
                try {
                    int stock = Integer.parseInt(parts[0].trim());
                    double sellingPrice = Double.parseDouble(parts[1].trim());
                    manager.modifyItem(item.getName(), stock, sellingPrice);
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Item updated successfully.");
                } catch (NumberFormatException ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid numeric values for stock or price.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Please enter stock and price in the correct format.");
            }
        });
    }

    private void restockItem() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Restock Item");
        dialog.setHeaderText("Enter item name and quantity to restock (Name, Quantity):");

        dialog.showAndWait().ifPresent(input -> {
            String[] parts = input.split(",");
            if (parts.length == 2) {
                try {
                    String name = parts[0].trim();
                    int quantity = Integer.parseInt(parts[1].trim());
                    manager.restockItem(name, quantity);
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Item restocked successfully.");
                } catch (NumberFormatException ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid numeric value for quantity.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Please enter name and quantity in the correct format.");
            }
        });
    }

    private void viewLowStock() {
        manager.checkLowStock(5);
    }

    private void generateStatistics() {
        LocalDateTime startDate = LocalDate.now().minusMonths(1).atStartOfDay();
        LocalDateTime endDate = LocalDate.now().atTime(23, 59, 59);
        manager.generateStatistics(startDate, endDate);
    }

    private void manageSuppliers() {
        // Supplier management UI
        Stage supplierStage = new Stage();
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 10;");

        TextField nameField = new TextField();
        nameField.setPromptText("Supplier Name");

        TextField contactField = new TextField();
        contactField.setPromptText("Contact Information");

        TextField productField = new TextField();
        productField.setPromptText("Products (comma-separated)");

        Button addSupplierButton = new Button("Add Supplier");
        addSupplierButton.setOnAction(event -> {
            String name = nameField.getText().trim();
            String contact = contactField.getText().trim();
            String[] products = productField.getText().split(",");
            if (!name.isEmpty() && !contact.isEmpty()) {
                Supplier supplier = new Supplier(name, contact);
                for (String product : products) {
                    supplier.addProduct(product.trim());
                }
                manager.addSupplier(supplier);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Supplier added successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Name and contact information are required.");
            }
        });

        layout.getChildren().addAll(new Label("Suppliers"), nameField, contactField, productField, addSupplierButton);

        Scene scene = new Scene(layout, 400, 300);
        supplierStage.setScene(scene);
        supplierStage.setTitle("Suppliers");
        supplierStage.show();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
