import dao.TestExchange;
import dao.TestExchangeListener;
import dao.TestOrder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rogersf.core.Book;
import org.rogersf.core.FixedPrice;
import org.rogersf.core.Order;
import org.rogersf.listeners.ExchangeListener;
import org.rogersf.listeners.OrderBookListenToExchangeListener;
import org.rogersf.listeners.OrderBookListener;
import org.rogersf.locking.OrderBook;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.rogersf.core.Order.Side.BUY;

public class PerformanceTest {
	public static final int N_ORDERS = 1000_000;

	@Test
	public void testProcessOrdersSingleThreaded () {
		System.out.println ( "testProcessOrdersSingleThreaded" );
		OrderBookListener lst = new OrderBookListenToExchangeListener ( new ArrayList <> () );
		List < OrderBookListener > listeners = new ArrayList < OrderBookListener > ();
		listeners.add ( lst );
		OrderBook ob = new OrderBook ( listeners , "test" );

		long start = System.currentTimeMillis ();
		for ( int i = 0 ; i < N_ORDERS ; i++ ) {
			ob.insertOrder ( new TestOrder ( i , 100.0 + 1 * ( i % 1000 ) , 100 , BUY ) );
		}

		for ( int i = 0 ; i < N_ORDERS ; i++ ) {
			ob.insertOrder ( new TestOrder ( N_ORDERS + i , 100.0 + 1 * ( i % 1000 ) , 100 , Order.Side.SELL ) );
		}

		long end = System.currentTimeMillis ();
		System.out.println ( " 1M single threaded buy and sell was matched within " + ( end - start ) + " milli seconds" );
		Book book = ob.book ();
		Assertions.assertTrue ( book.bids.size () != 0 );
	}

	/**
	 * tests the time to remove an order at a random position in the OrderBook
	 */
	@Test
	public void testCancelOrdersSingleThreaded () {
		System.out.println ( "testCancelOrdersSingleThreaded" );
		OrderBookListener lst = new OrderBookListenToExchangeListener ( new ArrayList <> () );
		List < OrderBookListener > listeners = new ArrayList <> ();
		listeners.add ( lst );
		OrderBook ob = new OrderBook ( listeners , "test" );
		List < String > output;
		Set < Order > orders = new HashSet <> ( N_ORDERS );

		for ( int i = 0 ; i < N_ORDERS ; i++ ) {
			Order order = new TestOrder ( i , 100.0 + 1 * ( i % 1000 ) , 10 , BUY );
			ob.insertOrder ( order );
			orders.add ( order );
		}

		long start = System.currentTimeMillis ();
		orders.forEach ( o -> {
			ob.cancelOrder ( o );
		} );
		long end = System.currentTimeMillis ();
		System.out.println ( " 1M single threaded buy  was cancelled within " + ( end - start ) + " milli seconds" );
		Book book = ob.book ();
		Assertions.assertTrue ( book.bids.size () == 0 );
	}

	@Test
	public void testExchangeOrdersSingleThreaded () throws InterruptedException {
		System.out.println ( "testExchangeOrdersSingleThreaded" );
		List < ExchangeListener > listeners = new ArrayList <> ();
		TestExchangeListener listener = new TestExchangeListener ();
		listeners.add ( listener );
		TestExchange testExchange = new TestExchange ( listeners );

		long start = System.currentTimeMillis ();
		for ( int j = 0 ; j < N_ORDERS ; ++ j ) {
			testExchange.buy ( new FixedPrice ( 100.0 + 1 * ( j % 1000 ) ) , 100 , "" );
		}

		for ( int j = 0 ; j < N_ORDERS ; ++ j ) {
			testExchange.sell ( new FixedPrice ( 100.0 + 1 * ( j % 1000 ) ) , 100 , "" );
		}
		long end = System.currentTimeMillis ();
		System.out.println ( " 1M single threaded buy and sell was matched within " + ( end - start ) + " milli seconds" );
	}

