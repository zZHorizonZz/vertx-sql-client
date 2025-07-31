package io.vertx.sqldsl;

import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Fluent query builder for constructing SQL queries in a type-safe manner.
 * Supports SELECT, FROM, JOIN (INNER, LEFT, RIGHT, FULL OUTER), WHERE, ORDER BY, LIMIT, and OFFSET operations.
 */
public class Query {

    private final List<String> selectColumns;
    private String fromTable;
    private final List<JoinClause> joinClauses;
    private Predicate wherePredicate;
    private final List<OrderByClause> orderByClauses;
    private Integer limitValue;
    private Integer offsetValue;

    /**
     * Creates a new query builder.
     */
    public Query() {
        this.selectColumns = new ArrayList<>();
        this.joinClauses = new ArrayList<>();
        this.orderByClauses = new ArrayList<>();
    }

    /**
     * Specifies the columns to select.
     * @param columns the column names to select
     * @return this query builder for method chaining
     */
    public Query select(String... columns) {
        if (columns != null) {
            this.selectColumns.addAll(Arrays.asList(columns));
        }
        return this;
    }

    /**
     * Specifies the table to select from.
     * @param tableName the table name
     * @return this query builder for method chaining
     */
    public Query from(String tableName) {
        this.fromTable = tableName;
        return this;
    }

    /**
     * Adds a WHERE clause with the specified predicate.
     * @param predicate the predicate for the WHERE clause
     * @return this query builder for method chaining
     */
    public Query where(Predicate predicate) {
        if (this.wherePredicate == null) {
            this.wherePredicate = predicate;
        } else {
            this.wherePredicate = this.wherePredicate.and(predicate);
        }
        return this;
    }

    /**
     * Adds an ORDER BY clause with the specified column and sort order.
     * @param columnName the column name to order by
     * @param sortOrder the sort order (ASC or DESC)
     * @return this query builder for method chaining
     */
    public Query orderBy(String columnName, SortOrder sortOrder) {
        this.orderByClauses.add(new OrderByClause(columnName, sortOrder));
        return this;
    }

    /**
     * Adds an ORDER BY clause with ascending sort order.
     * @param columnName the column name to order by
     * @return this query builder for method chaining
     */
    public Query orderBy(String columnName) {
        return orderBy(columnName, SortOrder.ASC);
    }

    /**
     * Sets the LIMIT clause.
     * @param limit the maximum number of rows to return
     * @return this query builder for method chaining
     */
    public Query limit(int limit) {
        this.limitValue = limit;
        return this;
    }

    /**
     * Sets the OFFSET clause.
     * @param offset the number of rows to skip
     * @return this query builder for method chaining
     */
    public Query offset(int offset) {
        this.offsetValue = offset;
        return this;
    }

    /**
     * Adds an INNER JOIN clause.
     * @param tableName the table to join
     * @param joinCondition the condition for the join
     * @return this query builder for method chaining
     */
    public Query innerJoin(String tableName, Predicate joinCondition) {
        this.joinClauses.add(new JoinClause(JoinType.INNER, tableName, joinCondition));
        return this;
    }

    /**
     * Adds an INNER JOIN clause with table alias.
     * @param tableName the table to join
     * @param tableAlias the alias for the joined table
     * @param joinCondition the condition for the join
     * @return this query builder for method chaining
     */
    public Query innerJoin(String tableName, String tableAlias, Predicate joinCondition) {
        this.joinClauses.add(new JoinClause(JoinType.INNER, tableName, tableAlias, joinCondition));
        return this;
    }

    /**
     * Adds a LEFT JOIN clause.
     * @param tableName the table to join
     * @param joinCondition the condition for the join
     * @return this query builder for method chaining
     */
    public Query leftJoin(String tableName, Predicate joinCondition) {
        this.joinClauses.add(new JoinClause(JoinType.LEFT, tableName, joinCondition));
        return this;
    }

    /**
     * Adds a LEFT JOIN clause with table alias.
     * @param tableName the table to join
     * @param tableAlias the alias for the joined table
     * @param joinCondition the condition for the join
     * @return this query builder for method chaining
     */
    public Query leftJoin(String tableName, String tableAlias, Predicate joinCondition) {
        this.joinClauses.add(new JoinClause(JoinType.LEFT, tableName, tableAlias, joinCondition));
        return this;
    }

    /**
     * Adds a RIGHT JOIN clause.
     * @param tableName the table to join
     * @param joinCondition the condition for the join
     * @return this query builder for method chaining
     */
    public Query rightJoin(String tableName, Predicate joinCondition) {
        this.joinClauses.add(new JoinClause(JoinType.RIGHT, tableName, joinCondition));
        return this;
    }

