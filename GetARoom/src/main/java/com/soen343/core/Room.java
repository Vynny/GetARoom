package com.soen343.core;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Room implements DomainObject {
	private long id;
	private String description;

	public Room() {
		
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
