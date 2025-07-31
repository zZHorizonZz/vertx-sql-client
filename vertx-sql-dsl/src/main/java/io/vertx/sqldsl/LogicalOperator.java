package io.vertx.sqldsl;

/**
 * Enumeration of logical operators used to combine predicates.
 */
public enum LogicalOperator {

    /** AND logical operator */
    AND("AND"),

    /** OR logical operator */
    OR("OR");

    private final String sqlOperator;

    LogicalOperator(String sqlOperator) {
        this.sqlOperator = sqlOperator;
    }

    /**
     * Gets the SQL representation of this logical operator.
     * @return the SQL operator string
     */
    public String getSqlOperator() {
        return sqlOperator;
    }

    @Override
    public String toString() {
        return sqlOperator;
    }
}
