package com.vasciie.bkbl.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.vasciie.bkbl.GameMessageSender;
import com.vasciie.bkbl.MyGdxGame;
import com.vasciie.bkbl.gamespace.tools.SettingsPrefsIO;
import com.vasciie.bkbl.gui.Button;
import com.vasciie.bkbl.gui.GUIRenderer;
import com.vasciie.bkbl.gui.Label;
import com.vasciie.bkbl.gui.NumUpDown;
import com.vasciie.bkbl.gui.TextUpDown;
import com.vasciie.bkbl.gui.UpDown;
import com.vasciie.bkbl.gui.UpDownListener;


public class LevelScreen implements Screen, GUIRenderer, UpDownListener, GameMessageSender {

	MyGdxGame game;
	
	SpriteBatch batch;
	ShapeRenderer shape;
	OrthographicCamera cam;
	BitmapFont btnFont, textFont, descFont;
	
	NumUpDown numUpDown;
	TextUpDown textUpDown;
	Button play, back;
	Label tmPlAmount, difficulty; //tmPlAmount - team players amount description
	Label highscore;
	Label[][] difficultyDesc;
	
	private static final String highscoreText = "Highscore For The Game Level Combination Below: ", highscoreChText = "Highscore For The Challenge & Game Level Combination: ";
	
	
	public LevelScreen(MyGdxGame mg) {
		game = mg;
		
		cam = new OrthographicCamera();
		
		shape = new ShapeRenderer();
		batch = new SpriteBatch();
		
		btnFont = new BitmapFont();
		btnFont.getData().setScale(MyGdxGame.GUI_SCALE);
		
		textFont = new BitmapFont();
		textFont.getData().setScale(2 * MyGdxGame.GUI_SCALE);
		
		descFont = new BitmapFont();
		descFont.getData().setScale(1.25f * MyGdxGame.GUI_SCALE);
		
		createGui();
	}
	
