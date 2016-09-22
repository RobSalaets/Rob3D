package com.base.engine.profiling;

import com.base.engine.core.Time;

public class ProfileTimer{

	private int numInvocations;
	private double totalTime;
	private double startTime;

	public ProfileTimer(){
		numInvocations = 0;
		totalTime = 0;
		startTime = 0.0;
	}

	public void startInvocation(){
		startTime = Time.getTime();
	}

	public void stopInvocation(){
		if(startTime == 0.0){
			System.err.println("Error: StopInvocation called without matching start Invocation");
			throw new IllegalArgumentException();
		}
		numInvocations++;
		totalTime += (Time.getTime() - startTime);
		startTime = 0;
	}

	/**
	 * Set Dividend to 0, for average function callTime  
	 * @param whiteSpaceNum 
	 */
	public double displayAndReset(String message, double inputDividend, int whiteSpaceNum){
		int whiteSpaceNeeded = whiteSpaceNum - message.length();
		if(whiteSpaceNeeded < 0)System.err.println("Profiling Error: insufficient WhiteSpaceNum");
		String white = "";
		for(int i = 0; i < whiteSpaceNeeded; i++){
			white += " ";
		}
		
		double dividend = inputDividend;
		if(dividend == 0)
			dividend = numInvocations;
		
		double time;
		if(dividend == 0){
			time = 0;
		}else{
			time = (1000.0 * totalTime) / ((double) dividend);
		}
		System.out.println(message + white + time + " ms");
//		EditorContentPane.printToConsole(message + white + time + " ms");
		totalTime = 0.0;
		numInvocations = 0;
		return time;
	}
}
