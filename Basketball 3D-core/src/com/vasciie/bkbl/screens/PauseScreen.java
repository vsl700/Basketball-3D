package com.vasciie.bkbl.screens;

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
		font.getData().setScale(1);
		
		resume = new Button("Resume", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true);
		newGame = new Button("New Game", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true);
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
		settings.render(batch, shape, cam);
		quit.render(batch, shape, cam);
		
		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE) || resume.justReleased(cam)) {
			exit();
			game.setScreen(game.game);
		}else if(newGame.justReleased(cam)) {
			game.game.reset();
			game.game.show();
			exit();
		}
		else if(settings.justReleased(cam)) {
			game.settings.setPreviousScreen(this);
			game.setScreen(game.settings);
		}
		else if(quit.justReleased(cam)) {
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
		
		float spaceHeight = 120;
		
		resume.setSize(game.pixelXByCurrentSize(223), game.pixelYByCurrentSize(30));
		resume.setPos(width / 2 - resume.getWidth() / 2, game.pixelYByCurrentSize(spaceHeight * 4));
		
		newGame.setSize(game.pixelXByCurrentSize(223), game.pixelYByCurrentSize(30));
		newGame.setPos(width / 2 - newGame.getWidth() / 2, game.pixelYByCurrentSize(spaceHeight * 3));
		
		settings.setSize(game.pixelXByCurrentSize(223), game.pixelYByCurrentSize(30));
		settings.setPos(width / 2 - settings.getWidth() / 2, game.pixelYByCurrentSize(spaceHeight * 2));
		
		quit.setSize(game.pixelXByCurrentSize(223), game.pixelYByCurrentSize(30));
		quit.setPos(width / 2 - quit.getWidth() / 2, game.pixelYByCurrentSize(spaceHeight));
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
