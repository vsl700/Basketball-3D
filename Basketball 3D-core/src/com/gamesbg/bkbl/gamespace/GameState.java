package com.gamesbg.bkbl.gamespace;

/**
 * Used mainly by the AI so that a player knows what position it should take when the game is not running (after a foul for example). This enum contains all the game rules and their
 * calculates players' targets.
 * 
 * @author studi
 *
 */
public enum GameState {

	BALL_OUT_OF_TERRAIN(){
		
	},
	
	INCORRECT_BALL_STEAL_ATTEMPT(){
		
	},
	
	STAY_WITHOUT_DRIBBLE(){
		
	},
	
	MOVE_WITHOUT_DRIBBLE(){
		
	},
	
	GAME_RUNNING(){
		
	};
	
	public void update() {
		
	}
	
}
