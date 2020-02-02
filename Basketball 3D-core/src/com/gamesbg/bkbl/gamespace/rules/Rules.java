/**
 * 
 */
package com.gamesbg.bkbl.gamespace.rules;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
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
	
	RulesListener rulesListener; //That's here because the GameScreen does not have any connection with the GameMap or Rules so I had to make the GameScreen an interface
	
	public Rules(GameMap map, RulesListener rulesListener) {
		this.map = map;
		this.rulesListener = rulesListener;
		
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
						//TODO Also add a check for situations in which the ball gets in the basket through its bottom!
						return false;
					}

					@Override
					public Vector3[] getCalculatedTargetPositions() {
						Vector3[] targets = new Vector3[map.getAllPlayers().size()];
						
						
						return targets;
					}
					
					
				},
				
				new GameRule("incorrect_ball_steal", "Reached In!", "The Ball Has Been Touched While The Holding Player Was Not Dribbling!", map) {
					@Override
					public boolean checkRule() {
						Player temp = map.getHoldingPlayer();
						
						if (temp != null) {
							for (btCollisionObject obj : map.getBall().getOutsideColliders()) {
								// The checked entity would be always a player
								// as the players and the ball are the only
								// entities and an entity cannot collide with
								// its own collision objects
								Player checked = (Player) map.getCollObjsInEntityMap().get(obj);

								if (checked != null && !checked.equals(temp) && checked.isPointing() && !temp.isDribbling()) {
									ruleBreaker = checked;
									return true;
								}
							}
						}
						
						return false;
					}
				},
				
				//FIXME Check again for the names of the following two game rules!
				new GameRule("stay_no_dribble", "Dribble Violation!", "The Ball Has Not Been Dribbled For 5 Seconds!", map) {
					float timer = 5;
					
					@Override
					public boolean checkRule() {
						Player temp = map.getHoldingPlayer();
						if(temp == null) {
							timer = 5;
							return false;
						}
						
						if (!temp.isDribbling()) {
							if (timer <= 0) {
								timer = 5;

								ruleBreaker = temp;
								return true;
							} else
								timer -= Gdx.graphics.getDeltaTime();
						}
						else timer = 5;
						
						return false;
					}
				},
				
				new GameRule("move_no_dribble", "Dribble Violation!", "The Player That Is Holding The Ball Is Moving Without Dribbling It For A Total Of 1 Second!", map) {
					float timer = 1;
					
					@Override
					public boolean checkRule() {
						Player temp = map.getHoldingPlayer();
						if(temp == null) {
							timer = 1;
							return false;
						}
						
						if (!temp.getMoveVector().isZero()) {
							if (!temp.isDribbling() && !temp.isShooting() && !temp.isAiming()) {
								if (timer <= 0) {
									timer = 1;

									ruleBreaker = temp;
									return true;
								} else
									timer -= Gdx.graphics.getDeltaTime();
							} else
								timer = 1;
						}
						
						return false;
					}
				},
				
				//FIXME Add this rule and also note that the collision world doesn't always detect the collisions which messes the system up. Think about putting a timeout for clearing
				//collision objects from entities' lists or limiting the FPS of the game!
				/*new GameRule("backcourt_violation", "Backcourt Violation!", "The Team That Has The Ball Cannot Let The Ball Cross The Midcourt Line Once It Got In Their Opposite's Team Zone!", map) {
					boolean crossed;//Whether it has already crossed the midcourt lane of the terrain
					
					@Override
					public boolean checkRule() {
						Player currentHolder = map.getHoldingPlayer();
						
						if(currentHolder == null) {
							crossed = false;
							return false;
						}
						
						
						if (currentHolder instanceof Teammate) {
							if (currentHolder.getOutsideColliders().contains(map.getTerrain().getTeamzone())) { //If it is in Teammate zone
								if (crossed) {
									System.out.println("MidCourt");
									crossed = false;
									
									return true;// If the player doesn't collide
												// with the teamzone any more,
												// it means that it's not in it.
												// Then we give the foul
												// message.
									
								}
							} else
								crossed = true;
						}
						else {
							if (!currentHolder.getOutsideColliders().contains(map.getTerrain().getTeamzone())) {
								if (crossed) {
									System.out.println("MidCourt");
									crossed = false;
									
									return true;
								}
							} else
								crossed = true;
						}
						
						return false;
					}
				},*/
		};
	}
	
	public void update() {
		for(GameRule rule : gameRules) {
			if(rule.checkRule()) {
				//A rule has been broken
				brokenRule = rule;
				map.onRuleBroken(rule);
				rulesListener.onRuleBroken(rule);
				
				break;
			}
		}
	}
	
	public void clearBrokenRule() {
		brokenRule = null;
	}
	
	public GameRule getBrokenRule() {
		return brokenRule;
	}
	
	public class GameRule{
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
		
		public Vector3[] getCalculatedTargetPositions() {return null;}
		
		public String getId() {
			return id;
		}
		
		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
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
	
	public interface RulesListener{
		
		public void onRuleBroken(GameRule rule);
	}
}
