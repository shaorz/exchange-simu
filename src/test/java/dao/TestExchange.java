package dao;

import org.rogersf.core.Book;
import org.rogersf.core.FixedPrice;
import org.rogersf.listeners.ExchangeListener;
import org.rogersf.locking.Exchange;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class TestExchange extends Exchange {
	public TestExchange () {
	}

	public TestExchange ( List < ExchangeListener > ls ) {
		super ( ls , new ArrayBlockingQueue <> ( 10 ) );
	}

	public long buy ( FixedPrice price , int quantity , String orderId ) {
		return super.buy ( "test" , price , quantity , orderId );
	}

	public long marketBuy ( int quantity , String orderId ) {
		return super.marketBuy ( "test" , quantity , orderId );
	}

	public long sell ( FixedPrice price , int quantity , String orderId ) {
		return super.sell ( "test" , price , quantity , orderId );
	}

	public long marketSell ( int quantity , String orderId ) {
		return super.marketSell ( "test" , quantity , orderId );
	}

	public Book book () {
		return super.book ( "test" );
	}

	public int bidCount () {
		return book ().bids.size ();
	}

	public int askCount () {
		return book ().asks.size ();
	}
//	public int bidIndex( long exchangeId ){
//		Book book = book();
//		int index = 0;
//
//		book.bidOrderIds.forEach ( id -> {
//			if ( exchangeId == id ) {
//				break;
//			}
//		} );
//		return
//	}
}
