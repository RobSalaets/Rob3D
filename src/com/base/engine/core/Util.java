package com.base.engine.core;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;

import org.lwjgl.BufferUtils;

import com.base.engine.rendering.Vertex;
import com.base.engine.rendering.meshLoading.Rig;

public class Util {
	
	public static FloatBuffer createFloatBuffer(int size){
		return BufferUtils.createFloatBuffer(size);
	}
	
	public static IntBuffer createIntBuffer(int size){
		return BufferUtils.createIntBuffer(size);
	}
	
	public static ByteBuffer createByteBuffer(int size){
		return BufferUtils.createByteBuffer(size);
	}
	
	public static IntBuffer createFlippedBuffer(int...values){
		IntBuffer buffer = createIntBuffer(values.length);
		buffer.put(values);
		buffer.flip();
		
		return buffer;
	}
	
	public static FloatBuffer createFlippedVertexBuffer(Vertex[] vertices, boolean animated)
	{
		FloatBuffer buffer = createFloatBuffer(vertices.length * (animated ? Vertex.ASIZE : Vertex.SIZE));


		for(int i = 0; i < vertices.length; i++)
		{
			buffer.put(vertices[i].getPos().getX());
			buffer.put(vertices[i].getPos().getY());
			buffer.put(vertices[i].getPos().getZ());
			buffer.put(vertices[i].getTexCoord().getX());
			buffer.put(vertices[i].getTexCoord().getY());
			buffer.put(vertices[i].getNormal().getX());
			buffer.put(vertices[i].getNormal().getY());
			buffer.put(vertices[i].getNormal().getZ());
			buffer.put(vertices[i].getTangent().getX());
			buffer.put(vertices[i].getTangent().getY());
			buffer.put(vertices[i].getTangent().getZ());
			if(animated){
				for(int j = 0; j < Rig.MAX_JOINTS; j++)
					buffer.put(vertices[i].getBoneIDs()[j]);
				for(int j = 0; j < Rig.MAX_JOINTS; j++){
					buffer.put(vertices[i].getBoneWeights()[j]);
				}
			}
		}


		buffer.flip();


		return buffer;
	}

	public static <T> ArrayList<T> flipArrayList(ArrayList<T> list){
		Collections.reverse(list);
		return list;	
	}
	
	public static FloatBuffer createFlippedBuffer(Matrix4f value){
		FloatBuffer buffer = createFloatBuffer(4*4);
		
		for(int i = 0; i < 4; i ++){
			for(int j = 0; j <4; j++){
				buffer.put(value.get(i, j));
			}
		}
		
		buffer.flip();
		
		return buffer;
	}
	
	public static String[] removeEmptyStrings(String[] data){
		ArrayList<String> result = new ArrayList<String>();
		
		for(int i =0; i < data.length; i++){
			if(!data[i].equals("")){
				result.add(data[i]);
			}
		}
		
		String[] res = new String[result.size()];
		result.toArray(res);
		return res;
	}
	
	public static int[] toIntArray(Integer[] data){
		int[] result = new int[data.length];
		
		for(int i = 0; i < data.length; i++){
			result[i] = data[i].intValue();
		}
		return result;
	}
	
	public static float[] parseToFloatArray(String[] content){
		float[] result = new float[content.length];
		for(int i = 0; i < content.length; i++)
			result[i] = Float.parseFloat(content[i]);
		return result;
	}
	
	public static int[] parseToInt(String[] content){
		int[] result = new int[content.length];
		for(int i = 0; i < content.length; i++)
			result[i] = Integer.parseInt(content[i]);
		return result;
	}
}
