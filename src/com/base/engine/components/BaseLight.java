package com.base.engine.components;


import com.base.engine.core.CoreEngine;
import com.base.engine.core.Quaternion;
import com.base.engine.core.Vector3f;
import com.base.engine.rendering.Shader;
import com.base.engine.rendering.ShadowCameraTransform;
import com.base.engine.rendering.ShadowInfo;


public class BaseLight extends GameComponent
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4523781562253521112L;
	private Vector3f color;
	private float intensity;
	private Shader shader;
	private Shader terrainShader;
	protected ShadowInfo shadowInfo;


	public BaseLight(Vector3f color, float intensity)
	{
		this.color = color;
		this.intensity = intensity;
	}


	@Override
	public void addToEngine(CoreEngine engine) 
	{
		engine.getRenderingEngine().addLight(this);
	}

	public ShadowCameraTransform calcShadowCameraTransform(Vector3f mainCameraPos, Quaternion mainCameraRot){
		ShadowCameraTransform result = new ShadowCameraTransform();
		result.pos = getTransform().getTransformedPos();
		result.rot = getTransform().getTransformedRot();
		return result;
	}
	
	public void setShader(Shader shader)
	{
		this.shader = shader;
	}
	
	public void setTerrainShader(Shader shader){
		this.terrainShader = shader;
	}
	
	public void setShadowInfo(ShadowInfo shadowInfo){
		this.shadowInfo = shadowInfo;
	}


	public Shader getShader()
	{
		return shader;
	}
	
	public Shader getTerrainShader(){
		return terrainShader;
	}


	public Vector3f getColor()
	{
		return color;
	}


	public void setColor(Vector3f color)
	{
		this.color = color;
	}


	public float getIntensity()
	{
		return intensity;
	}
	
	public ShadowInfo getShadowInfo(){
		return this.shadowInfo;
	}


	public void setIntensity(float intensity)
	{
		this.intensity = intensity;
	}
}