    /**
     * Adds a RIGHT JOIN clause with table alias.
     * @param tableName the table to join
     * @param tableAlias the alias for the joined table
     * @param joinCondition the condition for the join
     * @return this query builder for method chaining
     */
    public Query rightJoin(String tableName, String tableAlias, Predicate joinCondition) {
        this.joinClauses.add(new JoinClause(JoinType.RIGHT, tableName, tableAlias, joinCondition));
        return this;
    }

    /**
     * Adds a FULL OUTER JOIN clause.
     * @param tableName the table to join
     * @param joinCondition the condition for the join
     * @return this query builder for method chaining
     */
    public Query fullOuterJoin(String tableName, Predicate joinCondition) {
        this.joinClauses.add(new JoinClause(JoinType.FULL_OUTER, tableName, joinCondition));
        return this;
    }

    /**
     * Adds a FULL OUTER JOIN clause with table alias.
     * @param tableName the table to join
     * @param tableAlias the alias for the joined table
     * @param joinCondition the condition for the join
     * @return this query builder for method chaining
     */
    public Query fullOuterJoin(String tableName, String tableAlias, Predicate joinCondition) {
        this.joinClauses.add(new JoinClause(JoinType.FULL_OUTER, tableName, tableAlias, joinCondition));
        return this;
    }

    /**
     * Gets the selected columns.
     * @return the list of selected columns
     */
    public List<String> getSelectColumns() {
        return new ArrayList<>(selectColumns);
    }

    /**
     * Gets the FROM table.
     * @return the table name
     */
    public String getFromTable() {
        return fromTable;
    }

    /**
     * Gets the join clauses.
     * @return the list of join clauses
     */
    public List<JoinClause> getJoinClauses() {
        return new ArrayList<>(joinClauses);
    }

    /**
     * Gets the WHERE predicate.
     * @return the WHERE predicate, or null if none specified
     */
    public Predicate getWherePredicate() {
        return wherePredicate;
    }

    /**
     * Gets the ORDER BY clauses.
     * @return the list of ORDER BY clauses
     */
    public List<OrderByClause> getOrderByClauses() {
        return new ArrayList<>(orderByClauses);
    }

    /**
     * Gets the LIMIT value.
     * @return the LIMIT value, or null if none specified
     */
    public Integer getLimit() {
        return limitValue;
    }

    /**
     * Gets the OFFSET value.
     * @return the OFFSET value, or null if none specified
     */
    public Integer getOffset() {
        return offsetValue;
    }

    /**
     * Represents an ORDER BY clause with column name and sort order.
     */
    public static class OrderByClause {
        private final String columnName;
        private final SortOrder sortOrder;

        public OrderByClause(String columnName, SortOrder sortOrder) {
            this.columnName = Objects.requireNonNull(columnName, "Column name cannot be null");
            this.sortOrder = Objects.requireNonNull(sortOrder, "Sort order cannot be null");
        }

        public String getColumnName() {
            return columnName;
        }

        public SortOrder getSortOrder() {
            return sortOrder;
        }

        @Override
        public String toString() {
            return columnName + " " + sortOrder;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OrderByClause that = (OrderByClause) o;
            return Objects.equals(columnName, that.columnName) && sortOrder == that.sortOrder;
        }

        @Override
        public int hashCode() {
            return Objects.hash(columnName, sortOrder);
        }
    }

    /**
     * Generates the SQL template string with named parameters.
     * This method creates a SQL string compatible with vertx-sql-client-templates
     * using named parameters like #{param1}, #{param2}, etc.
     *
     * @return SQL template string with named parameters
     */
    public String toSqlString() {
        SqlTemplateGenerator generator = new SqlTemplateGenerator();
        return generator.generateSelectTemplate(this);
    }

    /**
     * Gets the parameters map for the generated SQL template.
     * This should be called after toSqlString() to get the parameter values
     * that correspond to the named parameters in the SQL template.
     *
     * @return map of parameter names to values
     */
    public Map<String, Object> getParameters() {
        SqlTemplateGenerator generator = new SqlTemplateGenerator();
        generator.generateSelectTemplate(this); // Generate to collect parameters
        return generator.getParameters();
    }

    /**
     * Creates a SqlTemplate for query execution using vertx-sql-client-templates.
     * This provides a safe way to execute the query with automatic parameter binding
     * and SQL injection protection.
     *
     * @param client the SQL client to use for execution
     * @return SqlTemplate ready for execution
     */
    public SqlTemplate<Map<String, Object>, RowSet<Row>> toSqlTemplate(SqlClient client) {
        String sqlString = toSqlString();
        return SqlTemplate.forQuery(client, sqlString);
    }

    @Override
    public String toString() {
        return "Query{" +
                "selectColumns=" + selectColumns +
                ", fromTable='" + fromTable + '\'' +
                ", wherePredicate=" + wherePredicate +
                ", orderByClauses=" + orderByClauses +
                ", limitValue=" + limitValue +
                ", offsetValue=" + offsetValue +
                '}';
    }
}
