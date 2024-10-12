package org.rogersf.locking;

import org.rogersf.core.Book;
import org.rogersf.core.FixedPrice;
import org.rogersf.core.Order;
import org.rogersf.listeners.ExchangeListener;
import org.rogersf.listeners.OrderBookListenToExchangeListener;
import org.rogersf.listeners.OrderBookListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class Exchange {
	public ConcurrentHashMap < String, OrderBook > books;
	public ConcurrentSkipListMap < Long, Order > allOrders;
	private List < ExchangeListener > listeners;
	private static long id = 0;

	public Exchange () {
		this ( new ArrayList <> () );
	}

	public Exchange ( List < ExchangeListener > listeners ) {
		this.listeners = listeners;
		books = new ConcurrentHashMap <> ();
		allOrders = new ConcurrentSkipListMap <> ();
	}

	private long nextOrderId () {
		id += 1;
		return id;
	}

	private synchronized long insertOrder ( String ticker , FixedPrice price , int quantity , Order.Side side , String orderId , Order.OrderType type ) {
		OrderBook orderBook;
		long id = nextOrderId ();
		if ( ! books.containsKey ( ticker ) ) {
			OrderBookListenToExchangeListener listener = new OrderBookListenToExchangeListener ( listeners );
			List < OrderBookListener > listeners = new ArrayList <> ();
			listeners.add ( listener );
			orderBook = new OrderBook ( listeners , ticker );
			books.put ( ticker , orderBook );
		}
		orderBook = books.get ( ticker );
		Order order = new Order ( orderId , ticker , id , price , quantity , side , type );
		allOrders.put ( id , order );
		orderBook.insertOrder ( order );
		return id;
	}

	/**
	 * submit limit buy order. returns exchange order id
	 */
	public long buy ( String instrument , FixedPrice price , int quantity , String orderId ) {
		return insertOrder ( instrument , price , quantity , Order.Side.BUY , orderId , Order.OrderType.LIMIT );
	}

	/**
	 * submit market buy order. returns exchange order id. If the order cannot be filled, the rest is cancelled
	 */
	public long marketBuy ( String instrument , int quantity , String orderId ) {
		return insertOrder ( instrument , FixedPrice.MAX , quantity , Order.Side.BUY , orderId , Order.OrderType.MARKET );
	}

	/**
	 * submit limit sell order. returns exchange order id
	 */
	public long sell ( String instrument , FixedPrice price , int quantity , String orderId ) {
		return insertOrder ( instrument , price , quantity , Order.Side.SELL , orderId , Order.OrderType.LIMIT );
	}

	/**
	 * submit market sell order. returns exchange order id. If the order cannot be filled, the rest is cancelled
	 */
	public long marketSell ( String instrument , int quantity , String orderId ) {
		return insertOrder ( instrument , FixedPrice.ZERO , quantity , Order.Side.SELL , orderId , Order.OrderType.MARKET );
	}

	public synchronized int cancel ( long exchangeId ) {
		if ( ! allOrders.containsKey ( exchangeId ) ) {
			throw new IllegalArgumentException ( "Unknown exchange id " + exchangeId );
		}
		Order order = allOrders.get ( exchangeId );
		OrderBook book = books.get ( order.ticker );
		if ( book == null ) {
			throw new IllegalArgumentException ( "Unknown ticker " + order.ticker + " in order books" );
		}
		return book.cancelOrder ( order );
	}

	public synchronized Book book ( String instrument ) {
		if ( ! books.containsKey ( instrument ) ) {
			throw new IllegalArgumentException ( "Unknown order id with ticker " + instrument );
		}
		OrderBook book = books.get ( instrument );
		return book.book ();
	}

	public synchronized Order getOrder ( long exchangeId ) {
		if ( ! allOrders.containsKey ( exchangeId ) ) {
			throw new IllegalArgumentException ( "Unknown exchange id " + exchangeId );
		}
		Order order = allOrders.get ( exchangeId );
		OrderBook book = books.get ( order.ticker );
		if ( book == null ) {
			throw new IllegalArgumentException ( "Unknown ticker " + order.ticker + " in order books" );
		}
		return order;
	}

	public synchronized OrderBook orderBook ( String ticker ) {
		if ( ! books.containsKey ( ticker ) )
			throw new RuntimeException ( ticker + " not found in the exchange order book" );
		return books.get ( ticker );
	}
}
