/**
 * 
 */
package com.vasciie.bkbl.gamespace.rules;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.vasciie.bkbl.gamespace.GameMap;
import com.vasciie.bkbl.gamespace.entities.Entity;
import com.vasciie.bkbl.gamespace.entities.Player;
import com.vasciie.bkbl.gamespace.entities.players.Teammate;
import com.vasciie.bkbl.gamespace.rules.Rules.GameRule.Actions.Action;
import com.vasciie.bkbl.gamespace.tools.GameTools;

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
				new GameRule(this, "ball_out", "Out Of Bounds!", "The Ball Has Reached The Bounds Of The Terrain!", map) {
					Player recentHolder, thrower;
					
					
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

					/*@Override
					public void managePlayers() {
						if(recentHolder.equals(thrower)) {
							rules.clearBrokenRuleWRuleBreaker();
						}
					}*/

					@Override
					public void createActions() {
						actions.addAction(new Action() {

							@Override
							public boolean act() {
								ArrayList<Player> allPlayers = map.getAllPlayers();
								
								if(ruleBreaker instanceof Teammate) {
									thrower = GameTools.getClosestPlayer(map.getBall().getPosition(), map.getOpponents(), null);
								}else {
									thrower = GameTools.getClosestPlayer(map.getBall().getPosition(), map.getTeammates(), null);
								}
								recentHolder = thrower;
								
								
								Vector3 posGroupPos = thrower.getPosition().cpy().sub(thrower.angleToVector(new Vector3(), thrower.getOrientation()).scl(3));
								// This is supposed to be a position right in front of
								// the thrower's sight, and then random coordinates from
								// -6 to 6 z-axis and -3 to 3 x-axis will .mul()-ed by
								// this matrix, so that the other players get in random
								// positions in front of the thrower. Also the group
								// should be pointed at the thrower.
								Matrix4 positionsCalc = new Matrix4().setToLookAt(posGroupPos, thrower.getPosition(), new Vector3(0, -1, 0)).trn(posGroupPos);
								for(int i = 0; i < allPlayers.size(); i++) {
									Player temp = allPlayers.get(i);
									if(temp.equals(thrower)) {
										//map.setPlayerTargetPosition(map.getBall().getPosition(), temp);
										if(temp.getBrain().getMemory().getTargetPosition() == null) {
											temp.getBrain().getMemory().setTargetPosition(map.getBall());
											temp.getBrain().getMemory().setCatchBall(true);
										}
									}
									else {
										//Choosing a random target position for the following players
										Matrix4 tempTrans = new Matrix4().setToTranslation(MathUtils.random(-3f, 3f), 0, MathUtils.random(-6f, 6f)); //We need to specify that the range value is a float (with an f), otherwise we are calling the integer method
										
										//Putting a calculated from the original by the group one position into the targets vector
										map.setPlayerTargetPosition(positionsCalc.cpy().mul(tempTrans).getTranslation(new Vector3()), temp);
										temp.getBrain().getMemory().setTargetFacing(map.getBall());
										temp.getBrain().getCustomPursue().setArrivalTolerance(2);
									}
								}
								
								System.out.println("Calculated targets");
								
								return true;
							}

							@Override
							public boolean isGameDependent() {
								return false;
							}
							
						});
						
						actions.addAction(new Action() {

							@Override
							public boolean act() {
								if(recentHolder.isHoldingBall()) {
									recentHolder.getBrain().clearCustomTarget();
									return true;
								}
								
								return false;
							}

							@Override
							public boolean isGameDependent() {
								return false;
							}
							
						});
						
						actions.addAction(new Action() {

							@Override
							public boolean act() {
								
								
								return false;
							}

							@Override
							public boolean isGameDependent() {
								return true;
							}
							
						});
						
					}
				},
				
				new GameRule(this, "incorrect_ball_steal", "Reached In!", "The Ball Has Been Touched While The Holding Player Was Not Dribbling!", map) {
					@Override
					public boolean checkRule() {
						Player temp = map.getHoldingPlayer();
						
						if (temp != null) {
							for (btCollisionObject obj : map.getBall().getOutsideColliders()) {
								// The checked entity would be always a player
								// as the players and the ball are the only
								// entities and an entity cannot collide with
								// its own collision objects
								Entity tempE = map.getCollObjsInEntityMap().get(obj);
								if(!(tempE instanceof Player)) break;//Well sometimes crashes occur
								
								Player checked = (Player) tempE;

								if (checked != null && !checked.equals(temp) && checked.isPointing() && !temp.isDribbling()) {
									ruleBreaker = checked;
									return true;
								}
							}
						}
						
						return false;
					}

					@Override
					public void createActions() {
						
						
					}
				},
				
				//FIXME Check again for the names of the following two game rules!
				new GameRule(this, "stay_no_dribble", "Dribble Violation!", "The Ball Has Not Been Dribbled For 5 Seconds!", map) {
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

					@Override
					public void createActions() {
						
						
					}
				},
				
				new GameRule(this, "move_no_dribble", "Dribble Violation!", "The Player That Is Holding The Ball Is Moving Without Dribbling It For A Total Of 1 Second!", map) {
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

					@Override
					public void createActions() {
						
						
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
		if(brokenRule != null)
			brokenRule.managePlayers();
		else {
			/*for (GameRule rule : gameRules) {
				if (rule.checkRule()) {
					// A rule has been broken
					brokenRule = rule;
					map.onRuleBroken(rule);
					rulesListener.onRuleBroken(rule);

					break;
				}
			}*/ // TODO Bring this back after you finish testing the rules!!!

			GameRule tempRule = gameRules[0];
			if (tempRule.checkRule()) {
				brokenRule = tempRule;
				map.onRuleBroken(tempRule);
				rulesListener.onRuleBroken(tempRule);
			}
		}
	}
	
	public void clearBrokenRule() {
		brokenRule = null;
	}
	
	public void clearBrokenRuleWRuleBreaker() {
		brokenRule.clearRuleBreaker();
		brokenRule = null;
	}
	
	public GameRule getBrokenRule() {
		return brokenRule;
	}
	
	public static abstract class GameRule{
		String name, description;
		String id;
		GameMap map;
		
		Actions actions;
		
		Player ruleBreaker;
		
		Rules rules;
		
		public GameRule(Rules rules, String id, String name, String desc, GameMap map) {
			this.id = id;
			this.name = name;
			description = desc;
			this.map = map;
			this.rules = rules;
			
			actions = new Actions(map);
			createActions();
		}
		
		public abstract void createActions();
		
		/**
		 * To see if the following rule is broken, this method should be called to which should check that for each rule
		 * 
		 * @return true if the rule is broken
		 */
		public abstract boolean checkRule();
		
		public boolean arePlayersReady() {
			return actions.getCurrentAction().isGameDependent();
		}
		
		/**
		 * Used after a rule is broken (when the players are acting on the broken rule)
		 * 
		 * @return true if the players are ready
		 */
		public boolean managePlayers() {
			if(actions.act()) {
				rules.clearBrokenRuleWRuleBreaker();
				return true;
			}
			
			return false;
		}
		
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
			actions.firstAction();
			ruleBreaker = null;
		}
		
		public Player getRuleBreaker() {
			return ruleBreaker;
		}
		
		public static class Actions{
			Action firstAction, currentAction;
			GameMap map;
			
			public Actions(GameMap map) {
				this.map = map;
			}
			
			public void addAction(Action action) {
				if(currentAction == null) {
					firstAction = currentAction = action;
					return;
				}
				
				Action temp = currentAction;
				while(temp.next != null) {
					temp = temp.next;
				}
				
				temp.next = action;
			}
			
			public Action getCurrentAction() {
				return currentAction;
			}
			
			public void nextAction() {
				currentAction = currentAction.next;
			}
			
			public void firstAction() {
				currentAction = firstAction;
			}
			
			public boolean isLastAction() {
				return currentAction.next == null;
			}
			
			/**
			 * 
			 * @return true if all the actions are completed
			 */
			public boolean act() {
				if(((currentAction.isGameDependent() && map.isGameRunning()) || !currentAction.isGameDependent()) && currentAction.act()) {
					if(isLastAction()) {
						firstAction();
						return true;
					}
					
					nextAction();
				}
				
				return false;
			}
			
			public static abstract class Action{
				public Action next;
				
				/**
				 * 
				 * @return true if the action is dependent on the game and the game should start (it doesn't clear the broken rule or rule breaker)
				 */
				public abstract boolean isGameDependent();
				
				/**
				 * 
				 * @return true if the action is completed
				 */
				public abstract boolean act();
			}
		}
	}
	
	public interface RulesListener{
		
		public void onRuleBroken(GameRule rule);
	}
}
