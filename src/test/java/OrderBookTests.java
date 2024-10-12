import dao.TestOrder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rogersf.core.Book;
import org.rogersf.core.FixedPrice;
import org.rogersf.core.Order;
import org.rogersf.locking.OrderBook;

import java.util.ArrayList;


public class OrderBookTests {

	@Test
	public void TestOrderBookCancel () {
		OrderBook ob = new OrderBook ( new ArrayList <> () , "test" );

		Order test1 = new TestOrder ( 1 , 101.888 , 10 , Order.Side.BUY );
		Order test2 = new TestOrder ( 1 , 101.88800001 , 10 , Order.Side.BUY );
		Order test3 = new TestOrder ( 1 , 91.888 , 10 , Order.Side.BUY );
		Order test4 = new TestOrder ( 1 , 81.888 , 10 , Order.Side.BUY );

		ob.insertOrder ( test1 );
		Book b = ob.book ();
		Assertions.assertTrue ( b.bids.get ( 0 ).price.equals ( new FixedPrice ( "101.88800001" ) ) );
		Assertions.assertTrue ( b.bids.get ( 0 ).quantity == 10 );

		ob.cancelOrder ( test1 );

		b = ob.book ();
		Assertions.assertTrue ( b.bids.size () == 0 );

		ob.insertOrder ( test2 );
		ob.insertOrder ( test3 );
		ob.insertOrder ( test4 );
		b = ob.book ();
		Assertions.assertTrue ( b.bids.get ( 0 ).price.equals ( new FixedPrice ( "101.88800001" ) ) );
		Assertions.assertTrue ( b.bids.get ( 0 ).quantity == 10 );

		ob.cancelOrder ( test3 );
		b = ob.book ();
		Assertions.assertTrue ( b.bids.size () == 2 );
		Assertions.assertTrue ( b.bids.get ( 0 ).price.equals ( new FixedPrice ( "101.88800002" ) ) );
		Assertions.assertTrue ( b.bids.get ( 1 ).price.equals ( new FixedPrice ( "81.88800003" ) ) );
	}

	@Test
	public void TestMultiOrderBookLevels () {
		OrderBook ob = new OrderBook ( new ArrayList <> () , "test" );
		Order test1 = new TestOrder ( 1 , 101.888 , 10 , Order.Side.BUY );
		Order test2 = new TestOrder ( 2 , 101.88800001 , 10 , Order.Side.BUY );
		Order test3 = new TestOrder ( 2 , 91.888 , 10 , Order.Side.BUY );
		Order test4 = new TestOrder ( 3 , 81.888 , 10 , Order.Side.BUY );
		ob.insertOrder ( test1 );
		ob.insertOrder ( test2 );
		ob.insertOrder ( test3 );
		ob.insertOrder ( test4 );
		Book b = ob.book ();
		System.out.println ( "MAX Price: " + FixedPrice.MAX );

		Assertions.assertTrue ( b.bids.size () == 3 );
		Assertions.assertTrue ( b.bids.get ( 0 ).price.equals ( new FixedPrice ( "101.88800001" ) ) );
		Assertions.assertTrue ( b.bids.get ( 0 ).quantity == 20 );
		Assertions.assertTrue ( b.bids.get ( 1 ).price.equals ( new FixedPrice ( "91.88800002" ) ) );
		Assertions.assertTrue ( b.bids.get ( 1 ).quantity == 10 );
		Assertions.assertTrue ( b.bids.get ( 2 ).price.equals ( new FixedPrice ( "81.88800003" ) ) );
		Assertions.assertTrue ( b.bids.get ( 2 ).quantity == 10 );
	}

	@Test
	public void TestMatchOrderBookLevels () {
		OrderBook ob = new OrderBook ( new ArrayList <> () , "test" );
		Order test1 = new TestOrder ( 1 , 101.888 , 10 , Order.Side.BUY );
		Order test2 = new TestOrder ( 2 , 101.888 , 10 , Order.Side.SELL );
		Order test3 = new TestOrder ( 2 , 91.888 , 15 , Order.Side.BUY );
		Order test4 = new TestOrder ( 3 , 81.888 , 10 , Order.Side.SELL );
		ob.insertOrder ( test1 );
		ob.insertOrder ( test2 );
		Book b = ob.book ();
		Assertions.assertTrue ( b.bids.size () == 0 );
		Assertions.assertTrue ( b.asks.size () == 0 );
		ob.insertOrder ( test3 );
		ob.insertOrder ( test4 );
		b = ob.book ();

		Assertions.assertTrue ( b.bids.size () == 1 );
		Assertions.assertTrue ( b.bids.get ( 0 ).price.equals ( new FixedPrice ( "91.88800001" ) ) );
		Assertions.assertTrue ( b.bids.get ( 0 ).quantity == 5 );
		Order test5 = new TestOrder ( 3 , 81.888 , 10 , Order.Side.SELL , Order.OrderType.MARKET );
		ob.insertOrder ( test5 );
		b = ob.book ();
		Assertions.assertTrue ( b.bids.size () == 0 );
		Assertions.assertTrue ( b.asks.size () == 0 );
	}
}
