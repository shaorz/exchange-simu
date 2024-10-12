package org.rogersf.core;

public class Order {
	public enum Side {
		BUY, SELL
	}

	public enum OrderType {
		MARKET, LIMIT
	}

	public long timeSubmitted;
	public int remaining;
	public int filled = 0;

	public String orderId;
	public String ticker;
	public long exchangeId;
	public FixedPrice price;
	public int quantity;
	public Side side;
	public OrderType orderType;

	public Order ( final String orderId , final String ticker , final long exchangeId , final double price , final int quantity , final Side side , final OrderType orderType ) {
		this ( orderId , ticker , exchangeId , new FixedPrice ( price ) , quantity , side , orderType );
	}

	public Order ( final String orderId , final String ticker , final long exchangeId , final FixedPrice price , final int quantity , final Side side , final OrderType orderType ) {
		this.orderId = orderId;
		this.ticker = ticker;
		this.exchangeId = exchangeId;
		this.price = price;
		this.quantity = quantity;
		this.side = side;
		this.orderType = orderType;
		this.timeSubmitted = System.currentTimeMillis ();
		this.remaining = quantity;
	}

	public void fill ( final int quantity ) {
		if ( remaining >= quantity && filled + quantity <= this.quantity ) {
			filled += quantity;
			remaining -= quantity;
		} else {
			throw new RuntimeException ( "Won't over-fill " + quantity + " in order " + orderId );
		}
	}

	public void cancel () {
		remaining = 0;
	}

	public boolean isMarket () {
		return orderType == OrderType.MARKET;
	}

	public int remainingQuantity () {
		return remaining;
	}

	public int filledQuantity () {
		return filled;
	}

	public boolean isCancelled () {
		return remaining == 0 && filled != this.quantity;
	}

	public boolean isFilled () {
		return remaining == 0 && filled == this.quantity;
	}

	public boolean isPartiallyFilled () {
		return filled > 0;
	}

	public boolean isActive () {
		return remaining > 0;
	}
}
