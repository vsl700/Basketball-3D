package com.gamesbg.bkbl.screens;

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
		
		numUpDown = new NumUpDown(btnFont, textFont, batch, shape, Color.WHITE, cam, 1, 5);
		numUpDown.setOption(3);
		
		textUpDown = new TextUpDown(btnFont, textFont, batch, shape, Color.WHITE, cam, new String[] {"Easy", "Hard", "Very Hard"});
		
		play = new Button("Play", btnFont, batch, shape, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), cam, true, true, 0);
		back = new Button("Go Back", btnFont, batch, shape, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), cam, true, true, 0);
		
		tmPlAmount = new Label("THE AMOUNT OF PLAYERS PER TEAM", btnFont, batch, Color.WHITE, cam, true);
		
		difficulty = new Label("THE DIFFICULTY OF THE GAME", btnFont, batch, Color.WHITE, cam, true);
	}
	
	@Override
	public void show() {

	}

	@Override
	public void render(float delta) {
		cam.update();
		
		numUpDown.render();
		textUpDown.render();
		play.render();
		back.render();
		
		tmPlAmount.render();
		difficulty.render();
		
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
