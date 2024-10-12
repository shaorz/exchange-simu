package org.rogersf;

import org.rogersf.core.FixedPrice;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Utils {
	public static double round ( double in , int place ) {
		if ( place < 0 ) {
			throw new IllegalArgumentException ();
		}
		BigDecimal bd = new BigDecimal ( Double.toString ( in ) );
		bd = bd.setScale ( place , RoundingMode.HALF_UP );
		return bd.doubleValue ();
	}

	public static double round ( double in ) {
		return round ( in , FixedPrice.DECIMAL_POINT );
	}

}
