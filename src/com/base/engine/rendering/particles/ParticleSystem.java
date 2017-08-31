package com.base.engine.rendering.particles;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_STREAM_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL31.glDrawArraysInstanced;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Arrays;

import org.lwjgl.BufferUtils;

import com.base.engine.components.Camera;
import com.base.engine.components.GameComponent;
import com.base.engine.core.CoreEngine;
import com.base.engine.core.Quaternion;
import com.base.engine.core.Vector3f;
import com.base.engine.rendering.Material;
import com.base.engine.rendering.RenderingEngine;
import com.base.engine.rendering.Shader;

public class ParticleSystem extends GameComponent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7052001874963237934L;

	private static final float vertexBufferData[] = { -0.5f, -0.5f, 0.0f, 0.5f, -0.5f, 0.0f, -0.5f, 0.5f, 0.0f, 0.5f, 0.5f, 0.0f };

	private static int maxParticles = 25000;
	private static int vertexBuffer;
	private int positionBuffer;
	private int colorBuffer;
	private Particle particleContainer[];
	private Particle particle;
	private ParticleSimulation particleSimulation;
	private FloatBuffer positions;
	private ByteBuffer colors;
	private float life;
	private boolean remove;
	private boolean stopSpawning;

	private int particleCount;
	private int lastUsedParticle;

	private int particlesPerFrame;
	private Shader particleShader;
	private Shader particleAtlasShader;
	private Material material;
	private Camera camera;

	public ParticleSystem(int particlesPerFrame, Particle particle, ParticleSimulation particleSim, Camera mainCamera, float life){
		this.particlesPerFrame = particlesPerFrame;
		this.particleShader = new Shader("particleShader");
		this.particleAtlasShader = new Shader("particleAtlasShader");
		this.camera = mainCamera;
		this.particle = particle;
		this.particleSimulation = particleSim;
		this.life = (float) Math.ceil(life);
		this.remove = false;
		on();

		material = new Material();
		material.setTexture("diffuse", particle.sprite);
		vertexBuffer = glGenBuffers();
		FloatBuffer buffer = BufferUtils.createFloatBuffer(12);
		for(int i = 0; i < 12; i++)
			buffer.put(vertexBufferData[i]);
		buffer.flip();
		glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);

		positionBuffer = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, positionBuffer);
		glBufferData(GL_ARRAY_BUFFER, BufferUtils.createFloatBuffer(maxParticles * 4), GL_STREAM_DRAW);

		colorBuffer = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, colorBuffer);
		glBufferData(GL_ARRAY_BUFFER, BufferUtils.createByteBuffer(maxParticles * 4), GL_STREAM_DRAW);

		particleContainer = new Particle[maxParticles];

		for(int i = 0; i < maxParticles; i++){
			particleContainer[i] = new Particle();
			particleContainer[i].life = -1.0f;
		}

		lastUsedParticle = 0;
		positions = BufferUtils.createFloatBuffer(4 * maxParticles);
		colors = BufferUtils.createByteBuffer(4 * maxParticles);

	}
	
	public ParticleSystem(int particlesPerFrame, Particle particle, ParticleSimulation particleSim, Camera mainCamera){
		this(particlesPerFrame, particle, particleSim, mainCamera, -2);
	}

	private void updateBuffers(){
		glBindBuffer(GL_ARRAY_BUFFER, positionBuffer);
		glBufferData(GL_ARRAY_BUFFER, BufferUtils.createFloatBuffer(maxParticles * 4), GL_STREAM_DRAW);
		glBufferSubData(GL_ARRAY_BUFFER, 0, positions);

		glBindBuffer(GL_ARRAY_BUFFER, colorBuffer);
		glBufferData(GL_ARRAY_BUFFER, BufferUtils.createByteBuffer(maxParticles * 4), GL_STREAM_DRAW);
		glBufferSubData(GL_ARRAY_BUFFER, 0, colors);
	}

	private void simulate(float delta){
		int newParticles = (int) (delta * (float) particlesPerFrame);
		if(newParticles > (int) (0.016f * (float) particlesPerFrame)) newParticles = (int) (0.016f * (float) particlesPerFrame);
		
		
		
		if(stopSpawning == true)newParticles = 0;

		
		for(int i = 0; i < newParticles; i++){
			int index = findUnusedParticle();
			particleContainer[index].life = particle.life;
			particleContainer[index].r = (byte) (particle.r);
			particleContainer[index].g = (byte) (particle.g);
			particleContainer[index].b = (byte) (particle.b);
			particleContainer[index].a = (byte) (particle.size);
			particleContainer[index].pos = particleSimulation.getSpawnPos();
			particleContainer[index].emitterSpawningPos = getTransform().getTransformedPos();
			particleContainer[index].emitterSpawningRot = getTransform().getTransformedRot();
			particleContainer[index].speed = particleSimulation.getSpawnSpeed();
		}

		int particlesCount = 0;
		for(int i = 0; i < maxParticles; i++){
			Particle p = particleContainer[i];

			if(p.life > 0.0f){
				p.life -= delta;
				if(p.life > 0.0f){
					Vector3f offset = p.emitterSpawningPos.sub(getTransform().getTransformedPos());
					Quaternion rotOffset  = getTransform().getTransformedRot().mul(p.emitterSpawningRot).normalized();
					particleSimulation.simulateParticle(p, camera, getTransform(), delta);
					p.distanceFromCamera = Math.abs((getTransform().getTransformedPos().add(p.pos)).sub(camera.getTransform().getTransformedPos()).length());
					//Vector3f rotatedPos = p.pos.rotate(rotOffset);
					
					positions.put(4 * particlesCount + 0, p.pos.getX() + offset.getX());
					positions.put(4 * particlesCount + 1, p.pos.getY() + offset.getY());
					positions.put(4 * particlesCount + 2, p.pos.getZ() + offset.getZ());
					positions.put(4 * particlesCount + 3, p.life);
					colors.put(4 * particlesCount + 0, p.r);
					colors.put(4 * particlesCount + 1, p.g);
					colors.put(4 * particlesCount + 2, p.b);
					colors.put(4 * particlesCount + 3, p.a);
				}else{
					p.distanceFromCamera = -1.0f;
				}
				particlesCount++;
			}
		}
		particleSimulation.simulateEmitter(getTransform(), camera, delta);
		particleCount = particlesCount;
	}

	private void sort(){
		Arrays.sort(particleContainer);
	}

	public void update(float delta){

		if(life > 0.0f)
			life -= 1.0f;
		else if(life == 0.0f)
			remove();
		
		simulate(delta);
		sort();
		updateBuffers();
		
	}
	
	public void on(){
		this.stopSpawning = false;
	}
	
	public void off(){
		this.stopSpawning = true;
	}
	
	public void remove(){
		remove = true;
	}

	public void addToEngine(CoreEngine engine){
		engine.getRenderingEngine().addParticleSystem(this);
	}

	public void render(RenderingEngine renderingEngine){
		if(remove)renderingEngine.removeParticleSystem(this);
		
		
			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			
			if(particle.atlas){
				material.setFloat("atlasSize", particle.atlasSize);
				material.setFloat("lifeSpan", particle.lifeSpan);
				particleAtlasShader.bind();
				particleAtlasShader.updateUniforms(getTransform(), material, renderingEngine);

			}else{
				particleShader.bind();
				particleShader.updateUniforms(getTransform(), material, renderingEngine);
			}

			glEnableVertexAttribArray(0);
			glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
			glVertexAttribPointer(0, // attribute. No particular reason for 0,
										// but must match the layout in the
										// shader.
					3, // size
					GL_FLOAT, // type
					false, // normalized?
					0, // stride
					0 // array buffer offset
			);

			glEnableVertexAttribArray(4);
			glBindBuffer(GL_ARRAY_BUFFER, positionBuffer);
			glVertexAttribPointer(4, // attribute. No particular reason for 0,
										// but must match the layout in the
										// shader.
					4, // size
					GL_FLOAT, // type
					false, // normalized?
					0, // stride
					0 // array buffer offset
			);

			glEnableVertexAttribArray(3);
			glBindBuffer(GL_ARRAY_BUFFER, colorBuffer);
			glVertexAttribPointer(3, // attribute. No particular reason for 0,
										// but must match the layout in the
										// shader.
					4, // size
					GL_UNSIGNED_BYTE, // type
					true, // normalized?
					0, // stride
					0 // array buffer offset
			);

			glVertexAttribDivisor(0, 0); // particles vertices : always reuse
			glVertexAttribDivisor(4, 1);
			glVertexAttribDivisor(3, 1); // color : one per quad -> 1
			glDrawArraysInstanced(GL_TRIANGLE_STRIP, 0, 4, particleCount);
			glDisableVertexAttribArray(0);
			glDisableVertexAttribArray(4);
			glDisableVertexAttribArray(3);
			
			glDisable(GL_BLEND);
	}
	

	private int findUnusedParticle(){
		for(int i = lastUsedParticle; i < maxParticles; i++){
			if(particleContainer[i].life < 0){
				lastUsedParticle = i;
				return i;
			}
		}

		for(int i = 0; i < lastUsedParticle; i++){
			if(particleContainer[i].life < 0){
				lastUsedParticle = i;
				return i;
			}
		}
		return 0;
	}
}
