package org.rogersf.nolocking;

import com.lmax.disruptor.RingBuffer;
import org.rogersf.core.FixedPrice;
import org.rogersf.core.IBroker;
import org.rogersf.core.Order;

public class NonLockingBroker implements IBroker {
	private final RingBuffer < Order > exchangeRing;
	private final String name;

	public NonLockingBroker ( final RingBuffer < Order > r , final String n ) {
		exchangeRing = r;
		name = n;
	}

	private long insertOrder ( final String ticker , final FixedPrice price , final int quantity , final Order.Side side , final String orderId , final Order.OrderType type ) {
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

	@Override
	public long buy ( String instrument , FixedPrice price , int quantity , String orderId ) {
		return insertOrder ( instrument , price , quantity , Order.Side.BUY , orderId , Order.OrderType.LIMIT );
	}

	@Override
	public long marketBuy ( String instrument , int quantity , String orderId ) {
		return insertOrder ( instrument , FixedPrice.MAX , quantity , Order.Side.BUY , orderId , Order.OrderType.MARKET );
	}

	@Override
	public long sell ( String instrument , FixedPrice price , int quantity , String orderId ) {
		return insertOrder ( instrument , price , quantity , Order.Side.SELL , orderId , Order.OrderType.LIMIT );
	}

	@Override
	public long marketSell ( String instrument , int quantity , String orderId ) {
		return insertOrder ( instrument , FixedPrice.ZERO , quantity , Order.Side.SELL , orderId , Order.OrderType.MARKET );
	}

	// TODO implement cancel
	@Override
	public int cancel ( long exchangeId ) {
		System.out.println ( "Broker right now can't cancel order yet" );
		return 0;
	}

}
