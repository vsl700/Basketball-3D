package com.vasciie.bkbl.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.vasciie.bkbl.GameMessageSender;
import com.vasciie.bkbl.MyGdxGame;
import com.vasciie.bkbl.gamespace.tools.SettingsPrefsIO;
import com.vasciie.bkbl.gui.Button;
import com.vasciie.bkbl.gui.GUIRenderer;
import com.vasciie.bkbl.gui.ImageButton;

public class MainScreen implements Screen, GUIRenderer, GameMessageSender {
	
	MyGdxGame game;
	
	SpriteBatch batch;
	ShapeRenderer shape;
	OrthographicCamera cam;
	BitmapFont font;
	
	Button play, settings, quit;
	ImageButton /*facebook, */twitter, itchio;
	
	public MainScreen(MyGdxGame mg) {
		game = mg;
		
		cam = new OrthographicCamera();
		
		shape = new ShapeRenderer();
		batch = new SpriteBatch();
		font = new BitmapFont();
		font.getData().setScale(MyGdxGame.GUI_SCALE);
		
		play = new Button("Play", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
		if(!Gdx.app.getType().equals(Application.ApplicationType.Android))
			settings = new Button("Settings", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
		quit = new Button("Quit", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
		
		//facebook = new ImageButton(this, new Texture(Gdx.files.internal("application/facebook_logo.png")));
		twitter = new ImageButton(this, new Texture(Gdx.files.internal("application/twitter_logo.png")));
		itchio = new ImageButton(this, new Texture(Gdx.files.internal("application/itch.io.jpg")));
	}

	@Override
	public void show() {
		if(!SettingsPrefsIO.readSettingBool("welcome")) {
			game.sendMessage("Hello And Welcome To Basketball-3D! There Are Some Tutorial Levels In 'Play>Singleplayer>Tutorial Levels', Which I Highly Recommend Going Through First! They Are Not Harder Than The Game Itself, But They Will Make You Get Attatched To The Game Much Easier! Try Them Out!", Color.RED, this, true);
			SettingsPrefsIO.writeSettingBool("welcome", true);
			SettingsPrefsIO.flush();
		}
	}

	@Override
	public void render(float delta) {
		//batch.setProjectionMatrix(cam.combined);
		//batch.begin();
		cam.update();
		
		play.update();
		if(!Gdx.app.getType().equals(Application.ApplicationType.Android))
			settings.update();
		quit.update();
		
		//facebook.update();
		twitter.update();
		itchio.update();
		
		if(quit.justReleased() && !game.isThereAMessage())
			Gdx.app.exit();
		else if(!Gdx.app.getType().equals(Application.ApplicationType.Android) && settings.justReleased() && !game.isThereAMessage()) {
			game.settings.setPreviousScreen(this);
			game.setScreen(game.settings);
		}
		else if(play.justReleased() && !game.isThereAMessage())
			game.setScreen(game.playerGameType);
		/*else if(facebook.justReleased() && !game.isThereAMessage())
			Gdx.net.openURI("");*/
		else if(twitter.justReleased() && !game.isThereAMessage())
			Gdx.net.openURI("https://twitter.com/VasciiE");
		else if(itchio.justReleased() && !game.isThereAMessage())
			Gdx.net.openURI("https://vascii-entertainment.itch.io/");

		game.renderLogo(batch, cam);

		play.draw();
		if(settings != null)
			settings.draw();

		quit.draw();
		
		//facebook.draw();
		twitter.draw();
		itchio.draw();
		//batch.end();
	}
	
	public void sendWebPageMessage() {
		if(!SettingsPrefsIO.readSettingBool("webpage")) {
			game.sendMessage("So How's The Game Performing? If You Like It You Can Follow Us On itch.io And Twitter To Hear About Updates And Incoming Features! The Buttons In The Bottom-Right Will Lead You To Our Pages!", Color.RED, this, true);
			SettingsPrefsIO.writeSettingBool("webpage", true);
			SettingsPrefsIO.flush();
		}
	}

	@Override
	public void resize(int width, int height) {
		cam.setToOrtho(false, width, height);
		
		//font.getData().setScale(width * height / 921600);

		play.setSize(game.pixelXByCurrentSize(223 * MyGdxGame.GUI_SCALE), game.pixelYByCurrentSize(30 * MyGdxGame.GUI_SCALE));
		play.setPos(width / 4 - play.getWidth() / 2, game.pixelYByCurrentSize(240 / MyGdxGame.GUI_SCALE));
		
		if(!Gdx.app.getType().equals(Application.ApplicationType.Android)) {
			settings.setSize(game.pixelXByCurrentSize(223), game.pixelYByCurrentSize(30));
			settings.setPos(width / 2 - settings.getWidth() / 2, play.getY());
		}
		
		quit.setSize(game.pixelXByCurrentSize(223 * MyGdxGame.GUI_SCALE), game.pixelYByCurrentSize(30 * MyGdxGame.GUI_SCALE));
		quit.setPos(width * 3 / 4 - play.getWidth() / 2, play.getY());
		
		twitter.setPos(width - twitter.getWidth(), 0);
		//facebook.setPos(width - facebook.getWidth() - twitter.getWidth(), 0);
		itchio.setPos(width - itchio.getWidth() - /*facebook.getWidth() - */twitter.getWidth(), 0);
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
		twitter.disposeImage();
		itchio.disposeImage();
		//facebook.disposeImage();
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
