package com.base.engine.physics;

import java.util.ArrayList;

import com.base.engine.components.GameComponent;
import com.base.engine.core.Vector3f;

public class PhysicsEngine extends GameComponent{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6373949181654982715L;
	
	private ArrayList<PhysicsObject> objects;
	private static Vector3f gravitationalForce = new Vector3f(0, -9.81f*0.2f, 0);

	
	public PhysicsEngine(){
		objects = new ArrayList<PhysicsObject>();
	}
	
	public void addObject(PhysicsObject object){
		objects.add(object);
	}
	
	@Override
	public void update(float delta){
		simulate(delta);
		handleForces(delta);
	}
	
	public void simulate(float delta){
		for(int i = 0; i < objects.size(); i++){
			objects.get(i).advance(delta);
		}
	}
	
	public void handleForces(float delta){
		for(int i = 0; i < objects.size(); i++){
			for(int j = i + 1; j < objects.size(); j++){
				IntersectData intersectData = objects.get(i).getCollider().intersect(objects.get(j).getCollider());
				if(intersectData.getDoesIntersect()){
					objects.get(i).setContact(true);
					objects.get(j).setContact(true);
					if(objects.get(i).isFixed() ^ objects.get(j).isFixed()){
						if(objects.get(i).isFixed()){
							objects.get(j).getTransform().setPos(objects.get(j).getTransform().getPos().add(intersectData.getDirection().mul(-intersectData.getDistance())));
							objects.get(j).setVelocity(objects.get(i).getVelocity().add(gravitationalForce.sub(intersectData.getDirection().mul(gravitationalForce.dot(intersectData.getDirection())))));
						}else{
							objects.get(i).getTransform().setPos(objects.get(i).getTransform().getPos().add(intersectData.getDirection().mul(-intersectData.getDistance())));
							objects.get(i).setVelocity(objects.get(i).getVelocity().add(gravitationalForce.sub(intersectData.getDirection().mul(gravitationalForce.dot(intersectData.getDirection())))));
						}
					}
					
//					if(objects.get(i).getCollider().getType() == ColliderType.TYPE_HEIGHTMAP){
//						Vector3f up = intersectData.getDirection().normalized().mul(-1);
//						objects.get(j).setRot(objects.get(j).getRot().slerp(new Quaternion(new Matrix4f().initRotation(up.cross(up.cross(objects.get(j).getVelocity().normalized())).mul(-1), up)), 0.05f, true));
//						objects.get(i).getVelocity().normalized().print();
//						objects.get(j).getPos().set(objects.get(j).getPos().sub(intersectData.getDirection()));
//						objects.get(j).setVelocity(up.cross(up.cross(objects.get(i).getVelocity().normalized())).mul(-1 * objects.get(j).getVelocity().length()));
//						System.out.println("runs");
//					}
//					else if(objects.get(j).getCollider().getType() == ColliderType.TYPE_HEIGHTMAP){
//						Vector3f up = intersectData.getDirection().normalized().mul(-1);
//						objects.get(i).setRot(objects.get(i).getRot().slerp(new Quaternion(new Matrix4f().initRotation(up.cross(up.cross(objects.get(i).getVelocity().normalized())).mul(-1), up)), 0.05f, true));
//						objects.get(i).getPos().set(objects.get(i).getPos().sub(intersectData.getDirection()));
//						objects.get(i).setVelocity(up.cross(up.cross(objects.get(i).getVelocity().normalized())).mul(-1 * objects.get(i).getVelocity().length()));
//					}
				}else{
					objects.get(i).setContact(false);
					objects.get(j).setContact(false);
					if(objects.get(i).doGravity())
						objects.get(i).setVelocity(objects.get(i).getVelocity().add(gravitationalForce.mul(delta)));
					if(objects.get(j).doGravity())
						objects.get(j).setVelocity(objects.get(j).getVelocity().add(gravitationalForce.mul(delta)));
				}
			}
		}
	}
}
