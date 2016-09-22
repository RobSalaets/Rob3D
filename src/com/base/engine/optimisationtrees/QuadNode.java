package com.base.engine.optimisationtrees;

import java.util.ArrayList;

import com.base.engine.core.Vector2f;

public class QuadNode{
	
	//NOTE: non-leaf nodes may contain trash points and data;
	
	public QuadNode[] children;
	public QuadNode parent;
	public Vector2f center;
	public float halfRange;
	public boolean isLeaf;
	public int occupants;
	public Object[] data;
	public Vector2f[] points;
	
	public QuadNode(QuadNode parent, Vector2f center, float halfRange, int maxPerCell){
		this.children = new QuadNode[4];
		this.data = new Object[maxPerCell];
		this.points = new Vector2f[maxPerCell];
		this.center = center;
		this.halfRange = halfRange;
		this.occupants = 0;
		this.isLeaf = true;
		this.parent = parent;
	}
	
	
	public void addAllQueryResults(ArrayList<Object> data, QuadTreeQuery qtq){
		if(isLeaf){
			if(occupants > 0)
				data.add(qtq.returnData(this));
		}
		else{
			for(QuadNode n: children)
				n.addAllQueryResults(data, qtq);
		}
	}
}
