package com.vasciie.bkbl.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class CheckButton extends Button {

	Color toggleColor;

	private static final float textFromBtnSpace = 13;
	
	boolean toggled;
	
	public CheckButton(String text, BitmapFont font, Color color, boolean mark, boolean filled, GUIRenderer guiRenderer) {
		super(text, font, color, mark, filled, guiRenderer);
	}

	@Override
	public void update(){
		super.update();

		if(isMouseOn() && mark) {
			renderColor = markColorMouse();
		}
		else renderColor = new Color(r, g, b, a);

		if(justLocalTouched())
			toggled = !toggled;

		if(!isLocalTouched())
			multitouch = -1;

		if(toggled && filled)
			toggleColor = new Color(1, 1, 1, 1).sub(renderColor);
	}
	
	private void renderShapes() {
		ShapeRenderer shape = guiRenderer.getShapeRenderer();

		shape.setProjectionMatrix(guiRenderer.getCam().combined);
		
		if(filled)
			shape.begin(ShapeRenderer.ShapeType.Filled);
		else shape.begin(ShapeRenderer.ShapeType.Line);

		Color tempColor = shape.getColor().cpy();
		
		//font.setColor(shape.getColor().cpy());

		shape.setColor(renderColor);
		shape.rect(x, y, width, height);
		
		if(toggled) {
			if(filled)
				shape.setColor(toggleColor);

			shape.set(ShapeRenderer.ShapeType.Filled);
			
			float markScale = 20;
			shape.rect(x + width / 2 - markScale / 2, y + height / 2 - markScale / 2, markScale, markScale);
		}
		shape.end();
		shape.setColor(tempColor);

		renderColor = toggleColor = null;
	}
	
	private void renderText() {
		SpriteBatch batch = guiRenderer.getSpriteBatch();

		/*float r2 = font.getColor().r;
		float g2 = font.getColor().g;
		float b2 = font.getColor().b;
		float a2 = font.getColor().a;*/
		
		batch.setProjectionMatrix(guiRenderer.getCam().combined);
		batch.begin();
		font.draw(batch, text, x + width + textFromBtnSpace, y + height / 2 + font.getLineHeight() / 3);
		batch.end();
		
		//font.setColor(r2, g2, b2, a2);
	}
	
	@Override
	public void render() {
		renderShapes();
		renderText();
	}
	
	public void setToggled(boolean toggled) {
		this.toggled = toggled;
	}
	
	public boolean isToggled() {
		return toggled;
	}
	
	public float getTotalWidth() {
		return width + textFromBtnSpace + textSize(font, text);
	}

}
