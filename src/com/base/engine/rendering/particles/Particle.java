package com.base.engine.rendering.particles;

import com.base.engine.core.Quaternion;
import com.base.engine.core.Vector3f;
import com.base.engine.rendering.Texture;

public class Particle implements Comparable<Particle>{

	protected Vector3f pos;
	protected Vector3f emitterSpawningPos;
	protected Quaternion emitterSpawningRot;
	protected Vector3f speed;
	protected Texture sprite;
	protected byte r;
	protected byte g;
	protected byte b;
	protected byte a;
	protected byte size;
	protected float life;
	protected float lifeSpan;
	protected float distanceFromCamera;
	protected boolean atlas;
	protected int atlasSize;
	
	
	public Particle(){
		atlas = false;
	}
	
	public void setTexture(Texture texture, boolean atlas, int atlasSize){
		sprite = texture;
		this.atlas = atlas;
		this.atlasSize = atlasSize;
	}

	public void setColor(byte a, byte r, byte g, byte b){
		this.a = a;
		this.r = r;			
		this.g = g;
		this.b = b;
	}

	public void setLife(float i){
		life = i;
		lifeSpan = i;
	}

	public void setSize(byte f){
		size = f;
	}
	
	@Override
	public int compareTo(Particle o){
		if(o.distanceFromCamera > distanceFromCamera)return 1;
		if(o.distanceFromCamera < distanceFromCamera) return -1;
		return 0;	
	}
}
