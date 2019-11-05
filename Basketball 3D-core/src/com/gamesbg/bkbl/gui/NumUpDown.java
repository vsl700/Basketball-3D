package com.gamesbg.bkbl.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class NumUpDown extends UpDown {

	/*Button down, up;*/
	TextPanel textPanel;

	int min, max;

	public NumUpDown(BitmapFont btnFont, BitmapFont textFont, SpriteBatch batch, ShapeRenderer shape, Color color, OrthographicCamera cam, int min, int max) {
		this.min = min;
		this.max = max;

		num = min;

		create(btnFont, textFont, batch, shape, color, cam);
		
		textPanel = new TextPanel(textFont, batch, shape, color, cam, min, max);
		textPanel.setTextChangeListener(this);
		textPanel.setText(num);
	}

	public void render() {
		super.render();
		textPanel.render();
		
		if (down.justReleased()) {
			//num = Integer.parseInt(textPanel.getText());
			
			if (num > min) {
				num--;
				textPanel.setText(num);
			}
		} else if (up.justReleased()) {
			//num = Integer.parseInt(textPanel.getText());
			
			if (num < max) {
				num++;
				textPanel.setText(num);
			}
		}

	}

	void resize() {
		/*down.setPosAndSize(x, y, 30, height);
		up.setPosAndSize(x + width + 50, y, 30, height);*/
		super.resize();
		textPanel.setPosAndSize(x + 40, y, width, height);
	}

	public void setOption(int num) {
		if (num < min || num > max)
			this.num = min;

		else
			this.num = num;

		textPanel.setText(this.num);
	}

	@Override
	public void onTextChanged(String text) {
		// TODO Auto-generated method stub
		num = Integer.parseInt(textPanel.getText());
	}

}
