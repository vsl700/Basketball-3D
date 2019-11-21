package com.gamesbg.bkbl.gui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

public abstract class Text extends GUI {
	
	//protected TextListener textListener;
	
	TextChangeListener textChangeListener;
	
	protected BitmapFont font;
	
	protected String text;
	
	protected float r, g, b, a;
	//protected float x, y, width, height;
	
	public void setText(String text) {
		this.text = text;
		
		//textListener.onTextChanged(text);
		textChangeListener.onTextChanged(text);
	}
	
	public void setTextChangeListener(TextChangeListener listener) {
		textChangeListener = listener;
	}
	
}
