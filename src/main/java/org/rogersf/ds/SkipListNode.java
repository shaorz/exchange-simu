package org.rogersf.ds;

import java.util.Objects;

public class SkipListNode < T extends Comparable < T > > {
	public T value;
	public SkipListNode < T > up, down, left, right;

	public SkipListNode ( T value ) {
		this.value = value;
	}

	@Override
	public boolean equals ( Object o ) {
		if ( this == o ) return true;
		if ( o == null || getClass () != o.getClass () ) return false;

		SkipListNode < T > that;
		try {
			that = ( SkipListNode < T > ) o;
		} catch ( ClassCastException e ) {
			return false;
		}
		return value.compareTo ( that.value ) == 0;
	}

	@Override
	public int hashCode () {
		return Objects.hash ( value , up , down , left , right );
	}

	@Override
	public String toString () {
		return "SkipListNode{" +
				", value=" + value +
				'}';
	}
}
