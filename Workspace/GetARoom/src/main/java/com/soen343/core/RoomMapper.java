package com.soen343.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.soen343.db.RoomTDG;
import com.soen343.domain.Room;

public class RoomMapper {

	private Logger logger = LoggerFactory.getLogger("com.foo");

	private RoomTDG roomTDG;

	public RoomMapper(RoomTDG roomTDG) {
		this.roomTDG = roomTDG;
	}

	public Room get(int id) {
		logger.info("Got room with description: " + roomTDG.findById(id).getDescription());
		return roomTDG.findById(id);
	}

	public Room set(int id, String description) {
		return null;
	}

}
