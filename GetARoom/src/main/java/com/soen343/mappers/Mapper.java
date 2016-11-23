package com.soen343.mappers;

public interface Mapper<T> {

	void save(T o);

	void create(T o);
	
	void delete(T o);
}
