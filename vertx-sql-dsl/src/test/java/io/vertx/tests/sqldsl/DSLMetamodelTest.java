package io.vertx.tests.sqldsl;

import io.vertx.sqldsl.*;
import io.vertx.tests.sqldsl.example.User_;
import io.vertx.tests.sqldsl.example.Order_;
import org.junit.Test;

import static io.vertx.sqldsl.DSL.*;
import static org.junit.Assert.*;

/**
 * Test class demonstrating the SQL DSL functionality using generated metamodel classes.
 * This shows how the DSL would be used in practice with type-safe queries and relationships.
 */
public class DSLMetamodelTest {

    @Test
    public void testBasicQueryBuilding() {
        // Test basic query construction using generated metamodel
        Query query = query()
            .select("id", "username", "email")
            .from(User_.TABLE_NAME)
            .where(User_.EMAIL.endsWith("@gmail.com"))
            .orderBy("username", SortOrder.ASC)
            .limit(10)
            .offset(20);

        assertNotNull(query);
        assertEquals(3, query.getSelectColumns().size());
        assertEquals("users", query.getFromTable());
        assertNotNull(query.getWherePredicate());
        assertEquals(1, query.getOrderByClauses().size());
        assertEquals(Integer.valueOf(10), query.getLimit());
        assertEquals(Integer.valueOf(20), query.getOffset());
    }

    @Test
    public void testPredicateOperations() {
        // Test basic predicate operations using generated metamodel
        Predicate emailPredicate = User_.EMAIL.eq("john@example.com");
        Predicate agePredicate = User_.AGE.gt(18);
        Predicate activePredicate = User_.ACTIVE.eq(true);

        assertNotNull(emailPredicate);
        assertNotNull(agePredicate);
        assertNotNull(activePredicate);

        // Test predicate combination
        Predicate combined = emailPredicate.and(agePredicate).and(activePredicate);
        assertNotNull(combined);
        assertTrue(combined instanceof CompositePredicate);
    }

    @Test
    public void testStringOperations() {
        // Test string-specific operations using generated metamodel
        Predicate likePredicate = User_.USERNAME.like("john%");
        Predicate containsPredicate = User_.EMAIL.contains("example");
        Predicate startsWithPredicate = User_.FIRSTNAME.startsWith("J");
        Predicate endsWithPredicate = User_.EMAIL.endsWith("@gmail.com");

        assertNotNull(likePredicate);
        assertNotNull(containsPredicate);
        assertNotNull(startsWithPredicate);
        assertNotNull(endsWithPredicate);
    }

    @Test
    public void testComparableOperations() {
        // Test comparable operations using generated metamodel
        Predicate gtPredicate = User_.AGE.gt(21);
        Predicate ltePredicate = User_.AGE.lte(65);
        Predicate betweenPredicate = User_.AGE.between(18, 65);
        Predicate idInPredicate = User_.ID.in(1L, 2L, 3L);

        assertNotNull(gtPredicate);
        assertNotNull(ltePredicate);
        assertNotNull(betweenPredicate);
        assertNotNull(idInPredicate);

        assertTrue(betweenPredicate instanceof BetweenPredicate);
    }

    @Test
    public void testNullOperations() {
        // Test null operations using generated metamodel
        Predicate isNullPredicate = User_.FIRSTNAME.isNull();
        Predicate isNotNullPredicate = User_.LASTNAME.isNotNull();

        assertNotNull(isNullPredicate);
        assertNotNull(isNotNullPredicate);

        assertTrue(isNullPredicate instanceof SimplePredicate);
        assertTrue(isNotNullPredicate instanceof SimplePredicate);

        SimplePredicate nullPred = (SimplePredicate) isNullPredicate;
        assertEquals(PredicateOperator.IS_NULL, nullPred.getOperator());

        SimplePredicate notNullPred = (SimplePredicate) isNotNullPredicate;
        assertEquals(PredicateOperator.IS_NOT_NULL, notNullPred.getOperator());
    }

