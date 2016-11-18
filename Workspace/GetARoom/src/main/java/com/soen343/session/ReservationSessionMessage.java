package com.soen343.session;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ReservationSessionMessage {

	private Long userId;
	private Long roomId;
	private DateTime day;
	private Boolean valid;

	@JsonCreator
	public ReservationSessionMessage(@JsonProperty("userId") Long userId, @JsonProperty("roomId") Long roomId,
			@JsonProperty("day") String day) {
		this.setValid(false);
		this.setUserId(userId);
		this.setRoomId(roomId);
		setDay(DateTime.parse(day, ISODateTimeFormat.date()));
	}

	@JsonProperty
	public Long getUserId() {
		return userId;
	}

	@JsonProperty
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@JsonProperty
	public Long getRoomId() {
		return roomId;
	}

	@JsonProperty
	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}

	@JsonProperty
	public DateTime getDay() {
		return day;
	}

	@JsonProperty
	public void setDay(DateTime day) {
		this.day = day;
	}

	@JsonProperty
	public Boolean getValid() {
		return valid;
	}

	@JsonProperty
	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	public String toString() {
		return "userId: " + getUserId() + ", roomId: " + getRoomId() + " , day: " + getDay().toDate();
	}

}
