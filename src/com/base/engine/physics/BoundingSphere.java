package com.base.engine.physics;

import com.base.engine.core.Vector3f;

public class BoundingSphere extends Collider{

	private final float radius;
	
	public BoundingSphere(float radius){
		this.radius = radius;
	}
	
	public IntersectData intersectSphere(BoundingSphere other){
		float radiusDistance = radius + other.getRadius();
		Vector3f direction = (other.getPos().sub(getPos()));
		float centerDistance = direction.length();
		direction = direction.div(centerDistance);
		float distance = centerDistance - radiusDistance;
		return new IntersectData(centerDistance < radiusDistance, direction, distance);
	}
	
	@Override
	public IntersectData intersect(Collider other){
		if(other instanceof HeightMapCollider)
			return other.intersect(this);
		if(other instanceof BoundingSphere)
			return intersectSphere((BoundingSphere) other);
		throw new IllegalArgumentException();
	}
	
	public float getRadius(){
		return radius;
	}
}
