package com.base.engine.components;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameterf;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL30.GL_RG;
import static org.lwjgl.opengl.GL30.GL_RG32F;

import java.nio.FloatBuffer;

import com.base.engine.core.Util;
import com.base.engine.rendering.Material;
import com.base.engine.rendering.Mesh;
import com.base.engine.rendering.RenderingEngine;
import com.base.engine.rendering.Shader;
import com.base.engine.rendering.Texture;
import com.base.engine.rendering.TextureArray;
import com.base.engine.rendering.resourceManagement.TextureInitializer;

public class TerrainRenderer extends GameComponent{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4229471231938919062L;

	private Mesh mesh;
	private Material material;
	private Shader ambient;
	private Shader diffuse;
	private Shader raysprepass;
	private Shader shadowprep;
	
	
	public TerrainRenderer(Texture heightMap, Texture normals, float unitScale, float heightScale, Material material, float tilingAmt){
		if(heightMap.getWidth() != heightMap.getHeight()){
			System.err.println("heightMap is not square!");
			System.exit(1);
		}
		this.material = material;
		this.mesh = new Mesh("terrainMesh.obj");
		this.ambient = new Shader("terrain-ambient");
		this.diffuse = new Shader("terrain-diffuse");
		this.raysprepass = new Shader("terrain-creprays-prepass");
		this.shadowprep = new Shader("terrain-shadowMapGenerator");

		material.setFloat("mapSize", heightMap.getWidth());
		material.setTexture("heightMap", heightMap);
		material.setTexture("terrainNormals", normals);
		material.setFloat("unitScale", unitScale);
		material.setFloat("heightScale", heightScale);
		material.setFloat("tiling", tilingAmt);
		material.setFloat("numTextures", 1);
	}
	
	public TerrainRenderer(Texture heightMap, Texture normals, float unitScale, float heightScale, Material material, String[] textures, float tilingAmt){
		this(heightMap, normals, unitScale, heightScale, material, tilingAmt);
		TextureArray texArray = new TextureArray(textures, GL_LINEAR_MIPMAP_LINEAR, GL_RGBA, GL_RGBA, false);
//		String[] dispStrings = new String[textures.length];
//		for(int i = 0; i < dispStrings.length; i++){
//			dispStrings[i] = textures[i].replaceAll(".png", "_disp.png");
//		}
//		TextureArray dispArray = new TextureArray(dispStrings, GL_LINEAR_MIPMAP_LINEAR, GL_RGBA, GL_RGBA, false);
		
		material.setFloat("numTextures", textures.length);
		material.setTextureArray("diffuseTextures", texArray);
	}
	
	public void render(Shader shader, RenderingEngine renderingEngine){
		glDepthMask(true);
		switch(renderingEngine.getRenderPhase()){
			
			case SHADOW_PREP:
				shadowprep.bind();
				shadowprep.updateUniforms(getTransform(), material, renderingEngine);
				mesh.draw();
				break;
			case WATER_REFLECTION:
				diffuse.bind();
				diffuse.updateUniforms(getTransform(), material, renderingEngine);
				mesh.draw();
				break;
			case WATER_REFRACTION:
				diffuse.bind();
				diffuse.updateUniforms(getTransform(), material, renderingEngine);
				mesh.draw();
				break;
			case RAYS:
				raysprepass.bind();
				raysprepass.updateUniforms(getTransform(), material, renderingEngine);
				mesh.draw();
				break;
			case AMBIENT:
				ambient.bind();
				ambient.updateUniforms(getTransform(), material, renderingEngine);
				mesh.draw();
				break;
			case LIGHTING:
				glDepthFunc(GL_LEQUAL);
				renderingEngine.getActiveLight().getTerrainShader().bind();
				renderingEngine.getActiveLight().getTerrainShader().updateUniforms(getTransform(), material, renderingEngine);
				mesh.draw();
				break;
			default:
				break;
		}
	}
	
	/**
	 * For image with height in g-component
	 */
	public static float[] textureToHeightField(Texture heightTexture, float unitScale, float heightScale){
		int[] pixels = heightTexture.getPixels();
		float[] result = new float[pixels.length];
		for(int y = 0; y < heightTexture.getHeight(); y++){
			for(int x = 0; x < heightTexture.getWidth(); x++){
				int pixel = pixels[y * heightTexture.getWidth() + x];
				result[x + y * heightTexture.getWidth()] = ((pixel >> 8) & 0xFF) / 255.0f;
			}
		}
		return result;
	}
	
	public static Texture heightFieldToTexture(final float[] heightMap, int width, int height){
		
		return new Texture(GL_TEXTURE_2D, width, height, new TextureInitializer() {

			@Override
			public void initTexture(int textureTarget, int width, int height, int texture){
				glBindTexture(textureTarget, texture);

				glTexParameterf(textureTarget, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
				glTexParameterf(textureTarget, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

				glTexParameteri(textureTarget, GL_TEXTURE_WRAP_S, GL_REPEAT);
				glTexParameteri(textureTarget, GL_TEXTURE_WRAP_T, GL_REPEAT);
				
				FloatBuffer fb = Util.createFloatBuffer(heightMap.length * 2);
				for(float f : heightMap){
					fb.put(f);
					fb.put(f);
				}
				fb.flip();
				glTexImage2D(textureTarget, 0, GL_RG32F, width, height, 0, GL_RG, GL_FLOAT, fb);
				
			}
		});
		
	}
}

