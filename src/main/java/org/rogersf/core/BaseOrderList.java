package org.rogersf.core;

import java.util.Map;

public class BaseOrderList {
	public class Node {
		public Node next;
		public Node prev;
		public Order order;

		public Node ( final Order order ) {
			this.order = order;
			next = null;
			prev = null;
		}
	}

	public class Iterator {
		public Node current;

		public Iterator ( final Node n ) {
			current = n;
		}

		public Iterator next () {
			current = current.next;
			return this;
		}

		public boolean equals ( final Iterator o ) {
			return current == o.current;
		}
	}

	protected Node head;
	protected Node tail;
	protected Map < Order, Node > allOrders;

	public BaseOrderList () {
		head = null;
		tail = null;
	}

	public void pushback ( final Order order ) {
		final Node newNode = new Node ( order );
		if ( head == null ) {
			head = newNode;
			tail = newNode;
		} else {
			newNode.prev = tail;
			tail.next = newNode;
			tail = newNode;
		}
		allOrders.put ( order , newNode );
	}

	public void remove ( final Order order ) {
		allOrders.forEach ( ( o , node ) -> {
			if ( o.equals ( order ) ) {
				allOrders.remove ( o );
				if ( head == node )
					head = node.next;
				if ( tail == node )
					tail = node.prev;
				if ( node.prev != null )
					node.prev.next = node.next;
				if ( node.next != null )
					node.next.prev = node.prev;
			}
		} );
	}

	public Order front () {
		return head == null ? null : head.order;
	}

	public Iterator begin () {
		return new Iterator ( head );
	}

	public Iterator end () {
		return new Iterator ( null );
	}
}
