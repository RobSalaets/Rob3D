package com.base.engine.rendering.resourceManagement;

import java.io.Serializable;
import java.nio.FloatBuffer;
import java.util.HashMap;

import com.base.engine.core.Vector3f;
import com.base.engine.rendering.Texture;
import com.base.engine.rendering.TextureArray;

public abstract class MappedValues implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7885535108596699815L;
	private HashMap<String, Vector3f> vector3fHashMap;
	private HashMap<String, Float> floatHashMap;
	private HashMap<String, Texture> textureMap;
	private HashMap<String, TextureArray> textureArrayMap;
	private HashMap<String, FloatBuffer> bufferArrayMap;
	private HashMap<String, Boolean> boolmap;

	public MappedValues(){
		vector3fHashMap = new HashMap<String, Vector3f>();
		floatHashMap = new HashMap<String, Float>();
		textureMap = new HashMap<String, Texture>();
		textureArrayMap = new HashMap<String, TextureArray>();
		bufferArrayMap = new HashMap<String, FloatBuffer>();
		boolmap = new HashMap<String, Boolean>();
	}

	public void setVector3f(String name, Vector3f vec3){
		vector3fHashMap.put(name, vec3);
	}

	public void setFloat(String name, float f){
		floatHashMap.put(name, f);
	}

	public void setTexture(String name, Texture texture){
		textureMap.put(name, texture);
	}
	
	public void setTextureArray(String name, TextureArray textureArray){
		textureArrayMap.put(name, textureArray);
	}
	
	public void setBoolean(String name, boolean b){
		boolmap.put(name, b);
	}
	
	public boolean getBoolean(String name){
		return boolmap.get(name);
	}

	public Vector3f getVector3f(String name){
		Vector3f result = vector3fHashMap.get(name);
		if(result != null){
			return result;
		}
		return new Vector3f(0, 0, 0);
	}

	public float getFloat(String name){
		Float result = floatHashMap.get(name);
		if(result != null){
			return result;
		}
		return 0;
	}

	public Texture getTexture(String name){
		Texture result = textureMap.get(name);
		if(result != null){
			return result;
		}
		return new Texture("testTex.png");
	}
	
	public TextureArray getTextureArray(String name){
		TextureArray result = textureArrayMap.get(name);
		if(result != null){
			return result;
		}
		System.err.println("No such textureArray");
		return null;
	}
	
	public void setFloatBuffer(String name, FloatBuffer fb){
		bufferArrayMap.put(name, fb);
	}
	
	public FloatBuffer getFloatBuffer(String name){
		FloatBuffer result = bufferArrayMap.get(name);
		if(result != null){
			return result;
		}
		System.err.println("No such buffer");
		return null;
	}
}
