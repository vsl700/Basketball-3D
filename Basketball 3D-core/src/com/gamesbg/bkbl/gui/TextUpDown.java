package com.gamesbg.bkbl.gui;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class TextUpDown extends UpDown {
	
	Label label;
	
	ArrayList<String> options;
	
	//int index;
	
	public TextUpDown(BitmapFont btnFont, BitmapFont textFont, Color color, Color fillColor, Color textFillColor, ArrayList<String> options, boolean textShorten) {
		this.options = options;
		
		create(btnFont, fillColor);
		
		label = new Label(options.get(0), textFont, color, textFillColor, textShorten);
	}

	@Override
	public void render(SpriteBatch batch, ShapeRenderer shape, OrthographicCamera cam) {
		// TODO Auto-generated method stub
		super.render(batch, shape, cam);
		label.render(batch, shape, cam);
		
		if(down.justReleased(cam) && num > 0) {
			num--;
			label.setText(options.get(num));
		}
		else if(up.justReleased(cam) && num < options.size() - 1) {
			num++;
			label.setText(options.get(num));
		}
	}
	
	protected void onResize() {
		super.onResize();
		label.setPosAndSize(x + 40, y, width, height);
	}
	
	public void setOption(int i) {
		num = i;
		label.setText(options.get(i));
	}

	@Override
	public void onTextChanged(String text) {
		// TODO Auto-generated method stub
		
	}

}
