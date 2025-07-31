package io.vertx.sqldsl;

import java.util.Objects;

/**
 * Represents a simple predicate with a column, operator, and value.
 * Examples: column = value, column > value, column IN (values), etc.
 */
public class SimplePredicate implements Predicate {

    private final String columnName;
    private final PredicateOperator operator;
    private final Object value;

    /**
     * Creates a new simple predicate.
     * @param columnName the column name
     * @param operator the predicate operator
     * @param value the value to compare with (can be null for IS NULL/IS NOT NULL)
     */
    public SimplePredicate(String columnName, PredicateOperator operator, Object value) {
        this.columnName = Objects.requireNonNull(columnName, "Column name cannot be null");
        this.operator = Objects.requireNonNull(operator, "Operator cannot be null");
        this.value = value;
    }

    /**
     * Gets the column name.
     * @return the column name
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * Gets the predicate operator.
     * @return the operator
     */
    public PredicateOperator getOperator() {
        return operator;
    }

    /**
     * Gets the value.
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    @Override
    public <T> T accept(PredicateVisitor<T> visitor) {
        return visitor.visitSimple(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimplePredicate that = (SimplePredicate) o;
        return Objects.equals(columnName, that.columnName) &&
                operator == that.operator &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(columnName, operator, value);
    }

    @Override
    public String toString() {
        return "SimplePredicate{" +
                "columnName='" + columnName + '\'' +
                ", operator=" + operator +
                ", value=" + value +
                '}';
    }
}
