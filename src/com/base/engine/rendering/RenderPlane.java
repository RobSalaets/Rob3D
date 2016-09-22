package com.base.engine.rendering;

import com.base.engine.core.Vector2f;
import com.base.engine.core.Vector3f;

public class RenderPlane{
	
	private Vertex[] verts;
	private int[] inds;
	private Mesh mesh;

	public RenderPlane(){
		
		verts = new Vertex[]{
				new Vertex(new Vector3f(-1.0f, -1.0f, 0.0f), new Vector2f(0,0)),	
				new Vertex(new Vector3f(-1.0f,  1.0f, 0.0f), new Vector2f(0,1)),	
				new Vertex(new Vector3f( 1.0f,  1.0f, 0.0f), new Vector2f(1,1)),	
				new Vertex(new Vector3f( 1.0f, -1.0f, 0.0f), new Vector2f(1,0))	
			};
			
		
		inds = new int[]{ 
				0,1,2,
				2,3,0
		};
		
		mesh = new Mesh(verts, inds, true);
	}
	
	public Mesh getMesh(){
		return mesh;
	}
}
