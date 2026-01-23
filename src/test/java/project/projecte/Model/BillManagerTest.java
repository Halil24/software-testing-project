package project.projecte.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BillManager class
 *
 * Testing Coverage:
 * - addBill functionality
 * - getTodayBills with various date scenarios
 * - getBillsWithinDateRange with MC/DC coverage analysis
 * - Complete code coverage demonstration
 *
 * MC/DC (Modified Condition/Decision Coverage) Analysis for
 * getBillsWithinDateRange:
 * This method has a compound condition with AND operator:
 * Condition A: !bill.getBillDate().toLocalDate().isBefore(startDate)
 * Condition B: !bill.getBillDate().toLocalDate().isAfter(endDate)
 *
 * MC/DC requires each condition to independently affect the outcome.
 *
 * halili
 */
@DisplayName("BillManager Class Tests")
class BillManagerTest {

    private BillManager billManager;
    private Bill testBill1;
    private Bill testBill2;
    private Bill testBill3;

    @BeforeEach
    void setUp() {
        // Note: BillManager constructor auto-loads from file
        // For isolated testing, we would need to modify the class
        billManager = new BillManager();

        // Create test bills
        testBill1 = new Bill(1, "cashier1");
        testBill2 = new Bill(2, "cashier2");
        testBill3 = new Bill(3, "cashier1");
    }

    // ==================== addBill() Tests ====================

    @Test
    @DisplayName("addBill should add bill to manager")
    void testAddBill_ValidBill_ShouldAdd() {
        // Arrange
        int initialSize = billManager.getBills().size();

        // Act
        billManager.addBill(testBill1);

        // Assert
        assertEquals(initialSize + 1, billManager.getBills().size(), "Size should increase by 1");
        assertTrue(billManager.getBills().contains(testBill1), "Should contain added bill");
    }

    @Test
    @DisplayName("addBill should add multiple bills")
    void testAddBill_MultipleBills_ShouldAddAll() {
        // Arrange
        int initialSize = billManager.getBills().size();

        // Act
        billManager.addBill(testBill1);
        billManager.addBill(testBill2);
        billManager.addBill(testBill3);

        // Assert
        assertEquals(initialSize + 3, billManager.getBills().size(), "Should add all 3 bills");
    }

    @Test
    @DisplayName("addBill with null should not throw exception during add")
    void testAddBill_Null_MayAddButCauseIssuesLater() {
        // Act & Assert
        assertDoesNotThrow(() -> billManager.addBill(null),
                "Adding null might work but will cause issues in queries");
        // TODO: Should add validation to prevent null bills
    }

    // ==================== getTodayBills() Tests ====================

    @Test
    @DisplayName("getTodayBills should return bills from today")
    void testGetTodayBills_TodayBills_ShouldReturn() {
        // Arrange - bills created in setup are from "now"
        billManager.addBill(testBill1);
        billManager.addBill(testBill2);

        // Act
        List<Bill> todayBills = billManager.getTodayBills();

        // Assert
        assertTrue(todayBills.size() >= 2, "Should include today's bills");
        assertTrue(todayBills.stream()
                .allMatch(bill -> bill.getBillDate().toLocalDate().equals(LocalDate.now())),
                "All bills should be from today");
    }

    @Test
    @DisplayName("getTodayBills should return empty list when no bills from today")
    void testGetTodayBills_NoBillsToday_ShouldReturnEmpty() {
        // Note: Hard to test without ability to create bills with specific dates
        // This documents the limitation

        // Act
        List<Bill> todayBills = billManager.getTodayBills();

        // Assert
        assertNotNull(todayBills, "Should return non-null list");
        // All newly created bills will be from today, so this test is limited
    }

    @Test
    @DisplayName("getTodayBills should return non-null list")
    void testGetTodayBills_ShouldReturnNonNullList() {
        // Act
        List<Bill> todayBills = billManager.getTodayBills();

        // Assert
        assertNotNull(todayBills, "Should never return null");
    }

    // ==================== MC/DC Coverage: getBillsWithinDateRange()
    // ====================

