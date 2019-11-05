package com.gamesbg.bkbl.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Label extends Text {

	BitmapFont font;
	OrthographicCamera cam;
	ShapeRenderer shape;
	SpriteBatch batch;

	// String text;

	// float r, g, b, a; // text color
	// float x, y, width;

	boolean multiline;

	public Label(String text, BitmapFont font, SpriteBatch batch, Color color, OrthographicCamera cam, boolean multiline) {
		this.font = font;
		this.batch = batch;
		this.cam = cam;
		this.text = text;
		this.multiline = multiline;

		r = color.r;
		g = color.g;
		b = color.b;
		a = color.a;
	}

	public Label(String text, BitmapFont font, SpriteBatch batch, ShapeRenderer shape, Color color, OrthographicCamera cam, boolean multiline) {
		this.font = font;
		this.batch = batch;
		this.shape = shape;
		this.cam = cam;
		this.text = text;
		this.multiline = multiline;

		r = color.r;
		g = color.g;
		b = color.b;
		a = color.a;
		
		//new Label(text, font, batch, color, cam, multiline);
		//this.shape = shape;
	}

	public void render() {
		float r1 = font.getColor().r;
		float g1 = font.getColor().g;
		float b1 = font.getColor().b;
		float a1 = font.getColor().a;

		if (shape != null) {
			shape.setProjectionMatrix(cam.combined);
			shape.begin(ShapeRenderer.ShapeType.Line);
			shape.rect(x, y, width, height);
			shape.end();
		}

		font.setColor(r, g, b, a);

		batch.setProjectionMatrix(cam.combined);
		batch.begin();

		// font.draw(batch, ":", x, y); //Borders
		// font.draw(batch, ":", x + width, y);

		if (multiline) {
			String[] rows = text.split("\n");
			
			for (int i = 0; i < rows.length; i++) {
				font.draw(batch, rows[i], x + width / 2 - textSize(rows[i]) / 2, y + (rows.length - i) * font.getLineHeight());
			}
		}else {
			float textW = textSize(text);
			if(textW >= width) {
				String[] words = text.split(" ");
				String temp = "";
				
				for(int i = 0; i < words.length; i++)
					temp+= words[i].charAt(0);
				
				font.draw(batch, temp, x + width / 2 - textSize(temp) / 2, y + height / 2 + font.getLineHeight() / 2 - 10);
			}
			else
				font.draw(batch, text, x + width / 2 - textW / 2, y + height / 2 + font.getLineHeight() / 2 - 10);
		}
		batch.end();

		font.setColor(r1, g1, b1, a1);
	}

	float textSize(String t) {
		float textW = 0;

		for (int i = 0; i < t.length(); i++) {
			char ch = text.charAt(i);
			if (ch != '\n') {
				textW += font.getData().getGlyph(ch).width * font.getScaleX();
			}
		}

		return textW;
	}

	void onResize() {
		StringBuilder sb = new StringBuilder();

		// String[] words = text.split(" ");
		//TODO Try to use textX,Y,W,H variables in here with the abstract class
		float textW = 0;

		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);

			if (ch != '\n') {
				textW += font.getData().getGlyph(ch).width * font.getScaleX();
				sb.append(ch);

				if (textW > width && ch == ' ' && multiline) {
					sb.append('\n');
					textW = font.getData().getGlyph(ch).width * font.getScaleX();
				}
			}
		}

		text = sb.toString();
	}

	@Override
	public void setText(String text) {
		this.text = text;
		
		onResize();
	}

	public void setPosAndSize(float x, float y, float w) {
		this.x = x;
		this.y = y;

		width = w;

		onResize();
	}

}
