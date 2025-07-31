package io.vertx.sqldsl;

import java.util.Objects;

/**
 * Represents a JOIN clause in a SQL query, including the join type,
 * target table, and join condition.
 */
public class JoinClause {

    private final JoinType joinType;
    private final String tableName;
    private final String tableAlias;
    private final Predicate joinCondition;

    /**
     * Creates a new join clause.
     *
     * @param joinType the type of join (INNER, LEFT, RIGHT, FULL_OUTER)
     * @param tableName the name of the table to join
     * @param tableAlias optional alias for the joined table (can be null)
     * @param joinCondition the condition for the join (ON clause)
     */
    public JoinClause(JoinType joinType, String tableName, String tableAlias, Predicate joinCondition) {
        this.joinType = Objects.requireNonNull(joinType, "joinType cannot be null");
        this.tableName = Objects.requireNonNull(tableName, "tableName cannot be null");
        this.tableAlias = tableAlias;
        this.joinCondition = Objects.requireNonNull(joinCondition, "joinCondition cannot be null");
    }

    /**
     * Creates a new join clause without a table alias.
     *
     * @param joinType the type of join (INNER, LEFT, RIGHT, FULL_OUTER)
     * @param tableName the name of the table to join
     * @param joinCondition the condition for the join (ON clause)
     */
    public JoinClause(JoinType joinType, String tableName, Predicate joinCondition) {
        this(joinType, tableName, null, joinCondition);
    }

    /**
     * Gets the join type.
     * @return the join type
     */
    public JoinType getJoinType() {
        return joinType;
    }

    /**
     * Gets the table name to join.
     * @return the table name
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Gets the table alias, if any.
     * @return the table alias, or null if no alias is specified
     */
    public String getTableAlias() {
        return tableAlias;
    }

    /**
     * Gets the join condition.
     * @return the join condition predicate
     */
    public Predicate getJoinCondition() {
        return joinCondition;
    }

    /**
     * Gets the effective table reference (alias if present, otherwise table name).
     * @return the table reference to use in SQL
     */
    public String getTableReference() {
        return tableAlias != null ? tableAlias : tableName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(joinType.getSqlKeyword()).append(" ");
        sb.append(tableName);
        if (tableAlias != null) {
            sb.append(" AS ").append(tableAlias);
        }
        sb.append(" ON ").append(joinCondition);
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JoinClause that = (JoinClause) o;
        return joinType == that.joinType &&
               Objects.equals(tableName, that.tableName) &&
               Objects.equals(tableAlias, that.tableAlias) &&
               Objects.equals(joinCondition, that.joinCondition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(joinType, tableName, tableAlias, joinCondition);
    }
}
