package com.base.engine.rendering;

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_CLIP_PLANE0;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_CW;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_EQUAL;
import static org.lwjgl.opengl.GL11.GL_FOG;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_LESS;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glFrontFace;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_SRGB;
import static org.lwjgl.opengl.GL30.GL_RG32F;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.BufferUtils;

import com.base.engine.components.BaseLight;
import com.base.engine.components.Camera;
import com.base.engine.components.RaysComponent;
import com.base.engine.components.Water;
import com.base.engine.core.GameObject;
import com.base.engine.core.Matrix4f;
import com.base.engine.core.Time;
import com.base.engine.core.Transform;
import com.base.engine.core.Vector3f;
import com.base.engine.profiling.ProfileTimer;
import com.base.engine.rendering.particles.ParticleSystem;
import com.base.engine.rendering.resourceManagement.MappedValues;

public class RenderingEngine extends MappedValues{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3625862084507798294L;
	private Window window;
	private ArrayList<BaseLight> lights;
	private ArrayList<Water> water;
	private ArrayList<ParticleSystem> particleEmitters;
	private ArrayList<RaysComponent> rayEmitters;
	private HashMap<Integer, Object[]> waterDataMap;
	private HashMap<String, Integer> samplerMap;
	private RenderPhase renderPhase;
	private ProfileTimer renderProfileTimer;
	private ProfileTimer windowSyncProfileTimer;

	private static final int numShadowMaps = 10;
	private boolean waterLevelEqual;
	private int width;
	private int height;

	private Texture shadowMaps[];
	private Texture shadowTempTargets[];
	private ArrayList<Texture> rayPrePassTextures;

	private BaseLight activeLight;
	private Shader ambientShader;
	private Shader diffuseShader;
	private Shader shadowMapShader;
	private Shader ssaoShader;
	private Shader nullFilter;
	private Shader addFilter;
	private Shader mulFilter;
	private Shader gaussianBlurFilter;
	private Shader fxaaFilter;
	private Shader hdrFilter;
	private Shader raysFilter;
	private Shader toonFilter;
	private Shader ssaoBlurFilter;
	private Shader preraysShader;
	private Shader preSSAOShader;
	private Camera mainCamera;
	private Camera altCamera;
	private GameObject altCameraObject;

	private Camera waterCamera;
	private GameObject waterCameraObject;

	private Vector3f ambientColor;

	// Render to texture
	private Material planeMaterial;
	private Transform planeTransform;
	private Mesh plane;
	private Texture tempTarget;

	private Matrix4f biasMatrix = new Matrix4f().initScale(0.5f, 0.5f, 0.5f).mul(new Matrix4f().initTranslation(1.0f, 1.0f, 1.0f));
	private Matrix4f lightMatrix;
	private Matrix4f waterReflectionMatrix;

	private FloatBuffer kernel;
	private int kernelsize;

