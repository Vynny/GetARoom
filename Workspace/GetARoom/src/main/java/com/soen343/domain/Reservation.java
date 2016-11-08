package com.soen343.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Reservation {
	private long id;
	private boolean waitlisted;
	private String start_time;
	private String end_time;

	public Reservation() {
		// Jackson deserialization
	}

	public Reservation(long id, boolean waitlisted, String start_time, String end_time) {
		this.id = id;
		this.waitlisted = waitlisted;
		this.start_time = start_time;
		this.end_time = end_time;		
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
	public String getStart_time() {
		return start_time;
	}

	@JsonProperty
	public String getEnd_time() {
		return end_time;
	}
}
