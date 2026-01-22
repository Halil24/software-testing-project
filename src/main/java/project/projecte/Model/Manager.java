package project.projecte.Model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
// import javax.swing.JOptionPane; // Removed - causes JavaFX freezing
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class Manager extends User {
    private Inventory inventory; // The inventory managed by the manager
    private List<Cashier> cashiers; // List of cashiers under the manager
    private List<Supplier> suppliers; // List of suppliers providing products
    
    private static final String SUPPLIERS_FILE_PATH = "data/suppliers.dat";
    
    public Manager(String username, String password, Inventory inventory, List<Cashier> cashiers, List<Supplier> suppliers) {
        super(username, password, "Manager");
        this.inventory = inventory;
        this.cashiers = cashiers != null ? cashiers : new ArrayList<>();
        this.suppliers = (suppliers != null) ? suppliers : loadSuppliers(); ;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public List<Supplier> getSuppliers() {
        return suppliers;
    }

    // Restock an existing item in the inventory
    public void restockItem(String name, int quantity) {
        Item item = inventory.findItemByName(name);
        if (item != null) {
            item.setStockLevel(item.getStockLevel() + quantity);
            inventory.saveInventory();
            System.out.println("Item restocked: " + name + " (" + quantity + " added)");
        } else {
        	System.out.println("Item not found: " + name);
        }
    }

    // Modify an existing item's details
    public void modifyItem(String name, int stock, double sellingPrice) {
        Item item = inventory.findItemByName(name);
        if (item != null) {
            item.setStockLevel(stock);
            item.setSellingPrice(sellingPrice);
            inventory.saveInventory();
            System.out.println("Item modified: " + name);
        } else {
        	System.out.println("Item not found: " + name);
        }
    }

    // Notify if any item in the inventory is below the threshold
    // Note: UI handled by ManagerView.viewLowStock() now
    public void checkLowStock(int threshold) {
        List<Item> lowStockItems = inventory.getItems().stream()
                .filter(item -> item.getStockLevel() < threshold)
                .toList();
        if (lowStockItems.isEmpty()) {
            System.out.println("No items below stock threshold.");
        } else {
        	System.out.println("Low stock items:");
            for (Item item : lowStockItems) {
            	System.out.println("- " + item.getName() + ": " + item.getStockLevel() + " left");
            }
        }
    }

    public void generateStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        Stage stage = new Stage();
        stage.setTitle("Statistics from " + startDate.toLocalDate() + " to " + endDate.toLocalDate());

        // Root layout
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #F1ECFF;");

        // Ensure inventory is loaded
        if (inventory == null) {
            inventory = new Inventory();
        }
        inventory.loadInventory();
        
        System.out.println("DEBUG: Inventory items count: " + inventory.getItems().size());

        // Tables for statistics
        TableView<SalesStatistics> salesTable = new TableView<>();
        TableColumn<SalesStatistics, String> cashierColumn = new TableColumn<>("Cashier");
        cashierColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCashierName()));
        cashierColumn.setPrefWidth(200);
        TableColumn<SalesStatistics, Double> revenueColumn = new TableColumn<>("Total Revenue");
        revenueColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getTotalRevenue()).asObject());
        revenueColumn.setPrefWidth(200);
        salesTable.getColumns().addAll(cashierColumn, revenueColumn);

        TableView<Item> inventoryTable = new TableView<>();
        TableColumn<Item, String> itemNameColumn = new TableColumn<>("Item Name");
        itemNameColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        itemNameColumn.setPrefWidth(200);
        TableColumn<Item, Integer> stockLevelColumn = new TableColumn<>("Stock Level");
        stockLevelColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getStockLevel()).asObject());
        stockLevelColumn.setPrefWidth(150);
        TableColumn<Item, Double> sellingPriceColumn = new TableColumn<>("Selling Price");
        sellingPriceColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getSellingPrice()).asObject());
        sellingPriceColumn.setPrefWidth(150);
        inventoryTable.getColumns().addAll(itemNameColumn, stockLevelColumn, sellingPriceColumn);

        // Always reload cashiers with bills for fresh data
        UserManager userManager = new UserManager();
        List<Cashier> loadedCashiers = userManager.getUsers().stream()
                .filter(u -> u instanceof Cashier)
                .map(u -> (Cashier) u)
                .toList();
        
        System.out.println("DEBUG: Cashiers found: " + loadedCashiers.size());
        
        // Load bills for each cashier
        BillManager billManager = new BillManager();
        List<Bill> allBills = billManager.getBills();
        
        System.out.println("DEBUG: Total bills found: " + allBills.size());
        
        for (Cashier cashier : loadedCashiers) {
            cashier.getBills().clear();
            List<Bill> cashierBills = allBills.stream()
                    .filter(b -> b.getCashierUsername() != null
                            && b.getCashierUsername().equalsIgnoreCase(cashier.getUsername()))
                    .toList();
            cashier.getBills().addAll(cashierBills);
            System.out.println("DEBUG: Cashier " + cashier.getUsername() + " has " + cashierBills.size() + " bills");
        }

        // Populate sales table
        ObservableList<SalesStatistics> salesData = FXCollections.observableArrayList();
        double totalRevenue = 0.0;
        
        for (Cashier cashier : loadedCashiers) {
            double cashierRevenue = cashier.getBills().stream()
                    .filter(bill -> !bill.getBillDate().isBefore(startDate) && !bill.getBillDate().isAfter(endDate))
                    .mapToDouble(Bill::getTotalAmount)
                    .sum();
            salesData.add(new SalesStatistics(cashier.getUsername(), cashierRevenue));
            totalRevenue += cashierRevenue;
            System.out.println("DEBUG: Cashier " + cashier.getUsername() + " revenue in period: $" + cashierRevenue);
        }
        
        // Add total row
        if (!salesData.isEmpty()) {
            salesData.add(new SalesStatistics("TOTAL", totalRevenue));
        }
        salesTable.setItems(salesData);
        
        System.out.println("DEBUG: Total revenue: $" + totalRevenue);

        // Populate inventory table
        ObservableList<Item> inventoryData = FXCollections.observableArrayList();
        inventoryData.addAll(inventory.getItems());
        inventoryTable.setItems(inventoryData);
        
        System.out.println("DEBUG: Inventory table populated with " + inventoryData.size() + " items");

        // Add components to root layout with labels
        Label salesLabel = new Label("Sales Statistics:");
        salesLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Label inventoryLabel = new Label("Inventory Statistics:");
        inventoryLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Add info labels if no data
        if (salesData.isEmpty()) {
            Label noSalesLabel = new Label("No sales data available for the selected period.");
            noSalesLabel.setStyle("-fx-text-fill: gray; -fx-font-style: italic;");
            root.getChildren().addAll(salesLabel, noSalesLabel);
        } else {
            root.getChildren().addAll(salesLabel, salesTable);
        }
        
        if (inventoryData.isEmpty()) {
            Label noInventoryLabel = new Label("No inventory items available.");
            noInventoryLabel.setStyle("-fx-text-fill: gray; -fx-font-style: italic;");
            root.getChildren().addAll(inventoryLabel, noInventoryLabel);
        } else {
            root.getChildren().addAll(inventoryLabel, inventoryTable);
        }

        // Set up stage
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();
    }
    
    // Add a supplier to the list
    public void addSupplier(Supplier supplier) {
        suppliers.add(supplier);
        saveSuppliers();
        System.out.println("Supplier added: " + supplier.getName());
    }

    // Save the suppliers list to a file
    public void saveSuppliers() {
        try {
            // Ensure the directory exists before writing the file
            File directory = new File("data");
            if (!directory.exists()) {
                directory.mkdir(); // Create the directory if it doesn't exist
            }

            // Write to the suppliers.dat file
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SUPPLIERS_FILE_PATH))) {
                oos.writeObject(suppliers);
                System.out.println("Suppliers saved to file.");
            }
        } catch (IOException e) {
            System.err.println("Error saving suppliers: " + e.getMessage());
        }
    }

    // Load the suppliers list from a file
    private List<Supplier> loadSuppliers() {
        try {
            File file = new File(SUPPLIERS_FILE_PATH);
            if (!file.exists()) {
                System.out.println("No suppliers file found, creating sample data.");
                List<Supplier> sampleSuppliers = new ArrayList<>();
                Supplier alpha = new Supplier("Alpha Wholesale", "alpha@wholesale.com");
                alpha.addProduct("Laptops");
                alpha.addProduct("Monitors");
                sampleSuppliers.add(alpha);

                Supplier beta = new Supplier("Beta Foods", "beta@foods.com");
                beta.addProduct("Snacks");
                beta.addProduct("Beverages");
                sampleSuppliers.add(beta);

                Supplier gamma = new Supplier("Gamma Tech", "gamma@tech.com");
                gamma.addProduct("Keyboards");
                gamma.addProduct("Mice");
                sampleSuppliers.add(gamma);

                suppliers = sampleSuppliers;
                saveSuppliers();
                return sampleSuppliers;
            }
            
            // Read from the suppliers.dat file
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SUPPLIERS_FILE_PATH))) {
                return (List<Supplier>) ois.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading suppliers: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    
}
