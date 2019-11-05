package com.gamesbg.bkbl.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public abstract class UpDown implements TextChangeListener {
	
	protected Button down, up;
	//protected Text textPanel;
	
	protected int num;
	
	protected float x, y, width, height;
	
	protected void create(BitmapFont btnFont, BitmapFont textFont, SpriteBatch batch, ShapeRenderer shape, Color color, OrthographicCamera cam) {
		down = new Button("<", btnFont, batch, shape, color, cam, true, false, 0);
		up = new Button(">", btnFont, batch, shape, color, cam, true, false, 0);
	}
	
	public void render() {
		down.render();
		up.render();
		//textPanel.render();
	}
	
	void resize() {
		down.setPosAndSize(x, y, 30, height);
		up.setPosAndSize(x + width + 50, y, 30, height);
		//textPanel.setPosAndSize(x + 40, y, width, height);
	}
	
	public abstract void setOption(int i);
	
	public int getOption() {
		return num;
	}
	
	public void setX(float x) {
		this.x = x;

		resize();
	}

	public void setY(float y) {
		this.y = y;

		resize();
	}

	public void setWidth(float width) {
		this.width = width;

		resize();
	}

	public void setHeight(float height) {
		this.height = height;

		resize();
	}

	public void setPos(float x, float y) {
		this.x = x;
		this.y = y;

		resize();
	}

	public void setSize(float width, float height) {
		this.width = width;
		this.height = height;

		resize();
	}

	public void setPosAndSize(float x, float y, float w, float h) {
		this.x = x;
		this.y = y;
		width = w;
		height = h;

		resize();
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public float getTotalWidth() {
		return width + 80;
	}
	
}
