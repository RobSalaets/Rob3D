package com.base.engine.rendering;

import com.base.engine.core.Matrix4f;

public class ShadowInfo{
	
	private Matrix4f projection;
	private float shadowSoftness;
	private float lightBleedReductionAmount;
	private float varianceMin;
	private boolean flipFaces;
	private int shadowMapSizeAsPowerOf2;
	
	public ShadowInfo(Matrix4f projection, boolean flipFaces, int shadowMapSizeAsPowerOf2,float shadowSoftness, float lightBleedReductionAmount, float varianceMin){
		this.projection = projection;
		this.flipFaces = flipFaces;
		this.shadowSoftness = shadowSoftness;
		this.lightBleedReductionAmount = lightBleedReductionAmount;
		this.varianceMin = varianceMin;
		this.shadowMapSizeAsPowerOf2 = shadowMapSizeAsPowerOf2;
	}
	
	public Matrix4f getProjection(){
		return this.projection;
	}
	
	public boolean getFlipFaces(){
		return this.flipFaces;
	}
	
	public float getShadowSoftness(){
		return shadowSoftness;
	}
	
	public float getLightBleedReductionAmount(){
		return lightBleedReductionAmount;
	}
	
	public float getVarianceMin(){
		return varianceMin;
	}
	
	public int getShadowMapSizeAsPowerOf2(){
		return this.shadowMapSizeAsPowerOf2;
	}
}
