import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import dao.TestExchangeListener;
import org.junit.jupiter.api.Test;
import org.rogersf.core.FixedPrice;
import org.rogersf.core.Order;
import org.rogersf.listeners.ExchangeListener;
import org.rogersf.nolocking.NonLockingBroker;
import org.rogersf.nolocking.NonLockingExchange;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NonLockingExchangeTests {

	public static final int BROKER_COUNT = 10;
	public static final int RB_SIZE = 1024 * 16;

	@Test
	public void testNonLockingExchange () throws InterruptedException {

		ExecutorService executor = Executors.newFixedThreadPool ( 5 );// Runtime.getRuntime ().availableProcessors ()

		Disruptor < Order > disruptor = new Disruptor <> ( new EventFactory < Order > () {
			@Override
			public Order newInstance () {
				return new Order ( "" , "" , 0 , 0 , 0 , Order.Side.BUY , Order.OrderType.LIMIT );
			}
		} ,
				RB_SIZE ,
				executor ,
				ProducerType.MULTI ,
				new YieldingWaitStrategy () );

		List < ExchangeListener > listeners = new ArrayList <> ();
		TestExchangeListener listener = new TestExchangeListener ();
		listeners.add ( listener );
		NonLockingExchange testExchange = new NonLockingExchange ( listeners );

		disruptor.handleEventsWith ( testExchange );
		disruptor.start ();

		RingBuffer < Order > ringBuffer = disruptor.getRingBuffer ();

		long start = System.currentTimeMillis ();
		for ( int i = 0 ; i < BROKER_COUNT ; i++ ) {
			final int index = i;
			new Thread ( new Runnable () {
				@Override
				public void run () {
					NonLockingBroker b = new NonLockingBroker ( ringBuffer , "Broker" + index );
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
//		ExecutorService executorService = Executors.newFixedThreadPool ( 10 );
//		CountDownLatch latch = new CountDownLatch ( 10 );
//		// Submit 10 tasks to the thread pool
//		for ( int i = 0 ; i < 10 ; i++ ) {
//			final int index = i;
//			Runnable task = () -> {
//				System.out.println ( "Task " + index + " started" );
//				for ( int tens = 0 ; tens < 100 ; tens += 10 ) {
//					NonLockingBroker b = brokers.get ( index + tens );
//					for ( int orderC = 0 ; orderC < 10000 ; orderC += 10 ) {
//						b.buy ( "test" , new FixedPrice ( 100 + new Random ().nextDouble () * 10 ) , 100 , "testO" + index );
//					}
//				}
//				System.out.println ( "Task " + index + " completed" );
//				latch.countDown (); // Decrement the count of remaining tasks
//			};
//			executorService.submit ( task );
//		}
//		// Wait for all tasks to complete
//		latch.await ();
//		// Shut down the thread pool
//		executorService.shutdown ();
		while ( testExchange.nextOrderId () <= 100_000 ) {
			Thread.sleep ( 10 );
		}
		long end = System.currentTimeMillis ();
		System.out.println ( " 1M buy and sell was matched within " + ( end - start ) + " milli seconds in disruptor" );

		Thread.sleep ( 10000 );

	}

//	@Test
//	public void testNonLockingExchange2 () throws InterruptedException {
//		RingBuffer < Order > exchangeRing = RingBuffer.create ( ProducerType.MULTI ,
//				new EventFactory < Order > () {
//					@Override
//					public Order newInstance () {
//						return new Order ( "" , "" , 0 , 0 , 0 , Order.Side.BUY , Order.OrderType.LIMIT );
//					}
//				} ,
//				RB_SIZE ,
//				new YieldingWaitStrategy () );
//
//		SequenceBarrier barrier = exchangeRing.newBarrier ();
//
//		List < ExchangeListener > listeners = new ArrayList <> ();
//		TestExchangeListener listener = new TestExchangeListener ();
//		listeners.add ( listener );
//		NonLockingExchange testExchange = new NonLockingExchange ( listeners );
//
//		WorkerPool < Order > workerPool = new WorkerPool < Order > (
//				exchangeRing ,
//				barrier ,
//				new ExceptionHandler () {
//					@Override
//					public void handleEventException ( Throwable ex , long sequence , Object event ) {
//					}
//
//					@Override
//					public void handleOnStartException ( Throwable ex ) {
//					}
//
//					@Override
//					public void handleOnShutdownException ( Throwable ex ) {
//					}
//				} ,
//				testExchange );
//
//		exchangeRing.addGatingSequences ( workerPool.getWorkerSequences () );
//		workerPool.start ( Executors.newFixedThreadPool ( 5 ) );
//	}
}

