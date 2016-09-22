package com.base.engine.rendering;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import com.base.engine.core.Game;
import com.base.engine.editor.gui.GuiComponent;
import com.base.engine.editor.gui.Label;
import com.base.game.LevelEditor;

public class Window{

	private JFrame frame;
	private Canvas canvas;
	private JPanel background;
	// private EditorContentPane contentPanel;
	private boolean isEditor;
	private boolean isGameWindow;

	private HashMap<String, GuiComponent> components;
	private HashMap<String, Window> sub;
	private ArrayList<JFrame> oldFrames;

	public Window(int width, int height, String title, Game game){
		this.isGameWindow = false;
		this.isEditor = false;
		
		
		if(game != null){

			if(game instanceof LevelEditor){
				isEditor = true;
				frame = new JFrame();
				canvas = new Canvas();
				background = new JPanel();
				components = new HashMap<String, GuiComponent>();
				sub = new HashMap<>();
				oldFrames = new ArrayList<>();
				frame.setSize(width, height);
				canvas.setSize(width, height);
				frame.setIconImage(new ImageIcon("./res/textures/gui/icon_application_logo.png").getImage());
				frame.setLocationRelativeTo(null);
				frame.add(canvas);
				frame.add(background);
				canvas.setFocusable(true);
				canvas.requestFocus();
				frame.setVisible(true);
				frame.setTitle(title);
				background.setBackground(new Color(0.1f, 0.16f, 0.16f));
				background.setLayout(null);
				frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

				
				frame.addComponentListener(new ComponentAdapter() {
					public void componentResized(ComponentEvent comp){
						resizeInterface();
					}
				});

				Display.setTitle(title);
				try{
					Display.setParent(canvas);
					Display.setDisplayMode(new DisplayMode(width, height));
					Display.create();
					Keyboard.create();
					Mouse.create();
				}catch (LWJGLException e){
					e.printStackTrace();
				}
			}else{
				Display.setTitle(title);
				try{
					Display.setDisplayMode(new DisplayMode(width, height));
					Display.create();
					Keyboard.create();
					Mouse.create();
				}catch (LWJGLException e){
					e.printStackTrace();
				}
			}

			this.isGameWindow = true;
		}else{
			frame = new JFrame();
			background = new JPanel();
			components = new HashMap<String, GuiComponent>();
			sub = new HashMap<>();
			oldFrames = new ArrayList<>();
			frame.setSize(width, height);
			frame.setIconImage(new ImageIcon("./res/textures/gui/icon_application_logo.png").getImage());
			frame.setLocationRelativeTo(null);
			frame.add(background);
			frame.setVisible(true);
			frame.setTitle(title);
			background.setBackground(new Color(0.1f, 0.16f, 0.16f));
			background.setLayout(null);
			frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			frame.addComponentListener(new ComponentAdapter() {
				public void componentResized(ComponentEvent comp){
					resizeInterface();
				}
			});
		}
	}

	public Window(int width, int height, String title){
		this(width, height, title, null);
	}

	public void render(){
		Display.update();
	}

	public void updateComponents(){
		Iterator<Entry<String, Window>> wit = sub.entrySet().iterator();
		while(wit.hasNext()){
			Entry<String, Window> current = wit.next();
			if(!current.getValue().getFrame().isVisible())
				oldFrames.add(sub.remove(current.getKey()).getFrame());
			else
				current.getValue().updateComponents();
		}
		
		Iterator<GuiComponent> it = components.values().iterator();
		while(it.hasNext()){
			it.next().update();
		}
	}

	public void dispose(){
		if(isGameWindow){
			Display.destroy();
			Keyboard.destroy();
			Mouse.destroy();
		}
		if(frame != null) frame.dispose();
		for(int i = 0; i < oldFrames.size(); i++)
			oldFrames.get(i).dispose();
		for(int i = 0; i < sub.size(); i++)
			sub.get(i).dispose();
		
	}

	public void bindAsRenderTarget(){
		glBindTexture(GL_TEXTURE_2D, 0);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glViewport(0, 0, GameWindowDimension.getWidth(), GameWindowDimension.getHeight());
	}

	public boolean isCloseRequested(){
		if(isEditor) return !frame.isVisible();
		return Display.isCloseRequested();
	}

	public String getTitle(){
		return Display.getTitle();
	}

	public boolean isEditor(){
		return isEditor;
	}

	public boolean isGameWindow(){
		return isGameWindow;
	}

	public JFrame getFrame(){
		return frame;
	}

	private void resizeInterface(){
		Iterator<GuiComponent> it = components.values().iterator();
		while(it.hasNext()){
			it.next().onResize();
		}
	}

	public void add(GuiComponent guiComponent){
		if(guiComponent instanceof Label) background.add(((Label) guiComponent).getLabel());
		background.add(guiComponent);
		components.put(guiComponent.getName(), guiComponent);
	}

	public void addSubWindow(String windowName, Window window){
		sub.put(windowName, window);
	}

	public GuiComponent getGuiComponent(String name){
		return this.components.get(name);
	}

	public void removeGuiComponent(String string){
		components.get(string).setVisible(false);
		background.remove(components.get(string));
		this.components.remove(string);
	}
	
	public Window getSubWindow(String name){
		return this.sub.get(name);
	}


	// public static EditorContentPane getEditorContentPane(){
	// if(contentPanel != null) return contentPanel;
	// System.err.println("Null bitch");
	// throw new NullPointerException();
	// }

	// public static void setEditorContentPane(EditorContentPane ecp){
	// contentPanel = ecp;
	// frame.add(contentPanel);
	// }
}
