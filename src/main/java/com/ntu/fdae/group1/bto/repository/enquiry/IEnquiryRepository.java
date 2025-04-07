package com.ntu.fdae.group1.bto.repository.enquiry;

import java.util.List;

import com.ntu.fdae.group1.bto.exceptions.DataAccessException;
import com.ntu.fdae.group1.bto.models.enquiry.Enquiry;
import com.ntu.fdae.group1.bto.repository.IRepository;

public interface IEnquiryRepository extends IRepository<Enquiry, String> {
    List<Enquiry> findByUserNric(String nric);

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
