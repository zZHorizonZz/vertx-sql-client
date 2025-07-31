package io.vertx.sqldsl;

import java.util.Objects;

/**
 * Represents a BETWEEN predicate for range queries.
 * Examples: column BETWEEN start AND end, column NOT BETWEEN start AND end
 */
public class BetweenPredicate implements Predicate {

    private final String columnName;
    private final Object startValue;
    private final Object endValue;
    private final boolean negated;

    /**
     * Creates a new BETWEEN predicate.
     * @param columnName the column name
     * @param startValue the start value of the range (inclusive)
     * @param endValue the end value of the range (inclusive)
     */
    public BetweenPredicate(String columnName, Object startValue, Object endValue) {
        this(columnName, startValue, endValue, false);
    }

    /**
     * Creates a new BETWEEN predicate.
     * @param columnName the column name
     * @param startValue the start value of the range (inclusive)
     * @param endValue the end value of the range (inclusive)
     * @param negated true for NOT BETWEEN, false for BETWEEN
     */
    public BetweenPredicate(String columnName, Object startValue, Object endValue, boolean negated) {
        this.columnName = Objects.requireNonNull(columnName, "Column name cannot be null");
        this.startValue = Objects.requireNonNull(startValue, "Start value cannot be null");
        this.endValue = Objects.requireNonNull(endValue, "End value cannot be null");
        this.negated = negated;
    }

    /**
     * Gets the column name.
     * @return the column name
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * Gets the start value of the range.
     * @return the start value
     */
    public Object getStartValue() {
        return startValue;
    }

    /**
     * Gets the end value of the range.
     * @return the end value
     */
    public Object getEndValue() {
        return endValue;
    }

    /**
     * Checks if this is a NOT BETWEEN predicate.
     * @return true if negated, false otherwise
     */
    public boolean isNegated() {
        return negated;
    }

    @Override
    public <T> T accept(PredicateVisitor<T> visitor) {
        return visitor.visitBetween(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BetweenPredicate that = (BetweenPredicate) o;
        return negated == that.negated &&
                Objects.equals(columnName, that.columnName) &&
                Objects.equals(startValue, that.startValue) &&
                Objects.equals(endValue, that.endValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(columnName, startValue, endValue, negated);
    }

    @Override
    public String toString() {
        return "BetweenPredicate{" +
                "columnName='" + columnName + '\'' +
                ", startValue=" + startValue +
                ", endValue=" + endValue +
                ", negated=" + negated +
                '}';
    }
}
