package com.vasciie.bkbl.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.vasciie.bkbl.GameMessageSender;
import com.vasciie.bkbl.MyGdxGame;
import com.vasciie.bkbl.gamespace.tools.SettingsPrefsIO;
import com.vasciie.bkbl.gui.Button;
import com.vasciie.bkbl.gui.CheckButton;
import com.vasciie.bkbl.gui.GUIRenderer;
import com.vasciie.bkbl.gui.Label;
import com.vasciie.bkbl.gui.NumUpDown;
import com.vasciie.bkbl.gui.UpDown;
import com.vasciie.bkbl.gui.UpDownListener;

public class SettingsScreen implements Screen, UpDownListener, GUIRenderer, GameMessageSender {

	MyGdxGame game;
	
	static ArrayList<String> res;
	
	
	SpriteBatch batch;
	ShapeRenderer shape;
	OrthographicCamera cam;
	BitmapFont font, font2;
	
	Button goBack;
	CheckButton beautifulGfx, fullscreen;
	CheckButton invX, invY;
	//CheckButton multithread;
	NumUpDown fpsUpDown;
	//TextUpDown resUpDown;
	Label fpsLabel, camRotLabel, mthLabel;
	
	Screen prevScreen;
	
	public static boolean invertX, invertY, multithreadOption;
	
	//boolean checkForFSCN; //Check for fullscreen
	
	public SettingsScreen(MyGdxGame mg) {
		game = mg;
		
		//getPossibleRes();
		
		cam = new OrthographicCamera();
		
		shape = new ShapeRenderer();
		batch = new SpriteBatch();
		font = new BitmapFont();
		font.getData().setScale(1);
		
		font2 = new BitmapFont();
		font2.getData().setScale(2);
		
		createGui();
	}
	
	/*private void getPossibleRes() {
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
	}*/
	
