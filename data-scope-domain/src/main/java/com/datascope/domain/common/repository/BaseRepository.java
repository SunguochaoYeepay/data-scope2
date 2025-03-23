package com.datascope.domain.common.repository;

import java.util.List;
import java.util.Optional;

/**
 * Base repository interface providing common CRUD operations
 * @param <T> Entity type
 * @param <ID> ID type
 */
public interface BaseRepository<T, ID> {
    T save(T entity);
    List<T> saveAll(List<T> entities);
    Optional<T> findById(ID id);

    boolean existsById(ID id);
    List<T> findAll();

    List<T> findAllById(List<ID> ids);

    long count();
    void deleteById(ID id);
    void deleteAll();

    void deleteAllById(List<ID> ids);
}
