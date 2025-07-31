package io.vertx.sqldsl;

/**
 * Enumeration representing sort order for ORDER BY clauses.
 */
public enum SortOrder {

    /** Ascending sort order */
    ASC("ASC"),

    /** Descending sort order */
    DESC("DESC");

    private final String sqlKeyword;

    SortOrder(String sqlKeyword) {
        this.sqlKeyword = sqlKeyword;
    }

    /**
     * Gets the SQL keyword for this sort order.
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
