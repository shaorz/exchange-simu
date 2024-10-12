package org.rogersf.core;

public interface IBroker {
	/**
	 * submit limit buy order. returns exchange order id
	 */
	long buy ( String instrument , FixedPrice price , int quantity , String orderId );

	/**
	 * submit market buy order. returns exchange order id. If the order cannot be filled, the rest is cancelled
	 */
	long marketBuy ( String instrument , int quantity , String orderId );

	/**
	 * submit limit sell order. returns exchange order id
	 */
	long sell ( String instrument , FixedPrice price , int quantity , String orderId );

	/**
	 * submit market sell order. returns exchange order id. If the order cannot be filled, the rest is cancelled
	 */
	long marketSell ( String instrument , int quantity , String orderId );

	int cancel ( long exchangeId );
}
