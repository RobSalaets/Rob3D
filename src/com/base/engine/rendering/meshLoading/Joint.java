package com.base.engine.rendering.meshLoading;

import java.util.ArrayList;

import com.base.engine.core.Matrix4f;
import com.base.engine.core.Quaternion;

public class Joint{

	private Joint parent;
	private Matrix4f jointMatrix;
	private Quaternion jointQuaternion;
	private Matrix4f globalMatrix;
	private ArrayList<Joint> children;
	private Matrix4f invBindMatrix;
	private int id;

	public Joint(int id, Matrix4f jointMatrix, Matrix4f invBindMatrix){
		this.id = id;
		this.jointMatrix = jointMatrix;
		this.invBindMatrix = invBindMatrix;
		//Calc quaternion;
		this.globalMatrix = new Matrix4f().initIdentity();
		children = new ArrayList<Joint>();
	}

	public void addChild(Joint child){
		child.parent = this;
		child.globalMatrix = globalMatrix.mul(child.jointMatrix);
		children.add(child);
	}
	
	public void editJoint(Matrix4f newMat){
		this.globalMatrix = parent.globalMatrix.mul(this.jointMatrix.mul(newMat));
		for(int i = 0; i < children.size(); i++)
			children.get(i).globalMatrix = globalMatrix.mul(children.get(i).jointMatrix);
	}
	
	public ArrayList<Joint> getChildren(){
		return children;
	}
	
	public Matrix4f getInvBindMatrix(){
		return this.invBindMatrix;
	}

	public Joint getParent(){
		return parent;
	}

	public Matrix4f getJointMatrix(){
		return jointMatrix;
	}

	public Matrix4f getGlobalMatrix(){
		return globalMatrix;
	}
	
	public int getId(){
		return id;
	}
}
