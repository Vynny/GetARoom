package com.soen343.idmappers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.soen343.core.DomainObject;
import com.soen343.mappers.ReservationMapper;

public class IdentityMap {

	protected Map<Long, DomainObject> identityMap;

	public IdentityMap() {
		this.identityMap = new HashMap<Long, DomainObject>();
	}

	public void add(DomainObject o) {
		identityMap.put(o.getId(), o);
	}

	public void delete(long id) {
		identityMap.remove(id);
	}
	
	public void addAll(List<DomainObject> list) {
		Iterator<DomainObject> it = list.iterator();
		while (it.hasNext()) {
			DomainObject o = it.next();
			add(o);
		}
	}

	public DomainObject get(long id) {
		return identityMap.get(id);
	}
}
