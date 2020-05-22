package com.vasciie.bkbl.gui;

import com.badlogic.gdx.Application;
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

    Color renderColor;

    byte multitouch = -1;


    float r, g, b, a; // color variables
    float r1, g1, b1, a1;

    boolean mark;
    boolean filled;
    boolean toggle;
    boolean toggleFill;

    boolean touched;
    boolean toggled;
    boolean release;



    /**
     * Creates a basic button
     *
     * @param text
     * @param font
     * @param color
     * @param mark
     * @param filled
     */
    public Button(String text, BitmapFont font, Color color, boolean mark, boolean filled, GUIRenderer guiRenderer) {
        super(guiRenderer);

        this.font = font;
        this.text = text;
        if (!Gdx.app.getType().equals(Application.ApplicationType.Android))
            this.mark = mark;
        this.filled = filled;

        r = color.r;
        g = color.g;
        b = color.b;
        a = color.a;


    }

    /**
     * Creates a new toggle button
     *
     * @param text
     * @param font
     * @param color
     * @param fillColor
     * @param mark
     * @param filled
     * @param toggleFill
     */
    public Button(String text, BitmapFont font, Color color, Color fillColor, boolean mark, boolean filled, boolean toggleFill, GUIRenderer guiRenderer) {
        this(text, font, color, mark, filled, guiRenderer);

        toggle = true;
        this.toggleFill = toggleFill;

        this.r1 = fillColor.r;
        this.g1 = fillColor.g;
        this.b1 = fillColor.b;
        this.a1 = fillColor.a;

    }

    @Override
    public void update(){
        super.update();

        if(this instanceof CheckButton || this instanceof Stick)
            return;

        if (Gdx.app.getType().equals(Application.ApplicationType.Android) || isMouseOn()) {
            if (justLocalTouched() || isLocalTouched() || toggled)
                renderColor = markColorClick();
            else {
                multitouch = -1;
                if (mark)
                    renderColor = markColorMouse();
                else renderColor = new Color(r, g, b, a);
            }
        } else
            renderColor = new Color(r, g, b, a);
    }

    @Override
    public void draw(){
        if(!renderable){
            touched = release = false;
            multitouch = -1;
        }

        super.draw();
    }

    @Override
    public void render() {
        ShapeRenderer shape = guiRenderer.getShapeRenderer();
        SpriteBatch batch = guiRenderer.getSpriteBatch();
        OrthographicCamera cam = guiRenderer.getCam();

        Color tempColor = shape.getColor().cpy();

        shape.setColor(renderColor);
        shape.setProjectionMatrix(cam.combined);

        if (filled || toggleFill && toggled)
            shape.begin(ShapeRenderer.ShapeType.Filled);
        else shape.begin(ShapeRenderer.ShapeType.Line);

        shape.rect(x, y, width, height);
        shape.end();
        shape.setColor(tempColor);

        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        font.draw(batch, text, x + width / 2 - textSize(font, text) / 2, y + height / 2 + font.getLineHeight() / 3);

        batch.end();

        renderColor = null;
    }

    public boolean isMouseOn() {
        OrthographicCamera cam = guiRenderer.getCam();

        Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        cam.unproject(touchPos);

        return touchPos.x >= x && touchPos.x <= x + width && touchPos.y <= y + height && touchPos.y >= y;
    }

    public boolean isLocalTouched() {
        OrthographicCamera cam = guiRenderer.getCam();

        for (int i = 0; i < 3; i++) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(i), Gdx.input.getY(i), 0);
            cam.unproject(touchPos);

            touched = Gdx.input.isTouched(i) && touchPos.x >= x && touchPos.x <= x + width && touchPos.y <= y + height && touchPos.y >= y;

            if (touched) {
                release = true;
                multitouch = (byte) i;
                return true;
            }
        }

        multitouch = -1;
        return false;
    }

    public boolean isTouched() {
        return touched;
    }

    public boolean isTouchedCheck() {
        OrthographicCamera cam = guiRenderer.getCam();

        if (renderable) {
            for (int i = 0; i < 3; i++) {
                Vector3 touchPos = new Vector3(Gdx.input.getX(i), Gdx.input.getY(i), 0);
                cam.unproject(touchPos);

                touched = Gdx.input.isTouched(i) && touchPos.x >= x && touchPos.x <= x + width && touchPos.y <= y + height && touchPos.y >= y;

                if (touched) {
                    release = true;
                    multitouch = (byte) i;
                    return true;
                }
            }
        }

        multitouch = -1;
        return false;
    }

    public boolean justReleased() {
        if(!renderable)
            return false;

        if (release && !touched) {
            release = false;
            //multitouch = -1;
            return true;
        }

        return false;
    }

    public boolean justLocalTouched() {
        OrthographicCamera cam = guiRenderer.getCam();

        if (multitouch == -1) {
            for (int i = 0; i < 3; i++) {
                Vector3 touchPos = new Vector3(Gdx.input.getX(i), Gdx.input.getY(i), 0);
                cam.unproject(touchPos);

                boolean touch = Gdx.input.isTouched(i) && touchPos.x >= x && touchPos.x <= x + width && touchPos.y <= y + height && touchPos.y >= y;

                if (touch) {
                    if (toggle)
                        toggled = !toggled;

                    multitouch = (byte) i;
                    return true;
                }
            }
        }

        return false;
    }

    public boolean justTouched() {
        OrthographicCamera cam = guiRenderer.getCam();

        if (renderable) {
            if (Gdx.input.justTouched())
                for (int i = 0; i < 3; i++) {
                    Vector3 touchPos = new Vector3(Gdx.input.getX(i), Gdx.input.getY(i), 0);
                    cam.unproject(touchPos);

                    boolean touch = touchPos.x >= x && touchPos.x <= x + width && touchPos.y <= y + height && touchPos.y >= y;

                    if (touch) {
                        return true;
                    }
                }
        }
        return false;
    }

    public boolean isToggled(){
        return toggled;
    }

    public void setToggled(boolean toggled){
        this.toggled = toggled;
    }

    public void setRenderable(boolean renderable){
        this.renderable = renderable;
    }

    protected Color markColorClick() {
        if (toggled && toggleFill)
            return new Color(r1, g1, b1, a1);

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
    
    public float getTextSize() {
    	return textSize(font, text);
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

    public byte getMultitouch() {
        return multitouch;//This will be used to prevent from rotating the player by pressing buttons
    }

    @Override
    protected void onResize() {
        // TODO This method is used inside the Text class which should probably use the button and text class' variables x, y, width and height. I dunno. I just want to remind you about the thing you were about to do here.

    }
}
