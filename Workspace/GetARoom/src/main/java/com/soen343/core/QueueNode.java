package com.soen343.core;

import java.util.LinkedList;
import java.util.List;

public class QueueNode {
	private Reservation res;
	private List<QueueNode> parents;
	private List<QueueNode> children;
	
	public QueueNode(Reservation res) {
		this.res = res;
		children = new LinkedList<QueueNode>();
		parents = new LinkedList<QueueNode>();
	}
	
	public long getReservationId() {
		return res.getId();
	}
	
	public Reservation getReservation() {
		return res;
	}
	
	public QueueNode addChild(QueueNode child) {
		child.parents.add(this);
		children.add(child);
		return child;
	}
				
	public static QueueNode search(Reservation res, QueueNode node) {
	    if (node != null) {
	        if (node.getReservationId() == res.getId()) {
	           return node;
	        }
	        else {
	        	for (QueueNode it : node.getChildren()) {
	        		QueueNode found = search(res, it);
	        		if (found != null) {
	        			return found;
	        		}
	        	}
	        }
	    }
	    return null;
	}
	
	public static List<QueueNode> getNewParents(String start, String end, List<QueueNode> cursors) {
		LinkedList<QueueNode> newParents = new LinkedList<QueueNode>();
		for (QueueNode cursor : cursors) {
			if (cursor.getReservation().isCollision(start, end)) {
				List<QueueNode> below = getNewParents(start, end, cursor.getChildren());
				if (below.isEmpty()) {
					newParents.add(cursor);
				}
				else {
					return below;
				}
			}
			else {
				newParents.addAll(getNewParents(start, end, cursor.getChildren()));
			}
		}
		return newParents;
	}
	
	public static List<QueueNode> getRoots(List<QueueNode> cursors) {
		LinkedList<QueueNode> roots = new LinkedList<QueueNode>();
		for (QueueNode cursor : cursors) {
			if (cursor.parentsEmpty()) {
				roots.add(cursor);
			}
			else {
				roots.addAll(getRoots(cursor.getParents()));
			}
		}
		return roots;
	}
	
	public List<QueueNode> getNewParents(String start, String end) {
		List<QueueNode> newParents = new LinkedList<QueueNode>();
		newParents.add(this);
		return getNewParents(start, end, newParents);
	}
	
	public List<QueueNode> getRoots() {
		List<QueueNode> here = new LinkedList<QueueNode>();
		here.add(this);
		return getRoots(here);
	}
	
	public QueueNode search(Reservation res) {
		return search(res, this);
	}
		
	public List<QueueNode> getChildren() {
		return children;
	}
	
	public List<QueueNode> getParents() {
		return parents;
	}
	
	public boolean childrenEmpty() {
		return children.isEmpty();
	}
	
	public boolean parentsEmpty() {
		return parents.isEmpty();
	}
	
	public void removeChild(QueueNode child) {
		child.parents.remove(this);
		children.remove(child);
	}
}
