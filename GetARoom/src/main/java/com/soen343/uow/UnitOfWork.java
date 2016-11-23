package com.soen343.uow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.soen343.core.DomainObject;
import com.soen343.mappers.Mapper;

public class UnitOfWork {
	final Logger logger = LoggerFactory.getLogger(UnitOfWork.class);
	
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
		newDomainObjects.add(newObj);
	}

	public void registerDeleted(DomainObject deletedObj) {
		deletedDomainObjects.add(deletedObj);
	}

	public void commit() {
		logger.info("Issuing commit operation");
		updateDirty();
		updateNew();
		updateDeleted();
	}

	private void updateDirty() {
		Iterator<DomainObject> it = dirtyDomainObjects.iterator();
		while (it.hasNext()) {
			DomainObject obj = it.next();
			dataMapper.save(obj);
		}
	}

	private void updateNew() {
		Iterator<DomainObject> it = newDomainObjects.iterator();
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
