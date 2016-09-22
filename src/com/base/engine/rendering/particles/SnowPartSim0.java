package com.base.engine.rendering.particles;

import com.base.engine.components.Camera;
import com.base.engine.core.Transform;
import com.base.engine.core.Vector3f;

public class SnowPartSim0 implements ParticleSimulation{

	@Override
	public void simulateParticle(Particle p, Camera cameraPos, Transform emitter, float delta){
		p.pos = p.pos.add(p.speed.mul(delta));
		emitter.setPos(cameraPos.getTransform().getTransformedPos());
	}

	@Override
	public Vector3f getSpawnPos(){
		return Vector3f.randomVector().normalized().mul(10);
	}

	@Override
	public Vector3f getSpawnSpeed(){
		return new Vector3f(0,-1,0).mul(0.6f);
	}

	@Override
	public void simulateEmitter(Transform emitter, Camera camera, float delta){
		// TODO Auto-generated method stub
		
	}

}
