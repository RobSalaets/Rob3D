package com.base.engine.components;

import com.base.engine.core.Input;
import com.base.engine.core.Vector3f;

public class FreeMove extends GameComponent{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7743562325786440051L;
	private float speed;
	private int forwardKey;
	private int backwardKey;
	private int leftKey;
	private int rightKey;
	private boolean rotates;
	
	public FreeMove(float speed){
		this(speed, Input.KEY_Z, Input.KEY_S, Input.KEY_Q, Input.KEY_D, false);
	}
	
	public FreeMove(){
		this(10.0f, Input.KEY_Z, Input.KEY_S, Input.KEY_Q, Input.KEY_D, false);
	}
	
	public FreeMove(float speed, int forwardKey, int backwardKey, int leftKey, int rightKey, boolean rotates){
		this.speed = speed;
		this.forwardKey = forwardKey;
		this.backwardKey = backwardKey;
		this.leftKey = leftKey;
		this.rightKey = rightKey;
		this.rotates = rotates;
	}
	
	@Override
	public void input(float delta){
		float movAmt = (float) (speed * delta);

		if(Input.getKey(forwardKey)) move(getTransform().getRot().getForward(), movAmt);
		if(Input.getKey(backwardKey)) move(getTransform().getRot().getForward(), -movAmt);
		if(rotates){
			if(Input.getKey(leftKey)) getTransform().rotate(getTransform().getTransformedRot().getUp(), (float)Math.toRadians(-speed*2 * delta));
			if(Input.getKey(rightKey)) getTransform().rotate(getTransform().getTransformedRot().getUp(), (float)Math.toRadians(speed*2 * delta));
		}else{
			if(Input.getKey(leftKey)) move(getTransform().getRot().getLeft(), movAmt);
			if(Input.getKey(rightKey)) move(getTransform().getRot().getRight(), movAmt);			
		}

	}

	public void move(Vector3f dir, float amt){
		getTransform().setPos(getTransform().getPos().add(dir.mul(amt)));
	}
}
