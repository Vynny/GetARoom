package com.soen343.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.soen343.domain.DomainObject;
import com.soen343.mappers.Mapper;

public class UnitOfWork {

	private List<DomainObject> dirtyDomainObjects;
	private List<DomainObject> newDomainObjects;
	private List<DomainObject> deletedDomainObjects;
	private Mapper dataMapper;

	public UnitOfWork(Mapper mapper) {
		this.dataMapper = mapper;
		this.dirtyDomainObjects = new ArrayList<DomainObject>();
		this.newDomainObjects = new ArrayList<DomainObject>();
		this.deletedDomainObjects = new ArrayList<DomainObject>();
	}

	public void registerDirty(DomainObject dirtyObj) {
		dirtyDomainObjects.add(dirtyObj);
	}

	public void registerNew(DomainObject newObj) {
		dirtyDomainObjects.add(newObj);
	}

	public void registerDeleted(DomainObject deletedObj) {
		dirtyDomainObjects.add(deletedObj);
	}

	public void commit() {
		updateDirty();
		updateNew();
		updateDeleted();
	}

	private void updateDirty() {
		Iterator<DomainObject> it = deletedDomainObjects.iterator();
		while (it.hasNext()) {
			DomainObject obj = it.next();
			dataMapper.save(obj);
		}
	}

	private void updateNew() {
		Iterator<DomainObject> it = deletedDomainObjects.iterator();
		while (it.hasNext()) {
			DomainObject obj = it.next();
			dataMapper.create(obj);
		}
	}

	private void updateDeleted() {
		Iterator<DomainObject> it = deletedDomainObjects.iterator();
		while (it.hasNext()) {
			DomainObject obj = it.next();
			dataMapper.delete(obj);
		}
	}
}
