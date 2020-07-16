/**
 * 
 */
package com.vasciie.bkbl.gamespace.levels;

import com.vasciie.bkbl.GameMessageListener;
import com.vasciie.bkbl.gamespace.GameMap;

/**
 * @author studi
 *
 */
public abstract class Levels {
	
	GameMap map;
	
	GameLevel[] gameLevels;
	GameMessageListener messageListener;
	
	
	public Levels(GameMap map, GameMessageListener messageListener) {
		this.map = map;
		this.messageListener = messageListener;
	}
	
	public GameLevel getGameLevel(String id) {
		for(GameLevel level : gameLevels)
			if(level.getId().equals(id))
				return level;
		
		return null;
	}
	
	public GameLevel getGameLevel(int i) {
		return gameLevels[i];
	}
	
	public abstract boolean usesOriginalRules();
	
	
	public static abstract class GameLevel {
		GameMap map;

		String id;
		
		GameMessageListener messageListener;
		
		
		public GameLevel(String id, GameMap map, GameMessageListener messageListener) {
			this.map = map;
			this.id = id;
			this.messageListener = messageListener;
		}
		
		public String getId() {
			return id;
		}
		
	}
}
