package com.ntu.fdae.group1.bto.repository.project;

import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.repository.IRepository;

/**
 * Repository interface specifically for Project entities.
 * Extends the base IRepository. Add project-specific query methods here if needed.
 */
public interface IProjectRepository extends IRepository<Project, String> {
    // List<Project> findByNeighborhood(String neighborhood);
}