package com.gamesbg.bkbl;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gamesbg.bkbl.screens.*;

public class MyGdxGame extends Game {
	
	SpriteBatch batch;
	BitmapFont font;
	OrthographicCamera cam;
	
	public MainScreen main;
	public LevelScreen level;
	public GameScreen game;
	
	public static int WIDTH = 640;
	public static int HEIGHT = 480;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		
		font = new BitmapFont();
		font.getData().setScale(1);
		
		cam = new OrthographicCamera();
		
		main = new MainScreen(this);
		setScreen(main);
		
		level = new LevelScreen(this);
		game = new GameScreen(this);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT|GL20.GL_DEPTH_BUFFER_BIT);
		
		super.render();
		
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		font.draw(batch, Gdx.graphics.getFramesPerSecond() + " fps; SNAPSHOT (v0.5.1)", 0, 13);
		batch.end();
	}
	
	@Override
	public void resize(int width, int height) {
		cam.setToOrtho(false, width, height);
		getScreen().resize(width, height); //That method doesn't automatically call in the screen so we have to call it manually
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
	
	public float pixelXByCurrentSize(float value) {
	
		return value * cam.viewportWidth / 1280;
	}
	
	public float pixelYByCurrentSize(float value) {
		
		return value * cam.viewportHeight / 720;
	}
}
