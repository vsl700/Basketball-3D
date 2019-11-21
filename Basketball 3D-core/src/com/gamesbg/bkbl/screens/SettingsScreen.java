package com.gamesbg.bkbl.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.gamesbg.bkbl.MyGdxGame;
import com.gamesbg.bkbl.gui.Button;
import com.gamesbg.bkbl.gui.CheckButton;
import com.gamesbg.bkbl.gui.TextUpDown;

public class SettingsScreen implements Screen {

	MyGdxGame game;
	
	SpriteBatch batch;
	ShapeRenderer shape;
	OrthographicCamera cam;
	BitmapFont font;
	
	Button goBack;
	CheckButton beautifulGfx, fullscreen;
	TextUpDown resolutions;
	
	public SettingsScreen(MyGdxGame mg) {
		game = mg;
		
		cam = new OrthographicCamera();
		
		shape = new ShapeRenderer();
		batch = new SpriteBatch();
		font = new BitmapFont();
		font.getData().setScale(1);
		
		goBack = new Button("Go Back", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true);
		beautifulGfx = new CheckButton("Beautiful Background", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true);
		fullscreen = new CheckButton("Fullscreen", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true);
		
		resolutions = new TextUpDown(font, font, Color.WHITE, Color.BROWN, new Color().set(0.8f, 0.4f, 0, 1), new String[] {"800x600", "1024x768", "1280x720"}, false);
	}
	
	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		cam.update();
		
		goBack.render(batch, shape, cam);
		beautifulGfx.render(batch, shape, cam);
		fullscreen.render(batch, shape, cam);
		
		resolutions.render(batch, shape, cam);
		
		if(goBack.justReleased(cam))
			game.setScreen(game.main);
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		cam.setToOrtho(false, width, height);
		
		goBack.setSize(game.pixelXByCurrentSize(223), game.pixelYByCurrentSize(30));
		goBack.setPos(width / 2 - goBack.getWidth() / 2, 60);
		
		resolutions.setSize(game.pixelXByCurrentSize(74), game.pixelYByCurrentSize(45));
		resolutions.setPos(width / 2 - resolutions.getTotalWidth() / 2, height / 2 - resolutions.getHeight() / 2);
		
		beautifulGfx.setPosAndSize(resolutions.getX() + resolutions.getTotalWidth() + 90, game.pixelYByCurrentSize(325), 40, 40);
		fullscreen.setSize(40, 40);
		fullscreen.setPos(resolutions.getX() - 230, game.pixelYByCurrentSize(325));
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		batch.dispose();
		shape.dispose();
		font.dispose();
	}

}
