package com.ntu.fdae.group1.bto.services.booking;

import java.time.LocalDate;
import java.util.Collection;

import com.ntu.fdae.group1.bto.models.project.Application;
import com.ntu.fdae.group1.bto.models.project.OfficerRegistration;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.user.HDBManager;
import com.ntu.fdae.group1.bto.models.user.HDBOfficer;
import com.ntu.fdae.group1.bto.models.user.User;
import com.ntu.fdae.group1.bto.enums.FlatType;

/**
 * Service interface for determining eligibility of users for various BTO system
 * operations.
 * 
 * This interface provides methods to check if users meet the requirements for
 * specific actions within the BTO Management System, including:
 * <ul>
 * <li>Applicant eligibility for projects based on demographic criteria</li>
 * <li>Applicant eligibility for specific flat types based on household size and
 * composition</li>
 * <li>Officer eligibility to register for projects based on workload and
 * conflict-of-interest rules</li>
 * </ul>
 * 
 * 
 * The eligibility rules encapsulate complex business logic that considers
 * factors like:
 * <ul>
 * <li>Age and marital status requirements for different flat types</li>
 * <li>Income ceiling restrictions</li>
 * <li>Maximum project assignments for officers</li>
 * <li>Conflict of interest prevention</li>
 * </ul>
 * 
 */
public interface IEligibilityService {

        /**
         * Checks if an applicant meets the eligibility criteria for a specific project.
         * <p>
         * This method considers factors such as age, marital status, income level,
         * and whether the project offers suitable flat types for the applicant.
         * </p>
         * 
         * @param user    The user applying for the project
         * @param project The project to check eligibility for
         * @return true if the applicant is eligible for the project, false otherwise
         */
        boolean canApplicantApply(User user, Project project);

        /**
         * Checks if an applicant is eligible for a specific flat type.
         * 
         * Different flat types have different eligibility criteria. For example:
         * <ul>
         * <li>Singles may only be eligible for smaller flat types</li>
         * <li>Larger households may require larger flat types</li>
         * <li>Income ceilings may vary by flat type</li>
         * </ul>
         * 
         * 
         * @param user     The user to check eligibility for
         * @param flatType The flat type to check eligibility for
         * @return true if the applicant is eligible for the flat type, false otherwise
         */
        boolean isApplicantEligibleForFlatType(User user, FlatType flatType);

        /**
         * Checks if an officer can register to work on a specific project.
         * <p>
         * This method evaluates whether an officer should be permitted to request
         * registration for a project, considering applications and existing
         * registrations.
         * </p>
         * 
         * @param officer          The officer requesting registration
         * @param project          The project the officer wants to register for
         * @param allRegistrations All existing officer registrations in the system
         * @param allApplications  All existing applications in the system
         * @return true if the officer can register for the project, false otherwise
         */
        boolean canOfficerRegister(HDBOfficer officer, Project project,
                        Collection<OfficerRegistration> allRegistrations,
                        Collection<Application> allApplications);

        /**
         * Checks if an HDB Manager is eligible to create/handle a new project based on
         * concurrency rules.
         * Managers can only handle one project where the application periods overlap.
         *
         * @param manager             The manager creating/handling the project.
         * @param newProjectOpenDate  The opening date of the project being considered.
         * @param newProjectCloseDate The closing date of the project being considered.
         * @param allExistingProjects A collection of all projects currently in the
         *                            system.
         * @return true if the manager can handle this new project concurrently, false
         *         otherwise.
         */
        boolean checkManagerProjectHandlingEligibility(HDBManager manager, LocalDate newProjectOpenDate,
                        LocalDate newProjectCloseDate,
                        Collection<Project> allExistingProjects);
}
