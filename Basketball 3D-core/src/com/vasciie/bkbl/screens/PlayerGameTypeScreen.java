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

public class PlayerGameTypeScreen implements Screen, GUIRenderer {

	MyGdxGame game;
	
	SpriteBatch batch;
	ShapeRenderer shape;
	OrthographicCamera cam;
	
	BitmapFont font;
	
	Button single, multi, goBack;
	
	
	public PlayerGameTypeScreen(MyGdxGame mg) {
		game = mg;
		
		cam = new OrthographicCamera();
		
		shape = new ShapeRenderer();
		batch = new SpriteBatch();
		
		font = new BitmapFont();
		font.getData().setScale(MyGdxGame.GUI_SCALE);
		
		single = new Button("Singleplayer", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
		multi = new Button("Multiplayer", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
		
		goBack = new Button("Go Back", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
	}
	
	@Override
	public void show() {
		

	}

	@Override
	public void render(float delta) {
		single.update();
		multi.update();
		goBack.update();
		
		if(goBack.justReleased())
			game.setScreen(game.main);
		else if(single.justReleased())
			game.setScreen(game.gameType);
		/*else if(multi.justReleased())
			game.setScreen(game);*/
		
		game.renderLogo(batch, cam);

		single.draw();
		multi.draw();
		goBack.draw();
		
	}

	@Override
	public void resize(int width, int height) {
		cam.setToOrtho(false, width, height);
		
		single.setSize(game.pixelXByCurrentSize(223 * MyGdxGame.GUI_SCALE), game.pixelYByCurrentSize(30 * MyGdxGame.GUI_SCALE));
		single.setPos(width / 4 - single.getWidth() / 2, game.pixelYByCurrentSize(260));
		
		multi.setSize(single.getWidth(), single.getHeight());
		multi.setPos(width * 3 / 4 - single.getWidth() / 2, single.getY());
		
		goBack.setSize(single.getWidth(), single.getHeight());
		goBack.setPos(width / 2 - single.getWidth() / 2, single.getY() / 2);

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
