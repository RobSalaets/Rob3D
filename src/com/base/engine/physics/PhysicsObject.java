package com.base.engine.physics;

import com.base.engine.core.Quaternion;
import com.base.engine.core.Vector3f;

public class PhysicsObject{
	
	private Vector3f position;
	private Vector3f oldPos;
	private Vector3f velocity;
	private Collider collider;
	
	//nonbenny
	private Quaternion rot;
	private boolean gravity;
	private boolean flying;
	private static  Vector3f gravitationalForce = new Vector3f(0,-9.81f,0);
	
	public PhysicsObject(Collider collider, Vector3f velocity, boolean gravity){
		this.position = collider.getCenter();
		this.oldPos = position;
		this.velocity = velocity;
		this.collider = collider;
		this.gravity = gravity;
		flying = true;
		this.rot = new Quaternion(0,0,0,1);
	}
	
	public void integrate(float delta){
		
		if(gravity)
			velocity = velocity.add(gravitationalForce.mul(delta * (flying? 2.0f : 1.0f)));
		position = position.add(velocity.mul(delta));
		
	}
	
	public Vector3f getPos(){
		return position;
	}
	
	public Quaternion getRot(){
		return rot;
	}
	
	public void setRot(Quaternion rotation){
		rot = rotation;
	}
	
	public void setFlying(boolean b){
		flying = b;
	}
	
	public boolean isFlying(){
		return flying;
	}
	
	public void setVelocity(Vector3f velocity){
		this.velocity = velocity;
	}

	public Vector3f getVelocity(){
		return velocity;
	}
	
	public final Collider getCollider(){
		Vector3f translation = position.sub(oldPos);
		oldPos = position;
		collider.transform(translation);
		return collider;
	}
}
