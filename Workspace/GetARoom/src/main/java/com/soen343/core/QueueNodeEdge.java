package com.soen343.core;

public class QueueNodeEdge implements DomainObject {
	private long id;
	private long parentId;
	private long childId;
		
	public QueueNodeEdge(long id, long parentId, long childId) {
		this.id = id;
		this.parentId = parentId;
		this.childId = childId;
	}
	
	public long getParentId() {
		return parentId;
	}
	
	public long getChildId() {
		return childId;
	}

	@Override
	public long getId() {
		return id;
	}
}
