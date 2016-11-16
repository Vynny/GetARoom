package com.soen343.client;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.codahale.metrics.annotation.Timed;
import com.soen343.core.Reservation;
import com.soen343.db.ReservationTDG;
import com.soen343.mappers.ReservationMapper;

@Path("/reservation")
@Produces(MediaType.APPLICATION_JSON)
public class ReservationController {
	
	private ReservationMapper reservationMapper;
	private ReservationTDG reservationTDG;

    public ReservationController(ReservationTDG reservationTDG) {
    	this.reservationTDG = reservationTDG;
    	reservationMapper = new ReservationMapper(reservationTDG);
    }

    @GET
    @Path("/{id}")
    @Timed
    public Reservation getReservation(@PathParam("id") Integer id) {
    	Reservation reservation = reservationMapper.get(id);
    	if (reservation != null) {
            return reservation;
        } else {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }
    
    @GET
    @Path("/createtest")
    @Timed
    public void test() {
    	reservationMapper.makeNew(1, 1, false, "2016-11-14T08:00", "2016-11-14T10:00");
    }
}