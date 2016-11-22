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
	
	public void setStart_time(String start_time) {
		this.start_time = parseDate(start_time);
	}
	
	public void setEnd_time(String end_time) {
		this.end_time = parseDate(end_time);
	}

	public boolean isCollision(String start, String end) {
		DateTime startDateTime, endDateTime;
		startDateTime = parseDate(start);
		endDateTime = parseDate(end);
		
		return isCollision(startDateTime, endDateTime);
	}
	
	public boolean isCollision(DateTime start, DateTime end) {
		return ((start.isAfter(start_time) || start.isEqual(start_time)) && (start.isBefore(end_time)) || 
				(end.isAfter(start_time)) && (end.isBefore(end_time) || (end.isEqual(end_time))) ||
				(start.isBefore(start_time) && end.isAfter(end_time)));
	}
}
