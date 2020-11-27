package com.vasciie.bkbl;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.vasciie.bkbl.gamespace.GameMap;
import com.vasciie.bkbl.gamespace.levels.Challenges.ChallengeLevel;
import com.vasciie.bkbl.gui.Button;
import com.vasciie.bkbl.gui.GUI;
import com.vasciie.bkbl.gui.GUIBox;
import com.vasciie.bkbl.gui.GUIRenderer;
import com.vasciie.bkbl.gui.Label;
import com.vasciie.bkbl.screens.*;

public class MyGdxGame extends Game implements GameMessageListener, GUIRenderer {
	
	public static Color defaultColor = new Color(0, 0.7f, 0.8f, 1), currentColor;
	public static final boolean TESTING = true;
	
	Matrix4 spinMx;
	
	ShapeRenderer shape;
	SpriteBatch batch;
	BitmapFont font, textFont, btnFont;
	OrthographicCamera cam;
	
	ModelBatch mBatch;
	Environment environment;
	PerspectiveCamera pCam;
	
	GameMap map;
	
	Texture background, logo;
	
	public SplashScreen1 spScreen1;
	public SplashScreen2 spScreen2;
	public MainScreen main;
	public LevelScreen level;
	public TutorialLevelScreen tutorial;
	public ChallengeLevelScreen challenge;
	public PlayerGameTypeScreen playerGameType;
	public RulesChooseScreen rulesChoose;
	public JoinOrCreateScreen joinOrCreate;
	public JoinScreen join;
	public GameTypeScreen gameType;
	public GameScreen game;
	public GameOverScreen gameOver;
	public SettingsScreen settings;
	public TestScreen tester;
	
	public static AssetManager assets;
	
	public final static int WIDTH = 848;
	public final static int HEIGHT = 480;
	public static float GUI_SCALE;
	
	boolean beautifulBack = true;
	
	GameMessageSender sender;
	
	GUIBox messageBox;
	Label messageLabel;
	Button messageCont;
	String[] messageArgs;
	float messageTime;
	boolean skippable;
	
	
	@Override
	public void create () {
		assets = new AssetManager();
		assets.load("game/basketframe.obj", Model.class);
		
		if (TESTING) {
			assets.finishLoading();
			
			load3DGraphics();
			tester = new TestScreen(this, pCam);
			setScreen(tester);
			
			return;
		}
		
		if(Gdx.app.getType().equals(Application.ApplicationType.Android))
			GUI_SCALE = 1.5f;
		else GUI_SCALE = 1;

		if (!TESTING) {
			spinMx = new Matrix4();
			
			font = new BitmapFont();
			font.getData().setScale(1);
			
			textFont = new BitmapFont();
			textFont.getData().setScale(2);
			
			btnFont = new BitmapFont();
			btnFont.getData().setScale(GUI_SCALE);
			
			messageLabel = new Label("", textFont, true, this);
			
			messageCont = new Button("OK!", btnFont, Color.RED, true, true, this);
			
			batch = new SpriteBatch();
			shape = new ShapeRenderer();
			
			
			messageBox = new GUIBox(this, new GUI[] {messageLabel, messageCont}, 20);
		}
		
		cam = new OrthographicCamera();
		
		logo = new Texture(Gdx.files.internal("application/bkbl_logo.png"));
		
		spScreen1 = new SplashScreen1(this);
		spScreen2 = new SplashScreen2(this);
		main = new MainScreen(this);		
		level = new LevelScreen(this);
		tutorial = new TutorialLevelScreen(this);
		challenge = new ChallengeLevelScreen(this);
		gameType = new GameTypeScreen(this);
		playerGameType = new PlayerGameTypeScreen(this);
		rulesChoose = new RulesChooseScreen(this);
		joinOrCreate = new JoinOrCreateScreen(this);
		join = new JoinScreen(this);
		game = new GameScreen(this);
		gameOver = new GameOverScreen(this);
		

		if(!Gdx.app.getType().equals(Application.ApplicationType.Android))
			settings = new SettingsScreen(this);
		
		setScreen(spScreen1);
		
		
	}
	
	private void loadTexture() {
		background = new Texture(Gdx.files.internal("application/bkbl_background.png"));
	}
	
