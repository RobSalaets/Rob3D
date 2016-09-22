package com.base.engine.physics;

import java.util.ArrayList;

import com.base.engine.core.Matrix4f;
import com.base.engine.core.Quaternion;
import com.base.engine.core.Vector3f;
import com.base.engine.physics.Collider.ColliderType;

public class PhysicsEngine{

	private ArrayList<PhysicsObject> objects;
	
	public PhysicsEngine(){
		objects = new ArrayList<PhysicsObject>();
	}
	
	public void addObject(PhysicsObject object){
		objects.add(object);
	}
	
	public void simulate(float delta){
		for(int i = 0; i < objects.size(); i++){
			objects.get(i).integrate(delta);
		}
	}
	
	public void handleCollision(){
		for(int i = 0; i < objects.size(); i++){
			for(int j = i + 1; j < objects.size(); j++){
				IntersectData intersectData = objects.get(i).getCollider().intersect(objects.get(j).getCollider());
				if(intersectData.getDoesIntersect()){
//					Vector3f direction = intersectData.getDirection().normalized();
//					Vector3f otherDirection = direction.reflect(objects.get(i).getVelocity().normalized());
					
//					objects.get(i).setVelocity(objects.get(i).getVelocity().reflect(otherDirection));
//					objects.get(j).setVelocity(objects.get(j).getVelocity().reflect(direction));
					objects.get(i).setFlying(false);
					objects.get(j).setFlying(false);
					if(objects.get(i).getCollider().getType() == ColliderType.TYPE_HEIGHTMAP){
						Vector3f up = intersectData.getDirection().normalized().mul(-1);
						objects.get(j).setRot(objects.get(j).getRot().slerp(new Quaternion(new Matrix4f().initRotation(up.cross(up.cross(objects.get(j).getVelocity().normalized())).mul(-1), up)), 0.05f, true));
						objects.get(i).getVelocity().normalized().print();
						objects.get(j).getPos().set(objects.get(j).getPos().sub(intersectData.getDirection()));
						objects.get(j).setVelocity(up.cross(up.cross(objects.get(i).getVelocity().normalized())).mul(-1 * objects.get(j).getVelocity().length()));
						System.out.println("runs");
					}
					else if(objects.get(j).getCollider().getType() == ColliderType.TYPE_HEIGHTMAP){
						Vector3f up = intersectData.getDirection().normalized().mul(-1);
						objects.get(i).setRot(objects.get(i).getRot().slerp(new Quaternion(new Matrix4f().initRotation(up.cross(up.cross(objects.get(i).getVelocity().normalized())).mul(-1), up)), 0.05f, true));
						objects.get(i).getPos().set(objects.get(i).getPos().sub(intersectData.getDirection()));
						objects.get(i).setVelocity(up.cross(up.cross(objects.get(i).getVelocity().normalized())).mul(-1 * objects.get(i).getVelocity().length()));
					}
				}else{
					objects.get(i).setFlying(true);
					objects.get(j).setFlying(true);
				
				}
			}
		}
	}
	
	//temp
	public PhysicsObject getObject(int index){
		return objects.get(index);
	}
	
	public int getNumObjects(){
		return objects.size();
	}
}
