package com.vasciie.bkbl.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.vasciie.bkbl.MyGdxGame;

/**
 * This is just a screen which will help with models building. Don't delete it, just take it out
 * of the game's classes if you really want to take it out!
 * 
 * @author studi
 *
 */

public class TestScreen implements Screen {
	
	FirstPersonCameraController camController;
	
	MyGdxGame game;
	
	
	public TestScreen(MyGdxGame mg, PerspectiveCamera pCam) {
		game = mg;
		
		camController = new FirstPersonCameraController(pCam);
		Gdx.input.setInputProcessor(camController);
	}

	@Override
	public void show() {
		game.getPCam().position.set(-0.1522632f,5.0042458f,13.894746f);
		game.getPCam().direction.set(0.008421174f,-0.25880882f,-0.9658913f);
	}

	@Override
	public void render(float delta) {
		camController.update(delta);

		
		System.out.println(game.getPCam().position);
		System.out.println(game.getPCam().direction);
	}

	@Override
	public void resize(int width, int height) {
		

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
		

	}

}
