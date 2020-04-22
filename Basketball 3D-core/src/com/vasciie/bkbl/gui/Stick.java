package com.vasciie.bkbl.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Stick extends Button {
    Button sprint;
    Vector2 stickPos;

    public Stick(Color color, GUIRenderer guiRenderer) {
        super("", null, color, false, false, guiRenderer);

        BitmapFont font = new BitmapFont();
        font.setColor(color);

        sprint = new Button("Shift", font, color, false, false, guiRenderer);
    }

    @Override
    public void update(){
        super.update();

        sprint.update();

        if(sprint.isTouched()){
            stickPos.set(x + width / 2, y + width);
            multitouch = -1;
        }
        else if(isLocalTouched()){
            float tX = getTouchX(multitouch);
            float tY = guiRenderer.getCam().viewportHeight - getTouchY(multitouch);
            /*float normalPosX = x + width - width / 2;
            float normalPosY = y + width - width / 2;

            float visibleX = x + width / 2;
            float visibleY = y + width / 2;

            if(Math.sqrt(tX*tX - visibleX*visibleX) > normalPosX)
                if(tX < visibleX)
                    tX = -normalPosX;
                else tX = normalPosX;

            if(Math.sqrt(tY*tY - visibleY*visibleY) > normalPosY)
                if(tY < visibleY)
                    tY = -normalPosY;
                else tY = normalPosY;*/

            stickPos.set(tX, tY);
            //System.out.println("STICK");
        }
        else{
            stickPos.set(x + width / 2, y + width / 2);
            multitouch = -1;
        }
    }

    @Override
    public void render(){
        OrthographicCamera cam = guiRenderer.getCam();
        ShapeRenderer shape = guiRenderer.getShapeRenderer();

        Color tempColor = shape.getColor().cpy();

        shape.setProjectionMatrix(cam.combined);
        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setColor(r, g, b, a);
        shape.circle(x + width / 2, y + width / 2, width / 2);

        //Stick
        shape.set(ShapeRenderer.ShapeType.Filled);
        shape.circle(stickPos.x, stickPos.y, width / 4);

        shape.end();
        shape.setColor(tempColor);

        sprint.render();
    }

    public byte getSprintMultitouch(){
        return sprint.getMultitouch();
    }

    @Override
    public void setSize(float w, float h){
        super.setSize(w, h);

        sprint.setSize(width / 2, width / 4);
    }

    @Override
    public void setPos(float x, float y){
        super.setPos(x, y);

        sprint.setPos(x + width / 2 - sprint.getWidth() / 2, y + width);

        stickPos = new Vector2(x + width / 2, y + width / 2);
    }

    public Vector2 getOffset(){
        return stickPos.cpy().sub(x + width / 2, y + width / 2).nor();
    }

    public boolean isSprintPressed(){
        return sprint.isTouched();
    }
}
