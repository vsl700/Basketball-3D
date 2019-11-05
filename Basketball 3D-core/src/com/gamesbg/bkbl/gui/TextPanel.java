package com.gamesbg.bkbl.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

public class TextPanel extends Text implements InputProcessor {

	BitmapFont font;
	OrthographicCamera cam;
	ShapeRenderer shape;
	SpriteBatch batch;

	//String text;
	String prevText;
	

	long time;

	//float r, g, b, a; // text color
	//float x, y, width, height;
	float textX, textY, textW, textH;
	
	int min, max;

	boolean active, cursor;

	public TextPanel(BitmapFont font, SpriteBatch batch, ShapeRenderer shape, Color color, OrthographicCamera cam) {
		/*this.font = font;
		this.batch = batch;
		this.shape = shape;
		this.cam = cam;

		r = color.r;
		g = color.g;
		b = color.b;
		a = color.a;

		text = "";
		
		time = System.currentTimeMillis();*/
		
		new TextPanel(font, batch, shape, color, cam, 0, 0);
	}
	
	public TextPanel(BitmapFont font, SpriteBatch batch, ShapeRenderer shape, Color color, OrthographicCamera cam, int min, int max) {
		this.font = font;
		this.batch = batch;
		this.shape = shape;
		this.cam = cam;
		this.min = min;
		this.max = max;

		r = color.r;
		g = color.g;
		b = color.b;
		a = color.a;

		text = "";
		prevText = "";
		
		time = System.currentTimeMillis();
		
		//textListener = new TextListener();
	}

	public void render() {
		// visible = true;

		if (justTouched())
			active = true;
		else if(justTouchedOut()) {
			deactive();
		}

		if (active) {
			if (System.currentTimeMillis() - time >= 500) {
				cursor = !cursor;
				time = System.currentTimeMillis();
			}
			
			Gdx.input.setInputProcessor(this);
		}

		shape.setProjectionMatrix(cam.combined);
		shape.begin(ShapeRenderer.ShapeType.Line);
		shape.rect(x, y, width, height);

		if (cursor) {
			shape.line(textX + textW + 10, textY, textX + textW + 10, textY - textH / 2 - 5);
		}

		shape.end();

		float r1 = font.getColor().r;
		float g1 = font.getColor().g;
		float b1 = font.getColor().b;
		float a1 = font.getColor().a;
		
		font.setColor(r, g, b, a);
		
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		font.draw(batch, text, textX, textY);
		batch.end();
		
		font.setColor(r1, g1, b1, a1);
	}

	void onResize() {
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
	void deactive() {
		active = false;
		cursor = false;
		
		if(min != 0 && max != 0 && text.equals("")) {
			text = prevText.toString();
			onResize();
		}
		
		else {
			prevText = text.toString();
			textChangeListener.onTextChanged(text);
			//textListener.onTextChanged(text);
		}
	}

	boolean justTouched() {
		// for (int i = 0; i < 5; i++) {
		Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
		cam.unproject(touchPos);

		return Gdx.input.justTouched() && touchPos.x >= x && touchPos.x <= x + width && touchPos.y <= y + height && touchPos.y >= y;
		// }
	}

	/**
	 * 
	 * @return true if the pointer touches outside of the panel
	 */
	boolean justTouchedOut() {
		Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
		cam.unproject(touchPos);

		return Gdx.input.justTouched() && (touchPos.x < x || touchPos.x > x + width || touchPos.y > y + height || touchPos.y < y);
	}

	public void setText(String text) {
		//this.text = text;
		super.setText(text);
		prevText = text.toString();
		
		onResize();
	}
	
	public void setText(int num) {
		/*this.text = Integer.toString(num);
		prevText = text.toString();
		
		onResize();*/
		setText(Integer.toString(num));
	}

	public void setMin(int min) {
		this.min = min;
	}

	public void setMax(int max) {
		this.max = max;
	}

	/*public void setX(float x) {
		this.x = x;
		
		onResize();
	}

	public void setY(float y) {
		this.y = y;
		
		onResize();
	}

	public void setWidth(float width) {
		this.width = width;
		
		onResize();
	}

	public void setHeight(float height) {
		this.height = height;
		
		onResize();
	}

	public void setPos(float x, float y) {
		this.x = x;
		this.y = y;
		
		onResize();
	}

	public void setSize(float width, float height) {
		this.width = width;
		this.height = height;
		
		onResize();
	}

	public void setPosAndSize(float x, float y, float w, float h) {
		this.x = x;
		this.y = y;
		width = w;
		height = h;

		onResize();
	}*/
	
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
		if((min != 0 || max != 0) && text == "") 
			return Integer.toString(min);
			
		return text;
	}

	@Override
	public boolean keyDown(int keycode) {
		String key = Keys.toString(keycode);
		if(active) {
			if(key.length() == 1) { //A letter or a number
				if(min != 0 || max != 0) {
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
