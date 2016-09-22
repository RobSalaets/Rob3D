package com.base.engine.rendering.particles;

import com.base.engine.components.Camera;
import com.base.engine.core.Transform;
import com.base.engine.core.Vector3f;

public class FirePartSim0 implements ParticleSimulation{

	@Override
	public void simulateParticle(Particle p, Camera cameraPos, Transform emitter, float delta){
		p.pos = p.pos.add(p.speed.mul(delta));
		
	}

	@Override
	public Vector3f getSpawnPos(){
		return Vector3f.zeroVector;
	}

	@Override
	public Vector3f getSpawnSpeed(){
		return new Vector3f(0,1,0).add(Vector3f.randomVector(0.21f)).normalized().mul(3.0f);
	}

	@Override
	public void simulateEmitter(Transform emitter, Camera camera, float delta){
		// TODO Auto-generated method stub
		
	}

}
