package com.vasciie.bkbl.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.vasciie.bkbl.MyGdxGame;
import com.vasciie.bkbl.gui.Button;

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
		font.getData().setScale(MyGdxGame.GUI_SCALE);
		
		play = new Button("Play", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true);
		if(!Gdx.app.getType().equals(Application.ApplicationType.Android))
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
		if(!Gdx.app.getType().equals(Application.ApplicationType.Android))
			settings.render(batch, shape, cam);
		quit.render(batch, shape, cam);
		
		if(quit.justReleased())
			Gdx.app.exit();
		else if(!Gdx.app.getType().equals(Application.ApplicationType.Android) && settings.justReleased()) {
			game.settings.setPreviousScreen(this);
			game.setScreen(game.settings);
		}
		else if(play.justReleased())
			game.setScreen(game.level);
		//batch.end();
	}

	@Override
	public void resize(int width, int height) {
		cam.setToOrtho(false, width, height);
		
		//font.getData().setScale(width * height / 921600);

		play.setSize(game.pixelXByCurrentSize(223 * MyGdxGame.GUI_SCALE), game.pixelYByCurrentSize(30 * MyGdxGame.GUI_SCALE));
		play.setPos(width / 4 - play.getWidth() / 2, game.pixelYByCurrentSize(240 / MyGdxGame.GUI_SCALE));
		if(!Gdx.app.getType().equals(Application.ApplicationType.Android))
			settings.setPosAndSize(game.pixelXByCurrentSize(520), game.pixelYByCurrentSize(240), game.pixelXByCurrentSize(223), game.pixelYByCurrentSize(30));
		quit.setSize(game.pixelXByCurrentSize(223 * MyGdxGame.GUI_SCALE), game.pixelYByCurrentSize(30 * MyGdxGame.GUI_SCALE));
		quit.setPos(width * 3 / 4 - play.getWidth() / 2, game.pixelYByCurrentSize(240 / MyGdxGame.GUI_SCALE));
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
