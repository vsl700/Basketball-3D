package com.vasciie.bkbl.gui;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class NumUpDown extends UpDown implements TextChangeListener {

	HashMap<Integer, String> exceptions;
	TextPanel textPanel;

	int min, max;
	int diff;

	public NumUpDown(BitmapFont btnFont, BitmapFont textFont, Color color, Color fillColor, Color textFillColor, int min, int max) {
		regularConstructor(btnFont, textFont, color, fillColor, textFillColor, min, max);
		
		this.diff = 1;
	}
	
	public NumUpDown(BitmapFont btnFont, BitmapFont textFont, Color color, Color fillColor, Color textFillColor, int min, int max, int diff) {
		regularConstructor(btnFont, textFont, color, fillColor, textFillColor, min, max);
		
		this.diff = diff;
	}
	
	private void regularConstructor(BitmapFont btnFont, BitmapFont textFont, Color color, Color fillColor, Color textFillColor, int min, int max) {
		this.min = min;
		this.max = max;

		num = min;

		create(btnFont, fillColor);
		
		textPanel = new TextPanel(textFont, color, textFillColor, min, max);
		textPanel.setTextChangeListener(this);
		textPanel.setText(num);
	}

	@Override
	public void render(SpriteBatch batch, ShapeRenderer shape, OrthographicCamera cam) {
		super.render(batch, shape, cam);
		
		if (down.justReleased(cam)) {
			
			if (num - diff >= min) {
				num-= diff;
				
				checkForExceptions(num);
				
				sendSignalToListen();
			}
		} else if (up.justReleased(cam)) {
			
			if (num + diff <= max) {
				num+= diff;
				
				checkForExceptions(num);
				
				sendSignalToListen();
			}
		}

		textPanel.render(batch, shape, cam);
	}

	protected void onResize() {
		super.onResize();
		textPanel.setPosAndSize(x + 40, y, width, height);
	}

	public void setOption(int num) {
		if (num < min)
			this.num = min;
		else if(num > max)
			this.num = max;
		else
			this.num = num;
		
		checkForExceptions(num);
		
		super.setOption(num);
	}
	
	public void addException(int num, String s) {
		if(exceptions == null)
			exceptions = new HashMap<Integer, String>();
		
		exceptions.put(num, s);
	}
	
	private void checkForExceptions(int num) {
		if(exceptions != null && exceptions.containsKey(this.num))
			textPanel.setText(exceptions.get(this.num), false);
		else
			textPanel.setText(this.num);
	}

	@Override
	public void onTextChanged(String text) {
		num = Integer.parseInt(textPanel.getText());
		
		sendSignalToListen();
		
		checkForExceptions(num);
	}

}
