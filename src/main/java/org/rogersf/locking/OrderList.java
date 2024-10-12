package org.rogersf.locking;

import org.rogersf.core.BaseOrderList;

import java.util.concurrent.ConcurrentHashMap;

public class OrderList extends BaseOrderList {

	public OrderList () {
		super ();
		allOrders = new ConcurrentHashMap <> ();
	}
}
