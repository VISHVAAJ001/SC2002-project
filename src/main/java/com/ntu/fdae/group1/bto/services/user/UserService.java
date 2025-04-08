package com.ntu.fdae.group1.bto.services.user;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.ntu.fdae.group1.bto.models.user.User;
import com.ntu.fdae.group1.bto.repository.user.IUserRepository;

public class UserService implements IUserService {
    private final IUserRepository userRepository;

    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User findUserById(String nric) {
        return userRepository.findById(nric); // Handle null if necessary
    }

    @Override
    public Map<String, String> findUserNames(Collection<String> nrics) {
        if (nrics == null || nrics.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, String> nameMap = new HashMap<>();
        for (String nric : nrics) {
            if (nric != null && !nric.isEmpty()) {
                User user = userRepository.findById(nric);
                nameMap.put(nric, (user != null && user.getName() != null) ? user.getName() : "N/A");
            }
        }
        return nameMap;
    }
}