package com.base.engine.physics;

import com.base.engine.core.Vector3f;

public class AABB{

	private final Vector3f minExtents;
	private final Vector3f maxExtents;
	
	public AABB(Vector3f minExtents, Vector3f maxExtents){
//		super(ColliderType.TYPE_AABB);
		this.minExtents = minExtents;
		this.maxExtents = maxExtents;
	}
	
	public IntersectData intersectAABB(final AABB other){
		Vector3f distances1 = other.getMinExtents().sub(maxExtents);
		Vector3f distances2 = minExtents.sub(other.getMaxExtents());
		Vector3f distances = distances1.max(distances2);
		
		float maxDistance = distances.max();
		
		return new IntersectData(maxDistance < 0, distances);
	}
	
	public Vector3f getMinExtents(){
		return minExtents;
	}
	
	public Vector3f getMaxExtents(){
		return maxExtents;
	}
}
