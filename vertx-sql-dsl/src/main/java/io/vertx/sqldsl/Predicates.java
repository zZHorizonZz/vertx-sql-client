package io.vertx.sqldsl;

/**
 * Utility class for creating and combining predicates.
 * Provides static methods for common predicate operations.
 */
public final class Predicates {

    private Predicates() {
        // Utility class - prevent instantiation
    }

    /**
     * Creates a predicate that negates the given predicate.
     * @param predicate the predicate to negate
     * @return a NOT predicate
     */
    public static Predicate not(Predicate predicate) {
        return predicate.not();
    }

    /**
     * Combines multiple predicates with AND logic.
     * @param predicates the predicates to combine
     * @return a composite predicate representing AND operation
     * @throws IllegalArgumentException if no predicates are provided
     */
    public static Predicate and(Predicate... predicates) {
        if (predicates == null || predicates.length == 0) {
            throw new IllegalArgumentException("At least one predicate is required");
        }

        Predicate result = predicates[0];
        for (int i = 1; i < predicates.length; i++) {
            result = result.and(predicates[i]);
        }
        return result;
    }

    /**
     * Combines multiple predicates with OR logic.
     * @param predicates the predicates to combine
     * @return a composite predicate representing OR operation
     * @throws IllegalArgumentException if no predicates are provided
     */
    public static Predicate or(Predicate... predicates) {
        if (predicates == null || predicates.length == 0) {
            throw new IllegalArgumentException("At least one predicate is required");
        }

        Predicate result = predicates[0];
        for (int i = 1; i < predicates.length; i++) {
            result = result.or(predicates[i]);
        }
        return result;
    }

    /**
     * Creates a predicate that is always true.
     * This can be useful as a starting point for dynamic query building.
     * @return a predicate that always evaluates to true
     */
    public static Predicate alwaysTrue() {
        return new SimplePredicate("1", PredicateOperator.EQ, 1);
    }

    /**
     * Creates a predicate that is always false.
     * This can be useful as a starting point for dynamic query building.
     * @return a predicate that always evaluates to false
     */
    public static Predicate alwaysFalse() {
        return new SimplePredicate("1", PredicateOperator.EQ, 0);
    }

    /**
     * Creates a conditional predicate that applies the given predicate only if the condition is true.
     * If the condition is false, returns alwaysTrue().
     * @param condition the condition to check
     * @param predicate the predicate to apply if condition is true
     * @return the predicate if condition is true, otherwise alwaysTrue()
     */
    public static Predicate when(boolean condition, Predicate predicate) {
        return condition ? predicate : alwaysTrue();
    }

    /**
     * Creates a conditional predicate with an else clause.
     * @param condition the condition to check
     * @param truePredicate the predicate to apply if condition is true
     * @param falsePredicate the predicate to apply if condition is false
     * @return truePredicate if condition is true, otherwise falsePredicate
     */
    public static Predicate when(boolean condition, Predicate truePredicate, Predicate falsePredicate) {
        return condition ? truePredicate : falsePredicate;
    }
}
