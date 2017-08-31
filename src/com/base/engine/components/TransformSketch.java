package com.base.engine.components;

import com.base.engine.rendering.RenderingEngine;
import com.base.engine.rendering.Shader;

public class TransformSketch extends GameComponent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private RenderingEngine re;
	
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
