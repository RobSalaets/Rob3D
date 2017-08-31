package com.base.engine.core;

import java.io.Serializable;
import java.util.ArrayList;

import com.base.engine.components.GameComponent;
import com.base.engine.components.MeshRenderer;
import com.base.engine.rendering.RenderPhase;
import com.base.engine.rendering.RenderingEngine;
import com.base.engine.rendering.Shader;

public class GameObject implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6763849364652566267L;

	private ArrayList<GameObject> children;
	private ArrayList<GameComponent> components;
	private Transform transform;
	private transient CoreEngine engine;


	public GameObject(){
		children = new ArrayList<GameObject>();
		components = new ArrayList<GameComponent>();
		transform = new Transform();
		engine = null;
	}

	public GameObject addChild(GameObject child){
		children.add(child);
		child.getTransform().setParent(transform);
		child.setEngine(engine);
		return this;
	}

	public void addChild(ArrayList<GameObject> children){
		this.children.addAll(children);
		for(int i = 0; i < children.size(); i++){
			children.get(i).setEngine(engine);
			children.get(i).getTransform().setParent(transform);
		}
	}

	public GameObject addComponent(GameComponent component){
		components.add(component);
		component.setParent(this);

		return this;
	}

	public void inputAll(float delta){
		input(delta);

		for(GameObject child : children)
			child.inputAll(delta);
	}

	public void updateAll(float delta){
		update(delta);

		for(GameObject child : children)
			child.updateAll(delta);
	}

	public void renderAll(Shader shader, RenderingEngine renderingEngine){
		render(shader, renderingEngine);

		for(GameObject child : children)
			child.renderAll(shader, renderingEngine);
	}

	public void input(float delta){
		transform.update();

		for(GameComponent component : components)
			component.input(delta);
	}

	public void update(float delta){
		for(GameComponent component : components)
			component.update(delta);
	}

	public void render(Shader shader, RenderingEngine renderingEngine){
		for(GameComponent component : components)
			component.render(shader, renderingEngine);
	}

	public ArrayList<GameObject> getAllAttached(){
		ArrayList<GameObject> result = new ArrayList<GameObject>();

		for(GameObject child : children)
			result.addAll(child.getAllAttached());

		result.add(this);
		return result;
	}

	public Transform getTransform(){
		return transform;
	}

	public void setEngine(CoreEngine engine){
		if(this.engine != engine){
			this.engine = engine;

			for(GameComponent component : components)
				component.addToEngine(engine);

			for(GameObject child : children)
				child.setEngine(engine);
		}
	}

	public ArrayList<GameComponent> getComponents(){
		return this.components;
	}

	public GameObject setTransform(Transform transform){
		this.transform = transform;
		return this;
	}
	
//	public GameObject getCopy(){
//		GameObject copy = new GameObject();
//		copy.transform = new Transform(transform);
//		for(GameObject child : children)
//			copy.addChild(child.getCopy());
//		for(GameComponent component : components)
//			copy.addComponent(component.getCopy());
//		copy.engine = engine;
//		return copy;
//	}

	public void deleteChild(GameObject gameObject){
		if(this.children.remove(gameObject)){
		}else throw new NullPointerException();
	}

}
