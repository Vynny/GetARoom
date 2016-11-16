package com.soen343.core;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Reservation implements DomainObject {
	private long id;
	private long userId;
	private long roomId;
	private boolean waitlisted;
	private DateTime start_time;
	private DateTime end_time;
	
	public Reservation(long id, long userId, long roomId, boolean waitlisted, String start_time, String end_time) {
		this.id = id;
		this.userId = userId;
		this.roomId = roomId;
		this.waitlisted = waitlisted;
		this.start_time = DateTime.parse(start_time, ISODateTimeFormat.dateHourMinute());
		this.end_time = DateTime.parse(end_time, ISODateTimeFormat.dateHourMinute());
	}
	
	@JsonProperty
	public long getId() {
		return id;
	}
	
	@JsonProperty
	public long getuserId() {
		return userId;
	}
	
	@JsonProperty
	public long getroomId() {
		return roomId;
	}

	@JsonProperty
	public boolean isWaitlisted() {
		return waitlisted;
	}

	@JsonProperty
	public DateTime getStart_time() {
		return start_time;
	}

	@JsonProperty
	public DateTime getEnd_time() {
		return end_time;
	}
}
