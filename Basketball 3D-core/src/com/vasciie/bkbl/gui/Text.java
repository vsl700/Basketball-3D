package com.vasciie.bkbl.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public abstract class Text extends GUI {
	
	TextChangeListener textChangeListener;
	
	protected BitmapFont font;
	
	protected String text;
	
	protected float r, g, b, a;

	public Text(GUIRenderer guiRenderer) {
		super(guiRenderer);
	}

	protected boolean containsDiffFromLetter(String text) {
		for(int i = 0; i < text.length(); i++) {
			if(!Character.isLetter(text.charAt(i)))
				return true;
		}
		
		return false;
	}
	
	protected boolean containsDiffFromNum(String text) {
		for(int i = 0; i < text.length(); i++) {
			if(!Character.isDigit(text.charAt(i)))
				return true;
		}
		
		return false;
	}
	
	public void setText(String text) {
		this.text = text;
		
		textChangeListener.onTextChanged(text);
	}
	
	public void setText(String text, boolean listen) {
		this.text = text;
		
		//textListener.onTextChanged(text);
		if(listen)
			textChangeListener.onTextChanged(text);
	}
	
	public void setTextChangeListener(TextChangeListener listener) {
		textChangeListener = listener;
	}
	
	public void setColor(Color color) {
		r = color.r;
		g = color.g;
		b = color.b;
		a = color.a;
	}
	
}
