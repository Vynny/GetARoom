package com.soen343.db;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import com.soen343.JDBImappers.QueueNodeEdgeJDBIMapper;
import com.soen343.core.QueueNodeEdge;

@RegisterMapper(QueueNodeEdgeJDBIMapper.class)
public interface QueueNodeEdgeTDG {		
	@SqlQuery("select MAX(id) from QueueNodeEdge")
	long getMaxID();
	
	@SqlUpdate("insert into QueueNodeEdge (id, parent_id, child_id) values (:id, :parentId, :childId)")
	void insert(@BindBean QueueNodeEdge edge);
	
	@SqlUpdate("delete from QueueNodeEdge where id = :id")
	void delete(@BindBean QueueNodeEdge edge);
	
	@SqlQuery("select * from QueueNodeEdge where id = :id")
	QueueNodeEdge findById(@Bind("id") int id);
	
	@SqlQuery("select * from QueueNodeEdge where parent_id = :parentId and child_id = :childId")
	QueueNodeEdge findByParentChildId(@Bind("parentId") long parentId, @Bind("childId") long childId);
	
	@SqlQuery("select * from QueueNodeEdge")
	List<QueueNodeEdge> getAll();
}
