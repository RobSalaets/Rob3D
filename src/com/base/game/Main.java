package com.base.game;

import com.base.engine.core.CoreEngine;

public class Main{

	public static void main(String[] args){
		CoreEngine engine = new CoreEngine(1000, 700, 60, new LevelEditor());
		engine.createWindow("Engine");
		engine.start();
	}
}
