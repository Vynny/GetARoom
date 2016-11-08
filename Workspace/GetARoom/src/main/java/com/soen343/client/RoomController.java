package com.soen343.client;

import java.util.Optional;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;
import com.soen343.core.RoomMapper;
import com.soen343.db.RoomTDG;
import com.soen343.domain.Room;

@Path("/room")
@Produces(MediaType.APPLICATION_JSON)
public class RoomController {
	
	private RoomMapper roomMapper;
	private RoomTDG roomTDG;

    public RoomController(RoomTDG roomTDG) {
    	roomTDG = this.roomTDG;
    	roomMapper = new RoomMapper(roomTDG);
    }

    @GET
    @Timed
    public Room getRoom(@QueryParam("id") Optional<Integer> id) {
    	Room room = roomMapper.get(id.get());
        return room;
    }
}