package com.base.game;

import java.io.File;
import java.util.ArrayList;

import com.base.engine.components.AnimatedMeshRenderer;
import com.base.engine.components.Camera;
import com.base.engine.components.DirectionalLight;
import com.base.engine.components.FreeLook;
import com.base.engine.components.RaysComponent;
import com.base.engine.components.Skydome;
import com.base.engine.core.Game;
import com.base.engine.core.GameObject;
import com.base.engine.core.Vector3f;
import com.base.engine.core.persistence.SerializeUtil;
import com.base.engine.editor.EditorMove;
import com.base.engine.editor.GameObjectBuilder;
import com.base.engine.rendering.AnimatedMesh;
import com.base.engine.rendering.GameWindowDimension;
import com.base.engine.rendering.Material;
import com.base.engine.rendering.Texture;

public class LevelEditor extends Game{

	private ArrayList<GameObject> add;
	private ArrayList<GameObject> delete;
	
	private ArrayList<GameObject> scene;
	
	private Skydome skydome;

	public void init(){
		scene = new ArrayList<GameObject>();
		add = new ArrayList<GameObject>();
		delete = new ArrayList<GameObject>();

		Camera camera = new Camera((float) Math.toRadians(70.0f), GameWindowDimension.getAspect(), 0.1f, 1000.0f);
		GameObject cameraObject = new GameObject().addComponent(camera).addComponent(new FreeLook(5f)).addComponent(new EditorMove());
		cameraObject.getTransform().setPos(new Vector3f(0,0,0));
		cameraObject.addComponent(new GameObjectBuilder(this));
		
		addObject(cameraObject);
		
		
//		GameObject waterObject = new GameObject();
//		waterObject.addComponent(new Water(200.0f, new Vector3f(0,40.0f,0), 18, 0.03f, 0.08f, new Vector2f(0,1f), new Vector3f(0.2f,0.2f,0.25f), 0.5f));
//		addObject(waterObject);
		

		skydome = new Skydome("skydome_10.png", "skydome_11.png");
		GameObject skydomeObject = new GameObject();
		skydomeObject.addComponent(skydome);
		skydomeObject.addComponent(new RaysComponent(skydome.getMaterial(), new Texture("skydome_10_rays.png"), new Vector3f(-68790f, 69465f, 21031f),
				0.7f, 0.2f, 0.98f, 0.4f));
		addObject(skydomeObject);
		
//		GameObject plane3 = new GameObject();
//		plane3.addComponent(new MeshRenderer(new Mesh("plane.obj"), new Material(new Texture("testTex.png"), 1f, 16, new Texture("bricks3_normal.png"), new Texture("bricks3_disp.png"), 0.05f, -0.2f)));
//		addObject(plane3);
//		
//		GameObject wall = new GameObject();
//		wall.addComponent(new MeshRenderer(new Mesh("detailedWall.obj"), new Material(new Texture("Wood 2.png"), 1f, 16)));
//		wall.getTransform().setPos(new Vector3f(0,-2,0));
//		wall.getTransform().setScale(0.2f);
//		addObject(wall);
		
		
//		Particle p = new Particle();
//		p.setColor((byte)0xFF, (byte)0x33, (byte)0x33, (byte)0x66);
//		p.setLife(2.5f);
//		p.setSize((byte)0x82);
//		p.setTexture(new Texture("particle_water_atlas.png"), true, 4);
//		
//		ParticleSystem ps = new ParticleSystem(100, p, new BoatPartSim(false), camera);
//		ParticleSystem ps2 = new ParticleSystem(100, p, new BoatPartSim(true), camera);
//		
//		GameObject emitter = new GameObject().addComponent(ps);
//		GameObject emitter2 = new GameObject().addComponent(ps2);
//		ArrayList<ParticleSystem> listOfParticleSystems = new ArrayList<ParticleSystem>();
//		listOfParticleSystems.add(ps);
//		listOfParticleSystems.add(ps2);
//		ParticleSystemController psc = new ParticleSystemController(listOfParticleSystems, Input.KEY_UP, false);
//		
//		GameObject tree = new GameObject();
//		tree.addComponent(new FreeMove(12f, Input.KEY_UP, Input.KEY_DOWN, Input.KEY_LEFT, Input.KEY_RIGHT, true));
//		tree.getTransform().setPos(new Vector3f(0,0,0));
//		tree.getTransform().rotate(Vector3f.xAxis, -90f);
//		tree.addComponent(new MeshRenderer(new Mesh("squirrel.dae"), new Material()));
		//tree.getTransform().setScale(1000000);
//		addObject(tree);
		
		
		GameObject directionalLightObject = new GameObject();
		DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1,0.6f,0.4f), 0.8f, 10, 100.0f);
		directionalLightObject.addComponent(directionalLight);
		directionalLightObject.getTransform().lookAt(new Vector3f(1,1,1), Vector3f.yAxis);
		addObject(directionalLightObject);
		
		
