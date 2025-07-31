package io.vertx.tests.sqldsl;

import io.vertx.tests.sqldsl.example.*;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.junit.Assert.*;

/**
 * Comprehensive test suite for generated row mappers with complex entities.
 */
public class ComprehensiveRowMapperTest {

    @Test
    public void testAllGeneratedMappersExist() {
        // Verify all mappers were generated
        assertNotNull("UserRowMapper should exist", UserRowMapper.class);
        assertNotNull("OrderRowMapper should exist", OrderRowMapper.class);
        assertNotNull("CategoryRowMapper should exist", CategoryRowMapper.class);
        assertNotNull("ProductRowMapper should exist", ProductRowMapper.class);
        assertNotNull("OrderItemRowMapper should exist", OrderItemRowMapper.class);
        assertNotNull("ReviewRowMapper should exist", ReviewRowMapper.class);
    }

    @Test
    public void testMapperFunctions() {
        // Test that all mapper functions can be created
        Function<?, User> userMapper = UserRowMapper.mapper();
        Function<?, Order> orderMapper = OrderRowMapper.mapper();
        Function<?, Category> categoryMapper = CategoryRowMapper.mapper();
        Function<?, Product> productMapper = ProductRowMapper.mapper();
        Function<?, OrderItem> orderItemMapper = OrderItemRowMapper.mapper();
        Function<?, Review> reviewMapper = ReviewRowMapper.mapper();

        assertNotNull("User mapper function should not be null", userMapper);
        assertNotNull("Order mapper function should not be null", orderMapper);
        assertNotNull("Category mapper function should not be null", categoryMapper);
        assertNotNull("Product mapper function should not be null", productMapper);
        assertNotNull("OrderItem mapper function should not be null", orderItemMapper);
        assertNotNull("Review mapper function should not be null", reviewMapper);
    }

    @Test
    public void testComplexEntityInstantiation() {
        // Test Category with BigDecimal fields
        Category category = new Category("Electronics", "Electronic products");
        category.setId(1L);
        category.setDiscountPercentage(new BigDecimal("10.50"));
        category.setMinimumOrderValue(new BigDecimal("100.00"));
        category.setDisplayOrder(1);

        assertEquals("Electronics", category.getName());
        assertEquals(new BigDecimal("10.50"), category.getDiscountPercentage());
        assertEquals(Integer.valueOf(1), category.getDisplayOrder());
        assertTrue("Category should be active by default", category.getActive());
        assertNotNull("Created date should be set", category.getCreatedDate());
    }

    @Test
    public void testProductWithComplexFields() {
        Product product = new Product("SKU-001", "Test Product", new BigDecimal("99.99"), 1L);
        product.setId(1L);
        product.setWeight(new BigDecimal("2.500"));
        product.setRating(new BigDecimal("4.5"));
        product.setStockQuantity(100);
        product.setFeatured(true);

        assertEquals("SKU-001", product.getSku());
        assertEquals(new BigDecimal("99.99"), product.getPrice());
        assertEquals(new BigDecimal("2.500"), product.getWeight());
        assertEquals(new BigDecimal("4.5"), product.getRating());
        assertTrue("Product should be featured", product.getFeatured());
        assertNotNull("Created timestamp should be set", product.getCreatedAt());
    }

    @Test
    public void testOrderWithBigDecimalFields() {
        Order order = new Order("ORD-001", new BigDecimal("150.00"), 1L);
        order.setId(1L);
        order.setTaxAmount(new BigDecimal("15.00"));
        order.setShippingCost(new BigDecimal("5.99"));
        order.setStatus("CONFIRMED");

        assertEquals("ORD-001", order.getOrderNumber());
        assertEquals(new BigDecimal("150.00"), order.getTotalAmount());
        assertEquals(new BigDecimal("15.00"), order.getTaxAmount());
        assertEquals(new BigDecimal("5.99"), order.getShippingCost());
        assertEquals("CONFIRMED", order.getStatus());
    }

    @Test
    public void testOrderItemCalculations() {
        OrderItem item = new OrderItem(1L, 1L, 3, new BigDecimal("29.99"));
        item.setId(1L);
        item.setDiscountAmount(new BigDecimal("5.00"));

        assertEquals(Integer.valueOf(3), item.getQuantity());
        assertEquals(new BigDecimal("29.99"), item.getUnitPrice());
        assertEquals(new BigDecimal("89.97"), item.getTotalPrice()); // 3 * 29.99
        assertEquals(new BigDecimal("5.00"), item.getDiscountAmount());
    }

    @Test
    public void testReviewEntity() {
        Review review = new Review(1L, 1L, new BigDecimal("4.5"), "Great product!", "Really satisfied with this purchase.");
        review.setId(1L);
        review.setVerifiedPurchase(true);
        review.setHelpfulVotes(10);

        assertEquals(new BigDecimal("4.5"), review.getRating());
        assertEquals("Great product!", review.getTitle());
        assertTrue("Should be verified purchase", review.getVerifiedPurchase());
        assertEquals(Integer.valueOf(10), review.getHelpfulVotes());
        assertNotNull("Created timestamp should be set", review.getCreatedAt());
    }

