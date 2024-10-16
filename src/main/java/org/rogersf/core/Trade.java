package org.rogersf.core;

public class Trade {
	public final FixedPrice price;
	public final int quantity;
	public Order aggressor;
	public Order opposite;

	public Trade ( final Double price , final int quantity , final Order aggressor , final Order opposite ) {
		this.price = new FixedPrice ( price );
		this.quantity = quantity;
		this.aggressor = aggressor;
		this.opposite = opposite;
		System.out.println ( "trade has been  made @ " + price );
	}

	@Override
	public String toString () {
		return "Trade{" +
				"price=" + price +
				", quantity=" + quantity +
				", aggressor=" + aggressor.exchangeId +
				", opposite=" + opposite.exchangeId +
				'}';
	}
}