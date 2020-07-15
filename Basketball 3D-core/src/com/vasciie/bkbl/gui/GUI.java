package com.vasciie.bkbl.gui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

public abstract class GUI {

	GUIRenderer guiRenderer;

	float x, y, width, height;
	boolean renderable;

	public GUI(GUIRenderer guiRenderer){
		this.guiRenderer = guiRenderer;
	}

	public void update(){
		renderable = true;
	}
	
	protected abstract void render();

	public void draw(){
		if(renderable) render();

		renderable = false;
	}
	
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

	public boolean isRenderable() {
		return renderable;
	}

	protected abstract void onResize();
}
