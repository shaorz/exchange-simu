package org.rogersf.core;

import org.rogersf.locking.OrderList;

import java.util.Comparator;
import java.util.NavigableMap;

public class BasePriceLevels {
	// Create a comparator that reverses the natural ordering of integers
	protected static Comparator < FixedPrice > priceComparator ( boolean ascending ) {
		if ( ascending ) {
			return new Comparator <> () {
				@Override
				public int compare ( FixedPrice o1 , FixedPrice o2 ) {
					return ( int ) ( o1.doubleValue () - o2.doubleValue () );
				}
			};
		} else {
			return new Comparator <> () {
				@Override
				public int compare ( FixedPrice o1 , FixedPrice o2 ) {
					return ( int ) ( o2.doubleValue () - o1.doubleValue () );
				}
			};
		}
	}

	public void inserOrder ( Order order ) {
		if ( ! levels.containsKey ( order.price ) )
			levels.put ( order.price , new OrderList () );
		levels.get ( order.price ).pushback ( order );
	}

	public void removeOrder ( Order order ) {
		if ( ! levels.containsKey ( order.price ) ) {
			throw new IllegalArgumentException ( "Order does not exist: " + order.orderId + " on PriceLevel list @" + order.price );
		}
		levels.get ( order.price ).remove ( order );
		if ( levels.get ( order.price ).front () == null )
			levels.remove ( order.price );
	}

	private final boolean ascending;
	public NavigableMap < FixedPrice, OrderList > levels;

	public BasePriceLevels ( boolean ascending ) {
		this.ascending = ascending;
	}

	public boolean empty () {
		return levels.isEmpty ();
	}

	public Order front () {
		return levels.get ( levels.firstKey () ).front ();
	}

	public int size () {
		return levels.size ();
	}
}
