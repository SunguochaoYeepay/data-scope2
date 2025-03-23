package com.datascope.domain.common.service;

import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

/**
 * Base service interface
 * 
 * @author dreambt
 * @param <T> Entity type
 * @param <ID> ID type
 */
public interface BaseService<T, ID> {
    /**
     * Create entity
     *
     * @param entity Entity to create
     * @return Created entity
     */
    @Transactional
    T create(T entity);

    /**
     * Update entity
     *
     * @param entity Entity to update
     * @return Updated entity
     */
    @Transactional
    T update(T entity);

    /**
     * Get entity by ID
     *
     * @param id Entity ID
     * @return Entity if found
     */
    Optional<T> getById(ID id);

    /**
     * Get all entities
     *
     * @return List of entities
     */
    List<T> getAll();

    /**
     * Delete entity by ID
     *
     * @param id Entity ID
     */
    @Transactional
    void deleteById(ID id);

    /**
     * Check if entity exists by ID
     *
     * @param id Entity ID
     * @return true if exists
     */
    boolean exists(ID id);

    /**
     * Count all entities
     *
     * @return Count of entities
     */
    long count();

    /**
     * Validate entity
     *
     * @param entity Entity to validate
     */
    void validate(T entity);
}