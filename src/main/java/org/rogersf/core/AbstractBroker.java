package org.rogersf.core;

public abstract class AbstractBroker implements IBroker {

	// TODO implement cancel
	@Override
	public int cancel ( final long exchangeId ) {
		System.out.println ( "Broker right now can't cancel order yet" );
		return 0;
	}

	@Override
	public long marketSell ( final String instrument ,final  int quantity , final String orderId ) {
		return insertOrder ( instrument , FixedPrice.ZERO , quantity , Order.Side.SELL , orderId , Order.OrderType.MARKET );
	}

	@Override
	public long sell ( final String instrument , final FixedPrice price ,final  int quantity , final String orderId ) {
		return insertOrder ( instrument , price , quantity , Order.Side.SELL , orderId , Order.OrderType.LIMIT );
	}

	@Override
	public long marketBuy ( final String instrument ,final  int quantity , final String orderId ) {
		return insertOrder ( instrument , FixedPrice.MAX , quantity , Order.Side.BUY , orderId , Order.OrderType.MARKET );
	}

	@Override
	public long buy ( final String instrument ,final  FixedPrice price ,final  int quantity , final String orderId ) {
		return insertOrder ( instrument , price , quantity , Order.Side.BUY , orderId , Order.OrderType.LIMIT );
	}
}
