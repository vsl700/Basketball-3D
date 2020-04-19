package com.vasciie.bkbl.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.vasciie.bkbl.MyGdxGame;
import com.vasciie.bkbl.gui.Button;
import com.vasciie.bkbl.gui.Label;
import com.vasciie.bkbl.gui.NumUpDown;
import com.vasciie.bkbl.gui.TextUpDown;


public class LevelScreen implements Screen {

	MyGdxGame game;
	
	SpriteBatch batch;
	ShapeRenderer shape;
	OrthographicCamera cam;
	BitmapFont btnFont, textFont;
	
	NumUpDown numUpDown;
	TextUpDown textUpDown;
	Button play, back;
	Label tmPlAmount, difficulty; //tmPlAmount - team players amount description
	
	public LevelScreen(MyGdxGame mg) {
		game = mg;
		
		cam = new OrthographicCamera();
		
		shape = new ShapeRenderer();
		batch = new SpriteBatch();
		
		btnFont = new BitmapFont();
		btnFont.getData().setScale(MyGdxGame.GUI_SCALE);
		
		textFont = new BitmapFont();
		textFont.getData().setScale(2 * MyGdxGame.GUI_SCALE);
		
		createGui();
	}
	
	private void createGui() {
		numUpDown = new NumUpDown(btnFont, textFont, Color.WHITE, Color.BROWN, new Color().set(0.8f, 0.4f, 0, 1), 1, 5);
		numUpDown.setOption(3);
		
		ArrayList<String> choices = new ArrayList<String>();
		choices.add("Easy");
		choices.add("Hard");
		choices.add("Very Hard");
		textUpDown = new TextUpDown(btnFont, textFont, Color.WHITE, Color.BROWN, new Color().set(0.8f, 0.4f, 0, 1), choices, true);
		
		play = new Button("Play", btnFont, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true);
		back = new Button("Go Back", btnFont, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true);
		
		tmPlAmount = new Label("THE AMOUNT OF PLAYERS PER TEAM", btnFont, Color.BROWN, true);
		
		difficulty = new Label("THE DIFFICULTY OF THE GAME", btnFont, Color.BROWN, true);
	}
	
	@Override
	public void show() {

	}

	@Override
	public void render(float delta) {
		cam.update();
		
		game.renderLogo(batch, cam);
		
		numUpDown.render(batch, shape, cam);
		textUpDown.render(batch, shape, cam);
		play.render(batch, shape, cam);
		back.render(batch, shape, cam);
		
		tmPlAmount.render(batch, shape, cam);
		difficulty.render(batch, shape, cam);
		
		if(back.justReleased())
			game.setScreen(game.main);
		else if(play.justReleased()) {
			game.game.setPlayersAmount(numUpDown.getOption());
			game.setScreen(game.game);
		}
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
	}
	
	public int getDifficulty() {
		return textUpDown.getOption();
	}

}
