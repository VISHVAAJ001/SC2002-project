package com.ntu.fdae.group1.bto.controllers.user;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import com.ntu.fdae.group1.bto.models.user.User;
import com.ntu.fdae.group1.bto.services.user.IUserService;

public class UserController {
    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = Objects.requireNonNull(userService);
    }

    // Simple wrapper, could add auth checks if needed for fetching specific user
    // data
    public String getUserName(String nric) {
        User user = userService.findUserById(nric);
        return (user != null && user.getName() != null) ? user.getName() : "N/A";
    }

    // Method specifically designed for UI list displays
    public Map<String, String> getUserNamesForList(Collection<String> nrics) {
        // Directly call the service method designed for this
        return userService.findUserNames(nrics);
    }

}
