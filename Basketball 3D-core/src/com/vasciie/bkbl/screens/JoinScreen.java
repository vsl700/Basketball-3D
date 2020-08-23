package com.vasciie.bkbl.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.vasciie.bkbl.MyGdxGame;
import com.vasciie.bkbl.gui.Button;
import com.vasciie.bkbl.gui.GUIRenderer;
import com.vasciie.bkbl.gui.Label;
import com.vasciie.bkbl.gui.TextPanel;

public class JoinScreen implements Screen, GUIRenderer {

	MyGdxGame game;
	
	SpriteBatch batch;
	ShapeRenderer shape;
	OrthographicCamera cam;
	
	BitmapFont font, textFont;
	
	Label ipDesc;
	TextPanel ipPanel;
	Button join, goBack;
	
	
	public JoinScreen(MyGdxGame mg) {
		game = mg;
		
		cam = new OrthographicCamera();
		
		shape = new ShapeRenderer();
		batch = new SpriteBatch();
		
		font = new BitmapFont();
		font.getData().setScale(MyGdxGame.GUI_SCALE);
		
		textFont = new BitmapFont();
		textFont.getData().setScale(2 * MyGdxGame.GUI_SCALE);
		
		
		ipDesc = new Label("THE IP ADDRESS OF THE HOST OF THE SERVER!", font, Color.BROWN, true, this);
		
		ipPanel = new TextPanel(textFont, Color.WHITE, new Color().set(0.8f, 0.4f, 0, 1), this);
		
		join = new Button("Join!", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
		goBack = new Button("Go Back", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
	}
	
	@Override
	public void show() {
		

	}

	@Override
	public void render(float delta) {
		ipDesc.update();
		ipPanel.update();
		join.update();
		goBack.update();
		
		if(goBack.justReleased())
			game.setScreen(game.joinOrCreate);
		
		game.renderLogo(batch, cam);
		
		ipDesc.draw();
		ipPanel.draw();
		join.draw();
		goBack.draw();

	}

	@Override
	public void resize(int width, int height) {
		cam.setToOrtho(false, width, height);
		
		ipPanel.setSize(300 * MyGdxGame.GUI_SCALE, 45 * MyGdxGame.GUI_SCALE);
		ipPanel.setPos(width / 2 - ipPanel.getWidth() / 2, height / 2);
		
		ipDesc.setWidth(ipPanel.getWidth());
		ipDesc.setPos(width / 2 - ipDesc.getWidth() / 2, ipPanel.getY() + ipPanel.getHeight() + 13 * ipDesc.getRows() / 2);

		join.setSize(game.pixelXByCurrentSize(223 * MyGdxGame.GUI_SCALE), game.pixelYByCurrentSize(30 * MyGdxGame.GUI_SCALE));
		join.setPos(width / 2 - join.getWidth() / 2, ipPanel.getY() / 2);
		
		goBack.setSize(join.getWidth(), join.getHeight());
		goBack.setPos(join.getX(), join.getY() - 13 - goBack.getHeight());
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