	public RenderingEngine(Window window){

		super();
		this.window = window;
		lights = new ArrayList<BaseLight>();
		water = new ArrayList<Water>();
		particleEmitters = new ArrayList<ParticleSystem>();
		rayEmitters = new ArrayList<RaysComponent>();
		waterDataMap = new HashMap<Integer, Object[]>();
		samplerMap = new HashMap<String, Integer>();
		rayPrePassTextures = new ArrayList<Texture>();
		samplerMap.put("diffuse", 0);
		samplerMap.put("normalMap", 1);
		samplerMap.put("dispMap", 2);
		samplerMap.put("shadowMap", 3);
		samplerMap.put("heightMap", 4);
		samplerMap.put("terrainNormals", 5);
		samplerMap.put("diffuseTextures", 0);
		samplerMap.put("waveBumpMap", 0);
		samplerMap.put("reflectionMap", 1);
		samplerMap.put("refractionMap", 2);
		samplerMap.put("rayMap", 0);
		samplerMap.put("filterTexture", 0);
		samplerMap.put("displayTexture", 1);
		samplerMap.put("additive", 1);
		samplerMap.put("ssaoDepth", 0);
		samplerMap.put("noise", 1);

		shadowMaps = new Texture[numShadowMaps];
		shadowTempTargets = new Texture[numShadowMaps];

		waterLevelEqual = true;
		width = GameWindowDimension.getWidth();
		height = GameWindowDimension.getHeight();

		ambientColor = new Vector3f(0.4f, 0.4f, 0.4f);
		setVector3f("ambient", ambientColor);
		setBoolean("doSSAO", false);
		setBoolean("doRays", false);
		setBoolean("doWater", true);
		setVector3f("fogColor", new Vector3f(0.453f, 0.406f, 0.504f));
		setFloat("fogStart", 500.0f);
		setFloat("fogSpan", 60.0f);
		setFloat("fxaaSpanMax", 12.0f); // 8.0f
		setFloat("fxaaReduceMin", 1.0f / 128.0f);
		setFloat("fxaaReduceMul", 1.0f / 8.0f); // GL_NEAREST voor minder
												// textureBlurring
		setTexture("displayTexture", new Texture(width, height, new int[width * height], true, GL_TEXTURE_2D, GL_LINEAR, GL_RGBA16F, GL_RGBA, true, GL_COLOR_ATTACHMENT0));
		setTexture("displayTextureTemp", new Texture(width, height, new int[width * height], true, GL_TEXTURE_2D, GL_LINEAR, GL_RGBA16F, GL_RGBA, true, GL_COLOR_ATTACHMENT0));
		setTexture("displayTexture2", new Texture(width, height, new int[width * height], true, GL_TEXTURE_2D, GL_LINEAR, GL_RGBA, GL_RGBA, true,  GL_COLOR_ATTACHMENT0));
		setTexture("displayTextureTemp2", new Texture(width, height, new int[width * height], true, GL_TEXTURE_2D, GL_LINEAR, GL_RGBA, GL_RGBA, true, GL_COLOR_ATTACHMENT0));
		setTexture("ssaoDepth", new Texture(width, height, new int[width * height], true, GL_TEXTURE_2D, GL_LINEAR, GL_RG32F, GL_RGBA, true, GL_COLOR_ATTACHMENT0));
		setTexture("ssao", new Texture(width, height, new int[width * height], true, GL_TEXTURE_2D, GL_LINEAR, GL_RGBA16F, GL_RGBA, true, GL_COLOR_ATTACHMENT0));
		setVector3f("ssaoDimension", new Vector3f(width, height, 0));
		setTexture("rayAdd", new Texture(width, height, new int[width * height], true, GL_TEXTURE_2D, GL_LINEAR, GL_RGBA, GL_RGBA, true, GL_COLOR_ATTACHMENT0));
		setTexture("waveBumpMap", new Texture("waves_bump3.png", GL_TEXTURE_2D, GL_LINEAR, GL_RGBA, GL_RGBA, false, GL_COLOR_ATTACHMENT0));

		ambientShader = new Shader("forward-ambient");
		diffuseShader = new Shader("diffuse");
		shadowMapShader = new Shader("shadowMapGenerator");
		nullFilter = new Shader("filter-null");
		addFilter = new Shader("filter-add");
		mulFilter = new Shader("filter-mul");
		gaussianBlurFilter = new Shader("filter-gausBlur7x1");
		fxaaFilter = new Shader("filter-fxaa");
		hdrFilter = new Shader("filter-hdr");
		raysFilter = new Shader("filter-crepuscular-rays");
		ssaoShader = new Shader("ssao");
		ssaoBlurFilter = new Shader("filter-ssao-blur");
		toonFilter = new Shader("filter-cartoon");
		preraysShader = new Shader("crepuscular-rays-prepass");
		preSSAOShader = new Shader("ssao-prepass");

		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glFrontFace(GL_CW);
		glCullFace(GL_BACK);
		glEnable(GL_CULL_FACE);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_TEXTURE_2D);
		//glEnable(GL_FOG);

