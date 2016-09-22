package com.base.engine.components;


import com.base.engine.core.Matrix4f;
import com.base.engine.core.Vector3f;
import com.base.engine.rendering.Attenuation;
import com.base.engine.rendering.Shader;
import com.base.engine.rendering.ShadowInfo;


public class SpotLight extends PointLight
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5871129699062027177L;
	private float cutoff;


	public SpotLight(Vector3f color, float intensity, Attenuation attenuation, float fov, int shadowMapSizeAsPowerOf2, float shadowSoftness, float lightBleedReductionAmount, float varianceMin)
	{
		super(color, intensity, attenuation);
		this.cutoff = (float)Math.cos(fov / 2);


		setShader(new Shader("forward-spot"));
		setTerrainShader(new Shader("terrain-spot"));
		
		if(shadowMapSizeAsPowerOf2 != 0)
			setShadowInfo(new ShadowInfo(new Matrix4f().initPerspective(fov, 1.0f, 0.1f, this.range), false, shadowMapSizeAsPowerOf2, shadowSoftness, lightBleedReductionAmount, varianceMin));
	}
	
	public SpotLight(Vector3f color, float intensity, Attenuation attenuation, float fov, int shadowMapSizeAsPowerOf2)
	{
		this(color, intensity, attenuation, fov, shadowMapSizeAsPowerOf2, 1.0f, 0.2f, 0.00002f);
	}
	
	public SpotLight(Vector3f color, float intensity, Attenuation attenuation, float fov)
	{
		this(color, intensity, attenuation, fov, 0, 1.0f, 0.2f, 0.00002f);
	}
	
	public Vector3f getDirection()
	{
		return getTransform().getTransformedRot().getForward();
	}


	public float getCutoff()
	{
		return cutoff;
	}
	public void setCutoff(float cutoff)
	{
		this.cutoff = cutoff;
	}
	
	public GameComponent getCopy(){
		SpotLight s = new SpotLight(getColor(), getIntensity(), getAttenuation(), 0f);
		s.cutoff = cutoff;
		s.setShadowInfo(this.getShadowInfo());
		return s;
	}
}



