package com.vasciie.bkbl.screens;

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
import com.vasciie.bkbl.MyGdxGame;
import com.vasciie.bkbl.gamespace.GameMap;
import com.vasciie.bkbl.gamespace.rules.Rules.GameRule;
import com.vasciie.bkbl.gamespace.rules.Rules.RulesListener;
import com.vasciie.bkbl.gui.Label;

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
	
	PauseScreen pause;
	
	Label homeScore, awayScore, timer, power, powerNum;
	Label ruleHeading, ruleDesc, clickToCont;
	
	int amount; //Player amount per team
	
	float contTimer;//clickToCont timer
	
	boolean ignorePause;

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
		
		pause = new PauseScreen(mg);
		
		homeScore = new Label("0", textFont, Color.BLUE, true);
		awayScore = new Label("0", textFont, Color.RED, true);
		timer = new Label("", textFont, Color.ORANGE, true);
		power = new Label("POWER", textFont, Color.RED, true);
		powerNum = new Label("10", powFont, Color.WHITE, true);
		
		ruleHeading = new Label("", textFont, true);
		ruleDesc = new Label("", powFont, true);
		clickToCont = new Label("Click E To Continue!", powFont, Color.WHITE, true);
	}

	@Override
	public void show() {
		if(map == null) {
			game.load3DGraphics();//If the default menus setting is just to show a simple picture of the game instead of the game world
			map = game.getMap();
		}
		
		if(map.getTeammates().size() > 0) {
			ignorePause = true;
			return;
		}
		
		map.spawnPlayers(amount);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0.7f, 0.8f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		if (!pause.isActive()) {
			map.update(delta);
			// map.getCamera().getMainTrans().getTranslation(pCam.position);
			map.getMainPlayer().getFocusTransform().mul(new Matrix4().setToTranslation(0, map.getMainPlayer().getHeight(), -10)).getTranslation(pCam.position);
			game.customLookAt(pCam, new Matrix4(map.getMainPlayer().getModelInstance().transform).mul(new Matrix4().setToTranslation(0, map.getMainPlayer().getHeight(), 0)).getTranslation(new Vector3()));
			pCam.update();
		}

		mBatch.begin(pCam);
		map.render(mBatch, environment);
		mBatch.end();
		
		cam.update();
		homeScore.render(batch, shape, cam);
		awayScore.render(batch, shape, cam);
		
		if (!paused())
			if (map.isGameRunning()) {
				int pow = map.getMainPlayer().getShootingPower();

				power.render(batch, shape, cam);
				shape.begin(ShapeRenderer.ShapeType.Filled);
				shape.setColor(Color.RED);
				shape.rect(cam.viewportWidth / 2 - pow * 8 / 2, power.getY() - power.getHeight() / 2 - 30, pow * 8, 30);
				shape.end();

				powerNum.setText(pow + "");
				powerNum.render(batch, shape, cam);
			} else {
				if (map.getTimer() >= 0 && map.isPlayersReady()) {
					if ((int) map.getTimer() == 0)
						timer.setText("GO!");
					else if (map.getTimer() <= 4)
						timer.setText((int) map.getTimer() + "");
					else
						timer.setText("Ready?");

					timer.render(batch, shape, cam);
				} else if (map.isRuleTriggered()) { // If the game is not
													// running and there is no
													// timer counting down
					ruleHeading.render(batch, shape, cam);
					ruleDesc.render(batch, shape, cam);

					if (contTimer <= 0) {
						clickToCont.render(batch, shape, cam);

						if (Gdx.input.isKeyJustPressed(Keys.E))
							map.onRuleBrokenContinue();
					} else
						contTimer -= delta;
				}

			}
		
		if(paused() && game.getScreen().equals(this)) {
			pause.render(delta);
			return;
		}else if(Gdx.input.isKeyJustPressed(Keys.ESCAPE) && !ignorePause)
			pause.show();
		
		ignorePause = false;
	}

	@Override
	public void resize(int width, int height) {
		pause.resize(width, height);
		
		pCam.viewportWidth = width;
		pCam.viewportHeight = height;
		cam.setToOrtho(false, width, height);
		
		homeScore.setPosAndSize(30, height - 70, 10);
		awayScore.setPosAndSize(width - 30, height - 70, 10);
		timer.setPosAndSize(width / 2, height - 120, 10);
		power.setPosAndSize(width / 2, height - 80, 10);
		powerNum.setPosAndSize(width / 2, height - 120, 10);
		ruleHeading.setPosAndSize(width / 2 - (width - 10) / 2, height - 80, width - 10);
		ruleDesc.setPosAndSize(width / 2 - (width - 10) / 2, height - 120, width - 10);
		clickToCont.setPosAndSize(width / 2 - (width - 10) / 2, height - 160, width - 10);
		pCam.update();
	}
	
	public boolean paused() {
		return pause.isActive();
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}
	
	public void reset() {
		map.clear();
		homeScore.setText(0 + "");
		awayScore.setText(0 + "");
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void dispose() {
		mBatch.dispose();
	}
	
	public void setPlayersAmount(int amount) {
		this.amount = amount;
	}

	@Override
	public void onRuleTriggered(GameRule rule) {
		Color textColor = rule.getTextColor();
		
		ruleHeading.setText(rule.getName());
		ruleHeading.setColor(textColor);
		
		ruleDesc.setText(rule.getDescription());
		ruleDesc.setColor(textColor);
		
		homeScore.setText(map.getTeamScore() + "");
		awayScore.setText(map.getOppScore() + "");
		
		contTimer = 1;
	}

}