		altCamera = new Camera(new Matrix4f().initIdentity());
		altCameraObject = (new GameObject()).addComponent(altCamera);

		waterCamera = new Camera(new Matrix4f().initIdentity());
		waterCameraObject = (new GameObject()).addComponent(waterCamera);

		tempTarget = new Texture(width, height, new int[width * height], true, GL_TEXTURE_2D, GL_NEAREST, GL_RGBA, GL_RGBA, false, GL_COLOR_ATTACHMENT0);
		planeTransform = new Transform();
		planeMaterial = new Material(tempTarget);
		planeTransform.setPos(new Vector3f(0, 0, 0));
		plane = new RenderPlane().getMesh();
		renderProfileTimer = new ProfileTimer();
		windowSyncProfileTimer = new ProfileTimer();

		for(int i = 0; i < numShadowMaps; i++){
			int shadowMapSize = 1 << (i + 1);
			shadowMaps[i] = new Texture(shadowMapSize, shadowMapSize, new int[shadowMapSize * shadowMapSize], false, GL_TEXTURE_2D, GL_LINEAR, GL_RG32F, GL_RGBA, true, GL_COLOR_ATTACHMENT0);
			shadowTempTargets[i] = new Texture(shadowMapSize, shadowMapSize, new int[shadowMapSize * shadowMapSize], false, GL_TEXTURE_2D, GL_LINEAR, GL_RG32F, GL_RGBA, true, GL_COLOR_ATTACHMENT0);
		}

		lightMatrix = new Matrix4f().initScale(0, 0, 0);

		// SSAO
		kernelsize = 8;
		Vector3f[] vKernel = new Vector3f[kernelsize];
		for(int i = 0; i < kernelsize; i++){
			do{
				vKernel[i] = new Vector3f((float) Math.random() * 2.0f - 1.0f, (float) Math.random() * 2.0f - 1.0f, (float) Math.random());
				vKernel[i].normalized();
			}while(vKernel[i].dot(Vector3f.zAxis) < 0.15f);

			float scale = (float) i / (float) kernelsize;
			scale = lerp(0.1f, 1.0f, scale * scale);
			vKernel[i].mulEquals(scale);
		}

		float[] kernelValues = new float[kernelsize * 3];
		for(int i = 0; i < vKernel.length; i++){
			kernelValues[i * 3 + 0] = vKernel[i].getX();
			kernelValues[i * 3 + 1] = vKernel[i].getY();
			kernelValues[i * 3 + 2] = vKernel[i].getZ();
		}

		kernelValues[0] = 0;
		kernelValues[1] = 0;
		kernelValues[2] = 1;

		this.kernel = BufferUtils.createFloatBuffer(kernelsize * 3).put(kernelValues);
		kernel.flip();

