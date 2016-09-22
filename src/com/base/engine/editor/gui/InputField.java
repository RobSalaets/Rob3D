package com.base.engine.editor.gui;

import javax.swing.BorderFactory;
import javax.swing.JTextField;

import com.base.engine.rendering.GameWindowDimension;
import com.base.engine.rendering.Window;

public class InputField extends GuiComponent{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5384683923024743181L;
	
	private Window parent;
	private float xTextFieldRatio;
	private float yRatio;
	private JTextField textField;

	public InputField(String componentName, Window parent, String text, float xLabelRatio, float xTextFieldRatio, float yRatio){
		super(componentName);
		this.setBounds((int)(parent.getFrame().getWidth() * xTextFieldRatio),(int)(parent.getFrame().getHeight() * yRatio), 60, 20);
		new Label(componentName + "_label", parent, text, xLabelRatio, yRatio);
		this.textField = new JTextField("");
		textField.setBorder(BorderFactory.createRaisedSoftBevelBorder());
		this.setLayout(null);
		textField.setBounds(0, 0, 60, 20);
		textField.setEditable(true);
		this.add(textField);
		
		this.parent = parent;
		this.xTextFieldRatio = xTextFieldRatio;
		this.yRatio = yRatio;
		
		this.parent.add(this);
		this.setBackground(componentBGColor);
	}

	@Override
	public void onResize(){
		float gameWidth = parent.isGameWindow() ? GameWindowDimension.getWidth() : 0;
		this.setBounds((int)(gameWidth + (parent.getFrame().getWidth() - gameWidth) * xTextFieldRatio),(int)(parent.getFrame().getHeight() * yRatio), 60, 20);
	}
	
	public String getInput(){
		return textField.getText();
	}
	
	public void setText(String text){
		textField.setText(text);
	}
	
	@Override
	public void update(){
		
	}
}