	public void load3DGraphics() {
		currentColor = defaultColor.cpy();
		
		mBatch = new ModelBatch();

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.6f, 0.6f, 0.6f, 0, -10, 0));
		environment.add(new PointLight().set(1, 1, 1, 0, 13, 0, 150));

		pCam = new PerspectiveCamera(40, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		pCam.far = 200;
		pCam.near = 0.1f;
		
		assets.finishLoading();
		
		map = new GameMap(game, game);
	}
	
	public static void clearColor() {
		currentColor = defaultColor.cpy();
	}

	private static final Matrix4 tempMx = new Matrix4();
	private static final Vector3 tempVec = new Vector3();
	@Override
	public void render () {
		if(getScreen().equals(tester)) {
			pCam.update();
			Gdx.gl.glClearColor(currentColor.r, currentColor.g, currentColor.b, currentColor.a);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
			map.render(mBatch, environment, pCam);
			
			super.render();
			return;
		}
		
		if (!getScreen().equals(spScreen1) && !getScreen().equals(game) && !game.paused()) {
			if (beautifulBack) {
				if (map == null) {
					load3DGraphics();
					return;
				}
				
				if(getScreen().equals(spScreen2)) {
					super.render();
					return;
				}

				Gdx.gl.glClearColor(currentColor.r, currentColor.g, currentColor.b, currentColor.a);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

				pCam.position.set(spinMx.cpy().mul(tempMx.setToTranslation(0, 9, 25)).getTranslation(tempVec));
				customLookAt(pCam, Vector3.Zero);
				if(map.getTerrain().getTheme() != null && map.getTerrain().getTheme().hasWallModels()) {
					map.getCamera().setPosition(pCam.position);
					map.getCamera().setDirection(pCam.direction);
					map.updateCamera();
					pCam.position.set(map.getCamera().getPosition());
				}
				
				pCam.update();
				//mBatch.begin(pCam);
				map.render(mBatch, environment, pCam);
				
				spinMx.rotate(0, 1, 0, 10 * Gdx.graphics.getDeltaTime());
				//mBatch.end();
			} else {
				if (background == null) {
					loadTexture();
					return;
				}
				
				if(getScreen().equals(spScreen2)) {
					super.render();
					return;
				}
				
				Gdx.gl.glClearColor(0, 0, 0, 1);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

				batch.setProjectionMatrix(cam.combined);
				batch.begin();
				batch.draw(background, cam.viewportWidth / 2 - background.getWidth() / 2, cam.viewportHeight / 2 - background.getHeight() / 2);
				batch.end();
			}
		}
		else if(game.paused()/* && !map.getMultiplayer().isMultiplayer()*/ && !getScreen().equals(game)) game.render(Gdx.graphics.getDeltaTime());
		
		super.render();
		
		/*System.out.println();
		System.out.println(Gdx.graphics.getFramesPerSecond() + " fps");
		System.out.println();*/
		
		if(sender != null) {
			messageLabel.update();
			if(messageTime < 0 && skippable)
				messageCont.update();
			else messageTime -= Gdx.graphics.getDeltaTime();
			messageBox.update();
			
			if(messageCont.justReleased()) {
				String temp = messageLabel.getText();
				sender.messageReceived();
				
				if(messageLabel.getText().equals(temp))
					sender = null;
			}
			
			messageBox.draw();
		}
		
		if (!getScreen().equals(spScreen1) && !getScreen().equals(spScreen2) && !game.paused()) {
			batch.setProjectionMatrix(cam.combined);
			batch.begin();
			font.draw(batch, Gdx.graphics.getFramesPerSecond() + " fps; v1.0.0.0", 0, font.getLineHeight());
			batch.end();
		}
	}
	
	public static void setColor(Color color) {
		if(color == null)
			currentColor = defaultColor;
		else currentColor = color;
	}
	
	/**
	 * This method is being overridden inside the LwjglApplication's constructor, where we can modify the LwjglApplicationConfiguration's values from, something we cannot
	 * do in other way in runtime.
	 * @param fps
	 */
	public void setForegroundFps(int fps) {}
	
	public int getForegroundFps() {return 0;}
	
	public void setResolution(int width, int height) {}
	
	public void renderLogo(SpriteBatch batch, OrthographicCamera cam) {
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		batch.draw(logo, getLogoX(), getLogoY());
		batch.end();
	}
	
	public float getLogoX() {
		return cam.viewportWidth / 2 - logo.getWidth() / 2;
	}
	
	public float getLogoY() {
		return cam.viewportHeight - logo.getHeight() - 20;
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
	
	public String getChallengeDataStr() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < map.getChallenges().getCurrentChallenges().length; i++) {
			ChallengeLevel lvl = map.getChallenges().getCurrentChallenges()[i];
			sb.append(lvl.getId());
			
			if(lvl.hasChallengeLevels())
				sb.append(lvl.getChallengeLevel());
			
			if(i + 1 < map.getChallenges().getCurrentChallenges().length)
				sb.append(';');
		}
		
		sb.append(':');
		sb.append(level.getDifficulty());
		sb.append(level.getPlayersAmount());
		
		String tempStr = sb.toString();
		return tempStr;
	}
	
	private void messageResize() {
		float width = cam.viewportWidth;
		float height = cam.viewportHeight;
		
		messageLabel.setWidth(width - 80);
		messageLabel.setHeight(messageLabel.getRows() * textFont.getLineHeight());
		messageLabel.setPos(width / 2 - messageLabel.getWidth() / 2, height - 40 - messageLabel.getRows() * textFont.getLineHeight());
		//System.out.println((messageLabel.getRows() * textFont.getLineHeight()));
		messageCont.setSize(pixelXByCurrentSize(223 * MyGdxGame.GUI_SCALE), pixelYByCurrentSize(30 * MyGdxGame.GUI_SCALE));
		messageCont.setPos(width / 2 - messageCont.getWidth() / 2, messageLabel.getY() - messageCont.getHeight() - 40);
		
		/*messageBox.setSize(width, (height - messageCont.getY()));
		messageBox.setPos(width / 2 - messageBox.getWidth() / 2, height - 20 - messageBox.getHeight());*/
	}
	
	@Override
	public void resize(int width, int height) {
		if(!TESTING) {
			cam.setToOrtho(false, width, height);
			
			messageResize();
		}
		
		if(pCam != null) {
			pCam.viewportWidth = width;
			pCam.viewportHeight = height;
		}
		
		getScreen().resize(width, height); //That method doesn't automatically call in the screen so we have to call it manually
		
		if(!TESTING)
			if (game.paused())
				game.resize(width, height);
	}
	
	@Override
	public void dispose () {
		if (!TESTING) {
			batch.dispose();
			logo.dispose();
			shape.dispose();
			font.dispose();
			textFont.dispose();
			btnFont.dispose();
		}
		
		if(map != null) {
			mBatch.dispose();
			map.dispose();
		}
		else if(background != null)
			background.dispose();
		
		if(!TESTING) {
			spScreen1.dispose();
			spScreen2.dispose();
			main.dispose();		
			level.dispose();
			tutorial.dispose();
			challenge.dispose();
			gameType.dispose();
			playerGameType.dispose();
			game.dispose();
			gameOver.dispose();
		}
		
		
		assets.dispose();
	}
	
	public GameMap getMap() {
		return map;
	}
	
	public Environment getEnvironment() {
		return environment;
	}
	
	public PerspectiveCamera getPCam() {
		return pCam;
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
	
	public boolean isThereAMessage() {
		return sender != null;
	}

	@Override
	public void sendMessage(String heading, String description, Color textColor, GameMessageSender sender, boolean skippable, String[] args) {
		
	}

	@Override
	public void sendMinorMessage(String message) {
		
	}

	@Override
	public void sendMessage(String heading, String description, Color textColor, GameMessageSender sender, boolean skippable) {
		
		
	}

	@Override
	public void sendMessage(String message, Color textColor, GameMessageSender sender, boolean skippable) {
		this.sender = sender;
		messageLabel.setColor(textColor);
		messageLabel.setText(message);
		
		messageResize();
		
		messageTime = 1;
		this.skippable = skippable;
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
	public String getMessageHeading() {
		
		return messageLabel.getText();
	}

	@Override
	public String getMessageDesc() {
		
		return messageLabel.getText();
	}

	@Override
	public Color getMessageColor() {
		
		return messageLabel.getColor();
	}

	@Override
	public String[] getMessageArgs() {
		
		return messageArgs;
	}

	@Override
	public boolean isMessageSkippable() {
		
		return skippable;
	}
}
