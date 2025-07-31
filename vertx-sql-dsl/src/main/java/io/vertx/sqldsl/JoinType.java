package io.vertx.sqldsl;

/**
 * Enumeration of SQL join types supported by the query DSL.
 */
public enum JoinType {
    /**
     * INNER JOIN - returns only rows that have matching values in both tables.
     */
    INNER("INNER JOIN"),

    /**
     * LEFT JOIN (LEFT OUTER JOIN) - returns all rows from the left table,
     * and matching rows from the right table. NULL values are returned
     * for non-matching rows from the right table.
     */
    LEFT("LEFT JOIN"),

    /**
     * RIGHT JOIN (RIGHT OUTER JOIN) - returns all rows from the right table,
     * and matching rows from the left table. NULL values are returned
     * for non-matching rows from the left table.
     */
    RIGHT("RIGHT JOIN"),

    /**
     * FULL OUTER JOIN - returns all rows when there is a match in either
     * left or right table. NULL values are returned for non-matching rows
     * from both tables.
     */
    FULL_OUTER("FULL OUTER JOIN");

    private final String sqlKeyword;

    JoinType(String sqlKeyword) {
        this.sqlKeyword = sqlKeyword;
    }

    /**
     * Gets the SQL keyword for this join type.
     * @return the SQL keyword
     */
    public String getSqlKeyword() {
        return sqlKeyword;
    }

    @Override
    public String toString() {
        return sqlKeyword;
    }
}
