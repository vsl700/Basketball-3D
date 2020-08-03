package com.vasciie.bkbl.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * A box containing GUI elements inside! Needs to be updated, as well as the GUI elements, but their rendering is automatic!
 * @author studi
 *
 */
public class GUIBox extends GUI {
	
	GUI[] guis;
	float border;
	
	float minX, minY, maxX, maxY;

	public GUIBox(GUIRenderer guiRenderer, GUI[] guis, float border) {
		super(guiRenderer);
		
		this.guis = guis;
		this.border = border;
	}

	@Override
	protected void render() {
		ShapeRenderer shape = guiRenderer.getShapeRenderer();
        OrthographicCamera cam = guiRenderer.getCam();
        
        minX = guis[0].getX();
        minY = guis[0].getY();
    	maxX = guis[0].getX() + guis[0].getWidth();
    	maxY = guis[0].getY() + guis[0].getHeight();
        for(int i = 1; i < guis.length; i++) {
        	GUI gui = guis[i];
        	
        	if(!gui.isRenderable())
        		continue;
        	
			if(gui.getX() < minX)
				minX = gui.getX();
			else if(gui.getX() + gui.getWidth() > maxX) 
				maxX = gui.getX() + gui.getWidth();
			
			if(gui.getY() < minY)
				minY = gui.getY();
			else if(gui.getY() + gui.getHeight() > maxY) 
				maxY = gui.getY() + gui.getHeight();
		}
        
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(Color.ORANGE.cpy().sub(0, 0.3f, 0, 1));
        shape.setProjectionMatrix(cam.combined);
        shape.rect(minX - border, minY - border, maxX - minX + border * 2, maxY - minY + border * 2);
        shape.end();
        
        for(GUI gui : guis)
        	gui.draw();
	}
	
	@Override
	public float getWidth() {
		return maxX - minX + border * 2;
	}
	
	@Override
	public float getHeight() {
		return maxY - minY + border * 2;
	}

	@Override
	protected void onResize() {
		
	}

}
