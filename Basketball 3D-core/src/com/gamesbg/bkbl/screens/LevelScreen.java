package com.gamesbg.bkbl.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.gamesbg.bkbl.MyGdxGame;
import com.gamesbg.bkbl.gui.Button;
import com.gamesbg.bkbl.gui.Label;
import com.gamesbg.bkbl.gui.NumUpDown;
import com.gamesbg.bkbl.gui.TextUpDown;


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
		btnFont.getData().setScale(1);
		
		textFont = new BitmapFont();
		textFont.getData().setScale(2);
		
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
		
		if(back.justReleased(cam))
			game.setScreen(game.main);
		else if(play.justReleased(cam)) {
			game.game.setPlayersAmount(numUpDown.getOption());
			game.setScreen(game.game);
		}
	}

	@Override
	public void resize(int width, int height) {
		cam.setToOrtho(false, width, height);
		
		//textFont.getData().setScale(width * height / 921600 + 1);
		//btnFont.getData().setScale(width * height / 921600);
		
		play.setPosAndSize(game.pixelXByCurrentSize(149), game.pixelYByCurrentSize(150), game.pixelXByCurrentSize(223), game.pixelYByCurrentSize(30));
		back.setPosAndSize(game.pixelXByCurrentSize(893), game.pixelYByCurrentSize(150), game.pixelXByCurrentSize(223), game.pixelYByCurrentSize(30));
		//numUpDown.setPosAndSize(game.pixelXByCurrentSize(558), game.pixelYByCurrentSize(225), game.pixelXByCurrentSize(74), game.pixelYByCurrentSize(45));
		numUpDown.setSize(game.pixelXByCurrentSize(74), game.pixelYByCurrentSize(45));
		numUpDown.setPos(back.getX() - 90, game.pixelYByCurrentSize(325));
		textUpDown.setSize(game.pixelXByCurrentSize(104), game.pixelYByCurrentSize(45));
		textUpDown.setPos(play.getX() + play.getWidth() + 90 - textUpDown.getTotalWidth(), game.pixelYByCurrentSize(325));
		
		tmPlAmount.setPosAndSize(numUpDown.getX() + numUpDown.getTotalWidth() / 2 - 164 / 2, numUpDown.getY() + numUpDown.getHeight() + 10, 164);
		difficulty.setPosAndSize(textUpDown.getX() + textUpDown.getTotalWidth() / 2 - 164 / 2, textUpDown.getY() + textUpDown.getHeight() + 10, 164);
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

}
