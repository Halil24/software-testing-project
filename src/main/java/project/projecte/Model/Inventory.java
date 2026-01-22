package project.projecte.Model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private List<Item> items;
    private String filename = "data/inventory.txt";

    public Inventory() {
        this.items = new ArrayList<>();
        loadInventory();
    }

    // Add a new item to the inventory
    public void addItem(Item item) {
        items.add(item);
    }

    // Get the list of all items in the inventory
    public List<Item> getItems() {
        return items;
    }

    // Find an item by its name (case-insensitive)
    public Item findItemByName(String name) {
        return items.stream()
                .filter(item -> item.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    // Save the inventory to a file
    public void saveInventory() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Item item : items) {
                writer.write(String.format("%s,%s,%.2f,%.2f,%d",
                        item.getName(),
                        item.getCategory(),
                        item.getPurchasePrice(),
                        item.getSellingPrice(),
                        item.getStockLevel()));
                writer.newLine();
                
            }
        } catch (IOException e) {
            System.err.println("Error saving inventory: " + e.getMessage());
        }
    }

    // Load the inventory from a file
    public void loadInventory() {
        items.clear(); // Clear the current list before loading new data
        System.out.println("DEBUG Inventory.loadInventory: Loading from " + filename);
        
        File file = new File(filename);
        if (!file.exists()) {
            System.err.println("DEBUG Inventory.loadInventory: File does not exist: " + filename);
            System.err.println("DEBUG Inventory.loadInventory: Absolute path: " + file.getAbsolutePath());
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineCount = 0;
            while ((line = reader.readLine()) != null) {
                lineCount++;
                System.out.println("DEBUG Inventory.loadInventory: Read line " + lineCount + ": " + line);
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String name = parts[0];
                    String category = parts[1];
                    double purchasePrice = Double.parseDouble(parts[2]);
                    double sellingPrice = Double.parseDouble(parts[3]);
                    int stockLevel = Integer.parseInt(parts[4]);
                    items.add(new Item(name, category, purchasePrice, sellingPrice, stockLevel));
                    System.out.println("DEBUG Inventory.loadInventory: Added item: " + name);
                } else {
                    System.err.println("DEBUG Inventory.loadInventory: Invalid line format (expected 5 parts, got " + parts.length + "): " + line);
                }
            }
            System.out.println("DEBUG Inventory.loadInventory: Loaded " + items.size() + " items total");
        } catch (IOException e) {
            System.err.println("Error loading inventory: " + e.getMessage());
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("Error parsing inventory numbers: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Update the stock level of an existing item
    public boolean updateStockLevel(String name, int newStockLevel) {
        Item item = findItemByName(name);
        if (item != null) {
            item.setStockLevel(newStockLevel);
            return true;
        }
        return false;
    }

    // Remove an item from the inventory
    public boolean removeItem(String name) {
        return items.removeIf(item -> item.getName().equalsIgnoreCase(name));
    }

    // Display all items (for debugging or logs)
    public void displayItems() {
        if (items.isEmpty()) {
            System.out.println("No items in the inventory.");
        } else {
            System.out.println("Inventory Items:");
            for (Item item : items) {
                System.out.println(item);
            }
        }
    }

    // Add a new category to the inventory (creates a new item with that category)
    public void addNewItemCategory(String categoryName) {
        // Check if the category already exists in inventory
        // If not, create a new dummy item to represent the category
        Item newCategoryItem = new Item("New " + categoryName + " Item", categoryName, 0.0, 0.0, 0);
        addItem(newCategoryItem);
        saveInventory();
    }
}
