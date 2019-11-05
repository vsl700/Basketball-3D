package com.gamesbg.bkbl.gui;

public abstract class Text {
	
	//protected TextListener textListener;
	
	TextChangeListener textChangeListener;
	
	protected String text;
	
	protected float r, g, b, a;
	protected float x, y, width, height;
	
	abstract void onResize();
	
	public abstract void render();
	
	public void setText(String text) {
		this.text = text;
		
		//textListener.onTextChanged(text);
		textChangeListener.onTextChanged(text);
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
	
	public void setTextChangeListener(TextChangeListener listener) {
		textChangeListener = listener;
	}
	
}
