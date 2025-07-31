package io.vertx.sqldsl;

/**
 * Property class for comparable types that supports additional comparison operations.
 * Extends the base Property class with greater than, less than, and between operations.
 *
 * @param <T> the type of the property value, must be Comparable
 */
public class ComparableProperty<T extends Comparable<T>> extends Property<T> {

    public ComparableProperty(String columnName, Class<T> type) {
        super(columnName, type);
    }

    /**
     * Creates a greater than predicate.
     * @param value the value to compare with
     * @return a predicate representing greater than operation
     */
    public Predicate gt(T value) {
        return new SimplePredicate(columnName, PredicateOperator.GT, value);
    }

    /**
     * Creates a greater than or equal predicate.
     * @param value the value to compare with
     * @return a predicate representing greater than or equal operation
     */
    public Predicate gte(T value) {
        return new SimplePredicate(columnName, PredicateOperator.GTE, value);
    }

    /**
     * Creates a less than predicate.
     * @param value the value to compare with
     * @return a predicate representing less than operation
     */
    public Predicate lt(T value) {
        return new SimplePredicate(columnName, PredicateOperator.LT, value);
    }

    /**
     * Creates a less than or equal predicate.
     * @param value the value to compare with
     * @return a predicate representing less than or equal operation
     */
    public Predicate lte(T value) {
        return new SimplePredicate(columnName, PredicateOperator.LTE, value);
    }

    /**
     * Creates a BETWEEN predicate for range queries.
     * @param start the start value of the range (inclusive)
     * @param end the end value of the range (inclusive)
     * @return a predicate representing BETWEEN operation
     */
    public Predicate between(T start, T end) {
        return new BetweenPredicate(columnName, start, end);
    }

    /**
     * Creates a NOT BETWEEN predicate for range queries.
     * @param start the start value of the range (inclusive)
     * @param end the end value of the range (inclusive)
     * @return a predicate representing NOT BETWEEN operation
     */
    public Predicate notBetween(T start, T end) {
        return new BetweenPredicate(columnName, start, end, true);
    }

    @Override
    public String toString() {
        return "ComparableProperty{" +
                "columnName='" + columnName + '\'' +
                ", type=" + type.getSimpleName() +
                '}';
    }
}
