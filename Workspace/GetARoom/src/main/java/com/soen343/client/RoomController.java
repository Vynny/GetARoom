package com.soen343.client;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
    	this.roomTDG = roomTDG;
    	roomMapper = new RoomMapper(roomTDG);
    }

    @GET
    @Path("/{id}")
    @Timed
    public Room getRoom(@PathParam("id") Integer id) {
    	Room room = roomMapper.get(id);
    	if (room != null) {
            return room;
        } else {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    	//Room room = new Room(0, "Test Room");
    }
}