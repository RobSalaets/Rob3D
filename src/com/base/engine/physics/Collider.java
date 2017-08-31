package com.base.engine.physics;

import com.base.engine.core.Quaternion;
import com.base.engine.core.Vector3f;

public abstract class Collider{
	
	
	private Quaternion rotation = Quaternion.noRotation;
	private Vector3f position = Vector3f.zeroVector;

	public abstract IntersectData intersect(Collider other);
	
	public void setPos(Vector3f position){
		this.position = position;
	}
	
	public void setRot(Quaternion q){
		this.rotation = q;
	}
	
	public Vector3f getPos(){
		return position;
	}
	
	public Quaternion getRot(){
		return rotation;
	}
}
