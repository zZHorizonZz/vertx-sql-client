package io.vertx.sqldsl;

import java.util.Objects;

/**
 * Represents a NOT predicate that negates another predicate.
 * Examples: NOT (column = value), NOT (predicate1 AND predicate2)
 */
public class NotPredicate implements Predicate {

    private final Predicate predicate;

    /**
     * Creates a new NOT predicate.
     * @param predicate the predicate to negate
     */
    public NotPredicate(Predicate predicate) {
        this.predicate = Objects.requireNonNull(predicate, "Predicate cannot be null");
    }

    /**
     * Gets the predicate being negated.
     * @return the predicate
     */
    public Predicate getPredicate() {
        return predicate;
    }

    @Override
    public <T> T accept(PredicateVisitor<T> visitor) {
        return visitor.visitNot(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotPredicate that = (NotPredicate) o;
        return Objects.equals(predicate, that.predicate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(predicate);
    }

    @Override
    public String toString() {
        return "NotPredicate{" +
                "predicate=" + predicate +
                '}';
    }
}
