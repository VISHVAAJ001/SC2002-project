package com.ntu.fdae.group1.bto.repository;

import java.util.Map;

import com.ntu.fdae.group1.bto.exceptions.DataAccessException;

public interface IRepository<T, ID> {
    T findById(ID id); // Nullable

    Map<ID, T> findAll();

    void save(T entity);

    void saveAll(Map<ID, T> entities);

    Map<ID, T> loadAll() throws DataAccessException;
}
