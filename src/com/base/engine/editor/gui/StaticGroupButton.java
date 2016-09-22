package com.base.engine.editor.gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.Icon;

import com.base.engine.rendering.Window;

public class StaticGroupButton extends Button{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8556443177981731388L;

	private ArrayList<StaticGroupButton> group;

	public StaticGroupButton(String componentName, Window parent, String text, String toolTip, int width, int height, float xRatio, float yRatio){
		super("s_" + componentName, parent, text, toolTip, width, height, xRatio, yRatio);
		addMouseFunctionality();

	}

	public StaticGroupButton(String componentName, Window parent, Icon icon, String toolTip, int width, int height, float xRatio, float yRatio){
		super("s_" + componentName, parent, icon, toolTip, width, height, xRatio, yRatio);
		addMouseFunctionality();
	}

	private void addMouseFunctionality(){
		this.addMouseListener(new MouseListener() {
			public void mousePressed(MouseEvent arg0){
				if(enable){
					if(clicked){
						clicked = false;
						setBackground(componentBGColor);
					}else{
						press();
					}
					
				}
			}

			public void mouseClicked(MouseEvent arg0){
			}

			public void mouseEntered(MouseEvent e){

			}

			public void mouseExited(MouseEvent e){

			}

			public void mouseReleased(MouseEvent e){
			}
		});
	}

	public boolean getClick(){
		return clicked;
	}

	public StaticGroupButton press(){
		this.clicked = true;
		setBackground(buttonPressColor);
		for(StaticGroupButton b : group){
			if(!b.equals(this))
				b.unPress();
		}
		return this;
	}

	public void unPress(){
		System.out.println("rusn");
		this.clicked = false;
		setBackground(componentBGColor);
	}

	public void setGroup(ArrayList<StaticGroupButton> group){
		this.group = group;
	}

}
