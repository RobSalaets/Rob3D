package com.base.engine.editor;

import com.base.engine.components.GameComponent;
import com.base.engine.core.Input;
import com.base.engine.core.Vector3f;

public class EditorMove extends GameComponent{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6929150895092701034L;

	public static final Vector3f yAxis = new Vector3f(0,1,0);
	
	private float speed;
	private int forwardKey;
	private int backwardKey;
	private int leftKey;
	private int rightKey;
	private int upKey;
	private int downKey;
	
	
	//temp
	private boolean sign;
	
	public EditorMove(float speed){
		this(speed, Input.KEY_Z, Input.KEY_S, Input.KEY_Q, Input.KEY_D, Input.KEY_SPACE , Input.KEY_LSHIFT);
	}
	
	public EditorMove(){
		this(20.0f, Input.KEY_Z, Input.KEY_S, Input.KEY_Q, Input.KEY_D, Input.KEY_SPACE , Input.KEY_LSHIFT);
	}
	
	public EditorMove(float speed, int forwardKey, int backwardKey, int leftKey, int rightKey, int upKey, int downKey){
		this.speed = speed;
		this.forwardKey = forwardKey;
		this.backwardKey = backwardKey;
		this.leftKey = leftKey;
		this.rightKey = rightKey;
		this.upKey = upKey;
		this.downKey = downKey;
	}
	
	@Override
	public void input(float delta){
		int wheelAmt = Input.getWheelAmt();
		speed = !Input.getKey(Input.KEY_LCONTROL) ? speed * (1200 + wheelAmt) / 1200 : speed;
		
		float movAmt = (float) (speed * delta);

		if(Input.getKey(forwardKey)) move(getTransform().getRot().getForward(), movAmt);
		if(Input.getKey(backwardKey)) move(getTransform().getRot().getForward(), -movAmt);
		if(Input.getKey(leftKey)) move(getTransform().getRot().getLeft(), movAmt);
		if(Input.getKey(rightKey)) move(getTransform().getRot().getRight(), movAmt);
		if(Input.getKey(upKey)) move(yAxis, movAmt);
		if(Input.getKey(downKey)) move(yAxis, -movAmt);
		if(Input.getKeyDown(Input.KEY_HOME)){
			getTransform().setPos(new Vector3f(0,0,0));
		}
	}

	public void move(Vector3f dir, float amt){
		getTransform().setPos(getTransform().getPos().add(dir.mul(amt)));
	}
}
