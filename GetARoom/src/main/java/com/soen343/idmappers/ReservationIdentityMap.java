package com.soen343.idmappers;

import java.util.LinkedList;
import java.util.List;

import com.soen343.core.DomainObject;
import com.soen343.core.Reservation;

public class ReservationIdentityMap extends IdentityMap {
	public List<Reservation> getByUserId(long id) {
		List<Reservation> vals = new LinkedList<Reservation>();
		for (DomainObject obj : identityMap.values()) {
			if (((Reservation)obj).getuserId() == id) {
				vals.add((Reservation)obj);
			}
 		}
		return vals;
	}
	
	public List<Reservation> getByRoomId(long id) {
		List<Reservation> vals = new LinkedList<Reservation>();
		for (DomainObject obj : identityMap.values()) {
			if (((Reservation)obj).getroomId() == id) {
				vals.add((Reservation)obj);
			}
 		}
		return vals;
	}
}
