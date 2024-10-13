package org.rogersf.locking;

import org.rogersf.core.Book;
import org.rogersf.core.BookLevel;
import org.rogersf.core.Order;
import org.rogersf.core.Trade;
import org.rogersf.listeners.OrderBookListener;

import java.util.ArrayList;
import java.util.List;

public class OrderBook {
	private final PriceLevels bids;
	private final PriceLevels asks;
	private final List < OrderBookListener > listener;
	private final String ticker;

	private void matchOrders ( Order.Side aggressorSide ) {
		while ( ! bids.empty () && ! asks.empty () ) {
			Order bestBid = bids.front ();
			Order bestAsk = asks.front ();

			if ( bestBid.price.doubleValue () >= bestAsk.price.doubleValue () ) {
				int qty = Math.min ( bestBid.remainingQuantity () , bestAsk.remainingQuantity () );
				double price = Math.min ( bestBid.price.doubleValue () , bestAsk.price.doubleValue () );

				Order aggressor = aggressorSide == Order.Side.BUY ? bestBid : bestAsk;
				Order opposite = aggressorSide == Order.Side.BUY ? bestAsk : bestBid;

				bestBid.fill ( qty );
				bestAsk.fill ( qty );
				Trade t = new Trade ( price , qty , aggressor , opposite );


				if ( bestBid.remainingQuantity () == 0 )
					bids.removeOrder ( bestBid );

				if ( bestAsk.remainingQuantity () == 0 )
					asks.removeOrder ( bestAsk );

				listener.forEach ( l -> {
					l.onOrder ( bestBid );
					l.onOrder ( bestAsk );
					l.onTrade ( t );
				} );
			} else {
				break;
			}
		}

		PriceLevels orders = aggressorSide == Order.Side.BUY ? bids : asks;
		if ( ! orders.empty () ) {
			Order o = orders.front ();
			if ( o.isMarket () ) {
				o.cancel ();
				orders.removeOrder ( o );
				listener.forEach ( l -> {
					l.onOrder ( o );
				} );
			}
		}
	}

	public OrderBook ( final List < OrderBookListener > listener , final String ticker ) {
		this.listener = listener;
		bids = new PriceLevels ( false );
		asks = new PriceLevels ( true );
		this.ticker = ticker;
	}

	public void insertOrder ( Order order ) {
		if ( ! order.ticker.equals ( this.ticker ) ) {
			return;
		}
		if ( order.side == Order.Side.BUY ) {
			bids.inserOrder ( order );
		} else {
			asks.inserOrder ( order );
		}
		listener.forEach ( l -> l.onOrder ( order ) );
		matchOrders ( order.side );
	}

	public int cancelOrder ( Order order ) {
		if ( ! order.ticker.equals ( this.ticker ) ) {
			return - 1;
		}
		if ( order.remainingQuantity () > 0 ) {
			order.cancel ();
			if ( order.side == Order.Side.BUY ) {
				bids.removeOrder ( order );
			} else {
				asks.removeOrder ( order );
			}
			listener.forEach ( l -> l.onOrder ( order ) );
			return 0;
		} else {
			return - 1;
		}
	}

	public Book book () {
		Book ret = new Book ();
		ret.bids = new ArrayList <> ( bids.size () );
		ret.asks = new ArrayList <> ( asks.size () );
		snap ( bids , ret.bids , ret.bidOrderIds );
		snap ( asks , ret.asks , ret.askOrderIds );
		return ret;
	}

	private void snap ( final PriceLevels src , List < BookLevel > dest , List < Long > orderIds ) {
		src.levels.forEach ( ( p , orderList ) -> {
			int qty = 0;
			OrderList.Iterator itr = orderList.begin ();
			while ( ! itr.equals ( orderList.end () ) ) {
				qty = qty + itr.current.order.remainingQuantity ();
				orderIds.add ( itr.current.order.exchangeId );
				itr.next ();
			}
			dest.add ( new BookLevel ( p , qty ) );
		} );
	}
}
