package com.base.engine.components;


import com.base.engine.core.Matrix4f;
import com.base.engine.core.Quaternion;
import com.base.engine.core.Vector3f;
import com.base.engine.rendering.Shader;
import com.base.engine.rendering.ShadowCameraTransform;
import com.base.engine.rendering.ShadowInfo;


public class DirectionalLight extends BaseLight
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7895745965972912630L;
	private float halfShadowArea;
	
	public DirectionalLight(Vector3f color, float intensity, int shadowMapSizeAsPowerOf2, float shadowArea, float shadowSoftness, float lightBleedReductionAmount, float varianceMin)
	{
		super(color, intensity);

		halfShadowArea = shadowArea / 2.0f;
		setShader(new Shader("forward-directional"));
		setTerrainShader(new Shader("terrain-directional"));
		if(shadowMapSizeAsPowerOf2 != 0)
			setShadowInfo(new ShadowInfo(new Matrix4f().initOrthographic(-halfShadowArea, halfShadowArea, -halfShadowArea, halfShadowArea, -halfShadowArea, halfShadowArea), true, shadowMapSizeAsPowerOf2, shadowSoftness, lightBleedReductionAmount, varianceMin));
	}
	
	public DirectionalLight(Vector3f color, float intensity, int shadowMapSizeAsPowerOf2, float shadowArea)
	{
		this(color, intensity, shadowMapSizeAsPowerOf2, shadowArea, 1.0f, 0.2f, 0.00002f);
	}

	public DirectionalLight(Vector3f color, float intensity)
	{
		this(color, intensity, 8, 80.0f, 1.0f, 0.2f, 0.00002f);
	}
	
	public ShadowCameraTransform calcShadowCameraTransform(Vector3f mainCameraPos, Quaternion mainCameraRot){
		ShadowCameraTransform result = new ShadowCameraTransform();
		result.pos = mainCameraPos.add(mainCameraRot.getForward().mul(halfShadowArea));
		result.rot = getTransform().getTransformedRot();
		
		//reduce shimmering
		float worldTexelSize = (halfShadowArea * 2)/((float)(1 << this.getShadowInfo().getShadowMapSizeAsPowerOf2()));
		Vector3f lightSpaceCameraPos = result.pos.rotate(result.rot.conjugate());
		
		lightSpaceCameraPos.setX(worldTexelSize * (float)Math.floor(lightSpaceCameraPos.getX() / worldTexelSize));
		lightSpaceCameraPos.setY(worldTexelSize * (float)Math.floor(lightSpaceCameraPos.getY() / worldTexelSize));
		
		result.pos = lightSpaceCameraPos.rotate(result.rot);
		return result;
	}
	
	public Vector3f getDirection()
	{
		return getTransform().getTransformedRot().getForward();
	}
	
	public GameComponent getCopy(){
		return new DirectionalLight(getColor(), getIntensity(), shadowInfo.getShadowMapSizeAsPowerOf2(), 2.0f * halfShadowArea, shadowInfo.getShadowSoftness(), shadowInfo.getLightBleedReductionAmount(), shadowInfo.getVarianceMin());
	}
}

