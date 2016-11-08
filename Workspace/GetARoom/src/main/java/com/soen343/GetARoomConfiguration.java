package com.soen343;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

public class GetARoomConfiguration extends Configuration {
	@Valid
	@NotNull
    @JsonProperty("database")
	private DataSourceFactory database = new DataSourceFactory();

	public DataSourceFactory getDataSourceFactory() {
		return database;
	}
	
	public void setDatabase(DataSourceFactory database) {
	    this.database = database;
	}
}
