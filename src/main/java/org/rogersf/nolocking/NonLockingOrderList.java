package org.rogersf.nolocking;

import org.rogersf.core.BaseOrderList;

import java.util.HashMap;

public class NonLockingOrderList extends BaseOrderList {
	public NonLockingOrderList () {
		super ();
		allOrders = new HashMap <> ();
	}
}
