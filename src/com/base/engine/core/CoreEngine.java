package com.base.engine.core;

import com.base.engine.profiling.ProfileTimer;
import com.base.engine.rendering.RenderingEngine;
import com.base.engine.rendering.Window;

public class CoreEngine {
	
	private boolean isRunning;
	private Game game;
	private Game next;
	private Window window;
	private RenderingEngine renderingEngine;
	private ProfileTimer sleepTimer;
	private ProfileTimer windowUpdateTimer;
	private int width;
	private int height;
	private double frameTime;
	
	public CoreEngine(int width, int height, double framerate, Game game){
		isRunning = false;
		this.game = game;
		this.width = width;
		this.height = height;
		this.frameTime = 1.0/framerate;
		sleepTimer = new ProfileTimer();
		windowUpdateTimer = new ProfileTimer();
		this.game.setEngine(this);
	}
	
	public void createWindow(String title){
		window = new Window(width, height, title, game);
		this.renderingEngine = new RenderingEngine(window);
		
	}
	
	public void setNewGameInstance(Game gameInstance){
		this.stop();
		next = gameInstance;
	} 
	
	public void start(){
		if(isRunning)
			return;
		
		run();
	}
	
	public void stop(){
		if(!isRunning)
			return;
		
		isRunning = false;
	}
	
	private void run(){
		isRunning = true;
		
		int frames = 0;
		double frameCounter = 0;
		renderingEngine.renderLoadingFrame("loading_frame.png");
		window.render();
		game.init();
		
		double lastTime = Time.getTime();
		double unprocessedTime=0;
		
		while(isRunning){
			
			boolean render = false;
			
			double startTime = Time.getTime();
			double passedTime = startTime - lastTime;
			lastTime = startTime;
			
			unprocessedTime += passedTime;
			frameCounter += passedTime;
			
			if(frameCounter >= 1.0){
				double totalTime = (1000.0 * frameCounter)/ ((double)frames);
				
				double totalRecordedTime = 0.0;
				int whiteSpaceNum = 26;
				totalRecordedTime += game.displayInputTime((double)frames, whiteSpaceNum);
				totalRecordedTime += game.displayUpdateTime((double)frames, whiteSpaceNum);
				totalRecordedTime += renderingEngine.displayRenderTime((double)frames, whiteSpaceNum);
				totalRecordedTime += sleepTimer.displayAndReset("Sleep Time: ", (double)frames, whiteSpaceNum);
				totalRecordedTime += windowUpdateTimer.displayAndReset("Window Update Time: ", (double)frames, whiteSpaceNum);
				//totalRecordedTime += renderingEngine.displayWindowSyncTime((double)frames, whiteSpaceNum);
				System.out.println("Other Time:               " + (double)(totalTime - totalRecordedTime) + " ms");
//				EditorContentPane.printToConsole("Other Time:               " + (double)(totalTime - totalRecordedTime) + " ms");
				System.out.println("Total Time:               " + totalTime + " ms");
				System.out.println("FPS:                      " + 1000.0f/totalTime);
//				EditorContentPane.printToConsole("\nTotal Time:               " + totalTime + " ms");
				System.out.println();
				frames = 0;
				frameCounter = 0;
			}
			while(unprocessedTime > frameTime){
				
				render = true;
				
				if(window.isCloseRequested())
					stop();
				
				
				
				game.input((float)frameTime);	
				Input.update();		
				
				game.update((float)frameTime);
				
				if(!window.isCloseRequested())
					window.updateComponents();
				
				unprocessedTime -= frameTime;
				
			}
			if(render){
				game.render(renderingEngine);
				windowUpdateTimer.startInvocation();
				window.render();
				windowUpdateTimer.stopInvocation();
				frames++;
			}else{
				try {
					sleepTimer.startInvocation();
					Thread.sleep(1);
					sleepTimer.stopInvocation();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		if(next == null)
			cleanUp();
		else{
			game = next;
			next = null;
			renderingEngine.addCamera(null);
			game.setEngine(this);
			this.start();
		}
			
	}
	
	private void cleanUp(){
		window.dispose();
	}
	
	public RenderingEngine getRenderingEngine(){
		return this.renderingEngine;
	}

	public Window getWindow(){
		return this.window;
	}
}
