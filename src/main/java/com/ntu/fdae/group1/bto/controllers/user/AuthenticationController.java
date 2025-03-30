package com.ntu.fdae.group1.bto.controllers.user;

import com.ntu.fdae.group1.bto.models.user.User;
import com.ntu.fdae.group1.bto.services.booking.IDataManager;
import java.util.Map;

public class AuthenticationController {
    public Map<String, User> userRepo;
    public IDataManager dataManager;

    public AuthenticationController(Map<String, User> userMap, IDataManager dataMgr){
        this.userRepo = userMap;
        this.dataManager = dataMgr;
    }

    public User login(String nric, String password){
        User user = userRepo.get(nric);
        return (user != null && user.getPassword().equals(password)) ? user : null;
    }

    public void logout(){

    }

    public boolean changePassword(User user, String newPassword){
        if (user != null){
            user.setPassword(newPassword);
            return true;
        }
        return false;
    }
    
}
