package com.base.engine.core;

import java.io.Serializable;

public class Vector2f implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2719536880479796840L;
	private float x;
	private float y;
	
	public Vector2f(float x, float y){
		this.x = x;
		this.y = y;
	}
	
	public float length(){ 
		return  (float)Math.sqrt(x*x + y*y);
	}
	
	public float dot(Vector2f r){ 
		return x * r.getX() + y * r.getY();
	}
	
	public Vector2f normalize(){ 
		float length = length();
		
		return new Vector2f(x/length, y/length);
	}

	public float dot(Vector3f r){
		return x * r.getX() + y * r.getZ();
	}
	
	public Vector2f rotate(float angle){
		double rad = Math.toRadians(angle);
		double cos = Math.cos(rad);
		double sin = Math.sin(rad);
		
		return new Vector2f((float)(x * cos - y * sin),(float) (x * sin + y * cos));
	}
	
	public float max(){
		return  Math.max(x, y);
	}
	
	public Vector2f add(Vector2f r){
		return new Vector2f(x + r.getX(), y + r.getY());
	}
	
	public Vector2f add(float r){
		return new Vector2f(x + r, y + r);
	}
	
	public Vector2f sub(Vector2f r){
		return new Vector2f(x - r.getX(), y - r.getY());
	}
	
	public Vector2f sub(float r){
		return new Vector2f(x - r, y - r);
	}
	
	public Vector2f mul(Vector2f r){
		return new Vector2f(x * r.getX(), y * r.getY());
	}
	
	public Vector2f mul(float r){
		return new Vector2f(x * r, y * r);
	}
	
	public Vector2f div(Vector2f r){
		return new Vector2f(x / r.getX(), y / r.getY());
	}
	
	public Vector2f div(float r){
		return new Vector2f(x / r, y / r);
	}
	
	public float cross(Vector2f r){
		return x * r.getY() - y * r.getX();
	}
	
	public Vector2f lerp(Vector2f dest, float lerpFactor){
		return dest.sub(this).mul(lerpFactor).add(this);
	}
	
	public boolean equals(Vector2f other){
		return x == other.getX() && y == other.getY();
	}
	
	public static Vector2f randomVector(){
		float x, y;
		do{
			x = ((float) Math.random() - 0.5f) * 2;
			y = ((float) Math.random() - 0.5f) * 2;
		}while(x * x + y * y > 1);
		return new Vector2f(x,y).normalize();
	}
	
	public String toString(){
		return "(" + x +", " + y + ")";
	}
	
	public Vector2f abs(){
		return new Vector2f(Math.abs(x), Math.abs(y));
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public void print(){
		System.out.println(this.toString());
	}

	public Vector3f toVec3(){
		return new Vector3f(x,0,y);
	}
}
