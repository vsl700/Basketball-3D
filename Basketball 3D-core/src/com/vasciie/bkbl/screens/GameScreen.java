package com.vasciie.bkbl.screens;

import java.lang.Thread.State;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
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
import com.vasciie.bkbl.gamespace.entities.Player;
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
	Label ruleHeading, ruleDesc, clickToCont, playerRemove;
	
	int amount; //Player amount per team
	
	float contTimer;//clickToCont timer
	
	float messageBarWidth, messageBarHeight;
	
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
		pCam.far = 200;
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
		
		homeScore = new Label("0", textFont, Color.BLUE, Color.CYAN, false, this);
		awayScore = new Label("0", textFont, Color.RED, Color.ORANGE, false, this);
		timer = new Label("", textFont, Color.ORANGE, false, this);
		power = new Label("POWER", textFont, Color.RED, false, this);
		powerNum = new Label("x1", powFont, Color.WHITE, Color.RED, false, this);
		
		ruleHeading = new Label("", textFont, true, this);
		ruleDesc = new Label("", powFont, true, this);

		String message;
		if(Gdx.app.getType().equals(Application.ApplicationType.Android))
			message = "Tap Anywhere To Continue!";
		else message = "Click E To Continue!";
		clickToCont = new Label(message, powFont, Color.WHITE, true, this);
		
		playerRemove = new Label("Rule Triggerer Will Be Removed From The Game As He Just Got His 7th Foul!", powFont, Color.RED, true, this);
	}

	@Override
	public void show() {
		if(map == null) {
			game.load3DGraphics();//If the default menus setting is just to show a simple picture of the game instead of the game world
			map = game.getMap();
		}else {
			if(map.getTeammates().size() > 0) {
				ignorePause = true;
				return;
			}
			
			
		}
		
		environment = game.getEnvironment();
		
		map.setDifficulty(game.level.getDifficulty());
		map.spawnPlayers(amount);
	}

	private void renderGUI(){
		if(!paused() && map.isRuleTriggered()) {
			shape.begin(ShapeRenderer.ShapeType.Filled);
			shape.setColor(Color.ORANGE.cpy().sub(0, 0.3f, 0, 1));
			
			float tempHeight;
			if(map.getDifficulty() > 0 && map.getRules().getTriggeredRule().getRuleTriggerer().getFouls() == 7)
				tempHeight = messageBarHeight + playerRemove.getRows() * 26;
			else tempHeight = messageBarHeight;
			
			shape.rect(cam.viewportWidth / 2 - messageBarWidth / 2, cam.viewportHeight - 20 - tempHeight, messageBarWidth, tempHeight);
			shape.end();
		}
		
		homeScore.draw();
		awayScore.draw();

		power.draw();
		powerNum.draw();

		if(Gdx.app.getType().equals(Application.ApplicationType.Android))
			map.renderController();
		
		ruleHeading.draw();
		ruleDesc.draw();
		clickToCont.draw();
		playerRemove.draw();

		timer.draw();
	}

	private void updateGUI(float delta){
		homeScore.update();
		awayScore.update();

		if (!paused())
			if (map.isGameRunning() && !Gdx.app.getType().equals(Application.ApplicationType.Android)) {
				int pow = map.getMainPlayer().getShootingPower() - 9;

				power.update();
				/*shape.begin(ShapeRenderer.ShapeType.Filled);
				shape.setColor(Color.RED);
				shape.rect(cam.viewportWidth / 2 - pow * 8 / 2, power.getY() - power.getHeight() / 2 - 30, pow * 8, 30);
				shape.end();*/

				powerNum.setText("x" + pow);
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
				} else if (map.isRuleTriggered()) { // If the game is not running and there is no timer counting down
					ruleHeading.update();
					ruleDesc.update();
					
					if(map.getDifficulty() > 0 && map.getRules().getTriggeredRule().getRuleTriggerer().getFouls() == 7)
						playerRemove.update();

					if (contTimer <= 0) {
						clickToCont.update();

						if (Gdx.app.getType().equals(Application.ApplicationType.Android) && Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Keys.E)) {
							Player triggerer = map.getRules().getTriggeredRule().getRuleTriggerer();
							if(triggerer.getFouls() == 7 && triggerer.isMainPlayer() || map.getTeamScore() == 15 || map.getOppScore() > 15) {
								game.setScreen(game.gameOver);
							}
							
							map.onRuleBrokenContinue();
						}
					} else
						contTimer -= delta;
				}

			}
	}

	private static final Matrix4 tempMatrix = new Matrix4();
	private static final Matrix4 tempMatrix2 = new Matrix4();
	private static final Vector3 tempVec = new Vector3();
	boolean recentlyPaused;
	@Override
	public void render(float delta) {
		if(recentlyPaused && !paused()) {//After the pause the delta is big, which causes troubles
			recentlyPaused = false;
			return;
		}

		Gdx.gl.glClearColor(MyGdxGame.currentColor.r, MyGdxGame.currentColor.g, MyGdxGame.currentColor.b, MyGdxGame.currentColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		cam.update();
		
		if(SettingsScreen.multithreadOption)
			updateThread.waitToFinish();

		map.getMainPlayer().getFocusTransform().mul(tempMatrix.setToTranslation(0, map.getMainPlayer().getHeight(), -10)).getTranslation(pCam.position);
		game.customLookAt(pCam, tempMatrix.set(map.getMainPlayer().getModelInstance().transform).mul(tempMatrix2.setToTranslation(0, map.getMainPlayer().getHeight(), 0)).getTranslation(tempVec));
		pCam.update();
		map.render(mBatch, environment, pCam);

		if (!paused()) {
			if(Gdx.app.getType().equals(Application.ApplicationType.Android))
				map.updateController();

			if(SettingsScreen.multithreadOption)
				updateThread.start();
			else updateRunnable.run();
		}

		updateGUI(delta);

		renderGUI();
		
		if(paused() && game.getScreen().equals(this)) {
			pause.render(delta);
			return;
		}else if((Gdx.input.isKeyJustPressed(Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Keys.BACK)) && !ignorePause) {
			pause.show();
			recentlyPaused = true;
		}
		
		ignorePause = false;
	}

	@Override
	public void resize(int width, int height) {
		pause.resize(width, height);
		
		pCam.viewportWidth = width;
		pCam.viewportHeight = height;
		cam.setToOrtho(false, width, height);
		
		homeScore.setPosAndSize(0, height - 70, 70, 70);
		awayScore.setPosAndSize(width - 70, height - 70, 70, 70);
		timer.setPosAndSize(width / 2, height - 120, 10);
		power.setPosAndSize(width / 2, height - 40, 10);
		powerNum.setSize(power.textSize(), 26);
		powerNum.setPos(width / 2 - powerNum.getWidth() / 2 + 5, power.getY() - 50);
		
		resizeMessageText(width, height);
		
		pCam.update();
	}
	
	private void resizeMessageText() {
		resizeMessageText((int) cam.viewportWidth, (int) cam.viewportHeight);
	}
	
	private void resizeMessageText(int width, int height) {
		float diff = 140;
		ruleHeading.setPosAndSize(width / 2 - (width - diff) / 2, height - 80, width - diff);
		ruleDesc.setPosAndSize(width / 2 - (width - diff) / 2, ruleHeading.getY() - 30 * ruleDesc.getRows(), width - diff);
		clickToCont.setPosAndSize(width / 2 - (width - diff) / 2, ruleDesc.getY() - 40 * clickToCont.getRows(), width - diff);
		playerRemove.setPosAndSize(width / 2 - (width - diff) / 2, clickToCont.getY() - 30 * playerRemove.getRows(), width - diff);
		
		messageBarWidth = width - diff;
		messageBarHeight = ruleHeading.getRows() * 39 + 26 * (ruleDesc.getRows() + clickToCont.getRows() + playerRemove.getRows()) + 13;
	}
	
	public boolean paused() {
		return pause.isActive();
	}

	@Override
	public void pause() {
		pause.show();
		recentlyPaused = true;
	}

	@Override
	public void resume() {
		
	}
	
	public void reset() {
		if(map != null)
			map.clear();
		
		homeScore.setText(0 + "");
		awayScore.setText(0 + "");
		
		//map.dispose();
		//game.resetMap();
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void dispose() {
		mBatch.dispose();
		
		if(!updateThread.getState().equals(State.NEW))
			updateThread.interrupt();
	}
	
	public void setPlayersAmount(int amount) {
		this.amount = amount;
	}

	public int getAmount() {
		return amount;
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
		
		resizeMessageText();
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
