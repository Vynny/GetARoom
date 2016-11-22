package com.soen343.client;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
	
	private long maxActiveReservations;
	
	private ReservationSessionManager reservationSessionManager;
	private ReservationMapper reservationMapper;
	private QueueNodeEdgeMapper queueNodeEdgeMapper;

    public ReservationController(ReservationTDG reservationTDG, QueueNodeEdgeTDG queueNodeEdgeTDG, ReservationSessionManager reservationSessionManager, long maxActiveReservations) {
    	reservationMapper = new ReservationMapper(reservationTDG);
    	queueNodeEdgeMapper = new QueueNodeEdgeMapper(queueNodeEdgeTDG);
    	this.reservationSessionManager = reservationSessionManager;
    	this.maxActiveReservations = maxActiveReservations;
    	logger.info("maxActiveReservations is: " + this.maxActiveReservations);
    }
    
    @POST
    @Path("/modify")
    @Timed
    public ReservationModificationMessage modifyReservation(ReservationModificationMessage message) {
    	logger.info("Modifying reservation with message: \n\t" + message);
    	
    	Reservation current = reservationMapper.get(message.getId());
    	
    	removeFromQueue(message.getId());
    	List<Long> newParents = addToQueue(current.getroomId(), message.getStartTime(), message.getEndTime());    	
    	ReservationSession session = reservationSessionManager.getSessionByUserId(message.getUserId());
    	session.modifyReservation(message, !newParents.isEmpty());
    	
	    for (long i : newParents) {
	    	queueNodeEdgeMapper.makeNew(i, message.getId());
	    }
    	
    	return message;   	
    }
    
    @POST
    @Timed
    public Map<String, String> createReservation(ReservationMessage message) { 
	    HashMap<String, String> returnMap = new HashMap<String, String>();

    	if (reservationMapper.getUserReservationPermitted(message.getUserId(), maxActiveReservations)) {
        	logger.info("Adding message: \n\t" + message);

	    	List<Long> parents = addToQueue(message.getRoomId(), message.getStartTime(), message.getEndTime());
	    	ReservationSession session = reservationSessionManager.getSessionByUserId(message.getUserId());   
		    long id = session.makeReservation(message, !parents.isEmpty());
		    
		    for (long i : parents) {
		    	queueNodeEdgeMapper.makeNew(i, id);
		    }
		    
		    returnMap.put("message", "Reservation made!");
		    
    	} else {
        	logger.info("User over reservation limit from message: \n\t" + message);
        	returnMap.put("message", "Limit of " + maxActiveReservations + " reached!");
    	}
    	
	    return returnMap ;
    }
    
    @GET
    @Path("/{id}")
    @Timed
    public Reservation getReservation(@PathParam("id") Long id) {
    	Reservation reservation = reservationMapper.get(id);
    	if (reservation != null) {
            return reservation;
        } else {
            throw new WebApplicationException(Response.Status.NO_CONTENT);
        }
    }
    
    @GET
    @Path("/{id}/position")
    @Timed
    public long getReservationPosition(@PathParam("id") Long id) {
    	Reservation reservation = reservationMapper.get(id);
    	if (reservation != null) {
    		Hashtable<Long, QueueNode> table = getGraph();
    		if (table.containsKey(id)) {
    			return table.get(id).getPosition();
    		}
    		else {
    			return 0;
    		}
    	} else {
    		throw new WebApplicationException(Response.Status.NO_CONTENT);
    	}
    }

    @GET
    @Path("/{id}/cancel")
    @Timed
    public Response.Status cancelReservation(@PathParam("id") Long id) {
    	Reservation reservation = reservationMapper.get(id);
    	if (reservation != null) {
    		removeFromQueue(reservation.getId());
    		reservationMapper.remove(reservation.getId());
    		return Response.Status.OK;
        } else {
            throw new WebApplicationException(Response.Status.NO_CONTENT);
        }
    }
    		
    @GET
    @Path("/getbyroom/{id}")
    @Timed
    public List<Reservation> getReservationByRoom(@PathParam("id") Long id) {
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
    public List<Reservation> getReservationByUser(@PathParam("id") Long id) {
    	List<Reservation> reservations = reservationMapper.getByUser(id);
    	if (!reservations.isEmpty()) {
            return reservations;
        } else {
            throw new WebApplicationException(Response.Status.NO_CONTENT);
        }
    }
    
    private List<Long> addToQueue(long roomId, String startTime, String endTime) {    	
    	Hashtable<Long, QueueNode> nodeTable = getGraph();
    	LinkedList<Long> parents = new LinkedList<Long>();

		boolean known = false;
		// Need to check against all reservations on roomday, they might not be in the queue
    	for (Reservation res: reservationMapper.getByRoom(roomId)) {
    		if (res.isCollision(startTime, endTime)) {    			
    			if (nodeTable.containsKey(res.getId())) {
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
			List<QueueNode> newParents = QueueNode.getNewParents(startTime, endTime, getParentlessNodes(nodeTable));
			for (QueueNode np : newParents) {
				parents.add(np.getReservationId());
			}
    	}
    	
    	return parents;
    }
    
    private boolean removeFromQueue(Long reservationId) {
		Hashtable<Long, QueueNode> nodeTable = getGraph();
		QueueNode node = nodeTable.get(reservationId);
		
		if (node == null) {
			return false; // Not in the queue
		}
		else {
    		if (node.parentsEmpty()) {
    			// reservation was active, activate children
    			for (QueueNode child : node.getChildren()) {
    				queueNodeEdgeMapper.remove(node.getReservationId(), child.getReservationId());
    				if (child.getParents().size() == 1) {
    	    			reservationMapper.removeFromWaitlist(child.getReservation());
    	    			// remove colliding reservations from other rooms
    	    			for (Reservation res : reservationMapper.getAll()) {
    	    				if (res.getroomId() != child.getReservation().getroomId() && res.isCollision(child.getReservation().getStart_time(), child.getReservation().getEnd_time())) {
    	    					cancelReservation(res.getId());
    	    				}
    	    			}
    				}
    			}
    		} else {
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
    	    			// remove colliding reservations from other rooms
        				if (child.getParents().size() == 1) {
        	    			reservationMapper.removeFromWaitlist(child.getReservation());
        	    			for (Reservation res : reservationMapper.getAll()) {
        	    				if (res.getroomId() != child.getReservation().getroomId() && res.isCollision(child.getReservation().getStart_time(), child.getReservation().getEnd_time())) {
        	    					cancelReservation(res.getId());
        	    				}
        	    			}
        				}
    				}
    			}
    		}
    		return true; // successfully removed reservation from queue
    	}
    }
        
    // Get parentless nodes on the graph (reservations with queue dependencies)
	private List<QueueNode> getParentlessNodes(Hashtable<Long, QueueNode> table) {
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
	
	// Build queue graph from DB edges
	private Hashtable<Long, QueueNode> getGraph() {
		Hashtable<Long, QueueNode> table = new Hashtable<Long, QueueNode>();				
		List<QueueNodeEdge> all = queueNodeEdgeMapper.getAll();
		
		for (QueueNodeEdge edge : all) {
			long pid = edge.getParentId();
			long cid = edge.getChildId();
			
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