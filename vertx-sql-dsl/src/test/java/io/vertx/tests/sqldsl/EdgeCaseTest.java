package io.vertx.tests.sqldsl;

import io.vertx.sqldsl.*;
import io.vertx.tests.sqldsl.example.*;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests for edge cases and error handling in the DSL.
 */
public class EdgeCaseTest {

    @Test
    public void testNullPredicateHandling() {
        // Test that DSL handles null values gracefully
        try {
            Predicate nullPredicate = Predicates.equal(User_.USERNAME, null);
            assertNotNull("Should handle null value predicate", nullPredicate);
        } catch (Exception e) {
            // This is acceptable - some implementations might reject null values
            assertTrue("Exception should be meaningful", e.getMessage() != null);
        }
    }

    @Test
    public void testEmptyStringHandling() {
        // Test empty string predicates
        Predicate emptyStringPredicate = Predicates.equal(User_.USERNAME, "");
        assertNotNull("Should handle empty string predicate", emptyStringPredicate);

        // Test with LIKE pattern
        Predicate emptyLikePredicate = Predicates.like(Product_.NAME, "");
        assertNotNull("Should handle empty LIKE pattern", emptyLikePredicate);
    }

    @Test
    public void testZeroAndNegativeNumbers() {
        // Test zero values
        Predicate zeroPrice = Predicates.equal(Product_.PRICE, BigDecimal.ZERO);
        assertNotNull("Should handle zero BigDecimal", zeroPrice);

        Predicate zeroQuantity = Predicates.equal(Product_.STOCKQUANTITY, 0);
        assertNotNull("Should handle zero integer", zeroQuantity);

        // Test negative values
        Predicate negativePrice = Predicates.lessThan(Product_.PRICE, new BigDecimal("-1.00"));
        assertNotNull("Should handle negative BigDecimal", negativePrice);
    }

    @Test
    public void testVeryLargeNumbers() {
        // Test with very large BigDecimal values
        BigDecimal veryLarge = new BigDecimal("999999999.99");
        Predicate largePricePredicate = Predicates.greaterThan(Product_.PRICE, veryLarge);
        assertNotNull("Should handle very large BigDecimal", largePricePredicate);

        // Test with large integer
        Predicate largeQuantity = Predicates.equal(Product_.STOCKQUANTITY, Integer.MAX_VALUE);
        assertNotNull("Should handle MAX_VALUE integer", largeQuantity);
    }

    @Test
    public void testSpecialCharactersInStrings() {
        // Test strings with special characters
        String specialChars = "Test's \"Product\" & More <tag>";
        Predicate specialCharPredicate = Predicates.equal(Product_.NAME, specialChars);
        assertNotNull("Should handle special characters", specialCharPredicate);

        // Test LIKE with wildcards
        Predicate wildcardPredicate = Predicates.like(Product_.NAME, "%'s%");
        assertNotNull("Should handle wildcards with quotes", wildcardPredicate);
    }

    @Test
    public void testUnicodeStrings() {
        // Test Unicode characters
        String unicodeString = "Café ñoño 中文 🚀";
        Predicate unicodePredicate = Predicates.equal(Product_.NAME, unicodeString);
        assertNotNull("Should handle Unicode characters", unicodePredicate);
    }

    @Test
    public void testVeryLongStrings() {
        // Test very long strings
        StringBuilder longString = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longString.append("VeryLongString");
        }
        
