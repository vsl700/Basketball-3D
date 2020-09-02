package com.vasciie.bkbl.gamespace.tools;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector3;
import com.vasciie.bkbl.gamespace.GameMap;
import com.vasciie.bkbl.gui.Button;
import com.vasciie.bkbl.gui.GUIRenderer;
import com.vasciie.bkbl.gui.Stick;
import com.vasciie.bkbl.screens.SettingsScreen;

public class InputController implements InputProcessor {

    static final int forward = Keys.W, backward = Keys.S, strafeRight = Keys.D, strafeLeft = Keys.A;
    static final int sprint = Keys.SHIFT_LEFT;
    static final int shoot = Keys.CONTROL_LEFT;
    static final int focus = Keys.SPACE;
    static final int dribbleL = Buttons.LEFT, dribbleR = Buttons.RIGHT;

    //Alternative keys
    static final int altDribbleL = Keys.J, altDribbleR = Keys.L;
    static final int altScrollUp = Keys.E, altScrollDown = Keys.Q;

    int scroll = 0;
    float timeout;

    boolean forwardPressed, backwardPressed, strRightPressed, strLeftPressed;
    boolean sprintPressed;
    boolean shootPressed;
    boolean focusPressed;
    boolean dribbleLPressed, dribbleRPressed;
    boolean scrollUpPressed, scrollDownPressed;

    Stick movementStick;
    Button l, r;
    Button shootBtn, focusBtn;

    float dX, dY;
    int multitouch = -1;
    boolean dribble;
    
    
    /**
     * DO NOT USE IT! IT"S JUST FOR THE MULTIPLAYER!
     */
    public InputController() {
    	
    }

    public InputController(GUIRenderer guiRenderer) {
        if (Gdx.app.getType().equals(Application.ApplicationType.Android)) {
            movementStick = new Stick(Color.BLACK, guiRenderer);
            movementStick.setSize(300, 300);
            movementStick.setPos(100, 70);

            BitmapFont font = new BitmapFont();
            font.getData().setScale(1);
            font.setColor(Color.BLACK);

            //In order to make a pause button create and set it up above the dribble ones as their positions and sizes will depend on it

            l = new Button("L", font, Color.BLACK, false, false, guiRenderer);
            r = new Button("R", font, Color.BLACK, false, false, guiRenderer);

            float width = Gdx.graphics.getWidth(), height = Gdx.graphics.getHeight();
            float buttonSpace = 20;
            r.setSize(width / 4 - buttonSpace, 220 * height / 1280);
            r.setPos(width - r.getWidth(), height - r.getHeight());

            l.setSize(r.getWidth(), r.getHeight());
            l.setPos(r.getX() - l.getWidth() - buttonSpace, r.getY());

            shootBtn = new Button("Shoot", font, Color.BLACK, Color.BLACK, false, false, true, guiRenderer);
            focusBtn = new Button("Focus", font, Color.BLACK, Color.BLACK, false, false, true, guiRenderer);

            shootBtn.setSize(120, 80);
            shootBtn.setPos(width - shootBtn.getWidth() * 1.5f, height / 2 - shootBtn.getHeight() / 2);

            focusBtn.setSize(shootBtn.getWidth(), shootBtn.getHeight());
            focusBtn.setPos(shootBtn.getX(), shootBtn.getY() + focusBtn.getHeight() * 2);
        }
    }

    /**
     * Resets the scroll amount and the dribble (and ball pointing) buttons indicators
     *
     * @param resetDribble - whether to reset the ball dribble indicators
     */
    public void update(boolean resetDribble) {
        if (resetDribble) {
            dribbleLPressed = false;
            dribbleRPressed = false;
        }

        scroll = 0;

        if (scrollUpPressed || scrollDownPressed)
            timeout -= Gdx.graphics.getDeltaTime();
        else {
            return;
        }

        if (timeout <= 0) {
            if (scrollUpPressed) {
                scroll = 1;
                timeout = .1f;
            } else if (scrollDownPressed) {
                scroll = -1;
                timeout = .1f;
            } else timeout = 0;
        }
    }

    public void update(GameMap map){
        movementStick.update();

        l.update();
        r.update();

        if (map.getMainPlayer().isHoldingBall()) {
            shootBtn.update();
            if(map.getTeammates().size() > 1)
                focusBtn.update();

            shootPressed = shootBtn.isToggled();
            focusPressed = focusBtn.isToggled();

            if (dribble && !map.getMainPlayer().isDribbling()) {
                dribbleLPressed = l.isTouchedCheck();
                dribbleRPressed = r.isTouchedCheck();

                if(dribbleLPressed || dribbleRPressed)
                    dribble = false;
            } else {
                if (!l.isTouchedCheck() && !r.isTouchedCheck())
                    dribble = true;

                dribbleLPressed = dribbleRPressed = false;
            }
        } else {
            dribbleLPressed = l.isTouchedCheck();
            dribbleRPressed = r.isTouchedCheck();
            dribble = false;

            focusPressed = false;
            focusBtn.setToggled(false);

            shootPressed = false;
            shootBtn.setToggled(false);
        }

        if(shootPressed)
            sprintPressed = false;
        else sprintPressed = movementStick.isSprintPressed();

        updateRotation();
    }

