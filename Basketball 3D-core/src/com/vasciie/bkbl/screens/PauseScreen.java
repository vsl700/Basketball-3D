package com.vasciie.bkbl.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.vasciie.bkbl.MyGdxGame;
import com.vasciie.bkbl.gui.Button;

public class PauseScreen implements Screen {

	MyGdxGame game;
	
	ShapeRenderer shape;
	SpriteBatch batch;
	BitmapFont font;
	OrthographicCamera cam;
	
	Button resume, newGame, settings, quit;
	
	boolean active;
	
	public PauseScreen(MyGdxGame mg) {
		game = mg;
		
		cam = new OrthographicCamera();
		
		shape = new ShapeRenderer();
		batch = new SpriteBatch();
		font = new BitmapFont();
		font.getData().setScale(MyGdxGame.GUI_SCALE);
		
		resume = new Button("Resume", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true);
		newGame = new Button("New Game", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true);
		if(!Gdx.app.getType().equals(Application.ApplicationType.Android))
			settings = new Button("Settings", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true);
		quit = new Button("Quit", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true);
	}
	
	@Override
	public void show() {
		active = true;
	}
	
	@Override
	public void render(float delta) {
		game.renderLogo(batch, cam);
		
		resume.render(batch, shape, cam);
		newGame.render(batch, shape, cam);
		if(!Gdx.app.getType().equals(Application.ApplicationType.Android))
			settings.render(batch, shape, cam);
		quit.render(batch, shape, cam);
		
		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Keys.BACK) || resume.justReleased()) {
			exit();
			game.setScreen(game.game);
		}else if(newGame.justReleased()) {
			game.game.reset();
			game.game.show();
			exit();
		}
		else if(!Gdx.app.getType().equals(Application.ApplicationType.Android) && settings.justReleased()) {
			game.settings.setPreviousScreen(this);
			game.setScreen(game.settings);
		}
		else if(quit.justReleased()) {
			exit();
			game.game.reset();
			game.setScreen(game.main);
		}
			
	}
	
	@Override
	public void hide() {
		
	}
	
	public void exit() {
		active = false;
	}
	
	@Override
	public void resize(int width, int height) {
		cam.setToOrtho(false, width, height);

		System.out.println(height);

		float spaceHeight = 720 * 3 / 4;

		resume.setSize(game.pixelXByCurrentSize(223 * MyGdxGame.GUI_SCALE), game.pixelYByCurrentSize(30 * MyGdxGame.GUI_SCALE));
		resume.setPos(width / 2 - resume.getWidth() / 2, game.pixelYByCurrentSize(spaceHeight-= 120));
		
		newGame.setSize(game.pixelXByCurrentSize(223 * MyGdxGame.GUI_SCALE), game.pixelYByCurrentSize(30 * MyGdxGame.GUI_SCALE));
		newGame.setPos(width / 2 - newGame.getWidth() / 2, game.pixelYByCurrentSize(spaceHeight-= 120));

		if(!Gdx.app.getType().equals(Application.ApplicationType.Android)) {
			settings.setSize(game.pixelXByCurrentSize(223 * MyGdxGame.GUI_SCALE), game.pixelYByCurrentSize(30 * MyGdxGame.GUI_SCALE));
			settings.setPos(width / 2 - settings.getWidth() / 2, game.pixelYByCurrentSize(spaceHeight-= 120));
		}

		quit.setSize(game.pixelXByCurrentSize(223 * MyGdxGame.GUI_SCALE), game.pixelYByCurrentSize(30 * MyGdxGame.GUI_SCALE));
		quit.setPos(width / 2 - quit.getWidth() / 2, game.pixelYByCurrentSize(spaceHeight-= 120));
	}
	
	public boolean isActive() {
		return active;
	}

	@Override
	public void pause() {
		
		
	}

	@Override
	public void resume() {
		
		
	}

	@Override
	public void dispose() {
		batch.dispose();
		shape.dispose();
		font.dispose();
	}
	
}
