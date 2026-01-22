package project.projecte.Model;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BillManager {

    private List<Bill> bills;
    private final String billsFile = "data/bills_data.ser";

    public BillManager() {
        this.bills = new ArrayList<>();
        loadBillsFromFile();
        System.out.println("DEBUG BillManager: Loaded " + bills.size() + " bills from " + billsFile);
    }

    public void addBill(Bill bill) {
        if (bill != null) {  // Don't add null bills
            bills.add(bill);
            saveAllBillsToFile();
        } else {
            System.err.println("Warning: Attempted to add null bill - ignoring");
        }
    }

    public List<Bill> getBills() {
        return bills;
    }

    public List<Bill> getTodayBills() {
        return bills.stream()
                .filter(bill -> bill.getBillDate().toLocalDate().equals(LocalDate.now()))
                .collect(Collectors.toList());
    }

    public List<Bill> getBillsWithinDateRange(LocalDate startDate, LocalDate endDate) {
        return bills.stream()
                .filter(bill -> !bill.getBillDate().toLocalDate().isBefore(startDate) &&
                        !bill.getBillDate().toLocalDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    public void saveBillToFile(Bill bill) {
        String filename = "bills/Bill" + bill.getBillNumber() + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(bill.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveAllBillsToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(billsFile))) {
            oos.writeObject(bills);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to save bills to file: " + billsFile);
        }
    }


    private void loadBillsFromFile() {
        File file = new File(billsFile);
        System.out.println("DEBUG BillManager.loadBillsFromFile: Looking for " + file.getAbsolutePath());
        if (file.exists()) {
            System.out.println("DEBUG BillManager.loadBillsFromFile: File exists, loading...");
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                List<Bill> loadedBills = (List<Bill>) ois.readObject();
                System.out.println("DEBUG BillManager.loadBillsFromFile: Successfully loaded " + loadedBills.size() + " bills");
                
                // Filter out null bills (in case of corrupted data)
                bills = loadedBills.stream()
                        .filter(bill -> bill != null)
                        .collect(Collectors.toList());
                
                System.out.println("DEBUG BillManager.loadBillsFromFile: After filtering nulls: " + bills.size() + " valid bills");
                for (Bill bill : bills) {
                    System.out.println("DEBUG BillManager.loadBillsFromFile: Bill #" + bill.getBillNumber() + 
                                     " - Cashier: " + bill.getCashierUsername() + 
                                     " - Date: " + bill.getBillDate() + 
                                     " - Amount: $" + bill.getTotalAmount());
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                System.err.println("Failed to load bills from file: " + billsFile);
            }
        } else {
            System.err.println("DEBUG BillManager.loadBillsFromFile: File does not exist!");
        }
    }
}
