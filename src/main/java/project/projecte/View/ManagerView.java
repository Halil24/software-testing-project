package project.projecte.View;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import project.projecte.Controller.LoginController;
import project.projecte.Model.*;

public class ManagerView {

    private final Manager manager;

    public ManagerView(Manager manager) {
        this.manager = manager;
    }

    public void showManagerDashboard(Stage stage) {
        // Create UI elements for the dashboard
        Button viewItemsButton = createStyledButton("View Items", "#B085FF");
        Button viewCashiersButton = createStyledButton("View Cashiers", "#B085FF");
        Button viewSuppliersButton = createStyledButton("View Suppliers", "#B085FF");
        Button viewLowStockButton = createStyledButton("Stock Alerts", "#B085FF");
        Button generateStatisticsButton = createStyledButton("Statistics", "#B085FF");

        // Logout button
        Button logoutButton = createStyledButton("Logout", "#D32F2F");
        logoutButton.setOnAction(e -> logout(stage));

        // Add button actions
        viewItemsButton.setOnAction(e -> showItemsView(stage));
        viewCashiersButton.setOnAction(e -> showCashiersView(stage));
        viewSuppliersButton.setOnAction(e -> showSuppliersView(stage));
        viewLowStockButton.setOnAction(e -> viewLowStock());
        generateStatisticsButton.setOnAction(e -> generateStatistics());

        // Layout for the dashboard buttons
        VBox buttonLayout = new VBox(15, viewItemsButton, viewCashiersButton, viewSuppliersButton, 
                                      viewLowStockButton, generateStatisticsButton);
        buttonLayout.setPadding(new Insets(20));
        buttonLayout.setAlignment(Pos.CENTER);
        buttonLayout.setStyle("-fx-background-color: #F1ECFF;");

        // Layout for the logout button
        HBox logoutLayout = new HBox(logoutButton);
        logoutLayout.setAlignment(Pos.CENTER_RIGHT);
        logoutLayout.setPadding(new Insets(20));

        // Combine the button layout and logout layout in a VBox
        VBox layout = new VBox(15, buttonLayout, logoutLayout);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #F1ECFF;");

        // Set up the scene and stage
        Scene scene = new Scene(layout, 700, 500);
        stage.setScene(scene);
        stage.setTitle("Manager Dashboard");
        stage.show();
    }

    // Logout action
    private void logout(Stage stage) {
        // Handle logout logic - navigate back to login screen
        System.out.println("Logging out...");
        LoginController loginController = new LoginController(stage);
        new LoginView(stage, loginController);
    }


