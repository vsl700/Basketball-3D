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
	
	public int indexOf(GameLevel level) {
		for(int i = 0; i < gameLevels.length; i++) {
			if(gameLevels[i].equals(level))
				return i;
		}
			
		return -1;
	}
	
	public int getSize() {
		return gameLevels.length;
	}
	
	
	public static abstract class GameLevel {
		GameMap map;

		String id, name;
		
		GameMessageListener messageListener;
		
		
		public GameLevel(String id, String name, GameMap map, GameMessageListener messageListener) {
			this.map = map;
			this.id = id;
			this.name = name;
			this.messageListener = messageListener;
		}
		
		public String getId() {
			return id;
		}
		
		public String getName() {
			return name;
		}
		
	}
}
