package com.soen343;

import java.io.UnsupportedEncodingException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

public class GetARoomConfiguration extends Configuration {
	@Valid
	@NotNull
    @JsonProperty("database")
	private DataSourceFactory database = new DataSourceFactory();
	
	@NotEmpty
    private String maxActiveReservations;

	@NotEmpty
    private String jwtTokenSecret = "H8oaVSO4H6edGOTtoL06MllJL1t7XHwoieruugqoiuewgf";

	public DataSourceFactory getDataSourceFactory() {
		return database;
	}
	
	public void setDatabase(DataSourceFactory database) {
	    this.database = database;
	}
	
    public byte[] getJwtTokenSecret() throws UnsupportedEncodingException {
        return jwtTokenSecret.getBytes("UTF-8");
    }

    @JsonProperty
	public String getMaxActiveReservations() {
		return maxActiveReservations;
	}

    @JsonProperty
	public void setMaxActiveReservations(String maxActiveReservations) {
		this.maxActiveReservations = maxActiveReservations;
	}
}
