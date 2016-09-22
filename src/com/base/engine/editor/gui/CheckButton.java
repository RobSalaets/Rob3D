package com.base.engine.editor.gui;

import javax.swing.ImageIcon;

import com.base.engine.rendering.Window;

public class CheckButton extends Button{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8443058790412870506L;
	
	private boolean state;
	private ImageIcon checkIcon;

	public CheckButton(String componentName, Window parent, String toolTip, ImageIcon icon, boolean beginState, int width, int height, float xRatio, float yRatio){
		super(componentName, parent, beginState? icon : null, toolTip, width, height, xRatio, yRatio);
		this.icon.setIconTextGap(0);
		state = beginState;
		checkIcon = icon;
	}
	
	@Override
	public void update(){
		super.update();
		if(getClick()){
			state = !state;
			if(state)
				this.icon.setIcon(checkIcon);
			else
				this.icon.setIcon(null);
		}
	}
	
	public boolean getState(){
		return state;
	}

}
