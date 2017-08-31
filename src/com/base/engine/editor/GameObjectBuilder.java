package com.base.engine.editor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;

import com.base.engine.components.BaseLight;
import com.base.engine.components.Camera;
import com.base.engine.components.GameComponent;
import com.base.engine.components.MeshRenderer;
import com.base.engine.components.ParticleSystemController;
import com.base.engine.components.Skydome;
import com.base.engine.core.CoreEngine;
import com.base.engine.core.GameObject;
import com.base.engine.core.Input;
import com.base.engine.core.Quaternion;
import com.base.engine.core.Vector3f;
import com.base.engine.core.persistence.SerializeUtil;
import com.base.engine.editor.gui.Button;
import com.base.engine.editor.gui.CheckButton;
import com.base.engine.editor.gui.InputField;
import com.base.engine.editor.gui.Label;
import com.base.engine.editor.gui.StaticGroupButton;
import com.base.engine.rendering.Material;
import com.base.engine.rendering.Mesh;
import com.base.engine.rendering.RenderingEngine;
import com.base.engine.rendering.Texture;
import com.base.engine.rendering.Window;
import com.base.engine.rendering.particles.ParticleSystem;
import com.base.game.LevelEditor;

public class GameObjectBuilder extends GameComponent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8257955266442276645L;

	private enum ObjectBuilderStage {
		DEFAULT, CREATE, CREATE_MATERIAL, TRANSFORM;
	}

	private enum GuiWindowStage {
		MAIN, SETTTINGS, SAVE, LOAD
	}

	private Window baseWindow;
	private HashMap<Integer, Creation> objects;
	private JFileChooser fileChooser;
	private ObjectBuilderStage objectBuilderStage;
	private GuiWindowStage guiWindowStage;
	private LevelEditor game;
	private RenderingEngine renderingEngine;
	private Camera camera;
	private float transformValue;
	private float snapValue;
	private int highestId;

	public GameObjectBuilder(LevelEditor levelEditor){
		objects = new HashMap<Integer, Creation>();
		fileChooser = new JFileChooser();
		objectBuilderStage = ObjectBuilderStage.DEFAULT;
		guiWindowStage = GuiWindowStage.MAIN;
		game = levelEditor;
		highestId = -1;
		transformValue = 1.0f;
		snapValue = 0.25f;

	}

	public void input(float delta){

		if(((Button) baseWindow.getGuiComponent("save")).getClick()){
			((Button) baseWindow.getGuiComponent("save")).disableButton();
			Window save = new Window(300, 150, "Save");
			new Button("save_button", save, "Save", "Save scene to specified directoryname", 60, 30, 0.15f, 0.4f);
			new Button("cancel_button", save, "Cancel", "", 60, 30, 0.6f, 0.4f);
			new InputField("directory_input", save, "Scene Name", 0.15f, 0.6f, 0.15f);
			baseWindow.addSubWindow("save", save);
			guiWindowStage = GuiWindowStage.SAVE;
		}

		if(guiWindowStage == GuiWindowStage.SAVE){
			if(baseWindow.getSubWindow("save") == null){
				guiWindowStage = GuiWindowStage.MAIN;
				((Button) baseWindow.getGuiComponent("save")).enableButton();
			}else{
				if(((Button) baseWindow.getSubWindow("save").getGuiComponent("save_button")).getClick()){
					String input = ((InputField) baseWindow.getSubWindow("save").getGuiComponent("directory_input")).getInput();
					if(!input.equals("")){
						SerializeUtil.serializeScene(input, game.getSceneParts());
						Material.serializeToDir(input);
						settingsToFile(input);

						baseWindow.getSubWindow("save").getFrame().setVisible(false);
						guiWindowStage = GuiWindowStage.MAIN;
						((Button) baseWindow.getGuiComponent("save")).enableButton();
					}
				}
				if(((Button) baseWindow.getSubWindow("save").getGuiComponent("cancel_button")).getClick()){
					baseWindow.getSubWindow("save").getFrame().setVisible(false);
					guiWindowStage = GuiWindowStage.MAIN;
					((Button) baseWindow.getGuiComponent("save")).enableButton();
				}
			}
		}

		if(((Button) baseWindow.getGuiComponent("load")).getClick()){
			((Button) baseWindow.getGuiComponent("load")).disableButton();
			Window load = new Window(300, 150, "Load Scene");
			new Button("load_button", load, "Load", "Load scene from specified directoryname", 60, 30, 0.15f, 0.4f);
			new Button("cancel_button", load, "Cancel", "", 60, 30, 0.6f, 0.4f);
			new InputField("directory_input", load, "Scene Name", 0.15f, 0.6f, 0.15f);
			baseWindow.addSubWindow("load", load);
			guiWindowStage = GuiWindowStage.LOAD;
		}

		if(guiWindowStage == GuiWindowStage.LOAD){
			if(baseWindow.getSubWindow("load") == null){
				guiWindowStage = GuiWindowStage.MAIN;
				((Button) baseWindow.getGuiComponent("load")).enableButton();
			}else{
				if(((Button) baseWindow.getSubWindow("load").getGuiComponent("load_button")).getClick()){
					String input = ((InputField) baseWindow.getSubWindow("load").getGuiComponent("directory_input")).getInput();
					if(!input.equals("")){
						ArrayList<GameObject> newParts = new ArrayList<>();
						SerializeUtil.loadScene(input, newParts);
						game.add(newParts);
						Material.addFromFile(input);
						if(settingsFromFile(input)){
							baseWindow.getSubWindow("load").getFrame().setVisible(false);
							guiWindowStage = GuiWindowStage.MAIN;
							((Button) baseWindow.getGuiComponent("load")).enableButton();
						}
					}
				}
				if(((Button) baseWindow.getSubWindow("load").getGuiComponent("cancel_button")).getClick()){
					baseWindow.getSubWindow("load").getFrame().setVisible(false);
					guiWindowStage = GuiWindowStage.MAIN;
					((Button) baseWindow.getGuiComponent("load")).enableButton();
				}
			}
		}

		if(((Button) baseWindow.getGuiComponent("settings")).getClick()){
			((Button) baseWindow.getGuiComponent("settings")).disableButton();
			Window settings = new Window(350, 600, "Settings");
			new Label("ssao_label", settings, "Render Ambient Occlusion", 0.1f, 0.05f);
			new CheckButton("ssao_button", settings, "Enable Ambient Occlusion", new ImageIcon("./res/textures/gui/icon_check.png"), renderingEngine.getBoolean("doSSAO"), 15, 15, 0.75f, 0.06f);
			new InputField("ssao_radius_input", settings, "Ambient Occlusion Radius", 0.1f, 0.75f, 0.10f).setText(String.valueOf(renderingEngine.getFloat("ssaoRadius")));
			new InputField("ssao_exposure_input", settings, "Ambient Occlusion Effect", 0.1f, 0.75f, 0.15f).setText(String.valueOf(renderingEngine.getFloat("ssaoExposure")));
			new Label("rays_label", settings, "Render God Rays", 0.1f, 0.20f);
			new CheckButton("rays_button", settings, "Enable God Rays", new ImageIcon("./res/textures/gui/icon_check.png"), renderingEngine.getBoolean("doRays"), 15, 15, 0.75f, 0.21f);
			new Label("water_label", settings, "Render Water", 0.1f, 0.25f);
			new CheckButton("water_button", settings, "Enable Water", new ImageIcon("./res/textures/gui/icon_check.png"), renderingEngine.getBoolean("doWater"), 15, 15, 0.75f, 0.26f);
			String formatAmbientColor = renderingEngine.getAmbientColor().toString().substring(1, renderingEngine.getAmbientColor().toString().length() - 1).replace("f", "").trim();
			new InputField("ambient_color_input", settings, "Ambient Color", 0.1f, 0.75f, 0.30f).setText(formatAmbientColor);

			new Button("skydome_button", settings, "Skydome", "Open Skydome Texture (Select 2 textures for respectively top and bottom half)", 150, 30, 0.5f, 0.4f).center();
			new Button("skydome_rays_button", settings, "Skydome RayMask", "Open raymask for the (top) skydome", 150, 30, 0.5f, 0.48f).center();
			baseWindow.addSubWindow("settings", settings);
			guiWindowStage = GuiWindowStage.SETTTINGS;
			fileChooser.setSelectedFile(null);
		}

		if(guiWindowStage == GuiWindowStage.SETTTINGS){
			if(!baseWindow.getSubWindow("settings").getFrame().isVisible()){
				guiWindowStage = GuiWindowStage.MAIN;
				((Button) baseWindow.getGuiComponent("settings")).enableButton();
			}else{
				renderingEngine.setBoolean("doSSAO", ((CheckButton) baseWindow.getSubWindow("settings").getGuiComponent("ssao_button")).getState());
				renderingEngine.setBoolean("doRays", ((CheckButton) baseWindow.getSubWindow("settings").getGuiComponent("rays_button")).getState());
				renderingEngine.setBoolean("doWater", ((CheckButton) baseWindow.getSubWindow("settings").getGuiComponent("water_button")).getState());
				try{
					renderingEngine.setFloat("ssaoRadius", Float.parseFloat(((InputField) baseWindow.getSubWindow("settings").getGuiComponent("ssao_radius_input")).getInput()));
					renderingEngine.setFloat("ssaoExposure", Float.parseFloat(((InputField) baseWindow.getSubWindow("settings").getGuiComponent("ssao_exposure_input")).getInput()));
				}catch (Exception e){
				}

				String[] colorString = ((InputField) baseWindow.getSubWindow("settings").getGuiComponent("ambient_color_input")).getInput().split(",");
				float r, g, b;
				try{
					r = Float.parseFloat(colorString[0].trim());
					g = Float.parseFloat(colorString[1].trim());
					b = Float.parseFloat(colorString[2].trim());
					renderingEngine.setAmbientColor(new Vector3f(r, g, b));
				}catch (Exception e){
				}
				if(((Button) baseWindow.getSubWindow("settings").getGuiComponent("skydome_button")).getClick()){
					fileChooser.setCurrentDirectory(new File("./res/textures"));
					fileChooser.setMultiSelectionEnabled(true);
					int returnVal = fileChooser.showOpenDialog(baseWindow.getSubWindow("settings").getFrame());
					if(returnVal == JFileChooser.APPROVE_OPTION){
						if(fileChooser.getSelectedFiles().length > 1)
							game.setSkydome(fileChooser.getSelectedFiles()[0].getName(), fileChooser.getSelectedFiles()[1].getName());
						else
							game.setSkydome(fileChooser.getSelectedFile().getName(), "");
					}
				}
			}
		}

		switch(objectBuilderStage){

			case DEFAULT:
				if(((Button) baseWindow.getGuiComponent("create")).getClick()){
					Window createQuery = new Window(300, 300, "Create an Object");
					new Button("add_mesh", createQuery, "Add a mesh", "Click to initialize a mesh", 150, 30, 0.5f, 0.1f).center();
					new Button("add_material", createQuery, "Add a Material", "Click to initialize a material", 150, 30, 0.5f, 0.25f).center();
					new Button("create", createQuery, "Create", "Click to finish the creation process", 150, 30, 0.5f, 0.75f).center();
					baseWindow.addSubWindow("creation_query", createQuery);
					objectBuilderStage = ObjectBuilderStage.CREATE;
					fileChooser.setSelectedFile(null);
					Creation c = new Creation();
					objects.put(highestId, c);
				}
				break;
			case CREATE:
				if(((Button) baseWindow.getSubWindow("creation_query").getGuiComponent("add_mesh")).getClick()){
					fileChooser.setCurrentDirectory(new File("./res/models"));
					int returnVal = fileChooser.showOpenDialog(baseWindow.getSubWindow("creation_query").getFrame());
					if(returnVal == JFileChooser.APPROVE_OPTION){
						Mesh newMesh = new Mesh(fileChooser.getSelectedFile().getName());
						((Button) baseWindow.getSubWindow("creation_query").getGuiComponent("add_mesh")).changeText(fileChooser.getSelectedFile().getName());
						objects.get(highestId).addMesh(newMesh);
					}
				}
				if(((Button) baseWindow.getSubWindow("creation_query").getGuiComponent("add_material")).getClick()){
					Window materialQuery = new Window(250, 400, "Create a Material");
					new Button("material_diffuse", materialQuery, "Diffuse", "Open a diffuse texture", 150, 30, 0.5f, 0.08f).center();
					new Button("material_normal", materialQuery, "Normal", "Open a normal map (with specular in alpha)", 150, 30, 0.5f, 0.18f).center();
					new Button("material_displacement", materialQuery, "Displacement", "Open a displacement map", 150, 30, 0.5f, 0.28f).center();
					new Button("material_ok", materialQuery, "Ok", "", 80, 30, 0.33f, 0.83f).center();
					new Button("material_cancel", materialQuery, "Cancel", "", 80, 30, 0.66f, 0.83f).center();
					new InputField("material_specularIntensity", materialQuery, "Specular Intensity", 0.1f, 0.6f, 0.4f);
					new InputField("material_specularPower", materialQuery, "Specular Power", 0.1f, 0.6f, 0.5f);
					new InputField("material_dispScale", materialQuery, "Displacement Scale", 0.1f, 0.6f, 0.6f);
					new InputField("material_dispBias", materialQuery, "Displacemennt Bias", 0.1f, 0.6f, 0.7f);
					baseWindow.getSubWindow("creation_query").addSubWindow("material_query", materialQuery);
					objectBuilderStage = ObjectBuilderStage.CREATE_MATERIAL;
				}
				if(!baseWindow.getSubWindow("creation_query").getFrame().isVisible()) objectBuilderStage = ObjectBuilderStage.DEFAULT;

				if(((Button) baseWindow.getSubWindow("creation_query").getGuiComponent("create")).getClick()){
					if(objects.get(highestId).material != null && objects.get(highestId).mesh != null) objects.get(highestId).addMeshRenderer();
					objects.get(highestId).addObject(game);
					baseWindow.getSubWindow("creation_query").getFrame().setVisible(false);
					objectBuilderStage = ObjectBuilderStage.TRANSFORM;
					((StaticGroupButton) baseWindow.getGuiComponent("translate")).press();
				}

				break;
			case CREATE_MATERIAL:
				selectTextureForMaterial("material_diffuse", "diffuse");
				selectTextureForMaterial("material_normal", "normalMap");
				selectTextureForMaterial("material_displacement", "dispMap");

				if(((Button) baseWindow.getSubWindow("creation_query").getSubWindow("material_query").getGuiComponent("material_ok")).getClick()){
					boolean flawless = true;
					if(parseMaterialField("material_specularIntensity", "specularIntensity")) flawless = false;
					if(parseMaterialField("material_specularPower", "specularPower")) flawless = false;
					if(parseMaterialField("material_dispScale", "dispMapScale")) flawless = false;
					if(parseMaterialField("material_dispBias", "dispMapBias")) flawless = false;

					if(flawless){
						baseWindow.getSubWindow("creation_query").getSubWindow("material_query").dispose();
						((Button) baseWindow.getSubWindow("creation_query").getGuiComponent("add_material")).changeText("Change Material");
						objectBuilderStage = ObjectBuilderStage.CREATE;
					}
				}

				if(((Button) baseWindow.getSubWindow("creation_query").getSubWindow("material_query").getGuiComponent("material_cancel")).getClick()){
					baseWindow.getSubWindow("creation_query").getSubWindow("material_query").dispose();
					objectBuilderStage = ObjectBuilderStage.CREATE;
				}

				if(!baseWindow.getSubWindow("creation_query").getSubWindow("material_query").getFrame().isVisible()) objectBuilderStage = ObjectBuilderStage.CREATE;
				break;
			case TRANSFORM:
				boolean snap = manageTransformInputFields();
				Vector3f camForward = camera.getTransform().getTransformedRot().getForward();
				Vector3f forward = Math.abs(camForward.dot(Vector3f.xAxis)) > 0.707106 ? Vector3f.xAxis.mul((float) Math.signum(camForward.dot(Vector3f.xAxis))) : Vector3f.zAxis.mul((float) Math.signum(camForward.dot(Vector3f.zAxis)));
				if(((Button) baseWindow.getGuiComponent("translate")).getClick()){
					if(snap){
						if(Input.getKeyDown(Input.KEY_T)) objects.get(highestId).translate(forward, snapValue, 1, true);
						if(Input.getKeyDown(Input.KEY_G)) objects.get(highestId).translate(forward.mul(-1), snapValue, 1, true);
						if(Input.getKeyDown(Input.KEY_F)) objects.get(highestId).translate(forward.cross(Vector3f.yAxis), snapValue, 1, true);
						if(Input.getKeyDown(Input.KEY_H)) objects.get(highestId).translate(forward.cross(Vector3f.yAxis).mul(-1), snapValue, 1, true);
						if(Input.getKeyDown(Input.KEY_5)) objects.get(highestId).translate(Vector3f.yAxis.mul(-1), snapValue, 1, true);
						if(Input.getKeyDown(Input.KEY_6)) objects.get(highestId).translate(Vector3f.yAxis, snapValue, 1, true);
					}else{
						if(Input.getKey(Input.KEY_T)) objects.get(highestId).translate(forward, transformValue, delta, false);
						if(Input.getKey(Input.KEY_G)) objects.get(highestId).translate(forward.mul(-1), transformValue, delta, false);
						if(Input.getKey(Input.KEY_F)) objects.get(highestId).translate(forward.cross(Vector3f.yAxis), transformValue, delta, false);
						if(Input.getKey(Input.KEY_H)) objects.get(highestId).translate(forward.cross(Vector3f.yAxis).mul(-1), transformValue, delta, false);
						if(Input.getKey(Input.KEY_5)) objects.get(highestId).translate(Vector3f.yAxis.mul(-1), transformValue, delta, false);
						if(Input.getKey(Input.KEY_6)) objects.get(highestId).translate(Vector3f.yAxis, transformValue, delta, false);
					}
				}
				if(((Button) baseWindow.getGuiComponent("scale")).getClick()){
					if(snap){
						if(Input.getKeyDown(Input.KEY_V)) objects.get(highestId).scale(new Vector3f(1, 1, 1), -snapValue, true);
						if(Input.getKeyDown(Input.KEY_B)) objects.get(highestId).scale(new Vector3f(1, 1, 1), snapValue, true);
						if(Input.getKeyDown(Input.KEY_T)) objects.get(highestId).scale(forward, snapValue, true);
						if(Input.getKeyDown(Input.KEY_G)) objects.get(highestId).scale(forward.mul(-1), snapValue, true);
						if(Input.getKeyDown(Input.KEY_F)) objects.get(highestId).scale(forward.cross(Vector3f.yAxis), snapValue, true);
						if(Input.getKeyDown(Input.KEY_H)) objects.get(highestId).scale(forward.cross(Vector3f.yAxis).mul(-1), snapValue, true);
						if(Input.getKeyDown(Input.KEY_5)) objects.get(highestId).scale(Vector3f.yAxis.mul(-1), snapValue, true);
						if(Input.getKeyDown(Input.KEY_6)) objects.get(highestId).scale(Vector3f.yAxis, snapValue, true);

					}else{
						if(Input.getKey(Input.KEY_V)) objects.get(highestId).scale(new Vector3f(1, 1, 1), -transformValue, false);
						if(Input.getKey(Input.KEY_B)) objects.get(highestId).scale(new Vector3f(1, 1, 1), transformValue, false);
						if(Input.getKey(Input.KEY_T)) objects.get(highestId).scale(forward, transformValue, false);
						if(Input.getKey(Input.KEY_G)) objects.get(highestId).scale(forward.mul(-1), transformValue, false);
						if(Input.getKey(Input.KEY_F)) objects.get(highestId).scale(forward.cross(Vector3f.yAxis), transformValue, false);
						if(Input.getKey(Input.KEY_H)) objects.get(highestId).scale(forward.cross(Vector3f.yAxis).mul(-1), transformValue, false);
						if(Input.getKey(Input.KEY_5)) objects.get(highestId).scale(Vector3f.yAxis.mul(-1), transformValue, false);
						if(Input.getKey(Input.KEY_6)) objects.get(highestId).scale(Vector3f.yAxis, transformValue, false);
					}
				}
				if(((Button) baseWindow.getGuiComponent("rotate")).getClick()){
					float reverse = Input.getMouse(1) ? -20f : 20f;
					if(snap){
						if(Input.getKeyDown(Input.KEY_4)) objects.get(highestId).rotate(Vector3f.xAxis, 0.05f * reverse * (float) Math.PI * snapValue, 1);
						if(Input.getKeyDown(Input.KEY_5)) objects.get(highestId).rotate(Vector3f.yAxis, 0.05f * reverse * (float) Math.PI * snapValue, 1);
						if(Input.getKeyDown(Input.KEY_6)) objects.get(highestId).rotate(Vector3f.zAxis, 0.05f * reverse * (float) Math.PI * snapValue, 1);
					}else{
						if(Input.getKey(Input.KEY_4)) objects.get(highestId).rotate(Vector3f.xAxis, reverse * (float) Math.PI * transformValue, delta);
						if(Input.getKey(Input.KEY_5)) objects.get(highestId).rotate(Vector3f.yAxis, reverse * (float) Math.PI * transformValue, delta);
						if(Input.getKey(Input.KEY_6)) objects.get(highestId).rotate(Vector3f.zAxis, reverse * (float) Math.PI * transformValue, delta);
					}
					if(Input.getKey(Input.KEY_C)) objects.get(highestId).clearRot();
				}
				if(((Button) baseWindow.getGuiComponent("create")).getClick()){
					objectBuilderStage = ObjectBuilderStage.DEFAULT;

				}
				break;
			default:
				break;

		}
	}

	private void selectTextureForMaterial(String component, String materialMap){
		if(((Button) baseWindow.getSubWindow("creation_query").getSubWindow("material_query").getGuiComponent(component)).getClick()){
			fileChooser.setCurrentDirectory(new File("./res/textures"));
			int returnVal = fileChooser.showOpenDialog(baseWindow.getSubWindow("creation_query").getSubWindow("material_query").getFrame());
			if(returnVal == JFileChooser.APPROVE_OPTION){
				Texture tex = new Texture(fileChooser.getSelectedFile().getName());
				objects.get(highestId).material.setTexture(materialMap, tex);
				((Button) baseWindow.getSubWindow("creation_query").getSubWindow("material_query").getGuiComponent(component)).addIcon(new ImageIcon("./res/textures/" + fileChooser.getSelectedFile().getName()));
			}
		}
	}

	private boolean parseMaterialField(String component, String materialValue){
		String s = ((InputField) baseWindow.getSubWindow("creation_query").getSubWindow("material_query").getGuiComponent(component)).getInput();
		try{
			objects.get(highestId).material.setFloat(materialValue, Float.parseFloat(s));
			return false;
		}catch (Exception e){
			return true;
		}
	}

	private boolean manageTransformInputFields(){
		int wheelAmt = Input.getWheelAmt();
		float transformAmountS;
		try{
			transformAmountS = Float.parseFloat(((InputField) baseWindow.getGuiComponent("transformAmount")).getInput());
		}catch (Exception e){
			transformAmountS = 1.0f;
			if(!((InputField) baseWindow.getGuiComponent("transformAmount")).getInput().equals("")) ((InputField) baseWindow.getGuiComponent("transformAmount")).setText(String.valueOf(1.0f));
		}
		transformValue = Input.getKey(Input.KEY_LCONTROL) ? transformAmountS * (1200 + wheelAmt) / 1200 : transformAmountS;
		if(wheelAmt != 0) ((InputField) baseWindow.getGuiComponent("transformAmount")).setText(String.valueOf(transformValue));

		float snapAmountS;
		try{
			snapAmountS = Float.parseFloat(((InputField) baseWindow.getGuiComponent("snapAmount")).getInput());

		}catch (Exception e){
			snapAmountS = 0.25f;
			if(!((InputField) baseWindow.getGuiComponent("snapAmount")).getInput().equals("")) ((InputField) baseWindow.getGuiComponent("snapAmount")).setText(String.valueOf(.25f));
		}
		snapValue = snapAmountS;
		return ((CheckButton) baseWindow.getGuiComponent("snapButton")).getState();
	}

	private void settingsToFile(String directoryName){
		File settingsFile = new File("./res/scene/" + directoryName + "/rendersettings.properties");
		FileWriter writer;
		Properties propertiesObject = new Properties();
		try{
			settingsFile.createNewFile();
			writer = new FileWriter(settingsFile);
			propertiesObject.clear();
			propertiesObject.setProperty("doSSAO", String.valueOf(renderingEngine.getBoolean("doSSAO")));
			propertiesObject.setProperty("SSAORadius", String.valueOf(renderingEngine.getFloat("ssaoRadius")));
			propertiesObject.setProperty("SSAOExposure", String.valueOf(renderingEngine.getFloat("ssaoExposure")));
			propertiesObject.setProperty("doRays", String.valueOf(renderingEngine.getBoolean("doRays")));
			propertiesObject.setProperty("doWater", String.valueOf(renderingEngine.getBoolean("doWater")));
			propertiesObject.setProperty("ambientColor", renderingEngine.getAmbientColor().toString());
			propertiesObject.setProperty("topSkydome", game.getSkydomeMapNames()[0]);
			propertiesObject.setProperty("bottomSkydome", game.getSkydomeMapNames()[1]);
			propertiesObject.store(writer, "render settings");
			writer.close();
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	private boolean settingsFromFile(String directoryName){
		File configFile = new File("./res/scene/" + directoryName + "/rendersettings.properties");
		InputStream inputStream;
		try{
			inputStream = new FileInputStream(configFile);
			Properties props = new Properties();
			props.load(inputStream);
			renderingEngine.setBoolean("doSSAO", Boolean.parseBoolean(props.getProperty("doSSAO")));
			renderingEngine.setFloat("ssaoRadius", Float.parseFloat(props.getProperty("SSAORadius")));
			renderingEngine.setFloat("ssaoExposure", Float.parseFloat(props.getProperty("SSAOExposure")));
			renderingEngine.setBoolean("doRays", Boolean.parseBoolean(props.getProperty("doRays")));
			renderingEngine.setBoolean("doWater", Boolean.parseBoolean(props.getProperty("doWater")));
			renderingEngine.setAmbientColor(new Vector3f(props.getProperty("ambientColor")));
			game.setSkydome(props.getProperty("topSkydome"), props.getProperty("bottomSkydome"));
			inputStream.close();
		}catch (FileNotFoundException e){
			System.err.println("Scene doesn't exist");
			((InputField) baseWindow.getSubWindow("load").getGuiComponent("directory_input")).setText("Doest Exist, Try agian");
			e.printStackTrace();
			return false;
		}catch (IOException e){
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void update(float delta){

	}

	@Override
	public void addToEngine(CoreEngine engine){
		renderingEngine = engine.getRenderingEngine();
		baseWindow = engine.getWindow();
		camera = engine.getRenderingEngine().getMainCamera();
		initGui();
	}

	private void initGui(){
		new Button("create", baseWindow, new ImageIcon("./res/textures/gui/icon_create.png"), "Click to create a new GameObject", 38, 38, 0.02f, 0.01f);
		new Button("settings", baseWindow, new ImageIcon("./res/textures/gui/icon_configure.png"), "Click to configure render settings", 38, 38, 0.17f, 0.01f);
		new Button("save", baseWindow, new ImageIcon("./res/textures/gui/icon_save.png"), "Click to save this scene", 38, 38, 0.32f, 0.01f);
		new Button("load", baseWindow, new ImageIcon("./res/textures/gui/icon_load.png"), "Click to load a scene", 38, 38, 0.47f, 0.01f);
		StaticGroupButton t = (StaticGroupButton) new StaticGroupButton("translate", baseWindow, new ImageIcon("./res/textures/gui/icon_translate.png"), "Translate current Object", 38, 38, 0.02f, 0.08f);
		StaticGroupButton r = (StaticGroupButton) new StaticGroupButton("rotate", baseWindow, new ImageIcon("./res/textures/gui/icon_rotate.png"), "Rotate current Object", 38, 38, 0.17f, 0.08f);
		StaticGroupButton s = (StaticGroupButton) new StaticGroupButton("scale", baseWindow, new ImageIcon("./res/textures/gui/icon_scale.png"), "Scale current Object", 38, 38, 0.32f, 0.08f);
		ArrayList<StaticGroupButton> group = new ArrayList<>();
		group.add(t);
		group.add(r);
		group.add(s);
		t.setGroup(group);
		r.setGroup(group);
		s.setGroup(group);
		InputField transformAmount = new InputField("transformAmount", baseWindow, "Transform Amount:", 0.02f, 0.4f, 0.14f);
		transformAmount.setText(String.valueOf(transformValue));
		new Label("snapLabel", baseWindow, "Snap", 0.02f, 0.17f);
		new CheckButton("snapButton", baseWindow, "Use snapping in tranformation operations", new ImageIcon("./res/textures/gui/icon_check.png"), false, 15, 15, 0.2f, 0.173f);
		InputField snapAmount = new InputField("snapAmount", baseWindow, "Snap Amount:", 0.02f, 0.4f, 0.2f);
		snapAmount.setText(String.valueOf(snapValue));
	}

	private class Creation{

		private GameObject object;
		private Mesh mesh;
		private Material material;
		private int id;

		public Creation(){
			object = new GameObject();
			material = new Material();
			material.addToList();
			id = ++highestId;
		}

		public void addObject(LevelEditor game){
			game.add(object);
		}

		protected void addMesh(Mesh mesh){
			this.mesh = mesh;
		}

		protected void addLight(BaseLight light){
			object.addComponent(light);
		}

		protected void addParticleSystem(ParticleSystem partSystem, ParticleSystemController partSystemCtrl){
			object.addComponent(partSystem);
			if(partSystemCtrl != null) object.addComponent(partSystemCtrl);
		}

		protected void addMeshRenderer(){
			if(material != null && mesh != null) object.addComponent(new MeshRenderer(mesh, material));
			else{
				System.err.println("Add mesh and material first");
				throw new IllegalArgumentException();
			}
		}

		protected void translate(Vector3f axis, float amount, float delta, boolean snap){
			if(snap){
				object.getTransform().setPos(object.getTransform().getTransformedPos().add(axis.mul(amount * delta)).snap(amount));
			}else object.getTransform().setPos(object.getTransform().getTransformedPos().add(axis.mul(amount * delta)));
		}

		protected void scale(Vector3f direction, float amount, boolean snap){
			if(snap) object.getTransform().setScale(object.getTransform().getScale().add(direction.mul(amount)).snap(amount));
			else object.getTransform().setScale(object.getTransform().getScale().add(direction.mul(amount)));

		}

		protected void rotate(Vector3f axis, float amount, float delta){
			object.getTransform().rotate(axis, (float) Math.toRadians(amount * delta));
		}

		protected void clearRot(){
			object.getTransform().setRot(new Quaternion(0, 0, 0, 1));
		}
	}

}
