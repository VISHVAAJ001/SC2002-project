package com.ntu.fdae.group1.bto.repository.user;

import com.ntu.fdae.group1.bto.models.user.User;
import com.ntu.fdae.group1.bto.repository.IRepository;

/**
 * Repository interface for managing User entities.
 * <p>
 * This interface defines the contract for user data access operations,
 * providing methods to retrieve, store, and manage users of all types
 * (Applicant, HDBOfficer, HDBManager) in the BTO Management System.
 * </p>
 * <p>
 * The interface extends the base repository interface with user-specific
 * functionality and ensures proper data persistence regardless of the
 * underlying storage mechanism.
 * </p>
 */
public interface IUserRepository extends IRepository<User, String> {
}
