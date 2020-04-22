package com.vasciie.bkbl.gui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public interface GUIRenderer {
    public SpriteBatch getSpriteBatch();

    public ShapeRenderer getShapeRenderer();

    public OrthographicCamera getCam();
}
