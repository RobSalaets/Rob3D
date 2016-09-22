package com.base.engine.rendering;


import com.base.engine.core.Vector2f;
import com.base.engine.core.Vector3f;
import com.base.engine.rendering.meshLoading.Rig;


public class Vertex
{
	public static final int SIZE = 11;
	public static final int ASIZE = 11 + 2 * Rig.MAX_JOINTS;


	private Vector3f pos;
	private Vector2f texCoord;
	private Vector3f normal;
	private Vector3f tangent;
	
	private int[] boneIDs;
	private float[] boneWeights;


	public Vertex(Vector3f pos)
	{
		this(pos, new Vector2f(0,0));
	}


	public Vertex(Vector3f pos, Vector2f texCoord)
	{
		this(pos, texCoord, new Vector3f(0,0,0));
	}


	public Vertex(Vector3f pos, Vector2f texCoord, Vector3f normal)
	{
		this(pos, texCoord, normal, new Vector3f(0,0,0));
	}


	public Vertex(Vector3f pos, Vector2f texCoord, Vector3f normal, Vector3f tangent)
	{
		this(pos, texCoord, normal, tangent, new int[]{}, new float[]{});
	}

	public Vertex(Vector3f pos, Vector2f texCoord, Vector3f normal, Vector3f tangent, int[] boneIDs, float[] boneWeights)
	{
		this.pos = pos;
		this.texCoord = texCoord;
		this.normal = normal;
		this.tangent = tangent;
		this.boneIDs = boneIDs;
		this.boneWeights = boneWeights;
	}
	
	public Vector3f getTangent() {
		return tangent;
	}


	public void setTangent(Vector3f tangent) {
		this.tangent = tangent;
	}


	public Vector3f getPos()
	{
		return pos;
	}


	public void setPos(Vector3f pos)
	{
		this.pos = pos;
	}


	public Vector2f getTexCoord()
	{
		return texCoord;
	}


	public void setTexCoord(Vector2f texCoord)
	{
		this.texCoord = texCoord;
	}


	public Vector3f getNormal()
	{
		return normal;
	}


	public void setNormal(Vector3f normal)
	{
		this.normal = normal;
	}
	
	public int[] getBoneIDs(){
		return boneIDs;
	}
	
	public float[] getBoneWeights(){
		return boneWeights;
	}
}
