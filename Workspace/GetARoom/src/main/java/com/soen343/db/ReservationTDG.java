package com.soen343.db;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import com.soen343.core.ReservationJDBIMapper;
import com.soen343.domain.Reservation;

@RegisterMapper(ReservationJDBIMapper.class)
public interface ReservationTDG {
	@SqlUpdate("insert into Reservation (description) values (:description)")
	void insert(@BindBean Reservation reservation);

	@SqlUpdate("update Reservation set description = :r.description")
	void update(@BindBean("r") Reservation reservation);

	@SqlQuery("select * from Reservation where id = :id")
	Reservation findById(@Bind("id") int id);

	@SqlQuery("select * from Reservation")
	List<Reservation> getAll();

	@SqlUpdate("delete from Reservation where id = :it")
	void deleteById(@Bind int id);
}
