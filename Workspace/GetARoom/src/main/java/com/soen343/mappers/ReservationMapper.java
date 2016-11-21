package com.soen343.mappers;

import java.util.ArrayList;
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
	
	public long makeNew(long user_id, long room_id, boolean waitlisted, String start_time, String end_time) {
		logger.info("\t Start Time TS: " + start_time.toString());
		UnitOfWork uow = new UnitOfWork(this);
		long newID = reservationTDG.getMaxID() + 1;
		Reservation reservation = new Reservation(newID, user_id, room_id, waitlisted, start_time, end_time);
		reservationIdentityMapper.add((DomainObject) reservation);
		uow.registerNew((DomainObject) reservation);
		uow.commit();
		
		return newID;
	}
	
	public void removeFromWaitlist(Reservation reservation) {
		UnitOfWork uow = new UnitOfWork(this);
		reservation.setWaitlisted(false);
		uow.registerDirty((DomainObject) reservation);
		uow.commit();
	}
		
	public List<Reservation> getAll() {
		return reservationTDG.getAll();
	}
	
	public Reservation get(int id) {
		Reservation reservation = (Reservation) reservationIdentityMapper.get(id);
		if (reservation == null) {
			reservation = reservationTDG.findById(id);
			if (reservation != null) {
			reservationIdentityMapper.add(reservation);
			}
		}
		return reservation;
	}
	
	public List<Reservation> getByRoom(int id) {
		List<Reservation> reservations = reservationIdentityMapper.getByRoomId(id);
		if (reservations.isEmpty()) {
			reservations = reservationTDG.findByRoomId(id);
			
			List<DomainObject> reservationDO = new ArrayList<DomainObject>(reservations);
			reservationIdentityMapper.addAll(reservationDO);
		}
		return reservations;
	}

	public List<Reservation> getByUser(int id) {
		List<Reservation> reservations = reservationIdentityMapper.getByUserId(id);
		if (reservations.isEmpty()) {
			reservations = reservationTDG.findByUserId(id);
			
			List<DomainObject> reservationDO = new ArrayList<DomainObject>(reservations);
			reservationIdentityMapper.addAll(reservationDO);
		}
		return reservations;
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
		reservationIdentityMapper.delete(o.getId());
		reservationTDG.deleteById(o.getId());
	}

}
