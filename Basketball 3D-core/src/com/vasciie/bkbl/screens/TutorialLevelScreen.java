package com.vasciie.bkbl.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.vasciie.bkbl.GameMessageSender;
import com.vasciie.bkbl.MyGdxGame;
import com.vasciie.bkbl.gamespace.levels.TutorialLevels.TutorialLevel;
import com.vasciie.bkbl.gamespace.tools.SettingsPrefsIO;
import com.vasciie.bkbl.gui.Button;
import com.vasciie.bkbl.gui.GUIRenderer;
import com.vasciie.bkbl.gui.Label;

public class TutorialLevelScreen implements Screen, GUIRenderer, GameMessageSender {

	MyGdxGame game;
	
	SpriteBatch batch;
	ShapeRenderer shape;
	OrthographicCamera cam;
	BitmapFont font, btnFont;
	
	Label[] levelNames;
	Button[][] levelParts;
	Button goBack;
	
	
	public TutorialLevelScreen(MyGdxGame mg) {
		game = mg;
		
		cam = new OrthographicCamera();
		
		shape = new ShapeRenderer();
		batch = new SpriteBatch();
		font = new BitmapFont();
		font.getData().setScale(1.35f * MyGdxGame.GUI_SCALE);
		btnFont = new BitmapFont();
		btnFont.getData().setScale(MyGdxGame.GUI_SCALE);
		
		goBack = new Button("Go Back", btnFont, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
	}
	
	@Override
	public void show() {
		if(levelNames == null) {
			levelNames = new Label[game.getMap().getTutorial().getSize()];
			levelParts = new Button[levelNames.length][];
			
			for(int i = 0; i < levelNames.length; i++) {
				TutorialLevel level = (TutorialLevel) game.getMap().getTutorial().getGameLevel(i);
				
				levelNames[i] = new Label(level.getName(), font, Color.BROWN, true, this);
				
				levelParts[i] = new Button[level.getParts()];
				for(int j = 0; j < level.getParts(); j++) {
					levelParts[i][j] = new Button(level.getPart(j).getName(), btnFont, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
				}
			}
			
			if(!SettingsPrefsIO.readSettingBool("tutorial")) {
				game.sendMessage("Note That Each Tutorial Level Is Splitted On Parts And If You Just Want To Revive Something You Can Simply Start The Level From Where You Want To! After A Tutorial Level's Part Finishes, The Tutorial Goes To The Next Part! Hitting The New Game Button Sends You To The First Part Of The Level!", Color.RED, this, true);
				SettingsPrefsIO.writeSettingBool("tutorial", true);
				SettingsPrefsIO.flush();
			}
		}

	}

	@Override
	public void render(float delta) {
		for(Label label : levelNames)
			label.update();
		
		for(Button[] btnArr : levelParts)
			for(Button btn : btnArr)
				btn.update();
		
		goBack.update();
		
		if(goBack.justReleased() && !game.isThereAMessage())
			game.setScreen(game.gameType);
		else {
			for(int i = 0; i < levelParts.length; i++) {
				boolean flag = false;
				for(int j = 0; j < levelParts[i].length; j++)
					if(levelParts[i][j].justReleased() && !game.isThereAMessage()) {
						game.getMap().setTutorialLevel(i, j);
						game.setScreen(game.game);
						flag = true;
						break;
					}
				
				if(flag)
					break;
			}
		}
		
		game.renderLogo(batch, cam);

		for(Label label : levelNames)
			label.draw();
		
		for(Button[] btnArr : levelParts)
			for(Button btn : btnArr)
				btn.draw();
		
		goBack.draw();
		
	}

	@Override
	public void resize(int width, int height) {
		cam.setToOrtho(false, width, height);
		
		float btnWidth = game.pixelXByCurrentSize(223 * MyGdxGame.GUI_SCALE), btnHeight = game.pixelYByCurrentSize(30 * MyGdxGame.GUI_SCALE);
		
		goBack.setSize(btnWidth, btnHeight);
		goBack.setPos(width / 2 - btnWidth / 2, 50);
		
		for(int i = 0; i < levelNames.length; i++) {
			Label label = levelNames[i];
			
			float x = width * (i + 1) / (levelNames.length + 1) - btnWidth / 2;
			
			label.setSize(btnWidth, 0);
			label.setPos(x, game.getLogoY() - label.getRows() * 13 * (font.getData().scaleX + 0.5f));
			
			for(int j = 0; j < levelParts[i].length; j++) {
				Button btn = levelParts[i][j];
				
				btn.setPosAndSize(x, label.getY() - btnHeight * 2 * (j + 1), btnWidth, btnHeight);
			}
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
		game.sendMessage("Oh, And Make Sure To Read Every Single Message You'll See (not only in the tutorial)!", Color.RED, this, true);
	}

}
