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

public class JoinOrCreateScreen implements Screen, GUIRenderer {

	MyGdxGame game;
	
	SpriteBatch batch;
	ShapeRenderer shape;
	OrthographicCamera cam;
	
	BitmapFont font;
	
	Button create, join, goBack;
	
	
	public JoinOrCreateScreen(MyGdxGame mg) {
		game = mg;
		
		cam = new OrthographicCamera();
		
		shape = new ShapeRenderer();
		batch = new SpriteBatch();
		
		font = new BitmapFont();
		font.getData().setScale(MyGdxGame.GUI_SCALE);
		
		create = new Button("Create", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
		join = new Button("Join", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
		
		goBack = new Button("Go Back", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
	}
	
	@Override
	public void show() {
		

	}

	@Override
	public void render(float delta) {
		create.update();
		join.update();
		goBack.update();
		
		if(goBack.justReleased())
			game.setScreen(game.playerGameType);
		else if(create.justReleased())
			game.setScreen(game.rulesChoose);
		else if(join.justReleased())
			game.setScreen(game.join);
		
		game.renderLogo(batch, cam);

		create.draw();
		join.draw();
		goBack.draw();

	}

	@Override
	public void resize(int width, int height) {
		cam.setToOrtho(false, width, height);
		
		float btnY = game.pixelYByCurrentSize(260);
		create.setSize(game.pixelXByCurrentSize(223 * MyGdxGame.GUI_SCALE), game.pixelYByCurrentSize(30 * MyGdxGame.GUI_SCALE));
		create.setPos(width / 4 - create.getWidth() / 2, btnY - 30);
		
		join.setSize(create.getWidth(), create.getHeight());
		join.setPos(width * 3 / 4 - create.getWidth() / 2, create.getY());
		
		goBack.setSize(create.getWidth(), create.getHeight());
		goBack.setPos(width / 2 - create.getWidth() / 2, btnY / 2);

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
