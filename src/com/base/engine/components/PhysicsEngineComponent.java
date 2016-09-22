package com.base.engine.components;

import com.base.engine.physics.PhysicsEngine;

public class PhysicsEngineComponent extends GameComponent{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8713486613840661819L;
	private PhysicsEngine physicsEngine;

	public PhysicsEngineComponent(PhysicsEngine engine){
		physicsEngine = engine;
	}
	
	public void update(float delta){
		physicsEngine.simulate(delta);
		physicsEngine.handleCollision();
	}
	
	public PhysicsEngine getPhysicsEngine(){
		return physicsEngine;
	}

	@Override
	public GameComponent getCopy(){
		return new PhysicsEngineComponent(physicsEngine);
	}
}
