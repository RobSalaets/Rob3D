package com.base.engine.rendering;


import java.io.Serializable;
import java.util.ArrayList;

import com.base.engine.core.persistence.SerializeUtil;
import com.base.engine.rendering.resourceManagement.MappedValues;


public class Material extends MappedValues implements Serializable
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9000371017295704982L;
	
	private static ArrayList<Material> materials = new ArrayList<>();

	public Material()
	{
		this(new Texture("testTex.png"), 1, 8, new Texture("default_normal.png"), new Texture("default_disp.png"), 0.0f, 0.0f);
	}
	
	public Material(Texture diffuse, float specularIntensity, float specularPower, Texture normalMap, Texture dispMap, float dispMapScale, float dispMapOffset){
		super();
		setTexture("diffuse", diffuse);
		setFloat("specularIntensity", specularIntensity);
		setFloat("specularPower", specularPower);
		setTexture("normalMap", normalMap);
		setTexture("dispMap", dispMap);
		float baseBias = dispMapScale/2.0f;
		setFloat("dispMapScale", dispMapScale);
		setFloat("dispMapBias", -baseBias + baseBias * dispMapOffset);
		setFloat("hasRays", 0.0f);
	}

	public Material(Texture texture){
		this(texture, 1, 8, new Texture("default_normal.png"), new Texture("default_disp.png"), 0.0f, 0.0f);
	}
	
	public Material(Texture texture, float intensity, float power){
		this(texture, intensity, power, new Texture("default_normal.png"), new Texture("default_disp.png"), 0.0f, 0.0f);
	}

	public static void serializeToDir(String directory){
		SerializeUtil.serializeMaterials(directory, materials);
	}
	
	public static void addFromFile(String directoryName){
		SerializeUtil.loadMaterials(directoryName, materials);
	}
	
	public void addToList(){
		materials.add(this);
	}
	
	public String toString(){
		String cleanDiffuse = getTexture("diffuse").getFileName().split("_diffuse")[0];
		String norm = getTexture("normalMap").getFileName().equals("default_normal") ? "" : "N-";
		boolean disp = !getTexture("dispMap").getFileName().equals("default_normal");
		float hdms = (getFloat("dispMapScale") / 2.0f);
		float cdb = (getFloat("dispMapBias") + hdms) / hdms;
		return cleanDiffuse + "-" + norm + (disp ? "D-" : "") + "SI:" + getFloat("specularIntensity") + "-SP:" + getFloat("specularPower") + (disp ? "-DS:" + getFloat("dispMapScale") + "-DB:" + cdb: "");
		
	}
	
}

