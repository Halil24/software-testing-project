package project.projecte.Integration;

import org.junit.jupiter.api.*;
import project.projecte.Model.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration Test: Bill Creation Workflow
 *
 * This test verifies the integration between:
 * - Bill (model)
 * - BillManager (persistence)
 * - Inventory (stock management)
 * - Item (products)
 *
 * Test Scenario: Complete bill creation workflow from cashier perspective
 *
 * Integration Points Tested:
 * 1. Bill + BillManager: Bill creation and storage
 * 2. Bill + Item: Adding items to bill
 * 3. Bill + BillManager + Date filtering: Retrieving bills by date
 * 4. Inventory + Item: Stock level validation (conceptual)
 *
 * @author Dea
 */
@DisplayName("Integration Test: Bill Creation Workflow")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BillCreationIntegrationTest {

    private static BillManager billManager;
    private static Inventory inventory;
    
    private Bill testBill;
    private Item apple;
    private Item banana;
    private Item carrot;

    @BeforeAll
    static void setUpClass() {
        System.out.println("=== Starting Bill Creation Integration Test ===");
        billManager = new BillManager();
        inventory = new Inventory();
    }

    @BeforeEach
    void setUp() {
        // Set up test data for each test
        apple = new Item("Apple", "Fruits", 0.50, 1.00, 100);
        banana = new Item("Banana", "Fruits", 0.30, 0.80, 50);
        carrot = new Item("Carrot", "Vegetables", 0.20, 0.60, 200);
        
        inventory.addItem(apple);
        inventory.addItem(banana);
        inventory.addItem(carrot);
        
        // Create a new bill for testing
        int billNumber = billManager.getBills().size() + 1;
        testBill = new Bill(billNumber, "testCashier");
    }

    @AfterEach
    void tearDown() {
        System.out.println("Test completed. Current bills count: " + billManager.getBills().size());
    }

    @AfterAll
    static void tearDownClass() {
        System.out.println("=== Bill Creation Integration Test Complete ===");
        System.out.println("Total bills created: " + billManager.getBills().size());
    }

    // ==================== Integration Test Cases ====================

    @Test
    @Order(1)
    @DisplayName("IT-01: Create bill and add single item")
    void testIntegration_CreateBillWithSingleItem() {
        System.out.println("\n--- IT-01: Create bill with single item ---");
        
        // Step 1: Create bill
        assertNotNull(testBill, "Bill should be created");
        assertEquals("testCashier", testBill.getCashierUsername(), "Cashier should be set");
        
        // Step 2: Verify inventory has item
        Item foundItem = inventory.findItemByName("Apple");
        assertNotNull(foundItem, "Item should exist in inventory");
        assertEquals(100, foundItem.getStockLevel(), "Initial stock should be 100");
        
        // Step 3: Add item to bill
        testBill.addItem(apple, 5);
        assertEquals(1, testBill.getBillItems().size(), "Bill should have 1 item");
        
        // Step 4: Calculate total
        double expectedTotal = 5 * 1.00; // 5 apples * $1.00
        assertEquals(expectedTotal, testBill.getTotalAmount(), 0.01, 
                    "Total should be calculated correctly");
        
        // Step 5: Add bill to manager
        billManager.addBill(testBill);
        assertTrue(billManager.getBills().contains(testBill), 
                  "Bill should be stored in manager");
        
        System.out.println("✓ Bill created successfully with total: $" + testBill.getTotalAmount());
    }

    @Test
    @Order(2)
    @DisplayName("IT-02: Create bill with multiple items and verify total calculation")
    void testIntegration_CreateBillWithMultipleItems() {
        System.out.println("\n--- IT-02: Create bill with multiple items ---");
        
        // Step 1: Add multiple items to bill
        testBill.addItem(apple, 10);   // 10 * $1.00 = $10.00
        testBill.addItem(banana, 5);   // 5 * $0.80 = $4.00
        testBill.addItem(carrot, 20);  // 20 * $0.60 = $12.00
        // Expected total: $26.00
        
        // Step 2: Verify bill items count
        assertEquals(3, testBill.getBillItems().size(), "Bill should have 3 items");
        
        // Step 3: Verify total calculation
        double expectedTotal = (10 * 1.00) + (5 * 0.80) + (20 * 0.60);
        assertEquals(expectedTotal, testBill.getTotalAmount(), 0.01,
                    "Total should sum all items correctly");
        
        // Step 4: Add to bill manager
        billManager.addBill(testBill);
        
        // Step 5: Verify bill can be retrieved
        List<Bill> todayBills = billManager.getTodayBills();
        assertTrue(todayBills.contains(testBill), "Bill should be retrievable from today's bills");
        
        System.out.println("✓ Multi-item bill total: $" + testBill.getTotalAmount());
    }

    @Test
    @Order(3)
    @DisplayName("IT-03: Verify stock should be updated after sale (conceptual)")
    void testIntegration_StockUpdateAfterSale() {
        System.out.println("\n--- IT-03: Stock update after sale (conceptual) ---");
        
        // Current Implementation Note:
        // The current system doesn't automatically update stock when items are added to bills
        // This test documents expected behavior for future implementation
        
        // Step 1: Record initial stock
        int initialAppleStock = apple.getStockLevel();
        System.out.println("Initial Apple stock: " + initialAppleStock);
        
        // Step 2: Create bill with items
        int quantitySold = 15;
        testBill.addItem(apple, quantitySold);
        billManager.addBill(testBill);
        
        // Step 3: Manually update stock (simulating what should happen automatically)
        int newStockLevel = initialAppleStock - quantitySold;
        boolean updated = inventory.updateStockLevel("Apple", newStockLevel);
        
        assertTrue(updated, "Stock update should succeed");
        
        // Step 4: Verify stock was updated
        Item updatedApple = inventory.findItemByName("Apple");
        assertEquals(newStockLevel, updatedApple.getStockLevel(),
                    "Stock should be reduced after sale");
        
        System.out.println("✓ Stock updated from " + initialAppleStock + 
                          " to " + updatedApple.getStockLevel());
        
        // TODO: Implement automatic stock reduction when items are sold
    }

    @Test
    @Order(4)
    @DisplayName("IT-04: Filter bills by date range")
    void testIntegration_FilterBillsByDateRange() {
        System.out.println("\n--- IT-04: Filter bills by date range ---");
        
        // Step 1: Create multiple bills
        Bill bill1 = new Bill(101, "cashier1");
        bill1.addItem(apple, 5);
        billManager.addBill(bill1);
        
        Bill bill2 = new Bill(102, "cashier2");
        bill2.addItem(banana, 3);
        billManager.addBill(bill2);
        
        Bill bill3 = new Bill(103, "cashier1");
        bill3.addItem(carrot, 10);
        billManager.addBill(bill3);
        
        // Step 2: Get today's bills
        List<Bill> todayBills = billManager.getTodayBills();
        assertTrue(todayBills.size() >= 3, "Should find at least 3 bills from today");
        
        // Step 3: Get bills within date range
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<Bill> rangedBills = billManager.getBillsWithinDateRange(yesterday, tomorrow);
        
        assertTrue(rangedBills.contains(bill1), "Should include bill1 in range");
        assertTrue(rangedBills.contains(bill2), "Should include bill2 in range");
        assertTrue(rangedBills.contains(bill3), "Should include bill3 in range");
        
        System.out.println("✓ Found " + rangedBills.size() + " bills in date range");
    }

    @Test
    @Order(5)
    @DisplayName("IT-05: Calculate total revenue from multiple bills")
    void testIntegration_CalculateTotalRevenue() {
        System.out.println("\n--- IT-05: Calculate total revenue ---");
        
        // Step 1: Create bills with known totals
        Bill bill1 = new Bill(201, "cashier1");
        bill1.addItem(apple, 10);  // $10.00
        billManager.addBill(bill1);
        
        Bill bill2 = new Bill(202, "cashier1");
        bill2.addItem(banana, 10); // $8.00
        billManager.addBill(bill2);
        
        Bill bill3 = new Bill(203, "cashier2");
        bill3.addItem(carrot, 10); // $6.00
        billManager.addBill(bill3);
        
        // Step 2: Calculate total revenue for cashier1
        List<Bill> allBills = billManager.getTodayBills();
        double cashier1Revenue = allBills.stream()
            .filter(bill -> "cashier1".equals(bill.getCashierUsername()))
            .mapToDouble(Bill::getTotalAmount)
            .sum();
        
        // Step 3: Verify revenue calculation
        double expectedCashier1Revenue = 10.00 + 8.00; // bill1 + bill2
        assertTrue(cashier1Revenue >= expectedCashier1Revenue, 
                  "Cashier1 revenue should be at least $18.00");
        
        // Step 4: Calculate total revenue for all cashiers
        double totalRevenue = allBills.stream()
            .mapToDouble(Bill::getTotalAmount)
            .sum();
        
        assertTrue(totalRevenue >= 24.00, "Total revenue should be at least $24.00");
        
        System.out.println("✓ Cashier1 revenue: $" + String.format("%.2f", cashier1Revenue));
        System.out.println("✓ Total revenue: $" + String.format("%.2f", totalRevenue));
    }

    @Test
    @Order(6)
    @DisplayName("IT-06: Verify bill with zero-price items")
    void testIntegration_BillWithFreeItems() {
        System.out.println("\n--- IT-06: Bill with free items ---");
        
        // Step 1: Create free item (promotional)
        Item freeItem = new Item("Sample", "Promo", 0.0, 0.0, 1000);
        inventory.addItem(freeItem);
        
        // Step 2: Create bill with mix of paid and free items
        testBill.addItem(apple, 5);      // $5.00
        testBill.addItem(freeItem, 100); // $0.00
        
        // Step 3: Verify total
        assertEquals(5.00, testBill.getTotalAmount(), 0.01,
                    "Free items should not affect total");
        
        // Step 4: Verify bill items count
        assertEquals(2, testBill.getBillItems().size(), 
                    "Bill should include free items in count");
        
        billManager.addBill(testBill);
        System.out.println("✓ Bill with free items handled correctly");
    }

    @Test
    @Order(7)
    @DisplayName("IT-07: Verify bills from different cashiers are tracked separately")
    void testIntegration_MultipleCashierTracking() {
        System.out.println("\n--- IT-07: Multiple cashier tracking ---");
        
        // Step 1: Create bills from different cashiers
        Bill cashier1Bill = new Bill(301, "cashier1");
        cashier1Bill.addItem(apple, 10);
        billManager.addBill(cashier1Bill);
        
        Bill cashier2Bill = new Bill(302, "cashier2");
        cashier2Bill.addItem(banana, 10);
        billManager.addBill(cashier2Bill);
        
        Bill cashier3Bill = new Bill(303, "cashier1");
        cashier3Bill.addItem(carrot, 10);
        billManager.addBill(cashier3Bill);
        
        // Step 2: Filter bills by cashier
        List<Bill> allBills = billManager.getTodayBills();
        
        long cashier1Count = allBills.stream()
            .filter(bill -> "cashier1".equals(bill.getCashierUsername()))
            .count();
        
        long cashier2Count = allBills.stream()
            .filter(bill -> "cashier2".equals(bill.getCashierUsername()))
            .count();
        
        // Step 3: Verify bills are tracked correctly
        assertTrue(cashier1Count >= 2, "Cashier1 should have at least 2 bills");
        assertTrue(cashier2Count >= 1, "Cashier2 should have at least 1 bill");
        
        System.out.println("✓ Cashier1 bills: " + cashier1Count);
        System.out.println("✓ Cashier2 bills: " + cashier2Count);
    }

    @Test
    @Order(8)
    @DisplayName("IT-08: End-to-end complete sale workflow")
    void testIntegration_CompleteSaleWorkflow() {
        System.out.println("\n--- IT-08: Complete sale workflow ---");
        
        // This test simulates the complete workflow of a sale transaction
        
        // Step 1: Cashier logs in (simulated)
        String cashierUsername = "testCashier";
        System.out.println("Step 1: Cashier '" + cashierUsername + "' logged in");
        
        // Step 2: Customer selects items
        System.out.println("Step 2: Customer selects items");
        Item selectedItem1 = inventory.findItemByName("Apple");
        Item selectedItem2 = inventory.findItemByName("Banana");
        
        assertNotNull(selectedItem1, "Apple should be in inventory");
        assertNotNull(selectedItem2, "Banana should be in inventory");
        
        // Step 3: Create new bill
        int billNumber = billManager.getBills().size() + 1;
        Bill saleBill = new Bill(billNumber, cashierUsername);
        System.out.println("Step 3: Bill #" + billNumber + " created");
        
        // Step 4: Add items to bill
        int appleQty = 7;
        int bananaQty = 5;
        saleBill.addItem(selectedItem1, appleQty);
        saleBill.addItem(selectedItem2, bananaQty);
        System.out.println("Step 4: Items added to bill");
        
        // Step 5: Calculate total
        double total = saleBill.getTotalAmount();
        double expectedTotal = (appleQty * 1.00) + (bananaQty * 0.80);
        assertEquals(expectedTotal, total, 0.01, "Total should be calculated correctly");
        System.out.println("Step 5: Total calculated: $" + total);
        
        // Step 6: Finalize bill (add to manager)
        billManager.addBill(saleBill);
        assertTrue(billManager.getBills().contains(saleBill), "Bill should be saved");
        System.out.println("Step 6: Bill finalized and saved");
        
        // Step 7: Update inventory stock (manual in current system)
        boolean appleUpdated = inventory.updateStockLevel("Apple", 
                                    selectedItem1.getStockLevel() - appleQty);
        boolean bananaUpdated = inventory.updateStockLevel("Banana", 
                                    selectedItem2.getStockLevel() - bananaQty);
        
        assertTrue(appleUpdated && bananaUpdated, "Stock should be updated");
        System.out.println("Step 7: Inventory stock updated");
        
        // Step 8: Verify bill can be retrieved for reporting
        List<Bill> todaysBills = billManager.getTodayBills();
        assertTrue(todaysBills.contains(saleBill), "Bill should be retrievable");
        System.out.println("Step 8: Bill retrievable for reporting");
        
        // Step 9: Verify cashier's bills can be filtered
        long cashierBillCount = todaysBills.stream()
            .filter(bill -> cashierUsername.equals(bill.getCashierUsername()))
            .count();
        assertTrue(cashierBillCount >= 1, "Should find cashier's bills");
        System.out.println("Step 9: Cashier has " + cashierBillCount + " bill(s)");
        
        System.out.println("\n✓ Complete sale workflow successful!");
        System.out.println("  - Bill #" + billNumber);
        System.out.println("  - Cashier: " + cashierUsername);
        System.out.println("  - Items: " + saleBill.getBillItems().size());
        System.out.println("  - Total: $" + String.format("%.2f", total));
    }

    // ==================== Integration Test Summary ====================
    
    /**
     * INTEGRATION TEST SUMMARY
     * 
     * This test class demonstrates integration between multiple components:
     * 
     * Components Integrated:
     * - Bill: Represents a sales transaction
     * - BillManager: Manages bill persistence and retrieval
     * - Inventory: Manages product catalog
     * - Item: Represents products
     * 
     * Integration Points Verified:
     * ✓ Bill creation and item addition
     * ✓ Total amount calculation across components
     * ✓ Bill persistence in BillManager
     * ✓ Date-based bill filtering
     * ✓ Stock level updates after sales
     * ✓ Multi-cashier bill tracking
     * ✓ Revenue calculation from multiple bills
     * ✓ Complete end-to-end sale workflow
     * 
     * Why These Tests Are Important:
     * - Verify components work together correctly
     * - Ensure data consistency across components
     * - Test realistic business workflows
     * - Identify integration issues early
     * - Document expected system behavior
     * 
     * Limitations Identified:
     * - Stock not automatically reduced during sales (manual update required)
     * - Bills can be created with items not in inventory (no validation)
     * - No transaction rollback on failures
     * 
     * Recommended Improvements:
     * 1. Automatic stock reduction when bills are finalized
     * 2. Validate items exist in inventory before adding to bill
     * 3. Add transaction support for atomicity
     * 4. Implement proper error handling and logging
     */
}
