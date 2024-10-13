package org.rogersf;

import java.lang.management.ManagementFactory;

public class Main {
	public static void main ( String[] args ) {
		long memUsedKb = ManagementFactory.getMemoryMXBean ().getHeapMemoryUsage ().getUsed () / 1024; // Mem usage in KB
		System.out.println ( "Mem used = " + memUsedKb );
	}
}