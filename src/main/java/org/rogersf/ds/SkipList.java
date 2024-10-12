package org.rogersf.ds;

import java.util.Random;

public class SkipList < K extends Comparable < K > > {
	private SkipListNode < K > head, tail;
	private int maxLevel;
	public int listLevel;
	private Random rand;
	private int nodes;
	private static final double PROB = 0.5;

	public SkipList () {
		rand = new Random ();
		clear ();
	}

	private SkipListNode < K > findNode ( K key ) {
		SkipListNode < K > p = head;
		while ( true ) {
			while ( p.right.value != null && p.right.value.compareTo ( key ) <= 0 ) {
				p = p.right;
			}

			if ( p.down != null ) {
				p = p.down;
			} else {
				break;
			}
		}
		return p;
	}

	public SkipListNode < K > search ( K k ) {
		SkipListNode < K > p = findNode ( k );
		if ( p.value.compareTo ( k ) == 0 )
			return p;
		return null;
	}

	public void put ( K value ) {
		SkipListNode < K > toAdd = new SkipListNode <> ( value );
		SkipListNode < K > prev = findNode ( value );
		backLink ( prev , toAdd );
		int curLevel = 0;
		while ( rand.nextDouble () < PROB ) {
			// if we need to create a new top level
			if ( curLevel >= listLevel ) {
				listLevel += 1;
				SkipListNode < K > levelH = new SkipListNode <> ( null );
				SkipListNode < K > levelT = new SkipListNode <> ( null );
				horizontailLink ( levelH , levelT );
				verticalLink ( levelH , head );
				verticalLink ( levelT , tail );
				head = levelH;
				tail = levelT;
			}
			while ( prev.up == null ) {
				prev = prev.left;
			}
			prev = prev.up;

			SkipListNode < K > e = new SkipListNode <> ( null );
			backLink ( prev , e );
			verticalLink ( e , toAdd );
			toAdd = e;
			curLevel += 1;
		}
		nodes += 1;
	}

	public void clear () {
		head = new SkipListNode <> ( null );
		tail = new SkipListNode <> ( null );
		listLevel = 0;
		nodes = 0;
		horizontailLink ( head , tail );
	}

	public boolean isEmpty () {
		return nodes == 0;
	}

	public int size () {
		return nodes;
	}

	private int randomLevel () {
		int lvl = 0;
		while ( lvl < maxLevel && rand.nextDouble () < PROB ) {
			lvl++;
		}
		return lvl;
	}

	private void horizontailLink ( SkipListNode < K > head , SkipListNode < K > tail ) {
		head.right = tail;
		tail.left = head;
	}

	private void verticalLink ( SkipListNode < K > head , SkipListNode < K > tail ) {
		head.down = tail;
		tail.up = head;
	}

	// insert Node 2 after Node 1
	private void backLink ( SkipListNode < K > head , SkipListNode < K > tail ) {
		tail.left = head;
		tail.right = head.right;
		head.right.left = tail;
		head.right = tail;
	}

	@Override
	public String toString () {

		if ( isEmpty () )
			return "Empty SkipList: []";
		StringBuilder sb = new StringBuilder ();
		SkipListNode < K > cur = head;

		while ( cur.down != null ) {
			cur = cur.down;
		}

		while ( cur.left != null ) {
			cur = cur.left;
		}

		if ( cur.right != null ) {
			cur = cur.right;
		}
		while ( cur.right != null ) {
			sb.append ( cur );
			sb.append ( "\n" );
			cur = cur.right;
		}

		return sb.toString ();
	}
}
