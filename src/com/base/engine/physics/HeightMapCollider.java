package com.base.engine.physics;

import com.base.engine.core.Vector2f;
import com.base.engine.core.Vector3f;
import com.base.engine.rendering.Texture;

public class HeightMapCollider extends Collider{

	private float unitScale;
	private float heightScale;
	private int[] heights;
	private int heightMapHeight;
	private int heightMapWidth;
	private Vector3f position;

	public HeightMapCollider(Texture heightMap, float unitScale, float heightScale, Vector3f pos){
		super(ColliderType.TYPE_HEIGHTMAP);
		this.unitScale = unitScale;
		this.heightScale = heightScale;
		this.position = pos;

		this.heights = heightMap.getPixels();
		this.heightMapHeight = (int) heightMap.getHeight();
		this.heightMapWidth = (int) heightMap.getWidth();

	}

	public IntersectData intersectBoundingSphere(BoundingSphere other){
		if((other.getCenter().getX() < position.getX() || other.getCenter().getX() > position.getX() + unitScale * heightMapWidth) && other.getCenter().getZ() < position.getZ() || other.getCenter().getZ() > position.getZ() + unitScale * heightMapHeight) return new IntersectData(false, new Vector3f(0, 0, 0));
		else{
			float radius = other.getRadius();
			float heightScale = this.heightScale / 128.0f;
			if(heightScale == 0.0f) heightScale = 1.0f;
			int xb = (int) (((other.getCenter().getX() - radius) - position.getX()) / unitScale) - 1 < 0 ? 0 : (int) (((other.getCenter().getX() - radius) - position.getX()) / unitScale) - 1;
			int xe = (int) (((other.getCenter().getX() + radius) - position.getX()) / unitScale) + 1 > heightMapWidth ? heightMapWidth : (int) (((other.getCenter().getX() + radius) - position.getX()) / unitScale) + 1;

			int yb = (int) (((other.getCenter().getZ() - radius) - position.getZ()) / unitScale) - 1 < 0 ? 0 : (int) (((other.getCenter().getZ() - radius) - position.getZ()) / unitScale) - 1;
			int ye = (int) (((other.getCenter().getZ() + radius) - position.getZ()) / unitScale) + 1 > heightMapHeight ? heightMapHeight : (int) (((other.getCenter().getZ() + radius) - position.getZ()) / unitScale) + 1;
			Vector3f closest = new Vector3f(0,110,0);
			Vector2f cij = new Vector2f(0.1f,0.1f);
			
			for(int j = yb; j < ye; j++){
				for(int i = xb; i < xe; i++){
					float y = (((float) ((heights[(int) (j * heightMapWidth + i)] >> 8) & 0xFF) * heightScale) / 2.0f);
					Vector3f point = new Vector3f(i * unitScale, y, j * unitScale).add(position);
					Vector3f direction = other.getCenter().sub(point);
					if(direction.length() < other.getCenter().sub(closest).length()){
						cij = new Vector2f(i,j);
						closest = point;
					}
				}
			}
			cij = cij.add(other.getCenter().sub(closest).div(unitScale).getXZ());
			
			int x1 = clamp((int) Math.floor(cij.getX()), true);
			int x2 = clamp((int) Math.ceil(cij.getX()), true);
			int y1 = clamp((int) Math.floor(cij.getY()), false);
			int y2 = clamp((int) Math.ceil(cij.getY()), false);
			
			Vector3f p1 = new Vector3f(x1 * unitScale, (((float) ((heights[(int) (y1 * heightMapWidth + x1)] >> 8) & 0xFF) * heightScale) / 2.0f),y1 * unitScale).lerp(
					 	new Vector3f(x1 * unitScale, (((float) ((heights[(int) (y2 * heightMapWidth + x1)] >> 8) & 0xFF) * heightScale) / 2.0f),y2 * unitScale)	, cij.getY() - y1);
			
			Vector3f p2 = new Vector3f(x2 * unitScale, (((float) ((heights[(int) (y1 * heightMapWidth + x2)] >> 8) & 0xFF) * heightScale) / 2.0f),y1 * unitScale).lerp(
				 	new Vector3f(x2 * unitScale, (((float) ((heights[(int) (y2 * heightMapWidth + x2)] >> 8) & 0xFF) * heightScale) / 2.0f),y2 * unitScale)	, cij.getY() - y1);
		
			Vector3f lowIntersect = p1.lerp(p2, cij.getX() - x1).add(position);
			
			Vector3f p3 = new Vector3f(x1 * unitScale, (((float) ((heights[(int) (y1 * heightMapWidth + x1)] >> 8) & 0xFF) * heightScale) / 2.0f),y1 * unitScale).sub(
				 	new Vector3f(x2 * unitScale, (((float) ((heights[(int) (y2 * heightMapWidth + x2)] >> 8) & 0xFF) * heightScale) / 2.0f),y2 * unitScale));
		
			Vector3f p4 = new Vector3f(x1 * unitScale, (((float) ((heights[(int) (y2 * heightMapWidth + x1)] >> 8) & 0xFF) * heightScale) / 2.0f),y2 * unitScale).sub(
			 	new Vector3f(x2 * unitScale, (((float) ((heights[(int) (y1 * heightMapWidth + x2)] >> 8) & 0xFF) * heightScale) / 2.0f),y1 * unitScale));
			
			
			Vector3f finalPoint = other.getCenter().add(p3.cross(p4).normalized().mul(lowIntersect.sub(other.getCenter()).length() * lowIntersect.sub(other.getCenter()).normalized().dot(p3.cross(p4).normalized())));
			
			Vector3f dir = other.getCenter().sub(finalPoint);
			
			if(dir.length() < radius)
				return new IntersectData(true, dir.normalized().mul(dir.length() - radius));
			

			return new IntersectData(false, new Vector3f(0, 0, 0));
		}
	}

	@Override
	public void transform(Vector3f translation){

	}
	
	private int clamp(int i, boolean width){
		if(i < 0)return 0;
		if(width && i >= heightMapWidth)return heightMapWidth - 1;
		if(!width && i >= heightMapHeight)return heightMapHeight - 1;
		return i;
	}

}
