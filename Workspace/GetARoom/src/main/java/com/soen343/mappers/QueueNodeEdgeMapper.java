package com.soen343.mappers;

import java.util.List;

import com.soen343.core.DomainObject;
import com.soen343.core.QueueNodeEdge;
import com.soen343.core.Reservation;
import com.soen343.db.QueueNodeEdgeTDG;
import com.soen343.idmappers.QueueNodeEdgeIdentityMap;
import com.soen343.uow.UnitOfWork;

public class QueueNodeEdgeMapper implements Mapper<QueueNodeEdge> {

	private QueueNodeEdgeTDG queueNodeEdgeTDG;
	private QueueNodeEdgeIdentityMap queueNodeEdgeIdentityMapper;

	public QueueNodeEdgeMapper(QueueNodeEdgeTDG queueNodeEdgeTDG) {
		this.queueNodeEdgeTDG = queueNodeEdgeTDG;
		this.queueNodeEdgeIdentityMapper = new QueueNodeEdgeIdentityMap();
	}
	
	public void makeNew(long parent_id, long child_id) {
		UnitOfWork uow = new UnitOfWork(this);
		long newID = queueNodeEdgeTDG.getMaxID() + 1;
		QueueNodeEdge edge = new QueueNodeEdge(newID, parent_id, child_id);
		queueNodeEdgeIdentityMapper.add((DomainObject) edge);
		uow.registerNew((DomainObject) edge);
		uow.commit();
	}
	
	public void remove(long parent_id, long child_id) {
		QueueNodeEdge edge = queueNodeEdgeTDG.findByParentChildId(parent_id, child_id);
		if (edge != null) {
			UnitOfWork uow = new UnitOfWork(this);
			queueNodeEdgeIdentityMapper.delete(edge.getId());
			uow.registerDeleted(edge);
			uow.commit();
		}
	}
	
	public long getMaxId() {
		return queueNodeEdgeTDG.getMaxID();
	}

	public List<QueueNodeEdge> getAll() {
		return queueNodeEdgeTDG.getAll();
	}

	public QueueNodeEdge get(long id) {
		QueueNodeEdge queueNodeEdge = (QueueNodeEdge) queueNodeEdgeIdentityMapper.get(id);
		if (queueNodeEdge == null) {
			queueNodeEdge = queueNodeEdgeTDG.findById(id);
		}
		return queueNodeEdge;
	}
		
	@Override
	public void save(QueueNodeEdge o) {
	}

	@Override
	public void create(QueueNodeEdge o) {
		queueNodeEdgeTDG.insert(o);
	}

	@Override
	public void delete(QueueNodeEdge o) {
		queueNodeEdgeTDG.delete(o);
	}

}
