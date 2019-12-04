package com.gamesbg.bkbl.gamespace.tools;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;

public class InputController implements InputProcessor {

	static final int forward = Keys.W, backward = Keys.S, strafeRight = Keys.D, strafeLeft = Keys.A;
	static final int sprint = Keys.SHIFT_LEFT;
	static final int shoot = Keys.CONTROL_LEFT;
	static final int dribbleL = Buttons.LEFT, dribbleR = Buttons.RIGHT;
	
	int scroll = 0;
	
	boolean forwardPressed, backwardPressed, strRightPressed, strLeftPressed;
	boolean sprintPressed;
	boolean shootPressed;
	boolean dribbleLPressed, dribbleRPressed;
	/**
	 * Resets the scroll amount and the dribble (and ball pointing) buttons indicators
	 * @param resetDribble - whether to reset the ball dribble indicators
	 */
	public void update(boolean resetDribble) {
		if(resetDribble) {
			dribbleLPressed = false;
			dribbleRPressed = false;
		}
		
		scroll = 0;
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

	public boolean isDribbleLPressed() {
		return dribbleLPressed;
	}

	public boolean isDribbleRPressed() {
		return dribbleRPressed;
	}

	public int GetScroll() {
		return scroll;
	}
	
	@Override
	public boolean keyDown(int keycode) {
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
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
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
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		switch(button) {
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
		switch(button) {
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
		scroll += amount;
		return false;
	}

}
