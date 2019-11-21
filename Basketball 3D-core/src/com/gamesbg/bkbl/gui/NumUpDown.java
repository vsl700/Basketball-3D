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

	public NumUpDown(BitmapFont btnFont, BitmapFont textFont, Color color, Color fillColor, Color textFillColor, int min, int max) {
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
			//num = Integer.parseInt(textPanel.getText());
			
			if (num > min) {
				num--;
				textPanel.setText(num);
			}
		} else if (up.justReleased(cam)) {
			//num = Integer.parseInt(textPanel.getText());
			
			if (num < max) {
				num++;
				textPanel.setText(num);
			}
		}

		textPanel.render(batch, shape, cam);
	}

	protected void onResize() {
		/*down.setPosAndSize(x, y, 30, height);
		up.setPosAndSize(x + width + 50, y, 30, height);*/
		super.onResize();
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
