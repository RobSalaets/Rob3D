package com.base.engine.rendering.meshLoading;

import java.util.ArrayList;

public class Rig{
	
	public static final int MAX_JOINTS = 4;

	private Joint root;
	private ArrayList<int[]> jointIndices; //per vertex
	private ArrayList<float[]> jointWeights; // pervertex
	private ArrayList<Joint> joints;

	public Rig(Joint root, ArrayList<Joint> joints){
		this.root = root;
		this.joints = joints;
		this.jointIndices = new ArrayList<int[]>();
		this.jointWeights = new ArrayList<float[]>();
	}
	
	public Joint getRoot(){
		return root;
	}
	
	public ArrayList<Joint> getJoints(){
		return joints;
	}
	
	public ArrayList<int[]> getJointIndices(){
		return this.jointIndices;
	}
	
	public ArrayList<float[]> getJointWeights(){
		return this.jointWeights;
	}	
}
