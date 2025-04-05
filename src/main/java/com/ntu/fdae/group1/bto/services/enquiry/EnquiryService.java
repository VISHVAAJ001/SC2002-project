package com.ntu.fdae.group1.bto.services.enquiry;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.ntu.fdae.group1.bto.models.enquiry.Enquiry;
import com.ntu.fdae.group1.bto.models.user.HDBStaff;
import com.ntu.fdae.group1.bto.models.user.User;
import com.ntu.fdae.group1.bto.repository.enquiry.IEnquiryRepository;
import com.ntu.fdae.group1.bto.utils.IdGenerator;

public class EnquiryService implements IEnquiryService {
    private final IEnquiryRepository enquiryRepo;

    public EnquiryService(IEnquiryRepository enquiryRepo) {
        this.enquiryRepo = enquiryRepo;
    }

    @Override
    public Enquiry createEnquiry(User user, String projectId, String content) {
        String enquiryId = IdGenerator.generateEnquiryId();
        Enquiry newEnquiry = new Enquiry(enquiryId, user.getNric(), projectId, content, LocalDate.now());
        enquiryRepo.save(newEnquiry);
        return newEnquiry;
    }

    @Override
    public boolean editEnquiry(String enquiryId, String newContent, User user) {
        Enquiry enquiry = enquiryRepo.findById(enquiryId);

        // Check if enquiry exists and belongs to the user
        if (enquiry == null || !enquiry.getUserNric().equals(user.getNric())) {
            return false;
        }

        // Check if enquiry has already been replied to
        if (enquiry.isReplied()) {
            return false;
        }

        enquiry.editContent(newContent);
        enquiryRepo.save(enquiry);
        return true;
    }

    @Override
    public boolean deleteEnquiry(String enquiryId, User user) {
        Enquiry enquiry = enquiryRepo.findById(enquiryId);

        // Check if enquiry exists and belongs to the user
        if (enquiry == null || !enquiry.getUserNric().equals(user.getNric())) {
            return false;
        }

        // TODO
        // We're not actually deleting from repository, so we need a way to mark as
        // deleted
        // This would depend on the implementation of the repository.
        // For now, we'll just assume we have a method to delete by ID
        // enquiryRepo.deleteById(enquiryId);
        return true;
    }

    @Override
    public boolean replyToEnquiry(String enquiryId, String replyContent, HDBStaff staff) {
        Enquiry enquiry = enquiryRepo.findById(enquiryId);

        // Check if enquiry exists
        if (enquiry == null) {
            return false;
        }

        // Check if enquiry has already been replied to
        if (enquiry.isReplied()) {
            return false;
        }

        enquiry.addReply(replyContent, LocalDate.now());
        enquiryRepo.save(enquiry);
        return true;
    }

    @Override
    public List<Enquiry> viewMyEnquiries(User user) {
        return enquiryRepo.findByUserNric(user.getNric());
    }

    @Override
    public List<Enquiry> viewAllEnquiries() {
        return enquiryRepo.findAll().values().stream()
                .collect(Collectors.toList());
    }

    @Override
    public List<Enquiry> viewProjectEnquiries(String projectId) {
        return enquiryRepo.findByProjectId(projectId);
    }

    @Override
    public Enquiry findEnquiryById(String enquiryId) {
        return enquiryRepo.findById(enquiryId);
    }
}
