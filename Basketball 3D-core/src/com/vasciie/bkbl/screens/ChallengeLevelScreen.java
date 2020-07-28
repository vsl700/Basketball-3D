package com.vasciie.bkbl.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.vasciie.bkbl.MyGdxGame;
import com.vasciie.bkbl.gamespace.levels.Challenges.ChallengeLevel;
import com.vasciie.bkbl.gui.Button;
import com.vasciie.bkbl.gui.CheckButton;
import com.vasciie.bkbl.gui.GUIRenderer;
import com.vasciie.bkbl.gui.Label;

public class ChallengeLevelScreen implements Screen, GUIRenderer {

	MyGdxGame game;
	
	ShapeRenderer shape;
	SpriteBatch batch;
	BitmapFont font, bigFont, medFont, checkBtnFont;
	OrthographicCamera cam;
	
	Button play, goBack;
	Button[] challengePages;
	CheckButton[][] challengeOptions;
	CheckButton playChallenge;
	Label challengeName;
	
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
		bigFont = new BitmapFont();
		bigFont.getData().setScale(3);
		
		play = new Button("Play", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
		goBack = new Button("Go Back", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
		
		playChallenge = new CheckButton("Play This Challenge", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
		
		challengeName = new Label("", medFont, Color.RED, false, this);
	}
	
	@Override
	public void show() {
		if(challengePages == null) {
			challengePages = new Button[game.getMap().getChallenges().getSize()];
			challengeOptions = new CheckButton[challengePages.length][];
			
			for(int i = 0; i < challengePages.length; i++) {
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
		}

	}

	@Override
	public void render(float delta) {
		for(int i = 0; i < challengePages.length; i++) {
			challengePages[i].update();
			
			for(int j = 0; j < challengeOptions[index].length; j++) {
				challengeOptions[index][j].update();
				
				if(index == i && challengeOptions[i][j].justTouched()) {
					if (challengeOptions[index][j].isToggled()) {
						for (int k = 0; k < challengeOptions[index].length; k++) {
							if (k == j)
								continue;

							challengeOptions[index][k].setToggled(false);
						}
					}else challengeOptions[index][j].setToggled(true);
				}
			}
			
			if(challengePages[i].justReleased()) {
				index = i;
				challengeName.setText(game.getMap().getChallenges().getGameLevel(index).getName());
				
				resize((int) cam.viewportWidth, (int) cam.viewportHeight);
			}
		}
		
		play.update();
		goBack.update();
		
		challengeName.update();
		
		playChallenge.update();
		
		if(goBack.justReleased()) {
			game.setScreen(game.gameType);
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
		
		challengeName.draw();
		
		playChallenge.draw();

	}

	@Override
	public void resize(int width, int height) {
		cam.setToOrtho(false, width, height);

		float btnWidth = game.pixelXByCurrentSize(223 * MyGdxGame.GUI_SCALE), btnHeight = game.pixelYByCurrentSize(30 * MyGdxGame.GUI_SCALE);
		
		
		play.setSize(btnWidth, btnHeight);
		play.setPos(width / 4 - btnWidth / 2, 50);
		
		goBack.setSize(btnWidth, btnHeight);
		goBack.setPos(width * 3 / 4 - btnWidth / 2, 50);
		
		float btnWidth2 = game.pixelXByCurrentSize(60 * MyGdxGame.GUI_SCALE);
		float btnSpace = 40;
		float firstLevelY = game.getLogoY() - btnHeight * 1.5f, bottomLevelY = height / 4 + btnHeight / 2;
		
		int parts = (int) Math.ceil(challengePages.length / 2.0);
		
		for(int i = 0; i < parts; i++) {
			challengePages[i].setSize(btnWidth2, btnHeight);
			challengePages[i].setPos(width / 2 + btnSpace / 2 - (challengePages.length / 4.0f - i) * (btnWidth2 + btnSpace), firstLevelY);
		}
		
		//challengeName.setWidth(btnWidth);
		challengeName.setPos(width / 2/* - challengeName.getWidth() / 2*/, firstLevelY - 26 * 2);
		
		float checkBtnSize = 40;
		float optionY = challengeName.getY() - checkBtnSize * 2;
		
		for (int i = 0; i < challengeOptions[index].length; i++) {
			CheckButton btn = challengeOptions[index][i];
			
			btn.setSize(40, 40);
			btn.setPos(width / 2 + btnSpace / 2 - (challengeOptions[index].length / 2.0f - i) * (btn.getTotalWidth() + btnSpace), optionY);
		}
		
		playChallenge.setSize(checkBtnSize, checkBtnSize);
		playChallenge.setPos(challengeName.getX() + challengeName.textSize() / 2 + checkBtnSize, challengeName.getY() - checkBtnSize / 2);

		for(int i = parts; i < challengePages.length; i++) {
			challengePages[i].setSize(btnWidth2, btnHeight);
			challengePages[i].setPos(width / 2 + btnSpace / 2 - (challengePages.length / 4.0f - (i - challengePages.length / 2.0f)) * (btnWidth2 + btnSpace), bottomLevelY);
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