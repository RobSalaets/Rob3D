package com.base.engine.physics;

import com.base.engine.components.GameComponent;
import com.base.engine.core.CoreEngine;
import com.base.engine.core.Vector3f;

public class PhysicsObject extends GameComponent{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2365979242916778351L;
	private Vector3f offset;
	private Vector3f velocity;
	private Collider collider;

	private boolean gravity;
	private boolean contact;
	private boolean rotate;
	private boolean fixed;

	public PhysicsObject(Collider collider, Vector3f velocity, Vector3f offset, boolean rotate, boolean gravity, boolean fixed){
		this.velocity = velocity;
		this.offset = offset;
		this.collider = collider;
		this.rotate = rotate;
		this.gravity = gravity;
		this.fixed = fixed;
		this.contact = false;
	}

	@Override
	public void addToEngine(CoreEngine engine){
		collider.setPos(getTransform().getPos());
		collider.setRot(getTransform().getRot());
	}

	public void update(float delta){
		getTransform().setPos(collider.getPos().add(offset));
		if(rotate)
			getTransform().setRot(collider.getRot());
	}

	public void advance(float delta){
		collider.setPos(collider.getPos().add(velocity.mul(delta)));
	}

	public void setContact(boolean c){
		contact = c;
	}

	public boolean isContact(){
		return contact;
	}

	public boolean isFixed(){
		return fixed;
	}
	
	public boolean doGravity(){
		return gravity;
	}

	public void setVelocity(Vector3f velocity){
		this.velocity = velocity;
	}

	public Vector3f getVelocity(){
		return velocity;
	}

	public final Collider getCollider(){
		return collider;
	}
}
