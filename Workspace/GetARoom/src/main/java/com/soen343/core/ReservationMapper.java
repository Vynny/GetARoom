package com.soen343.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.soen343.db.ReservationTDG;
import com.soen343.domain.Reservation;

public class ReservationMapper {

	private Logger logger = LoggerFactory.getLogger("com.foo");

	private ReservationTDG reservationTDG;

	public ReservationMapper(ReservationTDG reservationTDG) {
		this.reservationTDG = reservationTDG;
	}

	public Reservation get(int id) {
		logger.info("Got reservation");
		return reservationTDG.findById(id);
	}

	public Reservation set(int id, String description) {
		return null;
	}

}
