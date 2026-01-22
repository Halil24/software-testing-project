package project.projecte.Model;

import java.util.List;

public class SalesStatistics {
    
    private String cashierName;
    private Double totalRevenue;

    // Constructor for JavaFX TableView binding
    public SalesStatistics(String cashierName, Double totalRevenue) {
        this.cashierName = cashierName;
        this.totalRevenue = totalRevenue;
    }

    // Default constructor
    public SalesStatistics() {
        this.cashierName = "";
        this.totalRevenue = 0.0;
    }

    // Getters for JavaFX PropertyValueFactory
    public String getCashierName() {
        return cashierName;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    // This method will generate a report for inventory and sales statistics.
    public void generateReport(Inventory inventory) {
        // Fetch items from the inventory
        List<Item> items = inventory.getItems();
        
        // If the inventory is empty
        if (items.isEmpty()) {
            System.out.println("No items in inventory to generate statistics.");
            return;
        }

        // Displaying the header of the report
        System.out.println("------ Sales and Inventory Statistics ------");
        System.out.printf("%-20s %-10s %-10s %-15s\n", "Item Name", "Quantity", "Price", "Total Sales");

        // Iterating over each item in the inventory to calculate and display sales statistics
        for (Item item : items) {
            double totalSales = item.getStockLevel() * item.getSellingPrice(); // Calculate the total sales for the item
            System.out.printf("%-20s %-10d %-10.2f %-15.2f\n", item.getName(), item.getStockLevel(), item.getSellingPrice(), totalSales);
        }

        // Optional: Total sales for all items
        double totalInventorySales = calculateTotalSales(items);
        System.out.println("\nTotal Sales for All Items: " + totalInventorySales);
    }

    // Helper method to calculate total sales for all items in the inventory
    private double calculateTotalSales(List<Item> items) {
        double totalSales = 0;
        for (Item item : items) {
            totalSales += item.getStockLevel() * item.getSellingPrice();
        }
        return totalSales;
    }

}
