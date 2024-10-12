import dao.TestOrder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rogersf.core.Order;
import org.rogersf.locking.OrderList;

public class OrderListTests {
	@Test
	public void TestOrderList () {
		OrderList list = new OrderList ();
		Assertions.assertNull ( list.front () );
		Order o1 = new TestOrder ( 1 , 101.888 , 10 , Order.Side.BUY );
		Order o2 = new TestOrder ( 2 , 101.888 , 10 , Order.Side.BUY );
		list.pushback ( o1 );
		list.pushback ( o2 );
		Assertions.assertTrue ( list.front () == o1 );
		list.remove ( o1 );
		Assertions.assertTrue ( list.front () == o2 );
		list.remove ( o2 );
		Assertions.assertNull ( list.front () );
	}

	@Test
	public void TestOrderListIterator () {
		OrderList list = new OrderList ();
		Assertions.assertTrue ( list.begin ().equals ( list.end () ) );
		Order o1 = new TestOrder ( 1 , 101.888 , 10 , Order.Side.BUY );
		Order o2 = new TestOrder ( 2 , 101.888 , 10 , Order.Side.BUY );
		list.pushback ( o1 );
		Assertions.assertTrue ( ! list.begin ().equals ( list.end () ) );
		Assertions.assertTrue ( list.begin ().current.order.equals ( o1 ) );
		list.pushback ( o2 );
		Assertions.assertTrue ( list.begin ().current.order.equals ( o1 ) );

		OrderList.Iterator itr = list.begin ();
		itr.next ();
		Assertions.assertTrue ( itr.current.order.equals ( o2 ) );
		itr.next ();
		Assertions.assertTrue ( itr.equals ( list.end () ) );

	}
}
