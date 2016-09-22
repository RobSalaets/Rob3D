package com.base.engine.components;

import com.base.engine.core.Vector3f;
import com.base.engine.physics.PhysicsObject;

public class PhysicsObjectComponent extends GameComponent{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2365979242916778351L;
	private PhysicsObject physicsObject;
	private Vector3f offset;
	private boolean rotate;

	public PhysicsObjectComponent(PhysicsObject object, Vector3f offset, boolean rotate){
		physicsObject = object;
		this.offset = offset;
		this.rotate = rotate;
	}
	
	public PhysicsObjectComponent(PhysicsObject object){
		this(object, Vector3f.zeroVector, true);
	}
	
	public void update(float delta){
		getTransform().setPos(physicsObject.getPos().add(offset));
		if(rotate)
			getTransform().setRot(physicsObject.getRot());
	}

	@Override
	public GameComponent getCopy(){
		return new PhysicsObjectComponent(physicsObject, offset, rotate);
	}
}
