package project.projecte.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Inventory class
 *
 * Testing Coverage:
 * - addItem functionality
 * - findItemByName with Equivalence Class Testing
 * - updateStockLevel with Decision Table approach
 * - removeItem functionality
 * - File I/O operations (save/load)
 *
 * daniela
 */
@DisplayName("Inventory Class Tests")
class InventoryTest {

    private Inventory inventory;
    private Item testItem1;
    private Item testItem2;
    private Item testItem3;

    @TempDir
    Path tempDir; // JUnit 5 creates temporary directory for testing

    @BeforeEach
    void setUp() {
        // Note: Inventory constructor auto-loads from file
        // For isolated testing, we might need to modify the class
        inventory = new Inventory();
        inventory.getItems().clear(); // Fix: Clear auto-loaded items for test isolation

        testItem1 = new Item("Apple", "Fruits", 0.50, 1.00, 100);
        testItem2 = new Item("Banana", "Fruits", 0.30, 0.80, 50);
        testItem3 = new Item("Carrot", "Vegetables", 0.20, 0.60, 200);
    }

    // ==================== addItem() Tests ====================

    @Test
    @DisplayName("addItem should add item to inventory")
    void testAddItem_ValidItem_ShouldAdd() {
        // Arrange
        int initialSize = inventory.getItems().size();

        // Act
        inventory.addItem(testItem1);

        // Assert
        assertEquals(initialSize + 1, inventory.getItems().size(), "Inventory size should increase by 1");
        assertTrue(inventory.getItems().contains(testItem1), "Inventory should contain the added item");
    }

    @Test
    @DisplayName("addItem should allow multiple items")
    void testAddItem_MultipleItems_ShouldAddAll() {
        // Arrange
        int initialSize = inventory.getItems().size();

        // Act
        inventory.addItem(testItem1);
        inventory.addItem(testItem2);
        inventory.addItem(testItem3);

        // Assert
        assertEquals(initialSize + 3, inventory.getItems().size(), "Should add all 3 items");
    }

    @Test
    @DisplayName("addItem should allow duplicate items")
    void testAddItem_DuplicateItem_ShouldAdd() {
        // Act
        inventory.addItem(testItem1);
        inventory.addItem(testItem1); // Same reference

        // Assert
        assertTrue(inventory.getItems().size() >= 2, "Duplicate items should be added");

    }

    @Test
    @DisplayName("addItem with null should not throw exception immediately")
    void testAddItem_Null_ShouldAddButMayCauseIssuesLater() {
        // This test documents current behavior
        // Act - adding null might work initially but cause problems later
        assertDoesNotThrow(() -> inventory.addItem(null));

        // TODO: Should add validation to prevent null items
    }

    // ==================== ECT: findItemByName() ====================
    // Equivalence Class Testing for findItemByName

    @Test
    @DisplayName("ECT: findItemByName - Item exists (valid class)")
    void testFindItemByName_ECT_ItemExists() {
        // Equivalence Class: Valid item names that exist
        // Arrange
        inventory.addItem(testItem1);

        // Act
        Item found = inventory.findItemByName("Apple");

        // Assert
        assertNotNull(found, "Should find existing item");
        assertEquals("Apple", found.getName(), "Found item should have correct name");
    }

    @Test
    @DisplayName("ECT: findItemByName - Item exists with different case")
    void testFindItemByName_ECT_ItemExistsDifferentCase() {
        // Equivalence Class: Case-insensitive search
        // Arrange
        inventory.addItem(testItem1);

        // Act
        Item found1 = inventory.findItemByName("apple");
        Item found2 = inventory.findItemByName("APPLE");
        Item found3 = inventory.findItemByName("ApPlE");

        // Assert
        assertAll("Case insensitive search",
                () -> assertNotNull(found1, "Should find with lowercase"),
                () -> assertNotNull(found2, "Should find with uppercase"),
                () -> assertNotNull(found3, "Should find with mixed case"));
    }

    @Test
    @DisplayName("ECT: findItemByName - Item does not exist (invalid class)")
    void testFindItemByName_ECT_ItemDoesNotExist() {
        // Equivalence Class: Invalid item names that don't exist
        // Arrange
        inventory.addItem(testItem1);

        // Act
        Item found = inventory.findItemByName("NonExistentItem");

        // Assert
        assertNull(found, "Should return null for non-existent item");
    }

    @Test
    @DisplayName("ECT: findItemByName - Null name (invalid class)")
    void testFindItemByName_ECT_NullName() {
        // Equivalence Class: Null input
        // Arrange
        inventory.addItem(testItem1);

        // Act
        Item found = inventory.findItemByName(null);

        // Assert
        // Note: Current implementation returns null instead of throwing exception
        // This documents actual behavior (no validation exists)
        assertNull(found, "Should return null for null name (no validation implemented)");
    }

    @Test
    @DisplayName("ECT: findItemByName - Empty string (invalid class)")
    void testFindItemByName_ECT_EmptyString() {
        // Equivalence Class: Empty string input
        // Arrange
        inventory.addItem(testItem1);

        // Act
        Item found = inventory.findItemByName("");

        // Assert
        assertNull(found, "Should return null for empty string");
    }

