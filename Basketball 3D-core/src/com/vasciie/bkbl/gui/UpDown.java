package com.vasciie.bkbl.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.vasciie.bkbl.MyGdxGame;

public abstract class UpDown extends GUI {
	
	UpDownListener listener;
	
	protected Button down, up;
	
	protected int num;

	public UpDown(GUIRenderer guiRenderer) {
		super(guiRenderer);
	}

	protected void create(BitmapFont font, Color color) {
		down = new Button("<", font, color, true, true, guiRenderer);
		up = new Button(">", font, color, true, true, guiRenderer);
	}
	
	public void setListener(UpDownListener listener) {
		this.listener = listener;
	}

	@Override
	public void update(){
		super.update();

		down.update();
		up.update();
	}

	@Override
	public void render() {
		down.render();
		up.render();
	}
	
	protected void onResize() {
		down.setPosAndSize(x, y, 30 * MyGdxGame.GUI_SCALE, height);
		up.setPosAndSize(x + width + 20 + 30 * MyGdxGame.GUI_SCALE, y, 30 * MyGdxGame.GUI_SCALE, height);
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
		return width + 20 + 2 * 30 * MyGdxGame.GUI_SCALE;
	}
	
}
