package com.base.engine.components;

import com.base.engine.core.CoreEngine;
import com.base.engine.core.Matrix4f;
import com.base.engine.core.Vector2f;
import com.base.engine.core.Vector3f;
import com.base.engine.rendering.Material;
import com.base.engine.rendering.Mesh;
import com.base.engine.rendering.RenderPhase;
import com.base.engine.rendering.RenderingEngine;
import com.base.engine.rendering.Shader;
import com.base.engine.rendering.Texture;

public class Water extends GameComponent{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1643231399956839282L;

	private Mesh mesh;
	private Material material;
	private Shader waterShader;
	private int waterID;
	private float waterLevel;
	private float bumpTilingAmount;
	private float bumpHeight;
	private float windSpeed;
	private Vector3f windDirection;
	private Vector3f waterColor;
	private Vector3f position;
	private float waterColorBlendFactor;
	private float size;
	private boolean firstRender;


	public Water(float size, Vector3f position, float bumpTilingAmount, float bumpHeight, float windSpeed, Vector2f windDirection, Vector3f waterColor, float waterColorBlendFactor){
		mesh = new Mesh(new Texture("plane.png"), size, 1.0f, 1.0f);
		material = new Material(new Texture("white.png"), 0.0f, 0, new Texture("default_normal.png"), new Texture("default_disp.png"), 0.03f, -0.5f);
		this.waterShader = new Shader("water-lake");
		this.position = position;
		this.waterLevel = position.getY();
		this.bumpTilingAmount = bumpTilingAmount;
		this.bumpHeight = bumpHeight;
		this.windSpeed = windSpeed;
		this.windDirection = new Vector3f(windDirection.getX(), windDirection.getY(), 0).normalized();
		this.waterColor = waterColor;
		this.waterColorBlendFactor = waterColorBlendFactor;
		this.size = size;
		this.firstRender = true;
	}

	public void render(Shader shader, RenderingEngine renderingEngine){
		if(firstRender){
			renderingEngine.setFloat("bumpTilingAmount", bumpTilingAmount);
			renderingEngine.setFloat("bumpHeight", bumpHeight);
			renderingEngine.setFloat("windSpeed", windSpeed);
			renderingEngine.setFloat("waterColorBlendFactor", waterColorBlendFactor);
			renderingEngine.setVector3f("waterColor", waterColor);
			renderingEngine.setVector3f("windDirection", windDirection);
			firstRender = false;
		}
		if(renderingEngine.getRenderPhase() == RenderPhase.AMBIENT){
			material.setTexture("reflectionMap", (Texture) renderingEngine.getWaterData(waterID)[0]);
			material.setTexture("refractionMap", (Texture) renderingEngine.getWaterData(waterID)[1]);
			renderingEngine.setWaterReflectionMatrix((Matrix4f) renderingEngine.getWaterData(waterID)[2]);
			
			waterShader.bind();
			waterShader.updateUniforms(getTransform(), material, renderingEngine);
			mesh.draw();
		}
	}
	
	public void addToEngine(CoreEngine engine){
		waterID = engine.getRenderingEngine().addWater(this);
		getTransform().setPos(position);
	}
	
	public float getWaterLevel(){
		return waterLevel;
	}

	public Vector3f getWaterColor(){
		return waterColor;
	}

	public float getSize(){
		return size;
	}
}