    @Test
    public void testComplexQuery() {
        // Test complex query with multiple conditions using generated metamodel
        Predicate complexPredicate = User_.ID.in(1L, 2L, 3L)
            .and(User_.EMAIL.endsWith("@gmail.com"))
            .and(User_.USERNAME.startsWith("test").not());

        Query complexQuery = query()
            .select("id", "username", "email", "first_name", "last_name")
            .from(User_.TABLE_NAME)
            .where(complexPredicate)
            .orderBy("username", SortOrder.ASC)
            .orderBy("id", SortOrder.DESC)
            .limit(20)
            .offset(40);

        assertNotNull(complexQuery);
        assertEquals(5, complexQuery.getSelectColumns().size());
        assertEquals(2, complexQuery.getOrderByClauses().size());
        assertNotNull(complexQuery.getWherePredicate());

        // Verify order by clauses
        assertEquals("username", complexQuery.getOrderByClauses().get(0).getColumnName());
        assertEquals(SortOrder.ASC, complexQuery.getOrderByClauses().get(0).getSortOrder());
        assertEquals("id", complexQuery.getOrderByClauses().get(1).getColumnName());
        assertEquals(SortOrder.DESC, complexQuery.getOrderByClauses().get(1).getSortOrder());
    }

    @Test
    public void testPredicateUtilities() {
        // Test predicate utility methods using generated metamodel
        Predicate pred1 = User_.USERNAME.eq("john");
        Predicate pred2 = User_.AGE.gt(18);
        Predicate pred3 = User_.ACTIVE.eq(true);

        // Test static utility methods
        Predicate andPredicate = Predicates.and(pred1, pred2, pred3);
        Predicate orPredicate = Predicates.or(pred1, pred2);
        Predicate notPredicate = Predicates.not(pred1);

        assertNotNull(andPredicate);
        assertNotNull(orPredicate);
        assertNotNull(notPredicate);

        assertTrue(notPredicate instanceof NotPredicate);
    }

    @Test
    public void testConditionalPredicates() {
        // Test conditional predicate building using generated metamodel
        String searchTerm = "john";
        Integer minAge = 21;

        Predicate conditionalPredicate = Predicates.when(
            searchTerm != null && !searchTerm.isEmpty(),
            User_.USERNAME.contains(searchTerm)
        ).and(Predicates.when(
            minAge != null,
            User_.AGE.gte(minAge)
        ));

        assertNotNull(conditionalPredicate);
        assertTrue(conditionalPredicate instanceof CompositePredicate);
    }

    // NEW TESTS FOR RELATIONSHIPS AND JOINS

    @Test
    public void testRelationshipProperties() {
        // Test that relationship properties are properly generated
        assertNotNull(User_.ORDERS);
        assertNotNull(Order_.USER);

        // Verify relationship types
        assertEquals(RelationshipProperty.RelationshipType.ONE_TO_MANY, User_.ORDERS.getRelationshipType());
        assertEquals(RelationshipProperty.RelationshipType.MANY_TO_ONE, Order_.USER.getRelationshipType());

        // Verify relationship metadata
        assertEquals("orders", User_.ORDERS.getTargetTableName());
        assertEquals("userId", User_.ORDERS.getJoinColumn());
        assertEquals("id", User_.ORDERS.getReferencedColumn());

        assertEquals("users", Order_.USER.getTargetTableName());
        assertEquals("user_id", Order_.USER.getJoinColumn());
        assertEquals("user_id", Order_.USER.getReferencedColumn());
    }

    @Test
    public void testBasicJoinQuery() {
        // Test basic join between User and Order using relationship property
        JoinClause joinClause = User_.ORDERS.innerJoin("o");

        Query joinQuery = query()
            .select("u.username", "u.email", "o.order_number", "o.total_amount")
            .from(User_.TABLE_NAME + " u")
            .innerJoin(joinClause.getTableName(), "o", joinClause.getJoinCondition())
            .where(User_.ACTIVE.eq(true))
            .orderBy("u.username", SortOrder.ASC);

        assertNotNull(joinQuery);
        assertEquals(4, joinQuery.getSelectColumns().size());
        assertEquals("users u", joinQuery.getFromTable());
        assertEquals(1, joinQuery.getJoinClauses().size());

        JoinClause actualJoinClause = joinQuery.getJoinClauses().get(0);
        assertEquals(JoinType.INNER, actualJoinClause.getJoinType());
        assertEquals("orders", actualJoinClause.getTableName());
        assertEquals("o", actualJoinClause.getTableAlias());
    }

