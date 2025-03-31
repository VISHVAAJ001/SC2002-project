package com.ntu.fdae.group1.bto.repository.enquiry;

import java.util.List;

import com.ntu.fdae.group1.bto.models.enquiry.Enquiry;
import com.ntu.fdae.group1.bto.repository.IRepository;

public interface IEnquiryRepository extends IRepository<Enquiry, String> {
    List<Enquiry> findByUserNric(String nric);

    List<Enquiry> findByProjectId(String projectId);
}
