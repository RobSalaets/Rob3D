package com.base.engine.components;
import com.base.engine.rendering.Material;
import com.base.engine.rendering.Mesh;
import com.base.engine.rendering.RenderingEngine;
import com.base.engine.rendering.Shader;


public class MeshRenderer extends GameComponent
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4055730670592362401L;
	protected  Mesh mesh;
	protected  Material material;


	public MeshRenderer(Mesh mesh, Material material)
	{
		this.mesh = mesh;
		this.material = material;
	}
	
	@Override
	public void render(Shader shader, RenderingEngine renderingEngine)
	{
		shader.bind();
		shader.updateUniforms(getTransform(), material, renderingEngine);
		mesh.draw();
	}
	
	public void setMaterial(Material material){
		this.material = material;
	}
	
	public Material getMaterial(){
		return material;
	}
	
	public Mesh getMesh(){
		return mesh;
	}
	@Override
	public GameComponent getCopy(){
		return new MeshRenderer(mesh, material);
	}
}