    @Test
    public void testHierarchicalCategoryRelationship() {
        Category parent = new Category("Electronics", "All electronic items");
        parent.setId(1L);

        Category child = new Category("Smartphones", "Mobile phones and accessories");
        child.setId(2L);
        child.setParentCategory(parent);

        assertEquals("Should have parent category", parent, child.getParentCategory());
        assertEquals("Parent should have correct name", "Electronics", child.getParentCategory().getName());
    }

    @Test
    public void testComplexRelationshipSetup() {
        // Create a user
        User user = new User("john_doe", "john@example.com");
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");

        // Create a category
        Category category = new Category("Books", "All kinds of books");
        category.setId(1L);

        // Create a product
        Product product = new Product("BOOK-001", "Java Programming", new BigDecimal("49.99"), 1L);
        product.setId(1L);
        product.setCategory(category);

        // Create an order
        Order order = new Order("ORD-001", new BigDecimal("49.99"), 1L);
        order.setId(1L);
        order.setUser(user);

        // Create an order item
        OrderItem orderItem = new OrderItem(1L, 1L, 1, new BigDecimal("49.99"));
        orderItem.setId(1L);
        orderItem.setOrder(order);
        orderItem.setProduct(product);

        // Create a review
        Review review = new Review(1L, 1L, new BigDecimal("5.0"), "Excellent book!", "Learned a lot from this book.");
        review.setId(1L);
        review.setUser(user);
        review.setProduct(product);

        // Verify relationships
        assertEquals("Order should belong to user", user, order.getUser());
        assertEquals("OrderItem should belong to order", order, orderItem.getOrder());
        assertEquals("OrderItem should reference product", product, orderItem.getProduct());
        assertEquals("Review should be by user", user, review.getUser());
        assertEquals("Review should be for product", product, review.getProduct());
        assertEquals("Product should be in category", category, product.getCategory());
    }

    @Test
    public void testDataTypeHandling() {
        // Test various data types are properly handled
        Category category = new Category();
        category.setDiscountPercentage(new BigDecimal("15.75"));
        category.setMinimumOrderValue(new BigDecimal("50.00"));
        category.setDisplayOrder(5);
        category.setActive(true);
        category.setCreatedDate(LocalDate.of(2023, 1, 1));
        category.setLastUpdated(LocalDateTime.of(2023, 1, 1, 10, 30, 0));

        assertEquals("BigDecimal precision should be maintained", new BigDecimal("15.75"), category.getDiscountPercentage());
        assertEquals("Integer value should be correct", Integer.valueOf(5), category.getDisplayOrder());
        assertTrue("Boolean value should be true", category.getActive());
        assertEquals("LocalDate should be correct", LocalDate.of(2023, 1, 1), category.getCreatedDate());
        assertEquals("LocalDateTime should be correct", LocalDateTime.of(2023, 1, 1, 10, 30, 0), category.getLastUpdated());
    }

    @Test
    public void testEmptyListMapping() {
        // Test mapping empty lists
        List<User> users = UserRowMapper.mapFromRows(Arrays.asList());
        List<Order> orders = OrderRowMapper.mapFromRows(Arrays.asList());
        List<Product> products = ProductRowMapper.mapFromRows(Arrays.asList());

        assertNotNull("User list should not be null", users);
        assertNotNull("Order list should not be null", orders);
        assertNotNull("Product list should not be null", products);
        
        assertEquals("User list should be empty", 0, users.size());
        assertEquals("Order list should be empty", 0, orders.size());
        assertEquals("Product list should be empty", 0, products.size());
    }

    @Test
    public void testNullSafetyInEntities() {
        // Test that entities handle null values gracefully
        Product product = new Product();
        assertNotNull("Default constructor should work", product);
        assertTrue("Should be active by default", product.getActive());
        assertFalse("Should not be featured by default", product.getFeatured());
        assertEquals("Review count should be 0", Integer.valueOf(0), product.getReviewCount());
        assertEquals("Stock quantity should be 0", Integer.valueOf(0), product.getStockQuantity());
        assertEquals("Rating should be zero", BigDecimal.ZERO, product.getRating());

        Review review = new Review();
        assertNotNull("Default constructor should work", review);
        assertFalse("Should not be verified purchase by default", review.getVerifiedPurchase());
        assertEquals("Helpful votes should be 0", Integer.valueOf(0), review.getHelpfulVotes());
        assertNotNull("Created timestamp should be set", review.getCreatedAt());
    }

    @Test
    public void testEntityStringRepresentation() {
        // Test toString methods work correctly
        User user = new User("testuser", "test@example.com");
        user.setId(1L);
        String userStr = user.toString();
        assertNotNull("User toString should not be null", userStr);
        assertTrue("User toString should contain username", userStr.contains("testuser"));

        Product product = new Product("SKU-001", "Test Product", new BigDecimal("99.99"), 1L);
        product.setId(1L);
        String productStr = product.toString();
        assertNotNull("Product toString should not be null", productStr);
        assertTrue("Product toString should contain SKU", productStr.contains("SKU-001"));
    }
}