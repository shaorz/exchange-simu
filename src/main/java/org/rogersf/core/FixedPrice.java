package org.rogersf.core;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FixedPrice extends BigDecimal {
	public static final int DECIMAL_POINT = 7;
	public static final FixedPrice MAX = new FixedPrice ( 999999999 );
	public static final FixedPrice ZERO = new FixedPrice ( 0 );

	public FixedPrice ( double in ) {
		this ( in , DECIMAL_POINT );
	}

	public FixedPrice ( double in , int place ) {
		super ( Double.toString ( in ) );
		if ( place < 0 ) {
			throw new IllegalArgumentException ();
		}
		this.setScale ( place , RoundingMode.HALF_UP );
	}

	public FixedPrice ( String in , int place ) {
		super ( in );
		if ( place < 0 ) {
			throw new IllegalArgumentException ();
		}
		this.setScale ( place , RoundingMode.HALF_UP );
	}

	public FixedPrice ( String in ) {
		this ( in , DECIMAL_POINT );
	}

	@Override
	public boolean equals ( Object obj ) {
		if ( ! ( obj instanceof FixedPrice ) )
			return false;
		if ( obj == this )
			return true;

		BigDecimal scaleThis = this.setScale ( DECIMAL_POINT , RoundingMode.HALF_UP );
		BigDecimal scaleObj = ( ( FixedPrice ) obj ).setScale ( DECIMAL_POINT , RoundingMode.HALF_UP );
		if ( scaleThis.compareTo ( scaleObj ) == 0 )
			return true;
		return false;
	}
}
