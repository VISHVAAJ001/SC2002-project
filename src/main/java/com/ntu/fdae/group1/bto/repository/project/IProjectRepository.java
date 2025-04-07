package com.ntu.fdae.group1.bto.repository.project;

import java.util.Set;

import com.ntu.fdae.group1.bto.exceptions.DataAccessException;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.repository.IRepository;

public interface IProjectRepository extends IRepository<Project, String> {
    // Add project-specific methods if needed
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
