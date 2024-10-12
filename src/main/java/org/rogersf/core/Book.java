package org.rogersf.core;

import java.util.ArrayList;
import java.util.List;

public class Book {
	public List < BookLevel > bids;
	public List < BookLevel > asks;
	public List < Long > bidOrderIds;
	public List < Long > askOrderIds;

	public Book () {
		bids = new ArrayList <> ();
		asks = new ArrayList <> ();
		bidOrderIds = new ArrayList <> ();
		askOrderIds = new ArrayList <> ();
	}
}
