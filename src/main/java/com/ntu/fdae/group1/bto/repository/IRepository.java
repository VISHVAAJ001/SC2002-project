package com.ntu.fdae.group1.bto.repository;

import java.util.Map;

import com.ntu.fdae.group1.bto.exceptions.DataAccessException;

/**
 * Generic repository interface defining the core data access operations for all
 * entities
 * in the BTO Management System.
 * <p>
 * This interface forms the foundation of the data access layer, providing a
 * consistent
 * contract for all repository implementations across the system. It follows the
 * Repository
 * pattern to abstract data persistence and retrieval mechanisms from the
 * business logic.
 * </p>
 * 
 * The interface is parameterized with:
 * <ul>
 * <li>T - The entity type this repository manages</li>
 * <li>ID - The type of the entity's unique identifier</li>
 * </ul>
 * 
 * <p>
 * All repository implementations must adhere to this contract, ensuring
 * consistent
 * data access behaviors throughout the system regardless of the underlying
 * persistence
 * technology.
 * </p>
 *
 * @param <T>  Type of the entity managed by this repository
 * @param <ID> Type of the identifier used to uniquely identify entities
 */
public interface IRepository<T, ID> {
    /**
     * Finds and retrieves a single entity by its unique identifier.
     * <p>
     * This method attempts to locate an entity with the specified ID in the data
     * store.
     * If no matching entity is found, the method returns null.
     * </p>
     * 
     * @param id The unique identifier of the entity to retrieve
     * @return The entity if found, or null if no entity exists with the given ID
     */
    T findById(ID id);

    /**
     * Retrieves all entities currently managed by this repository.
     * <p>
     * This method returns a map of all entities, with entity IDs as keys and
     * entity objects as values. If the repository is empty, an empty map is
     * returned.
     * </p>
     * 
     * @return A map containing all entities, with IDs as keys and entity objects as
     *         values
     */
    Map<ID, T> findAll();

    /**
     * Saves an entity to the repository.
     * <p>
     * If the entity already exists in the repository (based on its ID), the
     * existing
     * entity will be updated with the new values. If the entity does not exist,
     * it will be added as a new entry. The repository implementation is responsible
     * for determining how to extract the ID from the entity.
     * </p>
     * 
     * @param entity The entity to save
     */
    void save(T entity);

    /**
     * Saves multiple entities to the repository in a batch operation.
     * <p>
     * This method allows for more efficient bulk saving of entities compared to
     * calling save() repeatedly. It takes a map of entities keyed by their IDs
     * and persists them all, potentially in a single operation depending on the
     * implementation.
     * </p>
     * 
     * @param entities A map of entities to save, with IDs as keys and entity
     *                 objects as values
     */
    void saveAll(Map<ID, T> entities);

    /**
     * Loads all entities from the persistent storage into memory.
     * <p>
     * This method is typically called during application initialization to populate
     * the repository with data from the persistent store. Implementations should
     * handle
     * parsing and conversion from the storage format to entity objects.
     * </p>
     * 
     * @return A map of all loaded entities, with IDs as keys and entity objects as
     *         values
     * @throws DataAccessException If an error occurs during data loading or parsing
     */
    Map<ID, T> loadAll() throws DataAccessException;
}
