package com.gamesbg.bkbl;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
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
import com.gamesbg.bkbl.gamespace.GameMap;
import com.gamesbg.bkbl.screens.*;

public class MyGdxGame extends Game {
	
	SpriteBatch batch;
	BitmapFont font;
	OrthographicCamera cam;
	
	ModelBatch mBatch;
	Environment environment;
	PerspectiveCamera pCam;
	
	GameMap map;
	
	Texture background, logo;
	
	public MainScreen main;
	public LevelScreen level;
	public GameScreen game;
	public SettingsScreen settings;
	
	public final static int WIDTH = 848;
	public final static int HEIGHT = 480;
	
	boolean beautifulBack = true;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		
		font = new BitmapFont();
		font.getData().setScale(1);
		
		cam = new OrthographicCamera();
		
		logo = new Texture(Gdx.files.internal("application/bkbl_logo.png"));
		
		if(beautifulBack)
			load3DGraphics();
		else loadTexture();
		
		main = new MainScreen(this);
		setScreen(main);
		
		level = new LevelScreen(this);
		game = new GameScreen(this);
		settings = new SettingsScreen(this);
		//setScreen(settings);
	}
	
	private void loadTexture() {
		background = new Texture(Gdx.files.internal("application/bkbl_background.png"));
	}
	
	public void load3DGraphics() {
		mBatch = new ModelBatch();

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.6f, 0.6f, 0.6f, 0, -10, 0));
		environment.add(new PointLight().set(1, 1, 1, 0, 9, -15, 40));
		environment.add(new PointLight().set(1, 1, 1, 0, 9, 15, 40));

		pCam = new PerspectiveCamera(40, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		pCam.position.set(0, 9, 25);
		pCam.far = 100;
		pCam.near = 0.1f;
		
		map = new GameMap();
	}

	@Override
	public void render () {
		if(!getScreen().equals(game)) {
			if(beautifulBack) {
				if(map == null)
					load3DGraphics();
				
				Gdx.gl.glClearColor(0, 0.7f, 0.8f, 1);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT|GL20.GL_DEPTH_BUFFER_BIT);
				
				pCam.update();
				mBatch.begin(pCam);
				map.render(mBatch, environment);
				mBatch.end();
				
				pCam.rotateAround(new Vector3(), new Vector3(0, 1, 0), 10 * Gdx.graphics.getDeltaTime());
				customLookAt(pCam, new Vector3());
			}
			else {
				Gdx.gl.glClearColor(0, 0, 0, 1);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT|GL20.GL_DEPTH_BUFFER_BIT);
				
				batch.setProjectionMatrix(cam.combined);
				batch.begin();
				if(!beautifulBack) {
					if(background == null) loadTexture();
					batch.draw(background, WIDTH / 2 - background.getWidth() / 2, HEIGHT / 2 - background.getHeight() / 2);
				}
				//System.out.println(WIDTH - background.getWidth());
				batch.end();
			}
		}else {
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT|GL20.GL_DEPTH_BUFFER_BIT);
		}
		
		super.render();
		
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		//if(!beautifulBack)
			//batch.draw(background, WIDTH / 2 - background.getWidth() / 2, HEIGHT / 2 - background.getHeight() / 2);
		font.draw(batch, Gdx.graphics.getFramesPerSecond() + " fps; ALPHA v0.1", 0, 13);
		batch.end();
	}
	
	/**
	 * This method is being overriden inside the LwjglApplication's constructor, where we can modify the LwjglApplicationConfiguration's values from, something we cannot
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
		cam.setToOrtho(false, width, height);
		if(pCam != null) {
			pCam.viewportWidth = width;
			pCam.viewportHeight = height;
		}
		
		getScreen().resize(width, height); //That method doesn't automatically call in the screen so we have to call it manually
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		logo.dispose();
		
		if(map != null) {
			mBatch.dispose();
			map.dispose();
		}
		else
			background.dispose();
	}
	
	public GameMap getMap() {
		return map;
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
