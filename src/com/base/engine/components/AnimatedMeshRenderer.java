package com.base.engine.components;

import java.nio.FloatBuffer;

import com.base.engine.core.Matrix4f;
import com.base.engine.core.Time;
import com.base.engine.core.Util;
import com.base.engine.rendering.AnimatedMesh;
import com.base.engine.rendering.Material;
import com.base.engine.rendering.RenderingEngine;
import com.base.engine.rendering.Shader;
import com.base.engine.rendering.meshLoading.Joint;
import com.base.engine.rendering.meshLoading.Rig;

public class AnimatedMeshRenderer extends MeshRenderer{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4390051757897995886L;
	
	private Shader test;
	private Rig rig;

	public AnimatedMeshRenderer(AnimatedMesh mesh, Material material){
		super(mesh, material);
		rig = mesh.getRig();
		test = new Shader("forward-animated-ambient");
	}
	
	@Override
	public void render(Shader shader, RenderingEngine renderingEngine)
	{
		test.bind();
		calcJoints(shader);
		test.updateUniforms(getTransform(), material, renderingEngine);
		mesh.draw();
	}

	private void calcJoints(Shader shader){
		Joint root = rig.getRoot();
		root.getChildren().get(0).editJoint(new Matrix4f().initRotation(0, 45*(float)Math.sin(Time.getTime()),0));
		Matrix4f[] poses = new Matrix4f[rig.getJoints().size()];
		poses[root.getId()] = root.getGlobalMatrix().mul(root.getInvBindMatrix());
		recursivePoseInit(root, poses);
		FloatBuffer buffer = Util.createFloatBuffer(16*16);

		for(int m = 0; m < poses.length; m++)
			for(int i = 0; i < 4; i++)
				for(int j = 0; j < 4; j++)
					buffer.put(poses[m].get(i, j));
		buffer.flip();
		material.setFloatBuffer("animationJoints", buffer);
	}
	
	private void recursivePoseInit(Joint parent, Matrix4f[] poses){
		for(int i = 0; i < parent.getChildren().size(); i++){
			Joint joint = parent.getChildren().get(i);
			poses[joint.getId()] = joint.getGlobalMatrix().mul(joint.getInvBindMatrix());
			recursivePoseInit(joint, poses);
		}
	}

}
