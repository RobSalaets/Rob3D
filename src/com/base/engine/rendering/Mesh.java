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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.opengl.GL15;

import com.base.engine.core.Util;
import com.base.engine.core.Vector2f;
import com.base.engine.core.Vector3f;
import com.base.engine.rendering.meshLoading.COLLADAModel;
import com.base.engine.rendering.meshLoading.IndexedModel;
import com.base.engine.rendering.meshLoading.OBJModel;
import com.base.engine.rendering.resourceManagement.MeshResource;

public class Mesh implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6063472053376068584L;
	private static HashMap<String, MeshResource> loadedModels = new HashMap<String, MeshResource>();
	protected MeshResource resource;
	private String fileName;

	private Vertex[] possibleVertices;
	private int[] possibleIndices;
	private boolean calcNormals;

	public Mesh(String fileName){
		this.fileName = fileName;
		if(!fileName.equals("")){
			MeshResource oldResource = loadedModels.get(fileName);

			if(oldResource != null){
				resource = oldResource;
				resource.addReference();
			}else{
				loadMesh(fileName);
				loadedModels.put(fileName, resource);
			}
		}else{
			System.err.println("Mesh filename can't be empty");
			throw new IllegalArgumentException();
		}
	}

	public Mesh(Texture heightMap, float unitScale, float heightScale, float tilingAmount){
		Vertex[] vertices = new Vertex[(int) (heightMap.getWidth() * heightMap.getHeight())];
		int[] indices = new int[(int) ((heightMap.getWidth() - 1.0f) * (heightMap.getHeight() - 1.0f)) * 6];
		heightScale /= 128.0f;
		if(heightScale == 0.0f)heightScale = 1.0f;
		for(int j = 0; j < heightMap.getHeight(); j++){
			for(int i = 0; i < heightMap.getWidth(); i++){
				float y = (((float) ((heightMap.getPixels()[(int) (j * heightMap.getWidth() + i)] >> 8) & 0xFF) * heightScale) / 2.0f);
				
				vertices[(int) (j * heightMap.getWidth() + i)] = (new Vertex(new Vector3f(i * unitScale, y, j * unitScale), new Vector2f(i * tilingAmount/ heightMap.getWidth() % 2 == 0? 0 : 1 , j * tilingAmount/ heightMap.getHeight() % 2 == 0 ? 0 : 1)));
			}
		}
		for(int i = 0; i < heightMap.getWidth() - 1; i++){
			for(int j = 0; j < heightMap.getHeight() - 1; j++){
				makePlane(indices, i, j, (int) heightMap.getWidth() - 1, (int)heightMap.getWidth());
			}
		}

		fileName = "";
		this.possibleIndices = indices;
		this.possibleVertices = vertices;
		addVertices(vertices, indices, true);
		// throw new IllegalArgumentException();
	}
	
	public Mesh(float[] heightMap, int size, float unitScale, float heightScale, float tilingAmount){
		Vertex[] vertices = new Vertex[size * size];
		int[] indices = new int[(size - 1)*(size - 1) * 6];
		for(int j = 0; j < size; j++){
			for(int i = 0; i < size; i++){
				vertices[(int) (j * size + i)] = new Vertex(new Vector3f(i * unitScale, heightMap[i + j * size] * heightScale, j * unitScale), new Vector2f(i * tilingAmount/ size % 2 , j * tilingAmount/ size % 2));
			}
		}
		for(int i = 0; i < size - 1; i++){
			for(int j = 0; j < size - 1; j++){
				makePlane(indices, i, j, size - 1, size);
			}
		}

		fileName = "";
		this.possibleIndices = indices;
		this.possibleVertices = vertices;
		addVertices(vertices, indices, true);
	}

	private void makePlane(int[] indices, int i, int j, int sizeminusone, int width){
		indices[(i + j * sizeminusone) * 6] = i + (j + 1) * width;
		indices[(i + j * sizeminusone) * 6 + 1] = i + 1 + j * width;
		indices[(i + j * sizeminusone) * 6 + 2] = i + j * width;
		indices[(i + j * sizeminusone) * 6 + 3] = i + (j + 1) * width;
		indices[(i + j * sizeminusone) * 6 + 4] = i + 1 + (j + 1) * width;
		indices[(i + j * sizeminusone) * 6 + 5] = i + 1 + j * width;
	}

	public Mesh(Vertex[] vertices, int[] indices){
		this(vertices, indices, false);
	}

	public Mesh(Vertex[] vertices, int[] indices, boolean calcNormals){
		fileName = "";
		this.possibleIndices = indices;
		this.possibleVertices = vertices;
		this.calcNormals = calcNormals;
		addVertices(vertices, indices, calcNormals);
	}

	@Override
	protected void finalize(){
		if(resource.removeReference() && !fileName.isEmpty()){
			loadedModels.remove(fileName);
		}
	}

	public void flipFaces(){
		if(fileName.equals("")){
			System.err.println("flipping can only with models from files");
			throw new IllegalArgumentException();
		}

		OBJModel objModel = new OBJModel("./res/models/" + fileName);
		IndexedModel model = objModel.toIndexedModel();
		model.calcNormals();

		ArrayList<Vertex> vertices = new ArrayList<Vertex>();

		for(int i = 0; i < model.getPositions().size(); i++){
			vertices.add(new Vertex(model.getPositions().get(i), model.getTexCoords().get(i), model.getNormals().get(i), model.getTangents().get(i)));
		}

		Vertex[] vertexData = new Vertex[vertices.size()];
		vertices.toArray(vertexData);

		Integer[] indexData = new Integer[model.getIndices().size()];
		Util.flipArrayList(model.getIndices()).toArray(indexData);

		addVertices(vertexData, Util.toIntArray(indexData), false);
		loadedModels.put(fileName, resource);
	}

	protected void addVertices(Vertex[] vertices, int[] indices, boolean calcNormals){
		if(calcNormals){
			calcNormals(vertices, indices);
			calcTangents(vertices, indices);
		}

		resource = new MeshResource(indices.length);

		glBindBuffer(GL_ARRAY_BUFFER, resource.getVbo());
		GL15.glBufferData(GL_ARRAY_BUFFER, Util.createFlippedVertexBuffer(vertices, false), GL_STATIC_DRAW);

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, resource.getIbo());
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, Util.createFlippedBuffer(indices), GL_STATIC_DRAW);
	}

	public void draw(){
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glEnableVertexAttribArray(3);
		
		glBindBuffer(GL_ARRAY_BUFFER, resource.getVbo());
		glVertexAttribPointer(0, 3, GL_FLOAT, false, Vertex.SIZE * 4, 0);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, Vertex.SIZE * 4, 12);
		glVertexAttribPointer(2, 3, GL_FLOAT, false, Vertex.SIZE * 4, 20);
		glVertexAttribPointer(3, 3, GL_FLOAT, false, Vertex.SIZE * 4, 32);

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, resource.getIbo());
		glDrawElements(GL_TRIANGLES, resource.getSize(), GL_UNSIGNED_INT, 0);

		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glDisableVertexAttribArray(3);
	}

	protected void calcNormals(Vertex[] vertices, int[] indices){
		for(int i = 0; i < indices.length; i += 3){
			int i0 = indices[i];
			int i1 = indices[i + 1];
			int i2 = indices[i + 2];

			Vector3f v1 = vertices[i1].getPos().sub(vertices[i0].getPos());
			Vector3f v2 = vertices[i2].getPos().sub(vertices[i0].getPos());

			Vector3f normal = v1.cross(v2).normalized();

			vertices[i0].setNormal(vertices[i0].getNormal().add(normal));
			vertices[i1].setNormal(vertices[i1].getNormal().add(normal));
			vertices[i2].setNormal(vertices[i2].getNormal().add(normal));
		}

		for(int i = 0; i < vertices.length; i++)
			vertices[i].setNormal(vertices[i].getNormal().normalized());
	}

	protected void calcTangents(Vertex[] vertices, int[] indices){

		for(int i = 0; i < indices.length; i += 3){
			int i0 = indices[i];
			int i1 = indices[i + 1];
			int i2 = indices[i + 2];

			Vector3f edge1 = vertices[i1].getPos().sub(vertices[i0].getPos());
			Vector3f edge2 = vertices[i2].getPos().sub(vertices[i0].getPos());

			float deltaU1 = vertices[i1].getTexCoord().getX() - vertices[i0].getTexCoord().getX();
			float deltaU2 = vertices[i2].getTexCoord().getX() - vertices[i0].getTexCoord().getX();
			float deltaV1 = vertices[i1].getTexCoord().getY() - vertices[i0].getTexCoord().getY();
			float deltaV2 = vertices[i2].getTexCoord().getY() - vertices[i0].getTexCoord().getY();

			float f = 1.0f / (deltaU1 * deltaV2 - deltaU2 * deltaV1);
			Vector3f tangent = new Vector3f(0, 0, 0);
			tangent.setX(f * (edge1.getX() * deltaV2 - deltaV1 * edge2.getX()));
			tangent.setY(f * (edge1.getY() * deltaV2 - deltaV1 * edge2.getY()));
			tangent.setZ(f * (edge1.getZ() * deltaV2 - deltaV1 * edge2.getZ()));

			vertices[i0].setTangent(vertices[i0].getTangent().add(tangent));
			vertices[i1].setTangent(vertices[i1].getTangent().add(tangent));
			vertices[i2].setTangent(vertices[i2].getTangent().add(tangent));
		}

		for(int i = 0; i < vertices.length; i++){
			vertices[i].setTangent(vertices[i].getTangent().normalized());
		}

	}

	protected Mesh loadMesh(String fileName){
		String[] splitArray = fileName.split("\\.");
		String ext = splitArray[splitArray.length - 1];

		if(!ext.equals("obj") && !ext.equalsIgnoreCase("dae")){
			System.err.println("Error: '" + ext + "' file format not supported for mesh data.");
			new Exception().printStackTrace();
			System.exit(1);
		}
		IndexedModel model = null;
		if(ext.equalsIgnoreCase("obj")){
			
			OBJModel obj = new OBJModel("./res/models/" + fileName);
			model = obj.toIndexedModel();
		}
		if(ext.equalsIgnoreCase("dae")){
			COLLADAModel collada = new COLLADAModel("./res/models/" + fileName, true);
			model = collada.toIndexedModel();
		}

		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
	
		for(int i = 0; i < model.getPositions().size(); i++){
			vertices.add(new Vertex(model.getPositions().get(i), model.getTexCoords().get(i), model.getNormals().get(i), model.getTangents().get(i)));
		}
		

		Vertex[] vertexData = new Vertex[vertices.size()];
		vertices.toArray(vertexData);

		Integer[] indexData = new Integer[model.getIndices().size()];
		model.getIndices().toArray(indexData);

		addVertices(vertexData, Util.toIntArray(indexData), false);

		return null;
	}

	public String getFileName(){
		return fileName;
	}

	private void readObject(ObjectInputStream aInputStream) throws ClassNotFoundException, IOException{
		// always perform the default de-serialization first
		aInputStream.defaultReadObject();
		if(fileName.equals("")){
			addVertices(possibleVertices, possibleIndices, calcNormals);
		}else{
			MeshResource oldResource = loadedModels.get(fileName);

			if(oldResource != null){
				resource = oldResource;
				resource.addReference();
			}else{
				loadMesh(fileName);
				loadedModels.put(fileName, resource);
			}
		}
	}

	/**
	 * This is the default implementation of writeObject. Customise if
	 * necessary.
	 */
	private void writeObject(ObjectOutputStream aOutputStream) throws IOException{
		// perform the default serialization for all non-transient, non-static
		// fields
		aOutputStream.defaultWriteObject();

	}
}
