package com.base.engine.physics;

import com.base.engine.core.Vector3f;

public class IntersectData{

	private final boolean doesIntersect;
	private final Vector3f direction;
	private float distance;
	
	public IntersectData(boolean doesIntersect, Vector3f direction, float distance){
		this.doesIntersect = doesIntersect;
		this.direction = direction;
		this.distance = distance;
	}
	
	public boolean getDoesIntersect(){
		return doesIntersect;
	}
	
	public float getDistance(){
		return distance;
	}
	
	public Vector3f getDirection(){
		return direction;
	}
}