    public void reset(){
        dX = dY = 0;

        dribbleRPressed = dribbleLPressed = false;
        shootPressed = focusPressed = false;
        sprintPressed = false;

        shootBtn.setToggled(false);
        focusBtn.setToggled(false);
    }

    public void render() {
        movementStick.draw();

        l.draw();
        r.draw();

        shootBtn.draw();
        focusBtn.draw();
    }

    public void updateRotation(){
        if (Gdx.input.justTouched())//If a new pointer comes or the controller is not being updated
            for (int i = 0; i < 3; i++) {
                if (!isTouchPointerUsed(i)) {//If no buttons are working on this pointer
                    //We set the touch pointer to the new one, update the deltas and break the cycle
                    multitouch = i;
                    updateCameraRot();
                    break;
                }
            }
        else if (multitouch != -1 && Gdx.input.isTouched(multitouch))
            updateCameraRot();
        else multitouch = -1;
    }

    private void updateCameraRot() {
        dX = Gdx.input.getDeltaX(multitouch);
        dY = Gdx.input.getDeltaY(multitouch);
    }

    private boolean isTouchPointerUsed(int multitouch) {
        if (multitouch == -1)
            return true;

        return movementStick.getMultitouch() == multitouch ||
                movementStick.getSprintMultitouch() == multitouch ||
                l.getMultitouch() == multitouch ||
                r.getMultitouch() == multitouch ||
                shootBtn.getMultitouch() == multitouch ||
                focusBtn.getMultitouch() == multitouch;
    }

    public float getDeltaX() {
    	float returnDx;
    	
        if (Gdx.app.getType().equals(Application.ApplicationType.Android)) {
            float temp = dX;
            dX = 0;
            returnDx = temp;
        }

        returnDx = -Gdx.input.getDeltaX();
        
        if(SettingsScreen.invertX)
        	returnDx = -returnDx;
        
        return returnDx;
    }

    public float getDeltaY() {
    	float returnDy;
    	
        if (Gdx.app.getType().equals(Application.ApplicationType.Android)) {
            float temp = dY;
            dY = 0;
            returnDy = temp;
        }

        returnDy = Gdx.input.getDeltaY();
        
        if(SettingsScreen.invertY)
        	returnDy = -returnDy;
        
        return returnDy;
    }

    public Vector3 getMovementVec() {
        return new Vector3(movementStick.getOffset().x, 0, movementStick.getOffset().y);
    }

    public boolean isForwardPressed() {
        return forwardPressed;
    }

    public boolean isBackwardPressed() {
        return backwardPressed;
    }

    public boolean isStrRightPressed() {
        return strRightPressed;
    }

    public boolean isStrLeftPressed() {
        return strLeftPressed;
    }

    public boolean isSprintPressed() {
        return sprintPressed;
    }

    public boolean isShootPressed() {
        return shootPressed;
    }

    public boolean isFocusPressed() {
        return focusPressed;
    }

    public boolean isDribbleLPressed() {
        return dribbleLPressed;
    }

    public boolean isDribbleRPressed() {
        return dribbleRPressed;
    }

    public int getScroll() {
        return scroll;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (Gdx.app.getType().equals(Application.ApplicationType.Android))
            return false;

        switch (keycode) {
            case forward:
                forwardPressed = true;
                break;
            case backward:
                backwardPressed = true;
                break;
            case strafeRight:
                strRightPressed = true;
                break;
            case strafeLeft:
                strLeftPressed = true;
                break;
            case sprint:
                sprintPressed = true;
                break;
            case shoot:
                shootPressed = true;
                break;
            /*case focus:
                focusPressed = true;
                break;*/
            case altDribbleL:
                dribbleLPressed = true;
                break;
            case altDribbleR:
                dribbleRPressed = true;
                break;
            case altScrollUp:
                scrollUpPressed = true;
                break;
            case altScrollDown:
                scrollDownPressed = true;
                break;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (Gdx.app.getType().equals(Application.ApplicationType.Android))
            return false;


        switch (keycode) {
            case forward:
                forwardPressed = false;
                break;
            case backward:
                backwardPressed = false;
                break;
            case strafeRight:
                strRightPressed = false;
                break;
            case strafeLeft:
                strLeftPressed = false;
                break;
            case sprint:
                sprintPressed = false;
                break;
            case shoot:
                shootPressed = false;
                break;
            /*case focus:
                focusPressed = false;
                break;*/
            case altDribbleL:
                dribbleLPressed = false;
                break;
            case altDribbleR:
                dribbleRPressed = false;
                break;
            case altScrollUp:
                scrollUpPressed = false;
                break;
            case altScrollDown:
                scrollDownPressed = false;
                break;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {

        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (Gdx.app.getType().equals(Application.ApplicationType.Android)) {
            return false;
        }

        switch (button) {
            case dribbleL:
                dribbleLPressed = true;
                break;
            case dribbleR:
                dribbleRPressed = true;
                break;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (Gdx.app.getType().equals(Application.ApplicationType.Android)) {
            return false;
        }

        switch (button) {
            case dribbleL:
                dribbleLPressed = false;
                break;
            case dribbleR:
                dribbleRPressed = false;
                break;
        }
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
        scroll -= amount;
        return false;
    }

}
