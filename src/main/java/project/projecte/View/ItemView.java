package project.projecte.View;

import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import project.projecte.Model.Manager;
import project.projecte.Model.Item;

public class ItemView {

    private final Manager manager;

    public ItemView(Manager manager) {
        this.manager = manager;
    }

    public void showItemView(Stage stage) {
        Stage itemStage = new Stage();
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        TableView<Item> itemTable = new TableView<>();
        TableColumn<Item, String> nameColumn = new TableColumn<>("Item Name");
        TableColumn<Item, String> categoryColumn = new TableColumn<>("Category");
        TableColumn<Item, Double> purchasePriceColumn = new TableColumn<>("Purchase Price");
        TableColumn<Item, Double> sellingPriceColumn = new TableColumn<>("Selling Price");
        TableColumn<Item, Integer> stockColumn = new TableColumn<>("Stock Level");

        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        categoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory()));
        purchasePriceColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPurchasePrice()).asObject());
        sellingPriceColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getSellingPrice()).asObject());
        stockColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getStockLevel()).asObject());

        itemTable.getColumns().add(nameColumn);
        itemTable.getColumns().add(categoryColumn);
        itemTable.getColumns().add(purchasePriceColumn);
        itemTable.getColumns().add(sellingPriceColumn);
        itemTable.getColumns().add(stockColumn);
        itemTable.setItems((ObservableList<Item>) manager.getInventory().getItems()); // assuming manager.getInventory().getItems() returns an ObservableList<Item>

        TextField nameField = new TextField();
        nameField.setPromptText("Item Name");

        TextField categoryField = new TextField();
        categoryField.setPromptText("Category");

        TextField purchasePriceField = new TextField();
        purchasePriceField.setPromptText("Purchase Price");

        TextField sellingPriceField = new TextField();
        sellingPriceField.setPromptText("Selling Price");

        TextField stockField = new TextField();
        stockField.setPromptText("Stock Level");

        Button addItemButton = createStyledButton("Add Item", "#4CAF50");
        addItemButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            String category = categoryField.getText().trim();
            try {
                double purchasePrice = Double.parseDouble(purchasePriceField.getText().trim());
                double sellingPrice = Double.parseDouble(sellingPriceField.getText().trim());
                int stockLevel = Integer.parseInt(stockField.getText().trim());

                if (!name.isEmpty() && !category.isEmpty()) {
                    Item item = new Item(name, category, purchasePrice, sellingPrice, stockLevel);
                    manager.getInventory().addItem(item);
                    itemTable.setItems((ObservableList<Item>) manager.getInventory().getItems()); // Refresh table
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Item added successfully.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Name and category are required.");
                }
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please enter valid prices and stock.");
            }
        });

        layout.getChildren().addAll(new Label("Manage Items"), itemTable, nameField, categoryField, purchasePriceField, sellingPriceField, stockField, addItemButton);

        Scene scene = new Scene(layout, 800, 500);
        itemStage.setScene(scene);
        itemStage.setTitle("Manage Items");
        itemStage.show();
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
