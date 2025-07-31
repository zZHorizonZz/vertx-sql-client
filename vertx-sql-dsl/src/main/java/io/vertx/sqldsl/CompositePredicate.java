package io.vertx.sqldsl;

import java.util.Objects;

/**
 * Represents a composite predicate that combines two predicates with a logical operator (AND/OR).
 * Examples: (predicate1 AND predicate2), (predicate1 OR predicate2)
 */
public class CompositePredicate implements Predicate {

    private final Predicate left;
    private final LogicalOperator operator;
    private final Predicate right;

    /**
     * Creates a new composite predicate.
     * @param left the left predicate
     * @param operator the logical operator (AND/OR)
     * @param right the right predicate
     */
    public CompositePredicate(Predicate left, LogicalOperator operator, Predicate right) {
        this.left = Objects.requireNonNull(left, "Left predicate cannot be null");
        this.operator = Objects.requireNonNull(operator, "Logical operator cannot be null");
        this.right = Objects.requireNonNull(right, "Right predicate cannot be null");
    }

    /**
     * Gets the left predicate.
     * @return the left predicate
     */
    public Predicate getLeft() {
        return left;
    }

    /**
     * Gets the logical operator.
     * @return the logical operator
     */
    public LogicalOperator getOperator() {
        return operator;
    }

    /**
     * Gets the right predicate.
     * @return the right predicate
     */
    public Predicate getRight() {
        return right;
    }

    @Override
    public <T> T accept(PredicateVisitor<T> visitor) {
        return visitor.visitComposite(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompositePredicate that = (CompositePredicate) o;
        return Objects.equals(left, that.left) &&
                operator == that.operator &&
                Objects.equals(right, that.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, operator, right);
    }

    @Override
    public String toString() {
        return "CompositePredicate{" +
                "left=" + left +
                ", operator=" + operator +
                ", right=" + right +
                '}';
    }
}
