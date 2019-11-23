package com.gamesbg.bkbl.screens;

//import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.gamesbg.bkbl.MyGdxGame;
import com.gamesbg.bkbl.gui.Button;
import com.gamesbg.bkbl.gui.CheckButton;
import com.gamesbg.bkbl.gui.Label;
import com.gamesbg.bkbl.gui.NumUpDown;
import com.gamesbg.bkbl.gui.TextUpDown;
import com.gamesbg.bkbl.gui.UpDown;
import com.gamesbg.bkbl.gui.UpDownListener;

public class SettingsScreen implements Screen, UpDownListener {

	MyGdxGame game;
	
	static ArrayList<String> res;
	
	
	SpriteBatch batch;
	ShapeRenderer shape;
	OrthographicCamera cam;
	BitmapFont font, font2;
	
	Button goBack;
	CheckButton beautifulGfx, fullscreen;
	NumUpDown fpsUpDown;
	TextUpDown resUpDown;
	Label fpsLabel, resLabel;
	
	//boolean checkForFSCN; //Check for fullscreen
	
	public SettingsScreen(MyGdxGame mg) {
		game = mg;
		
		getPossibleRes();
		
		cam = new OrthographicCamera();
		
		shape = new ShapeRenderer();
		batch = new SpriteBatch();
		font = new BitmapFont();
		font.getData().setScale(1);
		
		font2 = new BitmapFont();
		font2.getData().setScale(2);
		
		createGui();
	}
	
	private void getPossibleRes() {
		res = new ArrayList<String>();
		
		//Code for getting possible resolutions taken from StackOverflow.
		GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		    for (int i = 0; i < devices.length; i++) {
		        GraphicsDevice dev = devices[i];
		        //System.out.println("device " + i);
		        java.awt.DisplayMode[] modes = dev.getDisplayModes();
		        for (int j = 0; j < modes.length; j++) {
		        	java.awt.DisplayMode m = modes[j];
		            String s = m.getWidth() + "x" + m.getHeight();
		            
		            if(!res.contains(s))
		            	res.add(s);
		            
		            //System.out.println(" " + j + ": " + m.getWidth() + " x " + m.getHeight());
		            
		        }
		    }
	}
	
	private void createGui() {
		goBack = new Button("Go Back", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true);
		beautifulGfx = new CheckButton("Beautiful Background", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true);
		fullscreen = new CheckButton("Fullscreen", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true);
		
		fpsUpDown = new NumUpDown(font, font2, Color.WHITE, Color.BROWN, new Color().set(0.8f, 0.4f, 0, 1), 0, 240, 10);
		fpsUpDown.addException(0, "Inf.");
		fpsUpDown.setListener(this);
		//fpsUpDown.setOption(60);
		
		resUpDown = new TextUpDown(font, font, Color.WHITE, Color.BROWN, new Color().set(0.8f, 0.4f, 0, 1), res, false);
		resUpDown.setListener(this);
		
		fpsLabel = new Label("THE MAXIMUM FRAMES PER SECOND", font, Color.BROWN, true);
		resLabel = new Label("THE RESOLUTION OF THE WINDOW", font, Color.BROWN, true);
		
		beautifulGfx.setToggled(game.isBeautifulBack());
		fullscreen.setToggled(Gdx.graphics.isFullscreen());
		fpsUpDown.setOption(game.getForegroundFps());
	}
	
	@Override
	public void show() {
		
		
	}

	@Override
	public void render(float delta) {
		cam.update();
		
		goBack.render(batch, shape, cam);
		
		beautifulGfx.render(batch, shape, cam);
		fullscreen.render(batch, shape, cam);
		
		fpsUpDown.render(batch, shape, cam);
		fpsLabel.render(batch, shape, cam);
		
		resUpDown.render(batch, shape, cam);
		resLabel.render(batch, shape, cam);
		
		if(beautifulGfx.justTouched(cam))
			game.setBeautifulBack(beautifulGfx.isToggled());
		else if(goBack.justReleased(cam)) {
			changeRes();
			
			game.setScreen(game.main);
		}
	}
	
	private void changeRes() {
		int[] ress = getResFromString(resUpDown.getTextOption());
		Gdx.graphics.setWindowedMode(ress[0], ress[1]);

		if (fullscreen.isToggled()) {
			game.setResolution(ress[0], ress[1]);
			Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
		}
	}
	
	private int[] getResFromString(String s) {
		int[] ress = new int[2];
		int temp = s.indexOf('x');
		
		String tempS1 = s.substring(0, temp);
		String tempS2 = s.substring(temp + 1);
		
		ress[0] = Integer.parseInt(tempS1);
		ress[1] = Integer.parseInt(tempS2);
		
		return ress;
	}

	@Override
	public void resize(int width, int height) {
		
		cam.setToOrtho(false, width, height);
		
		goBack.setSize(game.pixelXByCurrentSize(223), game.pixelYByCurrentSize(30));
		goBack.setPos(width / 2 - goBack.getWidth() / 2, 60);
		
		resUpDown.setSize(game.pixelXByCurrentSize(74), game.pixelYByCurrentSize(45));
		resUpDown.setPos(width / 2 - resUpDown.getTotalWidth() / 2, height / 2 - resUpDown.getHeight() / 2);
		
		fpsUpDown.setSize(game.pixelXByCurrentSize(74), game.pixelYByCurrentSize(45));
		fpsUpDown.setPos(width / 2 - fpsUpDown.getTotalWidth() / 2, height / 2 - fpsUpDown.getHeight() / 2 - resUpDown.getHeight() * 3);
		
		beautifulGfx.setPosAndSize(resUpDown.getX() + resUpDown.getTotalWidth() + 90, game.pixelYByCurrentSize(325), 40, 40);
		fullscreen.setSize(40, 40);
		fullscreen.setPos(resUpDown.getX() - 230, game.pixelYByCurrentSize(325));
		
		fpsLabel.setPosAndSize(fpsUpDown.getX() + fpsUpDown.getTotalWidth() / 2 - 164 / 2, fpsUpDown.getY() + fpsUpDown.getHeight() + 10, 164);
		resLabel.setPosAndSize(resUpDown.getX() + resUpDown.getTotalWidth() / 2 - 164 / 2, resUpDown.getY() + resUpDown.getHeight() + 10, 164);
		
		resUpDown.setTextOption(width + "x" + height);
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
	public void onOptionChanged(UpDown upDown, int newValue) {
		if(fpsUpDown.equals(upDown))
			game.setForegroundFps(newValue);
	}

}