package io.vertx.sqldsl;

import java.util.Objects;

/**
 * Represents a case-insensitive LIKE predicate.
 * This extends SimplePredicate with a special marker for case-insensitive operations.
 * This typically translates to UPPER(column) LIKE UPPER(pattern) in SQL.
 */
public class CaseInsensitiveLikePredicate extends SimplePredicate {

    /**
     * Creates a new case-insensitive LIKE predicate.
     * @param columnName the column name
     * @param pattern the pattern to match (case-insensitive)
     */
    public CaseInsensitiveLikePredicate(String columnName, String pattern) {
        super(columnName, PredicateOperator.LIKE, pattern);
    }

    /**
     * Gets the pattern.
     * @return the pattern
     */
    public String getPattern() {
        return (String) getValue();
    }

    /**
     * Indicates this is a case-insensitive LIKE operation.
     * @return true always
     */
    public boolean isCaseInsensitive() {
        return true;
    }

    @Override
    public String toString() {
        return "CaseInsensitiveLikePredicate{" +
                "columnName='" + getColumnName() + '\'' +
                ", pattern='" + getPattern() + '\'' +
                '}';
    }
}
