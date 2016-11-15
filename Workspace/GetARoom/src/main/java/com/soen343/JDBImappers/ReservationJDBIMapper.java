package com.soen343.JDBImappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.soen343.core.Reservation;

public class ReservationJDBIMapper implements ResultSetMapper<Reservation> {

	@Override
	public Reservation map(int index, ResultSet r, StatementContext ctx) throws SQLException {
		 return new Reservation(r.getInt("id"), r.getInt("user_id"), r.getInt("room_id"), r.getBoolean("waitlisted"), r.getString("start_time"), r.getString("end_time"));
	}

}