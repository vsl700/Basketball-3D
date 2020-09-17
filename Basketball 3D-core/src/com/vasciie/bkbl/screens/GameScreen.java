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
import com.vasciie.bkbl.GameMessageListener;
import com.vasciie.bkbl.GameMessageSender;
import com.vasciie.bkbl.gamespace.tools.VEThread;
import com.vasciie.bkbl.gui.GUI;
import com.vasciie.bkbl.gui.GUIBox;
import com.vasciie.bkbl.gui.GUIRenderer;
import com.vasciie.bkbl.gui.Label;

public class GameScreen implements Screen, GameMessageListener, GUIRenderer {

	GameMessageSender sender;
	
	VEThread updateThread;
	
	Runnable updateRunnable;
	
	ModelBatch mBatch;
	Environment environment;
	PerspectiveCamera pCam;
	
	SpriteBatch batch;
	ShapeRenderer shape;
	OrthographicCamera cam;
	BitmapFont textFont, powFont, font;

	GameMap map;

	MyGdxGame game;
	
	PauseScreen pause;
	
	Label homeScore, awayScore, minorMessage, power, powerNum, foulsAmount;
	Label messageHeading, messageDesc, clickToCont, playerRemove;
	Label[] challenges, currentChallenges;
	
	GUIBox messageBox;
	
	int amount; //Player amount per team
	
	float contTimer;//clickToCont timer
	
	//float messageBarWidth, messageBarHeight;
	
	boolean ignorePause;
	boolean skippableMessage, showPower;
	boolean minorMessageRec;

	public GameScreen(MyGdxGame mg) {
		game = mg;

		updateRunnable = new Runnable() {

			@Override
			public void run() {


				map.update(Gdx.graphics.getDeltaTime(), !paused());
				
				if(SettingsScreen.multithreadOption && !paused() && !game.getScreen().equals(game.game) && !game.getScreen().equals(game.settings) && !game.getScreen().equals(game.gameOver)) {
					reset();
				}

			}

		};

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
		
		font = new BitmapFont();
		font.getData().setScale(1.5f);
		
		pause = new PauseScreen(mg);
		
		homeScore = new Label("0", textFont, Color.BLUE, Color.CYAN, false, this);
		awayScore = new Label("0", textFont, Color.RED, Color.ORANGE, false, this);
		minorMessage = new Label("", textFont, Color.ORANGE, false, this);
		power = new Label("POWER", textFont, Color.RED, false, this);
		powerNum = new Label("x1", powFont, Color.WHITE, Color.RED, false, this);
		foulsAmount = new Label("", textFont, Color.BLACK, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), false, this);
		
		messageHeading = new Label("", textFont, true, this);
		messageDesc = new Label("", powFont, true, this);

		String message;
		if(Gdx.app.getType().equals(Application.ApplicationType.Android))
			message = "Tap Anywhere To Continue!";
		else message = "Click E To Continue!";
		clickToCont = new Label(message, powFont, Color.WHITE, true, this);
		
		playerRemove = new Label("Rule Triggerer Will Be Removed From The Game As He Just Got His 7th Foul!", powFont, Color.RED, true, this);
		
