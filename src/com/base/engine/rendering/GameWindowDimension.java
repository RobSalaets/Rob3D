package com.base.engine.rendering;

import org.lwjgl.opengl.Display;

import com.base.engine.core.Vector2f;

public class GameWindowDimension{

	public static int getWidth(){
		return Display.getDisplayMode().getWidth();
	}
	
	public static int getHeight(){
		return Display.getDisplayMode().getHeight();
	}
	
	public static float getAspect(){
		return (float) getWidth() / (float) getHeight();
	}
	
	public static Vector2f getCenter(){
		return new Vector2f(getWidth() / 2, getHeight() / 2);
	}
}
