package project.projecte.View;

import project.projecte.Controller.LoginController;
import project.projecte.Model.Bill;
import project.projecte.Model.BillItem;
import project.projecte.Model.BillManager;
import project.projecte.Model.Inventory;
import project.projecte.Model.Item;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Optional;

public class CashierView {

    private final BillManager billManager;
    private final Inventory inventory;
    private final String cashierUsername;

    public CashierView(Inventory inventory, String cashierUsername) {
        this.billManager = new BillManager();
        this.inventory = inventory;
        this.cashierUsername = cashierUsername;
        System.out.println("DEBUG CashierView: Created with cashier username: " + cashierUsername);
    }

    public void showCashierDashboard(Stage stage) {
        Button createBillButton = createStyledButton("Create Bill", "#4CAF50");
        Button viewBillsButton = createStyledButton("View Bills in Date Range", "#B085FF");
        Button logoutButton = createStyledButton("Log out", "#8533D7");

        ListView<String> billsListView = new ListView<>();
        billsListView.setStyle("-fx-font-size: 14px; -fx-background-color: #f0f0f0;");
        billsListView.setPrefHeight(250);

        createBillButton.setOnAction(e -> createBillForm(stage));
        viewBillsButton.setOnAction(e -> viewBillsInDateRange(billsListView));
        logoutButton.setOnAction(event -> navigateBack(stage));

        VBox layout = new VBox(20, createBillButton, viewBillsButton, billsListView, logoutButton);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #f4f4f9;");

        Scene scene = new Scene(layout, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Cashier Dashboard");
        stage.show();
    }

    private void createBillForm(Stage stage) {
        Stage billStage = new Stage();
        billStage.setTitle("Create Bill");

        Label header = new Label("Add Items to Bill");
        header.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        ComboBox<Item> itemComboBox = new ComboBox<>();
        itemComboBox.getItems().addAll(inventory.getItems());
        itemComboBox.setPromptText("Select Item");

        TextField quantityField = new TextField();
        quantityField.setPromptText("Enter Quantity");

        Button addItemButton = createStyledButton("Add Item", "#4CAF50");
        TableView<Item> tableView = createBillTable();

        Label totalLabel = new Label("Total: $0.00");
        totalLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Since CashierView doesn't have a reference to the Cashier object directly,
        // we use a placeholder or better yet, we should have passed the cashier to
        // CashierView.
        // For now, let's use "General" or find a way to get it.
        // Create bill with actual cashier username
        Bill bill = new Bill(billManager.getBills().size() + 1, cashierUsername);
        System.out.println("DEBUG CashierView: Creating bill with cashier username: " + cashierUsername);

        addItemButton.setOnAction(e -> {
            Item selectedItem = itemComboBox.getValue();
            String quantityText = quantityField.getText();

            if (selectedItem == null || quantityText.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Please select an item and enter a quantity.");
                return;
            }

            try {
                int quantity = Integer.parseInt(quantityText);
                if (quantity <= 0 || quantity > selectedItem.getStockLevel()) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Quantity",
                            "Quantity must be between 1 and " + selectedItem.getStockLevel() + ".");
                    return;
                }

                // Update inventory and bill
                selectedItem.setStockLevel(selectedItem.getStockLevel() - quantity);
                bill.addItem(selectedItem, quantity);
                inventory.saveInventory();

                // Update table and total
                tableView.getItems().add(new Item(selectedItem.getName(), selectedItem.getCategory(),
                        selectedItem.getPurchasePrice(), selectedItem.getSellingPrice(), quantity));
                totalLabel.setText(String.format("Total: $%.2f", bill.getTotalAmount()));

                // Reset fields
                itemComboBox.setValue(null);
                quantityField.clear();
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Quantity must be a valid number.");
            }
        });

        Button previewBillButton = createStyledButton("Preview Bill", "#B085FF");
        previewBillButton.setOnAction(e -> showBillPreview(bill));

        Button finalizeBillButton = createStyledButton("Finalize Bill", "#8533D7");
        finalizeBillButton.setOnAction(e -> {
            if (bill.getBillItems().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Empty Bill", "Please add at least one item to the bill.");
            } else {
                saveBillToFile(bill); // Save the bill in the specified format
                billManager.addBill(bill);
                billManager.saveBillToFile(bill);
                showAlert(Alert.AlertType.INFORMATION, "Bill Finalized",
                        "Bill created successfully!\nTotal: $" + bill.getTotalAmount());
                billStage.close();
            }
        });

