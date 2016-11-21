package com.soen343.db;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import com.soen343.JDBImappers.ReservationJDBIMapper;
import com.soen343.core.Reservation;

@RegisterMapper(ReservationJDBIMapper.class)
public interface ReservationTDG {
	
	@SqlQuery("select MAX(id) from Reservation")
	long getMaxID();
	
	@SqlUpdate("insert into Reservation (user_id, room_id, waitlisted, start_time, end_time) values (:userId, :roomId, :waitlisted, :start_time, :end_time)")
	void insert(@BindBean Reservation reservation);

	@SqlUpdate("update Reservation set user_id = :r.userId, room_id = :r.roomId, waitlisted = :r.waitlisted, start_time = :r.start_time, end_time = :r.end_time")
	void update(@BindBean("r") Reservation reservation);
	
	@SqlQuery("select * from Reservation where id = :id")
	Reservation findById(@Bind("id") int id);
	
	@SqlQuery("select * from Reservation where user_id = :id")
	List<Reservation> findByUserId(@Bind("id") int id);

	@SqlQuery("select * from Reservation where room_id = :id")
	List<Reservation> findByRoomId(@Bind("id") int id);
	
	@SqlQuery("select * from Reservation")
	List<Reservation> getAll();

	@SqlUpdate("delete from Reservation where id = :it")
	void deleteById(@Bind long id);
}
