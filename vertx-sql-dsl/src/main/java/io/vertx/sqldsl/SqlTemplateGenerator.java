package io.vertx.sqldsl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Visitor implementation that generates SQL template strings with named parameters
 * from DSL predicates and queries. The generated templates are compatible with
 * the vertx-sql-client-templates module for safe parameter binding.
 */
public class SqlTemplateGenerator implements PredicateVisitor<String> {

    private final Map<String, Object> parameters = new HashMap<>();
    private final AtomicInteger paramCounter = new AtomicInteger(1);

    /**
     * Gets the collected parameters for the generated SQL template.
     * @return map of parameter names to values
     */
    public Map<String, Object> getParameters() {
        return new HashMap<>(parameters);
    }

    /**
     * Clears all collected parameters and resets the parameter counter.
     */
    public void reset() {
        parameters.clear();
        paramCounter.set(1);
    }

    @Override
    public String visitSimple(SimplePredicate predicate) {
        String paramName = "param" + paramCounter.getAndIncrement();
        parameters.put(paramName, predicate.getValue());

        // Handle case-insensitive LIKE operations
        if (predicate instanceof CaseInsensitiveLikePredicate) {
            return "UPPER(" + predicate.getColumnName() + ") " +
                   predicate.getOperator().getSqlOperator() + " " +
                   "UPPER(#{" + paramName + "})";
        }

        return predicate.getColumnName() + " " +
               predicate.getOperator().getSqlOperator() + " " +
               "#{" + paramName + "}";
    }

    @Override
    public String visitComposite(CompositePredicate predicate) {
        String leftSql = predicate.getLeft().accept(this);
        String rightSql = predicate.getRight().accept(this);

        return "(" + leftSql + " " +
               predicate.getOperator().getSqlOperator() + " " +
               rightSql + ")";
    }

    @Override
    public String visitNot(NotPredicate predicate) {
        String innerSql = predicate.getPredicate().accept(this);
        return "NOT (" + innerSql + ")";
    }

    @Override
    public String visitBetween(BetweenPredicate predicate) {
        String startParamName = "param" + paramCounter.getAndIncrement();
        String endParamName = "param" + paramCounter.getAndIncrement();

        parameters.put(startParamName, predicate.getStartValue());
        parameters.put(endParamName, predicate.getEndValue());

        String sql = predicate.getColumnName() + " ";
        if (predicate.isNegated()) {
            sql += "NOT ";
        }
        sql += "BETWEEN #{" + startParamName + "} AND #{" + endParamName + "}";

        return sql;
    }

    /**
     * Generates a complete SQL SELECT template from a Query object.
     * @param query the query to convert
     * @return SQL template string with named parameters
     */
    public String generateSelectTemplate(Query query) {
        reset();
        StringBuilder sql = new StringBuilder();

        // SELECT clause
        sql.append("SELECT ");
        if (query.getSelectColumns().isEmpty()) {
            sql.append("*");
        } else {
            sql.append(String.join(", ", query.getSelectColumns()));
        }

        // FROM clause
        if (query.getFromTable() != null) {
            sql.append(" FROM ").append(query.getFromTable());
        }

        // JOIN clauses
        for (JoinClause joinClause : query.getJoinClauses()) {
            sql.append(" ").append(joinClause.getJoinType().getSqlKeyword());
            sql.append(" ").append(joinClause.getTableName());
            if (joinClause.getTableAlias() != null) {
                sql.append(" AS ").append(joinClause.getTableAlias());
            }
            sql.append(" ON ").append(joinClause.getJoinCondition().accept(this));
        }

        // WHERE clause
        if (query.getWherePredicate() != null) {
            sql.append(" WHERE ").append(query.getWherePredicate().accept(this));
        }

        // ORDER BY clause
        if (!query.getOrderByClauses().isEmpty()) {
            sql.append(" ORDER BY ");
            boolean first = true;
            for (Query.OrderByClause orderBy : query.getOrderByClauses()) {
                if (!first) {
                    sql.append(", ");
                }
                sql.append(orderBy.getColumnName()).append(" ").append(orderBy.getSortOrder());
                first = false;
            }
        }

        // LIMIT clause
        if (query.getLimit() != null) {
            String limitParamName = "param" + paramCounter.getAndIncrement();
            parameters.put(limitParamName, query.getLimit());
            sql.append(" LIMIT #{").append(limitParamName).append("}");
        }

        // OFFSET clause
        if (query.getOffset() != null) {
            String offsetParamName = "param" + paramCounter.getAndIncrement();
            parameters.put(offsetParamName, query.getOffset());
            sql.append(" OFFSET #{").append(offsetParamName).append("}");
        }

        return sql.toString();
    }
}
