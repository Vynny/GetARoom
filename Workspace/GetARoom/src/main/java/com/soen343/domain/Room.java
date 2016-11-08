package com.soen343.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Room {
	private long id;
	private String description;

	public Room() {
		// Jackson deserialization
	}

	public Room(long id, String description) {
		this.id = id;
		this.description = description;
	}

	@JsonProperty
	public long getId() {
		return id;
	}

	@JsonProperty
	public String getDescription() {
		return description;
	}
}
