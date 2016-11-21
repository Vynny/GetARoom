package com.soen343.client;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;
import com.soen343.core.QueueNode;
import com.soen343.core.QueueNodeEdge;
import com.soen343.core.Reservation;
import com.soen343.db.QueueNodeEdgeTDG;
import com.soen343.db.ReservationTDG;
import com.soen343.mappers.QueueNodeEdgeMapper;
import com.soen343.mappers.ReservationMapper;
import com.soen343.session.ReservationSession;
import com.soen343.session.ReservationSessionManager;

@Path("/reservation")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReservationController {
	
	final Logger logger = LoggerFactory.getLogger(ReservationController.class);
	
	private ReservationSessionManager reservationSessionManager;
	private ReservationMapper reservationMapper;
	private QueueNodeEdgeMapper queueNodeEdgeMapper;

    public ReservationController(ReservationTDG reservationTDG, QueueNodeEdgeTDG queueNodeEdgeTDG, ReservationSessionManager reservationSessionManager) {
    	reservationMapper = new ReservationMapper(reservationTDG);
    	queueNodeEdgeMapper = new QueueNodeEdgeMapper(queueNodeEdgeTDG);
    	this.reservationSessionManager = reservationSessionManager;
    }

    @GET
    @Path("/{id}")
    @Timed
    public Reservation getReservation(@PathParam("id") Integer id) {
    	Reservation reservation = reservationMapper.get(id);
    	if (reservation != null) {
            return reservation;
        } else {
            throw new WebApplicationException(Response.Status.NO_CONTENT);
        }
    }
        
    @POST
    @Timed
    public ReservationMessage createReservation(ReservationMessage message) {
    	logger.info("Got message: \n\t" + message);
    	Hashtable<Integer, QueueNode> nodeTable = getGraph();
    	LinkedList<Long> parents = new LinkedList<Long>();
    	ReservationSession session = reservationSessionManager.getSessionByUserId(message.getUserId());

		boolean collision = false;
		boolean known = false;
    	for (Reservation res: reservationMapper.getAll()) {
    		if (res.isCollision(message.getStartTime(), message.getEndTime())) {
    			collision = true;
    			
    			if (nodeTable.containsKey((int)res.getId())) {
    				// handle elements in the queue separately
    				known = true;
    			}
    			else {
    				// add dependencies to any reservations not in the queue
    				parents.add(res.getId());
    			}
    		}
    	}
    	
    	if (known) {
			List<QueueNode> newParents = QueueNode.getNewParents(message.getStartTime(), message.getEndTime(), getParentNodes());
			for (QueueNode np : newParents) {
				parents.add(np.getReservationId());
			}
    	}
    
	    long id = session.makeReservation(message, collision);
	    for (long i : parents) {
	    	queueNodeEdgeMapper.makeNew(i, id);
	    }
	    return message;
    }

    @GET
    @Path("/{id}/cancel")
    @Timed
    public Reservation cancelReservation(@PathParam("id") Integer id) {
    	Reservation reservation = reservationMapper.get(id);
    	if (reservation != null) {
    		Hashtable<Integer, QueueNode> nodeTable = getGraph();
    		QueueNode node = nodeTable.get((int)reservation.getId());
    		
    		if (node == null) {
    			// nothing to do, no dependencies. Just cancel
    		}
    		else {
	    		if (node.parentsEmpty()) {
	    			// reservation was active, activate children
	    			for (QueueNode child : node.getChildren()) {
	    				queueNodeEdgeMapper.remove(node.getReservationId(), child.getReservationId());
	    				node.removeChild(child);
	    				if (child.parentsEmpty()) {
	    	    			reservationMapper.removeFromWaitlist(child.getReservation());
	    				}
	    			}
	    		}
	    		else {
	    			// Remove parent connections
    				for (QueueNode parent : node.getParents()) {
    					queueNodeEdgeMapper.remove(parent.getReservationId(), node.getReservationId());
    				}
    				
	    			for (QueueNode child : node.getChildren()) {
	    				queueNodeEdgeMapper.remove(node.getReservationId(), child.getReservationId());
	    				// check collisions against parents, transfer dependencies
	    				boolean collision = false;
	    				for (QueueNode parent : node.getParents()) {
	    					if (parent.getReservation().isCollision(child.getReservation().getStart_time(), child.getReservation().getEnd_time())) {
	    						queueNodeEdgeMapper.makeNew(parent.getReservationId(), child.getReservationId());
	    						collision = true;
	    					}
	    				}
	    				if (!collision) {
	    					reservationMapper.removeFromWaitlist(child.getReservation());
	    				}
	    			}
	    		}
	    	}
    		
    		reservationMapper.delete(reservation);
    		return reservation;
        } else {
            throw new WebApplicationException(Response.Status.NO_CONTENT);
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
            throw new WebApplicationException(Response.Status.NO_CONTENT);
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
            throw new WebApplicationException(Response.Status.NO_CONTENT);
        }
    }
    
    // Helper class to construct tree from edges
	private List<QueueNode> getParentNodes() {
		Hashtable<Integer, QueueNode> table = getGraph();
		LinkedList<QueueNode> parents = new LinkedList<QueueNode>();
						
		for (QueueNode node : table.values()) {
			List<QueueNode> currentRoots = node.getRoots();
			for (QueueNode root : currentRoots) {
				if (!parents.contains(root)) {
					parents.add(root);
				}
			}
		}
		
		return parents;
	}
	
	private Hashtable<Integer, QueueNode> getGraph() {
		Hashtable<Integer, QueueNode> table = new Hashtable<Integer, QueueNode>();				
		List<QueueNodeEdge> all = queueNodeEdgeMapper.getAll();
		
		for (QueueNodeEdge edge : all) {
			int pid = (int)edge.getParentId();
			int cid = (int)edge.getChildId();
			
			if (!table.containsKey(pid)) {
				table.put(pid, new QueueNode(reservationMapper.get(pid)));
			}
			if (!table.containsKey(cid)) {
				table.put(cid, new QueueNode(reservationMapper.get(cid)));
			}
			
			table.get(pid).addChild(table.get(cid));
		}
		
		return table;
	}
	
	public ReservationMapper getReservationMapper() {
		return this.reservationMapper;
	}
}