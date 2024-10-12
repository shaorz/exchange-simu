package org.rogersf.core;

public class BookLevel {
	public final FixedPrice price;
	public final int quantity;

	public BookLevel ( double price , int quantity ) {
		this.price = new FixedPrice ( price );
		this.quantity = quantity;
	}

	public BookLevel ( FixedPrice price , int quantity ) {
		this.price = price;
		this.quantity = quantity;
	}
}
