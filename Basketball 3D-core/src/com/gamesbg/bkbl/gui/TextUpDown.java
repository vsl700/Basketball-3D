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
	
	public TextUpDown(BitmapFont btnFont, BitmapFont textFont, SpriteBatch batch, ShapeRenderer shape, Color color, OrthographicCamera cam, String[] options) {
		this.options = options;
		
		create(btnFont, textFont, batch, shape, color, cam);
		
		label = new Label(options[0], textFont, batch, shape, color, cam, false);
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub
		super.render();
		label.render();
		
		if(down.justReleased() && num > 0) {
			num--;
			label.setText(options[num]);
		}
		else if(up.justReleased() && num < options.length - 1) {
			num++;
			label.setText(options[num]);
		}
	}
	
	void resize() {
		super.resize();
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
