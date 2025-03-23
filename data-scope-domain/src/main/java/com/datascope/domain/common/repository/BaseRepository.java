package com.datascope.domain.common.repository;

import java.util.List;
import java.util.Optional;

/**
 * Base repository interface
 * 
 * @author dreambt
 * @param <T> Entity type
 * @param <ID> ID type
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
     * Find entity by ID
     *
     * @param id Entity ID
     * @return Entity if found
     */
    Optional<T> findById(ID id);

    /**
     * Find all entities
     *
     * @return List of entities
     */
    List<T> findAll();

    /**
     * Delete entity by ID
     *
     * @param id Entity ID
     */
    void deleteById(ID id);

    /**
     * Check if entity exists by ID
     *
     * @param id Entity ID
     * @return true if exists
     */
    boolean existsById(ID id);

    /**
     * Count all entities
     *
     * @return Count of entities
     */
    long count();

    /**
     * Delete all entities
     */
    void deleteAll();
}