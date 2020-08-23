package com.vasciie.bkbl.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.vasciie.bkbl.MyGdxGame;
import com.vasciie.bkbl.gamespace.rules.Rules;
import com.vasciie.bkbl.gamespace.tools.GameTools;
import com.vasciie.bkbl.gui.Button;
import com.vasciie.bkbl.gui.CheckButton;
import com.vasciie.bkbl.gui.GUIRenderer;
import com.vasciie.bkbl.gui.Label;
import com.vasciie.bkbl.gui.NumUpDown;

public class RulesChooseScreen implements Screen, GUIRenderer {

	MyGdxGame game;
	
	SpriteBatch batch;
	ShapeRenderer shape;
	OrthographicCamera cam;
	
	BitmapFont font, textFont, descFont;
	
	Label desc, ruleName, scoresDesc;
	CheckButton[] includeRule, gameStop, autoAct;
	Button[] rulePages;
	Button[][] innerRulePages;
	Button next, goBack, selectAll;
	NumUpDown scores;
	
	int page, innerPage;
	
	
	public RulesChooseScreen(MyGdxGame mg) {
		game = mg;
		
		cam = new OrthographicCamera();
		
		shape = new ShapeRenderer();
		batch = new SpriteBatch();
		
		font = new BitmapFont();
		font.getData().setScale(MyGdxGame.GUI_SCALE);
		
		textFont = new BitmapFont();
		textFont.getData().setScale(2 * MyGdxGame.GUI_SCALE);
		
		
		ruleName = new Label("", textFont, Color.RED, false, this);
		
		desc = new Label("Select The Game Rules You Want To Include In The Game!", textFont, Color.GREEN, false, this);
		
		scoresDesc = new Label("THE MAXIMUM SCORES AT WHICH THE GAME SHOULD BE OVER!", font, Color.BROWN, true, this);
		
		scores = new NumUpDown(font, textFont, Color.WHITE, Color.BROWN, new Color().set(0.8f, 0.4f, 0, 1), 0, (int) Float.MAX_VALUE, this);
		
		selectAll = new Button("Select/Deselect All", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
		next = new Button("Next", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
		goBack = new Button("Go Back", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
	}
	
	@Override
	public void show() {
		if(rulePages == null) {
			Rules tempRules = game.getMap().getRules();
			
			rulePages = new Button[tempRules.getGameRules().length];
			
			innerRulePages = new Button[rulePages.length][];
			
			gameStop = new CheckButton[rulePages.length];
			autoAct = new CheckButton[rulePages.length];
			
			for(int i = 0; i < rulePages.length; i++) {
				rulePages[i] = new Button(i + 1 + "", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
				
				if(tempRules.getGameRules()[i].getInnerRules() != null) {
					innerRulePages[i] = new Button[tempRules.getGameRules()[i].getInnerRules().length];
					
					for(int j = 0; j < innerRulePages[i].length; j++) {
						innerRulePages[i][j] = new Button((i + 1) + "." + (j + 1), font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
					}
				}else innerRulePages[i] = new Button[0];
				
				if(tempRules.getGameRules()[i].isGameStopChoosable()) {
					gameStop[i] = new CheckButton("On Triggered Rule - Game Stop", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
					autoAct[i] = new CheckButton("On Triggered Rule - Auto-acting", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
				}
			}
			
			
		}

	}

	@Override
	public void render(float delta) {
		for(int i = 0; i < rulePages.length; i++) {
			rulePages[i].update();
			
			if(rulePages[i].justReleased()) {
				page = i;
				innerPage = 0;
				ruleName.setText(game.getMap().getRules().getGameRules()[i].getName());
				resizeRuleName();
			}
		}
		
		for(int j = 0; j < innerRulePages[page].length; j++) {
			innerRulePages[page][j].update();
		}
		
		desc.update();
		
		ruleName.update();
		
		selectAll.update();
		
		next.update();
		goBack.update();
		
		if(goBack.justReleased())
			game.setScreen(game.joinOrCreate);
		
		game.renderLogo(batch, cam);

		for(int i = 0; i < rulePages.length; i++) {
			rulePages[i].draw();
		}
		
		for(int j = 0; j < innerRulePages[page].length; j++) {
			innerRulePages[page][j].draw();
		}
		
		desc.draw();
		
		ruleName.draw();
		
		selectAll.draw();
		
		next.draw();
		goBack.draw();
		
	}
	
	private void resizeRuleName() {
		ruleName.setX(cam.viewportWidth / 2);
	}

	@Override
	public void resize(int width, int height) {
		cam.setToOrtho(false, width, height);
		
		float btnWidth = game.pixelXByCurrentSize(223 * MyGdxGame.GUI_SCALE), btnHeight = game.pixelYByCurrentSize(30 * MyGdxGame.GUI_SCALE);
		
		
		next.setSize(btnWidth, btnHeight);
		next.setPos(width / 4 - btnWidth / 2, 50);
		
		goBack.setSize(btnWidth, btnHeight);
		goBack.setPos(width * 3 / 4 - btnWidth / 2, 50);

		desc.setPos(width / 2 - 13, game.getLogoY() - 13 * MyGdxGame.GUI_SCALE);
		
		float btnWidth2 = game.pixelXByCurrentSize(60 * MyGdxGame.GUI_SCALE);
		float btnSpace = 40;
		float firstLevelY = game.getLogoY() - btnHeight * 3f, bottomLevelY = next.getY() + btnHeight * 4, innerLevelY = firstLevelY - btnHeight - 15;
		float[][] tempXs = GameTools.beautyX1(rulePages.length, 2, width, btnSpace, btnWidth2);
		
		
		for (int i = 0; i < tempXs[0].length; i++) {
			rulePages[i].setSize(btnWidth2, btnHeight);
			rulePages[i].setPos(tempXs[0][i], firstLevelY);
			
			if(innerRulePages[i].length > 0) {
				float[] tempXs2 = GameTools.beautyX1(innerRulePages[i].length, width, btnSpace, btnWidth2);
				
				for(int j = 0; j < innerRulePages[i].length; j++) {
					innerRulePages[i][j].setSize(btnWidth2, btnHeight);
					innerRulePages[i][j].setPos(tempXs2[j], innerLevelY);
				}
			}
		}
		
		ruleName.setSize(0, 0);//We want the label to calculate the text width which is done by changing either a size or a pos
		ruleName.setPos(width / 2, innerLevelY - 26);
		
		for (int i = 0; i < tempXs[1].length; i++) {
			rulePages[i + tempXs[0].length].setSize(btnWidth2, btnHeight);
			rulePages[i + tempXs[0].length].setPos(tempXs[1][i], bottomLevelY);
		}
		
		selectAll.setSize(rulePages[rulePages.length - 1].getX() + rulePages[rulePages.length - 1].getWidth() - rulePages[tempXs[0].length].getX(), btnHeight);
		selectAll.setPos(width / 2 - selectAll.getWidth() / 2, rulePages[rulePages.length - 1].getY() - btnHeight - 30);
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

}