	@Test
	public void testExchangeOrders10Threaded () throws InterruptedException {
		System.out.println ( "testExchangeOrders10Threaded" );
		List < ExchangeListener > listeners = new ArrayList <> ();
		TestExchangeListener listener = new TestExchangeListener ();
		listeners.add ( listener );
		TestExchange testExchange = new TestExchange ( listeners );

		ExecutorService executorService = Executors.newFixedThreadPool ( 10 );
		CountDownLatch latch = new CountDownLatch ( 10 );
		long start = System.currentTimeMillis ();
		// Submit 10 tasks to the thread pool
		for ( int i = 0 ; i < 10 ; i++ ) {
			final int index = i;
			Runnable task = () -> {
				System.out.println ( "Task " + index + " started" );
				for ( int j = 0 ; j < N_ORDERS / 10 ; ++ j ) {
					testExchange.buy ( new FixedPrice ( 100.0 + 1 * ( j % 1000 ) ) , 100 , "" );
				}

				for ( int j = 0 ; j < N_ORDERS / 10 ; ++ j ) {
					testExchange.sell ( new FixedPrice ( 100.0 + 1 * ( j % 1000 ) ) , 100 , "" );
				}
				System.out.println ( "Task " + index + " completed" );
				latch.countDown (); // Decrement the count of remaining tasks
			};
			executorService.submit ( task );
		}

		// Wait for all tasks to complete
		latch.await ();
		// Shut down the thread pool
		executorService.shutdown ();
		long end = System.currentTimeMillis ();
		System.out.println ( " 1M single threaded buy and sell was matched within " + ( end - start ) + " milli seconds" );
	}

	@Test
	public void testExchangePartialFillCancelOrdersSingleThreaded () throws InterruptedException {
		System.out.println ( "testExchangePartialFillCancelOrdersSingleThreaded:" );
		List < ExchangeListener > listeners = new ArrayList <> ();
		TestExchangeListener listener = new TestExchangeListener ();
		listeners.add ( listener );
		TestExchange testExchange = new TestExchange ( listeners );
		long start = System.currentTimeMillis ();
		for ( int j = 0 ; j < N_ORDERS ; ++ j ) {
			testExchange.buy ( new FixedPrice ( 100.0 + 1 * ( j % 1000 ) ) , 100 , "" );
		}

		for ( int j = 0 ; j < N_ORDERS / 2 ; ++ j ) {
			testExchange.marketSell ( 100 , "" );
		}

		for ( int j = 0 ; j < N_ORDERS / 2 ; ++ j ) {
			long exchangeIdToCancel = testExchange.orderBook ( "test" ).book ().bidOrderIds.get ( j );
			testExchange.cancel ( exchangeIdToCancel );
		}
		long end = System.currentTimeMillis ();
		System.out.println ( " 1M single threaded buy and sell was matched within " + ( end - start ) + " milli seconds" );
	}

	// 1M > 30 min
	@Test
	public void testExchangePartialFillCancelOrders10Threaded () throws InterruptedException {
		System.out.println ( "testExchangePartialFillCancelOrders10Threaded:" );
		List < ExchangeListener > listeners = new ArrayList <> ();
		TestExchangeListener listener = new TestExchangeListener ();
		listeners.add ( listener );
		TestExchange testExchange = new TestExchange ( listeners );

		ExecutorService executorService = Executors.newFixedThreadPool ( 10 );
		CountDownLatch latch = new CountDownLatch ( 10 );
		long start = System.currentTimeMillis ();
		// Submit 10 tasks to the thread pool
		for ( int i = 0 ; i < 10 ; i++ ) {
			final int index = i;
			Runnable task = () -> {
				System.out.println ( "Task " + index + " started" );
				for ( int j = 0 ; j < N_ORDERS / 10 ; ++ j ) {
					testExchange.buy ( new FixedPrice ( 100.0 + 1 * ( j % 1000 ) ) , 100 , "" );
				}

				for ( int j = 0 ; j < N_ORDERS / 20 ; ++ j ) {
					testExchange.marketSell ( 100 , "" );
				}

				for ( int j = 0 ; j < N_ORDERS / 20 ; ++ j ) {
					long exchangeIdToCancel = testExchange.orderBook ( "test" ).book ().bidOrderIds.get ( j );
					testExchange.cancel ( exchangeIdToCancel );
				}
				System.out.println ( "Task " + index + " completed" );
				latch.countDown (); // Decrement the count of remaining tasks
			};
			executorService.submit ( task );
		}
		// Wait for all tasks to complete
		latch.await ();
		// Shut down the thread pool
		executorService.shutdown ();
		long end = System.currentTimeMillis ();
		System.out.println ( " 1M single threaded buy and sell then cancel was matched within " + ( end - start ) + " milli seconds" );
	}
}
