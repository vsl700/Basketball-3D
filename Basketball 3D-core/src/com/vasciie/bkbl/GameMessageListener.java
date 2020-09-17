package com.vasciie.bkbl;

import com.badlogic.gdx.graphics.Color;

public interface GameMessageListener {

	public void sendMessage(String heading, String description, Color textColor, GameMessageSender sender, boolean skippable, boolean showPower);
	
	public void sendMessage(String heading, String description, Color textColor, GameMessageSender sender, boolean skippable);
	
	public void sendMessage(String message, Color textColor, GameMessageSender sender, boolean skippable);
	
	public void sendMinorMessage(String message);
	
	public String getMessageHeading();
	
	public String getMessageDesc();
	
	public Color getMessageColor();
	
}
