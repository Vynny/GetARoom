package com.soen343.mappers;

import java.util.List;

import com.soen343.core.Room;
import com.soen343.db.RoomTDG;
import com.soen343.idmappers.RoomIdentityMap;

public class RoomMapper implements Mapper<Room> {

	private RoomTDG roomTDG;
	private RoomIdentityMap roomIdentityMapper;

	public RoomMapper(RoomTDG roomTDG) {
		this.roomTDG = roomTDG;
		this.roomIdentityMapper = new RoomIdentityMap();
	}

	public List<Room> getAll() {
		return roomTDG.getAll();
	}

	public Room get(long id) {
		Room room = (Room) roomIdentityMapper.get(id);
		if (room == null) {
			room = roomTDG.findById(id);
		}
		return room;
	}

	public Room set(long id, String description) {
		return null;
	}

	@Override
	public void save(Room o) {

	}

	@Override
	public void create(Room o) {

	}

	@Override
	public void delete(Room o) {

	}

}
