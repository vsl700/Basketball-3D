package com.gamesbg.bkbl.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class CheckButton extends Button {
	
	boolean toggled;
	
	public CheckButton(String text, BitmapFont font, Color color, boolean mark, boolean filled) {
		super(text, font, color, mark, filled);
	}
	
	private void renderShapes(ShapeRenderer shape, OrthographicCamera cam) {
		shape.setProjectionMatrix(cam.combined);
		
		if(filled)
			shape.begin(ShapeRenderer.ShapeType.Filled);
		else shape.begin(ShapeRenderer.ShapeType.Line);

		float r1 = shape.getColor().r;
		float g1 = shape.getColor().g;
		float b1 = shape.getColor().b;
		float a1 = shape.getColor().a;
		
		font.setColor(shape.getColor().cpy());
		
		if(isMouseOn(cam) && mark) {
			 shape.setColor(markColorMouse());
		}
		else shape.setColor(r, g, b, a);
		
		shape.rect(x, y, width, height);
		
		if(toggled) {
			if(filled) {
				Color temp = shape.getColor();
				shape.setColor(1 - temp.r, 1 - temp.g, 1 - temp.b, 1 - temp.a);
			}
			shape.setAutoShapeType(true);
			shape.set(ShapeRenderer.ShapeType.Filled);
			
			float markScale = 20;
			shape.rect(x + width / 2 - markScale / 2, y + height / 2 - markScale / 2, markScale, markScale);
		}
		shape.end();
		shape.setColor(r1, g1, b1, a1);
	}
	
	private void renderText(SpriteBatch batch, OrthographicCamera cam) {
		float r2 = font.getColor().r;
		float g2 = font.getColor().g;
		float b2 = font.getColor().b;
		float a2 = font.getColor().a;
		
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		font.draw(batch, text, x + width + 13, y + height / 2 + font.getLineHeight() / 3);
		batch.end();
		
		font.setColor(r2, g2, b2, a2);
	}
	
	@Override
	public void render(SpriteBatch batch, ShapeRenderer shape, OrthographicCamera cam) {
		touchable = true;
		
		if(justLocalTouched(cam))
			toggled = !toggled;
		
		renderShapes(shape, cam);
		renderText(batch, cam);
		
	}
	
	public boolean isToggled() {
		return toggled;
	}

}
