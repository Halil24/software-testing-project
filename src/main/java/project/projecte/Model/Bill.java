package project.projecte.Model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Bill implements Serializable {

    private static final long serialVersionUID = 1L;
    private int billNumber;
    private LocalDateTime billDate;
    private String cashierUsername;
    private List<BillItem> billItems;

    public Bill(int billNumber, String cashierUsername) {
        this.billNumber = billNumber;
        this.cashierUsername = cashierUsername;
        this.billDate = LocalDateTime.now();
        this.billItems = new ArrayList<>();
    }

    public String getCashierUsername() {
        return cashierUsername;
    }

    public int getBillNumber() {
        return billNumber;
    }

    public LocalDateTime getBillDate() {
        return billDate;
    }

    public void addItem(Item item, int quantity) {
        billItems.add(new BillItem(item.getName(), item.getSellingPrice(), quantity));
    }

    public List<BillItem> getBillItems() {
        return billItems;
    }

    public double getTotalAmount() {
        return billItems.stream()
                .mapToDouble(billItem -> billItem.getSellingPrice() * billItem.getQuantity())
                .sum();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Bill Number: ").append(billNumber).append("\n");
        builder.append("Date: ").append(billDate).append("\n");
        builder.append("Items:\n");
        for (BillItem item : billItems) {
            builder.append(item).append("\n");
        }
        builder.append("Total Amount: $").append(getTotalAmount());
        return builder.toString();
    }
}
