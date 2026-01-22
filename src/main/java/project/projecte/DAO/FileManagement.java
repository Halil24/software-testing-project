package project.projecte.DAO;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import project.projecte.Model.Item;
import project.projecte.Model.User;
import project.projecte.Model.Admin;
import project.projecte.Model.Manager;
import project.projecte.Model.Cashier;

public class FileManagement {

    public static void saveUsers(String filename, List<User> users) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (User user : users) {
                writer.write(user.getUsername() + "," + user.getPassword() + "," + user.getRole());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    public static List<User> loadUsers(String filename) {
        List<User> users = new ArrayList<>();
        File file = new File(filename);
        if (!file.exists()) {
            InputStream resourceStream = FileManagement.class.getResourceAsStream("/" + filename);
            if (resourceStream == null) {
                System.err.println("Error loading users: missing file and resource: " + filename);
                return users;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data.length >= 3) {
                        String username = data[0];
                        String password = data[1];
                        String role = data[2];

                        switch (role) {
                            case "Administrator" -> users.add(new Admin(username, password, null));
                            case "Manager" -> users.add(new Manager(username, password, null, null, null));
                            case "Cashier" -> users.add(new Cashier(username, password, "General"));
                            default -> users.add(new User(username, password, role));
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Error loading users: " + e.getMessage());
            }
            return users;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 3) {
                    String username = data[0];
                    String password = data[1];
                    String role = data[2];

                    switch (role) {
                        case "Administrator" -> users.add(new Admin(username, password, null));
                        case "Manager" -> users.add(new Manager(username, password, null, null, null));
                        case "Cashier" -> users.add(new Cashier(username, password, "General"));
                        default -> users.add(new User(username, password, role));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
        return users;
    }

    public static void saveItems(String filename, List<Item> items) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Item item : items) {
                writer.write(item.getName() + "," + item.getCategory() + "," +
                        item.getPurchasePrice() + "," + item.getSellingPrice() + "," + item.getStockLevel());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving items: " + e.getMessage());
        }
    }

    public static List<Item> loadItems(String filename) {
        List<Item> items = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 5) { // Ensure there are exactly 5 fields for each item
                    String name = data[0];
                    String category = data[1];
                    double purchasePrice = Double.parseDouble(data[2]);
                    double sellingPrice = Double.parseDouble(data[3]);
                    int stockLevel = Integer.parseInt(data[4]);

                    // Create a new Item object with the correct order of parameters
                    items.add(new Item(name, category, purchasePrice, sellingPrice, stockLevel));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading items: " + e.getMessage());
        }
        return items;
    }
}
