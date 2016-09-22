package com.base.engine.rendering.particles;

import com.base.engine.components.Camera;
import com.base.engine.core.Transform;
import com.base.engine.core.Vector3f;

public class ExperimentalPartSim0 implements ParticleSimulation{
	
//	private Vector3f rot;

	public ExperimentalPartSim0(){
//		rot = Vector3f.xAxis;
	}

	@Override
	public void simulateParticle(Particle p, Camera cameraPos, Transform emitter, float delta){
		p.pos = p.pos.add(p.speed.mul(-delta));

	}

	@Override
	public Vector3f getSpawnPos(){
		return Vector3f.randomVector().mul(0.2f);
	}

	@Override
	public Vector3f getSpawnSpeed(){
		return Vector3f.yAxis;
	}

	@Override
	public void simulateEmitter(Transform emitter, Camera camera, float delta){
		emitter.setPos(camera.getTransform().getTransformedPos()); 
	}

}