    // ITEMS VIEW - Consolidated view for all item operations
    private void showItemsView(Stage parentStage) {
        Stage itemsStage = new Stage();
        itemsStage.setTitle("Items Management");
        
        // Debug: Check inventory
        System.out.println("DEBUG showItemsView: Manager inventory is null? " + (manager.getInventory() == null));
        if (manager.getInventory() != null) {
            // Reload inventory to ensure fresh data
            manager.getInventory().loadInventory();
            System.out.println("DEBUG showItemsView: Items count: " + manager.getInventory().getItems().size());
            if (manager.getInventory().getItems().size() > 0) {
                System.out.println("DEBUG showItemsView: First item: " + manager.getInventory().getItems().get(0).getName());
            }
        }
        
        // Create TableView for items
        TableView<Item> itemsTable = new TableView<>();
        TableColumn<Item, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        nameCol.setPrefWidth(150);
        
        TableColumn<Item, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCategory()));
        categoryCol.setPrefWidth(120);
        
        TableColumn<Item, Integer> stockCol = new TableColumn<>("Stock");
        stockCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getStockLevel()).asObject());
        stockCol.setPrefWidth(80);
        
        TableColumn<Item, Double> purchasePriceCol = new TableColumn<>("Purchase Price");
        purchasePriceCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getPurchasePrice()).asObject());
        purchasePriceCol.setPrefWidth(120);
        
        TableColumn<Item, Double> sellingPriceCol = new TableColumn<>("Selling Price");
        sellingPriceCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getSellingPrice()).asObject());
        sellingPriceCol.setPrefWidth(120);
        
        itemsTable.getColumns().addAll(nameCol, categoryCol, stockCol, purchasePriceCol, sellingPriceCol);
        
        // Load items
        ObservableList<Item> items = FXCollections.observableArrayList(manager.getInventory().getItems());
        itemsTable.setItems(items);
        
        System.out.println("DEBUG showItemsView: Table items count: " + items.size());
        
        // Action buttons
        Button addButton = createStyledButton("Add Item", "#4CAF50");
        Button modifyButton = createStyledButton("Modify Item", "#FFC107");
        Button deleteButton = createStyledButton("Delete Item", "#F44336");
        Button restockButton = createStyledButton("Restock Item", "#2196F3");
        Button refreshButton = createStyledButton("Refresh", "#9C27B0");
        Button backButton = createStyledButton("Back", "#757575");
        
        addButton.setOnAction(e -> {
            showAddItemDialog(itemsStage);
            items.setAll(manager.getInventory().getItems());
        });
        
        modifyButton.setOnAction(e -> {
            Item selected = itemsTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showModifyItemDialog(selected, itemsStage);
                items.setAll(manager.getInventory().getItems());
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an item to modify.");
            }
        });
        
        deleteButton.setOnAction(e -> {
            Item selected = itemsTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Confirm Delete");
                confirm.setHeaderText("Delete Item");
                confirm.setContentText("Are you sure you want to delete " + selected.getName() + "?");
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        manager.getInventory().removeItem(selected.getName());
                        manager.getInventory().saveInventory();
                        items.setAll(manager.getInventory().getItems());
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Item deleted successfully.");
                    }
                });
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an item to delete.");
            }
        });
        
        restockButton.setOnAction(e -> {
            Item selected = itemsTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showRestockDialog(selected);
                items.setAll(manager.getInventory().getItems());
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an item to restock.");
            }
        });
        
        refreshButton.setOnAction(e -> items.setAll(manager.getInventory().getItems()));
        backButton.setOnAction(e -> itemsStage.close());
        
        HBox buttonBox = new HBox(10, addButton, modifyButton, deleteButton, restockButton, refreshButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));
        
        VBox layout = new VBox(15, new Label("Items Inventory"), itemsTable, buttonBox);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #F1ECFF;");
        
        Scene scene = new Scene(layout, 700, 500);
        itemsStage.setScene(scene);
        itemsStage.show();
    }
    
    private void showAddItemDialog(Stage parentStage) {
        Stage dialog = new Stage();
        dialog.setTitle("Add New Item");
        
        TextField nameField = new TextField();
        nameField.setPromptText("Item Name");
        TextField categoryField = new TextField();
        categoryField.setPromptText("Category");
        TextField stockField = new TextField();
        stockField.setPromptText("Stock");
        TextField purchasePriceField = new TextField();
        purchasePriceField.setPromptText("Purchase Price");
        TextField sellingPriceField = new TextField();
        sellingPriceField.setPromptText("Selling Price");
        
        Button submitButton = createStyledButton("Add", "#4CAF50");
        submitButton.setOnAction(e -> {
            try {
                String name = nameField.getText();
                String category = categoryField.getText();
                int stock = Integer.parseInt(stockField.getText());
                double purchasePrice = Double.parseDouble(purchasePriceField.getText());
                double sellingPrice = Double.parseDouble(sellingPriceField.getText());
                
                if (name.isEmpty() || category.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Name and category are required.");
                    return;
                }
                
                Item item = new Item(name, category, purchasePrice, sellingPrice, stock);
                manager.getInventory().addItem(item);
                manager.getInventory().saveInventory();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Item added successfully.");
                dialog.close();
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please enter valid numeric values.");
            }
        });
        
        VBox layout = new VBox(10, nameField, categoryField, stockField, purchasePriceField, sellingPriceField, submitButton);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #F1ECFF;");
        
        Scene scene = new Scene(layout, 350, 300);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
    
    private void showModifyItemDialog(Item item, Stage parentStage) {
        Stage dialog = new Stage();
        dialog.setTitle("Modify Item: " + item.getName());
        
        TextField categoryField = new TextField(item.getCategory());
        categoryField.setPromptText("Category");
        TextField stockField = new TextField(String.valueOf(item.getStockLevel()));
        stockField.setPromptText("Stock");
        TextField purchasePriceField = new TextField(String.valueOf(item.getPurchasePrice()));
        purchasePriceField.setPromptText("Purchase Price");
        TextField sellingPriceField = new TextField(String.valueOf(item.getSellingPrice()));
        sellingPriceField.setPromptText("Selling Price");
        
        Button submitButton = createStyledButton("Update", "#FFC107");
        submitButton.setOnAction(e -> {
            try {
                item.setCategory(categoryField.getText());
                item.setStockLevel(Integer.parseInt(stockField.getText()));
                item.setPurchasePrice(Double.parseDouble(purchasePriceField.getText()));
                item.setSellingPrice(Double.parseDouble(sellingPriceField.getText()));
                manager.getInventory().saveInventory();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Item updated successfully.");
                dialog.close();
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please enter valid numeric values.");
            }
        });
        
        VBox layout = new VBox(10, new Label("Modifying: " + item.getName()), 
                               categoryField, stockField, purchasePriceField, sellingPriceField, submitButton);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #F1ECFF;");
        
        Scene scene = new Scene(layout, 350, 280);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
    
    private void showRestockDialog(Item item) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Restock Item");
        dialog.setHeaderText("Restock: " + item.getName());
        dialog.setContentText("Enter quantity to add:");
        
        dialog.showAndWait().ifPresent(input -> {
            try {
                int quantity = Integer.parseInt(input);
                item.setStockLevel(item.getStockLevel() + quantity);
                manager.getInventory().saveInventory();
                showAlert(Alert.AlertType.INFORMATION, "Success", 
                         "Item restocked. New stock level: " + item.getStockLevel());
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid number.");
            }
        });
    }

    private void viewLowStock() {
        // Show low stock items in a JavaFX dialog
        Stage lowStockStage = new Stage();
        lowStockStage.setTitle("Low Stock Alert");
        
        // Get low stock items (threshold = 5)
        int threshold = 5;
        List<Item> lowStockItems = manager.getInventory().getItems().stream()
                .filter(item -> item.getStockLevel() < threshold)
                .toList();
        
        if (lowStockItems.isEmpty()) {
            // No low stock items
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Stock Status");
            alert.setHeaderText("All Items Well Stocked");
            alert.setContentText("No items are below the stock threshold of " + threshold + ".");
            alert.showAndWait();
            return;
        }
        
        // Create table for low stock items
        TableView<Item> lowStockTable = new TableView<>();
        
        TableColumn<Item, String> nameCol = new TableColumn<>("Item Name");
        nameCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        nameCol.setPrefWidth(200);
        
        TableColumn<Item, Integer> stockCol = new TableColumn<>("Current Stock");
        stockCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getStockLevel()).asObject());
        stockCol.setPrefWidth(120);
        
        TableColumn<Item, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCategory()));
        categoryCol.setPrefWidth(150);
        
        lowStockTable.getColumns().addAll(nameCol, stockCol, categoryCol);
        
        ObservableList<Item> items = FXCollections.observableArrayList(lowStockItems);
        lowStockTable.setItems(items);
        
        // Warning label
        Label warningLabel = new Label("⚠️ Warning: " + lowStockItems.size() + " item(s) below threshold of " + threshold);
        warningLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #F44336;");
        
        // Restock button
        Button restockButton = createStyledButton("Restock Selected", "#4CAF50");
        restockButton.setOnAction(e -> {
            Item selected = lowStockTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showRestockDialog(selected);
                // Refresh the list
                List<Item> updated = manager.getInventory().getItems().stream()
                        .filter(item -> item.getStockLevel() < threshold)
                        .toList();
                items.setAll(updated);
                if (items.isEmpty()) {
                    lowStockStage.close();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "All items are now well stocked!");
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an item to restock.");
            }
        });
        
        Button closeButton = createStyledButton("Close", "#757575");
        closeButton.setOnAction(e -> lowStockStage.close());
        
        HBox buttonBox = new HBox(10, restockButton, closeButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));
        
        VBox layout = new VBox(15, warningLabel, lowStockTable, buttonBox);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #F1ECFF;");
        
        Scene scene = new Scene(layout, 550, 400);
        lowStockStage.setScene(scene);
        lowStockStage.show();
    }

    private void generateStatistics() {
        // Debug: Print to verify method is called
        System.out.println("Generate Statistics called");
        
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        
        System.out.println("Date range: " + startDate + " to " + endDate);
        System.out.println("Manager inventory items: " + manager.getInventory().getItems().size());
        
        manager.generateStatistics(startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
    }

    // CASHIERS VIEW - View all cashiers
    private void showCashiersView(Stage parentStage) {
        Stage cashiersStage = new Stage();
        cashiersStage.setTitle("Cashiers Management");
        
        // Create TableView for cashiers
        TableView<Cashier> cashiersTable = new TableView<>();
        TableColumn<Cashier, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUsername()));
        usernameCol.setPrefWidth(150);
        
        TableColumn<Cashier, String> sectorCol = new TableColumn<>("Sector");
        sectorCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getSector()));
        sectorCol.setPrefWidth(120);
        
        TableColumn<Cashier, Integer> billsCountCol = new TableColumn<>("Total Bills");
        billsCountCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getBills().size()).asObject());
        billsCountCol.setPrefWidth(100);
        
        TableColumn<Cashier, Double> todaySalesCol = new TableColumn<>("Today's Sales");
        todaySalesCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getTotalSalesToday()).asObject());
        todaySalesCol.setPrefWidth(120);
        
        cashiersTable.getColumns().addAll(usernameCol, sectorCol, billsCountCol, todaySalesCol);
        
        // Load cashiers with their bills
        UserManager userManager = new UserManager();
        BillManager billManager = new BillManager();
        List<Cashier> cashiers = userManager.getUsers().stream()
                .filter(u -> u instanceof Cashier)
                .map(u -> (Cashier) u)
                .toList();
        
        System.out.println("DEBUG showCashiersView: Found " + cashiers.size() + " cashiers");
        for (Cashier c : cashiers) {
            System.out.println("DEBUG showCashiersView: - Cashier username: '" + c.getUsername() + "'");
        }
        
        // Load bills for each cashier
        List<Bill> allBills = billManager.getBills();
        System.out.println("DEBUG showCashiersView: Loaded " + allBills.size() + " bills");
        for (Bill b : allBills) {
            System.out.println("DEBUG showCashiersView: - Bill #" + b.getBillNumber() + 
                             " cashierUsername='" + b.getCashierUsername() + "'" +
                             " amount=$" + b.getTotalAmount());
        }
        
        for (Cashier cashier : cashiers) {
            cashier.getBills().clear();
            System.out.println("DEBUG showCashiersView: Matching bills for cashier '" + cashier.getUsername() + "'...");
            
            List<Bill> cashierBills = allBills.stream()
                    .filter(b -> {
                        boolean matches = b.getCashierUsername() != null
                                && b.getCashierUsername().equalsIgnoreCase(cashier.getUsername());
                        if (b.getCashierUsername() != null) {
                            System.out.println("DEBUG showCashiersView:   Bill #" + b.getBillNumber() + 
                                             " cashier='" + b.getCashierUsername() + "'" +
                                             " matches=" + matches);
                        }
                        return matches;
                    })
                    .toList();
            cashier.getBills().addAll(cashierBills);
            System.out.println("DEBUG showCashiersView: Cashier " + cashier.getUsername() + " matched " + cashierBills.size() + " bills");
        }
        
        ObservableList<Cashier> cashiersList = FXCollections.observableArrayList(cashiers);
        cashiersTable.setItems(cashiersList);
        
        System.out.println("DEBUG showCashiersView: Table cashiers count: " + cashiersList.size());
        
        // Action buttons
        Button refreshButton = createStyledButton("Refresh", "#9C27B0");
        Button backButton = createStyledButton("Back", "#757575");
        
        refreshButton.setOnAction(e -> {
            // Reload cashiers
            List<Cashier> updatedCashiers = userManager.getUsers().stream()
                    .filter(u -> u instanceof Cashier)
                    .map(u -> (Cashier) u)
                    .toList();
            List<Bill> updatedBills = billManager.getBills();
            for (Cashier cashier : updatedCashiers) {
                cashier.getBills().clear();
                List<Bill> cashierBills = updatedBills.stream()
                        .filter(b -> b.getCashierUsername() != null
                                && b.getCashierUsername().equalsIgnoreCase(cashier.getUsername()))
                        .toList();
                cashier.getBills().addAll(cashierBills);
            }
            cashiersList.setAll(updatedCashiers);
        });
        
        backButton.setOnAction(e -> cashiersStage.close());
        
        HBox buttonBox = new HBox(10, refreshButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));
        
        VBox layout = new VBox(15, new Label("Cashiers Overview"), cashiersTable, buttonBox);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #F1ECFF;");
        
        Scene scene = new Scene(layout, 600, 400);
        cashiersStage.setScene(scene);
        cashiersStage.show();
    }
    
    // SUPPLIERS VIEW - Consolidated view for all supplier operations
    private void showSuppliersView(Stage parentStage) {
        Stage suppliersStage = new Stage();
        suppliersStage.setTitle("Suppliers Management");
        
        // Create TableView for suppliers
        TableView<Supplier> suppliersTable = new TableView<>();
        TableColumn<Supplier, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        nameCol.setPrefWidth(200);
        
        TableColumn<Supplier, String> contactCol = new TableColumn<>("Contact");
        contactCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getContactInfo()));
        contactCol.setPrefWidth(200);
        
        TableColumn<Supplier, String> productsCol = new TableColumn<>("Products");
        productsCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(String.join(", ", cellData.getValue().getProducts())));
        productsCol.setPrefWidth(250);
        
        suppliersTable.getColumns().addAll(nameCol, contactCol, productsCol);
        
        // Load suppliers
        ObservableList<Supplier> suppliers = FXCollections.observableArrayList(manager.getSuppliers());
        suppliersTable.setItems(suppliers);
        
        // Action buttons
        Button addButton = createStyledButton("Add Supplier", "#4CAF50");
        Button modifyButton = createStyledButton("Modify Supplier", "#FFC107");
        Button deleteButton = createStyledButton("Delete Supplier", "#F44336");
        Button refreshButton = createStyledButton("Refresh", "#9C27B0");
        Button backButton = createStyledButton("Back", "#757575");
        
        addButton.setOnAction(e -> {
            showAddSupplierDialog(suppliersStage);
            suppliers.setAll(manager.getSuppliers());
        });
        
        modifyButton.setOnAction(e -> {
            Supplier selected = suppliersTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showModifySupplierDialog(selected, suppliersStage);
                suppliers.setAll(manager.getSuppliers());
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a supplier to modify.");
            }
        });
        
        deleteButton.setOnAction(e -> {
            Supplier selected = suppliersTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Confirm Delete");
                confirm.setHeaderText("Delete Supplier");
                confirm.setContentText("Are you sure you want to delete " + selected.getName() + "?");
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        manager.getSuppliers().remove(selected);
                        manager.saveSuppliers();
                        suppliers.setAll(manager.getSuppliers());
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Supplier deleted successfully.");
                    }
                });
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a supplier to delete.");
            }
        });
        
        refreshButton.setOnAction(e -> suppliers.setAll(manager.getSuppliers()));
        backButton.setOnAction(e -> suppliersStage.close());
        
        HBox buttonBox = new HBox(10, addButton, modifyButton, deleteButton, refreshButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));
        
        VBox layout = new VBox(15, new Label("Suppliers Directory"), suppliersTable, buttonBox);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #F1ECFF;");
        
        Scene scene = new Scene(layout, 700, 400);
        suppliersStage.setScene(scene);
        suppliersStage.show();
    }
    
    private void showAddSupplierDialog(Stage parentStage) {
        Stage dialog = new Stage();
        dialog.setTitle("Add New Supplier");
        
        TextField nameField = new TextField();
        nameField.setPromptText("Supplier Name");
        TextField contactField = new TextField();
        contactField.setPromptText("Contact Information");
        TextField productsField = new TextField();
        productsField.setPromptText("Products (comma-separated)");
        
        Button submitButton = createStyledButton("Add", "#4CAF50");
        submitButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            String contact = contactField.getText().trim();
            String productsText = productsField.getText().trim();
            
            if (name.isEmpty() || contact.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Name and contact are required.");
                return;
            }
            
            Supplier supplier = new Supplier(name, contact);
            if (!productsText.isEmpty()) {
                String[] products = productsText.split(",");
                for (String product : products) {
                    supplier.addProduct(product.trim());
                }
            }
            
            manager.addSupplier(supplier);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Supplier added successfully.");
            dialog.close();
        });
        
        VBox layout = new VBox(10, nameField, contactField, productsField, submitButton);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #F1ECFF;");
        
        Scene scene = new Scene(layout, 350, 220);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
    
    private void showModifySupplierDialog(Supplier supplier, Stage parentStage) {
        Stage dialog = new Stage();
        dialog.setTitle("Modify Supplier: " + supplier.getName());
        
        TextField nameField = new TextField(supplier.getName());
        nameField.setPromptText("Supplier Name");
        TextField contactField = new TextField(supplier.getContactInfo());
        contactField.setPromptText("Contact Information");
        TextField productsField = new TextField(String.join(", ", supplier.getProducts()));
        productsField.setPromptText("Products (comma-separated)");
        
        Button submitButton = createStyledButton("Update", "#FFC107");
        submitButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            String contact = contactField.getText().trim();
            String productsText = productsField.getText().trim();
            
            if (name.isEmpty() || contact.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Name and contact are required.");
                return;
            }
            
            supplier.setName(name);
            supplier.setContactInfo(contact);
            supplier.getProducts().clear();
            if (!productsText.isEmpty()) {
                String[] products = productsText.split(",");
                for (String product : products) {
                    supplier.addProduct(product.trim());
                }
            }
            
            manager.saveSuppliers();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Supplier updated successfully.");
            dialog.close();
        });
        
        VBox layout = new VBox(10, new Label("Modifying: " + supplier.getName()), 
                               nameField, contactField, productsField, submitButton);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #F1ECFF;");
        
        Scene scene = new Scene(layout, 350, 250);
        dialog.setScene(scene);
        dialog.showAndWait();
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
