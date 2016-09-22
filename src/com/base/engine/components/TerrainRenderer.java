package com.base.engine.components;

import static org.lwjgl.opengl.GL11.*;

import java.util.Arrays;

import com.base.engine.rendering.Material;
import com.base.engine.rendering.Mesh;
import com.base.engine.rendering.RenderingEngine;
import com.base.engine.rendering.Shader;
import com.base.engine.rendering.Texture;
import com.base.engine.rendering.TextureArray;

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
		this.material = material;
		this.mesh = new Mesh("terrainMesh.obj");
		this.ambient = new Shader("terrain-ambient");
		this.diffuse = new Shader("terrain-diffuse");
		this.raysprepass = new Shader("terrain-creprays-prepass");
		this.shadowprep = new Shader("terrain-shadowMapGenerator");

		if(heightMap.getWidth() != heightMap.getHeight()){
			System.err.println("heightMap is not square!");
			System.exit(1);
		}
		material.setFloat("mapSize", heightMap.getWidth());
		material.setTexture("heightMap", heightMap);
		material.setTexture("terrainNormals", normals);
		material.setFloat("unitScale", unitScale);
		material.setFloat("heightScale", heightScale);
		material.setFloat("tiling", tilingAmt);
		material.setFloat("numTextures", 1);
	}
	
	public TerrainRenderer(Texture heightMap, Texture normals, float unitScale, float heightScale, Material material, String[] textures, float tilingAmt){
		this.material = material;
		this.mesh = new Mesh("terrainMesh.obj");
		this.ambient = new Shader("terrain-ambient");
		this.diffuse = new Shader("terrain-diffuse");
		this.raysprepass = new Shader("terrain-creprays-prepass");
		this.shadowprep = new Shader("terrain-shadowMapGenerator");
		TextureArray texArray = new TextureArray(textures, GL_LINEAR_MIPMAP_LINEAR, GL_RGBA, GL_RGBA, false);
		String[] dispStrings = new String[textures.length];
		for(int i = 0; i < dispStrings.length; i++){
			dispStrings[i] = textures[i].replaceAll(".png", "_disp.png");
		}
		//TextureArray dispArray = new TextureArray(dispStrings, GL_LINEAR_MIPMAP_LINEAR, GL_RGBA, GL_RGBA, false);
		
		material.setFloat("mapSize", heightMap.getWidth());
		material.setTexture("heightMap", heightMap);
		material.setTexture("terrainNormals", normals);
		material.setFloat("unitScale", unitScale);
		material.setFloat("heightScale", heightScale);
		material.setFloat("tiling", tilingAmt);
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

	@Override
	public GameComponent getCopy(){
		throw new IllegalAccessError();
	}

}