    @Test
    public void testLeftJoinClause() {
        // Test creating left join clause using relationship property
        JoinClause leftJoinClause = User_.ORDERS.leftJoin("o");

        assertNotNull(leftJoinClause);
        assertEquals(JoinType.LEFT, leftJoinClause.getJoinType());
        assertEquals("orders", leftJoinClause.getTableName());
        assertEquals("o", leftJoinClause.getTableAlias());
        assertNotNull(leftJoinClause.getJoinCondition());
    }

    @Test
    public void testRightJoinClause() {
        // Test creating right join clause using relationship property
        JoinClause rightJoinClause = Order_.USER.rightJoin("u");

        assertNotNull(rightJoinClause);
        assertEquals(JoinType.RIGHT, rightJoinClause.getJoinType());
        assertEquals("users", rightJoinClause.getTableName());
        assertEquals("u", rightJoinClause.getTableAlias());
        assertNotNull(rightJoinClause.getJoinCondition());
    }

    @Test
    public void testJoinClauseWithoutAlias() {
        // Test creating join clause without alias
        JoinClause joinClause = User_.ORDERS.innerJoin();

        assertNotNull(joinClause);
        assertEquals(JoinType.INNER, joinClause.getJoinType());
        assertEquals("orders", joinClause.getTableName());
        assertNull(joinClause.getTableAlias());
        assertEquals("orders", joinClause.getTableReference());
        assertNotNull(joinClause.getJoinCondition());
    }

    @Test
    public void testFilteringWithRelationshipProperties() {
        // Test filtering using relationship properties in basic queries
        Query userQuery = query()
            .select("username", "email")
            .from(User_.TABLE_NAME)
            .where(User_.AGE.between(18, 65)
                .and(User_.ACTIVE.eq(true)))
            .orderBy("username", SortOrder.ASC);

        assertNotNull(userQuery);
        assertNotNull(userQuery.getWherePredicate());
        assertTrue(userQuery.getWherePredicate() instanceof CompositePredicate);

        Query orderQuery = query()
            .select("order_number", "total_amount")
            .from(Order_.TABLE_NAME)
            .where(Order_.TOTALAMOUNT.gt(100.0)
                .and(Order_.ORDERNUMBER.startsWith("ORD")))
            .orderBy("order_date", SortOrder.DESC);

        assertNotNull(orderQuery);
        assertNotNull(orderQuery.getWherePredicate());
    }

    @Test
    public void testOrderEntityQueries() {
        // Test queries on Order entity using generated metamodel
        Query orderQuery = query()
            .select("order_number", "total_amount", "order_date")
            .from(Order_.TABLE_NAME)
            .where(Order_.TOTALAMOUNT.between(50.0, 500.0)
                .and(Order_.ORDERNUMBER.startsWith("ORD")))
            .orderBy("order_date", SortOrder.DESC)
            .limit(20);

        assertNotNull(orderQuery);
        assertEquals(Order_.TABLE_NAME, orderQuery.getFromTable());
        assertEquals("orders", orderQuery.getFromTable());
        assertNotNull(orderQuery.getWherePredicate());
    }

    @Test
    public void testRelationshipMetadata() {
        // Test accessing relationship metadata
        RelationshipProperty<io.vertx.tests.sqldsl.example.Order> ordersRelation = User_.ORDERS;
        RelationshipProperty<io.vertx.tests.sqldsl.example.User> userRelation = Order_.USER;

        // Test User -> Orders relationship
        assertEquals("orders", ordersRelation.getTargetTableName());
        assertEquals("userId", ordersRelation.getJoinColumn());
        assertEquals("id", ordersRelation.getReferencedColumn());
        assertEquals(RelationshipProperty.RelationshipType.ONE_TO_MANY, ordersRelation.getRelationshipType());

        // Test Order -> User relationship
        assertEquals("users", userRelation.getTargetTableName());
        assertEquals("user_id", userRelation.getJoinColumn());
        assertEquals("user_id", userRelation.getReferencedColumn());
        assertEquals(RelationshipProperty.RelationshipType.MANY_TO_ONE, userRelation.getRelationshipType());

        // Test toString methods
        assertNotNull(ordersRelation.toString());
        assertNotNull(userRelation.toString());
        assertTrue(ordersRelation.toString().contains("ONE_TO_MANY"));
        assertTrue(userRelation.toString().contains("MANY_TO_ONE"));
    }
}
