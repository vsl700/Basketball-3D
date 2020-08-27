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
	
	Label desc, ruleName, scoresDesc, innerRuleDesc;
	CheckButton[][] includeRule, gameStop, autoAct;
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
		
		innerRuleDesc = new Label("Inner Rules: ", textFont, Color.RED, false, this);
		
		desc = new Label("Select The Game Rules You Want To Include In The Game!", textFont, Color.GREEN, false, this);
		
		scoresDesc = new Label("AMOUNT OF TARGET POINTS! (0 for endless)", font, Color.BROWN, true, this);
		
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
			
			includeRule = new CheckButton[rulePages.length][];
			gameStop = new CheckButton[rulePages.length][];
			autoAct = new CheckButton[rulePages.length][];
			
			for(int i = 0; i < rulePages.length; i++) {
				rulePages[i] = new Button(i + 1 + "", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this); 
				
				
				if(tempRules.getGameRules()[i].getInnerRules() != null) {
					innerRulePages[i] = new Button[tempRules.getGameRules()[i].getInnerRules().length];
					
					gameStop[i] = new CheckButton[innerRulePages.length + 1];
					autoAct[i] = new CheckButton[innerRulePages.length + 1];
					
					if(tempRules.getGameRules()[i].isGameStopChoosable()) {
						gameStop[i][0] = new CheckButton("On Triggered Rule - Game Stop", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
						autoAct[i][0] = new CheckButton("On Triggered Rule - Auto-acting", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
					}
					
					includeRule[i] = new CheckButton[innerRulePages[i].length + 1];
					includeRule[i][0] = new CheckButton("Include Rule", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
					
					for(int j = 0; j < innerRulePages[i].length; j++) {
						includeRule[i][j + 1] = new CheckButton("Include Rule", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
						
						if (tempRules.getGameRuleById(tempRules.getGameRules()[i].getInnerRules()[j].getId()) == null) {
							innerRulePages[i][j] = new Button((i + 1) + "." + (j + 1), font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
							if (tempRules.getGameRules()[i].getInnerRules()[j].isGameStopChoosable()) {
								gameStop[i][j + 1] = new CheckButton("On Triggered Rule - Game Stop", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
								autoAct[i][j + 1] = new CheckButton("On Triggered Rule - Auto-acting", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
							}
						}
					}
				}else {
					innerRulePages[i] = new Button[0];
					includeRule[i]  = new CheckButton[1];
					includeRule[i][0] = new CheckButton("Include Rule", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
					
					gameStop[i] = new CheckButton[1];
					autoAct[i] = new CheckButton[1];
					
					if(tempRules.getGameRules()[i].isGameStopChoosable()) {
						gameStop[i][0] = new CheckButton("On Triggered Rule - Game Stop", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
						autoAct[i][0] = new CheckButton("On Triggered Rule - Auto-acting", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
					}
				}
				
			}
			
			ruleName.setText(game.getMap().getRules().getGameRules()[page].getName());
			
		}
		
		
	}

	@Override
	public void render(float delta) {
		for(int i = 0; i < rulePages.length; i++) {
			rulePages[i].update();
			
			if(rulePages[i].justReleased()) {
				page = i;
				innerPage = -1;
				ruleName.setText(game.getMap().getRules().getGameRules()[i].getName());
				resizeRuleName();
			}
		}
		
		for(int j = 0; j < innerRulePages[page].length; j++) {
			if(innerRulePages[page][j] == null)
				continue;
			
			innerRulePages[page][j].update();
			
			if(!innerRuleDesc.isRenderable())
				innerRuleDesc.update();
			
			if(innerRulePages[page][j].justReleased()) {
				innerPage = j;
				ruleName.setText(game.getMap().getRules().getGameRules()[page].getInnerRules()[j].getName());
				resizeRuleName();
			}
		}
		
		desc.update();
		
		ruleName.update();
		
		selectAll.update();
		
		if(selectAll.justReleased()) {
			boolean flag = false;
			for(int i = 0; i < includeRule.length; i++) {
				if(!includeRule[i][0].isToggled()) {
					includeRule[i][0].setToggled(true);
					flag = true;
				}
				
				for(int j = 1; j < includeRule[i].length; j++) {
					if(!includeRule[i][j].isToggled()) {
						includeRule[i][j].setToggled(true);
						flag = true;
					}
				}
			}
			
			if(!flag) { //Do a deselection of all check buttons
				for(int i = 0; i < includeRule.length; i++) {
					includeRule[i][0].setToggled(false);
					
					for(int j = 1; j < includeRule[i].length; j++) {
						includeRule[i][j].setToggled(false);
					}
				}
			}
		}
		
		includeRule[page][innerPage + 1].update();
		
		if(gameStop[page][innerPage + 1] != null) {
			gameStop[page][innerPage + 1].update();
			autoAct[page][innerPage + 1].update();
		}
		
		if (ruleName.getText().equals("SCORE!")) {
			scores.update();
			scoresDesc.update();
		}
		
		next.update();
		goBack.update();
		
		if(goBack.justReleased()) {
			game.getMap().stopMultiplayer();
			
			game.setScreen(game.joinOrCreate);
		}else if(next.justReleased()) {
			Rules tempRules = game.getMap().getRules();
			for(int i = 0; i < includeRule.length; i++) {
				if(!includeRule[i][0].isToggled()) {
					tempRules.getGameRules()[i].setActive(false);
				}
				
				for(int j = 1; j < includeRule[i].length - 1; j++) {
					if(!includeRule[i][j].isToggled()) {
						tempRules.getGameRules()[i].getInnerRules()[j].setActive(false);
					}
				}
			}
			
			game.getMap().setTargetScore(scores.getOption());
			
			//TODO Add something about the GAMESTOP and AUTO-ACTING for the score rule!
			//game.setScreen(game.challenge);
			game.setScreen(game.game);
		}
		
		game.renderLogo(batch, cam);

		for(int i = 0; i < rulePages.length; i++) {
			rulePages[i].draw();
		}
		
		for(int j = 0; j < innerRulePages[page].length; j++) {
			if(innerRulePages[page][j] == null)
				continue;
			
			innerRulePages[page][j].draw();
		}
		
		desc.draw();
		
		ruleName.draw();
		
		innerRuleDesc.draw();
		
		includeRule[page][innerPage + 1].draw();
		
		if(gameStop[page][innerPage + 1] != null) {
			gameStop[page][innerPage + 1].draw();
			autoAct[page][innerPage + 1].draw();
		}
		
		selectAll.draw();
		
		scores.draw();
		scoresDesc.draw();
		
		next.draw();
		goBack.draw();
		
	}
	
	private void resizeRuleName() {
		ruleName.setX(cam.viewportWidth / 2);
		
		resizeIncludeRule(page, innerPage + 1);
	}
	
	private void resizeIncludeRule(int i, int j) {
		float checkBtnSize = 40;
		includeRule[i][j].setSize(checkBtnSize, checkBtnSize);
		includeRule[i][j].setPos(ruleName.getX() + ruleName.textSize() / 2 + 20, ruleName.getY() - checkBtnSize / 2);
		
		if(gameStop[i][j] != null) {
			gameStop[i][j].setSize(checkBtnSize, checkBtnSize);
			gameStop[i][j].setPos(includeRule[i][j].getX(), includeRule[i][j].getY() - checkBtnSize - 10);
			
			autoAct[i][j].setSize(checkBtnSize, checkBtnSize);
			autoAct[i][j].setPos(includeRule[i][j].getX(), gameStop[i][j].getY() - checkBtnSize - 10);
		}
		
		if(innerRulePages[i].length > 0 && innerRulePages[i][0] != null) {
			innerRuleDesc.setWidth(0);
			innerRuleDesc.setPos(innerRulePages[i][0].getX() - innerRuleDesc.textSize() / 2 - 10, innerRulePages[i][0].getY() + textFont.getLineHeight() / 4);
		}
		
		if(ruleName.getText().equals("SCORE!")) {
			scores.setSize(game.pixelXByCurrentSize(74 * MyGdxGame.GUI_SCALE), game.pixelYByCurrentSize(45 * MyGdxGame.GUI_SCALE));
			scores.setPos(cam.viewportWidth / 2 - scores.getTotalWidth() / 2, rulePages[rulePages.length - 1].getY() + rulePages[rulePages.length - 1].getHeight() + 10);
			
			scoresDesc.setWidth(scores.getTotalWidth());
			scoresDesc.setPos(scores.getX(), scores.getY() + scores.getHeight() + 10);
		}
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
					if(innerRulePages[i][j] == null)
						continue;
					
					innerRulePages[i][j].setSize(btnWidth2, btnHeight);
					innerRulePages[i][j].setPos(tempXs2[j], innerLevelY);
				}
			}
		}
		
		for (int i = 0; i < tempXs[1].length; i++) {
			rulePages[i + tempXs[0].length].setSize(btnWidth2, btnHeight);
			rulePages[i + tempXs[0].length].setPos(tempXs[1][i], bottomLevelY);
		}
		
		ruleName.setY(innerLevelY - 26);
		resizeRuleName();
		
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
		descFont.dispose();
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
