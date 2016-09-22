package com.base.engine.rendering.resourceManagement;


public class ReferenceCounter{
	
	private int refCount;
	
	public ReferenceCounter(){
		this.refCount = 1;
	}
	
	public void addReference(){
		refCount++;
	}
	
	public boolean removeReference(){
		refCount--;
		return refCount == 0;
	}
	
	public int getReferenceCount(){
		return refCount;
	}
}
