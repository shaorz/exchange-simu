package org.rogersf.listeners;

import org.rogersf.core.Order;
import org.rogersf.core.Trade;

public interface ExchangeListener {
	void onOrder ( final Order order );

	void onTrade ( final Trade trade );
}
