package com.vasciie.bkbl.gui;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class NumUpDown extends UpDown implements TextChangeListener {

	HashMap<Integer, String> exceptions;
	TextPanel textPanel;

	int min, max;
	int diff;

	public NumUpDown(BitmapFont btnFont, BitmapFont textFont, Color color, Color fillColor, Color textFillColor, int min, int max, GUIRenderer guiRenderer) {
		super(guiRenderer);
		this.min = min;
		this.max = max;

		num = min;

		create(btnFont, fillColor);

		textPanel = new TextPanel(textFont, color, textFillColor, min, max, guiRenderer);
		textPanel.setTextChangeListener(this);
		textPanel.setText(num);
		
		diff = 1;
	}
	
	public NumUpDown(BitmapFont btnFont, BitmapFont textFont, Color color, Color fillColor, Color textFillColor, int min, int max, int diff, GUIRenderer guiRenderer) {
		this(btnFont, textFont, color, fillColor, textFillColor, min, max, guiRenderer);
		
		this.diff = diff;
	}

	@Override
	public void update(){
		super.update();

		if (down.justReleased()) {

			if (num - diff >= min) {
				num-= diff;

				checkForExceptions(num);

				sendSignalToListen();
			}
		} else if (up.justReleased()) {

			if (num + diff <= max) {
				num+= diff;

				checkForExceptions(num);

				sendSignalToListen();
			}
		}

		textPanel.update();
	}

	@Override
	public void render() {
		super.render();
		textPanel.draw();
	}

	protected void onResize() {
		super.onResize();
		textPanel.setPosAndSize(down.getX() + down.getWidth() + 10, y, width, height);
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
