package com.ntu.fdae.group1.bto.services.user;

import com.ntu.fdae.group1.bto.models.user.User;

import java.util.Collection;
import java.util.Map;

public interface IUserService {
    User findUserById(String nric);

    Map<String, String> findUserNames(Collection<String> nrics);
}
