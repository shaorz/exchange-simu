import dao.TestExchange;
import dao.TestExchangeListener;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rogersf.core.FixedPrice;
import org.rogersf.listeners.ExchangeListener;

import java.util.ArrayList;
import java.util.List;

public class ExchangeTests {

	@Test
	public void testInsertOrderBuy () {
		TestExchange testExchange = new TestExchange ();
		testExchange.buy ( new FixedPrice ( 0.99 ) , 100 , "1" );
		testExchange.buy ( new FixedPrice ( 2.99 ) , 100 , "1" );
		Assertions.assertTrue ( testExchange.bidCount () == 2 );
		Assertions.assertTrue ( testExchange.askCount () == 0 );

		testExchange.buy ( new FixedPrice ( 0.99 ) , 100 , "1" );
		testExchange.buy ( new FixedPrice ( 0.99 ) , 200 , "1" );
		testExchange.buy ( new FixedPrice ( 2.99 ) , 100 , "1" );
		Assertions.assertTrue ( testExchange.bidCount () == 2 );
		Assertions.assertTrue ( testExchange.askCount () == 0 );
	}

	@Test
	public void testInsertOrderSell () {
		TestExchange testExchange = new TestExchange ();
		testExchange.sell ( new FixedPrice ( 0.99 ) , 100 , "1" );
		testExchange.sell ( new FixedPrice ( 2.99 ) , 100 , "1" );
		Assertions.assertTrue ( testExchange.bidCount () == 0 );
		Assertions.assertTrue ( testExchange.askCount () == 2 );

		testExchange.sell ( new FixedPrice ( 0.99 ) , 100 , "1" );
		testExchange.sell ( new FixedPrice ( 0.99 ) , 200 , "1" );
		testExchange.sell ( new FixedPrice ( 2.99 ) , 100 , "1" );
		Assertions.assertTrue ( testExchange.bidCount () == 0 );
		Assertions.assertTrue ( testExchange.askCount () == 2 );
		Assertions.assertTrue ( testExchange.allOrders.size () == 5 );
		Assertions.assertTrue ( testExchange.books.size () == 1 );
		Assertions.assertTrue ( testExchange.books.containsKey ( "test" ) );
		Assertions.assertTrue ( testExchange.books.get ( "test" ).book ().bids.size () == 0 );
		Assertions.assertTrue ( testExchange.books.get ( "test" ).book ().asks.size () == 2 );
	}

	@Test
	public void testMarketOrderFill () {
		List < ExchangeListener > listeners = new ArrayList <> ();
		TestExchangeListener listener = new TestExchangeListener ();
		listeners.add ( listener );
		TestExchange testExchange = new TestExchange ( listeners );

		testExchange.buy ( new FixedPrice ( 0.99 ) , 100 , "1" );
		testExchange.buy ( new FixedPrice ( 2.99 ) , 100 , "1" );
		Assertions.assertTrue ( testExchange.bidCount () == 2 );
		Assertions.assertTrue ( testExchange.askCount () == 0 );

		testExchange.marketSell ( 400 , "1" );

		Assertions.assertTrue ( testExchange.bidCount () == 0 );
		Assertions.assertTrue ( testExchange.askCount () == 0 );
		Assertions.assertTrue ( testExchange.allOrders.size () == 3 );

		Assertions.assertTrue ( listener.trades.size () == 2 );
		Assertions.assertTrue ( listener.orders.size () == 8 );
//		Assertions.assertTrue ( testExchange.books.containsKey ( "test" ) );
//		Assertions.assertTrue ( testExchange.books.get ( "test" ).book ().bids.size () == 0 );
//		Assertions.assertTrue ( testExchange.books.get ( "test" ).book ().asks.size () == 2 );
	}


}
