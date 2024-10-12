package org.rogersf.listeners;

import org.rogersf.core.Order;
import org.rogersf.core.Trade;

import java.util.List;

public class OrderBookListenToExchangeListener implements OrderBookListener {
	private List < ExchangeListener > exchangeListeners;

	public OrderBookListenToExchangeListener ( final List < ExchangeListener > exchangeListener ) {
		this.exchangeListeners = exchangeListener;
	}

	@Override
	public void onOrder ( final Order o ) {
		exchangeListeners.forEach ( l -> l.onOrder ( o ) );
	}

	@Override
	public void onTrade ( final Trade t ) {
		exchangeListeners.forEach ( l -> l.onTrade ( t ) );
	}
}
