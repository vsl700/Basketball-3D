package com.vasciie.bkbl.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.vasciie.bkbl.MyGdxGame;

public class SplashScreen1 implements Screen {

	MyGdxGame game;
	
	SpriteBatch batch;
	Texture logo;
	OrthographicCamera cam;
	
	float time;
	
	public SplashScreen1(MyGdxGame mg) {
		game=mg;
		
		batch = new SpriteBatch();
		cam = new OrthographicCamera();
		createTexture();
		
		time = 1.5f;
	}
	
	protected void createTexture() {
		logo = new Texture(Gdx.files.internal("application/ve_logo.png"));
	}
	
	protected void checkTime(float delta) {
		if(time <= 0)
			game.setScreen(game.spScreen2);
		else time-= delta;
	}
	
	@Override
	public void show() {
		

	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		batch.draw(logo, cam.viewportWidth / 2 - logo.getWidth() / 2, cam.viewportHeight / 2 - logo.getHeight() / 2);
		batch.end();
		
		checkTime(delta);

	}

	@Override
	public void resize(int width, int height) {
		cam.setToOrtho(false, width, height);
		
	}

	@Override
	public void pause() {
		

	}

	@Override
	public void resume() {
		

	}

	@Override
	public void hide() {
		

	}

	@Override
	public void dispose() {
		batch.dispose();
		logo.dispose();
	}

}
