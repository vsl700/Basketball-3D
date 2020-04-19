package com.vasciie.bkbl.gui;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class TextUpDown extends UpDown {
	
	Label label;
	
	ArrayList<String> options;
	
	public TextUpDown(BitmapFont btnFont, BitmapFont textFont, Color color, Color fillColor, Color textFillColor, ArrayList<String> options, boolean textShorten) {
		this.options = options;
		
		create(btnFont, fillColor);
		
		label = new Label(options.get(0), textFont, color, textFillColor, textShorten);
	}

	@Override
	public void render(SpriteBatch batch, ShapeRenderer shape, OrthographicCamera cam) {
		super.render(batch, shape, cam);
		
		if(down.justReleased() && num > 0) {
			num--;
			label.setText(options.get(num));
			
			sendSignalToListen();
		}
		else if(up.justReleased() && num < options.size() - 1) {
			num++;
			label.setText(options.get(num));
			
			sendSignalToListen();
		}
		
		label.render(batch, shape, cam);
	}
	
	@Override
	protected void onResize() {
		super.onResize();
		label.setPosAndSize(down.getX() + down.getWidth() + 10, y, width, height);
	}
	
	public void setTextOption(String s) {
		int n = options.indexOf(s);
		if(n > -1)
			setOption(n);
	}
	
	public String getTextOption() {
		return options.get(num);
	}
	
	@Override
	public void setOption(int i) {
		super.setOption(num);
		
		num = i;
		label.setText(options.get(i));
	}

}