    @Test
    @DisplayName("ECT: findItemByName - Empty inventory (boundary case)")
    void testFindItemByName_ECT_EmptyInventory() {
        // Equivalence Class: Search in empty inventory
        // Act
        Item found = inventory.findItemByName("Apple");

        // Assert
        assertNull(found, "Should return null when inventory is empty");
    }



    @Test
    @DisplayName("DT-T1: updateStockLevel - Item exists, valid stock -> Update")
    void testUpdateStockLevel_DT_T1_ExistsValidStock() {
        // Arrange
        inventory.addItem(testItem1);

        // Act
        boolean result = inventory.updateStockLevel("Apple", 50);

        // Assert
        assertTrue(result, "Should return true for successful update");
        assertEquals(50, testItem1.getStockLevel(), "Stock level should be updated");
    }

    @Test
    @DisplayName("DT-T2: updateStockLevel - Item exists, negative stock -> Update (no validation)")
    void testUpdateStockLevel_DT_T2_ExistsInvalidStock() {
        // Arrange
        inventory.addItem(testItem1);

        // Act
        boolean result = inventory.updateStockLevel("Apple", -10);

        // Assert
        assertTrue(result, "Currently returns true even for negative stock");
        assertEquals(-10, testItem1.getStockLevel(), "Negative stock is currently allowed");
        // TODO: Should add validation to reject negative stock
    }

    @Test
    @DisplayName("DT-T3: updateStockLevel - Item does not exist, valid stock -> Reject")
    void testUpdateStockLevel_DT_T3_NotExistsValidStock() {
        // Act
        boolean result = inventory.updateStockLevel("NonExistent", 50);

        // Assert
        assertFalse(result, "Should return false when item doesn't exist");
    }

    @Test
    @DisplayName("DT-T4: updateStockLevel - Item does not exist, invalid stock -> Reject")
    void testUpdateStockLevel_DT_T4_NotExistsInvalidStock() {
        // Act
        boolean result = inventory.updateStockLevel("NonExistent", -10);

        // Assert
        assertFalse(result, "Should return false when item doesn't exist");
    }

    @Test
    @DisplayName("DT-T5: updateStockLevel - Null name -> Exception or false")
    void testUpdateStockLevel_DT_T5_NullName() {
        // Arrange
        inventory.addItem(testItem1);

        // Act
        boolean result = inventory.updateStockLevel(null, 50);


        assertFalse(result, "Should return false for null name (no validation implemented)");
    }

    @Test
    @DisplayName("updateStockLevel with zero stock should update")
    void testUpdateStockLevel_ZeroStock() {
        // Arrange
        inventory.addItem(testItem1);

        // Act
        boolean result = inventory.updateStockLevel("Apple", 0);

        // Assert
        assertTrue(result, "Should accept zero stock");
        assertEquals(0, testItem1.getStockLevel(), "Stock should be 0");
    }

    // ==================== removeItem() Tests ====================

    @Test
    @DisplayName("removeItem should remove existing item")
    void testRemoveItem_ExistingItem_ShouldRemove() {
        // Arrange
        inventory.addItem(testItem1);
        inventory.addItem(testItem2);
        int initialSize = inventory.getItems().size();

        // Act
        boolean result = inventory.removeItem("Apple");

        // Assert
        assertTrue(result, "Should return true when item is removed");
        assertEquals(initialSize - 1, inventory.getItems().size(), "Size should decrease by 1");
        assertNull(inventory.findItemByName("Apple"), "Item should no longer be findable");
    }

    @Test
    @DisplayName("removeItem should return false for non-existent item")
    void testRemoveItem_NonExistentItem_ShouldReturnFalse() {
        // Arrange
        inventory.addItem(testItem1);

        // Act
        boolean result = inventory.removeItem("NonExistent");

        // Assert
        assertFalse(result, "Should return false when item doesn't exist");
    }

    @Test
    @DisplayName("removeItem should be case-insensitive")
    void testRemoveItem_CaseInsensitive_ShouldRemove() {
        // Arrange
        inventory.addItem(testItem1);

        // Act
        boolean result = inventory.removeItem("apple"); // lowercase

        // Assert
        assertTrue(result, "Should remove item with different case");
        assertNull(inventory.findItemByName("Apple"), "Item should be removed");
    }

    @Test
    @DisplayName("removeItem with null should not crash")
    void testRemoveItem_NullName_ShouldHandleGracefully() {
        // Arrange
        inventory.addItem(testItem1);

        // Act
        boolean result = inventory.removeItem(null);

        assertFalse(result, "Should return false for null name (no validation implemented)");
    }

    @Test
    @DisplayName("removeItem from empty inventory should return false")
    void testRemoveItem_EmptyInventory_ShouldReturnFalse() {
        // Act
        boolean result = inventory.removeItem("Apple");

        // Assert
        assertFalse(result, "Should return false for empty inventory");
    }


