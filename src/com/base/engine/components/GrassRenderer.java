package com.base.engine.components;

import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
//import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_STREAM_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.BufferUtils;

import com.base.engine.core.CoreEngine;
import com.base.engine.core.Input;
import com.base.engine.core.Time;
import com.base.engine.core.Util;
import com.base.engine.core.Vector2f;
import com.base.engine.core.Vector3f;
import com.base.engine.optimisationtrees.QuadNode;
import com.base.engine.optimisationtrees.QuadTreeQuery;
import com.base.engine.optimisationtrees.Quadtree;
import com.base.engine.profiling.ProfileTimer;
import com.base.engine.rendering.Material;
import com.base.engine.rendering.RenderPhase;
import com.base.engine.rendering.RenderingEngine;
import com.base.engine.rendering.Shader;
import com.base.engine.rendering.meshLoading.IndexedModel;
import com.base.engine.rendering.meshLoading.OBJModel;
import com.base.game.PerlinGenerator;

public class GrassRenderer extends GameComponent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7008401038649184131L;

	// voor diffuse en rays fix alg foliageShader-diffuse en -rays in re
	private Shader grassShader;
	private Material material;

	private int vertexBuffer;
	private int instanceBuffer;
	private int indexBuffer;
	private int totalInstanceCount;
	private int currentInstanceCount;
	private int indicesCount;
	private FloatBuffer instanceData;
	private float unitScale;
	
	private Vector3f[] allPositions;
	private Vector3f[] allNormals;
	private Vector3f[] allTangents;

	private Camera camera;
	private Quadtree quadtree;

	public GrassRenderer(float[] heightMap, float unitScale, float heightScale, Material material, DirectionalLight directionalLight, Camera camera, boolean atlasd, int atlasNumber){
		this.grassShader = new Shader("grass-lighting");
		this.material = material;
		this.camera = camera;
		this.unitScale = unitScale;

		this.vertexBuffer = glGenBuffers();
		this.instanceBuffer = glGenBuffers();
		this.indexBuffer = glGenBuffers();
		
		material.setVector3f("lightDirection", directionalLight.getDirection());
		material.setVector3f("lightColor", directionalLight.getColor());
		material.setFloat("lightIntensity", directionalLight.getIntensity());
		
		Random random = new Random();
		
		// Normal calcs
		Vector3f[] tnormals = calcNormals(heightMap, unitScale, heightScale);

		// Position calcs
		ArrayList<Vector3f> positions = new ArrayList<Vector3f>();
		ArrayList<Vector3f> normals = new ArrayList<Vector3f>();
		ArrayList<Vector3f> rTangents = new ArrayList<Vector3f>();
		float noiseDensity = 0.4f;
		int nSize = 128;
		int hSize = (int) Math.sqrt(heightMap.length);
		PerlinGenerator pg = new PerlinGenerator(nSize, (int) Time.getTime());
		float[] quickNoise = new float[nSize * nSize];
		pg.noise4DToTileable((int) (noiseDensity * nSize), 1, 1, quickNoise);
		for(int i = 0; i < heightMap.length; i++){
			if(tnormals[i].getY() > 0.96f && heightMap[i] < -0.2f){
				int x = i % hSize;
				int y = i / hSize;
				if(quickNoise[(x % nSize + y * nSize) % quickNoise.length] > -0.3f && Math.random() < 0.25f){
					Vector3f current = new Vector3f(x * unitScale, heightMapLookup(heightMap, heightScale, i), y * unitScale);
					Vector3f v1 = new Vector3f((x + 1) * unitScale, heightMapLookup(heightMap, heightScale, (i + 1) % heightMap.length), y * unitScale).sub(current);
					Vector3f v2 = new Vector3f(x * unitScale, heightMapLookup(heightMap, heightScale, (i + hSize) % heightMap.length), (y + 1) * unitScale).sub(current);
					
					Vector3f diag = v1.add(v2);
					positions.add(current.add(diag.mul((float)Math.random())));
					normals.add(tnormals[i].mul((float)Math.sqrt((quickNoise[(x % nSize + y * nSize) % quickNoise.length] + 0.3f)) * 7));
					Vector2f rVec = Vector2f.randomVector();
					Vector3f tn = tnormals[i].cross(new Vector3f(rVec.getX(), 0, rVec.getY())).normalized();
					if(atlasd){
						tn = tn.mul(1 + random.nextInt(atlasNumber)/(float)atlasNumber);
					}
					rTangents.add(tn);
				}
			}
		}
		allPositions = new Vector3f[positions.size()];
		positions.toArray(allPositions);
		allNormals = new Vector3f[normals.size()];
		normals.toArray(allNormals);
		allTangents = new Vector3f[rTangents.size()];
		rTangents.toArray(allTangents);

		// Vertex setup
		OBJModel obj = new OBJModel("./res/models/grassObject.obj");
		IndexedModel model = obj.toIndexedModel();
		FloatBuffer buffer = Util.createFloatBuffer(model.getPositions().size() * 5);
		for(int i = 0; i < model.getPositions().size(); i++){
			buffer.put(model.getPositions().get(i).getX() * unitScale);
			buffer.put(model.getPositions().get(i).getY() * unitScale);
			buffer.put(model.getPositions().get(i).getZ() * unitScale);
			if(atlasd)
				buffer.put(model.getTexCoords().get(i).getX() / atlasNumber);
			else
				buffer.put(model.getTexCoords().get(i).getX());
			buffer.put(model.getTexCoords().get(i).getY());
		}
		buffer.flip();
		glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		
		Integer[] indexData = new Integer[model.getIndices().size()];
		Util.flipArrayList(model.getIndices()).toArray(indexData);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, Util.createFlippedBuffer(Util.toIntArray(indexData)), GL_STATIC_DRAW);

		
		// Instance buffer
		quadtree = new Quadtree(new Vector2f(1024/2f * unitScale, 1024/2f * unitScale), 1024 * unitScale, 1);
		quadtree.addQuadTreeQuery(new QuadTreeQuery() {
			
			@Override
			public Object check(QuadNode node, Object o){
				
				Vector2f dir = node.center.sub(((Camera) o).getTransform().getTransformedPos().getXZ());
				
				if(dir.length() - node.halfRange * 1.4142f < 200  && dir.normalize().dot(((Camera) o).getTransform().getTransformedRot().getForward().getXZ().normalize()) + node.halfRange * 1.4142f / dir.length() > 0.50f){
					return returnData(node);
				}
				return null;
			}

			@Override
			public Object returnData(QuadNode node){
				return node.data[0];
			}
		});
		
		ByteBuffer bb = ByteBuffer.allocateDirect(positions.size() * 9 * 4); 
		bb.order(ByteOrder.nativeOrder());    // use the device hardware's native byte order
		instanceData = bb.asFloatBuffer();
		
		for(int i = 0; i < positions.size(); i++){
			quadtree.addPoint(positions.get(i).getXZ(), i);
		}
		
		glBindBuffer(GL_ARRAY_BUFFER, instanceBuffer);
		glBufferData(GL_ARRAY_BUFFER, instanceData, GL_STREAM_DRAW);
		totalInstanceCount = positions.size();
		currentInstanceCount = 0;
		indicesCount = model.getIndices().size();
		System.out.println("Number Grass-instances: " + totalInstanceCount);
	}

	private Vector3f[] calcNormals(float[] heightMap, float unitScale, float heightScale){
		int length = heightMap.length;
		int size = (int) Math.sqrt(length);
		Vector3f[] normals = new Vector3f[length];
		for(int i = 0; i < length; i++){
			Vector3f v1 = new Vector3f((i % size) * unitScale, (heightMap[(i + size) % length]/2f + .5f) * heightScale, (i / size + 1) * unitScale).sub(new Vector3f((i % size) * unitScale,(heightMap[i % length]/2f + .5f) * heightScale, (i / size) * unitScale));
			Vector3f v2 = new Vector3f((i % size + 1) * unitScale,(heightMap[(i + 1) % length]/2f + .5f) * heightScale, (i / size) * unitScale).sub(new Vector3f((i % size) * unitScale, (heightMap[i % length]/2f + .5f) * heightScale, (i / size) * unitScale));
			Vector3f normal = v1.cross(v2).normalized();
			normals[i] = normal;
		}

		return normals;
	}
	
	private float heightMapLookup(float[] heightMap, float heightScale, int index){
		float v = heightScale * ((byte)((heightMap[index]/2f + .5f) * 255) & 0xFF)/255f;
		return v;
	}
	
	public void update(float delta){
		currentInstanceCount = 0;
			ArrayList<Object> qtResults = quadtree.getQueryResults(0, 4f * unitScale,camera);
			
			instanceData.clear();
			for(int i = 0; i < qtResults.size(); i++){
				instanceData.put(new float[]{allPositions[(int)qtResults.get(i)].getX(),allPositions[(int)qtResults.get(i)].getY(),allPositions[(int)qtResults.get(i)].getZ(),
						allNormals[(int)qtResults.get(i)].getX(),allNormals[(int)qtResults.get(i)].getY(),allNormals[(int)qtResults.get(i)].getZ(),
						allTangents[(int)qtResults.get(i)].getX(),allTangents[(int)qtResults.get(i)].getY(),allTangents[(int)qtResults.get(i)].getZ()
						});
				currentInstanceCount++;	
			}
			instanceData.flip();
		
		glBindBuffer(GL_ARRAY_BUFFER, instanceBuffer);
//		glBufferData(GL_ARRAY_BUFFER, BufferUtils.createFloatBuffer(totalInstanceCount * 9), GL_STREAM_DRAW);
		glBufferData(GL_ARRAY_BUFFER, ByteBuffer.allocateDirect(totalInstanceCount * 9 * 4).asFloatBuffer(), GL_STREAM_DRAW);
		glBufferSubData(GL_ARRAY_BUFFER, 0, instanceData);
		
	}

	public void render(Shader shader, RenderingEngine renderingEngine){
		
		if(renderingEngine.getRenderPhase() == RenderPhase.AMBIENT){
			material.setFloat("time", renderingEngine.getFloat("time") % (20 * 3.1415f));
			grassShader.bind();
			grassShader.updateUniforms(getTransform(), material, renderingEngine);
			// culling off
			glDisable(GL_CULL_FACE);
			
			glEnableVertexAttribArray(0);
			glEnableVertexAttribArray(1);
			glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
			glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * 4, 0);
			glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * 4, 12);

			glEnableVertexAttribArray(2);
			glEnableVertexAttribArray(3);
			glEnableVertexAttribArray(4);
			glBindBuffer(GL_ARRAY_BUFFER, instanceBuffer);
			glVertexAttribPointer(2, 3, GL_FLOAT, false, 9 * 4, 0);
			glVertexAttribPointer(3, 3, GL_FLOAT, false, 9 * 4, 12);
			glVertexAttribPointer(4, 3, GL_FLOAT, true, 9 * 4, 24);
			
//			glEnableVertexAttribArray(5);
//			glBindBuffer(GL_ARRAY_BUFFER, animBuffer);
//			glVertexAttribPointer(5, 2, GL_FLOAT, false, 4*2, 0);
			

			glVertexAttribDivisor(0, 0); 
			glVertexAttribDivisor(1, 0); 
			glVertexAttribDivisor(2, 1);
			glVertexAttribDivisor(3, 1); 
			glVertexAttribDivisor(4, 1); 
//			glVertexAttribDivisor(5, 1); 
			
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
			glDrawElementsInstanced(GL_TRIANGLES, indicesCount, GL_UNSIGNED_INT, 0, currentInstanceCount);
			
			
			glDisableVertexAttribArray(0);
			glDisableVertexAttribArray(1);
			glDisableVertexAttribArray(2);
			glDisableVertexAttribArray(3);
			glDisableVertexAttribArray(4);
//			glDisableVertexAttribArray(5);
			glEnable(GL_CULL_FACE);
		}
	}
}
