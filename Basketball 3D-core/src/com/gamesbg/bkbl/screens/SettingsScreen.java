package com.gamesbg.bkbl.screens;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;

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
	
	static ArrayList<String> res;
	
	
	SpriteBatch batch;
	ShapeRenderer shape;
	OrthographicCamera cam;
	BitmapFont font;
	
	Button goBack;
	CheckButton beautifulGfx, fullscreen;
	TextUpDown resUpDown;
	
	public SettingsScreen(MyGdxGame mg) {
		game = mg;
		
		res = new ArrayList<String>();
		
		//Code for getting possible resolutions taken from StackOverflow.
		GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		    for (int i = 0; i < devices.length; i++) {
		        GraphicsDevice dev = devices[i];
		        System.out.println("device " + i);
		        DisplayMode[] modes = dev.getDisplayModes();
		        for (int j = 0; j < modes.length; j++) {
		            DisplayMode m = modes[j];
		            String s = m.getWidth() + "x" + m.getHeight();
		            
		            if(!res.contains(s))
		            	res.add(s);
		            
		            //System.out.println(" " + j + ": " + m.getWidth() + " x " + m.getHeight());
		            
		        }
		    }
		
		cam = new OrthographicCamera();
		
		shape = new ShapeRenderer();
		batch = new SpriteBatch();
		font = new BitmapFont();
		font.getData().setScale(1);
		
		createGui();
	}
	
	private void createGui() {
		goBack = new Button("Go Back", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true);
		beautifulGfx = new CheckButton("Beautiful Background", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true);
		fullscreen = new CheckButton("Fullscreen", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true);
		
		resUpDown = new TextUpDown(font, font, Color.WHITE, Color.BROWN, new Color().set(0.8f, 0.4f, 0, 1), res, false);
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
		
		resUpDown.render(batch, shape, cam);
		
		if(goBack.justReleased(cam))
			game.setScreen(game.main);
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		cam.setToOrtho(false, width, height);
		
		goBack.setSize(game.pixelXByCurrentSize(223), game.pixelYByCurrentSize(30));
		goBack.setPos(width / 2 - goBack.getWidth() / 2, 60);
		
		resUpDown.setSize(game.pixelXByCurrentSize(74), game.pixelYByCurrentSize(45));
		resUpDown.setPos(width / 2 - resUpDown.getTotalWidth() / 2, height / 2 - resUpDown.getHeight() / 2);
		
		beautifulGfx.setPosAndSize(resUpDown.getX() + resUpDown.getTotalWidth() + 90, game.pixelYByCurrentSize(325), 40, 40);
		fullscreen.setSize(40, 40);
		fullscreen.setPos(resUpDown.getX() - 230, game.pixelYByCurrentSize(325));
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
