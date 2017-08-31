package com.base.engine.components;

import com.base.engine.core.CoreEngine;
import com.base.engine.core.Game;
import com.base.engine.core.Input;

public class GameInstanceLoader extends GameComponent{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7755746526122733222L;
	private Game newGame;
	private CoreEngine engine;
	private int keyCode;
	
	public GameInstanceLoader(Game newGame, int key){
		this.newGame = newGame;
		this.keyCode = key;
	}
	
	public void input(float delta){
		if(Input.getKeyDown(keyCode)){
			engine.setNewGameInstance(newGame);
		}
	}
	
	@Override
	public void addToEngine(CoreEngine engine){
		this.engine = engine;
	}
}
