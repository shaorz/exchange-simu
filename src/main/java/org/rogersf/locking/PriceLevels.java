package org.rogersf.locking;

import org.rogersf.core.BasePriceLevels;

import java.util.concurrent.ConcurrentSkipListMap;

public class PriceLevels extends BasePriceLevels {

	public PriceLevels ( boolean ascending ) {
		super ( ascending );
		levels = new ConcurrentSkipListMap <> ( priceComparator ( ascending ) );
	}

}
