package com.vasciie.bkbl.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Label extends Text {
	
	float textWidth;
	float fR, fG, fB, fA;

	boolean multiline, textShorten, fill;

	public Label(String text, BitmapFont font, boolean multiline, GUIRenderer guiRenderer) {
		super(guiRenderer);

		this.text = text;
		this.font = font;
		this.multiline = multiline;
	}
	
	public Label(String text, BitmapFont font, Color color, boolean multiline, GUIRenderer guiRenderer) {
		this(text, font, multiline, guiRenderer);

		r = color.r;
		g = color.g;
		b = color.b;
		a = color.a;
	}
	
	public Label(String text, BitmapFont font, Color color, Color fillColor, boolean textShorten, GUIRenderer guiRenderer) {
		this(text, font, color, false, guiRenderer);

		this.textShorten = textShorten;
		
		fR = fillColor.r;
		fG = fillColor.g;
		fB = fillColor.b;
		fA = fillColor.a;
		
		fill = true;
	}
	
	private void renderShapes() {
		ShapeRenderer shape = guiRenderer.getShapeRenderer();

		if (shape != null && fill) {
			Color tempColor = shape.getColor().cpy();
			
			shape.setColor(fR, fG, fB, fA);
			
			shape.setProjectionMatrix(guiRenderer.getCam().combined);
			shape.begin(ShapeRenderer.ShapeType.Filled);
			shape.rect(x, y, width, height);
			shape.end();

			shape.setColor(tempColor);
		}
	}
	
	private void renderText() {
		SpriteBatch batch = guiRenderer.getSpriteBatch();

		float r1 = font.getColor().r;
		float g1 = font.getColor().g;
		float b1 = font.getColor().b;
		float a1 = font.getColor().a;
		
		font.setColor(r, g, b, a);

		batch.setProjectionMatrix(guiRenderer.getCam().combined);
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

					font.draw(batch, temp, x + width / 2 - textSize(font, temp) / 2, y + height / 2 + font.getLineHeight() / 2 - 5);
				} else
					font.draw(batch, text, x + width / 2 - textW / 2, y + height / 2 + font.getLineHeight() / 2 - 5);
			}
			
			else
				font.draw(batch, text, x + width / 2 - textW / 2, y + height / 2 + font.getLineHeight() / 2 - 5);
		}
		batch.end();

		font.setColor(r1, g1, b1, a1);
	}

	@Override
	public void render() {
		renderShapes();
		
		renderText();
	}

	protected void onResize() {
		StringBuilder sb = new StringBuilder();
		StringBuilder tempSb = new StringBuilder();

		float textW = 0;
		textWidth = 0;

		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);

			if (ch != '\n') {
				textW += font.getData().getGlyph(ch).width * font.getScaleX();
				textWidth += font.getData().getGlyph(ch).width * font.getScaleX();

				if (multiline) {
					if (ch == ' ') {
						if (textW > width) {
							sb.append('\n');
							textW = textSize(font, tempSb.toString());
						}

						sb.append(tempSb.toString());
						sb.append(ch);

						tempSb.delete(0, tempSb.length());
					} else
						tempSb.append(ch);
				}else sb.append(ch);
				
			}
		}

		if(tempSb.length() > 0)
			sb.append(tempSb.toString());
		
		text = sb.toString();
	}
	
	public float textSize() {
		return textWidth;
	}
	
	public float getRows() {
		return text.split("\n").length;
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
