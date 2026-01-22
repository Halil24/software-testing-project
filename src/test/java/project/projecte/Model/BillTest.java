package project.projecte.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Bill class
 *
 * Testing Coverage:
 * - Constructor validation
 * - addItem functionality
 * - getTotalAmount with various scenarios (empty, single, multiple items)
 * - Code Coverage Testing demonstration
 *
 * halili
 */
@DisplayName("Bill Class Tests")
class BillTest {

    private Bill testBill;
    private Item testItem1;
    private Item testItem2;

    @BeforeEach
    void setUp() {
        testBill = new Bill(1, "cashier");
        testItem1 = new Item("Apple", "Fruits", 0.50, 1.00, 100);
        testItem2 = new Item("Banana", "Fruits", 0.30, 0.80, 50);
    }

    // ==================== Constructor Tests ====================

    @Test
    @DisplayName("Constructor should create bill with valid parameters")
    void testConstructor_ValidParameters_ShouldCreateBill() {
        // Arrange & Act
        Bill bill = new Bill(123, "testCashier");

        // Assert
        assertNotNull(bill, "Bill should not be null");
        assertEquals(123, bill.getBillNumber(), "Bill number should match");
        assertEquals("testCashier", bill.getCashierUsername(), "Cashier username should match");
        assertNotNull(bill.getBillDate(), "Bill date should not be null");
        assertNotNull(bill.getBillItems(), "Bill items list should not be null");
        assertTrue(bill.getBillItems().isEmpty(), "New bill should have no items");
    }

    @Test
    @DisplayName("Constructor with null cashier should create bill")
    void testConstructor_NullCashier_ShouldCreateBill() {
        // Act
        Bill bill = new Bill(456, null);

        // Assert
        assertNull(bill.getCashierUsername(), "Cashier username should be null");
        // Note: This might be a design issue - should null cashiers be allowed?
    }

    @Test
    @DisplayName("Constructor should set current date and time")
    void testConstructor_ShouldSetCurrentDateTime() {
        // Arrange
        LocalDateTime before = LocalDateTime.now();

        // Act
        Bill bill = new Bill(789, "cashier");

        // Assert
        LocalDateTime after = LocalDateTime.now();
        LocalDateTime billDate = bill.getBillDate();

        assertNotNull(billDate, "Bill date should not be null");
        assertTrue(!billDate.isBefore(before), "Bill date should not be before creation");
        assertTrue(!billDate.isAfter(after), "Bill date should not be after creation");
    }

    // ==================== addItem() Tests ====================

    @Test
    @DisplayName("addItem should add item to bill")
    void testAddItem_ValidItem_ShouldAddToBill() {
        // Act
        testBill.addItem(testItem1, 5);

        // Assert
        assertEquals(1, testBill.getBillItems().size(), "Bill should have 1 item");
        BillItem billItem = testBill.getBillItems().get(0);
        assertEquals("Apple", billItem.getName(), "Item name should match");
        assertEquals(1.00, billItem.getSellingPrice(), 0.01, "Price should match");
        assertEquals(5, billItem.getQuantity(), "Quantity should match");
    }

    @Test
    @DisplayName("addItem should allow multiple items")
    void testAddItem_MultipleItems_ShouldAddAll() {
        // Act
        testBill.addItem(testItem1, 3);
        testBill.addItem(testItem2, 2);

        // Assert
        assertEquals(2, testBill.getBillItems().size(), "Bill should have 2 items");
        assertEquals("Apple", testBill.getBillItems().get(0).getName());
        assertEquals("Banana", testBill.getBillItems().get(1).getName());
    }

    @Test
    @DisplayName("addItem with zero quantity should add item")
    void testAddItem_ZeroQuantity_ShouldAddItem() {
        // Act
        testBill.addItem(testItem1, 0);

        // Assert
        assertEquals(1, testBill.getBillItems().size(), "Item with zero quantity should be added");
        assertEquals(0, testBill.getBillItems().get(0).getQuantity(), "Quantity should be 0");
    }

