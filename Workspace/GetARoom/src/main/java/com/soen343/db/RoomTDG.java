package com.soen343.db;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import com.soen343.JDBImappers.RoomJDBIMapper;
import com.soen343.core.Room;

@RegisterMapper(RoomJDBIMapper.class)
public interface RoomTDG {
	
	@SqlUpdate("insert into Room (description) values (:description)")
	void insert(@BindBean Room room);

	@SqlUpdate("update Room set description = :r.description")
	void update(@BindBean("r") Room room);

	@SqlQuery("select * from Room where id = :id")
	Room findById(@Bind("id") long id);

	@SqlQuery("select * from Room")
	List<Room> getAll();

	@SqlUpdate("delete from Room where id = :it")
	void deleteById(@Bind long id);
}
