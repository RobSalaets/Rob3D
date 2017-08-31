package com.base.engine.components;

import static org.lwjgl.opengl.GL11.GL_CLIP_PLANE0;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NONE;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import com.base.engine.core.Transform;
import com.base.engine.core.Vector3f;
import com.base.engine.rendering.Material;
import com.base.engine.rendering.Mesh;
import com.base.engine.rendering.RenderPhase;
import com.base.engine.rendering.RenderingEngine;
import com.base.engine.rendering.Shader;
import com.base.engine.rendering.Texture;

public class Skydome extends GameComponent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6757372539095750740L;

	private Mesh dome;
	private boolean hasBottom;
	private Material material;
	private Shader skyShader;
	private Material material2;
	private String topFileName;
	private String bottomFileName;
	private Transform bottomTransform;


	public Skydome(String topFileName, String bottomFileName){
		this.topFileName = topFileName;
		this.bottomFileName = bottomFileName;
		this.skyShader = new Shader("skydomeShader");
		dome = new Mesh("skydome.obj");
		Texture topTexture = new Texture(topFileName, GL_TEXTURE_2D, GL_LINEAR, GL_RGBA, GL_RGBA, true, GL_NONE);
		material = new Material(topTexture);
		material.setVector3f("ambient", new Vector3f(1f, 1f, 1f));
		if(bottomFileName.equals("")){
			hasBottom = false;
		}else{
			hasBottom = true;
			Texture bottomTexture = new Texture(bottomFileName, GL_TEXTURE_2D, GL_LINEAR, GL_RGBA, GL_RGBA, true, GL_NONE);
			material2 = new Material(bottomTexture);
			material2.setVector3f("ambient", new Vector3f(1f, 1f, 1f));
			bottomTransform = new Transform();
			bottomTransform.rotate(Vector3f.xAxis, (float)Math.toRadians(180.0f));
		}
	}
	
	public Skydome(String fileName){
		this(fileName, "");
	}

	public Material getMaterial(){
		return material;
	}
	
	public String[] getMapNames(){
		return new String[]{topFileName, bottomFileName};
	}
	
	public void changeMap(String top, String bottom){
		material.setTexture("diffuse", new Texture(top, GL_TEXTURE_2D, GL_LINEAR, GL_RGBA, GL_RGBA, true, GL_NONE));
		topFileName = top;
		bottomFileName = bottom;
		if(!bottom.equals("")){
			hasBottom = true;
			material2.setTexture("diffuse", new Texture(bottom, GL_TEXTURE_2D, GL_LINEAR, GL_RGBA, GL_RGBA, true, GL_NONE));
		}else
			hasBottom = false;
	}
	
	public void render(Shader shader, RenderingEngine renderingEngine){
		if(renderingEngine.getRenderPhase() == RenderPhase.AMBIENT || renderingEngine.getRenderPhase() == RenderPhase.WATER_REFLECTION){
			
			glDisable(GL_CLIP_PLANE0);
			skyShader.bind();
			getTransform().setPos(renderingEngine.getMainCamera().getTransform().getTransformedPos().add(new Vector3f(0, -20f, 0)));
			skyShader.updateUniforms(getTransform(), material, renderingEngine);
			dome.draw();
			if(hasBottom){
				skyShader.bind();
				bottomTransform.setPos(renderingEngine.getMainCamera().getTransform().getTransformedPos().add(new Vector3f(0, -20f, 0)));
				skyShader.updateUniforms(bottomTransform, material2, renderingEngine);
				dome.draw();
			}
			glEnable(GL_CLIP_PLANE0);
		}
		if(renderingEngine.getRenderPhase() == RenderPhase.RAYS){
			shader.bind();
			shader.updateUniforms(getTransform(), material, renderingEngine);
			dome.draw();
			if(hasBottom){
				shader.bind();
				shader.updateUniforms(bottomTransform, material2, renderingEngine);
				dome.draw();
			}
		}
	}
}
