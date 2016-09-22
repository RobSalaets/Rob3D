package com.base.game;

import java.util.ArrayList;

import com.base.engine.components.GameComponent;
import com.base.engine.core.Input;
import com.base.engine.rendering.particles.ParticleSystem;

public class ParticleSystemController extends GameComponent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6367695306528381543L;

	private ArrayList<ParticleSystem> particleSystems;
	private int toggleKey;
	private boolean state;

	public ParticleSystemController(ArrayList<ParticleSystem> particleSystems, int toggleKey, boolean beginState){
		this.toggleKey = toggleKey;
		state = beginState;
		this.particleSystems = particleSystems;
		if(state) for(int i = 0; i < particleSystems.size(); i++)
			particleSystems.get(i).on();
		else  for(int i = 0; i < particleSystems.size(); i++)
			particleSystems.get(i).off();
	}

	public ParticleSystemController(ParticleSystem particleSystem, int toggleKey, boolean beginState){
		this(new ArrayList<ParticleSystem>(), toggleKey, beginState);
		particleSystems.add(particleSystem);
	}

	public void input(float delta){
		if(Input.getKey(toggleKey)){
			if(!state) for(int i = 0; i < particleSystems.size(); i++)
				particleSystems.get(i).on();
			else  for(int i = 0; i < particleSystems.size(); i++)
				particleSystems.get(i).off();
		}else{
			if(state) for(int i = 0; i < particleSystems.size(); i++)
				particleSystems.get(i).on();
			else  for(int i = 0; i < particleSystems.size(); i++)
				particleSystems.get(i).off();
		}
	}

	@Override
	public GameComponent getCopy(){
		return new ParticleSystemController(particleSystems, toggleKey, state);
	}
}
