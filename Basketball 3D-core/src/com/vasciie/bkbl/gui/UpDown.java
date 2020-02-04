package com.vasciie.bkbl.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public abstract class UpDown extends GUI {
	
	UpDownListener listener;
	
	protected Button down, up;
	
	protected int num;
	
	protected void create(BitmapFont font, Color color) {
		down = new Button("<", font, color, true, true);
		up = new Button(">", font, color, true, true);
	}
	
	public void setListener(UpDownListener listener) {
		this.listener = listener;
	}
	
	@Override
	public void render(SpriteBatch batch, ShapeRenderer shape, OrthographicCamera cam) {
		down.render(batch, shape, cam);
		up.render(batch, shape, cam);
	}
	
	protected void onResize() {
		down.setPosAndSize(x, y, 30, height);
		up.setPosAndSize(x + width + 50, y, 30, height);
	}
	
	protected void sendSignalToListen() {
		if(listener != null)
			listener.onOptionChanged(this, num);
	}
	
	public void setOption(int i) {
		sendSignalToListen();
	}
	
	public int getOption() {
		return num;
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
