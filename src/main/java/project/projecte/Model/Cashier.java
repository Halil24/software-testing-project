package project.projecte.Model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

public class Cashier extends User {
    private List<Bill> bills; // List of bills handled by the cashier
    private int nextBillNumber; // To track the next bill number for unique bill generation
    private String sector; // Sector assigned to the cashier

    public Cashier(String username, String password, String sector) {
        super(username, password, "Cashier");
        this.bills = new ArrayList<>();
        this.nextBillNumber = 1; // Start bill numbering from 1
        this.sector = sector;
    }

    public String getSector() {
        return sector;
    }

    public List<Bill> getBills() {
        return bills;
    }

    public List<Bill> getTodayBills() {
        return bills.stream()
                .filter(bill -> bill.getBillDate().toLocalDate().equals(LocalDate.now()))
                .collect(Collectors.toList());
    }

    /**
     * Calculates the total sales for the current day.
     *
     * @return The total sales amount for today's bills.
     */
    public double getTotalSalesToday() {
        return getTodayBills().stream()
                .mapToDouble(Bill::getTotalAmount)
                .sum();
    }

    public List<Bill> getBillsWithinDateRange(LocalDate startDate, LocalDate endDate) {
        return bills.stream()
                .filter(bill -> !bill.getBillDate().toLocalDate().isBefore(startDate) &&
                        !bill.getBillDate().toLocalDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    public Bill createBill() {
        Bill bill = new Bill(nextBillNumber++, getUsername());
        bills.add(bill);
        return bill;
    }

    public void saveBillToFile(Bill bill) {
        String filename = String.format("bills/Bill_%d_%s.txt", bill.getBillNumber(), LocalDate.now());
        filename = filename.replaceAll("[\\\\/:*?\"<>|]", "_");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("Bill Number: " + bill.getBillNumber() + "\n");
            writer.write("Date: " + bill.getBillDate() + "\n");
            writer.write("Items Purchased:\n");

            for (BillItem item : bill.getBillItems()) {
                writer.write(String.format("- %s (Qty: %d, Price: $%.2f)\n",
                        item.getName(), item.getQuantity(), item.getSellingPrice()));
            }

            writer.write(String.format("Total Amount: $%.2f\n", bill.getTotalAmount()));
            JOptionPane.showMessageDialog(null, "Bill saved to file: " + filename);

        } catch (IOException e) {
            System.err.println("Error saving bill to file: " + e.getMessage());
        }
    }
}
