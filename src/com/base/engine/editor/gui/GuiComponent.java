package com.base.engine.editor.gui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;

public abstract class GuiComponent extends JPanel{

	/**
	 * 
	 */
	private String name;
	
	public GuiComponent(String name){
		super(new BorderLayout());
		this.name = name;
	}
	
	private static final long serialVersionUID = -4988393264633730445L;
	protected static final Color componentBGColor = new Color(0.2f,0.26f,0.26f);
	protected static final Color componentHighLightColor = new Color(0.4f,0.52f,0.52f);
	protected static final Color windowBGColor = new Color(0.09f,0.15f,0.15f);
	protected static final Color buttonPressColor = new Color(0.3f, 0.3f, 0.5f);
	
	public abstract void onResize();
	public abstract void update();
	
	public String getName(){
		return name;
		
	}

}
