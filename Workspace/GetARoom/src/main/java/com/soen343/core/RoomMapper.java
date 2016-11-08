package com.soen343.core;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.soen343.db.RoomTDG;
import com.soen343.domain.Room;

public class RoomMapper {
	
	private RoomTDG roomTDG;

	public RoomMapper(RoomTDG roomTDG) {
		this.roomTDG = roomTDG;
	}
	
	public Room get(int id) {
		return null; 
	}
	
	public Room set(int id, String description) {
		return null; 
	}

}
