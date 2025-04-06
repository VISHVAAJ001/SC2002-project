package com.ntu.fdae.group1.bto.repository.user;

import com.ntu.fdae.group1.bto.exceptions.DataAccessException;
import com.ntu.fdae.group1.bto.models.user.User;
import java.util.HashMap;
import java.util.Map;

public class InMemoryUserRepository implements IUserRepository {
    private Map<String, User> users = new HashMap<>();

    @Override
    public void save(User user) {
        users.put(user.getNric(), user);
    }

    @Override
    public User findById(String nric) {
        return users.get(nric);
    }

	@Override
	public Map<String, User> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveAll(Map<String, User> entities) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, User> loadAll() throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}
}
