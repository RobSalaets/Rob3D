package com.base.engine.rendering.particles;

import com.base.engine.components.Camera;
import com.base.engine.core.Transform;
import com.base.engine.core.Vector3f;

public class BoatPartSim implements ParticleSimulation{
	
	private boolean front;
	
	public BoatPartSim(boolean front){
		this.front = front;
	}

	@Override
	public void simulateParticle(Particle p, Camera cameraPos, Transform emitter, float delta){
		p.pos = p.pos.add(p.speed.mul(delta));
		
	}

	@Override
	public Vector3f getSpawnPos(){
		if(front){
			float vOffset = (float) (Math.random() * 1.5);
			return new Vector3f(vOffset/ (Math.random() <= 0.5f ? -0.75f : 0.75f) , 0.2f, 13 - vOffset);
		}
		return new Vector3f((float)Math.random() * 2 - 1.0f, 0.2f, -13 + (float)Math.random() * 4.0f - 2.0f);
	}

	@Override
	public Vector3f getSpawnSpeed(){
		if(front)
			return new Vector3f(0,0.1f, -0.1f);
		return new Vector3f((float)Math.random()*2 - 1.0f, 0.0f, -1).add(Vector3f.randomVector(0.5f));
	}

	@Override
	public void simulateEmitter(Transform emitter, Camera camera, float delta){
		
	}

}