		messageBox = new GUIBox(this, new GUI[] {messageHeading, messageDesc, clickToCont, playerRemove}, 0);
	}

	@Override
	public void show() {
		ignorePause = false;
		
		if(map == null) {
			if(game.getMap() == null)
				game.load3DGraphics();//If the default menus setting is just to show a simple picture of the game instead of the game world
			
			map = game.getMap();
		}else {
			if(map.getTeammates().size() > 0) {
				ignorePause = true;
			}
			
			
		}
		
		if(challenges == null) {
			challenges = new Label[map.getChallenges().getSize()];
			
			for(int i = 0; i < challenges.length; i++) {
				challenges[i] = new Label("", font, Color.RED.cpy().sub(0.3f, 0, 0, 0), false, this);
			}
		}
		
		if (!ignorePause) {
			if (map.isChallenge()) {
				currentChallenges = new Label[map.getChallenges().getCurrentChallenges().length];
				for (int i = 0; i < currentChallenges.length; i++) {
					currentChallenges[i] = challenges[map.getChallenges().indexOf(map.getChallenges().getCurrentChallenges()[i])];
					currentChallenges[i].setText(map.getChallenges().getCurrentChallenges()[i].getName() + (map.getChallenges().getCurrentChallenges()[i].hasChallengeLevels() ? " (" + map.getChallenges().getCurrentChallenges()[i].getChallengeLevelsNames()[map.getChallenges().getCurrentChallenges()[i].getChallengeLevel()] + ")" : ""));
				}
			} else
				currentChallenges = new Label[0];
		}
		
		environment = game.getEnvironment();
		
		if (!ignorePause) {
			if (!map.getMultiplayer().isMultiplayer() && !map.isTutorialMode()) {
				map.setDifficulty(game.level.getDifficulty());

				if (!map.isChallenge() || !map.getChallenges().containsCurrentChallenge("alone"))
					map.spawnPlayers(amount, amount);
				else
					map.spawnPlayers(1, amount);
			} else if (map.getMultiplayer().isMultiplayer() && map.getMultiplayer().isServer()) {
				map.setDifficulty(0);
			}
			
			map.begin();
		}
		
		if(updateThread == null)
			updateThread = new VEThread(updateRunnable);
		
		Gdx.input.setInputProcessor(map.getInputs());
	}

	private void renderGUI(){
		//if(!paused() && (map.isRuleTriggered() || sender != null)) {
			/*shape.begin(ShapeRenderer.ShapeType.Filled);
			shape.setColor(Color.ORANGE.cpy().sub(0, 0.3f, 0, 1));
			
			float tempHeight;
			if(map.getDifficulty() > 0 && map.isRuleTriggered() && map.getRules().getTriggeredRule().getRuleTriggerer().getFouls() == 7)
				tempHeight = messageBarHeight + playerRemove.getRows() * 26;
			else tempHeight = messageBarHeight;
			
			shape.rect(cam.viewportWidth / 2 - messageBarWidth / 2, cam.viewportHeight - 20 - tempHeight, messageBarWidth, tempHeight);
			shape.end();*/
			
			messageBox.draw();
		//}
		
		homeScore.draw();
		awayScore.draw();
		foulsAmount.draw();
		
		for(Label lbl : currentChallenges)
			lbl.draw();

		power.draw();
		powerNum.draw();

		if(Gdx.app.getType().equals(Application.ApplicationType.Android))
			map.renderController();
		
		messageHeading.draw();
		messageDesc.draw();
		clickToCont.draw();
		playerRemove.draw();

		minorMessage.draw();
		minorMessageRec = false;
	}

	private void updateGUI(float delta){
		if(!map.isTutorialMode()) {
			homeScore.update();
			awayScore.update();
			
			if (!paused() && limitedFouls()) {
				foulsAmount.update();
				String temp = foulsAmount.getText();
				foulsAmount.setText("Commited Fouls: " + map.getMainPlayer().getFouls());

				if (!temp.equals(foulsAmount.getText()))
					resizeFoulsAmount();
			}
		}
		
		for(Label lbl : currentChallenges)
			lbl.update();

		if (!paused()) {
			boolean temp = map.isGameRunning() && sender == null;
			
			if (map.getMainPlayer().equals(map.getHoldingPlayer()) && (((temp || showPower) && map.isTutorialMode() && map.getCurrentTutorialLevel().getCurrentPart().showPower() || !map.isTutorialMode() && (showPower || temp)) && map.getTimer() <= 0) && !Gdx.app.getType().equals(Application.ApplicationType.Android)) {
				int pow = map.getMainPlayer().getShootingPower() - 9;

				power.update();

				powerNum.setText("x" + pow);
				powerNum.update();
				
				if(sender != null) {
					power.setY(getPowerY() - messageBox.getHeight() - 13 * (minorMessageRec ? 5 : 1));
					powerNum.setY(getPowerNumY());
				}
				else {
					power.setY(getPowerY());
					powerNum.setY(getPowerNumY());
				}
			} 
			if (!temp || minorMessageRec) {
				if (map.getTimer() > 0 && map.isPlayersReady() && !map.isTutorialMode() || minorMessageRec && map.isGameRunning()) {
					if (!minorMessageRec) {
						if ((int) map.getTimer() == 0)
							minorMessage.setText("GO!");
						else if (map.getTimer() <= 4)
							minorMessage.setText((int) map.getTimer() + "");
						else
							minorMessage.setText("Ready?");
					}
					
					minorMessage.update();
				}  

				if (sender != null/* || map.isRuleTriggered() || map.getChallenges().isAChallengeBroken()*/) { // If the game is not running and
											// there is no timer counting down
					messageBox.update();
					messageHeading.update();

					if (!messageDesc.getText().equals(""))
						messageDesc.update();

					if (map.getDifficulty() > 0 && map.isRuleTriggered() && map.getRules().getTriggeredRule().getRuleTriggerer().getFouls() == 7)
						playerRemove.update();

					if (contTimer <= 0 && skippableMessage) {
						clickToCont.update();

						if (Gdx.app.getType().equals(Application.ApplicationType.Android) && Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Keys.E)) {
							if (map.isRuleTriggered()) {
								Player triggerer = map.getRules().getTriggeredRule().getRuleTriggerer();
								if (triggerer.getFouls() == 7 && triggerer.isMainPlayer() || map.getTargetScore() > 0 && (map.getTeamScore() >= map.getTargetScore() || map.getOppScore() >= map.getTargetScore()) && !map.isChallenge()) {
									sender.messageReceived();
									game.setScreen(game.gameOver);
									return;
								}
							}

							if (map.getChallenges().isAChallengeBroken() && !map.isRuleTriggered()) {
								game.setScreen(game.gameOver);
								return;
							}

							String tempText = messageHeading.getText();
							sender.messageReceived();

							if (!map.isTutorialMode() && (map.getRules().getTriggeredRule() != null && (map.getTeammates().size() == 0 || map.getOpponents().size() == 0)))
								game.setScreen(game.gameOver);

							if (messageHeading.getText().equals(tempText))
								sender = null;
						}
					} else if (contTimer > 0)
						contTimer -= delta;
				}

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
		
		pCam.position.set(map.getCamera().getPosition());
		pCam.update();
		map.render(mBatch, environment, pCam);
		
		map.getMainPlayer().getFocusTransform().mul(tempMatrix.setToTranslation(0, map.getMainPlayer().getHeight(), -10)).getTranslation(pCam.position);
		game.customLookAt(pCam, tempMatrix.set(map.getMainPlayer().getModelInstance().transform).mul(tempMatrix2.setToTranslation(0, map.getMainPlayer().getHeight(), 0)).getTranslation(tempVec));
		map.getCamera().setPosition(pCam.position);
		map.getCamera().setDirection(pCam.direction);
		
		updateGUI(delta);
		
		renderGUI();
		
		if (!paused() || map.getMultiplayer().isMultiplayer()) {
			if(Gdx.app.getType().equals(Application.ApplicationType.Android))
				map.updateController();

			if(SettingsScreen.multithreadOption)
				updateThread.start();
			else updateRunnable.run();
		}
		map.updateCamera();
		
		
		if(paused() && game.getScreen().equals(this)) {
			pause.render(delta);
			return;
		}else if((Gdx.input.isKeyJustPressed(Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Keys.BACK)) && !ignorePause) {
			pause.show();
			recentlyPaused = true;
		}
		
		ignorePause = false;
		
		
		if(!SettingsScreen.multithreadOption && !paused() && !game.getScreen().equals(this) && !game.getScreen().equals(game.settings) && !game.getScreen().equals(game.gameOver)) {
			reset();
		}
	}

	@Override
	public void resize(int width, int height) {
		pause.resize(width, height);
		
		pCam.viewportWidth = width;
		pCam.viewportHeight = height;
		cam.setToOrtho(false, width, height);
		
		homeScore.setPosAndSize(0, height - 70, 70, 70);
		awayScore.setPosAndSize(width - 70, height - 70, 70, 70);
		minorMessage.setPosAndSize(width / 2, height - 140, 10);
		power.setPosAndSize(width / 2, getPowerY(), 10);
		powerNum.setSize(power.textSize(), 26);
		powerNum.setPos(width / 2 - powerNum.getWidth() / 2 + 5, getPowerNumY());
		
		for(int i = 0; i < currentChallenges.length; i++) {
			currentChallenges[i].setY(font.getLineHeight() * (i + 1));
			currentChallenges[i].setX(width - currentChallenges[i].textSize() / 2);
		}
		
		resizeMessageText(width, height);
		
		if(limitedFouls())
			resizeFoulsAmount();
		
		pCam.update();
	}
	
	private float getPowerY() {
		return cam.viewportHeight - 40;
	}
	
	private float getPowerNumY() {
		return power.getY() - 50;
	}
	
	private void resizeMessageText() {
		resizeMessageText((int) cam.viewportWidth, (int) cam.viewportHeight);
	}
	
	private void resizeMessageText(int width, int height) {
		float diff = 140;
		messageHeading.setPosAndSize(width / 2 - (width - diff) / 2, height - 80, width - diff, messageHeading.getRows() * textFont.getLineHeight());
		messageDesc.setPosAndSize(width / 2 - (width - diff) / 2, messageHeading.getY() - 30 * messageDesc.getRows(), width - diff);
		clickToCont.setPosAndSize(width / 2 - (width - diff) / 2, messageDesc.getY() - 40 * clickToCont.getRows(), width - diff);
		playerRemove.setPosAndSize(width / 2 - (width - diff) / 2, clickToCont.getY() - 30 * playerRemove.getRows(), width - diff);
		
		//messageBarWidth = width - diff;
		//messageBarHeight = ruleHeading.getRows() * 39 + 26.5f * (ruleDesc.getRows() + (skippableMessage ? clickToCont.getRows() : 0) + playerRemove.getRows()) + 13;
	}
	
	private void resizeFoulsAmount() {
		foulsAmount.setSize(foulsAmount.textSize() + 25, 50);
		foulsAmount.setPos(cam.viewportWidth / 2 - foulsAmount.getWidth() / 2, 0);
	}
	
	private boolean limitedFouls() {
		return !map.isTutorialMode() && map.getDifficulty() > 0;
	}
	
	public boolean paused() {
		return pause.isActive();
	}

	@Override
	public void pause() {
		//pause.show();
		//recentlyPaused = true;
	}
	
	public PauseScreen getPauseScreen() {
		return pause;
	}

	@Override
	public void resume() {
		Gdx.input.setCursorPosition((int) cam.viewportWidth / 2, (int) cam.viewportHeight / 2);
	}
	
	public void reset() {
		if(map != null)
			map.clear();
		
		homeScore.setText(0 + "");
		awayScore.setText(0 + "");
		
		sender = null;
		
		if (updateThread != null) {
			updateThread.waitToFinish();
			updateThread.interrupt();
			updateThread = null;
		}
		//map.dispose();
		//game.resetMap();
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void dispose() {
		mBatch.dispose();
		
		if(updateThread != null && !updateThread.getState().equals(State.NEW)) {
			updateThread.waitToFinish();
			updateThread.interrupt();
		}
		
		batch.dispose();
		shape.dispose();
		textFont.dispose();
		powFont.dispose();
		font.dispose();
	}
	
	public void setPlayersAmount(int amount) {
		this.amount = amount;
	}

	public int getAmount() {
		return amount;
	}

	@Override
	public void sendMessage(String heading, String desc, Color textColor, GameMessageSender sender, boolean skippable, boolean showPower) {
		if(heading.equals("/")) {
			if(desc.equals("main")) {
				game.setScreen(game.main);
				return;
			}
		}
		
		messageHeading.setText(heading);
		messageHeading.setColor(textColor);
		
		messageDesc.setText(desc);
		messageDesc.setColor(textColor);
		
		homeScore.setText(map.getTeamScore() + "");
		awayScore.setText(map.getOppScore() + "");
		
		contTimer = 1;
		
		this.sender = sender;
		skippableMessage = skippable;
		this.showPower = showPower;
		
		resizeMessageText();
		
		if(map.getMultiplayer().isServer())
			map.getMultiplayer().sendMessage();
		
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
	public void sendMinorMessage(String message) {
		minorMessage.setText(message);
		minorMessageRec = true;
	}

	@Override
	public void sendMessage(String heading, String description, Color textColor, GameMessageSender sender, boolean skippable) {
		
		
	}

	@Override
	public void sendMessage(String message, Color textColor, GameMessageSender sender, boolean skippable) {
		
		
	}

	@Override
	public String getMessageHeading() {
		
		return messageHeading.getText();
	}

	@Override
	public String getMessageDesc() {
		
		return messageDesc.getText();
	}

	@Override
	public Color getMessageColor() {
		return messageHeading.getColor();
	}
}