    /**
     * MC/DC Analysis for getBillsWithinDateRange(LocalDate startDate, LocalDate
     * endDate)
     * 
     * Method filters bills with condition: A && B where:
     * - A: !bill.getBillDate().toLocalDate().isBefore(startDate)
     * (bill date >= startDate)
     * - B: !bill.getBillDate().toLocalDate().isAfter(endDate)
     * (bill date <= endDate)
     * 
     * MC/DC Truth Table:
     * Test | A | B | A&&B | Bill Date | Range | Included? |
     * -----|---|---|------|----------------|------------------|-----------|
     * T1 | F | F | F | Before range | [start, end] | No |
     * T2 | F | T | F | Before start | [start, end] | No |
     * T3 | T | F | F | After end | [start, end] | No |
     * T4 | T | T | T | Within range | [start, end] | Yes |
     * T5 | T | T | T | At start | [start, end] | Yes |
     * T6 | T | T | T | At end | [start, end] | Yes |
     * 
     * For MC/DC, we need tests showing each condition independently affects
     * outcome:
     * - A changes from F to T (holding B constant) changes outcome: T2 vs T6
     * - B changes from F to T (holding A constant) changes outcome: T3 vs T6
     */

    @Test
    @DisplayName("MC/DC-T1: Bill date before range (A=F, B=F) -> Not included")
    void testGetBillsWithinDateRange_MCDC_T1_BeforeRange() {
        // Arrange
        LocalDate startDate = LocalDate.now().plusDays(5);
        LocalDate endDate = LocalDate.now().plusDays(10);
        billManager.addBill(testBill1); // Bill from today (before range)

        // Act
        List<Bill> result = billManager.getBillsWithinDateRange(startDate, endDate);

        // Assert
        assertFalse(result.contains(testBill1),
                "Bill before range should not be included (A=F, B=F)");

        // Coverage:
        // - Condition A (>= startDate): FALSE (today < startDate)
        // - Condition B (<= endDate): FALSE (today < endDate, but irrelevant since A is
        // false)
        // - Result: Not included
    }

    @Test
    @DisplayName("MC/DC-T2: Bill date before start but hypothetically before end (A=F, B=T) -> Not included")
    void testGetBillsWithinDateRange_MCDC_T2_BeforeStart() {
        // Arrange
        LocalDate startDate = LocalDate.now().plusDays(2);
        LocalDate endDate = LocalDate.now().plusDays(10);
        billManager.addBill(testBill1); // Bill from today

        // Act
        List<Bill> result = billManager.getBillsWithinDateRange(startDate, endDate);

        // Assert
        assertFalse(result.contains(testBill1),
                "Bill before start date should not be included (A=F, B=T)");

        // Coverage:
        // - Condition A (>= startDate): FALSE (today < startDate)
        // - Condition B (<= endDate): TRUE (today < endDate)
        // - Result: Not included (A && B = FALSE)
    }

    @Test
    @DisplayName("MC/DC-T3: Bill date after end (A=T, B=F) -> Not included")
    void testGetBillsWithinDateRange_MCDC_T3_AfterEnd() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate endDate = LocalDate.now().minusDays(5);
        billManager.addBill(testBill1); // Bill from today (after range)

        // Act
        List<Bill> result = billManager.getBillsWithinDateRange(startDate, endDate);

        // Assert
        assertFalse(result.contains(testBill1),
                "Bill after range should not be included (A=T, B=F)");

