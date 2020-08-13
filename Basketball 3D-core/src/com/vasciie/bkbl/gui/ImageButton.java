package com.vasciie.bkbl.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ImageButton extends Button {

	Texture texture;
	
	public ImageButton(Color color, GUIRenderer guiRenderer, Texture texture) {
		super(null, null, color, false, true, guiRenderer);
		
		this.texture = texture;
		width = texture.getWidth();
		height = texture.getHeight();
	}
	
	public ImageButton(GUIRenderer guiRenderer, Texture texture) {
		this(null, guiRenderer, texture);
	}
	
	@Override
	public void update() {
		super.update();
		
		isLocalTouched();
	}
	
	@Override
	public void render() {
		SpriteBatch batch = guiRenderer.getSpriteBatch();
		OrthographicCamera cam = guiRenderer.getCam();
		
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		batch.draw(texture, x, y, width, height);
		batch.end();
	}
	
	public void disposeImage() {
		texture.dispose();
	}

}
