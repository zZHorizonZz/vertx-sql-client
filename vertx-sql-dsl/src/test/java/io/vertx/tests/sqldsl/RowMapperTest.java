package io.vertx.tests.sqldsl;

import io.vertx.tests.sqldsl.example.User;
import io.vertx.tests.sqldsl.example.UserRowMapper;
import io.vertx.tests.sqldsl.example.Order;
import io.vertx.tests.sqldsl.example.OrderRowMapper;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.junit.Assert.*;

/**
 * Test for generated row mappers.
 */
public class RowMapperTest {

  @Test
  public void testMapperGenerationExists() {
    // Test that the row mappers were generated and can be referenced
    assertNotNull(UserRowMapper.class);
    assertNotNull(OrderRowMapper.class);
  }

  @Test
  public void testMapperFunction() {
    // Test that the mapper function can be created
    Function<?, User> mapper = UserRowMapper.mapper();
    assertNotNull(mapper);
  }

  @Test
  public void testUserInstantiation() {
    // Test that we can create a User instance (basic constructor test)
    User user = new User();
    assertNotNull(user);
    
    user.setId(1L);
    user.setUsername("testuser");
    assertEquals(Long.valueOf(1L), user.getId());
    assertEquals("testuser", user.getUsername());
  }

  @Test
  public void testOrderInstantiation() {
    // Test that we can create an Order instance
    Order order = new Order();
    assertNotNull(order);
    
    order.setId(1L);
    order.setOrderNumber("ORD-001");
    assertEquals(Long.valueOf(1L), order.getId());
    assertEquals("ORD-001", order.getOrderNumber());
  }

  @Test
  public void testEmptyRowListMapping() {
    // Test mapping empty list
    List<User> users = UserRowMapper.mapFromRows(Arrays.asList());
    assertNotNull(users);
    assertEquals(0, users.size());
  }
}