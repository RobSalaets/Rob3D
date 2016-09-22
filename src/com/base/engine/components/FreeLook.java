package com.base.engine.components;

import com.base.engine.core.Input;
import com.base.engine.core.Vector2f;
import com.base.engine.core.Vector3f;
import com.base.engine.rendering.GameWindowDimension;

public class FreeLook extends GameComponent{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5149435016890466046L;

	public static final Vector3f yAxis = new Vector3f(0, 1, 0);

	private boolean mouseLocked;
	private float sensitivity;
	private int unlockMouseKey;
	
	public FreeLook(float sensitivity){
		this(sensitivity, Input.KEY_ESCAPE);
	}
	
	public FreeLook(){
		this(0.5f, Input.KEY_ESCAPE);
	}
	
	public FreeLook(float sensitivity, int unlockMouseKey){
		this.sensitivity = sensitivity;
		this.unlockMouseKey = unlockMouseKey;
		mouseLocked = false;
	}
	
	@Override
	public void input(float delta){
		Vector2f centerPosition = GameWindowDimension.getCenter();

		if(Input.getKey(this.unlockMouseKey) || Input.getMouseDown(2)){
			Input.setCursor(true);
			mouseLocked = false;
		}
		if(Input.getMouseDown(0)){
			Input.setMousePosition(centerPosition);
			Input.setCursor(false);
			mouseLocked = true;
		}

		if(mouseLocked){
			Vector2f deltaPos = Input.getMousePosition().sub(centerPosition);
			boolean rotY = deltaPos.getX() != 0;
			boolean rotX = deltaPos.getY() != 0;

			if(rotY){
				getTransform().rotate(yAxis, (float) Math.toRadians(deltaPos.getX() * sensitivity * delta));
			}
			if(rotX){
				getTransform().rotate(getTransform().getRot().getLeft(), (float) Math.toRadians(deltaPos.getY() * sensitivity * delta));
			}

			if(rotY || rotX) Input.setMousePosition(centerPosition);
		}
	}

	@Override
	public GameComponent getCopy(){
		return new FreeLook(sensitivity, unlockMouseKey);
	}
}
