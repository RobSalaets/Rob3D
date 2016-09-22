package com.base.engine.core;

public class Time {

	private static final long SECOND = 1000000000L;
	private static boolean firstTime = true;
	public static double birth;
	
	public static double getTime(){
		if(firstTime){
			firstTime = false;
			birth = (double)System.nanoTime()/(double)SECOND;
		}
		return (double)System.nanoTime()/(double)SECOND;
	}
}