	private void createGui() {
		goBack = new Button("Go Back", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
		beautifulGfx = new CheckButton("Beautiful Background", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
		fullscreen = new CheckButton("Fullscreen", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
		invX = new CheckButton("Invert camera X", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
		invY = new CheckButton("Invert camera Y", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
		//multithread = new CheckButton("Multithreaded Update", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
		
		fpsUpDown = new NumUpDown(font, font2, Color.WHITE, Color.BROWN, new Color().set(0.8f, 0.4f, 0, 1), 20, 240, 10, this);
		//fpsUpDown.addException(0, "Inf.");
		fpsUpDown.setListener(this);
		//fpsUpDown.setOption(60);
		
		//resUpDown = new TextUpDown(font, font, Color.WHITE, Color.BROWN, new Color().set(0.8f, 0.4f, 0, 1), res, false);
		//resUpDown.setListener(this);
		
		fpsLabel = new Label("MAX. FPS", font, Color.BROWN, true, this);
		camRotLabel = new Label("WHETHER TO INVERT THE CAMERA ROTATION", font, Color.BROWN, true, this);
		mthLabel = new Label("INCREASES PERFORMANCE, BUT MIGHT CAUSE BOTTLENECK", font, Color.BROWN, true, this);
		//resLabel = new Label("THE RESOLUTION OF THE WINDOW", font, Color.BROWN, true);
		
		if(SettingsPrefsIO.readSettingBool("exists")) {
			beautifulGfx.setToggled(SettingsPrefsIO.readSettingBool("beautifulBack"));
			fullscreen.setToggled(SettingsPrefsIO.readSettingBool("fullscreen"));
			fpsUpDown.setOption(SettingsPrefsIO.readSettingInteger("fps"));
			//multithread.setToggled(multithreadOption = SettingsPrefsIO.readSettingBool("multithread"));
			invX.setToggled(invertX = SettingsPrefsIO.readSettingBool("invertX"));
			invY.setToggled(invertY = SettingsPrefsIO.readSettingBool("invertY"));
			
			game.setForegroundFps(fpsUpDown.getOption());
			if(fullscreen.isToggled())
				Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
		} else {
			beautifulGfx.setToggled(game.isBeautifulBack());
			fullscreen.setToggled(Gdx.graphics.isFullscreen());
			fpsUpDown.setOption(game.getForegroundFps());
			//multithread.setToggled(true);
			multithreadOption = true;
			
			SettingsPrefsIO.writeSettingBool("exists", true);
			writeSettings();
			SettingsPrefsIO.flush();
		}
	}
	
	@Override
	public void show() {
		if(!SettingsPrefsIO.readSettingBool("editablePanels")) {
			game.sendMessage("Note That The Menu Elements Like The One For The Max FPS Below (that contain number values) Can Also Be Edited By Keyboard! Just Click On Them, Type An Accessible By The Element Number And Click Enter, Escape Or Anywhere On The Screen To Confirm The Changes!", Color.RED, this, true);
			SettingsPrefsIO.writeSettingBool("editablePanels", true);
			SettingsPrefsIO.flush();
		}
		
	}

	@Override
	public void render(float delta) {
		cam.update();
		
		goBack.update();
		
		beautifulGfx.update();
		fullscreen.update();
		invX.update();
		invY.update();
		//multithread.update();
		
		fpsUpDown.update();
		
		fpsLabel.update();
		if (cam.viewportHeight >= 814) {
			camRotLabel.update();
			mthLabel.update();
		}
		
		//resUpDown.render(batch, shape, cam);
		//resLabel.render(batch, shape, cam);
		
		if(beautifulGfx.justTouched() && !game.isThereAMessage())
			game.setBeautifulBack(beautifulGfx.isToggled());
		else if((goBack.justReleased() || prevScreen.equals(game.game.getPauseScreen()) && Gdx.input.isKeyJustPressed(Keys.ESCAPE) && !fpsUpDown.getTextPanel().isActive()) && !game.isThereAMessage()) {
			//changeRes();
			
			if (fullscreen.isToggled()) {
				Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
			}else if(Gdx.graphics.isFullscreen()){
				Gdx.graphics.setWindowedMode(MyGdxGame.WIDTH, MyGdxGame.HEIGHT);
			}
			
			game.setScreen(prevScreen);
		}
		
		if(invX.justTouched() && !game.isThereAMessage())
			invertX = invX.isToggled();
		if(invY.justTouched() && !game.isThereAMessage())
			invertY = invY.isToggled();
		/*if(multithread.justTouched() && !game.isThereAMessage())
			multithreadOption = multithread.isToggled();*/

		game.renderLogo(batch, cam);

		goBack.draw();

		beautifulGfx.draw();
		fullscreen.draw();
		invX.draw();
		invY.draw();
		//multithread.draw();

		fpsUpDown.draw();
		fpsLabel.draw();
		camRotLabel.draw();
		mthLabel.draw();
	}
	
	private void writeSettings() {
		SettingsPrefsIO.writeSettingBool("beautifulBack", game.isBeautifulBack());
		SettingsPrefsIO.writeSettingBool("fullscreen", fullscreen.isToggled());
		//SettingsPrefsIO.writeSettingBool("multithread", multithread.isToggled());
		SettingsPrefsIO.writeSettingBool("invertX", invertX);
		SettingsPrefsIO.writeSettingBool("invertY", invertY);
		if(fpsUpDown.getOption() == 0 || fpsUpDown.getOption() > 19)
			SettingsPrefsIO.writeSettingInt("fps", fpsUpDown.getOption());
		SettingsPrefsIO.flush();
	}
	
	/*private void changeRes() {
		int[] ress = getResFromString(resUpDown.getTextOption());
		Gdx.graphics.setWindowedMode(ress[0], ress[1]);

		if (fullscreen.isToggled()) {
			game.setResolution(ress[0], ress[1]);
			Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
		}
	}*/
	
	/*private int[] getResFromString(String s) {
		int[] ress = new int[2];
		int temp = s.indexOf('x');
		
		String tempS1 = s.substring(0, temp);
		String tempS2 = s.substring(temp + 1);
		
		ress[0] = Integer.parseInt(tempS1);
		ress[1] = Integer.parseInt(tempS2);
		
		return ress;
	}*/

	@Override
	public void resize(int width, int height) {
		//System.out.println(height);
		cam.setToOrtho(false, width, height);
		
		goBack.setSize(game.pixelXByCurrentSize(223), game.pixelYByCurrentSize(30));
		goBack.setPos(width / 2 - goBack.getWidth() / 2, 60);
		
		//resUpDown.setSize(game.pixelXByCurrentSize(74), game.pixelYByCurrentSize(45));
		//resUpDown.setPos(width / 2 - resUpDown.getTotalWidth() / 2, height / 2 - resUpDown.getHeight() / 2);
		
		//multithread.setSize(40, 40);
		//multithread.setPos(width / 2 - multithread.getTotalWidth() / 2, game.pixelYByCurrentSize(425));
		
		fpsUpDown.setSize(game.pixelXByCurrentSize(74), game.pixelYByCurrentSize(45));
		//fpsUpDown.setPos(width / 2 - fpsUpDown.getTotalWidth() / 2, height / 2 - fpsUpDown.getHeight() / 2 - resUpDown.getHeight() * 3);
		fpsUpDown.setPos(width / 2 - fpsUpDown.getTotalWidth() / 2, height / 2 - fpsUpDown.getHeight() / 2);
		
		beautifulGfx.setPosAndSize(fpsUpDown.getX() + fpsUpDown.getTotalWidth() + 90, game.pixelYByCurrentSize(325), 40, 40);
		fullscreen.setSize(40, 40);
		fullscreen.setPos(fpsUpDown.getX() - 230, game.pixelYByCurrentSize(325));
		
		invX.setSize(40, 40);
		invX.setPos(width / 2 - invX.getTotalWidth() - 20, game.pixelYByCurrentSize(225));
		
		invY.setSize(40, 40);
		invY.setPos(width / 2 + invX.getWidth() + 20, invX.getY());
		
		camRotLabel.setPosAndSize(fpsUpDown.getX() + fpsUpDown.getTotalWidth() / 2 - 194 / 2, invY.getY() + invY.getHeight(), 194);
		
		//mthLabel.setPosAndSize(fpsUpDown.getX() + fpsUpDown.getTotalWidth() / 2 - 194 / 2, multithread.getY() + invY.getHeight(), 194);
		
		fpsLabel.setPosAndSize(fpsUpDown.getX() + fpsUpDown.getTotalWidth() / 2 - 164 / 2, fpsUpDown.getY() + fpsUpDown.getHeight() + 10, 164);
		//resLabel.setPosAndSize(resUpDown.getX() + resUpDown.getTotalWidth() / 2 - 164 / 2, resUpDown.getY() + resUpDown.getHeight() + 10, 164);
		
		//resUpDown.setTextOption(width + "x" + height);
	}
	
	public void setPreviousScreen(Screen prevScreen) {
		this.prevScreen = prevScreen;
	}

	@Override
	public void pause() {
		

	}

	@Override
	public void resume() {
		

	}

	@Override
	public void hide() {
		writeSettings();

	}

	@Override
	public void dispose() {
		if(game.getScreen().equals(this))
			writeSettings();
		
		batch.dispose();
		shape.dispose();
		font.dispose();
	}

	@Override
	public void onOptionChanged(UpDown upDown, int newValue) {
		if(fpsUpDown.equals(upDown))
			game.setForegroundFps(newValue);
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

	@Override
	public void messageReceived() {
		
	}

}
