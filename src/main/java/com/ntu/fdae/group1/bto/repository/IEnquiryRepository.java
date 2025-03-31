package com.ntu.fdae.group1.bto.repository;

import com.ntu.fdae.group1.bto.models.project.Enquiry;

import java.util.List;

public interface IEnquiryRepository extends IRepository<Enquiry, String> {
    List<Enquiry> findByUserNric(String nric);

    List<Enquiry> findByProjectId(String projectId);
}
