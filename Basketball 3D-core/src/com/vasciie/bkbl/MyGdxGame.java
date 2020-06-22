package com.vasciie.bkbl;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Vector3;
import com.vasciie.bkbl.gamespace.GameMap;
import com.vasciie.bkbl.screens.*;

public class MyGdxGame extends Game {
	
	public static Color defaultColor = new Color(0, 0.7f, 0.8f, 1), currentColor;
	public static final boolean TESTING = false;
	
	SpriteBatch batch;
	BitmapFont font;
	OrthographicCamera cam;
	
	ModelBatch mBatch;
	Environment environment;
	PerspectiveCamera pCam;
	
	GameMap map;
	
	Texture background, logo;
	
	public SplashScreen1 spScreen1;
	public SplashScreen2 spScreen2;
	public MainScreen main;
	public LevelScreen level;
	public GameScreen game;
	public SettingsScreen settings;
	public TestScreen tester;
	
	public final static int WIDTH = 848;
	public final static int HEIGHT = 480;
	public static float GUI_SCALE;
	
	boolean beautifulBack = true;
	
	@Override
	public void create () {
		if (TESTING) {
			load3DGraphics();
			tester = new TestScreen(this, pCam);
			setScreen(tester);
			
			return;
		}
		
		if(Gdx.app.getType().equals(Application.ApplicationType.Android))
			GUI_SCALE = 1.5f;
		else GUI_SCALE = 1;

		batch = new SpriteBatch();
		
		font = new BitmapFont();
		font.getData().setScale(1);
		
		cam = new OrthographicCamera();
		
		logo = new Texture(Gdx.files.internal("application/bkbl_logo.png"));
		
		spScreen1 = new SplashScreen1(this);
		spScreen2 = new SplashScreen2(this);
		main = new MainScreen(this);		
		level = new LevelScreen(this);
		game = new GameScreen(this);
		
		
		

		if(!Gdx.app.getType().equals(Application.ApplicationType.Android))
			settings = new SettingsScreen(this);
		setScreen(spScreen1);
	}
	
	private void loadTexture() {
		background = new Texture(Gdx.files.internal("application/bkbl_background.png"));
	}
	
	public void load3DGraphics() {
		currentColor = defaultColor.cpy();
		
		mBatch = new ModelBatch();

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.6f, 0.6f, 0.6f, 0, -10, 0));
		environment.add(new PointLight().set(1, 1, 1, 0, 13, 0, 150));

		pCam = new PerspectiveCamera(40, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		pCam.position.set(0, 9, 25);
		pCam.far = 200;
		pCam.near = 0.1f;
		
		map = new GameMap(game, game);
	}
	
	public static void clearColor() {
		currentColor = defaultColor.cpy();
	}

	@Override
	public void render () {
		if(getScreen().equals(tester)) {
			pCam.update();
			Gdx.gl.glClearColor(currentColor.r, currentColor.g, currentColor.b, currentColor.a);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
			map.render(mBatch, environment, pCam);
			
			super.render();
			return;
		}
		
		if (!getScreen().equals(spScreen1) && !getScreen().equals(game) && !game.paused()) {
			if (beautifulBack) {
				if (map == null) {
					load3DGraphics();
					return;
				}
				
				if(getScreen().equals(spScreen2)) {
					super.render();
					return;
				}

				Gdx.gl.glClearColor(currentColor.r, currentColor.g, currentColor.b, currentColor.a);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

				pCam.update();
				//mBatch.begin(pCam);
				map.render(mBatch, environment, pCam);
				
				//mBatch.end();
				
				pCam.rotateAround(new Vector3(), new Vector3(0, 1, 0), 10 * Gdx.graphics.getDeltaTime());
				customLookAt(pCam, new Vector3());
			} else {
				if (background == null) {
					loadTexture();
					return;
				}
				
				if(getScreen().equals(spScreen2)) {
					super.render();
					return;
				}
				
				Gdx.gl.glClearColor(0, 0, 0, 1);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

				batch.setProjectionMatrix(cam.combined);
				batch.begin();
				batch.draw(background, cam.viewportWidth / 2 - background.getWidth() / 2, cam.viewportHeight / 2 - background.getHeight() / 2);
				batch.end();
			}
		}
		else if(game.paused()) game.render(Gdx.graphics.getDeltaTime());
		
		super.render();
		
		/*System.out.println();
		System.out.println(Gdx.graphics.getFramesPerSecond() + " fps");
		System.out.println();*/
		
		if (!getScreen().equals(spScreen1) && !getScreen().equals(spScreen2) && !game.paused()) {
			batch.setProjectionMatrix(cam.combined);
			batch.begin();
			font.draw(batch, Gdx.graphics.getFramesPerSecond() + " fps; SN:22/06/20-v1.0", 0, font.getLineHeight());
			batch.end();
		}
	}
	
	public static void setColor(Color color) {
		if(color == null)
			currentColor = defaultColor;
		else currentColor = color;
	}
	
	/**
	 * This method is being overridden inside the LwjglApplication's constructor, where we can modify the LwjglApplicationConfiguration's values from, something we cannot
	 * do in other way in runtime.
	 * @param fps
	 */
	public void setForegroundFps(int fps) {}
	
	public int getForegroundFps() {return 0;}
	
	public void setResolution(int width, int height) {}
	
	public void renderLogo(SpriteBatch batch, OrthographicCamera cam) {
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		batch.draw(logo, cam.viewportWidth / 2 - logo.getWidth() / 2, cam.viewportHeight - logo.getHeight() - 20);
		batch.end();
	}
	
	/**
	 * I needed a custom lookat method because the original one modifies the up vector of the camera which makes the camera to rotate the screen
	 * 
	 * <br><br>
	 * 
	 * By the way, setting the up vector manually would also fix the problem, but I think this method would do whatever has to be done much faster
	 * as it sets only the direction vector, as the original method set both the direction and the up vector and after that resetting the up vector
	 * to prevent from screen tilting.
	 * 
	 * @param trans - the translation of the object
	 */
	public void customLookAt(PerspectiveCamera pCam, Vector3 trans) {
		//Vector3 tmpVec = trans.cpy().sub(pCam.position).nor();
		//if (!tmpVec.isZero()) {
		pCam.direction.set(trans.cpy().sub(pCam.position).nor());
		//}
	}
	
	@Override
	public void resize(int width, int height) {
		if(!MyGdxGame.TESTING)
			cam.setToOrtho(false, width, height);
		
		if(pCam != null) {
			pCam.viewportWidth = width;
			pCam.viewportHeight = height;
		}
		
		getScreen().resize(width, height); //That method doesn't automatically call in the screen so we have to call it manually
		
		if(!MyGdxGame.TESTING)
			if (game.paused())
				game.resize(width, height);
	}
	
	@Override
	public void dispose () {
		if (!TESTING) {
			batch.dispose();
			logo.dispose();
		}
		
		if(map != null) {
			mBatch.dispose();
			map.dispose();
		}
		else if(background != null)
			background.dispose();
	}
	
	public GameMap getMap() {
		return map;
	}
	
	public Environment getEnvironment() {
		return environment;
	}
	
	public boolean isBeautifulBack() {
		return beautifulBack;
	}
	
	public void setBeautifulBack(boolean beautifulBack) {
		this.beautifulBack = beautifulBack;
	}

	public float pixelXByCurrentSize(float value) {
	
		return value * cam.viewportWidth / 1280;
	}
	
	public float pixelYByCurrentSize(float value) {
		
		return value * cam.viewportHeight / 720;
	}
}
