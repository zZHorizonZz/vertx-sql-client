package io.vertx.sqldsl.repository;

import io.vertx.core.Future;
import io.vertx.sqldsl.Query;
import io.vertx.sqldsl.Predicate;
import io.vertx.sqlclient.SqlClient;

import java.util.List;
import java.util.Optional;

/**
 * Base repository interface providing common database operations with Query DSL support.
 * This interface defines the contract for repository implementations that work with
 * the SQL DSL for type-safe query building.
 *
 * @param <T> the entity type
 * @param <ID> the primary key type
 */
public interface BaseRepository<T, ID> {

    /**
     * Gets the SQL client used by this repository.
     * @return the SQL client
     */
    SqlClient getSqlClient();

    /**
     * Gets the entity class managed by this repository.
     * @return the entity class
     */
    Class<T> getEntityClass();

    /**
     * Gets the table name for the entity.
     * @return the table name
     */
    String getTableName();

    /**
     * Finds all entities matching the given query.
     *
     * @param query the query to execute
     * @return a Future containing the list of matching entities
     */
    Future<List<T>> findAll(Query query);

    /**
     * Finds all entities matching the given predicate.
     *
     * @param predicate the predicate to match
     * @return a Future containing the list of matching entities
     */
    default Future<List<T>> findAll(Predicate predicate) {
        Query query = new Query()
            .from(getTableName())
            .where(predicate);
        return findAll(query);
    }

    /**
     * Finds all entities in the table.
     *
     * @return a Future containing all entities
     */
    default Future<List<T>> findAll() {
        Query query = new Query().from(getTableName());
        return findAll(query);
    }

    /**
     * Finds the first entity matching the given query.
     *
     * @param query the query to execute
     * @return a Future containing an Optional with the matching entity, or empty if not found
     */
    Future<Optional<T>> findOne(Query query);

    /**
     * Finds the first entity matching the given predicate.
     *
     * @param predicate the predicate to match
     * @return a Future containing an Optional with the matching entity, or empty if not found
     */
    default Future<Optional<T>> findOne(Predicate predicate) {
        Query query = new Query()
            .from(getTableName())
            .where(predicate)
            .limit(1);
        return findOne(query);
    }

    /**
     * Finds an entity by its primary key.
     *
     * @param id the primary key value
     * @return a Future containing an Optional with the entity, or empty if not found
     */
    Future<Optional<T>> findById(ID id);

    /**
     * Counts the number of entities matching the given query.
     *
     * @param query the query to execute (only WHERE, HAVING clauses are considered)
     * @return a Future containing the count
     */
    Future<Long> count(Query query);

    /**
     * Counts the number of entities matching the given predicate.
     *
     * @param predicate the predicate to match
     * @return a Future containing the count
     */
    default Future<Long> count(Predicate predicate) {
        Query query = new Query()
            .from(getTableName())
            .where(predicate);
        return count(query);
    }

    /**
     * Counts all entities in the table.
     *
     * @return a Future containing the total count
     */
    default Future<Long> count() {
        Query query = new Query().from(getTableName());
        return count(query);
    }

    /**
     * Checks if any entities match the given query.
     *
     * @param query the query to execute
     * @return a Future containing true if at least one entity matches, false otherwise
     */
    Future<Boolean> exists(Query query);

    /**
     * Checks if any entities match the given predicate.
     *
     * @param predicate the predicate to match
     * @return a Future containing true if at least one entity matches, false otherwise
     */
    default Future<Boolean> exists(Predicate predicate) {
        Query query = new Query()
            .from(getTableName())
            .where(predicate);
        return exists(query);
    }

    /**
     * Checks if an entity with the given primary key exists.
     *
     * @param id the primary key value
     * @return a Future containing true if the entity exists, false otherwise
     */
    Future<Boolean> existsById(ID id);

    /**
     * Saves an entity (insert or update).
     *
     * @param entity the entity to save
     * @return a Future containing the saved entity
     */
    Future<T> save(T entity);

    /**
     * Saves multiple entities.
     *
     * @param entities the entities to save
     * @return a Future containing the list of saved entities
     */
    Future<List<T>> saveAll(List<T> entities);

    /**
     * Deletes an entity by its primary key.
     *
     * @param id the primary key value
     * @return a Future containing true if the entity was deleted, false if it didn't exist
     */
    Future<Boolean> deleteById(ID id);

    /**
     * Deletes the given entity.
     *
     * @param entity the entity to delete
     * @return a Future containing true if the entity was deleted, false if it didn't exist
     */
    Future<Boolean> delete(T entity);

    /**
     * Deletes all entities matching the given predicate.
     *
     * @param predicate the predicate to match
     * @return a Future containing the number of deleted entities
     */
    Future<Long> deleteAll(Predicate predicate);

    /**
     * Deletes all entities in the table.
     *
     * @return a Future containing the number of deleted entities
     */
    Future<Long> deleteAll();
}
