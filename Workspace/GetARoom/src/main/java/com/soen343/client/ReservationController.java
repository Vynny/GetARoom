package com.soen343.client;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.joda.time.DateTime;

import com.codahale.metrics.annotation.Timed;
import com.soen343.core.QueueNode;
import com.soen343.core.QueueNodeEdge;
import com.soen343.core.Reservation;
import com.soen343.db.QueueNodeEdgeTDG;
import com.soen343.db.ReservationTDG;
import com.soen343.mappers.ReservationMapper;
import com.soen343.mappers.QueueNodeEdgeMapper;

@Path("/reservation")
@Produces(MediaType.APPLICATION_JSON)
public class ReservationController {
	
	private ReservationMapper reservationMapper;
	private QueueNodeEdgeMapper queueNodeEdgeMapper;

    public ReservationController(ReservationTDG reservationTDG, QueueNodeEdgeTDG queueNodeEdgeTDG) {
    	reservationMapper = new ReservationMapper(reservationTDG);
    	queueNodeEdgeMapper = new QueueNodeEdgeMapper(queueNodeEdgeTDG);
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
    @Path("/create")
    @Timed
    public void createReservation(@QueryParam("userid") long userId, @QueryParam("userid") long roomId, @QueryParam("start") String start, @QueryParam("end") String end) {
    	
    	Reservation newReservation = new Reservation((long)1, userId, roomId, true, start, end);
    	List<QueueNode> roots = getTrees();

    	for (Reservation res : reservationMapper.getAll()) {
    		if (res.isCollision(newReservation)) {
    			QueueNode newNode = new QueueNode(newReservation);
    	    	for (QueueNode root : roots) {
    	    		QueueNode resNode = root.search(res, root);
    	    		if (resNode != null) {
    	    			newNode.addChild(root);   
    	    			queueNodeEdgeMapper.create(new QueueNodeEdge((long)1, newReservation.getId(), root.getReservationId()));
    	    			return;
    	    		}
    	    	}
    		}
    	}
    }

    // Returns next reservation in queue
    @GET
    @Path("/{id}/cancel")
    @Timed
    public void cancelReservation(@PathParam("id") Integer id) {
    	Reservation reservation = reservationMapper.get(id);
    	if (reservation != null) {
    		List<QueueNode> roots = getTrees();
    		for (QueueNode root : roots) {
	    		if (root != null) {
		    		QueueNode parent = root.removeNode(reservation);
		    		
		       		if ((parent != null) && !(parent.childrenEmpty())) {
		    			reservationMapper.setWaitlisted(parent.getReservation(), false);
		    		}
	    		}
	    		reservationMapper.delete(reservation);
    		}
        } else {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }
    		
    @GET
    @Path("/getbyroom/{id}")
    @Timed
    public List<Reservation> getReservationByRoom(@PathParam("id") Integer id) {
    	List<Reservation> reservations = reservationMapper.getByRoom(id);
    	if (!reservations.isEmpty()) {
            return reservations;
        } else {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    @GET
    @Path("/getbyuser/{id}")
    @Timed
    public List<Reservation> getReservationByUser(@PathParam("id") Integer id) {
    	List<Reservation> reservations = reservationMapper.getByUser(id);
    	if (!reservations.isEmpty()) {
            return reservations;
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
    
    // Helper class to construct tree from edges
	private List<QueueNode> getTrees() {
		Hashtable<Integer, QueueNode> table = new Hashtable<Integer, QueueNode>();
		LinkedList<QueueNode> roots = new LinkedList<QueueNode>();
				
		List<QueueNodeEdge> all = queueNodeEdgeMapper.getAll();
		
		for (QueueNodeEdge edge : all) {
			int pid = (int)edge.getParentId();
			int cid = (int)edge.getChildId();
			
			if (!table.containsKey(pid)) {
				table.put(pid, new QueueNode(reservationMapper.get(pid)));
			}
			if (!table.contains(cid)) {
				table.put(cid, new QueueNode(reservationMapper.get(cid)));
			}
			
			table.get(pid).addChild(table.get(cid));
		}
		
		for (QueueNode node : table.values()) {
			QueueNode currentRoot = node.getRoot();
			if (!roots.contains(currentRoot)) {
				roots.add(currentRoot);
			}
		}
		
		return roots;
	}
}