        VBox layout = new VBox(15, header, itemComboBox, quantityField, addItemButton, tableView, totalLabel,
                previewBillButton, finalizeBillButton);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #F1ECFF;");
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 600, 600);
        billStage.setScene(scene);
        billStage.show();
    }

    private void showBillPreview(Bill bill) {
        Stage previewStage = new Stage();
        previewStage.setTitle("Bill Preview");

        TextArea previewArea = new TextArea();
        previewArea.setEditable(false);
        previewArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 14px;");

        StringBuilder previewContent = new StringBuilder();
        previewContent.append("********** BILL **********\n");
        previewContent.append("Bill Number: ").append(bill.getBillNumber()).append("\n");
        previewContent.append("Date: ").append(LocalDate.now()).append("\n");
        previewContent.append("--------------------------\n");

        for (BillItem item : bill.getBillItems()) {
            previewContent.append(String.format("%-20s %5d x $%.2f = $%.2f%n",
                    item.getName(),
                    item.getQuantity(),
                    item.getSellingPrice(),
                    item.getQuantity() * item.getSellingPrice()));
        }

        previewContent.append("--------------------------\n");
        previewContent.append(String.format("Total Amount: $%.2f%n", bill.getTotalAmount()));
        previewContent.append("**************************");

        previewArea.setText(previewContent.toString());

        VBox layout = new VBox(10, previewArea);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 400, 400);
        previewStage.setScene(scene);
        previewStage.show();
    }

    private void saveBillToFile(Bill bill) {
        // Generate the filename in the format [BillNrDate].txt
        String fileName = String.format("Bill%d_%s.txt", bill.getBillNumber(), LocalDate.now());
        File billFile = new File(fileName);

        try (PrintWriter writer = new PrintWriter(billFile)) {
            writer.println("********** BILL **********");
            writer.println("Bill Number: " + bill.getBillNumber());
            writer.println("Date: " + LocalDate.now());
            writer.println("--------------------------");

            for (BillItem item : bill.getBillItems()) {
                writer.printf("%-20s %5d x $%.2f = $%.2f%n",
                        item.getName(),
                        item.getQuantity(),
                        item.getSellingPrice(),
                        item.getQuantity() * item.getSellingPrice());
            }

            writer.println("--------------------------");
            writer.printf("Total Amount: $%.2f%n", bill.getTotalAmount());
            writer.println("**************************");

            showAlert(Alert.AlertType.INFORMATION, "Bill Saved", "Bill saved as: " + fileName);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Save Error", "Failed to save the bill: " + e.getMessage());
        }
    }

    private TableView<Item> createBillTable() {
        TableView<Item> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setPrefHeight(250);

        TableColumn<Item, String> nameCol = new TableColumn<>("Item Name");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

        TableColumn<Item, Integer> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getStockLevel()).asObject());

        TableColumn<Item, Double> priceCol = new TableColumn<>("Price ($)");
        priceCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getSellingPrice()).asObject());

        tableView.getColumns().addAll(nameCol, quantityCol, priceCol);
        return tableView;
    }

    private void viewBillsInDateRange(ListView<String> billsListView) {
        DatePicker startDatePicker = new DatePicker();
        DatePicker endDatePicker = new DatePicker();

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Select Date Range");
        VBox datePickerLayout = new VBox(10, new Label("Start Date:"), startDatePicker, new Label("End Date:"),
                endDatePicker);
        datePickerLayout.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(datePickerLayout);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            if (startDate != null && endDate != null && !startDate.isAfter(endDate)) {
                billsListView.getItems().clear();
                for (Bill bill : billManager.getBillsWithinDateRange(startDate, endDate)) {
                    billsListView.getItems().add(bill.toString());
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Invalid Date Range", "Please select a valid date range.");
            }
        }
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle(String.format(
                "-fx-background-color: %s; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10px 20px;", color));
        return button;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void navigateBack(Stage primaryStage) {
        // Implement the navigation logic for "Back" button
        LoginController login = new LoginController(primaryStage);
        new LoginView(primaryStage, login);
    }
}
