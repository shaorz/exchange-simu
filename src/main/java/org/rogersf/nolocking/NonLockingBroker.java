package org.rogersf.nolocking;

import com.lmax.disruptor.RingBuffer;
import org.rogersf.core.AbstractBroker;
import org.rogersf.core.FixedPrice;
import org.rogersf.core.IBroker;
import org.rogersf.core.Order;

public class NonLockingBroker extends AbstractBroker {
	private final RingBuffer < Order > exchangeRing;
	private final String name;

	public NonLockingBroker ( final RingBuffer < Order > r , final String n ) {
		exchangeRing = r;
		name = n;
	}

	@Override
	public long insertOrder ( final String ticker , final FixedPrice price , final int quantity , final Order.Side side , final String orderId , final Order.OrderType type ) {
		long seq = exchangeRing.next ();
		try {
			Order o = exchangeRing.get ( seq );
			o.price = price;
			o.quantity = quantity;
			o.side = side;
			o.orderId = orderId;
			o.orderType = type;
			o.ticker = ticker;

			o.timeSubmitted = System.currentTimeMillis ();
			o.remaining = quantity;
			o.filled = 0;
		} finally {
			exchangeRing.publish ( seq );
		}
		return seq;
	}
}
