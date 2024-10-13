package org.rogersf.core;

public interface IBroker {
	/**
	 * submit limit buy order. returns exchange order id
	 */
	long buy ( final String instrument , final FixedPrice price , final int quantity , final String orderId );

	/**
	 * submit market buy order. returns exchange order id. If the order cannot be filled, the rest is cancelled
	 */
	long marketBuy ( final String instrument , final int quantity , final String orderId );

	/**
	 * submit limit sell order. returns exchange order id
	 */
	long sell ( final String instrument , final FixedPrice price , final int quantity , final String orderId );

	/**
	 * submit market sell order. returns exchange order id. If the order cannot be filled, the rest is cancelled
	 */
	long marketSell ( final String instrument , final int quantity ,final  String orderId );

	int cancel ( final long exchangeId );

	long insertOrder ( final String ticker , final FixedPrice price , final int quantity , final Order.Side side , final String orderId , final Order.OrderType type ) ;
}
