package com.ntu.fdae.group1.bto.controllers.user;

import com.ntu.fdae.group1.bto.models.user.User;
import com.ntu.fdae.group1.bto.repository.FileUserRepository;

public class AuthenticationController {
    public FileUserRepository userRepo;

    public AuthenticationController(FileUserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public User login(String nric, String password) {
        // User user = userRepo.get(nric);
        // return (user != null && user.getPassword().equals(password)) ? user : null;
        return null;
    }

    public void logout() {

    }

    public boolean changePassword(User user, String newPassword) {
        // if (user != null) {
        // user.setPassword(newPassword);
        // return true;
        // }
        return false;
    }

}
