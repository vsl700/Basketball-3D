package com.vasciie.bkbl.gamespace.multiplayer;

import com.badlogic.gdx.graphics.Color;

public class GameMessage {

	public String[] args;
	public String heading, desc;
	public Color color;
	public boolean skippable;
	
	public void reset() {
		heading = desc = null;
		color = null;
	}
	
}
