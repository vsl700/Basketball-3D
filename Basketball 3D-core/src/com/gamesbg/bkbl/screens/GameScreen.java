package com.gamesbg.bkbl.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.gamesbg.bkbl.MyGdxGame;
import com.gamesbg.bkbl.gamespace.GameMap;
import com.gamesbg.bkbl.gamespace.rules.Rules.GameRule;
import com.gamesbg.bkbl.gamespace.rules.Rules.RulesListener;
import com.gamesbg.bkbl.gui.Label;

public class GameScreen implements Screen, RulesListener {

	ModelBatch mBatch;
	Environment environment;
	PerspectiveCamera pCam;
	
	SpriteBatch batch;
	ShapeRenderer shape;
	OrthographicCamera cam;
	BitmapFont textFont, powFont;

	GameMap map;

	MyGdxGame game;
	
	Label homeScore, awayScore, timer, power, powerNum;
	Label ruleHeading, ruleDesc, clickToCont;
	
	int amount; //Player amount per team

	public GameScreen(MyGdxGame mg) {
		game = mg;

		mBatch = new ModelBatch();

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.6f, 0.6f, 0.6f, 0, -10, 0));
		environment.add(new PointLight().set(1, 1, 1, 0, 9, -15, 40));
		environment.add(new PointLight().set(1, 1, 1, 0, 9, 15, 40));

		pCam = new PerspectiveCamera(40, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		pCam.position.set(3, 1, 0);
		pCam.far = 100;
		pCam.near = 0.1f;
		
		cam = new OrthographicCamera();
		
		shape = new ShapeRenderer();
		batch = new SpriteBatch();
		
		textFont = new BitmapFont();
		textFont.getData().setScale(3);
		
		powFont = new BitmapFont();
		powFont.getData().setScale(2);
		
		homeScore = new Label("0", textFont, Color.BLUE, true);
		awayScore = new Label("0", textFont, Color.RED, true);
		timer = new Label("Ready?", textFont, Color.ORANGE, true);
		power = new Label("POWER", textFont, Color.RED, true);
		powerNum = new Label("10", powFont, Color.WHITE, true);
		
		ruleHeading = new Label("", textFont, Color.RED, true);
		ruleDesc = new Label("", powFont, Color.RED, true);
		clickToCont = new Label("Click E To Continue!", powFont, Color.WHITE, true);
	}

	@Override
	public void show() {
		if(map == null)
			game.load3DGraphics();//If the default menus setting is just to show a simple picture of the game instead of the game world
		
		map = game.getMap();
		
		map.spawnPlayers(amount);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0.7f, 0.8f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		map.update(delta);
		map.getCamera().getMainTrans().getTranslation(pCam.position);
		game.customLookAt(pCam, new Matrix4(map.getMainPlayer().getModelInstance().transform).mul(new Matrix4().setToTranslation(0, map.getMainPlayer().getHeight(), 0)).getTranslation(new Vector3()));
		pCam.update();

		mBatch.begin(pCam);
		map.render(mBatch, environment);
		mBatch.end();
		
		cam.update();
		homeScore.render(batch, shape, cam);
		awayScore.render(batch, shape, cam);
		
		
		if (map.getTimer() >= 0) {
			if((int) map.getTimer() == 0)
				timer.setText("GO!");
			else if (map.getTimer() <= 4)
				timer.setText((int) map.getTimer() + "");
			
			timer.render(batch, shape, cam);
		}
		else {
			int pow = map.getMainPlayer().getShootingPower();
			
			power.render(batch, shape, cam);
			shape.begin(ShapeRenderer.ShapeType.Filled);
			shape.setColor(Color.RED);
			shape.rect(cam.viewportWidth / 2 - pow * 8 / 2, power.getY() - power.getHeight() / 2 - 30, pow * 8, 30);
			shape.end();
			
			powerNum.setText(pow + "");
			powerNum.render(batch, shape, cam);
		}
		
		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE))
			game.setScreen(game.main);
	}

	@Override
	public void resize(int width, int height) {
		pCam.viewportWidth = width;
		pCam.viewportHeight = height;
		cam.setToOrtho(false, width, height);
		
		homeScore.setPosAndSize(30, height - 70, 10);
		awayScore.setPosAndSize(width - 30, height - 70, 10);
		timer.setPosAndSize(width / 2, height - 120, 10);
		power.setPosAndSize(width / 2, height - 80, 10);
		powerNum.setPosAndSize(width / 2, height - 120, 10);
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

	@Override
	public void onRuleBroken(GameRule rule) {
		
	}

}
