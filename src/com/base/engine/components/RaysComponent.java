package com.base.engine.components;

import com.base.engine.core.CoreEngine;
import com.base.engine.core.Transform;
import com.base.engine.core.Vector3f;
import com.base.engine.rendering.Material;
import com.base.engine.rendering.Texture;

public class RaysComponent extends GameComponent{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4236870492252881175L;
	private Material material;
	private Transform lightPos;
	
	public RaysComponent(Material material, Texture texture, Transform lightPos, float density, float weight, float decay, float exposure){
		this.material = material;
		this.lightPos = lightPos;
		this.material.setFloat("density", density);
		this.material.setFloat("weight", weight);
		this.material.setFloat("decay", decay);
		this.material.setFloat("exposure", exposure);
		this.material.setTexture("rayMap", texture);
		
	}
	
	public RaysComponent(Material material, Texture texture, Vector3f lightPos, float density, float weight, float decay, float exposure){
		this(material, texture, new Transform(), density, weight, decay, exposure);
		this.lightPos.setPos(lightPos);
	}
	
	public void enable(){
		this.material.setFloat("hasRays", 1.0f);
	}
	
	public void disable(){
		this.material.setFloat("hasRays", 0.0f);
	}
	
	public Vector3f getLightPos(){
		return lightPos.getTransformedPos();
	}
	
	public Material getMaterial(){
		return material;
	}
	
	@Override
	public void addToEngine(CoreEngine engine){
		super.addToEngine(engine);
		engine.getRenderingEngine().addRayEmitter(this);
	}
}
