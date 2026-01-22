package project.projecte.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Item class
 * 
 * Testing Coverage:
 * - Constructor validation
 * - Boundary Value Testing (BVT) for setStockLevel
 * - Equivalence Class Testing for setSellingPrice
 * - All getters and setters
 * 
 * dea
 */
@DisplayName("Item Class Tests")
class ItemTest {

    private Item testItem;

    @BeforeEach
    void setUp() {
        // Create a valid item before each test
        testItem = new Item("Apple", "Fruits", 0.50, 1.00, 100);
    }

    // ==================== Constructor Tests ====================

    @Test
    @DisplayName("Constructor should create valid item with all parameters")
    void testConstructor_ValidParameters_ShouldCreateItem() {
        // Arrange & Act
        Item item = new Item("Banana", "Fruits", 0.30, 0.80, 50);

        // Assert
        assertNotNull(item, "Item should not be null");
        assertEquals("Banana", item.getName(), "Name should match");
        assertEquals("Fruits", item.getCategory(), "Category should match");
        assertEquals(0.30, item.getPurchasePrice(), 0.01, "Purchase price should match");
        assertEquals(0.80, item.getSellingPrice(), 0.01, "Selling price should match");
        assertEquals(50, item.getStockLevel(), "Stock level should match");
    }

    @Test
    @DisplayName("Constructor with negative stock should create item")
    void testConstructor_NegativeStock_ShouldCreateItem() {
        // Act
        Item item = new Item("Orange", "Fruits", 0.40, 0.90, -10);

        // Assert - Currently accepts negative stock (potential bug or design choice)
        assertEquals(-10, item.getStockLevel(), "Stock level should be -10");
    }

    // ==================== BVT: setStockLevel() ====================
    // Boundary Value Testing for stock level (int range: 0 to Integer.MAX_VALUE)

    @Test
    @DisplayName("BVT: setStockLevel with -1 (below minimum)")
    void testSetStockLevel_BVT_BelowMinimum() {
        // Act
        testItem.setStockLevel(-1);

        // Assert - Currently no validation (this is a test discovery)
        assertEquals(-1, testItem.getStockLevel(), "Negative stock is currently allowed");
        // TODO: Consider adding validation to reject negative stock
    }

    @Test
    @DisplayName("BVT: setStockLevel with 0 (minimum boundary)")
    void testSetStockLevel_BVT_Minimum() {
        // Act
        testItem.setStockLevel(0);

        // Assert
        assertEquals(0, testItem.getStockLevel(), "Stock level should be 0");
    }

    @Test
    @DisplayName("BVT: setStockLevel with 1 (just above minimum)")
    void testSetStockLevel_BVT_JustAboveMinimum() {
        // Act
        testItem.setStockLevel(1);

        // Assert
        assertEquals(1, testItem.getStockLevel(), "Stock level should be 1");
    }

    @Test
    @DisplayName("BVT: setStockLevel with normal value (100)")
    void testSetStockLevel_BVT_NormalValue() {
        // Act
        testItem.setStockLevel(100);

        // Assert
        assertEquals(100, testItem.getStockLevel(), "Stock level should be 100");
    }

    @Test
    @DisplayName("BVT: setStockLevel with large value (10000)")
    void testSetStockLevel_BVT_LargeValue() {
        // Act
        testItem.setStockLevel(10000);

        // Assert
        assertEquals(10000, testItem.getStockLevel(), "Stock level should be 10000");
    }

    @Test
    @DisplayName("BVT: setStockLevel with Integer.MAX_VALUE (maximum boundary)")
    void testSetStockLevel_BVT_Maximum() {
        // Act
        testItem.setStockLevel(Integer.MAX_VALUE);

        // Assert
        assertEquals(Integer.MAX_VALUE, testItem.getStockLevel(), "Stock level should be Integer.MAX_VALUE");
    }

    // ==================== ECT: setSellingPrice() ====================
    // Equivalence Class Testing for selling price

    @Test
    @DisplayName("ECT: setSellingPrice with valid positive value")
    void testSetSellingPrice_ECT_ValidPositive() {
        // Equivalence Class: Valid positive prices (0.01 to Double.MAX_VALUE)
        testItem.setSellingPrice(5.99);

        assertEquals(5.99, testItem.getSellingPrice(), 0.01, "Selling price should be 5.99");
    }

    @Test
    @DisplayName("ECT: setSellingPrice with zero")
    void testSetSellingPrice_ECT_Zero() {
        // Equivalence Class: Zero price (edge case)
        testItem.setSellingPrice(0.0);

        assertEquals(0.0, testItem.getSellingPrice(), 0.01, "Selling price should be 0.0");
    }

