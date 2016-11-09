package com.soen343.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

public class Reservation {
	private long id;
	private boolean waitlisted;
	private DateTime start_time;
	private DateTime end_time;
	//private String start_time;
	//private String end_time;

	public Reservation() {
		// Jackson deserialization
	}

	public Reservation(long id, boolean waitlisted, String start_time, String end_time) {
		this.id = id;
		this.waitlisted = waitlisted;
		this.start_time = DateTime.parse(start_time, ISODateTimeFormat.dateHourMinute());
		this.end_time = DateTime.parse(end_time, ISODateTimeFormat.dateHourMinute());
	}
	
	@JsonProperty
	public long getId() {
		return id;
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
