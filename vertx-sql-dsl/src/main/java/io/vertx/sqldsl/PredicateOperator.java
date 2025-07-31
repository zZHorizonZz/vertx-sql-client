package io.vertx.sqldsl;

/**
 * Enumeration of predicate operators used in SQL WHERE clauses.
 */
public enum PredicateOperator {

    /** Equality operator (=) */
    EQ("="),

    /** Not equal operator (!=) */
    NE("!="),

    /** Greater than operator (>) */
    GT(">"),

    /** Greater than or equal operator (>=) */
    GTE(">="),

    /** Less than operator (<) */
    LT("<"),

    /** Less than or equal operator (<=) */
    LTE("<="),

    /** LIKE operator for pattern matching */
    LIKE("LIKE"),

    /** NOT LIKE operator for pattern matching */
    NOT_LIKE("NOT LIKE"),

    /** IN operator for multiple values */
    IN("IN"),

    /** NOT IN operator for multiple values */
    NOT_IN("NOT IN"),

    /** IS NULL operator */
    IS_NULL("IS NULL"),

    /** IS NOT NULL operator */
    IS_NOT_NULL("IS NOT NULL"),

    /** BETWEEN operator for range queries */
    BETWEEN("BETWEEN"),

    /** NOT BETWEEN operator for range queries */
    NOT_BETWEEN("NOT BETWEEN");

    private final String sqlOperator;

    PredicateOperator(String sqlOperator) {
        this.sqlOperator = sqlOperator;
    }

    /**
     * Gets the SQL representation of this operator.
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
