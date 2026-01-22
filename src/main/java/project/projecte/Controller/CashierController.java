package project.projecte.Controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;

import project.projecte.Model.Bill;
import project.projecte.Model.Cashier;
import project.projecte.Model.Inventory;
import project.projecte.Model.Item;

public class CashierController {

    private final Cashier cashier;
    private final Inventory inventory;
    private final ObservableList<Item> availableItems;
    private final ObservableList<Item> billItems = FXCollections.observableArrayList();
    private TableView<Item> itemsTable;
    private TableView<Item> billTable;
    private Label totalLabel;

    public CashierController(Cashier cashier, Inventory inventory) {
        this.cashier = cashier;
        this.inventory = inventory;
        this.availableItems = FXCollections.observableArrayList(inventory.getItems());
    }

    public void start(Stage primaryStage) {
        // Create a table to display available items
        itemsTable = createItemsTable();

        // Create a table to display items in the bill
        billTable = createBillTable();

        // Label to show the total amount of the bill
        totalLabel = new Label("Total: $0.00");
        totalLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Button to add an item to the bill
        Button addItemButton = new Button("Add to Bill");
        addItemButton.setOnAction(event -> addItemToBill());

        // Button to finalize the bill
        Button finalizeBillButton = new Button("Finalize Bill");
        finalizeBillButton.setOnAction(event -> finalizeBill(primaryStage));

        // Layout for the Cashier dashboard
        VBox layout = new VBox(10, itemsTable, addItemButton, billTable, totalLabel, finalizeBillButton);
        layout.setStyle("-fx-padding: 10; -fx-alignment: center;");

        // Set the scene and show the dashboard
        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Cashier Dashboard");
        primaryStage.show();
    }

    private TableView<Item> createItemsTable() {
        TableView<Item> table = new TableView<>(availableItems);

        TableColumn<Item, String> nameColumn = new TableColumn<>("Item Name");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        TableColumn<Item, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getSellingPrice()).asObject());

        TableColumn<Item, Integer> stockColumn = new TableColumn<>("Stock");
        stockColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getStockLevel()).asObject());

        table.getColumns().addAll(nameColumn, priceColumn, stockColumn);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return table;
    }

    private TableView<Item> createBillTable() {
        TableView<Item> table = new TableView<>(billItems);

        TableColumn<Item, String> nameColumn = new TableColumn<>("Item Name");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        TableColumn<Item, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getStockLevel()).asObject());

        TableColumn<Item, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getSellingPrice()).asObject());

        table.getColumns().addAll(nameColumn, quantityColumn, priceColumn);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return table;
    }

    private void addItemToBill() {
        Item selectedItem = itemsTable.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            showAlert(Alert.AlertType.ERROR, "No Item Selected", "Please select an item to add to the bill.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Enter Quantity");
        dialog.setHeaderText("Enter the quantity for " + selectedItem.getName());
        dialog.setContentText("Quantity:");

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            try {
                int quantity = Integer.parseInt(result.get());
                if (quantity <= 0 || quantity > selectedItem.getStockLevel()) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Quantity",
                            "Please enter a valid quantity between 1 and " + selectedItem.getStockLevel());
                    return;
                }

                // Add item to the bill
                Item itemForBill = new Item(selectedItem.getName(), selectedItem.getCategory(),
                        selectedItem.getPurchasePrice(), selectedItem.getSellingPrice(), quantity);
                billItems.add(itemForBill);

                // Update stock level and total
                selectedItem.setStockLevel(selectedItem.getStockLevel() - quantity);
                inventory.saveInventory();
                updateTotal();

                // Refresh available items table
                itemsTable.refresh();

            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid number for quantity.");
            }
        }
    }

    private void finalizeBill(Stage primaryStage) {
        if (billItems.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Empty Bill", "Please add items to the bill before finalizing.");
            return;
        }

        Bill bill = cashier.createBill();
        for (Item item : billItems) {
            bill.addItem(item, item.getStockLevel());
        }

        cashier.saveBillToFile(bill);
        showAlert(Alert.AlertType.INFORMATION, "Bill Finalized",
                "Bill created successfully!\nTotal: $" + bill.getTotalAmount());

        // Clear bill items and reset total
        billItems.clear();
        updateTotal();
        primaryStage.close();
    }

    private void updateTotal() {
        double total = billItems.stream().mapToDouble(item -> item.getSellingPrice() * item.getStockLevel()).sum();
        totalLabel.setText(String.format("Total: $%.2f", total));
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
