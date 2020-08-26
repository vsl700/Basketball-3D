package com.vasciie.bkbl.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.vasciie.bkbl.GameMessageSender;
import com.vasciie.bkbl.MyGdxGame;
import com.vasciie.bkbl.gamespace.levels.Challenges.ChallengeLevel;
import com.vasciie.bkbl.gamespace.tools.GameTools;
import com.vasciie.bkbl.gamespace.tools.SettingsPrefsIO;
import com.vasciie.bkbl.gui.Button;
import com.vasciie.bkbl.gui.CheckButton;
import com.vasciie.bkbl.gui.GUIRenderer;
import com.vasciie.bkbl.gui.Label;

public class ChallengeLevelScreen implements Screen, GUIRenderer, GameMessageSender {

	MyGdxGame game;
	
	ShapeRenderer shape;
	SpriteBatch batch;
	BitmapFont font, bigFont, medFont, checkBtnFont, textFont;
	OrthographicCamera cam;
	
	Button play, goBack;
	Button[] challengePages;
	CheckButton[][] challengeOptions;
	CheckButton[] playChallenge;
	Label challengeName, losableChallenge, urgedChallenge, desc;
	
	int index;
	
	
	public ChallengeLevelScreen(MyGdxGame mg) {
		game = mg;
		
		cam = new OrthographicCamera();
		
		shape = new ShapeRenderer();
		batch = new SpriteBatch();
		font = new BitmapFont();
		font.getData().setScale(MyGdxGame.GUI_SCALE);
		checkBtnFont = new BitmapFont();
		checkBtnFont.getData().setScale(1.5f);
		//checkBtnFont.setColor(Color.BLUE);
		medFont = new BitmapFont();
		medFont.getData().setScale(2);
		textFont = new BitmapFont();
		textFont.getData().setScale(2 * MyGdxGame.GUI_SCALE);
		bigFont = new BitmapFont();
		bigFont.getData().setScale(3);
		
		play = new Button("Play", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
		goBack = new Button("Go Back", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
		
		//playChallenge = new CheckButton("Play This Challenge", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
		
		challengeName = new Label("", medFont, Color.RED, false, this);
		losableChallenge = new Label("IF THIS CHALLENGE BREAKS, YOU LOSE!", checkBtnFont, Color.RED, false, this);
		urgedChallenge = new Label("YOU WIN ONLY IF THIS CHALLENGE BREAKS!", checkBtnFont, Color.BLUE, false, this);
		desc = new Label("Select The Game Challenges You Want To Include In The Game!", textFont, Color.GREEN, false, this);
	}
	
	@Override
	public void show() {
		if(challengePages == null) {
			challengePages = new Button[game.getMap().getChallenges().getSize()];
			playChallenge = new CheckButton[challengePages.length];
			challengeOptions = new CheckButton[challengePages.length][];
			
			for(int i = 0; i < challengePages.length; i++) {
				playChallenge[i] = new CheckButton("Play This Challenge", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
				
				ChallengeLevel level = (ChallengeLevel) game.getMap().getChallenges().getGameLevel(i);
				
				challengePages[i] = new Button(i + 1 + "", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
				challengeOptions[i] = new CheckButton[level.getChallengeLevelsAmount()];
						
				for(int j = 0; j < challengeOptions[i].length; j++) {
					challengeOptions[i][j] = new CheckButton(level.getChallengeLevelsNames()[j], checkBtnFont, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
					
					if(j == 0)
						challengeOptions[i][j].setToggled(true);
				}
			}
			
			challengeName.setText(game.getMap().getChallenges().getGameLevel(index).getName());
			
			if(!SettingsPrefsIO.readSettingBool("challenges")) {
				game.sendMessage("Note That You Can Combine The Challenges Below To Make The Game You Probably Want To Make! Also, If You Don't Include Challenges That Require Breaking To Win Or You Don't Break A Losable Challenge, You Win If You Have More Score Than The Other Team (as soon as the game is over)! If You Don't Include A Challenge That Stops The Game, The Games Becomes Endless!", Color.RED, this, true);
				SettingsPrefsIO.writeSettingBool("challenges", true);
				SettingsPrefsIO.flush();
			}
		}

	}

	@Override
	public void render(float delta) {
		for(int i = 0; i < challengePages.length; i++) {
			challengePages[i].update();
			
			for(int j = 0; j < challengeOptions[index].length; j++) {
				challengeOptions[index][j].update();
				
				if(index == i && challengeOptions[index][j].justTouched()) {
					if (challengeOptions[index][j].isToggled()) {
						for (int k = 0; k < challengeOptions[index].length; k++) {
							if (k == j)
								continue;

							challengeOptions[index][k].setToggled(false);
						}
					}else challengeOptions[index][j].setToggled(true);
				}
			}
			
			if(challengePages[i].justReleased() && !game.isThereAMessage()) {
				index = i;
				challengeName.setText(game.getMap().getChallenges().getGameLevel(index).getName());
				
				resize((int) cam.viewportWidth, (int) cam.viewportHeight);
			}
		}
		
		play.update();
		goBack.update();
		
		desc.update();
		
		challengeName.update();
		
		playChallenge[index].update();
		if(playChallenge[index].isToggled())
			challengePages[index].setColor(Color.RED);
		else challengePages[index].setColor(Color.ORANGE.cpy().sub(0, 0.3f, 0, 1));
		
		if(((ChallengeLevel) game.getMap().getChallenges().getGameLevel(index)).isLosable())
			losableChallenge.update();
		else if(game.getMap().getChallenges().getGameLevel(index).getId().equals("team_score"))
			urgedChallenge.update();
		
		if(goBack.justReleased() && !game.isThereAMessage()) {
			game.setScreen(game.gameType);
			game.getMap().getChallenges().reset();
		}else if(play.justReleased() && !game.isThereAMessage()) {
			Array<ChallengeLevel> tempCh = new Array<ChallengeLevel>(playChallenge.length);
			for(int i = 0; i < playChallenge.length; i++) {
				if(playChallenge[i].isToggled()) {
					ChallengeLevel temp = (ChallengeLevel) game.getMap().getChallenges().getGameLevel(i);
					tempCh.add(temp);
					
					if(temp.hasChallengeLevels()) {
						for(int j = 0; j < challengeOptions[i].length; j++) {
							if(challengeOptions[i][j].isToggled()) {
								temp.setChallengeLevel(j);
								break;
							}
						}
					}
				}
			}
			
			if (tempCh.size > 0) {
				ChallengeLevel[] tempChArr = new ChallengeLevel[tempCh.size];
				for (int i = 0; i < tempCh.size; i++)
					tempChArr[i] = tempCh.get(i);

				game.getMap().getChallenges().setChallenge(tempChArr);//toArray() doesn't work on any Array class (Array, ArrayList...)
				game.setScreen(game.level);
			}
		}
		
		
		game.renderLogo(batch, cam);
		
		for(int i = 0; i < challengePages.length; i++) {
			challengePages[i].draw();
			
			for(int j = 0; j < challengeOptions[index].length; j++) {
				challengeOptions[index][j].draw();
			}
		}
		
		play.draw();
		goBack.draw();
		
		desc.draw();
		
		challengeName.draw();
		
		playChallenge[index].draw();
		
		losableChallenge.draw();
		urgedChallenge.draw();

	}

	@Override
	public void resize(int width, int height) {
		cam.setToOrtho(false, width, height);

		float btnWidth = game.pixelXByCurrentSize(223 * MyGdxGame.GUI_SCALE), btnHeight = game.pixelYByCurrentSize(30 * MyGdxGame.GUI_SCALE);
		
		
		play.setSize(btnWidth, btnHeight);
		play.setPos(width / 4 - btnWidth / 2, 50);
		
		goBack.setSize(btnWidth, btnHeight);
		goBack.setPos(width * 3 / 4 - btnWidth / 2, 50);
		
		desc.setPos(width / 2 - 13, game.getLogoY() - 13 * MyGdxGame.GUI_SCALE);
		
		float btnWidth2 = game.pixelXByCurrentSize(60 * MyGdxGame.GUI_SCALE);
		float btnSpace = 40;
		float firstLevelY = game.getLogoY() - btnHeight * 3f, bottomLevelY = height / 4 + btnHeight / 2;
		
		//int parts = (int) Math.ceil(challengePages.length / 2.0);
		float[][] tempXs = GameTools.beautyX1(challengePages.length, 2, width, btnSpace, btnWidth2);
		for(int i = 0; i < tempXs[0].length; i++) {
			challengePages[i].setSize(btnWidth2, btnHeight);
			challengePages[i].setPos(tempXs[0][i], firstLevelY);
		}
		
		//challengeName.setWidth(btnWidth);
		challengeName.setPos(width / 2/* - challengeName.getWidth() / 2*/, firstLevelY - 26);
		
		losableChallenge.setPos(width / 2, challengeName.getY() - 30);
		urgedChallenge.setPos(losableChallenge.getX(), losableChallenge.getY());
		
		float checkBtnSize = 40;
		float optionY = losableChallenge.getY() - checkBtnSize * 2;
		
		for (int i = 0; i < challengeOptions[index].length; i++) {
			CheckButton btn = challengeOptions[index][i];
			
			btn.setSize(40, 40);
			btn.setPos(width / 2 + btnSpace / 2 - (challengeOptions[index].length / 2.0f - i) * (btn.getTotalWidth() + btnSpace), optionY);
		}
		
		playChallenge[index].setSize(checkBtnSize, checkBtnSize);
		playChallenge[index].setPos(challengeName.getX() + challengeName.textSize() / 2 + checkBtnSize, challengeName.getY() - checkBtnSize / 2);

		for(int i = 0; i < tempXs[1].length; i++) {
			challengePages[i + tempXs[0].length].setSize(btnWidth2, btnHeight);
			challengePages[i + tempXs[0].length].setPos(tempXs[1][i], bottomLevelY);
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
		font.dispose();
		bigFont.dispose();
		medFont.dispose();
		checkBtnFont.dispose();
		textFont.dispose();
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
