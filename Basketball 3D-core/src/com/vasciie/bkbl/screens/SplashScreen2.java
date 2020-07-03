package com.vasciie.bkbl.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.vasciie.bkbl.MyGdxGame;

public class SplashScreen2 extends SplashScreen1 {

	public SplashScreen2(MyGdxGame mg) {
		super(mg);
	}
	
	@Override
	public void show() {
		super.show();
		
		MyGdxGame.assets.finishLoading();
	}
	
	@Override
	protected void createTexture() {
		logo = new Texture(Gdx.files.internal("application/powered_by_libgdx.png"));
	}
	
	@Override
	protected void checkTime(float delta) {
		if(time <= 0)
			game.setScreen(game.main);
		else time-= delta;
	}

}
