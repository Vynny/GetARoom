package com.soen343.core;

import java.security.Principal;
import com.fasterxml.jackson.annotation.JsonProperty;

public class User implements DomainObject, Principal {
	private long id;
	private String username;
	private String password;

	public User(long id, String username, String password) {
		this.id = id;
		this.username = username;
		this.password = password;
	}

	@JsonProperty
	public long getId() {
		return id;
	}
	
	@JsonProperty
	@Override
	public String getName() {
		return username;
	}
	
	@JsonProperty
	public String getPassword() {
		return password;
	}
}
