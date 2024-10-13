import dao.TestExchange;
import dao.TestExchangeListener;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rogersf.core.FixedPrice;
import org.rogersf.core.Order;
import org.rogersf.listeners.ExchangeListener;
import org.rogersf.locking.Broker;
import org.rogersf.locking.Exchange;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

import static java.lang.Thread.sleep;

public class ExchangeTests {

	@Test
	public void testLockingExchange () throws InterruptedException {

		ArrayBlockingQueue < Order > incomingOrders = new ArrayBlockingQueue <> ( NonLockingExchangeTests.RB_SIZE );

		for ( int i = 0 ; i < NonLockingExchangeTests.BROKER_COUNT ; i++ ) {
			final int index = i;
			new Thread ( new Runnable () {
				@Override
				public void run () {
					Broker b = new Broker ( incomingOrders , "Broker" + index );
					if ( index % 2 == 0 ) {
						for ( int orderC = 0 ; orderC < 5_000 ; orderC += 10 ) {
							b.buy ( "test" , new FixedPrice ( 100 + new Random ().nextDouble () * 10 ) , 100 , "testOrder" + index );
						}
						for ( int orderC = 0 ; orderC < 5_000 ; orderC += 10 ) {
							b.marketSell ( "test" , 100 , "testSellMarketOrder" + index );
						}
					} else {
						for ( int orderC = 0 ; orderC < 5_000 ; orderC += 10 ) {
							b.marketSell ( "test" , 100 , "testSellMarketOrder" + index );
						}
						for ( int orderC = 0 ; orderC < 5_000 ; orderC += 10 ) {
							b.buy ( "test" , new FixedPrice ( 100 + new Random ().nextDouble () * 10 ) , 100 , "testOrder" + index );
						}
					}
				}
			} ).start ();
		}

		List < ExchangeListener > listeners = new ArrayList <> ();
		TestExchangeListener listener = new TestExchangeListener ();
		listeners.add ( listener );
		Exchange testExchange = new Exchange ( listeners , incomingOrders );
		testExchange.start ();
		long start = System.currentTimeMillis ();
		while ( testExchange.nextOrderId () <= 100_000 )
			sleep ( 10 );
		long end = System.currentTimeMillis ();
		System.out.println ( " 1M buy and sell was matched within " + ( end - start ) + " milli seconds in a blocking queue" );

		sleep ( 10_000 );
	}

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
