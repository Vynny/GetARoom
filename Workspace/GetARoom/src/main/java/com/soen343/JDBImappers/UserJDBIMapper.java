package com.soen343.JDBImappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.soen343.core.User;

public class UserJDBIMapper implements ResultSetMapper<User> {

	@Override
	public User map(int index, ResultSet r, StatementContext ctx) throws SQLException {
		return new User(r.getInt("id"), r.getString("username"), r.getString("password"));
	}

}
