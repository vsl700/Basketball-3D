/**
 * 
 */
package com.gamesbg.bkbl.gamespace.rules;

import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.gamesbg.bkbl.gamespace.GameMap;
import com.gamesbg.bkbl.gamespace.entities.Player;

/**
 * This class contains all the game rules and their actions if the go broken
 * 
 * @author studi
 *
 */
public class Rules {

	GameRule[] gameRules;
	GameRule brokenRule;
	
	GameMap map;
	
	public Rules(GameMap map) {
		this.map = map;
		
		gameRules = new GameRule[] {
				new GameRule("ball_out", "Out Of Bounds!", "The Ball Has Reached The Bounds Of The Terrain!", map) {
					Player recentHolder;
					
					@Override
					public boolean checkRule() {
						Player tempPlayer = map.getHoldingPlayer();
						
						if (tempPlayer == null) {//If there is currently holding player
							if (recentHolder == null) { //If there was still no holding player (very rare case)
								for (btCollisionObject obj : map.getBall().getOutsideColliders()) {
									for (Player player : map.getAllPlayers()) {
										if (player.getAllCollObjects().contains(obj)) {
											recentHolder = tempPlayer;//Make the recent holder a player that has recently just touched the ball
										}
									}
								}
							}
						}else
							recentHolder = tempPlayer;
						
						if (recentHolder != null) //If the ball is still not ever touched, don't check for terrain bounds collision (CPU economy) 
							for (btCollisionObject obj : map.getBall().getOutsideColliders()) {
								if (map.getTerrain().getInvisBodies().contains(obj)) {
									ruleBreaker = recentHolder;
									return true;
								}
							}
						
						return false;
					}
				},
				
				new GameRule("incorrect_ball_steal", "Reached In", "The Ball Has Been Touched While The Holding Player Was Not Dribbling!", map) {
					@Override
					public boolean checkRule() {
						
						return false;
					}
				},
				
				//FIXME Check again for the names of the following two game rules!
				new GameRule("stay_no_dribble", "Dribble Violation", "The Ball Has Not Been Dribbled For 5 Seconds!", map) {
					@Override
					public boolean checkRule() {
						
						return false;
					}
				},
				
				new GameRule("move_no_dribble", "Dribble Violation", "The Player That Is Holding The Ball Is Moving Without Dribbling It For 1 Second!", map) {
					@Override
					public boolean checkRule() {
						
						return false;
					}
				},
				
				new GameRule("backcourt_violation", "Backcourt Violation", "The Team That Has The Ball Cannot Let The Ball Cross The Midcourt Line Once It Got In The Opposite's Team Zone!", map) {
					@Override
					public boolean checkRule() {
						
						return false;
					}
				},
		};
	}
	
	public void update() {
		for(GameRule rule : gameRules) {
			if(rule.checkRule()) {
				//A rule has been broken
				brokenRule = rule;
				
				break;
			}
		}
	}
	
	private class GameRule{
		String name, description;
		String id;
		GameMap map;
		
		Player ruleBreaker;
		
		public GameRule(String id, String name, String desc, GameMap map) {
			this.id = id;
			this.name = name;
			description = desc;
			this.map = map;
		}
		
		/**
		 * To see if the following rule is broken, this method should be called to which should check that for each rule
		 * 
		 * @return true if the rule is broken
		 */
		public boolean checkRule() {return false;}
		
		public String getId() {
			return id;
		}
		
		/**
		 * Called when the game continues after the foul
		 */
		public void clearRuleBreaker() {
			ruleBreaker = null;
		}
		
		public Player getRuleBreaker() {
			return ruleBreaker;
		}
	}
}
