package io.vertx.tests.sqldsl;

import io.vertx.sqldsl.*;
import io.vertx.tests.sqldsl.example.*;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Integration tests for DSL query building with complex entities and relationships.
 */
public class DSLQueryIntegrationTest {

    @Test
    public void testMetamodelGeneration() {
        // Verify metamodel classes were generated
        assertEquals("User table name", "users", User_.TABLE_NAME);
        assertEquals("Order table name", "orders", Order_.TABLE_NAME);
        assertEquals("Category table name", "categories", Category_.TABLE_NAME);
        assertEquals("Product table name", "products", Product_.TABLE_NAME);
        assertEquals("OrderItem table name", "order_items", OrderItem_.TABLE_NAME);
        assertEquals("Review table name", "reviews", Review_.TABLE_NAME);
    }

    @Test
    public void testBasicPropertyTypes() {
        // Test basic properties
        assertTrue("User ID should be ComparableProperty", User_.ID instanceof ComparableProperty);
        assertTrue("User username should be StringProperty", User_.USERNAME instanceof StringProperty);
        assertTrue("User email should be StringProperty", User_.EMAIL instanceof StringProperty);
        assertTrue("User age should be ComparableProperty", User_.AGE instanceof ComparableProperty);
        assertTrue("User active should be Property", User_.ACTIVE instanceof Property);

        // Test Product properties with BigDecimal
        assertTrue("Product price should be ComparableProperty", Product_.PRICE instanceof ComparableProperty);
        assertTrue("Product weight should be ComparableProperty", Product_.WEIGHT instanceof ComparableProperty);
        assertTrue("Product rating should be ComparableProperty", Product_.RATING instanceof ComparableProperty);
    }

    @Test
    public void testRelationshipProperties() {
        // Test OneToMany relationships
        assertTrue("User orders should be RelationshipProperty", User_.ORDERS instanceof RelationshipProperty);
        assertTrue("User reviews should be RelationshipProperty", User_.REVIEWS instanceof RelationshipProperty);
        assertTrue("Category subcategories should be RelationshipProperty", Category_.SUBCATEGORIES instanceof RelationshipProperty);
        assertTrue("Product reviews should be RelationshipProperty", Product_.REVIEWS instanceof RelationshipProperty);

        // Test ManyToOne relationships  
        assertTrue("Order user should be RelationshipProperty", Order_.USER instanceof RelationshipProperty);
        assertTrue("Product category should be RelationshipProperty", Product_.CATEGORY instanceof RelationshipProperty);
        assertTrue("Category parent should be RelationshipProperty", Category_.PARENTCATEGORY instanceof RelationshipProperty);
    }

    @Test
    public void testSimpleQueries() {
        // Test simple equality predicate
        Predicate userNamePredicate = Predicates.equal(User_.USERNAME, "john_doe");
        assertNotNull("Predicate should not be null", userNamePredicate);

        // Test comparison predicates with BigDecimal
        Predicate priceRange = Predicates.between(Product_.PRICE, new BigDecimal("10.00"), new BigDecimal("100.00"));
        assertNotNull("Price range predicate should not be null", priceRange);

        // Test string operations
        Predicate nameSearch = Predicates.like(Product_.NAME, "%Java%");
        assertNotNull("Name search predicate should not be null", nameSearch);
    }

    @Test
    public void testComplexPredicates() {
        // Test compound predicates
        Predicate activeUser = Predicates.equal(User_.ACTIVE, true);
        Predicate hasAge = Predicates.greaterThan(User_.AGE, 21);
        Predicate adultActiveUser = Predicates.and(activeUser, hasAge);
        
        assertNotNull("Compound predicate should not be null", adultActiveUser);
        assertTrue("Should be composite predicate", adultActiveUser instanceof CompositePredicate);

        // Test complex product search
        Predicate inStock = Predicates.greaterThan(Product_.STOCKQUANTITY, 0);
        Predicate affordable = Predicates.lessThanOrEqual(Product_.PRICE, new BigDecimal("50.00"));
        Predicate featured = Predicates.equal(Product_.FEATURED, true);
        
        Predicate searchCriteria = Predicates.and(
            Predicates.and(inStock, affordable),
            featured
        );
        
        assertNotNull("Complex search criteria should not be null", searchCriteria);
    }

    @Test
    public void testQueryBuilding() {
        // Test basic query
        Query userQuery = DSL.select()
            .from(User_.TABLE_NAME)
            .where(Predicates.equal(User_.ACTIVE, true))
            .orderBy(User_.USERNAME, SortOrder.ASC)
            .limit(10);

        assertNotNull("Query should not be null", userQuery);
        assertEquals("Should have correct table", User_.TABLE_NAME, userQuery.getFromTable());
        assertNotNull("Should have where predicate", userQuery.getWherePredicate());
        assertEquals("Should have limit", Integer.valueOf(10), userQuery.getLimit());

        // Test complex query with joins
        Query orderQuery = DSL.select("o.order_id", "o.order_number", "u.username")
            .from("orders o")
            .join(JoinType.INNER, "users u", Predicates.equal("o.user_id", "u.user_id"))
            .where(Predicates.greaterThan("o.total_amount", new BigDecimal("100.00")))
            .orderBy("o.order_date", SortOrder.DESC);

        assertNotNull("Complex query should not be null", orderQuery);
        assertEquals("Should have join", 1, orderQuery.getJoinClauses().size());
        assertEquals("Should have correct join type", JoinType.INNER, orderQuery.getJoinClauses().get(0).getJoinType());
    }

