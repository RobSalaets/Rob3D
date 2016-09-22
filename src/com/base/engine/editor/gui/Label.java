package com.base.engine.editor.gui;

import java.awt.Color;

import javax.swing.JLabel;

import com.base.engine.rendering.GameWindowDimension;
import com.base.engine.rendering.Window;

public class Label extends GuiComponent{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2917667538306431845L;
	
	private float xRatio;
	private float yRatio;
	private Window parent;
	private JLabel label;
	private int centerX;
	private int centerY;
	
	public Label(String componentName, Window parent, String text, float xRatio, float yRatio){
		super(componentName);
		this.label = new JLabel(text);
		label.setBounds((int)(parent.getFrame().getWidth() * xRatio),(int)(parent.getFrame().getHeight() * yRatio), 500, 20);
		
		this.parent = parent;
		this.xRatio = xRatio;
		this.yRatio = yRatio;
		this.centerX = 0;
		this.centerY = 0;
		this.parent.add(this);
		label.setForeground(new Color(0.75f,0.75f,0.75f));
	}

	@Override
	public void onResize(){
		float gameWidth = parent.isGameWindow() ? GameWindowDimension.getWidth() : 0;
		label.setBounds((int)(gameWidth + (parent.getFrame().getWidth() - gameWidth) * xRatio) - centerX,(int)(parent.getFrame().getHeight() * yRatio) - centerY, label.getWidth(), label.getHeight());
	}

	@Override
	public void update(){
		
	}
	
	public JLabel getLabel(){
		return label;
	}

	public Label center(){
		this.centerX = (int)(label.getText().length() * 6/2.0f);
		this.centerY = (int)(label.getHeight()/2.0f);
		onResize();
		return this;
	}

}
