package com.base.engine.physics;

import com.base.engine.core.Vector3f;

public class BoundingSphere extends Collider{

	private final Vector3f center;
	private final float radius;
	
	public BoundingSphere(Vector3f center, float radius){
		super(ColliderType.TYPE_SPHERE);
		this.center = center;
		this.radius = radius;
	}
	
	public IntersectData intersectBoundingSphere(final BoundingSphere other){
		float radiusDistance = radius + other.getRadius();
		Vector3f direction = (other.getCenter().sub(center));
		float centerDistance = direction.length();
		direction = direction.div(centerDistance);
		float distance = centerDistance - radiusDistance;
		return new IntersectData(centerDistance < radiusDistance, direction.mul(distance));
		
	}
	
	public Vector3f getCenter(){
		return center;
	}
	
	public float getRadius(){
		return radius;
	}
	
	public void transform(Vector3f translation){
		center.addEquals(translation);
	}
}
