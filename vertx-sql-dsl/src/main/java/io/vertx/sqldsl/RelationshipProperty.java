package io.vertx.sqldsl;

/**
 * Property class for handling relationships between entities.
 * Provides join functionality for @ManyToOne and @OneToMany relationships.
 *
 * @param <T> the type of the related entity
 */
public class RelationshipProperty<T> extends Property<T> {

    private final String targetTableName;
    private final String joinColumn;
    private final String referencedColumn;
    private final RelationshipType relationshipType;

    /**
     * Creates a new relationship property.
     *
     * @param columnName the column name (for ManyToOne, this is the foreign key column)
     * @param type the type of the related entity
     * @param targetTableName the name of the target table
     * @param joinColumn the join column name
     * @param referencedColumn the referenced column name in the target table
     * @param relationshipType the type of relationship (MANY_TO_ONE or ONE_TO_MANY)
     */
    public RelationshipProperty(String columnName, Class<T> type, String targetTableName,
                               String joinColumn, String referencedColumn, RelationshipType relationshipType) {
        super(columnName, type);
        this.targetTableName = targetTableName;
        this.joinColumn = joinColumn;
        this.referencedColumn = referencedColumn;
        this.relationshipType = relationshipType;
    }

    /**
     * Gets the target table name.
     * @return the target table name
     */
    public String getTargetTableName() {
        return targetTableName;
    }

    /**
     * Gets the join column name.
     * @return the join column name
     */
    public String getJoinColumn() {
        return joinColumn;
    }

    /**
     * Gets the referenced column name.
     * @return the referenced column name
     */
    public String getReferencedColumn() {
        return referencedColumn;
    }

    /**
     * Gets the relationship type.
     * @return the relationship type
     */
    public RelationshipType getRelationshipType() {
        return relationshipType;
    }

    /**
     * Creates an INNER JOIN clause for this relationship.
     * @return a JoinClause for inner join
     */
    public JoinClause innerJoin() {
        return createJoinClause(JoinType.INNER);
    }

    /**
     * Creates a LEFT JOIN clause for this relationship.
     * @return a JoinClause for left join
     */
    public JoinClause leftJoin() {
        return createJoinClause(JoinType.LEFT);
    }

    /**
     * Creates a RIGHT JOIN clause for this relationship.
     * @return a JoinClause for right join
     */
    public JoinClause rightJoin() {
        return createJoinClause(JoinType.RIGHT);
    }

    /**
     * Creates a FULL OUTER JOIN clause for this relationship.
     * @return a JoinClause for full outer join
     */
    public JoinClause fullOuterJoin() {
        return createJoinClause(JoinType.FULL_OUTER);
    }

    /**
     * Creates an INNER JOIN clause with a custom alias for this relationship.
     * @param alias the alias for the joined table
     * @return a JoinClause for inner join with alias
     */
    public JoinClause innerJoin(String alias) {
        return createJoinClause(JoinType.INNER, alias);
    }

    /**
     * Creates a LEFT JOIN clause with a custom alias for this relationship.
     * @param alias the alias for the joined table
     * @return a JoinClause for left join with alias
     */
    public JoinClause leftJoin(String alias) {
        return createJoinClause(JoinType.LEFT, alias);
    }

    /**
     * Creates a RIGHT JOIN clause with a custom alias for this relationship.
     * @param alias the alias for the joined table
     * @return a JoinClause for right join with alias
     */
    public JoinClause rightJoin(String alias) {
        return createJoinClause(JoinType.RIGHT, alias);
    }

    /**
     * Creates a FULL OUTER JOIN clause with a custom alias for this relationship.
     * @param alias the alias for the joined table
     * @return a JoinClause for full outer join with alias
     */
    public JoinClause fullOuterJoin(String alias) {
        return createJoinClause(JoinType.FULL_OUTER, alias);
    }

    private JoinClause createJoinClause(JoinType joinType) {
        return createJoinClause(joinType, null);
    }

    private JoinClause createJoinClause(JoinType joinType, String alias) {
        // Create the join condition based on relationship type
        Predicate joinCondition;
        if (relationshipType == RelationshipType.MANY_TO_ONE) {
            // For ManyToOne: current_table.foreign_key = target_table.primary_key
            joinCondition = new SimplePredicate(joinColumn, PredicateOperator.EQ,
                (alias != null ? alias : targetTableName) + "." + referencedColumn);
        } else {
            // For OneToMany: current_table.primary_key = target_table.foreign_key
            joinCondition = new SimplePredicate(referencedColumn, PredicateOperator.EQ,
                (alias != null ? alias : targetTableName) + "." + joinColumn);
        }

        return new JoinClause(joinType, targetTableName, alias, joinCondition);
    }

    @Override
    public String toString() {
        return "RelationshipProperty{" +
                "columnName='" + columnName + '\'' +
                ", type=" + type.getSimpleName() +
                ", targetTableName='" + targetTableName + '\'' +
                ", joinColumn='" + joinColumn + '\'' +
                ", referencedColumn='" + referencedColumn + '\'' +
                ", relationshipType=" + relationshipType +
                '}';
    }

    /**
     * Enumeration of relationship types.
     */
    public enum RelationshipType {
        MANY_TO_ONE,
        ONE_TO_MANY
    }
}
