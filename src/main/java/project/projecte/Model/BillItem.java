package project.projecte.Model;

import java.io.Serializable;

public class BillItem implements Serializable {

    private static final long serialVersionUID = 1L;
    private String name;
    private double sellingPrice;
    private int quantity;

    public BillItem(String name, double sellingPrice, int quantity) {
        this.name = name;
        this.sellingPrice = sellingPrice;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return String.format("%s (Qty: %d, Price: $%.2f)", name, quantity, sellingPrice);
    }
}
