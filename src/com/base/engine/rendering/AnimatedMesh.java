package com.base.engine.rendering;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import java.util.ArrayList;

import org.lwjgl.opengl.GL15;

import com.base.engine.core.Util;
import com.base.engine.rendering.meshLoading.COLLADAModel;
import com.base.engine.rendering.meshLoading.IndexedModel;
import com.base.engine.rendering.meshLoading.Rig;
import com.base.engine.rendering.resourceManagement.MeshResource;

public class AnimatedMesh extends Mesh{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3986205405770484683L;
	
	private int indicesOffset;
	private int weightsOffset;
	private Rig rig;

	public AnimatedMesh(String fileName){
		super("animated-" + fileName);
		indicesOffset = 44;
		weightsOffset = indicesOffset + 4 * Rig.MAX_JOINTS;
	}
	
	public void draw(){
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glEnableVertexAttribArray(3);
		glEnableVertexAttribArray(4);
		glEnableVertexAttribArray(5);
		
		glBindBuffer(GL_ARRAY_BUFFER, resource.getVbo());
		glVertexAttribPointer(0, 3, GL_FLOAT, false, Vertex.ASIZE * 4, 0);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, Vertex.ASIZE * 4, 12);
		glVertexAttribPointer(2, 3, GL_FLOAT, false, Vertex.ASIZE * 4, 20);
		glVertexAttribPointer(3, 3, GL_FLOAT, false, Vertex.ASIZE * 4, 32);
		glVertexAttribPointer(4, Rig.MAX_JOINTS, GL_FLOAT, false, Vertex.ASIZE * 4, indicesOffset);
		glVertexAttribPointer(5, Rig.MAX_JOINTS, GL_FLOAT, false, Vertex.ASIZE * 4, weightsOffset);

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, resource.getIbo());
		glDrawElements(GL_TRIANGLES, resource.getSize(), GL_UNSIGNED_INT, 0);

		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glDisableVertexAttribArray(3);
		glDisableVertexAttribArray(4);
		glDisableVertexAttribArray(5);
	}
	
	protected void addVertices(Vertex[] vertices, int[] indices, boolean calcNormals){
		if(calcNormals){
			calcNormals(vertices, indices);
			calcTangents(vertices, indices);
		}

		resource = new MeshResource(indices.length);

		glBindBuffer(GL_ARRAY_BUFFER, resource.getVbo());
		GL15.glBufferData(GL_ARRAY_BUFFER, Util.createFlippedVertexBuffer(vertices, true), GL_STATIC_DRAW);

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, resource.getIbo());
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, Util.createFlippedBuffer(indices), GL_STATIC_DRAW);
	}
	
	protected Mesh loadMesh(String fileName){
		String[] splitArray = fileName.split("\\.");
		String ext = splitArray[splitArray.length - 1];

		if(!ext.equalsIgnoreCase("dae")){
			System.err.println("Error: '" + ext + "' file format not supported for animated mesh data.");
			new Exception().printStackTrace();
			System.exit(1);
		}
		IndexedModel model = null;
		COLLADAModel collada = new COLLADAModel("./res/models/" + fileName.replaceAll("animated-", ""), false);
		model = collada.toIndexedModel();
		

		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		
		for(int i = 0; i < model.getPositions().size(); i++){
			vertices.add(new Vertex(model.getPositions().get(i), model.getTexCoords().get(i), model.getNormals().get(i), model.getTangents().get(i), model.getRig().getJointIndices().get(i), model.getRig().getJointWeights().get(i)));
		}
		

		Vertex[] vertexData = new Vertex[vertices.size()];
		vertices.toArray(vertexData);

		Integer[] indexData = new Integer[model.getIndices().size()];
		model.getIndices().toArray(indexData);

		addVertices(vertexData, Util.toIntArray(indexData), false);
		this.rig = model.getRig();
		return this;
	}
	
	public Rig getRig(){
		return rig;
	}

}
