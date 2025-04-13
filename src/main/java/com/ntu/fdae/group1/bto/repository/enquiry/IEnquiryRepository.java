package com.ntu.fdae.group1.bto.repository.enquiry;

import java.util.List;

import com.ntu.fdae.group1.bto.exceptions.DataAccessException;
import com.ntu.fdae.group1.bto.models.enquiry.Enquiry;
import com.ntu.fdae.group1.bto.repository.IRepository;

/**
 * Repository interface for accessing and manipulating Enquiry entities in the
 * BTO Management System.
 * <p>
 * This interface extends the generic IRepository interface, inheriting standard
 * CRUD operations while adding enquiry-specific query methods. It handles the
 * persistence and retrieval of Enquiry records, supporting the enquiry
 * management
 * functionality in the application.
 * </p>
 * <p>
 * Enquiries represent questions or information requests submitted by users
 * about
 * BTO projects or general topics. This repository provides methods for querying
 * enquiries based on user or project associations.
 * </p>
 */
public interface IEnquiryRepository extends IRepository<Enquiry, String> {

    /**
     * Retrieves all enquiries submitted by a specific user.
     * <p>
     * This method finds all enquiry records associated with the specified user
     * NRIC,
     * allowing retrieval of a user's complete enquiry history.
     * </p>
     * 
     * @param nric The NRIC of the user whose enquiries should be retrieved
     * @return A list of all enquiries submitted by the specified user
     */
    List<Enquiry> findByUserNric(String nric);

    /**
     * Retrieves all enquiries related to a specific project.
     * <p>
     * This method finds all enquiry records associated with the specified project,
     * which is useful for project management and responding to project-specific
     * queries.
     * </p>
     * 
     * @param projectId The ID of the project whose enquiries should be retrieved
     * @return A list of all enquiries related to the specified project
     */
    List<Enquiry> findByProjectId(String projectId);

    /**
     * Deletes the entity with the specified ID.
     * If the ID does not exist, the method might do nothing or throw an exception,
     * depending on implementation preference.
     *
     * @param id The ID of the entity to delete.
     * @throws DataAccessException If an error occurs during persistence.
     */
    void deleteById(String id) throws DataAccessException;
}
