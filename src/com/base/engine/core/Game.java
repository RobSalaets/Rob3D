package com.base.engine.core;

import java.util.ArrayList;

import com.base.engine.profiling.ProfileTimer;
import com.base.engine.rendering.RenderingEngine;

public abstract class Game{

	private GameObject root;
	private ProfileTimer inputTimer;
	private ProfileTimer updateTimer;
	
	public void init(){}
	
	public void input(float delta){
		getInputTimer().startInvocation();
		getRootObject().inputAll(delta);
		getInputTimer().stopInvocation();
	}
	
	public void update(float delta){
		getUpdateTimer().startInvocation();
		getRootObject().updateAll(delta);
		
		getUpdateTimer().stopInvocation();
	}
	
	public void render(RenderingEngine renderingEngine){
		renderingEngine.render(getRootObject());
	}
	
	protected void addObject(GameObject object){
		getRootObject().addChild(object);
	}
	
	protected void addObject(ArrayList<GameObject> objects){
		getRootObject().addChild(objects);
	}
	
	protected void deleteObject(GameObject gameObject){
		getRootObject().deleteChild(gameObject);
	}
	
	protected void updateRoot(float delta){
		getRootObject().updateAll(delta);
	}

	private GameObject getRootObject(){
		if(root == null){
			root = new GameObject();
		}
		
		return root;
	}
	
	private ProfileTimer getInputTimer(){
		if(inputTimer == null)
			inputTimer = new ProfileTimer();
		return inputTimer;
	}
	
	protected ProfileTimer getUpdateTimer(){
		if(updateTimer == null)
			updateTimer = new ProfileTimer();
		return updateTimer;
	}

	public void setEngine(CoreEngine engine){
		getRootObject().setEngine(engine);
	}
	
	public double displayInputTime(double dividend, int whiteSpaceNum){
		return inputTimer.displayAndReset("Input Time: ", dividend, whiteSpaceNum);
	}
	
	public double displayUpdateTime(double dividend, int whiteSpaceNum){
		return updateTimer.displayAndReset("Update Time: ", dividend, whiteSpaceNum);
	}
	
}