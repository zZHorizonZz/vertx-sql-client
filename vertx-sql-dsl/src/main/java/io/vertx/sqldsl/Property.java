package io.vertx.sqldsl;

import java.util.Arrays;
import java.util.Collection;

/**
 * Base class for all properties with basic operations.
 * Provides type-safe property references and basic comparison operations.
 *
 * @param <T> the type of the property value
 */
public class Property<T> {

    protected final String columnName;
    protected final Class<T> type;

    public Property(String columnName, Class<T> type) {
        this.columnName = columnName;
        this.type = type;
    }

    /**
     * Gets the column name for this property.
     * @return the column name
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * Gets the type of this property.
     * @return the property type
     */
    public Class<T> getType() {
        return type;
    }

    /**
     * Creates an equality predicate.
     * @param value the value to compare with
     * @return a predicate representing equality
     */
    public Predicate eq(T value) {
        return new SimplePredicate(columnName, PredicateOperator.EQ, value);
    }

    /**
     * Creates a not-equal predicate.
     * @param value the value to compare with
     * @return a predicate representing inequality
     */
    public Predicate ne(T value) {
        return new SimplePredicate(columnName, PredicateOperator.NE, value);
    }

    /**
     * Creates an IS NULL predicate.
     * @return a predicate representing null check
     */
    public Predicate isNull() {
        return new SimplePredicate(columnName, PredicateOperator.IS_NULL, null);
    }

    /**
     * Creates an IS NOT NULL predicate.
     * @return a predicate representing not null check
     */
    public Predicate isNotNull() {
        return new SimplePredicate(columnName, PredicateOperator.IS_NOT_NULL, null);
    }

    /**
     * Creates an IN predicate with multiple values.
     * @param values the values to check against
     * @return a predicate representing IN operation
     */
    @SafeVarargs
    public final Predicate in(T... values) {
        return new SimplePredicate(columnName, PredicateOperator.IN, Arrays.asList(values));
    }

    /**
     * Creates an IN predicate with a collection of values.
     * @param values the collection of values to check against
     * @return a predicate representing IN operation
     */
    public Predicate in(Collection<T> values) {
        return new SimplePredicate(columnName, PredicateOperator.IN, values);
    }

    /**
     * Creates a NOT IN predicate with multiple values.
     * @param values the values to check against
     * @return a predicate representing NOT IN operation
     */
    @SafeVarargs
    public final Predicate notIn(T... values) {
        return new SimplePredicate(columnName, PredicateOperator.NOT_IN, Arrays.asList(values));
    }

    /**
     * Creates a NOT IN predicate with a collection of values.
     * @param values the collection of values to check against
     * @return a predicate representing NOT IN operation
     */
    public Predicate notIn(Collection<T> values) {
        return new SimplePredicate(columnName, PredicateOperator.NOT_IN, values);
    }

    @Override
    public String toString() {
        return "Property{" +
                "columnName='" + columnName + '\'' +
                ", type=" + type.getSimpleName() +
                '}';
    }
}
