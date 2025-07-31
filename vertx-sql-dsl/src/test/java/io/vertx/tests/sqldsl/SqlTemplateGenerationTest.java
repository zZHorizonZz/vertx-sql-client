package io.vertx.tests.sqldsl;

import io.vertx.sqldsl.DSL;
import io.vertx.sqldsl.Query;
import io.vertx.sqldsl.SortOrder;
import io.vertx.tests.sqldsl.example.User_;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests for SQL template generation functionality.
 */
public class SqlTemplateGenerationTest {

    @Test
    public void testBasicSelectQuery() {
        Query query = DSL.query()
            .select("id", "username", "email")
            .from("users");

        String sql = query.toSqlString();
        Map<String, Object> params = query.getParameters();

        assertEquals("SELECT id, username, email FROM users", sql);
        assertTrue(params.isEmpty());
    }

    @Test
    public void testQueryWithWhereClause() {
        Query query = DSL.query()
            .select("id", "username", "email")
            .from("users")
            .where(User_.EMAIL.eq("john@example.com"));

        String sql = query.toSqlString();
        Map<String, Object> params = query.getParameters();

        assertEquals("SELECT id, username, email FROM users WHERE email_address = #{param1}", sql);
        assertEquals(1, params.size());
        assertEquals("john@example.com", params.get("param1"));
    }

    @Test
    public void testQueryWithMultiplePredicates() {
        Query query = DSL.query()
            .select("*")
            .from("users")
            .where(User_.EMAIL.eq("john@example.com").and(User_.AGE.gt(18)));

        String sql = query.toSqlString();
        Map<String, Object> params = query.getParameters();

        assertEquals("SELECT * FROM users WHERE (email_address = #{param1} AND age > #{param2})", sql);
        assertEquals(2, params.size());
        assertEquals("john@example.com", params.get("param1"));
        assertEquals(18, params.get("param2"));
    }

    @Test
    public void testQueryWithOrderByAndLimit() {
        Query query = DSL.query()
            .select("id", "username")
            .from("users")
            .where(User_.AGE.gt(21))
            .orderBy("username", SortOrder.ASC)
            .limit(10)
            .offset(5);

        String sql = query.toSqlString();
        Map<String, Object> params = query.getParameters();

        assertEquals("SELECT id, username FROM users WHERE age > #{param1} ORDER BY username ASC LIMIT #{param2} OFFSET #{param3}", sql);
        assertEquals(3, params.size());
        assertEquals(21, params.get("param1"));
        assertEquals(10, params.get("param2"));
        assertEquals(5, params.get("param3"));
    }

    @Test
    public void testQueryWithLike() {
        Query query = DSL.query()
            .select("*")
            .from("users")
            .where(User_.USERNAME.like("john%"));

        String sql = query.toSqlString();
        Map<String, Object> params = query.getParameters();

        assertEquals("SELECT * FROM users WHERE username LIKE #{param1}", sql);
        assertEquals(1, params.size());
        assertEquals("john%", params.get("param1"));
    }

    @Test
    public void testQueryWithNotPredicate() {
        Query query = DSL.query()
            .select("*")
            .from("users")
            .where(User_.EMAIL.eq("test@example.com").not());

        String sql = query.toSqlString();
        Map<String, Object> params = query.getParameters();

        assertEquals("SELECT * FROM users WHERE NOT (email_address = #{param1})", sql);
        assertEquals(1, params.size());
        assertEquals("test@example.com", params.get("param1"));
    }

    @Test
    public void testQueryWithBetweenPredicate() {
        Query query = DSL.query()
            .select("*")
            .from("users")
            .where(User_.AGE.between(18, 65));

        String sql = query.toSqlString();
        Map<String, Object> params = query.getParameters();

        assertEquals("SELECT * FROM users WHERE age BETWEEN #{param1} AND #{param2}", sql);
        assertEquals(2, params.size());
        assertEquals(18, params.get("param1"));
        assertEquals(65, params.get("param2"));
    }

    @Test
    public void testQueryWithOrPredicate() {
        Query query = DSL.query()
            .select("*")
            .from("users")
            .where(User_.EMAIL.eq("test1@example.com").or(User_.EMAIL.eq("test2@example.com")));

        String sql = query.toSqlString();
        Map<String, Object> params = query.getParameters();

        assertEquals("SELECT * FROM users WHERE (email_address = #{param1} OR email_address = #{param2})", sql);
        assertEquals(2, params.size());
        assertEquals("test1@example.com", params.get("param1"));
        assertEquals("test2@example.com", params.get("param2"));
    }
}
