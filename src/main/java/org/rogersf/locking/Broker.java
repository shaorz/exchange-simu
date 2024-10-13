package org.rogersf.locking;

import org.rogersf.core.AbstractBroker;
import org.rogersf.core.FixedPrice;
import org.rogersf.core.Order;

import java.util.concurrent.ArrayBlockingQueue;

public class Broker extends AbstractBroker {
	private final  ArrayBlockingQueue < Order > exchangeQueue;
	private final String name;

	public Broker(final ArrayBlockingQueue < Order > exchangeQueue, final String name) {
		this.exchangeQueue = exchangeQueue;
		this.name = name;
	}

	@Override
	public long insertOrder ( final String ticker , final FixedPrice price , final int quantity , final Order.Side side , final String orderId , final Order.OrderType type )  {
		Order toAdd = new Order ( orderId, ticker, -1, price , quantity , side , type );
		try {
			exchangeQueue.put ( toAdd );
		} catch (final InterruptedException e) {
			e.printStackTrace();
			return -1;
		}
		return 0;
	}
}
