package com.soen343.db;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

import com.soen343.domain.Room;

public interface RoomTDG {

	/*@SqlUpdate("create table Room (id int auto_increment primary key, description varchar(80))")
	void createPersonTable();*/

	@SqlUpdate("insert into Room (description) values (:description)")
	void insert(@BindBean Room room);

	@SqlUpdate("update Room set description = :r.description")
	void update(@BindBean("r") Room room);

	@SqlQuery("select * from Room where id = :id")
	Room findById(@Bind("id") int id);

	@SqlQuery("select * from Room")
	List<Room> getAll();

	@SqlUpdate("delete from Room where id = :it")
	void deleteById(@Bind int id);
}
