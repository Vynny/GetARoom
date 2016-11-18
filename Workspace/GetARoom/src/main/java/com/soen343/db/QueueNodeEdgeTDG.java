package com.soen343.db;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import com.soen343.JDBImappers.QueueNodeEdgeJDBIMapper;
import com.soen343.core.QueueNodeEdge;
import com.soen343.core.Reservation;
import com.soen343.core.User;

@RegisterMapper(QueueNodeEdgeJDBIMapper.class)
public interface QueueNodeEdgeTDG {			
	@SqlUpdate("insert into QueueNodeEdge (parent_id, child_id) values (:parentId, :childId")
	void insert(@BindBean QueueNodeEdge edge);
	
	@SqlUpdate("delete from QueueNodeEdge where id = :id")
	void delete(@BindBean QueueNodeEdge edge);
	
	@SqlQuery("select * from QueueNodeEdge where id = :id")
	QueueNodeEdge findById(@Bind("id") int id);
	
	@SqlQuery("select * from QueueNodeEdge")
	List<QueueNodeEdge> getAll();
}