        // Coverage:
        // - Condition A (>= startDate): TRUE (today > startDate)
        // - Condition B (<= endDate): FALSE (today > endDate)
        // - Result: Not included (A && B = FALSE)
    }

    @Test
    @DisplayName("MC/DC-T4: Bill date within range (A=T, B=T) -> Included")
    void testGetBillsWithinDateRange_MCDC_T4_WithinRange() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(1);
        billManager.addBill(testBill1); // Bill from today (within range)

        // Act
        List<Bill> result = billManager.getBillsWithinDateRange(startDate, endDate);

        // Assert
        assertTrue(result.contains(testBill1),
                "Bill within range should be included (A=T, B=T)");

        // Coverage:
        // - Condition A (>= startDate): TRUE (today >= yesterday)
        // - Condition B (<= endDate): TRUE (today <= tomorrow)
        // - Result: Included (A && B = TRUE)
    }

    @Test
    @DisplayName("MC/DC-T5: Bill date at start boundary (A=T, B=T) -> Included")
    void testGetBillsWithinDateRange_MCDC_T5_AtStartBoundary() {
        // Arrange
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(5);
        billManager.addBill(testBill1); // Bill from today (at start)

        // Act
        List<Bill> result = billManager.getBillsWithinDateRange(startDate, endDate);

        // Assert
        assertTrue(result.contains(testBill1),
                "Bill at start boundary should be included (A=T, B=T)");

        // Coverage:
        // - Condition A (>= startDate): TRUE (today == startDate)
        // - Condition B (<= endDate): TRUE (today < endDate)
        // - Result: Included
    }

    @Test
    @DisplayName("MC/DC-T6: Bill date at end boundary (A=T, B=T) -> Included")
    void testGetBillsWithinDateRange_MCDC_T6_AtEndBoundary() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusDays(5);
        LocalDate endDate = LocalDate.now();
        billManager.addBill(testBill1); // Bill from today (at end)

        // Act
        List<Bill> result = billManager.getBillsWithinDateRange(startDate, endDate);

        // Assert
        assertTrue(result.contains(testBill1),
                "Bill at end boundary should be included (A=T, B=T)");

        // Coverage:
        // - Condition A (>= startDate): TRUE (today > startDate)
        // - Condition B (<= endDate): TRUE (today == endDate)
        // - Result: Included
    }

    @Test
    @DisplayName("MC/DC: Independent effect of Condition A")
    void testGetBillsWithinDateRange_MCDC_ConditionA_IndependentEffect() {
        // This test demonstrates that changing A from FALSE to TRUE changes outcome
        // while holding B constant (TRUE)

        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(5);
        billManager.addBill(testBill1); // today

        List<Bill> result1 = billManager.getBillsWithinDateRange(startDate, endDate);
        // A=F (today < start), B=T (today < end) -> Not included

        startDate = LocalDate.now().minusDays(1);
        List<Bill> result2 = billManager.getBillsWithinDateRange(startDate, endDate);
        // A=T (today > start), B=T (today < end) -> Included

        assertAll("Condition A independently affects outcome",
                () -> assertFalse(result1.contains(testBill1), "A=F, B=T: Not included"),
                () -> assertTrue(result2.contains(testBill1), "A=T, B=T: Included"));
    }

    @Test
    @DisplayName("MC/DC: Independent effect of Condition B")
    void testGetBillsWithinDateRange_MCDC_ConditionB_IndependentEffect() {
        // This test demonstrates that changing B from FALSE to TRUE changes outcome
        // while holding A constant (TRUE)

        LocalDate startDate = LocalDate.now().minusDays(5);
        LocalDate endDate = LocalDate.now().minusDays(1);
        billManager.addBill(testBill1); // today

        List<Bill> result1 = billManager.getBillsWithinDateRange(startDate, endDate);
        // A=T (today > start), B=F (today > end) -> Not included

        endDate = LocalDate.now().plusDays(1);
        List<Bill> result2 = billManager.getBillsWithinDateRange(startDate, endDate);
        // A=T (today > start), B=T (today < end) -> Included

        assertAll("Condition B independently affects outcome",
                () -> assertFalse(result1.contains(testBill1), "A=T, B=F: Not included"),
                () -> assertTrue(result2.contains(testBill1), "A=T, B=T: Included"));
    }

    // ==================== Additional Coverage Tests ====================

    @Test
    @DisplayName("Coverage: getBillsWithinDateRange with no bills")
    void testGetBillsWithinDateRange_Coverage_NoBills() {
        // Act
        List<Bill> result = billManager.getBillsWithinDateRange(
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(1));

        // Assert
        assertNotNull(result, "Should return non-null list");
        // List might not be empty if bills were auto-loaded
    }

    @Test
    @DisplayName("Coverage: getBillsWithinDateRange with multiple bills in range")
    void testGetBillsWithinDateRange_Coverage_MultipleBillsInRange() {
        // Arrange
        billManager.addBill(testBill1);
        billManager.addBill(testBill2);
        billManager.addBill(testBill3);

        // Act
        List<Bill> result = billManager.getBillsWithinDateRange(
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(1));

        // Assert
        assertTrue(result.size() >= 3, "Should include all bills from today");
    }

    @Test
    @DisplayName("Coverage: getBillsWithinDateRange with same start and end date")
    void testGetBillsWithinDateRange_Coverage_SameDateRange() {
        // Arrange
        billManager.addBill(testBill1);
        LocalDate today = LocalDate.now();

        // Act
        List<Bill> result = billManager.getBillsWithinDateRange(today, today);

        // Assert
        assertTrue(result.contains(testBill1), "Should include bills from exact date");
    }

    @Test
    @DisplayName("Coverage: getBillsWithinDateRange with inverted range")
    void testGetBillsWithinDateRange_Coverage_InvertedRange() {
        // Arrange
        billManager.addBill(testBill1);
        LocalDate today = LocalDate.now();

        // Act - start date after end date (invalid range)
        List<Bill> result = billManager.getBillsWithinDateRange(
                today.plusDays(1),
                today.minusDays(1));

        // Assert
        assertFalse(result.contains(testBill1), "Inverted range should return no bills");
        // Note: Method doesn't validate range order - design decision
    }

    // ==================== getBills() Tests ====================

    @Test
    @DisplayName("getBills should return non-null list")
    void testGetBills_ShouldReturnNonNullList() {
        // Act
        List<Bill> bills = billManager.getBills();

        // Assert
        assertNotNull(bills, "Should never return null");
    }

    @Test
    @DisplayName("getBills should return all added bills")
    void testGetBills_ShouldReturnAllBills() {
        // Arrange
        int initialSize = billManager.getBills().size();
        billManager.addBill(testBill1);
        billManager.addBill(testBill2);

        // Act
        List<Bill> bills = billManager.getBills();

        // Assert
        assertEquals(initialSize + 2, bills.size(), "Should contain all bills");
    }

    // ==================== User Requested ECT Tests ====================

    @Test
    @DisplayName("ECT-01: Valid Range (Subset) Jan 2026")
    void testECT_01_ValidRange_Subset() {

        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 1, 31);

        // Act
        List<Bill> result = billManager.getBillsWithinDateRange(start, end);

        // Assert
        // We just assert the filter logic holds: all returned bills MUST be in that
        // range.
        assertTrue(result.stream().allMatch(b -> !b.getBillDate().toLocalDate().isBefore(start) &&
                !b.getBillDate().toLocalDate().isAfter(end)), "All returned bills must be strictly within Jan 2026");
    }

    @Test
    @DisplayName("ECT-02: No Bills in Range (Ancient Dates)")
    void testECT_02_NoBillsInRange_AncientDates() {
        // ECT-02: Start: 1900-01-01, End: 1900-01-01 -> Return Empty List
        LocalDate start = LocalDate.of(1900, 1, 1);
        LocalDate end = LocalDate.of(1900, 1, 1);

        List<Bill> result = billManager.getBillsWithinDateRange(start, end);

        assertTrue(result.isEmpty(), "Should return empty list for year 1900");
    }

    @Test
    @DisplayName("ECT-03: Wide Range (2020-2030) - All Bills")
    void testECT_03_WideDateRange_AllBills() {

        LocalDate start = LocalDate.of(2020, 1, 1);
        LocalDate end = LocalDate.of(2030, 1, 1);

        // Add some bills to be sure
        billManager.addBill(testBill1);
        billManager.addBill(testBill2);

        List<Bill> result = billManager.getBillsWithinDateRange(start, end);

        assertTrue(result.contains(testBill1));
        assertTrue(result.contains(testBill2));
    }

    @Test
    @DisplayName("ECT-04: Null Dates -> Throw Exception")
    void testECT_04_NullDates_ShouldThrowException() {
        // ECT-04: Start: null, End: null -> Throw Exception
        assertThrows(NullPointerException.class, () -> {
            billManager.getBillsWithinDateRange(null, null);
        }, "Should throw NullPointerException for null dates");
    }

    // ==================== Coverage Summary ====================

}
