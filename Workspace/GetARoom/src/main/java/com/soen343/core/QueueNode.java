package com.soen343.core;

import java.util.LinkedList;
import java.util.List;

public class QueueNode {
	private Reservation res;
	private QueueNode parent;
	private List<QueueNode> children;
	
	public QueueNode(Reservation res) {
		this.res = res;
		children = new LinkedList<QueueNode>();
	}
	
	public long getReservationId() {
		return res.getId();
	}
	
	public Reservation getReservation() {
		return res;
	}
	
	public QueueNode addChild(QueueNode child) {
		child.parent = this;
		children.add(child);
		return child;
	}
	
	public QueueNode getRoot() {
		QueueNode buf = this;
		while (buf != null) {
			buf = buf.parent;
		}
		
		return buf;
	}
	
	public QueueNode search(Reservation res, QueueNode node) {
	    if (node != null) {
	        if (node.getReservationId() == res.getId()) {
	           return node;
	        }
	        else {
	        	for (QueueNode it : children) {
	        		QueueNode found = search(res, it);
	        		if (found != null) {
	        			return found;
	        		}
	        	}
	        }
	    }
	    return null;
	}
	
	public List<QueueNode> timeslotCollisions(List<QueueNode> li, Reservation res, QueueNode node) {
	    if (node != null) {
	        if (node.getReservation().isCollision(res)) {
	        	li.add(node);
	        }
        	for (QueueNode it : children) {
        		li.addAll(timeslotCollisions(new LinkedList<QueueNode>(), res, it));
        	}
	    }
	    return li;
	}
	
	// returns parent
	public QueueNode removeNode(Reservation target) {
		QueueNode found = search(target, this);
		if (found != null) {
			QueueNode parent = found.parent;
			parent.removeChild(found);
			return parent;
		}
		else {
			return null;
		}
	}
	
	public boolean childrenEmpty() {
		return children.isEmpty();
	}
	
	public void removeChild(QueueNode child) {
		children.remove(child);
	}
}
