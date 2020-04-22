package com.vasciie.bkbl.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

/**
 * Represents an editable text panel in which you can edit text by methods or keyboard. It can be also set to a numeric text panel
 * so that it only writes and allows writing numbers. Useful function when using numeric-up-down.
 * 
 * @author studi
 *
 */
public class TextPanel extends Text implements InputProcessor {

	String prevText;
	

	long time;
	float textX, textY, textW, textH;
	
	float fR, fG, fB, fA; //Filling colors
	
	int min, max;

	boolean active, cursor, locked;

	public TextPanel(BitmapFont font, Color color, Color fillColor, GUIRenderer guiRenderer) {
		super(guiRenderer);
		this.font = font;

		r = color.r;
		g = color.g;
		b = color.b;
		a = color.a;
		
		fR = fillColor.r;
		fG = fillColor.g;
		fB = fillColor.b;
		fA = fillColor.a;

		text = "";
		prevText = "";
		
		time = System.currentTimeMillis();
	}
	
	public TextPanel(BitmapFont font, Color color, Color fillColor, int min, int max, GUIRenderer guiRenderer) {
		super(guiRenderer);
		this.min = min;
		this.max = max;
		this.font = font;

		r = color.r;
		g = color.g;
		b = color.b;
		a = color.a;
		
		fR = fillColor.r;
		fG = fillColor.g;
		fB = fillColor.b;
		fA = fillColor.a;

		text = "";
		prevText = "";
		
		time = System.currentTimeMillis();
	}

	@Override
	public void update(){
		super.update();

		if (justTouched() && !locked) {
			active = true;

			Gdx.input.setInputProcessor(this);
		}
		else if(justTouchedOut()) {
			deactive();
		}

		if (active) {
			if (System.currentTimeMillis() - time >= 500) {
				cursor = !cursor;
				time = System.currentTimeMillis();
			}
		}
	}
	
	private void renderShapes() {
		ShapeRenderer shape = guiRenderer.getShapeRenderer();

		float r1 = shape.getColor().r;
		float g1 = shape.getColor().g;
		float b1 = shape.getColor().b;
		float a1 = shape.getColor().a;
		
		shape.setColor(fR, fG, fB, fA);
		
		shape.setProjectionMatrix(guiRenderer.getCam().combined);
		shape.begin(ShapeRenderer.ShapeType.Filled);
		shape.rect(x, y, width, height);

		if (cursor) {
			shape.setColor(1 - fR, 1 - fG, 1 - fB, 1 - fA);
			shape.line(textX + textW + 10, textY, textX + textW + 10, textY - textH / 2 - 5);
		}

		shape.end();
		
		shape.setColor(r1, g1, b1, a1);
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
		font.draw(batch, text, textX, textY);
		batch.end();
		
		font.setColor(r1, g1, b1, a1);
	}
	
	@Override
	public void render() {
		renderShapes();

		renderText();
	}

	protected void onResize() {
		textW = 0;

		for (int i = 0; i < text.length(); i++) {
			textW += font.getData().getGlyph(text.charAt(i)).width * font.getScaleX();
		}

		textH = font.getLineHeight();

		textX = x + width / 2 - textW / 2;
		textY = y + height / 2 + textH / 2 - 10;
	}
	
	/**
	 *  Must be called when a deactivation command is executed
	 */
	private void deactive() {
		if (active) {
			active = false;
			cursor = false;

			if ((min != 0 || max != 0) && text.equals("")) {
				text = prevText.toString();
				onResize();
			}

			else {
				prevText = text.toString();
				textChangeListener.onTextChanged(text);
			}
		}
	}

	private boolean justTouched() {
		OrthographicCamera cam = guiRenderer.getCam();

		Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
		cam.unproject(touchPos);

		return Gdx.input.justTouched() && touchPos.x >= x && touchPos.x <= x + width && touchPos.y <= y + height && touchPos.y >= y;
	}

	/**
	 * 
	 * @return true if the pointer touches outside of the panel
	 */
	private boolean justTouchedOut() {
		OrthographicCamera cam = guiRenderer.getCam();

		Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
		cam.unproject(touchPos);

		return Gdx.input.justTouched() && (touchPos.x < x || touchPos.x > x + width || touchPos.y > y + height || touchPos.y < y);
	}
	
	private boolean isNumeric() {
		return min != 0 || max != 0;
	}
	
	public void setText(String text, boolean listen) {
		
		super.setText(text, listen);
		prevText = text.toString();
		
		onResize();
		
		if(isNumeric() && containsDiffFromNum(text))
			locked = true;
		else locked = false;
	}

	public void setText(String text) {
		
		super.setText(text);
		prevText = text.toString();
		
		onResize();
		
		if(isNumeric() && containsDiffFromNum(text))
			locked = true;
		else locked = false;
	}
	
	public void setText(int num) {
		setText(Integer.toString(num), false);
	}

	public void setMin(int min) {
		this.min = min;
	}

	public void setMax(int max) {
		this.max = max;
	}
	
	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public String getText() {
		if(isNumeric() && text == "") 
			return Integer.toString(min);
			
		return text;
	}

	@Override
	public boolean keyDown(int keycode) {
		String key = Keys.toString(keycode);
		if(active) {
			if(key.length() == 1) { //A letter or a number
				if(isNumeric()) {
					if(Character.isDigit(key.toCharArray()[0]) && Integer.parseInt(text + key) <= max && Integer.parseInt(text + key) >= min)
						text += key;
				}
				else {
					text += key;
				}
				
				onResize();
			}
			else if(keycode == 67 && text.length() > 0) { //Backspace
				StringBuilder strB = new StringBuilder();
				strB.append(text);
				strB.deleteCharAt(strB.length() - 1);
				text = strB.toString();
				
				onResize();
			}else if(keycode == 66) { //Enter
				deactive();
			}
				
		}
		
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
