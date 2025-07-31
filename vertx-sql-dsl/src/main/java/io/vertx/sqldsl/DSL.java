package io.vertx.sqldsl;

/**
 * Main entry point for the SQL DSL.
 * Provides factory methods for creating queries and other DSL components.
 */
public final class DSL {

    private DSL() {
        // Utility class - prevent instantiation
    }

    /**
     * Creates a new query builder.
     * This is the main entry point for building SQL queries using the DSL.
     *
     * Example usage:
     * <pre>
     * Query query = query()
     *     .select("id", "username", "email")
     *     .from("users")
     *     .where(User_.email.endsWith("@gmail.com"))
     *     .orderBy("username", SortOrder.ASC)
     *     .limit(10);
     * </pre>
     *
     * @return a new Query builder instance
     */
    public static Query query() {
        return new Query();
    }

    /**
     * Creates a new query builder with the specified table.
     * This is a convenience method that combines query() and from().
     *
     * @param tableName the table name to select from
     * @return a new Query builder instance with the FROM clause set
     */
    public static Query from(String tableName) {
        return new Query().from(tableName);
    }

    /**
     * Creates a new query builder with the specified columns to select.
     * This is a convenience method that combines query() and select().
     *
     * @param columns the column names to select
     * @return a new Query builder instance with the SELECT clause set
     */
    public static Query select(String... columns) {
        return new Query().select(columns);
    }

    /**
     * Creates a new string property for the specified column.
     * This is useful for creating ad-hoc property references.
     *
     * @param columnName the column name
     * @return a new StringProperty instance
     */
    public static StringProperty stringProperty(String columnName) {
        return new StringProperty(columnName);
    }

    /**
     * Creates a new comparable property for the specified column and type.
     * This is useful for creating ad-hoc property references.
     *
     * @param columnName the column name
     * @param type the property type
     * @param <T> the type parameter
     * @return a new ComparableProperty instance
     */
    public static <T extends Comparable<T>> ComparableProperty<T> comparableProperty(String columnName, Class<T> type) {
        return new ComparableProperty<>(columnName, type);
    }

    /**
     * Creates a new property for the specified column and type.
     * This is useful for creating ad-hoc property references.
     *
     * @param columnName the column name
     * @param type the property type
     * @param <T> the type parameter
     * @return a new Property instance
     */
    public static <T> Property<T> property(String columnName, Class<T> type) {
        return new Property<>(columnName, type);
    }
}
