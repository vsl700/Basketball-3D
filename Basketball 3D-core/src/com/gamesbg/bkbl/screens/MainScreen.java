package com.gamesbg.bkbl.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.gamesbg.bkbl.MyGdxGame;
import com.gamesbg.bkbl.gui.Button;

public class MainScreen implements Screen {
	
	MyGdxGame game;
	
	SpriteBatch batch;
	ShapeRenderer shape;
	OrthographicCamera cam;
	BitmapFont font;
	
	Button play, settings, quit;
	
	public MainScreen(MyGdxGame mg) {
		game = mg;
		
		cam = new OrthographicCamera();
		
		shape = new ShapeRenderer();
		batch = new SpriteBatch();
		font = new BitmapFont();
		font.getData().setScale(1);
		
		play = new Button("Play", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true);
		settings = new Button("Settings", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true);
		quit = new Button("Quit", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true);
	}

	@Override
	public void show() {

	}

	@Override
	public void render(float delta) {
		//batch.setProjectionMatrix(cam.combined);
		//batch.begin();
		cam.update();
		
		game.renderLogo(batch, cam);
		
		play.render(batch, shape, cam);
		settings.render(batch, shape, cam);
		quit.render(batch, shape, cam);
		
		if(quit.justReleased(cam))
			Gdx.app.exit();
		else if(settings.justReleased(cam))
			game.setScreen(game.settings);
		else if(play.justReleased(cam))
			game.setScreen(game.level);
		//batch.end();
	}

	@Override
	public void resize(int width, int height) {
		cam.setToOrtho(false, width, height);
		
		//font.getData().setScale(width * height / 921600);
		
		play.setPosAndSize(game.pixelXByCurrentSize(178), game.pixelYByCurrentSize(240), game.pixelXByCurrentSize(223), game.pixelYByCurrentSize(30));
		settings.setPosAndSize(game.pixelXByCurrentSize(520), game.pixelYByCurrentSize(240), game.pixelXByCurrentSize(223), game.pixelYByCurrentSize(30));
		quit.setPosAndSize(game.pixelXByCurrentSize(863), game.pixelYByCurrentSize(240), game.pixelXByCurrentSize(223), game.pixelYByCurrentSize(30));
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
		shape.dispose();
		font.dispose();
	}

}
