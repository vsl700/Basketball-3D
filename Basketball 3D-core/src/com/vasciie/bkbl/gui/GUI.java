package com.vasciie.bkbl.gui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public abstract class GUI {

	float x, y, width, height;
	
	public abstract void render(SpriteBatch batch, ShapeRenderer shape, OrthographicCamera cam);
	
	protected float textSize(BitmapFont font, String t) {
		float textW = 0;

		for (int i = 0; i < t.length(); i++) {
			char ch = t.charAt(i);
			if (ch != '\n') {
				textW += font.getData().getGlyph(ch).width * font.getScaleX();
			}
		}

		return textW;
	}
	
	public void setX(float x) {
		this.x = x;
		
		onResize();
	}

	public void setY(float y) {
		this.y = y;
		
		onResize();
	}

	public void setWidth(float width) {
		this.width = width;
		
		onResize();
	}

	public void setHeight(float height) {
		this.height = height;
		
		onResize();
	}

	public void setPos(float x, float y) {
		this.x = x;
		this.y = y;
		
		onResize();
	}

	public void setSize(float width, float height) {
		this.width = width;
		this.height = height;
		
		onResize();
	}
	
	public void setPosAndSize(float x, float y, float w, float h) {
		this.x = x;
		this.y = y;
		width = w;
		height = h;

		onResize();
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

	protected abstract void onResize();
}
