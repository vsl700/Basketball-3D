package com.vasciie.bkbl.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.vasciie.bkbl.MyGdxGame;
import com.vasciie.bkbl.gui.Button;
import com.vasciie.bkbl.gui.GUIRenderer;

public class GameTypeScreen implements Screen, GUIRenderer {
	
	MyGdxGame game;
	
	SpriteBatch batch;
	ShapeRenderer shape;
	OrthographicCamera cam;
	
	BitmapFont font;
	
	Button normalGame, tutorial, challenge, goBack;
	
	
	public GameTypeScreen(MyGdxGame mg) {
		game = mg;
		
		
		cam = new OrthographicCamera();
		
		shape = new ShapeRenderer();
		batch = new SpriteBatch();
		
		font = new BitmapFont();
		font.getData().setScale(MyGdxGame.GUI_SCALE);
		
		normalGame = new Button("Normal Game", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
		tutorial = new Button("Tutorial Levels", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
		challenge = new Button("Challenge Levels", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
		
		goBack = new Button("Go Back", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
	}

	@Override
	public void show() {
		

	}

	@Override
	public void render(float delta) {
		normalGame.update();
		tutorial.update();
		challenge.update();
		goBack.update();
		
		if(normalGame.justReleased()) {
			game.setScreen(game.level);
		}else if(tutorial.justReleased()) {
			game.setScreen(game.tutorial);
		}else if(challenge.justReleased()) {
			game.setScreen(game.challenge);
		}else if(goBack.justReleased()) {
			game.setScreen(game.playerGameType);
		}
		
		game.renderLogo(batch, cam);

		normalGame.draw();
		tutorial.draw();
		challenge.draw();
		goBack.draw();
	}

	@Override
	public void resize(int width, int height) {
		cam.setToOrtho(false, width, height);

		float btnY = 260;
		
		normalGame.setSize(game.pixelXByCurrentSize(223 * MyGdxGame.GUI_SCALE), game.pixelYByCurrentSize(30 * MyGdxGame.GUI_SCALE));
		normalGame.setPos(width / 4 - normalGame.getWidth() / 1.25f, game.pixelYByCurrentSize(btnY));
		
		tutorial.setSize(normalGame.getWidth(), normalGame.getHeight());
		tutorial.setPos(width * 2 / 4 - normalGame.getWidth() / 2, normalGame.getY());
		
		challenge.setSize(normalGame.getWidth(), normalGame.getHeight());
		challenge.setPos(width * 3 / 4 - normalGame.getWidth() / 4, normalGame.getY());
		
		goBack.setSize(normalGame.getWidth(), normalGame.getHeight());
		goBack.setPos(tutorial.getX(), tutorial.getY() / 2);
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

	@Override
	public SpriteBatch getSpriteBatch() {
		return batch;
	}

	@Override
	public ShapeRenderer getShapeRenderer() {
		return shape;
	}

	@Override
	public OrthographicCamera getCam() {
		return cam;
	}

}
