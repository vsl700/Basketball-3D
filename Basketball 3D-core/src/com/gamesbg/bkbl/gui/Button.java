package com.gamesbg.bkbl.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

public class Button extends Text {
	String text;
	BitmapFont font;
	// Color color;
	OrthographicCamera cam;
	ShapeRenderer shape;
	SpriteBatch batch;

	byte multitouch = 0;
	int type; // 0 - normal, 1 - toggle, 2 - radio
	float x;
	float y;
	float width;
	float height;

	float r, g, b, a; // color variables

	boolean mark;
	boolean filled;

	boolean touched;
	boolean toggled;
	boolean touchable;

	public Button(String text, BitmapFont font, SpriteBatch batch, ShapeRenderer shape, Color color, OrthographicCamera cam, boolean mark, boolean filled, int type) {
		this.text = text;
		this.font = font;
		this.mark = mark;
		this.filled = filled;
		this.type = type;
		this.cam = cam;
		this.shape = shape;
		this.batch = batch;

		r = color.r;
		g = color.g;
		b = color.b;
		a = color.a;
	}

	public void render() {
		touchable = true;

		shape.setProjectionMatrix(cam.combined);
		
		if(filled)
			shape.begin(ShapeRenderer.ShapeType.Filled);
		else shape.begin(ShapeRenderer.ShapeType.Line);

		float r1 = shape.getColor().r;
		float g1 = shape.getColor().g;
		float b1 = shape.getColor().b;
		float a1 = shape.getColor().a;

		if (isMouseOn()) {
			if ((isLocalTouched() || justLocalTouched() || toggled) && mark)
				shape.setColor(r - 0.3f, g - 0.3f, b - 0.3f, a);
			else
				shape.setColor(r + 0.3f, g + 0.3f, b + 0.3f, a);
		} else
			shape.setColor(r, g, b, a);

		shape.rect(x, y, width, height);
		shape.setColor(r1, g1, b1, a1);
		shape.end();

		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		font.draw(batch, text, x + width / 2 - font.getSpaceWidth() * 1.5f * text.length(), y + height / 2 + font.getLineHeight() / 3); // x
																																		// +
																																		// width
																																		// -
																																		// getSW
																																		// *5
																																		// (old
																																		// one)

		//font.draw(batch, text, textX, textY);
		
		batch.end();
	}

	public boolean isMouseOn() {
		Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
		cam.unproject(touchPos);

		return touchPos.x >= x && touchPos.x <= x + width && touchPos.y <= y + height && touchPos.y >= y;
	}

	public boolean isLocalTouched() {
		// if(type == 0)
		if (touchable) {
			// if(type == 0)
			for (int i = 0; i < 5; i++) {
				Vector3 touchPos = new Vector3(Gdx.input.getX(i), Gdx.input.getY(i), 0);
				cam.unproject(touchPos);

				if(!touched)
				touched = Gdx.input.isTouched(i) && touchPos.x >= x && touchPos.x <= x + width && touchPos.y <= y + height && touchPos.y >= y;

				if (touched) {
					// touchable = false;
					multitouch = (byte) i;
					if (type != 0 && !toggled)
						toggled = true;
					return true;
				}
				// else if(type == 1){
				// touchable = false;

				// return toggled;
				// }
			}
			// else
			if (type != 0)
				return toggled;
		}
		// else if(type == 1){
		// if()
		// }

		return false;
	}

	public boolean isTouched() {
		// if(type == 0)
		if (touchable) {
			touchable = false;
			// if(type == 0)
			for (int i = 0; i < 5; i++) {
				Vector3 touchPos = new Vector3(Gdx.input.getX(i), Gdx.input.getY(i), 0);
				cam.unproject(touchPos);

				touched = Gdx.input.isTouched(i) && touchPos.x >= x && touchPos.x <= x + width && touchPos.y <= y + height && touchPos.y >= y;
				// if(!local)
				// touchable = false;

				if (touched) {
					// touchable = false;
					multitouch = (byte) i;
					if (type != 0 && !toggled)
						toggled = true;
					return true;
				}
				// else if(type == 1){
				// touchable = false;

				// return toggled;
				// }
			}
			if (type != 0)
				return toggled;
		}
		// else if(type == 1){
		// if()
		// }

		return false;
	}

	public boolean justReleased() {
		Vector3 touchPos = new Vector3(Gdx.input.getX(multitouch), Gdx.input.getY(multitouch), 0);
		cam.unproject(touchPos);
		//FIXME There is a problem between touched and justReleased (or touch methods) (create wasTouched boolean and use it)
		if (touched && !(Gdx.input.isTouched(multitouch) && touchPos.x >= x && touchPos.x <= x + width && touchPos.y <= y + height && touchPos.y >= y)) {
			touched = false;
			return true;
		}

		return false;
	}

	public boolean justLocalTouched() {
		if (touchable) {
			for (int i = 0; i < 5; i++) {
				Vector3 touchPos = new Vector3(Gdx.input.getX(i), Gdx.input.getY(i), 0);
				cam.unproject(touchPos);

				boolean touch = Gdx.input.justTouched() && touchPos.x >= x && touchPos.x <= x + width && touchPos.y <= y + height && touchPos.y >= y;

				if (touch) {
					switch (type) {
					case 0:
						return true;
					case 1:
						toggled = !toggled;
						break;
					case 2:
						if (!toggled)
							toggled = true;
						break;
					}
					break;
				}

				// if(touch)
				// return touch;
			}
		}
		return false;
	}

	public boolean justTouched() {
		if (touchable) {
			touchable = false;
			for (int i = 0; i < 5; i++) {
				Vector3 touchPos = new Vector3(Gdx.input.getX(i), Gdx.input.getY(i), 0);
				cam.unproject(touchPos);

				boolean touch = Gdx.input.justTouched() && touchPos.x >= x && touchPos.x <= x + width && touchPos.y <= y + height && touchPos.y >= y;

				if (touch) {
					switch (type) {
					case 0:
						return true;
					case 1:
						toggled = !toggled;
						break;
					case 2:
						if (!toggled)
							toggled = true;
						break;
					}
					break;
				}
			}
		}
		return false;
	}

	public void setToggled(boolean b) {
		toggled = b;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public void setPos(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void setSize(float width, float height) {
		this.width = width;
		this.height = height;
	}

	public void setPosAndSize(float x, float y, float w, float h) {
		setPos(x, y);
		setSize(w, h);
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

	public float getTouchX() {
		return Gdx.input.getX();
	}

	public float getTouchX(int i) {
		return Gdx.input.getX(i);
	}

	public float getTouchY() {
		return Gdx.input.getY();
	}

	public float getTouchY(int i) {
		return Gdx.input.getY(i);
	}

	@Override
	void onResize() {
		// TODO This method is used inside the Text class which should probably use the button and text class' variables x, y, width and height. I dunno. I just want to remind you about the thing you were about to do here.
		
	}
}
