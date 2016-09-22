package com.base.game;

import com.base.engine.components.GameComponent;
import com.base.engine.core.Input;
import com.base.engine.core.Time;
import com.base.engine.core.Vector3f;
import com.base.engine.rendering.RenderingEngine;
import com.base.engine.rendering.Shader;

public class TransformSketch extends GameComponent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private RenderingEngine re;

	@Override
	public GameComponent getCopy(){
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void update(float delta){
		if(re != null){
			getTransform().setPos(re.getMainCamera().getTransform().getPos().add(re.getMainCamera().getTransform().getRot().getForward().mul(0.5f)));
			
		}
	}
	
	public void render(Shader shader, RenderingEngine renderingEngine){
		re = renderingEngine;
	}

}
