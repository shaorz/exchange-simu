package org.rogersf.nolocking;

import com.lmax.disruptor.EventHandler;
import org.rogersf.core.Book;
import org.rogersf.core.FixedPrice;
import org.rogersf.core.Order;
import org.rogersf.listeners.ExchangeListener;
import org.rogersf.listeners.OrderBookListenToExchangeListener;
import org.rogersf.listeners.OrderBookListener;
import org.rogersf.locking.OrderBook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

public class NonLockingExchange implements EventHandler < Order > {

	public HashMap < String, OrderBook > books;
	public TreeMap < Long, Order > allOrders;
	private List < ExchangeListener > listeners;
	private static AtomicLong id = new AtomicLong ( 0 );

	public NonLockingExchange () {
		this ( new ArrayList <> () );
	}

	public NonLockingExchange ( List < ExchangeListener > listeners ) {
		this.listeners = listeners;
		books = new HashMap <> ();
		allOrders = new TreeMap <> ();
	}

	@Override
	public void onEvent ( Order event , long sequence , boolean endOfBatch ) throws Exception {
		insertOrder ( event.ticker , event.price , event.quantity , event.side , event.orderId , event.orderType );
		System.out.println ( sequence + " order is processed" );
	}

	public long nextOrderId () {
		return id.get ();
	}

	private long insertOrder ( String ticker , FixedPrice price , int quantity , Order.Side side , String orderId , Order.OrderType type ) {
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

	public int cancel ( long exchangeId ) {
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

	public Book book ( String instrument ) {
		if ( ! books.containsKey ( instrument ) ) {
			throw new IllegalArgumentException ( "Unknown order id with ticker " + instrument );
		}
		OrderBook book = books.get ( instrument );
		return book.book ();
	}

	public Order getOrder ( long exchangeId ) {
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

	protected OrderBook orderBook ( String ticker ) {
		if ( ! books.containsKey ( ticker ) )
			throw new RuntimeException ( ticker + " not found in the exchange order book" );
		return books.get ( ticker );
	}

}