	private void createGui() {
		numUpDown = new NumUpDown(btnFont, textFont, Color.WHITE, Color.BROWN, new Color().set(0.8f, 0.4f, 0, 1), 1, 5, this);
		numUpDown.setOption(3);
		numUpDown.setListener(this);
		
		ArrayList<String> choices = new ArrayList<String>();
		choices.add("Easy");
		choices.add("Hard");
		choices.add("Very Hard");
		textUpDown = new TextUpDown(btnFont, textFont, Color.WHITE, Color.BROWN, new Color().set(0.8f, 0.4f, 0, 1), choices, true, this);
		textUpDown.setListener(this);
		
		play = new Button("Play", btnFont, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
		back = new Button("Go Back", btnFont, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
		
		tmPlAmount = new Label("THE AMOUNT OF PLAYERS PER TEAM", btnFont, Color.BROWN, true, this);
		
		difficulty = new Label("THE DIFFICULTY OF THE GAME", btnFont, Color.BROWN, true, this);
		
		highscore = new Label(highscoreText, textFont, Color.GREEN, false, this);
		
		
		difficultyDesc = new Label[3][];
		
		Color tempColor = Color.BROWN;
		difficultyDesc[0] = new Label[] {new Label("Auto-dribble", descFont, tempColor, false, this), 
				new Label("Opponent Bots Easily Make Fouls", descFont, tempColor, false, this),
				new Label("Players Dribble Longer Than Usual For Easier Stealing", descFont, tempColor, false, this),
				new Label("Opponents Steal The Ball Much Harder (originally always)", descFont, tempColor, false, this),
				new Label("No Fouls Limit Per Player", descFont, tempColor, false, this)};
		
		difficultyDesc[1] = new Label[] {new Label("Auto-dribble", descFont, tempColor, false, this), 
				new Label("Bots Don't Make Fouls", descFont, tempColor, false, this),
				new Label("'Time Out' Rule Included", descFont, tempColor, false, this),
				new Label("Fouls Limit Of 7 Per Player", descFont, tempColor, false, this)};
		
		difficultyDesc[2] = new Label[] {new Label("Teammate Bots Easily Make Fouls", descFont, tempColor, false, this),
				new Label("'Time Out' Rule Included", descFont, tempColor, false, this),
				new Label("'Free Throw Violation' Rule Included", descFont, tempColor, false, this),
				new Label("Fouls Limit Of 7 Per Player", descFont, tempColor, false, this)};
	}
	
	@Override
	public void show() {
		setHighscoreText();
		
		if(!SettingsPrefsIO.readSettingBool("maxPoints")) {
			game.sendMessage("The Normal Matches In This Game Last Until One Of The Teams Passes 15 Points! After That, The Game Is Over And If Your Team Has More Points Than The Other, You Win And You Get Much More Points Which Are Being Saved If They're More Than The Previous! Otherwise You Only Get Points If YOU Had Earned Any Points For Your Team!", Color.RED, this, true);
		}
	}
	
	private void setHighscoreText() {
		if(game.getMap().isChallenge()) {
			highscore.setText(highscoreChText + SettingsPrefsIO.readSettingInteger(game.getChallengeDataStr()));
		}else highscore.setText(highscoreText + SettingsPrefsIO.readSettingInteger(getDifficulty() + "" + getPlayersAmount()));
	}

	@Override
	public void render(float delta) {
		cam.update();

		numUpDown.update();
		textUpDown.update();
		play.update();
		back.update();
		
		tmPlAmount.update();
		difficulty.update();
		highscore.update();
		
		for(Label lbl : difficultyDesc[getDifficulty()])
			lbl.update();
		
		if(back.justReleased() && !game.isThereAMessage()) {
			if(game.getMap().isChallenge())
				game.setScreen(game.challenge);
			else game.setScreen(game.gameType);
		}else if(play.justReleased() && !game.isThereAMessage()) {
			game.game.setPlayersAmount(numUpDown.getOption());
			game.setScreen(game.game);
		}

		game.renderLogo(batch, cam);

		numUpDown.draw();
		textUpDown.draw();
		play.draw();
		back.draw();

		tmPlAmount.draw();
		difficulty.draw();
		highscore.draw();
		
		for(Label lbl : difficultyDesc[getDifficulty()])
			lbl.draw();
	}

	@Override
	public void resize(int width, int height) {
		cam.setToOrtho(false, width, height);

		int constant = 60;

		play.setSize(game.pixelXByCurrentSize(223 * MyGdxGame.GUI_SCALE), game.pixelYByCurrentSize(30 * MyGdxGame.GUI_SCALE));
		play.setPos(width / 4 - play.getWidth() / 2 - constant, game.pixelYByCurrentSize(150 / MyGdxGame.GUI_SCALE));
		back.setSize(game.pixelXByCurrentSize(223 * MyGdxGame.GUI_SCALE), game.pixelYByCurrentSize(30 * MyGdxGame.GUI_SCALE));
		back.setPos(width * 3 / 4 - play.getWidth() / 2 + constant, game.pixelYByCurrentSize(150 / MyGdxGame.GUI_SCALE));
		numUpDown.setSize(game.pixelXByCurrentSize(74 * MyGdxGame.GUI_SCALE), game.pixelYByCurrentSize(45 * MyGdxGame.GUI_SCALE));
		numUpDown.setPos(back.getX() - 90, game.pixelYByCurrentSize(325));
		textUpDown.setSize(game.pixelXByCurrentSize(104 * MyGdxGame.GUI_SCALE), game.pixelYByCurrentSize(45 * MyGdxGame.GUI_SCALE));
		textUpDown.setPos(play.getX() + play.getWidth() + 90 - textUpDown.getTotalWidth(), game.pixelYByCurrentSize(325));
		
		tmPlAmount.setPosAndSize(numUpDown.getX() + numUpDown.getTotalWidth() / 2 / MyGdxGame.GUI_SCALE - 164 / 2, numUpDown.getY() + numUpDown.getHeight() + 10, 164 * MyGdxGame.GUI_SCALE);
		difficulty.setPosAndSize(textUpDown.getX() + textUpDown.getTotalWidth() / 2 / MyGdxGame.GUI_SCALE - 164 / 2, textUpDown.getY() + textUpDown.getHeight() + 10, 164 * MyGdxGame.GUI_SCALE);
		highscore.setPos(width / 2 - 13, game.getLogoY() - 13 * MyGdxGame.GUI_SCALE);
		
		resizeDifficultyDesc();
	}
	
	private void resizeDifficultyDesc() {
		//float width = cam.viewportWidth, height = cam.viewportHeight;
		
		for(int i = 0; i < difficultyDesc[getDifficulty()].length; i++) {
			Label lbl = difficultyDesc[getDifficulty()][i];
			
			lbl.setY(textUpDown.getY() - 13 * 1.25f * MyGdxGame.GUI_SCALE * (i + 1));
			lbl.setX(textUpDown.getX() + lbl.textSize() / 2);
		}
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
		btnFont.dispose();
		textFont.dispose();
		descFont.dispose();
	}
	
	public int getDifficulty() {
		return textUpDown.getOption();
	}
	
	public int getPlayersAmount() {
		return numUpDown.getOption();
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
	public void onOptionChanged(UpDown upDown, int newValue) {
		setHighscoreText();
		
		if(upDown.equals(textUpDown))
			resizeDifficultyDesc();
		
	}

	@Override
	public void messageReceived() {
		if(!SettingsPrefsIO.readSettingBool("maxPoints")) {
			game.sendMessage("Oh, And Also If You Are Playing A Gamemode In Which The Players Have A Limited Amount Of Fouls To Make And A Player Reaches It, The Game Removes Him From The Game! If The Game Removes YOU, You Directly LOSE!", Color.RED, this, true);
			SettingsPrefsIO.writeSettingBool("maxPoints", true);
			SettingsPrefsIO.flush();
		}else if(!SettingsPrefsIO.readSettingBool("editablePanels")) {
			game.sendMessage("Note That The Menu Elements Like The One For The Amount Of Players Below (that contain number values) Can Also Be Edited By Keyboard! Just Click On Them, Type An Accessible By The Element Number And Click Enter Or Anywhere On The Screen To Confirm The Changes!", Color.RED, this, true);
			SettingsPrefsIO.writeSettingBool("editablePanels", true);
			SettingsPrefsIO.flush();
		}
	}
}