    @Test
    public void testSqlTemplateGeneration() {
        SqlTemplateGenerator generator = new SqlTemplateGenerator();

        // Test simple query generation
        Query simpleQuery = DSL.select()
            .from(User_.TABLE_NAME)
            .where(Predicates.equal(User_.USERNAME, "testuser"))
            .limit(1);

        String sql = generator.generateSelectTemplate(simpleQuery);
        Map<String, Object> params = generator.getParameters();

        assertNotNull("Generated SQL should not be null", sql);
        assertTrue("SQL should contain SELECT", sql.contains("SELECT"));
        assertTrue("SQL should contain FROM users", sql.contains("FROM users"));
        assertTrue("SQL should contain WHERE", sql.contains("WHERE"));
        assertTrue("SQL should contain parameter placeholder", sql.contains("#{param"));
        assertFalse("Parameters should not be empty", params.isEmpty());
        assertEquals("Should have testuser parameter", "testuser", params.get("param1"));
    }

    @Test
    public void testComplexSqlGeneration() {
        SqlTemplateGenerator generator = new SqlTemplateGenerator();

        // Test complex query with multiple conditions
        Predicate complexCondition = Predicates.and(
            Predicates.equal(Product_.ACTIVE, true),
            Predicates.and(
                Predicates.greaterThan(Product_.PRICE, new BigDecimal("20.00")),
                Predicates.like(Product_.NAME, "%Book%")
            )
        );

        Query complexQuery = DSL.select()
            .from(Product_.TABLE_NAME)
            .where(complexCondition)
            .orderBy(Product_.PRICE, SortOrder.ASC)
            .orderBy(Product_.NAME, SortOrder.ASC)
            .limit(50)
            .offset(10);

        String sql = generator.generateSelectTemplate(complexQuery);
        Map<String, Object> params = generator.getParameters();

        assertNotNull("Complex SQL should not be null", sql);
        assertTrue("SQL should contain nested conditions", sql.contains("(") && sql.contains(")"));
        assertTrue("SQL should contain ORDER BY", sql.contains("ORDER BY"));
        assertTrue("SQL should contain LIMIT", sql.contains("LIMIT"));
        assertTrue("SQL should contain OFFSET", sql.contains("OFFSET"));
        
        // Verify parameters
        assertTrue("Should have active parameter", params.containsValue(true));
        assertTrue("Should have price parameter", params.containsValue(new BigDecimal("20.00")));
        assertTrue("Should have name pattern parameter", params.containsValue("%Book%"));
        assertTrue("Should have limit parameter", params.containsValue(50));
        assertTrue("Should have offset parameter", params.containsValue(10));
    }

    @Test
    public void testJoinQueryGeneration() {
        SqlTemplateGenerator generator = new SqlTemplateGenerator();

        // Create a query with join
        Predicate joinCondition = Predicates.equal("o.user_id", "u.user_id");
        Predicate whereCondition = Predicates.and(
            Predicates.equal("u.active", true),
            Predicates.greaterThan("o.total_amount", new BigDecimal("100.00"))
        );

        Query joinQuery = DSL.select("u.username", "o.order_number", "o.total_amount")
            .from("orders o")
            .join(JoinType.INNER, "users u", joinCondition)
            .where(whereCondition)
            .orderBy("o.order_date", SortOrder.DESC);

        String sql = generator.generateSelectTemplate(joinQuery);
        Map<String, Object> params = generator.getParameters();

        assertNotNull("Join SQL should not be null", sql);
        assertTrue("SQL should contain INNER JOIN", sql.contains("INNER JOIN"));
        assertTrue("SQL should contain join condition", sql.contains("ON"));
        assertTrue("SQL should have selected columns", sql.contains("u.username"));
        assertTrue("SQL should have selected columns", sql.contains("o.order_number"));
        
        // Verify parameters
        assertTrue("Should have active parameter", params.containsValue(true));
        assertTrue("Should have amount parameter", params.containsValue(new BigDecimal("100.00")));
    }

    @Test
    public void testBetweenPredicates() {
        SqlTemplateGenerator generator = new SqlTemplateGenerator();

        // Test BETWEEN predicate
        Predicate dateBetween = Predicates.between(
            Product_.CREATEDAT, 
            LocalDateTime.of(2023, 1, 1, 0, 0, 0),
            LocalDateTime.of(2023, 12, 31, 23, 59, 59)
        );

        Query betweenQuery = DSL.select().from(Product_.TABLE_NAME).where(dateBetween);
        String sql = generator.generateSelectTemplate(betweenQuery);
        Map<String, Object> params = generator.getParameters();

        assertNotNull("BETWEEN SQL should not be null", sql);
        assertTrue("SQL should contain BETWEEN", sql.contains("BETWEEN"));
        assertTrue("SQL should contain AND", sql.contains("AND"));
        assertEquals("Should have 2 parameters for BETWEEN", 2, params.size());
    }

    @Test
    public void testCaseInsensitiveLike() {
        SqlTemplateGenerator generator = new SqlTemplateGenerator();

        // Test case-insensitive LIKE
        Predicate caseInsensitiveSearch = Predicates.ilike(Product_.NAME, "%java%");
        Query query = DSL.select().from(Product_.TABLE_NAME).where(caseInsensitiveSearch);
        
        String sql = generator.generateSelectTemplate(query);
        
        assertNotNull("Case-insensitive SQL should not be null", sql);
        assertTrue("SQL should contain UPPER for case-insensitive search", sql.contains("UPPER"));
    }

    @Test
    public void testNotPredicates() {
        SqlTemplateGenerator generator = new SqlTemplateGenerator();

        // Test NOT predicate
        Predicate notActive = Predicates.not(Predicates.equal(User_.ACTIVE, true));
        Query query = DSL.select().from(User_.TABLE_NAME).where(notActive);
        
        String sql = generator.generateSelectTemplate(query);
        
        assertNotNull("NOT SQL should not be null", sql);
        assertTrue("SQL should contain NOT", sql.contains("NOT"));
    }
}