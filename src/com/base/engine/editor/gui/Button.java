package com.base.engine.editor.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.base.engine.rendering.GameWindowDimension;
import com.base.engine.rendering.Window;

public class Button extends GuiComponent{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5384683923024743181L;

	protected Window parent;
	protected boolean enable;
	protected int width;
	protected int height;
	protected float xRatio;
	protected float yRatio;
	protected boolean clicked;
	protected int centerX;
	protected int centerY;
	protected JLabel icon;
	private JLabel text;

	public Button(String componentName, Window parent, String text, String toolTip, int width, int height, float xRatio, float yRatio){
		super(componentName.startsWith("s_")? componentName.substring(2): componentName);
		this.setBorder(BorderFactory.createRaisedSoftBevelBorder());
		this.setBounds((int) (parent.getFrame().getWidth() * xRatio), (int) (parent.getFrame().getHeight() * yRatio), width, height);

		this.setToolTipText(toolTip);
		this.text = new JLabel(text, SwingConstants.CENTER);
		this.text.setForeground(new Color(0.75f, 0.75f, 0.75f));
		this.add(this.text);

		this.setCursor(new Cursor(Cursor.HAND_CURSOR));

		this.width = width;
		this.parent = parent;
		this.height = height;
		this.xRatio = xRatio;
		this.yRatio = yRatio;
		this.centerX = 0;
		this.centerY = 0;

		this.parent.add(this);
		this.clicked = false;
		this.enable = true;
		if(!componentName.startsWith("s_"))
			addMouseFunctionality();
		this.setBackground(componentBGColor);
	}

	public Button(String componentName, Window parent, Icon icon, String toolTip, int width, int height, float xRatio, float yRatio){
		super(componentName.startsWith("s_")? componentName.substring(2): componentName);
		this.setBorder(BorderFactory.createRaisedSoftBevelBorder());
		this.setBounds((int) (parent.getFrame().getWidth() * xRatio), (int) (parent.getFrame().getHeight() * yRatio), width, height);
		this.setToolTipText(toolTip);
		this.setCursor(new Cursor(Cursor.HAND_CURSOR));
		this.icon = new JLabel(icon, SwingConstants.LEFT);
		this.add(this.icon);

		this.width = width;
		this.parent = parent;
		this.height = height;
		this.xRatio = xRatio;
		this.yRatio = yRatio;
		this.centerX = 0;
		this.centerY = 0;

		this.parent.add(this);
		this.clicked = false;
		this.enable = true;
		if(!componentName.startsWith("s_"))
			addMouseFunctionality();
		this.setBackground(componentBGColor);
	}

	private void addMouseFunctionality(){
		this.addMouseListener(new MouseListener() {
			public void mousePressed(MouseEvent arg0){
				if(enable){
					clicked = true;
				}
			}

			public void mouseClicked(MouseEvent arg0){
			}

			public void mouseEntered(MouseEvent e){
				if(enable) setBackground(componentHighLightColor);
			}

			public void mouseExited(MouseEvent e){
				if(enable) setBackground(componentBGColor);
			}

			public void mouseReleased(MouseEvent e){
			}
		});
	}

	@Override
	public void onResize(){
		float gameWidth = parent.isGameWindow() ? GameWindowDimension.getWidth() : 0;
		this.setBounds((int) (gameWidth + (parent.getFrame().getWidth() - gameWidth) * xRatio) - centerX, (int) (parent.getFrame().getHeight() * yRatio) - centerY, width, height);
	}

	@Override
	public void update(){

	}

	public boolean getClick(){
			if(clicked){
				clicked = false;
				return true;
			}

			return false;
	}

	public Button enableButton(){
		this.enable = true;
		return this;
	}

	public Button disableButton(){
		this.enable = false;
		return this;
	}

	public void addIcon(ImageIcon ii){
		JLabel icon = new JLabel(ii, SwingConstants.LEFT);
		this.add(icon);
		this.validate();
	}
	
	public void changeText(String text){
		this.text.setText(text);
	}

	public Button center(){
		this.centerX = (int) (width / 2.0f);
		this.centerY = (int) (height / 2.0f);
		onResize();
		return this;
	}
}
