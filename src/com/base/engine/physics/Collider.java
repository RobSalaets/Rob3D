package com.base.engine.physics;

import com.base.engine.core.Vector3f;

public abstract class Collider{
	
	public enum ColliderType{
		TYPE_SPHERE,
		TYPE_AABB,
		TYPE_HEIGHTMAP,
		TYPE_SIZE
	};

	private ColliderType type;
	
	public Collider(ColliderType type){
		this.type = type;
	}
	
	public ColliderType getType(){
		return type;
	}
	
	public IntersectData intersect(final Collider other){
		if(type == ColliderType.TYPE_SPHERE && other.getType() == ColliderType.TYPE_SPHERE){
			BoundingSphere self = (BoundingSphere)this;
			return self.intersectBoundingSphere((BoundingSphere)other);
		}
		else if(type == ColliderType.TYPE_SPHERE && other.getType() == ColliderType.TYPE_HEIGHTMAP){
			HeightMapCollider otherc = (HeightMapCollider)other;
			return otherc.intersectBoundingSphere((BoundingSphere)this);
		}
		else if(type == ColliderType.TYPE_HEIGHTMAP && other.getType() == ColliderType.TYPE_SPHERE){
			HeightMapCollider self = (HeightMapCollider)this;
			return self.intersectBoundingSphere((BoundingSphere)other);
		}
		
		System.err.println("Error: No collisions implemented between specified colliders.");
		System.exit(1);
		
		return new IntersectData(false, new Vector3f(0,0,0));
	}
	
	public abstract void transform(Vector3f translation);
	
	public Vector3f getCenter(){
		return new Vector3f(0,0,0);
	}
}