        Predicate longStringPredicate = Predicates.equal(Product_.DESCRIPTION, longString.toString());
        assertNotNull("Should handle very long strings", longStringPredicate);
    }

    @Test
    public void testDeeplyNestedPredicates() {
        // Create deeply nested AND/OR predicates
        Predicate base = Predicates.equal(User_.ACTIVE, true);
        Predicate nested = base;
        
        for (int i = 0; i < 10; i++) {
            Predicate additional = Predicates.greaterThan(User_.AGE, i * 5);
            nested = Predicates.and(nested, additional);
        }
        
        assertNotNull("Should handle deeply nested predicates", nested);
        assertTrue("Should be composite predicate", nested instanceof CompositePredicate);
    }

    @Test
    public void testQueryWithManyOrderByClauses() {
        // Test query with many ORDER BY clauses
        Query query = DSL.select()
            .from(Product_.TABLE_NAME)
            .orderBy(Product_.NAME, SortOrder.ASC)
            .orderBy(Product_.PRICE, SortOrder.DESC)
            .orderBy(Product_.RATING, SortOrder.DESC)
            .orderBy(Product_.STOCKQUANTITY, SortOrder.ASC)
            .orderBy(Product_.CREATEDAT, SortOrder.DESC);

        assertNotNull("Should handle multiple ORDER BY clauses", query);
        assertEquals("Should have 5 order by clauses", 5, query.getOrderByClauses().size());
    }

    @Test
    public void testQueryWithManySelectedColumns() {
        // Test query with many selected columns
        Query query = DSL.select(
            "product_id", "sku", "name", "description", "price", "cost", "weight_kg",
            "stock_quantity", "minimum_stock_level", "is_active", "is_featured",
            "rating", "review_count", "created_at", "updated_at", "category_id"
        ).from(Product_.TABLE_NAME);

        assertNotNull("Should handle many selected columns", query);
        assertEquals("Should have 16 columns", 16, query.getSelectColumns().size());
    }

    @Test
    public void testSqlGenerationWithComplexScenarios() {
        SqlTemplateGenerator generator = new SqlTemplateGenerator();

        // Test with extreme values
        Predicate extremeConditions = Predicates.and(
            Predicates.between(Product_.PRICE, new BigDecimal("0.01"), new BigDecimal("999999.99")),
            Predicates.like(Product_.NAME, "%Test's \"Special\" Product%")
        );

        Query extremeQuery = DSL.select()
            .from(Product_.TABLE_NAME)
            .where(extremeConditions)
            .limit(Integer.MAX_VALUE);

        String sql = generator.generateSelectTemplate(extremeQuery);
        Map<String, Object> params = generator.getParameters();

        assertNotNull("Should generate SQL for extreme scenarios", sql);
        assertFalse("Should have parameters", params.isEmpty());
        assertTrue("Should contain BETWEEN", sql.contains("BETWEEN"));
        assertTrue("Should contain LIKE", sql.contains("LIKE"));
    }

    @Test
    public void testBetweenWithSameValues() {
        // Test BETWEEN with identical start and end values
        Predicate sameBetween = Predicates.between(Product_.PRICE, new BigDecimal("50.00"), new BigDecimal("50.00"));
        assertNotNull("Should handle BETWEEN with same values", sameBetween);

        SqlTemplateGenerator generator = new SqlTemplateGenerator();
        Query query = DSL.select().from(Product_.TABLE_NAME).where(sameBetween);
        String sql = generator.generateSelectTemplate(query);
        
        assertNotNull("Should generate SQL for same BETWEEN values", sql);
        assertTrue("Should still contain BETWEEN", sql.contains("BETWEEN"));
    }

    @Test
    public void testBetweenWithReversedValues() {
        // Test BETWEEN with reversed values (end < start)
        Predicate reversedBetween = Predicates.between(Product_.PRICE, new BigDecimal("100.00"), new BigDecimal("50.00"));
        assertNotNull("Should handle reversed BETWEEN values", reversedBetween);

        // The SQL should still be generated, though it might not match any records
        SqlTemplateGenerator generator = new SqlTemplateGenerator();
        Query query = DSL.select().from(Product_.TABLE_NAME).where(reversedBetween);
        String sql = generator.generateSelectTemplate(query);
        
        assertNotNull("Should generate SQL for reversed BETWEEN values", sql);
    }

    @Test
    public void testComplexPredicateWithSpecialValues() {
        // Test complex predicate combining various edge cases
        Predicate complexEdgeCase = Predicates.or(
            Predicates.and(
                Predicates.equal(Product_.PRICE, BigDecimal.ZERO),
                Predicates.like(Product_.NAME, "")
            ),
            Predicates.and(
                Predicates.greaterThan(Product_.STOCKQUANTITY, Integer.MAX_VALUE - 1),
                Predicates.not(Predicates.equal(Product_.ACTIVE, false))
            )
        );

        assertNotNull("Should handle complex edge case predicate", complexEdgeCase);
        assertTrue("Should be composite predicate", complexEdgeCase instanceof CompositePredicate);
    }

    @Test
    public void testQueryLimitsAndOffsets() {
        // Test various limit and offset combinations
        Query query1 = DSL.select().from(User_.TABLE_NAME).limit(0);
        assertNotNull("Should handle zero limit", query1);
        assertEquals("Should have zero limit", Integer.valueOf(0), query1.getLimit());

        Query query2 = DSL.select().from(User_.TABLE_NAME).offset(0);
        assertNotNull("Should handle zero offset", query2);
        assertEquals("Should have zero offset", Integer.valueOf(0), query2.getOffset());

        Query query3 = DSL.select().from(User_.TABLE_NAME).limit(Integer.MAX_VALUE).offset(Integer.MAX_VALUE);
        assertNotNull("Should handle maximum values", query3);
        assertEquals("Should have max limit", Integer.valueOf(Integer.MAX_VALUE), query3.getLimit());
        assertEquals("Should have max offset", Integer.valueOf(Integer.MAX_VALUE), query3.getOffset());
    }

    @Test
    public void testParameterGeneration() {
        SqlTemplateGenerator generator = new SqlTemplateGenerator();

        // Create a query with many parameters to test parameter naming
        Predicate manyParams = Predicates.and(
            Predicates.equal(User_.USERNAME, "user1"),
            Predicates.and(
                Predicates.equal(User_.EMAIL, "user1@example.com"),
                Predicates.and(
                    Predicates.greaterThan(User_.AGE, 25),
                    Predicates.equal(User_.ACTIVE, true)
                )
            )
        );

        Query query = DSL.select().from(User_.TABLE_NAME).where(manyParams);
        String sql = generator.generateSelectTemplate(query);
        Map<String, Object> params = generator.getParameters();

        assertNotNull("Should generate SQL with many parameters", sql);
        assertEquals("Should have 4 parameters", 4, params.size());
        
        // Verify parameter naming is consistent
        assertTrue("Should have param1", params.containsKey("param1"));
        assertTrue("Should have param2", params.containsKey("param2"));
        assertTrue("Should have param3", params.containsKey("param3"));
        assertTrue("Should have param4", params.containsKey("param4"));
    }

    @Test
    public void testGeneratorReset() {
        SqlTemplateGenerator generator = new SqlTemplateGenerator();

        // Generate first query
        Query query1 = DSL.select().from(User_.TABLE_NAME).where(Predicates.equal(User_.USERNAME, "user1"));
        generator.generateSelectTemplate(query1);
        assertEquals("Should have 1 parameter", 1, generator.getParameters().size());

        // Reset and generate second query
        generator.reset();
        Query query2 = DSL.select().from(Product_.TABLE_NAME).where(Predicates.equal(Product_.NAME, "product1"));
        generator.generateSelectTemplate(query2);
        
        Map<String, Object> params = generator.getParameters();
        assertEquals("Should have 1 parameter after reset", 1, params.size());
        assertEquals("Should start from param1 again", "product1", params.get("param1"));
    }
}