package com.soen343.mappers;

import java.util.List;

import com.soen343.core.QueueNodeEdge;
import com.soen343.db.QueueNodeEdgeTDG;
import com.soen343.idmappers.QueueNodeEdgeIdentityMap;

public class QueueNodeEdgeMapper implements Mapper<QueueNodeEdge> {

	private QueueNodeEdgeTDG queueNodeEdgeTDG;
	private QueueNodeEdgeIdentityMap queueNodeEdgeIdentityMapper;

	public QueueNodeEdgeMapper(QueueNodeEdgeTDG queueNodeEdgeTDG) {
		this.queueNodeEdgeTDG = queueNodeEdgeTDG;
		this.queueNodeEdgeIdentityMapper = new QueueNodeEdgeIdentityMap();
	}

	public List<QueueNodeEdge> getAll() {
		return queueNodeEdgeTDG.getAll();
	}

	public QueueNodeEdge get(int id) {
		QueueNodeEdge queueNodeEdge = (QueueNodeEdge) queueNodeEdgeIdentityMapper.get(id);
		if (queueNodeEdge == null) {
			queueNodeEdge = queueNodeEdgeTDG.findById(id);
		}
		return queueNodeEdge;
	}

	public QueueNodeEdge set(int id, String description) {
		return null;
	}

	@Override
	public void save(QueueNodeEdge o) {

	}

	@Override
	public void create(QueueNodeEdge o) {

	}

	@Override
	public void delete(QueueNodeEdge o) {

	}

}