//		float heightScale = 150;
//		float unitScale = 1f;
//		PerlinGenerator perlin = new PerlinGenerator(1024, (int) System.currentTimeMillis());
//		float[] heightMap = new float[1024*1024];
//		int pp = 500;
//		perlin.noise4DToTileable(pp, 1f, 1f, heightMap);
//		perlin.noise4DToTileable(pp/2, 1f, 1/2f, heightMap);
//		perlin.noise4DToTileable(pp/4, 1f, 1/4f, heightMap);
//		perlin.noise4DToTileable(pp/8, 1f, 1/8f, heightMap);
//		perlin.noise4DToTileable(pp/16, 1f, 1/16f, heightMap);
//		perlin.noise4DToTileable(pp/32, 1f, 1/32f, heightMap);

//		heightMap = perlin.perturb(heightMap, 0.05f, 50);
//		perlin.erode(heightMap, 20, 0.033f);
//		perlin.smoothen(heightMap);
//		perlin.smoothen(heightMap);
//		perlin.smoothen(heightMap);
//		
//		String hmName = perlin.createImageFile(heightMap, true);
//		String normalstr = perlin.createNormalMap(heightMap, unitScale, heightScale);
//		
//		GameObject terrain = new GameObject();
//		Material mat = new Material();
//		mat.setFloat("specularIntensity", 0.2f);
//		mat.setFloat("specularPower", 8);
//		mat.setTexture("normalMap", new Texture("grass1024_normal.jpg"));
//		terrain.addComponent(new TerrainRenderer(new Texture(hmName, GL_TEXTURE_2D, GL_LINEAR, GL_RGBA, GL_RGBA, false, GL_NONE),
//												 new Texture(normalstr, GL_TEXTURE_2D, GL_LINEAR, GL_RGBA, GL_RGBA, false, GL_NONE), unitScale, heightScale, mat, new String[]{"grass1024.jpg", "rocks11024.jpg", "ice1024.jpg", "snow1024.jpg"}, 20));
//		terrain.addComponent(new TerrainRenderer(new Texture("noise1469233981.png", GL_TEXTURE_2D, GL_LINEAR, GL_RGBA, GL_RGBA, false, GL_NONE),
//				new Texture("normal1469233984.png", GL_TEXTURE_2D, GL_LINEAR, GL_RGBA, GL_RGBA, false, GL_NONE), unitScale, heightScale, mat, new String[]{"grass1024.jpg", "rocks11024.jpg", "ice1024.jpg", "snow1024.jpg"}, 20));
//		Material grassmat = new Material(new Texture("grass_2atlas_alpha.png"),1,1);
//		terrain.addComponent(new GrassRenderer(heightMap, unitScale, heightScale, grassmat, directionalLight, camera, false,-1));
//		addObject(terrain);
		
		AnimatedMesh test = new AnimatedMesh("test-rig.dae");
		//Mesh test2 = new Mesh("test-rig.dae");
		
		GameObject g = new GameObject();
		g.addComponent(new AnimatedMeshRenderer(test, new Material()));
		addObject(g);
		
		
		
//		GameObject point = new GameObject();
//		point.addComponent(new PointLight(new Vector3f(1,0.5f,0), 5f, new Attenuation(1, 0, 1)));
//		point.addComponent(new TransformSketch());
//		point.getTransform().setPos(new Vector3f(0,0,0));
//		addObject(point);
//		cameraObject.addChild(point);
		//addObject(spotLightObject);
//		final File folder = new File("./res");
//		listFilesForFolder(folder);
	}
	
	public void update(float delta){
		getUpdateTimer().startInvocation();
		updateRoot(delta);
		manageAdd();
		manageDelete();
		getUpdateTimer().stopInvocation();
	}

	public void listFilesForFolder(final File folder){
		for(final File fileEntry : folder.listFiles()){
			if(fileEntry.isDirectory()){
				listFilesForFolder(fileEntry);
			}else{
				String fileName = fileEntry.getName();
				System.out.println(fileName);
				String[] splitArray = fileName.split("\\.");
				String ext = splitArray[splitArray.length - 1];
			}
		}
	}

	public void add(ArrayList<GameObject> gameObjects){
		add.addAll(gameObjects);
	}
	
	public void add(GameObject gameObject){
		add.add(gameObject);
	}
	public void remove(GameObject object){
		delete.add(object);
	}
	
	private void manageAdd(){
		for(int i = 0; i < add.size(); i++){
			addObject(add.get(i));
			scene.add(add.get(i));
		}
		add.clear();
	}
	
	private void manageDelete(){
		for(int i = 0; i < delete.size(); i++){
			deleteObject(delete.get(i));
			scene.remove(delete.get(i));
		}
		delete.clear();		
	}
	
	private void loadScene(String directoryName){
		SerializeUtil.loadScene(directoryName, scene);
	}
	
	public void setSkydome(String top, String bottom){
		skydome.changeMap(top, bottom);
	}
	
	public String[] getSkydomeMapNames(){
		return skydome.getMapNames();
	}
	
	public ArrayList<GameObject> getSceneParts(){
		return scene;
	}
}
