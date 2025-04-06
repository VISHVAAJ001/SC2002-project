package com.ntu.fdae.group1.bto.services.user;

import com.ntu.fdae.group1.bto.models.user.User;
import com.ntu.fdae.group1.bto.repository.user.IUserRepository;
import com.ntu.fdae.group1.bto.utils.PasswordUtil;
import com.ntu.fdae.group1.bto.exceptions.AuthenticationException;

public class AuthenticationService implements IAuthenticationService {

    private final IUserRepository userRepository;

    public AuthenticationService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User login(String nric, String password) throws AuthenticationException {
        User user = userRepository.findById(nric);

        if (user == null) {
            throw new AuthenticationException("Login failed: User not found.");
        }

        if (!PasswordUtil.verifyPassword(password, user.getPasswordHash())) { // Assuming User has getPasswordHash()
            throw new AuthenticationException("Login failed: Incorrect password.");
        }

        // Login successful
        return user;
    }

    public boolean changePassword(User user, String newPassword) {
        if (user == null || newPassword == null || newPassword.trim().isEmpty()) {
            return false;
        }

        // Generate the new hash using PasswordUtil
        String newHash = PasswordUtil.hashPassword(newPassword);

        // Update the hash on the User object
        user.updatePasswordHash(newHash);

        // Update the user in the repository
        userRepository.save(user);

        return true;
    }

}