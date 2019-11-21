package com.gamesbg.bkbl.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class TextUpDown extends UpDown {
	
	Label label;
	
	String[] options;
	
	//int index;
	
	public TextUpDown(BitmapFont btnFont, BitmapFont textFont, Color color, Color fillColor, Color textFillColor, String[] options, boolean textShorten) {
		this.options = options;
		
		create(btnFont, fillColor);
		
		label = new Label(options[0], textFont, color, textFillColor, textShorten);
	}

	@Override
	public void render(SpriteBatch batch, ShapeRenderer shape, OrthographicCamera cam) {
		// TODO Auto-generated method stub
		super.render(batch, shape, cam);
		label.render(batch, shape, cam);
		
		if(down.justReleased(cam) && num > 0) {
			num--;
			label.setText(options[num]);
		}
		else if(up.justReleased(cam) && num < options.length - 1) {
			num++;
			label.setText(options[num]);
		}
	}
	
	protected void onResize() {
		super.onResize();
		label.setPosAndSize(x + 40, y, width, height);
	}
	
	public void setOption(int i) {
		num = i;
		label.setText(options[i]);
	}

	@Override
	public void onTextChanged(String text) {
		// TODO Auto-generated method stub
		
	}

}
