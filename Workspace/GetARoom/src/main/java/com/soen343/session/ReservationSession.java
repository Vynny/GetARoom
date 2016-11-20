package com.soen343.session;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.soen343.client.ReservationMessage;
import com.soen343.mappers.ReservationMapper;

public class ReservationSession {

	final Logger logger = LoggerFactory.getLogger(ReservationSession.class);
	private Long userId;
	private Long roomId;
	private DateTime day;

	public ReservationSession(long userId, long roomId, DateTime day) {
		this.userId = userId;
		this.roomId = roomId;
		this.day = day;
	}
	
	public void makeReservation(ReservationMessage reservationInfo, boolean waitlisted) {
		logger.info("start timeTS: " + reservationInfo.getStartTime().toString());
		ReservationSessionManager.reservationController.getReservationMapper().makeNew(reservationInfo.getUserId().longValue(), 
				reservationInfo.getRoomId().longValue(), 
				waitlisted, 
				reservationInfo.getStartTime(), 
				reservationInfo.getEndTime());
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getRoomId() {
		return roomId;
	}

	public void setRoomId(long roomId) {
		this.roomId = roomId;
	}

	public DateTime getDay() {
		return day;
	}

	public void setDay(DateTime day) {
		this.day = day;
	}
	
	public String toString() {
		return "userId: " + getUserId() + ", roomId: " + getRoomId() + " , day: " + getDay().toDate();
	}

}
