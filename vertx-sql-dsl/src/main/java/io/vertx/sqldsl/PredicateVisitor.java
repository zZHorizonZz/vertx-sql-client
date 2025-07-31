package io.vertx.sqldsl;

/**
 * Visitor interface for processing different types of predicates.
 * This follows the Visitor pattern to allow different operations on predicates
 * without modifying the predicate classes themselves.
 *
 * @param <T> the return type of the visitor methods
 */
public interface PredicateVisitor<T> {

    /**
     * Visits a simple predicate (column operator value).
     * @param predicate the simple predicate to visit
     * @return the result of processing the simple predicate
     */
    T visitSimple(SimplePredicate predicate);

    /**
     * Visits a composite predicate (predicate1 AND/OR predicate2).
     * @param predicate the composite predicate to visit
     * @return the result of processing the composite predicate
     */
    T visitComposite(CompositePredicate predicate);

    /**
     * Visits a NOT predicate (NOT predicate).
     * @param predicate the NOT predicate to visit
     * @return the result of processing the NOT predicate
     */
    T visitNot(NotPredicate predicate);

    /**
     * Visits a BETWEEN predicate (column BETWEEN start AND end).
     * @param predicate the BETWEEN predicate to visit
     * @return the result of processing the BETWEEN predicate
     */
    T visitBetween(BetweenPredicate predicate);
}
