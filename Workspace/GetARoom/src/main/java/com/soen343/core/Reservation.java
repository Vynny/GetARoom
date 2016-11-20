package com.soen343.core;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Reservation implements DomainObject {

	private final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

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
		this.start_time = parseDate(start_time);
		this.end_time = parseDate(end_time);
	}

	private DateTime parseDate(String date) {
		DateTimeFormatter format = DateTimeFormat.forPattern(DATE_FORMAT);
		String formattedDate = date.replaceAll("T", " ");
		return DateTime.parse(formattedDate, format);
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

	public void setWaitlisted(boolean waitlisted) {
		this.waitlisted = waitlisted;
	}

	public boolean isCollision(Reservation comp) {
		return ((comp.getStart_time().isAfter(this.start_time) && comp.getStart_time().isBefore(this.end_time))
				|| (comp.getEnd_time().isAfter(this.start_time) && comp.getEnd_time().isBefore(this.end_time)));
	}
}