    @Test
    @DisplayName("ECT: setSellingPrice with negative value")
    void testSetSellingPrice_ECT_Negative() {
        // Equivalence Class: Invalid negative prices
        testItem.setSellingPrice(-1.50);

        // Assert - Currently no validation (design issue)
        assertEquals(-1.50, testItem.getSellingPrice(), 0.01, "Negative price is currently allowed");
        // TODO: Should add validation to reject negative prices
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.01, 1.00, 10.50, 99.99, 1000.00})
    @DisplayName("ECT: setSellingPrice with various valid prices")
    void testSetSellingPrice_ECT_VariousValidPrices(double price) {
        // Test multiple values from valid equivalence class
        testItem.setSellingPrice(price);

        assertEquals(price, testItem.getSellingPrice(), 0.01, "Price should match input");
    }

    // ==================== Getter Tests ====================

    @Test
    @DisplayName("getName should return correct name")
    void testGetName() {
        assertEquals("Apple", testItem.getName(), "Name should be 'Apple'");
    }

    @Test
    @DisplayName("getCategory should return correct category")
    void testGetCategory() {
        assertEquals("Fruits", testItem.getCategory(), "Category should be 'Fruits'");
    }

    @Test
    @DisplayName("getPurchasePrice should return correct purchase price")
    void testGetPurchasePrice() {
        assertEquals(0.50, testItem.getPurchasePrice(), 0.01, "Purchase price should be 0.50");
    }

    @Test
    @DisplayName("getSellingPrice should return correct selling price")
    void testGetSellingPrice() {
        assertEquals(1.00, testItem.getSellingPrice(), 0.01, "Selling price should be 1.00");
    }

    @Test
    @DisplayName("getStockLevel should return correct stock level")
    void testGetStockLevel() {
        assertEquals(100, testItem.getStockLevel(), "Stock level should be 100");
    }

    // ==================== Setter Tests ====================

    @Test
    @DisplayName("setCategory should update category")
    void testSetCategory_Valid() {
        testItem.setCategory("Vegetables");

        assertEquals("Vegetables", testItem.getCategory(), "Category should be updated to 'Vegetables'");
    }

    @Test
    @DisplayName("setCategory with null should accept null")
    void testSetCategory_Null() {
        testItem.setCategory(null);

        assertNull(testItem.getCategory(), "Category should be null");
        // TODO: Consider validation - should null category be allowed?
    }

    @Test
    @DisplayName("setPurchasePrice should update purchase price")
    void testSetPurchasePrice_Valid() {
        testItem.setPurchasePrice(0.75);

        assertEquals(0.75, testItem.getPurchasePrice(), 0.01, "Purchase price should be 0.75");
    }

    @Test
    @DisplayName("setPurchasePrice with negative value")
    void testSetPurchasePrice_Negative() {
        testItem.setPurchasePrice(-0.50);

        assertEquals(-0.50, testItem.getPurchasePrice(), 0.01, "Negative purchase price is allowed");
        // TODO: Should validate against negative prices
    }

    // ==================== toString() Tests ====================

    @Test
    @DisplayName("toString should return formatted string")
    void testToString() {
        String result = testItem.toString();

        assertNotNull(result, "toString should not return null");
        assertTrue(result.contains("Apple"), "Should contain item name");
        assertTrue(result.contains("Fruits"), "Should contain category");
        assertTrue(result.contains("0.5"), "Should contain purchase price");
        assertTrue(result.contains("1.0"), "Should contain selling price");
        assertTrue(result.contains("100"), "Should contain stock level");
    }

    // ==================== Parameterized Tests ====================

    @ParameterizedTest
    @CsvSource({
        "Apple, Fruits, 0.50, 1.00, 100",
        "Banana, Fruits, 0.30, 0.80, 50",
        "Carrot, Vegetables, 0.20, 0.60, 200",
        "Milk, Dairy, 1.50, 2.00, 30"
    })
    @DisplayName("Constructor should create items with various valid inputs")
    void testConstructor_ParameterizedValidInputs(String name, String category, 
                                                   double purchasePrice, double sellingPrice, 
                                                   int stockLevel) {
        // Act
        Item item = new Item(name, category, purchasePrice, sellingPrice, stockLevel);

        // Assert
        assertAll("Item properties",
            () -> assertEquals(name, item.getName()),
            () -> assertEquals(category, item.getCategory()),
            () -> assertEquals(purchasePrice, item.getPurchasePrice(), 0.01),
            () -> assertEquals(sellingPrice, item.getSellingPrice(), 0.01),
            () -> assertEquals(stockLevel, item.getStockLevel())
        );
    }

    // ==================== Edge Case Tests ====================

    @Test
    @DisplayName("Item with empty name should be created")
    void testConstructor_EmptyName() {
        Item item = new Item("", "Category", 1.0, 2.0, 10);

        assertEquals("", item.getName(), "Empty name should be allowed");
        // TODO: Consider validation for empty names
    }

    @Test
    @DisplayName("Item with very long name should be created")
    void testConstructor_VeryLongName() {
        String longName = "A".repeat(1000);
        Item item = new Item(longName, "Category", 1.0, 2.0, 10);

        assertEquals(longName, item.getName(), "Long name should be allowed");
    }

    @Test
    @DisplayName("Item with purchase price greater than selling price")
    void testConstructor_PurchasePriceGreaterThanSellingPrice() {
        // This is a business logic issue - buying for more than selling
        Item item = new Item("Loss Item", "Test", 10.00, 5.00, 10);

        assertTrue(item.getPurchasePrice() > item.getSellingPrice(), 
                   "Currently allows purchase price > selling price");
        // TODO: Should add business rule validation
    }
}
