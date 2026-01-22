package project.projecte.Model;

public class Item {
    private String name;
    private String category;  // Added to match the Inventory class
    private double purchasePrice;  // Added to match the Inventory class
    private double sellingPrice;
    private int stockLevel;

    // Updated constructor to include all necessary attributes
    public Item(String name, String category, double purchasePrice, double sellingPrice, int stockLevel) {
        this.name = name;
        this.category = category;
        this.purchasePrice = purchasePrice;
        this.sellingPrice = sellingPrice;
        this.stockLevel = stockLevel;
    }

    // Getters and setters for all attributes
    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public int getStockLevel() {
        return stockLevel;
    }

    public void setStockLevel(int stockLevel) {
        this.stockLevel = stockLevel;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    // Convert Item to a string representation
    @Override
    public String toString() {
        return name + " (" + category + ") - Purchase Price: $" + purchasePrice + ", Selling Price: $" + sellingPrice + ", Stock: " + stockLevel;
    }
}
