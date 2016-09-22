package com.base.engine.rendering.particles;

import java.util.Random;

import com.base.engine.components.Camera;
import com.base.engine.core.Transform;
import com.base.engine.core.Vector3f;

public interface ParticleSimulation{
	
	public static Random random = new Random();
	
	public void simulateParticle(Particle p, Camera cameraPos, Transform emitter, float delta);
	public void simulateEmitter(Transform emitter, Camera camera, float delta);
	public Vector3f getSpawnPos();
	public Vector3f getSpawnSpeed();
}
