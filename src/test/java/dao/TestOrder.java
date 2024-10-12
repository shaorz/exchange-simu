package dao;

import org.rogersf.core.Order;

public class TestOrder extends Order {
	public TestOrder ( long id , double price , int quantity , Side side ) {
		super ( "" , "test" , id , price , quantity , side , OrderType.LIMIT );
	}

	public TestOrder ( long id , double price , int quantity , Side side , OrderType t ) {
		super ( "" , "test" , id , price , quantity , side , t );
	}
}
