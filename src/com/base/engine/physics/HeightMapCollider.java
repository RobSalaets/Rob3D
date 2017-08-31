package com.base.engine.physics;

import com.base.engine.core.Vector2f;
import com.base.engine.core.Vector3f;

public class HeightMapCollider extends Collider{

	private float unitScale;
	private float heightScale;
	private float[] heightMap;
	private int width;
	private int height;
	private Vector3f[][] normalField;

	public HeightMapCollider(float[] heightMap, int width, int height, float unitScale, float heightScale, boolean tiling){
		assert heightMap.length == width * height;
		//TODO tiling
		this.unitScale = unitScale;
		this.heightScale = heightScale;
		this.heightMap = heightMap;
		this.width = width;
		this.height = height;
		this.normalField = toNormalField(heightMap, width, height, heightScale / unitScale);
	}

	public IntersectData intersectSphere(BoundingSphere other){
		float unitRadius = other.getRadius() / unitScale;
		Vector3f center = other.getPos().div(unitScale);
		for(int y = (int) Math.max(0, Math.floor(center.getZ() - unitRadius)); y < (int) Math.min(height-1, Math.ceil(center.getZ() + unitRadius)); y++){
			float intersectSpan = (float) (Math.max(0, Math.sqrt(Math.pow(unitRadius, 2) - Math.pow(y - center.getZ(), 2))));
			for(int x = (int) Math.max(0, Math.floor(other.getPos().getX() - intersectSpan)); x < Math.min(width-1, Math.ceil(other.getPos().getX() + intersectSpan)); x++){
				Vector3f b = new Vector3f((x + 1) * unitScale, heightMap[x + y * width] * heightScale, y * unitScale);
				float distance1 = other.getPos().sub(b).dot(normalField[x + y * (width - 1)][0]) - other.getRadius();
				float distance2 = other.getPos().sub(b).dot(normalField[x + y * (width - 1)][1]) - other.getRadius();
				if(Math.min(distance1, distance2) < 0.0f){					
					return new IntersectData(true, distance1 < distance2 ? normalField[x + y * (width - 1)][0] : 
																			normalField[x + y * (width - 1)][1], Math.min(distance1, distance2));
				}
			}
		}
		return new IntersectData(false, null, 0.0f);
	}
	
	public IntersectData intersectPoint(PointCollider other){
		Vector2f pos = other.getPos().getXZ().div(unitScale);
		int x = (int)pos.getX();
		int y = (int)pos.getY();
//		Vector3f b = new Vector3f((x + 1) * unitScale, heightMap[x + y * width] * heightScale, y * unitScale);
//		float distance1 = other.getPos().sub(b).dot(normalField[x + y * (width - 1)][0]);
//		float distance2 = other.getPos().sub(b).dot(normalField[x + y * (width - 1)][1]);
//		if(Math.min(distance1, distance2) < 0.0f){					
//			return new IntersectData(true, distance1 < distance2 ? normalField[x + y * (width - 1)][0] : 
//																	normalField[x + y * (width - 1)][1], Math.min(distance1, distance2));
//		}
		
		return new IntersectData(true, new Vector3f((x) * unitScale, heightMap[x + y * width] * heightScale, y * unitScale), 0.0f);
	}
	
	@Override
	public IntersectData intersect(Collider other){
		if(other instanceof BoundingSphere)
			return intersectSphere((BoundingSphere) other);
		if(other instanceof PointCollider)
			return intersectPoint((PointCollider) other);
		throw new IllegalArgumentException();
	}

	private Vector3f[][] toNormalField(float[] heightMap, int width, int height, float scaleRatio){
		int length = width * height;
		assert heightMap.length == length;
		Vector3f[][] result = new Vector3f[(width - 1) * (height - 1)][2];
		for(int y = 0; y < height - 1; y++){
			for(int x = 0; x < width - 1; x++){
				Vector3f a = new Vector3f(x, heightMap[x + y * width] * scaleRatio, y);
				Vector3f b = new Vector3f(x + 1, heightMap[x + 1 + y * width] * scaleRatio, y);
				Vector3f c = new Vector3f(x + 1, heightMap[x + 1 + (y + 1) * width] * scaleRatio, y + 1);
				Vector3f d = new Vector3f(x, heightMap[x + (y + 1) * width] * scaleRatio, y + 1);
				result[x + y * (width - 1)][0] = d.sub(a).cross(b.sub(a)).normalized();
				result[x + y * (width - 1)][1] = b.sub(c).cross(d.sub(c)).normalized();
			}
		}
		return result;
	}
}
