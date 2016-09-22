package com.base.engine.components;

import com.base.engine.core.*;

public class Camera extends GameComponent{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3163459600911233702L;
	private Matrix4f projection;
	private float fov;
	private float aspect;
	private float zNear;
	private float zFar;

	public Camera(float fov, float aspect, float zNear, float zFar){
		this.projection = new Matrix4f().initPerspective(fov, aspect, zNear, zFar);
		this.fov = fov;
		this.aspect = aspect;
		this.zNear = zNear;
		this.zFar = zFar;
	}
	
	public Camera(Matrix4f projection){
		this.projection = projection;
	}

	public Matrix4f getViewProjection(){
		Matrix4f cameraRotation = getTransform().getTransformedRot().conjugate().toRotationMatrix();
		Vector3f cameraPos = getTransform().getTransformedPos().mul(-1);

		Matrix4f cameraTranslation = new Matrix4f().initTranslation(cameraPos.getX(), cameraPos.getY(), cameraPos.getZ());

		return projection.mul(cameraRotation.mul(cameraTranslation));
	}
	
	public Matrix4f getViewMatrix(){
		Matrix4f cameraRotation = getTransform().getTransformedRot().conjugate().toRotationMatrix();
		Vector3f cameraPos = getTransform().getTransformedPos().mul(-1);

		Matrix4f cameraTranslation = new Matrix4f().initTranslation(cameraPos.getX(), cameraPos.getY(), cameraPos.getZ());
		return cameraRotation.mul(cameraTranslation);
	}

	@Override
	public void addToEngine(CoreEngine engine){
		engine.getRenderingEngine().addCamera(this);
	}
	
	public void setProjection(Matrix4f projection){
		this.projection = projection;
	}
	
	public Matrix4f getProjection(){
		return projection;
	}

	public float getFov(){
		return fov;
	}

	public float getAspect(){
		return aspect;
	}

	public float getzNear(){
		return zNear;
	}

	public float getzFar(){
		return zFar;
	}

	@Override
	public GameComponent getCopy(){
		return new Camera(fov, aspect, zNear, zFar);
	}
}
