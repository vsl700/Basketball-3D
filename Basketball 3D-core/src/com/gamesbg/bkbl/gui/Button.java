package com.gamesbg.bkbl.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

public class Button extends GUI {
	String text;

	BitmapFont font;
	
	byte multitouch = 0;
	//int type; // 0 - normal, 1 - toggle, 2 - radio
	
	
	float r, g, b, a; // color variables

	boolean mark;
	boolean filled;

	boolean touched;
	//boolean toggled;
	boolean touchable;

	public Button(String text, BitmapFont font, Color color, boolean mark, boolean filled) {
		this.font = font;
		this.text = text;
		this.mark = mark;
		this.filled = filled;

		r = color.r;
		g = color.g;
		b = color.b;
		a = color.a;
	}

	@Override
	public void render(SpriteBatch batch, ShapeRenderer shape, OrthographicCamera cam) {
		touchable = true;

		shape.setProjectionMatrix(cam.combined);
		
		if(filled)
			shape.begin(ShapeRenderer.ShapeType.Filled);
		else shape.begin(ShapeRenderer.ShapeType.Line);

		float r1 = shape.getColor().r;
		float g1 = shape.getColor().g;
		float b1 = shape.getColor().b;
		float a1 = shape.getColor().a;

		if (isMouseOn(cam) && mark) {
			if (isLocalTouched(cam) || justLocalTouched(cam))
				shape.setColor(markColorClick());
			else
				shape.setColor(markColorMouse());
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

	public boolean isMouseOn(OrthographicCamera cam) {
		Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
		cam.unproject(touchPos);

		return touchPos.x >= x && touchPos.x <= x + width && touchPos.y <= y + height && touchPos.y >= y;
	}

	public boolean isLocalTouched(OrthographicCamera cam) {
		// if(type == 0)
		if (touchable) {
			// if(type == 0)
			for (int i = 0; i < 5; i++) {
				Vector3 touchPos = new Vector3(Gdx.input.getX(i), Gdx.input.getY(i), 0);
				cam.unproject(touchPos);

				if(!touched) //The justReleased method will not work if that if statement is not here.
				touched = Gdx.input.isTouched(i) && touchPos.x >= x && touchPos.x <= x + width && touchPos.y <= y + height && touchPos.y >= y;

				if (touched) {
					// touchable = false;
					multitouch = (byte) i;
					//if (type != 0 && !toggled)
						//toggled = true;
					return true;
				}
				// else if(type == 1){
				// touchable = false;

				// return toggled;
				// }
			}
			// else
			//if (type != 0)
				//return toggled;
		}
		// else if(type == 1){
		// if()
		// }

		return false;
	}

	public boolean isTouched(OrthographicCamera cam) {
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
					//if (type != 0 && !toggled)
						///toggled = true;
					return true;
				}
				// else if(type == 1){
				// touchable = false;

				// return toggled;
				// }
			}
			//if (type != 0)
				//return toggled;
		}
		// else if(type == 1){
		// if()
		// }

		return false;
	}

	public boolean justReleased(OrthographicCamera cam) {
		Vector3 touchPos = new Vector3(Gdx.input.getX(multitouch), Gdx.input.getY(multitouch), 0);
		cam.unproject(touchPos);
		//FIXME There is a problem between touched and justReleased (or touch methods) (create wasTouched boolean and use it)
		if (touched && !(Gdx.input.isTouched(multitouch) && touchPos.x >= x && touchPos.x <= x + width && touchPos.y <= y + height && touchPos.y >= y)) {
			touched = false;
			return true;
		}

		return false;
	}

	public boolean justLocalTouched(OrthographicCamera cam) {
		if (touchable) {
			for (int i = 0; i < 5; i++) {
				Vector3 touchPos = new Vector3(Gdx.input.getX(i), Gdx.input.getY(i), 0);
				cam.unproject(touchPos);

				boolean touch = Gdx.input.justTouched() && touchPos.x >= x && touchPos.x <= x + width && touchPos.y <= y + height && touchPos.y >= y;

				if (touch) {
					return true;
				}

				// if(touch)
				// return touch;
			}
		}
		return false;
	}

	public boolean justTouched(OrthographicCamera cam) {
		if (touchable) {
			touchable = false;
			for (int i = 0; i < 5; i++) {
				Vector3 touchPos = new Vector3(Gdx.input.getX(i), Gdx.input.getY(i), 0);
				cam.unproject(touchPos);

				boolean touch = Gdx.input.justTouched() && touchPos.x >= x && touchPos.x <= x + width && touchPos.y <= y + height && touchPos.y >= y;

				if (touch) {
					return true;
				}
			}
		}
		return false;
	}
	
	protected Color markColorClick() {
		return new Color(r - 0.3f, g - 0.3f, b - 0.3f, a);
	}
	
	protected Color markColorMouse() {
		return new Color(r + 0.3f, g + 0.3f, b + 0.3f, a);
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
	protected void onResize() {
		// TODO This method is used inside the Text class which should probably use the button and text class' variables x, y, width and height. I dunno. I just want to remind you about the thing you were about to do here.
		
	}
}
