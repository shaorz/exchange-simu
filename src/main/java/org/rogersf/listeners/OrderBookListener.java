package org.rogersf.listeners;

import org.rogersf.core.Order;
import org.rogersf.core.Trade;

public interface OrderBookListener {
	void onOrder ( final Order o );

	void onTrade ( final Trade t );
}
