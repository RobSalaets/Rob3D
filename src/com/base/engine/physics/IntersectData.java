package com.base.engine.physics;

import com.base.engine.core.Vector3f;

public class IntersectData{

	private final boolean doesIntersect;
	private final Vector3f direction;
	
	public IntersectData(boolean doesIntersect, Vector3f direction){
		this.doesIntersect = doesIntersect;
		this.direction = direction;
	}
	
	public boolean getDoesIntersect(){
		return doesIntersect;
	}
	
	public float getDistance(){
		return direction.length();
	}
	
	public Vector3f getDirection(){
		return direction;
	}
}
