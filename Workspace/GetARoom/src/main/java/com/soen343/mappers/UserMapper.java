package com.soen343.mappers;

import java.util.List;

import com.soen343.core.User;
import com.soen343.db.UserTDG;
import com.soen343.idmappers.UserIdentityMap;

public class UserMapper implements Mapper<User> {

	private UserTDG userTDG;
	private UserIdentityMap userIdentityMapper;

	public UserMapper(UserTDG userTDG) {
		this.userTDG = userTDG;
		this.userIdentityMapper = new UserIdentityMap();
	}

	public List<User> getAll() {
		return userTDG.getAll();
	}

	public User get(int id) {
		User user = (User) userIdentityMapper.get(id);
		if (user == null) {
			user = userTDG.findById(id);
		}
		return user;
	}
	
	public User get(String username) {
		User user = userTDG.findByUsername(username);
		return user;
	}

	public User set(int id, String description) {
		return null;
	}

	@Override
	public void save(User o) {

	}

	@Override
	public void create(User o) {

	}

	@Override
	public void delete(User o) {

	}

}
