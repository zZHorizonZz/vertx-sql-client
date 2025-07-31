package io.vertx.sqldsl;

/**
 * Property class for String types that supports string-specific operations.
 * Extends the base Property class with LIKE operations and pattern matching.
 */
public class StringProperty extends Property<String> {

    public StringProperty(String columnName) {
        super(columnName, String.class);
    }

    /**
     * Creates a LIKE predicate for pattern matching.
     * @param pattern the pattern to match (can include % and _ wildcards)
     * @return a predicate representing LIKE operation
     */
    public Predicate like(String pattern) {
        return new SimplePredicate(columnName, PredicateOperator.LIKE, pattern);
    }

    /**
     * Creates a NOT LIKE predicate for pattern matching.
     * @param pattern the pattern to match (can include % and _ wildcards)
     * @return a predicate representing NOT LIKE operation
     */
    public Predicate notLike(String pattern) {
        return new SimplePredicate(columnName, PredicateOperator.NOT_LIKE, pattern);
    }

    /**
     * Creates a LIKE predicate that matches strings containing the specified substring.
     * @param substring the substring to search for
     * @return a predicate representing LIKE '%substring%' operation
     */
    public Predicate contains(String substring) {
        return like("%" + escapeForLike(substring) + "%");
    }

    /**
     * Creates a LIKE predicate that matches strings starting with the specified prefix.
     * @param prefix the prefix to search for
     * @return a predicate representing LIKE 'prefix%' operation
     */
    public Predicate startsWith(String prefix) {
        return like(escapeForLike(prefix) + "%");
    }

    /**
     * Creates a LIKE predicate that matches strings ending with the specified suffix.
     * @param suffix the suffix to search for
     * @return a predicate representing LIKE '%suffix' operation
     */
    public Predicate endsWith(String suffix) {
        return like("%" + escapeForLike(suffix));
    }

    /**
     * Creates a case-insensitive LIKE predicate for pattern matching.
     * Note: This uses UPPER() function which may not be portable across all databases.
     * @param pattern the pattern to match (can include % and _ wildcards)
     * @return a predicate representing case-insensitive LIKE operation
     */
    public Predicate ilike(String pattern) {
        // This creates a case-insensitive LIKE using UPPER function
        // In a real implementation, you might want to use database-specific functions
        return new CaseInsensitiveLikePredicate(columnName, pattern);
    }

    /**
     * Creates a case-insensitive contains predicate.
     * @param substring the substring to search for (case-insensitive)
     * @return a predicate representing case-insensitive contains operation
     */
    public Predicate containsIgnoreCase(String substring) {
        return ilike("%" + escapeForLike(substring) + "%");
    }

    /**
     * Escapes special characters in LIKE patterns.
     * @param value the value to escape
     * @return the escaped value
     */
    private String escapeForLike(String value) {
        if (value == null) {
            return null;
        }
        // Escape % and _ characters that have special meaning in LIKE patterns
        return value.replace("\\", "\\\\")
                   .replace("%", "\\%")
                   .replace("_", "\\_");
    }

    @Override
    public String toString() {
        return "StringProperty{" +
                "columnName='" + columnName + '\'' +
                '}';
    }
}
