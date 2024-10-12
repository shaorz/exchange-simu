package dao;

import org.rogersf.core.Order;
import org.rogersf.core.Trade;
import org.rogersf.listeners.ExchangeListener;

import java.util.ArrayList;

public class TestExchangeListener implements ExchangeListener {
	public ArrayList < Trade > trades;
	public ArrayList < Order > orders;

	public TestExchangeListener () {
		trades = new ArrayList <> ();
		orders = new ArrayList <> ();
	}

	@Override
	public void onOrder ( final Order order ) {
		Order copy = new Order ( order.orderId , order.ticker , order.exchangeId , order.price , order.quantity , order.side , order.orderType );
		copy.remaining = order.remaining;
		copy.filled = order.filled;
		orders.add ( copy );
	}

	@Override
	public void onTrade ( final Trade t ) {
		trades.add ( t );
	}
}
