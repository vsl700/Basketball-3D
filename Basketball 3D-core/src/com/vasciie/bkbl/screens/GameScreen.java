package com.vasciie.bkbl.screens;

import java.lang.Thread.State;

import com.badlogic.gdx.Application;
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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.vasciie.bkbl.MyGdxGame;
import com.vasciie.bkbl.gamespace.GameMap;
import com.vasciie.bkbl.gamespace.rules.Rules.GameRule;
import com.vasciie.bkbl.gamespace.rules.Rules.RulesListener;
import com.vasciie.bkbl.gamespace.tools.VEThread;
import com.vasciie.bkbl.gui.GUIRenderer;
import com.vasciie.bkbl.gui.Label;

public class GameScreen implements Screen, RulesListener, GUIRenderer {

	VEThread updateThread;
	
	Runnable updateRunnable;
	
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

		updateRunnable = new Runnable() {

			@Override
			public void run() {


				map.update(Gdx.graphics.getDeltaTime());

			}

		};
		
		updateThread = new VEThread(updateRunnable);

		mBatch = new ModelBatch();

		/*environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.6f, 0.6f, 0.6f, 0, -10, 0));
		environment.add(new PointLight().set(1, 1, 1, 0, 9, -15, 40));
		environment.add(new PointLight().set(1, 1, 1, 0, 9, 15, 40));*/

		pCam = new PerspectiveCamera(40, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		pCam.position.set(3, 1, 0);
		pCam.far = 100;
		pCam.near = 0.1f;
		
		cam = new OrthographicCamera();
		
		shape = new ShapeRenderer();
		shape.setAutoShapeType(true);
		batch = new SpriteBatch();
		
		textFont = new BitmapFont();
		textFont.getData().setScale(3);
		
		powFont = new BitmapFont();
		powFont.getData().setScale(2);
		
		pause = new PauseScreen(mg);
		
		homeScore = new Label("0", textFont, Color.BLUE, true, this);
		awayScore = new Label("0", textFont, Color.RED, true, this);
		timer = new Label("", textFont, Color.ORANGE, true, this);
		power = new Label("POWER", textFont, Color.RED, true, this);
		powerNum = new Label("10", powFont, Color.WHITE, true, this);
		
		ruleHeading = new Label("", textFont, true, this);
		ruleDesc = new Label("", powFont, true, this);

		String message;
		if(Gdx.app.getType().equals(Application.ApplicationType.Android))
			message = "Tap Anywhere To Continue!";
		else message = "Click E To Continue!";
		clickToCont = new Label(message, powFont, Color.WHITE, true, this);
	}

	@Override
	public void show() {
		if(map == null) {
			game.load3DGraphics();//If the default menus setting is just to show a simple picture of the game instead of the game world
			
		}else {
			if(map.getTeammates().size() > 0) {
				ignorePause = true;
				return;
			}
			
			
		}
		
		map = game.getMap();
		environment = game.getEnvironment();
		
		map.setDifficulty(game.level.getDifficulty());
		map.spawnPlayers(amount);
		
		Gdx.app.postRunnable(updateRunnable);
	}

	private void renderGUI(){
		homeScore.draw();
		awayScore.draw();

		power.draw();
		powerNum.draw();

		if(Gdx.app.getType().equals(Application.ApplicationType.Android))
			map.renderController();

		ruleHeading.draw();
		ruleDesc.draw();
		clickToCont.draw();

		timer.draw();
	}

	private void updateGUI(float delta){
		homeScore.update();
		awayScore.update();

		if (!paused())
			if (map.isGameRunning() && !Gdx.app.getType().equals(Application.ApplicationType.Android)) {
				int pow = map.getMainPlayer().getShootingPower();

				power.update();
				shape.begin(ShapeRenderer.ShapeType.Filled);
				shape.setColor(Color.RED);
				shape.rect(cam.viewportWidth / 2 - pow * 8 / 2, power.getY() - power.getHeight() / 2 - 30, pow * 8, 30);
				shape.end();

				powerNum.setText(pow + "");
				powerNum.update();
			} else if (!map.isGameRunning()) {
				if (map.getTimer() >= 0 && map.isPlayersReady()) {
					if ((int) map.getTimer() == 0)
						timer.setText("GO!");
					else if (map.getTimer() <= 4)
						timer.setText((int) map.getTimer() + "");
					else
						timer.setText("Ready?");

					timer.update();
				} else if (map.isRuleTriggered()) { // If the game is not
					// running and there is no
					// timer counting down
					ruleHeading.update();
					ruleDesc.update();

					if (contTimer <= 0) {
						clickToCont.update();

						if (Gdx.app.getType().equals(Application.ApplicationType.Android)) {
							if (Gdx.input.justTouched())
								map.onRuleBrokenContinue();
						} else if (Gdx.input.isKeyJustPressed(Keys.E))
							map.onRuleBrokenContinue();
					} else
						contTimer -= delta;
				}

			}
	}

	private static final Matrix4 tempMatrix = new Matrix4();
	private static final Matrix4 tempMatrix2 = new Matrix4();
	private static final Vector3 tempVec = new Vector3();
	@Override
	public void render(final float delta) {
		Gdx.gl.glClearColor(0, 0.7f, 0.8f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		cam.update();
		
		while(!updateThread.getState().equals(State.NEW) && !updateThread.getState().equals(State.WAITING));

		map.getMainPlayer().getFocusTransform().mul(tempMatrix.setToTranslation(0, map.getMainPlayer().getHeight(), -10)).getTranslation(pCam.position);
		game.customLookAt(pCam, tempMatrix.set(map.getMainPlayer().getModelInstance().transform).mul(tempMatrix2.setToTranslation(0, map.getMainPlayer().getHeight(), 0)).getTranslation(tempVec));
		pCam.update();
		map.render(mBatch, environment, pCam);

		if (!paused()) {
			if(Gdx.app.getType().equals(Application.ApplicationType.Android))
				map.updateController();


			updateThread.start();
		}

		updateGUI(delta);

		renderGUI();
		
		if(paused() && game.getScreen().equals(this)) {
			pause.render(delta);
			return;
		}else if((Gdx.input.isKeyJustPressed(Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Keys.BACK)) && !ignorePause)
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
		pause.show();
	}

	@Override
	public void resume() {
		
	}
	
	public void reset() {
		if(map != null)
			map.clear();
		
		homeScore.setText(0 + "");
		awayScore.setText(0 + "");
		
		map.dispose();
		game.resetMap();
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void dispose() {
		mBatch.dispose();
		
		updateThread.interrupt();
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
}