		setTexture("noise", new Texture("ssao_noise.png"));
		setFloat("ssaoRadius", 0.001f);
		setFloat("ssaoExposure", 1.1f);
	}

	private float lerp(float l, float h, float i){
		return l + (h - l) * i;
	}

	/** manually edit this function or extend RE and override */
	public void updateUniformStruct(Transform transform, Material material, Shader shader, String uniformName, String uniformType){

		throw new IllegalArgumentException(uniformType + " is not a supported type in Rendering Engine");
	}

	public void render(GameObject object){
		renderProfileTimer.startInvocation();

		setFloat("time", (float) Time.getTime());

		// render the reflectionMap with ambientshader and skydome
		if(getBoolean("doWater")){
			if(water.size() > 0){
				waterPreparation(object);
				Water highestWaterlevel = water.get(0);
				for(int i = 0; i < water.size(); i++){
					Water waterLevel = water.get(i);
					if(waterLevel.getWaterLevel() > highestWaterlevel.getWaterLevel()) highestWaterlevel = waterLevel;
				}
				if(mainCamera.getTransform().getTransformedPos().getY() < highestWaterlevel.getWaterLevel() && mainCamera.getTransform().getTransformedPos().sub(highestWaterlevel.getTransform().getPos().add(new Vector3f(highestWaterlevel.getSize() / 2, 0, highestWaterlevel.getSize() / 2))).length() < highestWaterlevel.getSize()){
					setVector3f("ambient", highestWaterlevel.getWaterColor());
					setVector3f("fogColor", highestWaterlevel.getWaterColor());
					setFloat("fogStart", 0.01f);
					setFloat("fogSpan", 1.0f);
				}else{
					setVector3f("fogColor", new Vector3f(0.5f, 0.5f, 0.68f));
					setFloat("fogStart", 50.0f);
					setFloat("fogSpan", 400.0f);
					setVector3f("ambient", ambientColor);
				}
			}
		}

		if(getBoolean("doSSAO")){
			renderPhase = RenderPhase.SSAO;
			getTexture("ssaoDepth").bindAsRenderTarget();
			glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			object.renderAll(preSSAOShader, this);

			getTexture("ssao").bindAsRenderTarget();
			glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			object.renderAll(ssaoShader, this);
		}

		// display Ambient and watershading and skydome
		getTexture("displayTexture2").bindAsRenderTarget();
		renderPhase = RenderPhase.AMBIENT;

		glClearColor(0.317647f, 0.380392f, 0.431373f, 0.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		object.renderAll(ambientShader, this);

		for(int i = 0; i < lights.size(); i++){
			activeLight = lights.get(i);
			ShadowInfo shadowInfo = activeLight.getShadowInfo();

			int shadowMapIndex = 0;
			if(shadowInfo != null) shadowMapIndex = shadowInfo.getShadowMapSizeAsPowerOf2() - 1;

			setTexture("shadowMap", shadowMaps[shadowMapIndex]);
			shadowMaps[shadowMapIndex].bindAsRenderTarget();
			renderPhase = RenderPhase.SHADOW_PREP;
			glClearColor(1.0f, 1.0f, 0.0f, 0.0f);
			glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

			if(shadowInfo != null){
				altCamera.setProjection(shadowInfo.getProjection());
				ShadowCameraTransform shadowCameraTransform = activeLight.calcShadowCameraTransform(mainCamera.getTransform().getTransformedPos(), mainCamera.getTransform().getTransformedRot());
				altCamera.getTransform().setPos(shadowCameraTransform.pos);
				altCamera.getTransform().setRot(shadowCameraTransform.rot);

				lightMatrix = biasMatrix.mul(altCamera.getViewProjection());

				setFloat("shadowVarianceMin", shadowInfo.getVarianceMin());
				setFloat("shadowLightBleedReduction", shadowInfo.getLightBleedReductionAmount());
				boolean flipFaces = shadowInfo.getFlipFaces();

				Camera tempC = mainCamera;
				mainCamera = altCamera;
				getTexture("shadowMap").bindAsRenderTarget();
				if(flipFaces) glCullFace(GL_FRONT);
				
				object.renderAll(shadowMapShader, this);
				if(flipFaces) glCullFace(GL_BACK);

				float shadowSoftness = shadowInfo.getShadowSoftness();
				if(shadowSoftness != 0) blurShadowMap(shadowMapIndex, shadowSoftness);
				mainCamera = tempC;


			}else{
				lightMatrix = new Matrix4f().initScale(0, 0, 0);
				setFloat("shadowVarianceMin", 0.00002f);
				setFloat("shadowLightBleedReduction", 0.0f);
			}

			getTexture("displayTexture2").bindAsRenderTarget();
			renderPhase = RenderPhase.LIGHTING;
			glEnable(GL_BLEND);
			glBlendFunc(GL_ONE, GL_ONE);
			glDepthMask(false);
			glDepthFunc(GL_EQUAL);

			activeLight = lights.get(i);
			object.renderAll(activeLight.getShader(), this);

			glDepthMask(true);
			glDepthFunc(GL_LEQUAL);
			glDisable(GL_BLEND);
		}

		for(int i = 0; i < particleEmitters.size(); i++){
			particleEmitters.get(i).render(this);
		}

		if(getBoolean("doRays")){
			renderPhase = RenderPhase.RAYS;
			for(int i = 0; i < rayEmitters.size(); i++){
				rayPrePassTextures.get(i).bindAsRenderTarget();
				glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
				glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
				rayEmitters.get(i).enable();
				object.renderAll(preraysShader, this);

				setVector3f("lightPos", rayEmitters.get(i).getLightPos());
				getTexture("rayAdd").bindAsRenderTarget();
				glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
				glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
				setTexture("filterTexture", rayPrePassTextures.get(i));
				glClear(GL_DEPTH_BUFFER_BIT);
				raysFilter.bind();
				raysFilter.updateUniforms(planeTransform, rayEmitters.get(i).getMaterial(), this);
				plane.draw();
				rayEmitters.get(i).disable();
				setTexture("filterTexture", null);

				setTexture("additive", getTexture("rayAdd"));
				applyFilter(addFilter, getTexture("displayTexture2"), getTexture("displayTextureTemp2"));
				setTexture("displayTexture2", getTexture("displayTextureTemp2"));
			}
		}

		renderPhase = RenderPhase.POST_PROCESS;
		//Hdr
		//applyFilter(hdrFilter, getTexture("displayTexture"), getTexture("displayTexture2"));
		
		if(getBoolean("doSSAO")) applyFilter(ssaoBlurFilter, getTexture("ssao"), getTexture("displayTexture2"));

		setVector3f("inverseFilterTextureSize", new Vector3f(1.0f / getTexture("displayTexture2").getWidth(), 1.0f / getTexture("displayTexture2").getHeight(), 0.0f));
		renderProfileTimer.stopInvocation();
		windowSyncProfileTimer.startInvocation();
		applyFilter(fxaaFilter, getTexture("displayTexture2"), null);
		windowSyncProfileTimer.stopInvocation();
	}

	private void applyFilter(Shader filter, Texture source, Texture dest){
		if(dest == null) window.bindAsRenderTarget();
		else dest.bindAsRenderTarget();

		setTexture("filterTexture", source);

		glClear(GL_DEPTH_BUFFER_BIT);
		filter.bind();
		filter.updateUniforms(planeTransform, planeMaterial, this);
		plane.draw();

		setTexture("filterTexture", null);
	}
	
	public void renderLoadingFrame(String filename){
		Texture texture = new Texture(filename, GL_TEXTURE_2D, GL_LINEAR, GL_RGBA, GL_RGBA, true, GL_COLOR_ATTACHMENT0);
		window.bindAsRenderTarget();
		setTexture("filterTexture", texture);
		glClear(GL_DEPTH_BUFFER_BIT);
		nullFilter.bind();
		int samplerSlot = getSamplerSlot("filterTexture");
		getTexture("filterTexture").bind(samplerSlot);
		nullFilter.setUniformi("R_filterTexture", samplerSlot);
		plane.draw();
		setTexture("filterTexture", null);
	}
	
	public void blurShadowMap(int shadowMapIndex, float blurAmount){
		Texture shadowMap = shadowMaps[shadowMapIndex];
		Texture shadowMapTempTarget = shadowTempTargets[shadowMapIndex];

		setVector3f("blurScale", new Vector3f(blurAmount / shadowMap.getWidth(), 0.0f, 0.0f));
		applyFilter(gaussianBlurFilter, shadowMap, shadowMapTempTarget);

		setVector3f("blurScale", new Vector3f(0.0f, blurAmount / shadowMap.getHeight(), 0.0f));
		applyFilter(gaussianBlurFilter, shadowMapTempTarget, shadowMap);
	}

	public void blurMap(Texture source, Texture sourceTemp, float blurAmount){
		setVector3f("blurScale", new Vector3f(blurAmount / source.getWidth(), 0.0f, 0.0f));
		applyFilter(gaussianBlurFilter, source, sourceTemp);

		setVector3f("blurScale", new Vector3f(0.0f, blurAmount / source.getHeight(), 0.0f));
		applyFilter(gaussianBlurFilter, sourceTemp, source);
	}

	private void waterPreparation(GameObject object){
		if(waterLevelEqual){
			((Texture) waterDataMap.get(0)[0]).bindAsRenderTarget();
			renderPhase = RenderPhase.WATER_REFLECTION;
			glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			float waterLevel = water.get(0).getWaterLevel();

			waterCamera.setProjection(mainCamera.getProjection());
			waterCamera.getTransform().setPos(new Vector3f(mainCamera.getTransform().getTransformedPos().getX(), waterLevel + (waterLevel - mainCamera.getTransform().getTransformedPos().getY()), mainCamera.getTransform().getTransformedPos().getZ()));
			waterCamera.getTransform().setRot(mainCamera.getTransform().getTransformedRot());
			Vector3f flatVec = mainCamera.getTransform().getTransformedRot().getForward().setY(0);
			float angle = (float) Math.acos(mainCamera.getTransform().getTransformedRot().getForward().dot(flatVec.normalized()));
			waterCamera.getTransform().rotate(mainCamera.getTransform().getTransformedRot().getLeft(), 2 * angle);

			Camera temp = mainCamera;
			mainCamera = waterCamera;
			setVector3f("clipPlaneNormal", new Vector3f(0, 1, 0));
			setFloat("clipPlaneDistance", -waterLevel + 0.2f);
			glEnable(GL_CLIP_PLANE0);

			object.renderAll(diffuseShader, this);
			waterDataMap.get(0)[2] = mainCamera.getViewProjection();
			mainCamera = temp;

			((Texture) waterDataMap.get(0)[1]).bindAsRenderTarget();
			renderPhase = RenderPhase.WATER_REFRACTION;
			glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			setVector3f("clipPlaneNormal", new Vector3f(0, -1, 0));
			setFloat("clipPlaneDistance", waterLevel + 1.0f);
			glEnable(GL_CLIP_PLANE0);
			object.renderAll(diffuseShader, this);

		}else{
			for(int i = 0; i < water.size(); i++){
				((Texture) waterDataMap.get(i)[0]).bindAsRenderTarget();
				renderPhase = RenderPhase.WATER_REFLECTION;
				glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
				glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

				float waterLevel = water.get(i).getWaterLevel();

				altCamera.setProjection(mainCamera.getProjection());
				altCamera.getTransform().setPos(new Vector3f(mainCamera.getTransform().getTransformedPos().getX(), waterLevel + (waterLevel - mainCamera.getTransform().getTransformedPos().getY()), mainCamera.getTransform().getTransformedPos().getZ()));
				altCamera.getTransform().setRot(mainCamera.getTransform().getTransformedRot());
				Vector3f flatVec = mainCamera.getTransform().getTransformedRot().getForward().setY(0);
				float angle = (float) Math.acos(mainCamera.getTransform().getTransformedRot().getForward().dot(flatVec.normalized()));
				altCamera.getTransform().rotate(mainCamera.getTransform().getTransformedRot().getLeft(), 2 * angle);

				Camera temp = mainCamera;
				mainCamera = altCamera;
				setVector3f("clipPlaneNormal", new Vector3f(0, 1, 0));
				setFloat("clipPlaneDistance", -waterLevel + 0.2f);
				glEnable(GL_CLIP_PLANE0);

				object.renderAll(diffuseShader, this);
				waterDataMap.get(i)[2] = mainCamera.getViewProjection();
				mainCamera = temp;

				((Texture) waterDataMap.get(i)[1]).bindAsRenderTarget();
				renderPhase = RenderPhase.WATER_REFRACTION;
				glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
				glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
				setVector3f("clipPlaneNormal", new Vector3f(0, -1, 0));
				setFloat("clipPlaneDistance", waterLevel + 1.0f);
				glEnable(GL_CLIP_PLANE0);
				object.renderAll(diffuseShader, this);

			}
		}
	}

	public double displayRenderTime(double dividend, int whiteSpaceNum){
		return renderProfileTimer.displayAndReset("Render Time: ", dividend, whiteSpaceNum);
	}

	public double displayWindowSyncTime(double dividend, int whiteSpaceNum){
		return windowSyncProfileTimer.displayAndReset("Window Sync Time: ", dividend, whiteSpaceNum);
	}

	public static String getOpenGLVersion(){
		return glGetString(GL_VERSION);
	}

	public void addLight(BaseLight light){
		lights.add(light);
	}

	public int addWater(Water water){
		for(int i = 0; i < this.water.size(); i++){
			if(this.water.get(i).getWaterLevel() != water.getWaterLevel()) waterLevelEqual = false;
		}
		this.water.add(water);
		waterDataMap.put(this.water.size() - 1, new Object[] { new Texture(GameWindowDimension.getWidth(), GameWindowDimension.getHeight(), new int[GameWindowDimension.getWidth() * GameWindowDimension.getHeight()], true, GL_TEXTURE_2D, GL_LINEAR, GL_RGBA, GL_RGBA, true, GL_COLOR_ATTACHMENT0), new Texture(GameWindowDimension.getWidth(), GameWindowDimension.getHeight(), new int[GameWindowDimension.getWidth() * GameWindowDimension.getHeight()], true, GL_TEXTURE_2D, GL_LINEAR, GL_RGBA, GL_RGBA, true, GL_COLOR_ATTACHMENT0), new Matrix4f().initIdentity() });
		return this.water.size() - 1;
	}

	public void addRayEmitter(RaysComponent raysComponent){
		rayEmitters.add(raysComponent);
		rayPrePassTextures.add(new Texture(width / 2, height / 2, new int[width * height / 4], true, GL_TEXTURE_2D, GL_LINEAR, GL_RGBA, GL_RGBA, true, GL_COLOR_ATTACHMENT0));
	}

	public void addParticleSystem(ParticleSystem ps){
		particleEmitters.add(ps);
	}

	public void removeParticleSystem(ParticleSystem ps){
		particleEmitters.remove(ps);
	}

	public void addCamera(Camera camera){
		mainCamera = camera;
	}

	public void initFog(Vector3f color, float fogStart, float fogSpan){
		setVector3f("fogColor", color);
		setFloat("fogStart", fogStart);
		setFloat("fogSpan", fogSpan);
	}

	public int getSamplerSlot(String samplerName){
		if(!samplerMap.containsKey(samplerName)) return 0;
		return samplerMap.get(samplerName);
	}

	public Object[] getWaterData(int waterID){
		if(waterLevelEqual) return waterDataMap.get(0);
		return waterDataMap.get(waterID);
	}

	public FloatBuffer getKernel(){
		return kernel;
	}

	public int getKernelSize(){
		return kernelsize;
	}

	public BaseLight getActiveLight(){
		return activeLight;
	}

	public Camera getMainCamera(){
		return mainCamera;
	}

	public Matrix4f getLightMatrix(){
		return lightMatrix;
	}

	public Matrix4f getWaterReflectionMatrix(){
		return waterReflectionMatrix;
	}

	public void setWaterReflectionMatrix(Matrix4f wrm){
		waterReflectionMatrix = wrm;
	}

	public Matrix4f getBiasMatrix(){
		return this.biasMatrix;
	}

	public float[] getClipPlaneEquation(){
		return new float[] { getVector3f("clipPlaneNormal").getX(), getVector3f("clipPlaneNormal").getY(), getVector3f("clipPlaneNormal").getZ(), getFloat("clipPlaneDistance") };
	}

	public Vector3f getAmbientColor(){
		return this.ambientColor;
	}

	public RenderPhase getRenderPhase(){
		return renderPhase;
	}

	public void setMainCamera(Camera mainCamera){
		this.mainCamera = mainCamera;
	}

	public void setAmbientColor(Vector3f color){
		this.ambientColor = color;
		setVector3f("ambient", ambientColor);
	}
}
