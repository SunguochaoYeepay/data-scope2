package com.datascope.domain.common.repository;

import java.util.List;
import java.util.Optional;

/**
 * Base repository interface with common CRUD operations
 *
 * @param <T> Entity type
 * @param <ID> ID type
 * @author dreambt
 */
public interface BaseRepository<T, ID> {
    /**
     * Save entity
     *
     * @param entity Entity to save
     * @return Saved entity
     */
    T save(T entity);

    /**
     * Save multiple entities
     *
     * @param entities Entities to save
     * @return Saved entities
     */
    List<T> saveAll(List<T> entities);

    /**
     * Find entity by ID
     *
     * @param id Entity ID
     * @return Found entity
     */
    Optional<T> findById(ID id);

    /**
     * Find all entities
     *
     * @return All entities
     */
    List<T> findAll();

    /**
     * Find entities by IDs
     *
     * @param ids Entity IDs
     * @return Found entities
     */
    List<T> findAllById(Iterable<ID> ids);

    /**
     * Delete entity by ID
     *
     * @param id Entity ID
     */
    void deleteById(ID id);

    /**
     * Delete entity
     *
     * @param entity Entity to delete
     */
    void delete(T entity);

    /**
     * Delete multiple entities
     *
     * @param entities Entities to delete
     */
    void deleteAll(Iterable<T> entities);

    /**
     * Count total entities
     *
     * @return Total count
     */
    long count();

    /**
     * Check if entity exists
     *
     * @param id Entity ID
     * @return True if exists
     */
    boolean existsById(ID id);
}