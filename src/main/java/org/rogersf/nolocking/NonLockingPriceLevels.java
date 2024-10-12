package org.rogersf.nolocking;

import org.rogersf.core.BasePriceLevels;

import java.util.TreeMap;

public class NonLockingPriceLevels extends BasePriceLevels {

	public NonLockingPriceLevels ( boolean ascending ) {
		super ( ascending );
		levels = new TreeMap <> ( priceComparator ( ascending ) );
	}


}
