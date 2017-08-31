package com.base.engine.core;

import java.io.Serializable;

public class Vector3f implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2195168526725276099L;

	public static final Vector3f zeroVector = new Vector3f(0, 0, 0);
	public static final Vector3f xAxis = new Vector3f(1, 0, 0);
	public static final Vector3f yAxis = new Vector3f(0, 1, 0);
	public static final Vector3f zAxis = new Vector3f(0, 0, 1);

	private float x;
	private float y;
	private float z;

	public Vector3f(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3f(){
		x = 0;
		y = 0;
		z = 0;
	}

	public Vector3f(float[] vecArray){
		if(vecArray.length < 3){
			System.err.println("Error: Vector3f initialization failed, array is too small");
			throw new IllegalArgumentException();
		}
		x = vecArray[0];
		y = vecArray[1];
		z = vecArray[2];
	}

	public Vector3f(float f){
		x = f;
		y = f;
		z = f;
	}

	public Vector3f(String s){
		String noBraces = s.substring(1, s.length() - 1);
		String[] components = noBraces.split(",");
		try{
			x = Float.parseFloat(components[0].trim());
			y = Float.parseFloat(components[1].trim());
			z = Float.parseFloat(components[2].trim());
		}catch (Exception e){
			System.err.print("Vector creation from string should follow the toString()-methods format");
			x = 0;
			y = 0;
			z = 0;
		}
	}

	public float length(){
		return (float) Math.sqrt(x * x + y * y + z * z);
	}

	public float max(){
		return Math.max(x, Math.max(y, z));
	}

	public float dot(Vector3f r){
		return x * r.getX() + y * r.getY() + z * r.getZ();
	}

	public Vector3f cross(Vector3f r){
		float x_ = y * r.getZ() - z * r.getY();
		float y_ = z * r.getX() - x * r.getZ();
		float z_ = x * r.getY() - y * r.getX();

		return new Vector3f(x_, y_, z_);
	}

	public Vector3f normalized(){
		float length = length();

		return new Vector3f(x / length, y / length, z / length);
	}

	public Vector3f rotate(Vector3f axis, float angle){
		float sinAngle = (float) Math.sin(-angle);
		float cosAngle = (float) Math.cos(-angle);

		return this.cross(axis.mul(sinAngle)).add( // Rotation on local X
				(this.mul(cosAngle)).add( // Rotation on local Z
						axis.mul(this.dot(axis.mul(1 - cosAngle))))); // Rotation
																		// on
																		// local
																		// Y
	}

	public Vector3f rotate(Quaternion rotation){
		Quaternion conjugate = rotation.conjugate();

		Quaternion w = rotation.mul(this).mul(conjugate);

		return new Vector3f(w.getX(), w.getY(), w.getZ());
	}

	public Vector3f lerp(Vector3f dest, float lerpFactor){
		return dest.sub(this).mul(lerpFactor).add(this);
	}

	public Vector3f add(Vector3f r){
		return new Vector3f(x + r.getX(), y + r.getY(), z + r.getZ());
	}

	public Vector3f add(float r){
		return new Vector3f(x + r, y + r, z + r);
	}

	public Vector3f sub(Vector3f r){
		return new Vector3f(x - r.getX(), y - r.getY(), z - r.getZ());
	}

	public Vector3f sub(float r){
		return new Vector3f(x - r, y - r, z - r);
	}

	public Vector3f mul(Vector3f r){
		return new Vector3f(x * r.getX(), y * r.getY(), z * r.getZ());
	}

	public Vector3f mul(float r){
		return new Vector3f(x * r, y * r, z * r);
	}

	public Vector3f div(Vector3f r){
		return new Vector3f(x / r.getX(), y / r.getY(), z / r.getZ());
	}

	public Vector3f div(float r){
		return new Vector3f(x / r, y / r, z / r);
	}

	public Vector3f abs(){
		return new Vector3f(Math.abs(x), Math.abs(y), Math.abs(z));
	}

	public String toString(){
		return "(" + x + "f, " + y + "f, " + z + "f)";
	}

	public Vector2f getXY(){
		return new Vector2f(x, y);
	}

	public Vector2f getYZ(){
		return new Vector2f(y, z);
	}

	public Vector2f getZX(){
		return new Vector2f(z, x);
	}

	public Vector2f getYX(){
		return new Vector2f(y, x);
	}

	public Vector2f getZY(){
		return new Vector2f(z, y);
	}

	public Vector2f getXZ(){
		return new Vector2f(x, z);
	}

	public Vector3f set(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	public Vector3f set(Vector3f r){
		set(r.getX(), r.getY(), r.getZ());
		return this;
	}

	public float getX(){
		return x;
	}

	public Vector3f setX(float x){
		this.x = x;
		return this;
	}

	public float getY(){
		return y;
	}

	public Vector3f setY(float y){
		this.y = y;
		return this;
	}

	public float getZ(){
		return z;
	}

	public Vector3f setZ(float z){
		this.z = z;
		return this;
	}

	public float[] toArray(){
		return new float[] { x, y, z };
	}

	public boolean equals(Vector3f r){
		return x == r.getX() && y == r.getY() && z == r.getZ();
	}

	/**
	 * Returns a vector with the highest component of each index
	 */
	public Vector3f max(Vector3f r){
		float[] resultArray = new float[3];
		for(int i = 0; i < 3; i++){
			resultArray[i] = this.toArray()[i] > r.toArray()[i] ? this.toArray()[i] : r.toArray()[i];
		}
		return new Vector3f(resultArray);
	}

	/**
	 * Returns a random normalized vector
	 */

	public static Vector3f randomVector(){
		float x, y, z;
		do{
			x = ((float) Math.random() - 0.5f) * 2;
			y = ((float) Math.random() - 0.5f) * 2;
			z = ((float) Math.random() - 0.5f) * 2;
		}while(x * x + y * y + z * z > 1);
		return new Vector3f(x,y,z).normalized();
	}

	/**
	 * Returns a random vector within a range
	 */

	public static Vector3f randomVector(float range){
		return randomVector().mul(range);
	}

	public Vector3f reflect(Vector3f normal){
		Vector3f projVec = normal.normalized().mul(this.dot(normal.normalized()));
		Vector3f diffrence = projVec.sub(this);
		return this.add(diffrence).add(diffrence);
	}

	public void print(){
		System.out.println(this.toString());
	}

	public Vector3f snap(float amount){
		return new Vector3f((float) Math.floor(x / amount) * amount, (float) Math.floor(y / amount) * amount, (float) Math.floor(z / amount) * amount);
	}
}
