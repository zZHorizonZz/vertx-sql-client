package io.vertx.sqldsl;

/**
 * Interface representing a predicate that can be used in SQL WHERE clauses.
 * Predicates can be combined using logical operators (AND, OR, NOT).
 */
public interface Predicate {

    /**
     * Combines this predicate with another using AND logic.
     * @param other the other predicate
     * @return a composite predicate representing AND operation
     */
    default Predicate and(Predicate other) {
        return new CompositePredicate(this, LogicalOperator.AND, other);
    }

    /**
     * Combines this predicate with another using OR logic.
     * @param other the other predicate
     * @return a composite predicate representing OR operation
     */
    default Predicate or(Predicate other) {
        return new CompositePredicate(this, LogicalOperator.OR, other);
    }

    /**
     * Negates this predicate using NOT logic.
     * @return a negated predicate
     */
    default Predicate not() {
        return new NotPredicate(this);
    }

    /**
     * Accepts a visitor for processing this predicate.
     * @param visitor the visitor to accept
     * @param <T> the return type of the visitor
     * @return the result of the visitor processing
     */
    <T> T accept(PredicateVisitor<T> visitor);
}
