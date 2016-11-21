package com.soen343.client;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ReservationModificationMessage {
	final Logger logger = LoggerFactory.getLogger(ReservationMessage.class);
	private Long id;
	private Long userId;
	private String startTime;
	private String endTime;

	@JsonCreator
	public ReservationModificationMessage(@JsonProperty("id") Long id, @JsonProperty("userId") Long userId, @JsonProperty("startTime") String startTime, @JsonProperty("endTime") String endTime) {
		this.id = id;
		this.userId = userId;
		this.setStartTime(startTime);
		this.setEndTime(endTime);
	}

	@JsonProperty
	public Long getId() {
		return id;
	}

	@JsonProperty
	public void setId(Long id) {
		this.id = id;
	}

	@JsonProperty
	public Long getUserId() {
		return userId;
	}

	@JsonProperty
	public void setUserId(Long id) {
		this.userId = userId;
	}

	@JsonProperty
	public String getStartTime() {
		return startTime;
	}

	@JsonProperty
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	@JsonProperty
	public String getEndTime() {
		return endTime;
	}

	@JsonProperty
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	
	public String toString() {
		return "id: " + getId() + ", userId: " + getUserId() + " startTime: " + getStartTime() + ", endTime: " + getEndTime();
	}

}
