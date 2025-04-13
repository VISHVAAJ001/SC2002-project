package com.ntu.fdae.group1.bto.repository.project;

import java.util.Set;

import com.ntu.fdae.group1.bto.exceptions.DataAccessException;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.repository.IRepository;

/**
 * Repository interface for accessing and manipulating Project entities in the
 * BTO Management System.
 * <p>
 * This interface extends the generic IRepository interface, inheriting standard
 * CRUD operations while adding project-specific query methods. It handles the
 * persistence
 * and retrieval of Project entities, supporting the project management
 * functionality
 * in the BTO Management System.
 * </p>
 * <p>
 * Projects represent the core housing developments in the BTO system. The
 * repository
 * provides methods for retrieving project information, managing associated flat
 * types,
 * and handling project lifecycle operations.
 * </p>
 * 
 * Implementations of this interface are responsible for:
 * <ul>
 * <li>Persisting project data to the chosen storage medium</li>
 * <li>Retrieving project information efficiently</li>
 * <li>Handling the relationships between projects and their flat types</li>
 * <li>Maintaining data consistency during project operations</li>
 * </ul>
 * 
 */
public interface IProjectRepository extends IRepository<Project, String> {
    /**
     * Retrieves all unique flat information IDs associated with projects across the
     * system.
     * 
     * This method collects the IDs of all flat information objects from all
     * projects
     * in the repository. It's particularly useful for:
     * <ul>
     * <li>Generating unique IDs for new flat information entities</li>
     * <li>Validating the existence of flat information references</li>
     * <li>Tracking all flat types across projects</li>
     * </ul>
     * 
     * 
     * @return A set of unique flat information IDs across all projects
     * @throws DataAccessException If an error occurs while accessing the data store
     */
    public Set<String> findAllFlatInfoIds() throws DataAccessException;

    /**
     * Deletes the Project with the specified ID.
     * If the ID does not exist, the method might do nothing or throw an exception,
     *
     * @param id The ID of the entity to delete.
     * @throws DataAccessException If an error occurs during persistence.
     */
    void deleteById(String id) throws DataAccessException;
}
