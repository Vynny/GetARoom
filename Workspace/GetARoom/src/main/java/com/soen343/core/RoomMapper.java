package com.soen343.core;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.soen343.db.RoomTDG;
import com.soen343.domain.Room;
import com.soen343.mappers.Mapper;
import com.soen343.mappers.RoomIdentityMap;

public class RoomMapper implements Mapper{

	private Logger logger = LoggerFactory.getLogger("com.foo");

	private RoomTDG roomTDG;
	private RoomIdentityMap roomIdentityMapper;

	public RoomMapper(RoomTDG roomTDG) {
		this.roomTDG = roomTDG;
		this.roomIdentityMapper = new RoomIdentityMap();
	}
	
	public List<Room> getAll() {
		return roomTDG.getAll();
	}

	public Room get(int id) {
		logger.info("Got room with description: " + roomTDG.findById(id).getDescription());
		Room room = (Room)roomIdentityMapper.get(id);
		if (room == null) {
			room = roomTDG.findById(id);
		}
		return room;
	}

	public Room set(int id, String description) {
		return null;
	}

	@Override
	public void save(Object o) {
		
	}
	
	@Override
	public void create(Object o) {

	}

	@Override
	public void delete(Object o) {
		
	}

}