    @Test
    @DisplayName("addItem with negative quantity should add item")
    void testAddItem_NegativeQuantity_ShouldAddItem() {
        // Act
        testBill.addItem(testItem1, -5);

        // Assert
        assertEquals(1, testBill.getBillItems().size(), "Item with negative quantity is added");
        assertEquals(-5, testBill.getBillItems().get(0).getQuantity());
        // TODO: Should validate and reject negative quantities
    }

    @Test
    @DisplayName("addItem with null item should throw NullPointerException")
    void testAddItem_NullItem_ShouldThrowException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            testBill.addItem(null, 5);
        }, "Adding null item should throw NullPointerException");
    }

    // ==================== getTotalAmount() Tests ====================
    // Code Coverage Testing: Statement, Branch, Condition Coverage

    @Test
    @DisplayName("Coverage: getTotalAmount with empty bill should return 0")
    void testGetTotalAmount_EmptyBill_ShouldReturnZero() {
        // Act
        double total = testBill.getTotalAmount();

        // Assert
        assertEquals(0.0, total, 0.01, "Empty bill should have total of 0");
        
        // Coverage Analysis:
        // - Statement Coverage: Covers stream(), mapToDouble(), sum() with empty collection
        // - Branch Coverage: Covers empty stream case
    }

    @Test
    @DisplayName("Coverage: getTotalAmount with single item should calculate correctly")
    void testGetTotalAmount_SingleItem_ShouldCalculateCorrectly() {
        // Arrange
        testBill.addItem(testItem1, 5); // 5 * $1.00 = $5.00

        // Act
        double total = testBill.getTotalAmount();

        // Assert
        assertEquals(5.0, total, 0.01, "Total should be 5.00");
        
        // Coverage Analysis:
        // - Statement Coverage: All statements in getTotalAmount() executed
        // - Branch Coverage: Stream with one element
    }

    @Test
    @DisplayName("Coverage: getTotalAmount with multiple items should sum correctly")
    void testGetTotalAmount_MultipleItems_ShouldSumCorrectly() {
        // Arrange
        testBill.addItem(testItem1, 3); // 3 * $1.00 = $3.00
        testBill.addItem(testItem2, 5); // 5 * $0.80 = $4.00
        // Expected total: $7.00

        // Act
        double total = testBill.getTotalAmount();

        // Assert
        assertEquals(7.0, total, 0.01, "Total should be 7.00");
        
        // Coverage Analysis:
        // - Statement Coverage: 100% - all statements executed
        // - Branch Coverage: 100% - stream with multiple elements
        // - Condition Coverage: 100% - all conditions in lambda evaluated
    }

    @Test
    @DisplayName("Coverage: getTotalAmount with zero-price items should return 0")
    void testGetTotalAmount_ZeroPriceItems_ShouldReturnZero() {
        // Arrange
        Item freeItem = new Item("Free Sample", "Promo", 0.0, 0.0, 10);
        testBill.addItem(freeItem, 100);

        // Act
        double total = testBill.getTotalAmount();

        // Assert
        assertEquals(0.0, total, 0.01, "Total should be 0.00 for free items");
    }

    @Test
    @DisplayName("Coverage: getTotalAmount with large quantities should calculate correctly")
    void testGetTotalAmount_LargeQuantities_ShouldCalculate() {
        // Arrange
        testBill.addItem(testItem1, 1000); // 1000 * $1.00 = $1000.00

        // Act
        double total = testBill.getTotalAmount();

        // Assert
        assertEquals(1000.0, total, 0.01, "Total should handle large quantities");
    }

    @Test
    @DisplayName("Coverage: getTotalAmount with decimal prices should calculate precisely")
    void testGetTotalAmount_DecimalPrices_ShouldCalculatePrecisely() {
        // Arrange
        Item decimalItem = new Item("Test", "Test", 1.23, 4.56, 10);
        testBill.addItem(decimalItem, 7); // 7 * $4.56 = $31.92

        // Act
        double total = testBill.getTotalAmount();

        // Assert
        assertEquals(31.92, total, 0.01, "Total should calculate decimal prices correctly");
    }

    // ==================== Getter Tests ====================

    @Test
    @DisplayName("getBillNumber should return correct bill number")
    void testGetBillNumber() {
        assertEquals(1, testBill.getBillNumber(), "Bill number should be 1");
    }

    @Test
    @DisplayName("getCashierUsername should return correct cashier")
    void testGetCashierUsername() {
        assertEquals("cashier", testBill.getCashierUsername(), "Cashier should be 'cashier'");
    }

    @Test
    @DisplayName("getBillDate should return non-null date")
    void testGetBillDate() {
        assertNotNull(testBill.getBillDate(), "Bill date should not be null");
    }

    @Test
    @DisplayName("getBillItems should return non-null list")
    void testGetBillItems() {
        assertNotNull(testBill.getBillItems(), "Bill items should not be null");
    }

    @Test
    @DisplayName("getBillItems should return modifiable list")
    void testGetBillItems_ShouldReturnModifiableList() {
        // Act
        testBill.addItem(testItem1, 1);
        int sizeBefore = testBill.getBillItems().size();
        testBill.addItem(testItem2, 1);
        int sizeAfter = testBill.getBillItems().size();

        // Assert
        assertEquals(1, sizeBefore, "Should have 1 item before");
        assertEquals(2, sizeAfter, "Should have 2 items after");
    }

    // ==================== toString() Tests ====================

    @Test
    @DisplayName("toString with empty bill should return formatted string")
    void testToString_EmptyBill_ShouldReturnFormattedString() {
        // Act
        String result = testBill.toString();

        // Assert
        assertNotNull(result, "toString should not return null");
        assertTrue(result.contains("Bill Number: 1"), "Should contain bill number");
        assertTrue(result.contains("Date:"), "Should contain date label");
        assertTrue(result.contains("Items:"), "Should contain items label");
        assertTrue(result.contains("Total Amount: $0.0"), "Should contain total amount");
    }

    @Test
    @DisplayName("toString with items should include all item details")
    void testToString_WithItems_ShouldIncludeItemDetails() {
        // Arrange
        testBill.addItem(testItem1, 2);
        testBill.addItem(testItem2, 3);

        // Act
        String result = testBill.toString();

        // Assert
        assertNotNull(result, "toString should not return null");
        assertTrue(result.contains("Bill Number: 1"), "Should contain bill number");
        assertTrue(result.contains("Apple"), "Should contain first item");
        assertTrue(result.contains("Banana"), "Should contain second item");
        assertTrue(result.contains("Total Amount:"), "Should contain total amount");
    }

    // ==================== Edge Cases and Business Logic Tests ====================

    @Test
    @DisplayName("Bill should handle same item added multiple times")
    void testAddItem_SameItemMultipleTimes_ShouldAddSeparately() {
        // Act
        testBill.addItem(testItem1, 2);
        testBill.addItem(testItem1, 3);

        // Assert
        assertEquals(2, testBill.getBillItems().size(), "Same item added twice creates 2 entries");
        // Note: This might be intended behavior or could be consolidated
    }

    @Test
    @DisplayName("Bill with negative price item should calculate correctly")
    void testGetTotalAmount_NegativePriceItem_ShouldCalculate() {
        // Arrange - simulating a refund/discount scenario
        Item refundItem = new Item("Refund", "Returns", 0.0, -10.0, 1);
        testBill.addItem(testItem1, 1); // +$1.00
        testBill.addItem(refundItem, 1); // -$10.00

        // Act
        double total = testBill.getTotalAmount();

        // Assert
        assertEquals(-9.0, total, 0.01, "Negative prices should reduce total");
    }

    @Test
    @DisplayName("Bill number can be negative")
    void testConstructor_NegativeBillNumber() {
        // Act
        Bill bill = new Bill(-1, "cashier");

        // Assert
        assertEquals(-1, bill.getBillNumber(), "Negative bill numbers are allowed");
        // TODO: Consider validation for bill numbers
    }

    @Test
    @DisplayName("Bill number can be zero")
    void testConstructor_ZeroBillNumber() {
        // Act
        Bill bill = new Bill(0, "cashier");

        // Assert
        assertEquals(0, bill.getBillNumber(), "Zero bill number is allowed");
        // TODO: Consider if bill numbers should start from 1
    }
}
