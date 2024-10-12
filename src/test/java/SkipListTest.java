import org.junit.jupiter.api.Test;
import org.rogersf.ds.SkipList;

public class SkipListTest {

	@Test
	public void test () {
		SkipList < Integer > list = null;
		int trail = 0;
		while ( true ) {
			list = new SkipList <> ();
//			System.out.println ( list );
			list.put ( 4 );
			list.put ( 5 );
			list.put ( 3 );
			list.put ( 6 );
			list.put ( 5 );
			list.put ( 4 );
			list.put ( 2 );
			list.put ( 5 );
			list.put ( 1 );
			trail += 1;
//			System.out.println ( list );
			if ( list.listLevel >= 5 )
				break;
		}
		System.out.println ( "after " + trail + " trails:" );
		System.out.println ( list.size () );
	}
}
