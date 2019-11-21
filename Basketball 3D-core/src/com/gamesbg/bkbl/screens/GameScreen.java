package com.gamesbg.bkbl.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.gamesbg.bkbl.MyGdxGame;
import com.gamesbg.bkbl.gamespace.GameMap;

public class GameScreen implements Screen {

	ModelBatch mBatch;
	Environment environment;
	PerspectiveCamera pCam;
	// CameraInputController camController;
	FirstPersonCameraController camController;

	GameMap map;

	MyGdxGame game;
	
	int amount; //Player amount per team

	public GameScreen(MyGdxGame mg) {
		game = mg;

		mBatch = new ModelBatch();

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.6f, 0.6f, 0.6f, 0, -10, 0));
		environment.add(new PointLight().set(1, 1, 1, 0, 9, -15, 40));
		environment.add(new PointLight().set(1, 1, 1, 0, 9, 15, 40));
		//environment.add(new PointLight().set(1, 1, 1, 0, 9, 0, 40));

		pCam = new PerspectiveCamera(40, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		pCam.position.set(3, 1, 0);
		//pCam.up.x = 0;
		//pCam.up.y = 0;
		//pCam.up.z = 0;
		//pCam.lookAt(new Vector3(0, 0.999f, 0));
		//pCam.lookAt(0, 0, 0);
		//pCam.rotate(-18f, 0, 0, 1);
		pCam.far = 100;
		pCam.near = 0.1f;
		
		camController = new FirstPersonCameraController(pCam);

		// camController = new CameraInputController(pCam);
		// System.out.println(Keys.toString(camController.translateButton));

		//map = game.getMap();
	}

	@Override
	public void show() {
		//The text panels use the setInputProcessor option so we need to set the controller as an Input Processor every time we start playing
		
		//Gdx.input.setInputProcessor(camController);
		if(map == null)
			game.load3DGraphics();
		
		map = game.getMap();
		
		map.spawnPlayers(amount);
		//map.setCameraTrans(pCam.combined);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0.7f, 0.8f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		//if(Gdx.input.isButtonPressed(Buttons.LEFT))
			//camController.update();
		
		map.update(delta);
		//posTheCam();
		map.getCamera().getMainTrans().getTranslation(pCam.position);
		//map.getCamera().getMainTrans().getRotation(new Quaternion()).transform(pCam.direction);
		game.customLookAt(pCam, new Matrix4(map.getMainPlayer().getModelInstance().transform).mul(new Matrix4().setToTranslation(0, map.getMainPlayer().getHeight(), 0)).getTranslation(new Vector3()));
		pCam.update();

		mBatch.begin(pCam);
		map.render(mBatch, environment);
		mBatch.end();
	}

	@Override
	public void resize(int width, int height) {
		pCam.viewportWidth = width;
		pCam.viewportHeight = height;
		//pCam.update();
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {
		map.clear();
	}

	@Override
	public void dispose() {
		mBatch.dispose();
	}
	
	public void setPlayersAmount(int amount) {
		this.amount = amount;
	}

}
