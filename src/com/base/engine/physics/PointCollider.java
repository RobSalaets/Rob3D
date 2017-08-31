package com.base.engine.physics;

public class PointCollider extends Collider{

	@Override
	public IntersectData intersect(Collider other){
		if(other instanceof HeightMapCollider)
			return other.intersect(this);
		throw new IllegalArgumentException();
	}

}