    @Test
    @DisplayName("getItems should return non-null list")
    void testGetItems_ShouldReturnNonNullList() {
        // Act
        var items = inventory.getItems();

        // Assert
        assertNotNull(items, "Items list should not be null");
    }

    @Test
    @DisplayName("getItems should return empty list for new inventory")
    void testGetItems_NewInventory_ShouldReturnList() {
        // Act
        var items = inventory.getItems();

        // Assert
        assertNotNull(items, "Should return a list (might be empty or contain loaded items)");
    }

    // ==================== displayItems() Tests ====================

    @Test
    @DisplayName("displayItems should not throw exception with empty inventory")
    void testDisplayItems_EmptyInventory_ShouldNotThrow() {
        // Act & Assert
        assertDoesNotThrow(() -> inventory.displayItems(),
                "displayItems should not throw exception");
    }

    @Test
    @DisplayName("displayItems should not throw exception with items")
    void testDisplayItems_WithItems_ShouldNotThrow() {
        // Arrange
        inventory.addItem(testItem1);
        inventory.addItem(testItem2);

        // Act & Assert
        assertDoesNotThrow(() -> inventory.displayItems(),
                "displayItems should not throw exception");
    }

    // ==================== Edge Cases ====================

    @Test
    @DisplayName("Adding many items should work")
    void testAddItem_ManyItems_ShouldWork() {
        // Arrange & Act
        for (int i = 0; i < 1000; i++) {
            Item item = new Item("Item" + i, "Test", 1.0, 2.0, i);
            inventory.addItem(item);
        }

        // Assert
        assertTrue(inventory.getItems().size() >= 1000, "Should handle many items");
    }

    @Test
    @DisplayName("Find item after adding multiple items with similar names")
    void testFindItemByName_SimilarNames_ShouldFindCorrect() {
        // Arrange
        Item apple1 = new Item("Apple", "Fruits", 0.5, 1.0, 100);
        Item apple2 = new Item("Apple Juice", "Beverages", 1.0, 2.0, 50);
        inventory.addItem(apple1);
        inventory.addItem(apple2);

        // Act
        Item found = inventory.findItemByName("Apple");

        // Assert
        assertNotNull(found, "Should find item");
        assertEquals("Apple", found.getName(), "Should find exact match, not partial");
    }

    @Test
    @DisplayName("Update stock after removing item should return false")
    void testUpdateStockLevel_AfterRemove_ShouldReturnFalse() {
        // Arrange
        inventory.addItem(testItem1);
        inventory.removeItem("Apple");

        // Act
        boolean result = inventory.updateStockLevel("Apple", 50);

        // Assert
        assertFalse(result, "Should return false for removed item");
    }

    @Test
    @DisplayName("BVT-04: Shortest Name 'A'")
    void testBVT_04_ShortestName_A() {
        // BVT-04: Shortest Name "A" -> Return Item "A"
        Item itemA = new Item("A", "Test", 1.0, 1.0, 10);
        inventory.addItem(itemA);

        Item found = inventory.findItemByName("A");
        assertNotNull(found, "Should find item with single character name 'A'");
        assertEquals("A", found.getName());
    }

    @Test
    @DisplayName("ECT-04: Partial String Match (Invalid) -> Return Null")
    void testECT_04_PartialMatch_ShouldReturnNull() {
        // ECT-04: Partial String "App" (Item is "Apple") -> Return null (Exact match
        // required)
        inventory.addItem(testItem1); // Apple

        Item found = inventory.findItemByName("App");
        assertNull(found, "Should not find item with partial match 'App'");
    }

    @Test
    @DisplayName("BVT-03: Max Integer Stock")
    void testBVT_03_MaxIntegerStock() {
        // BVT-03: Max Integer Stock (2147483647) -> Stock = MAX, Return true
        inventory.addItem(testItem1);

        boolean result = inventory.updateStockLevel("Apple", Integer.MAX_VALUE);

        assertTrue(result, "Should successfully update to MAX_VALUE");
        assertEquals(Integer.MAX_VALUE, testItem1.getStockLevel(), "Stock level should be Integer.MAX_VALUE");
    }

    @Test
    @DisplayName("BVT-05: Empty String - Contains items -> Return null")
    void testBVT_05_EmptyString_ContainsItems() {
        // BVT-05: Empty String "" - Contains items -> Return null
        // (Re-verifying existing behavior mentioned in ECT-EmptyString but explicitly
        // as BVT)
        inventory.addItem(testItem1);
        Item found = inventory.findItemByName("");
        assertNull(found, "Should return null for empty string search even if inventory has items");
    }

    @Test
    @DisplayName("Adding item with same name as existing item creates separate entry")
    void testAddItem_SameName_CreatesSeparateEntry() {
        // Arrange
        Item apple1 = new Item("Apple", "Fruits", 0.5, 1.0, 100);
        Item apple2 = new Item("Apple", "Organic", 0.7, 1.5, 50);

        // Act
        inventory.addItem(apple1);
        inventory.addItem(apple2);

        // Assert
        assertTrue(inventory.getItems().size() >= 2, "Should allow items with same name");
        // Note: findItemByName will return the first match
    }
}
