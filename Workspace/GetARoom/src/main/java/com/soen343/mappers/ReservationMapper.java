package com.soen343.mappers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.soen343.core.DomainObject;
import com.soen343.core.Reservation;
import com.soen343.db.ReservationTDG;
import com.soen343.idmappers.ReservationIdentityMap;
import com.soen343.uow.UnitOfWork;

public class ReservationMapper implements Mapper<Reservation> {
	
	final Logger logger = LoggerFactory.getLogger(ReservationMapper.class);
	
	private ReservationTDG reservationTDG;
	private ReservationIdentityMap reservationIdentityMapper;

	public ReservationMapper(ReservationTDG reservationTDG) {
		this.reservationTDG = reservationTDG;
		this.reservationIdentityMapper = new ReservationIdentityMap();
	}
	
	public void makeNew(long user_id, long room_id, boolean waitlisted, String start_time, String end_time) {
		UnitOfWork uow = new UnitOfWork(this);
		long newID = reservationTDG.getMaxID() + 1;
		Reservation reservation = new Reservation(newID, user_id, room_id, waitlisted, start_time, end_time);
		reservationIdentityMapper.add((DomainObject) reservation);
		uow.registerNew((DomainObject) reservation);
		uow.commit();
	}

	public List<Reservation> getAll() {
		return reservationTDG.getAll();
	}

	public Reservation get(int id) {
		Reservation reservation = (Reservation) reservationIdentityMapper.get(id);
		if (reservation == null) {
			reservation = reservationTDG.findById(id);
		}
		return reservation;
	}

	public Reservation set(int id, String description) {
		return null;
	}

	@Override
	public void save(Reservation o) {
		reservationTDG.update(o);
	}

	@Override
	public void create(Reservation o) {
		reservationTDG.insert(o);
	}

	@Override
	public void delete(Reservation o) {
		reservationTDG.deleteById(o.getId());
	}

}