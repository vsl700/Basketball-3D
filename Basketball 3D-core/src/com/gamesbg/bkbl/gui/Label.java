package com.gamesbg.bkbl.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Label extends Text {
	
	float fR, fG, fB, fA;

	boolean multiline, textShorten;

	public Label(String text, BitmapFont font, Color color, boolean multiline) {
		this.text = text;
		this.font = font;
		this.multiline = multiline;

		r = color.r;
		g = color.g;
		b = color.b;
		a = color.a;
	}
	
	public Label(String text, BitmapFont font, Color color, Color fillColor, boolean textShorten) {
		this.text = text;
		this.font = font;
		this.textShorten = textShorten;

		r = color.r;
		g = color.g;
		b = color.b;
		a = color.a;
		
		fR = fillColor.r;
		fG = fillColor.g;
		fB = fillColor.b;
		fA = fillColor.a;
	}
	
	private void renderShapes(ShapeRenderer shape, OrthographicCamera cam) {
		if (shape != null) {
			float r1 = shape.getColor().r;
			float g1 = shape.getColor().g;
			float b1 = shape.getColor().b;
			float a1 = shape.getColor().a;
			
			shape.setColor(fR, fG, fB, fA);
			
			shape.setProjectionMatrix(cam.combined);
			shape.begin(ShapeRenderer.ShapeType.Filled);
			shape.rect(x, y, width, height);
			shape.end();

			shape.setColor(r1, g1, b1, a1);
		}
	}
	
	private void renderText(SpriteBatch batch, OrthographicCamera cam) {
		float r1 = font.getColor().r;
		float g1 = font.getColor().g;
		float b1 = font.getColor().b;
		float a1 = font.getColor().a;
		
		font.setColor(r, g, b, a);

		batch.setProjectionMatrix(cam.combined);
		batch.begin();

		if (multiline) {
			String[] rows = text.split("\n");
			
			for (int i = 0; i < rows.length; i++) {
				font.draw(batch, rows[i], x + width / 2 - textSize(font, rows[i]) / 2, y + (rows.length - i) * font.getLineHeight());
			}
		} else {
			float textW = textSize(font, text);
			if (textShorten) {
				if (textW >= width) { // If there is not enough space for the
										// text, it turns into a text with the
										// original text's first letters
					String[] words = text.split(" ");
					String temp = "";

					for (int i = 0; i < words.length; i++)
						temp += words[i].charAt(0);

					font.draw(batch, temp, x + width / 2 - textSize(font, temp) / 2, y + height / 2 + font.getLineHeight() / 2 - 10);
				} else
					font.draw(batch, text, x + width / 2 - textW / 2, y + height / 2 + font.getLineHeight() / 2 - 10);
			}
			
			else
				font.draw(batch, text, x + width / 2 - textW / 2, y + height / 2 + font.getLineHeight() / 2 - 10);
		}
		batch.end();

		font.setColor(r1, g1, b1, a1);
	}

	@Override
	public void render(SpriteBatch batch, ShapeRenderer shape, OrthographicCamera cam) {
		renderShapes(shape, cam);
		
		renderText(batch, cam);
	}

	protected void onResize() {
		StringBuilder sb = new StringBuilder();

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
