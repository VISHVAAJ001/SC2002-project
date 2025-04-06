package com.ntu.fdae.group1.bto.repository.project;

import com.ntu.fdae.group1.bto.models.user.User;

public interface IUserRepository {
    void save(User user);
    User findById(String nric);
}
