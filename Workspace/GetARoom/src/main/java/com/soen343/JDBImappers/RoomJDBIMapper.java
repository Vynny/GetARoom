package com.soen343.JDBImappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.soen343.core.Room;

public class RoomJDBIMapper implements ResultSetMapper<Room> {

	@Override
	public Room map(int index, ResultSet r, StatementContext ctx) throws SQLException {
		 return new Room(r.getLong("id"), r.getString("description"));
	}

}
