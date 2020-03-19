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
 * This class contains all the game rules and their actions if they go broken
 * 
 * @author studi
 *
 */
public class Rules {

	final GameRule[] gameRules;
	GameRule brokenRule;
	
	GameMap map;
	
	RulesListener rulesListener; //That's here because the GameScreen does not have any connection with the GameMap or Rules so I had to make the GameScreen an interface
	
	public Rules(GameMap map, RulesListener rulesListener) {
		this.map = map;
		this.rulesListener = rulesListener;
		
		gameRules = new GameRule[] {
				new GameRule(this, null, "ball_out", "Out Of Bounds!", "The Ball Has Reached The Bounds Of The Terrain!", map) {
					final Vector3 occurPlace = new Vector3();
					Player recentHolder, thrower;
					
					
					@Override
					public boolean checkRule() {
						Player tempPlayer = map.getHoldingPlayer();
						
						if(thrower != null && thrower.isBallFree() && thrower.getPosition().dst(map.getBall().getPosition()) <= 1)
							return false;
						
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
						
						/*if(thrower != null && thrower.equals(recentHolder))
							return false;*/
						
						if (recentHolder != null) //If the ball is still not ever touched, don't check for terrain bounds collision (CPU economy) 
							for (btCollisionObject obj : map.getBall().getOutsideColliders()) {
								if (map.getTerrain().getInvisBodies().contains(obj)) {
									ruleBreaker = recentHolder;
									occurPlace.set(map.getBall().getPosition()).y = recentHolder.getPosition().y;
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
								
								//thrower = map.getMainPlayer();
								recentHolder = thrower;
								
								
								Vector3 posGroupPos = thrower.getPosition().cpy().sub(thrower.angleToVector(Vector3.Z, thrower.getOrientation()));
								// This is supposed to be a position right in front of
								// the thrower's sight, and then random coordinates from
								// -3 to 3 z-axis and -1.5 to 1.5 x-axis will be .mul()-ed by
								// this matrix, so that the other players get in random
								// positions in front of the thrower. Also the group
								// should be pointed at the thrower.
								Matrix4 positionsCalc = new Matrix4().setToLookAt(posGroupPos, thrower.getPosition(), new Vector3(0, -1, 0)).trn(posGroupPos);
								for(Player p : allPlayers) {
									if(p.equals(thrower)) {
										//map.setPlayerTargetPosition(map.getBall().getPosition(), temp);
										p.getBrain().setCustomTarget(map.getBall());
										p.getBrain().getMemory().setTargetFacing(map.getBall());
										p.getBrain().getMemory().setCatchBall(true);
										p.getBrain().getCustomPursue().setArrivalTolerance(0);
										//p.setUpdateBrain(true);
									}
									else {
										//Choosing a random target position for the following players
										Matrix4 tempTrans = new Matrix4().setToTranslation(MathUtils.random(-1.5f, 1.5f), 0, MathUtils.random(-3f, 3f)); //We need to specify that the range value is a float (with an f), otherwise we are calling the integer method
										
										//Putting a calculated from the original by the group one position into the targets vector
										p.getBrain().setCustomVecTarget(positionsCalc.cpy().mul(tempTrans).getTranslation(new Vector3()), true);
										//p.getBrain().getMemory().setTargetFacing(p.getBrain().getMemory().getTargetPosition());
										p.getBrain().getCustomPursue().setArrivalTolerance(2);
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
									
									//map.setPlayerTargetPosition(occurPlace, recentHolder);
									recentHolder.getBrain().setCustomVecTarget(occurPlace, true);
									
									
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
								if(thrower.getPosition().dst(occurPlace) < 2.5f) {
									thrower.getBrain().clearCustomTarget();
									thrower.focus(true);//Just for beauty (it's just for one frame and I don't think that it will cost that much)
									
									for(Player p : map.getAllPlayers())
										if(!p.equals(thrower))
											p.getBrain().getMemory().setTargetFacing(map.getBall());
									
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
								// We also check whether it is holding or not,
								// because when the action starts, the player
								// won't be aiming or shooting, which will
								// result in ending the action before it starts
								if(!thrower.isHoldingBall() && !thrower.isAiming() && !thrower.isShooting())
									return true;
								
								if (!thrower.isMainPlayer()) {
									thrower.focus(true);

									Player focusedPlayer = thrower.getFocusedPlayer();
									if (focusedPlayer == null)
										return false;

									Vector3 tempAimVec = focusedPlayer.getPosition();

									if (!thrower.getBrain().updateShooting(3))
										thrower.getBrain().performShooting(tempAimVec);
									else
										thrower.getBrain().getMemory().setTargetVec(tempAimVec);
								}
								
								thrower.getBrain().getBasketSeparate().setEnabled(true);
								
								return false;
							}

							@Override
							public boolean isGameDependent() {
								return true;
							}
							
						});
						
					}

					@Override
					public GameRule[] createInnerRules() {
						GameRule[] gameRules = new GameRule[] {
								new GameRule(rules, this, "move_zone", "Moved Out!", "You Should Stay Around The Foul Occuring Plcae During Throw-in!", map) {

									@Override
									public GameRule[] createInnerRules() {
										
										return null;
									}

									@Override
									public void createActions() {
										//This will just stay empty! The system will automatically switch to the parent rule.
										
									}

									@Override
									public boolean checkRule() {
										//Terrain terrain = map.getTerrain();
										
										//Vector3 closestWallPos, secondCloseWallPos;
										Vector3 throwerPos = thrower.getPosition();
										Vector3 occurPlaceCpy = occurPlace.cpy();
										
										/*ArrayList<Vector3> wallPositions = new ArrayList<Vector3>();
										
										for(int i = 1; i < 5; i++)//We set it from 1 to 5 because the matrixes for the walls in this case start from index 1
											wallPositions.add(terrain.getMatrixes().get(i).getTranslation(new Vector3()));
										
										closestWallPos = GameTools.getShortestDistanceWVectors(occurPlace, wallPositions);
										
										wallPositions.remove(closestWallPos);
										
										secondCloseWallPos = GameTools.getShortestDistanceWVectors(occurPlace, wallPositions);
										
										
										closestWallPos.y = throwerPos.y;
										if(closestWallPos.z == 0)
											closestWallPos.z = throwerPos.z;
										else closestWallPos.x = throwerPos.x;
										
										secondCloseWallPos.y = throwerPos.y;
										if(secondCloseWallPos.z == 0)
											secondCloseWallPos.z = throwerPos.z;
										else secondCloseWallPos.x = throwerPos.x;*/
										
										/*if(occurPlaceCpy.z == 0)
											occurPlaceCpy.z = throwerPos.z;
										else occurPlaceCpy.x = throwerPos.x;*/
										
										float checkConst = /*Terrain.getWalldepth() * 1.6f*/ 3;
										if(throwerPos.dst(occurPlaceCpy) > checkConst/* && throwerPos.dst(secondCloseWallPos) > checkConst*/) {
											ruleBreaker = thrower;
											return true;
										}
										
										
										return false;
									}
									
								}
						};
						
						return gameRules;
					}
				},
				
				new GameRule(this, null, "incorrect_ball_steal", "Reached In!", "The Ball Has Been Touched While The Holding Player Was Not Dribbling!", map) {
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

					@Override
					public GameRule[] createInnerRules() {
						
						return null;
					}
				},
				
				//FIXME Check again for the names of the following two game rules!
				new GameRule(this, null, "stay_no_dribble", "Dribble Violation!", "The Ball Has Not Been Dribbled For 5 Seconds!", map) {
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

					@Override
					public GameRule[] createInnerRules() {
						
						return null;
					}
				},
				
				new GameRule(this, null, "move_no_dribble", "Dribble Violation!", "The Player That Is Holding The Ball Is Moving Without Dribbling It For A Total Of 1 Second!", map) {
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

					@Override
					public GameRule[] createInnerRules() {
						
						return null;
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
		if(brokenRule == null) {
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
				setBrokenRule(tempRule);
			}
		}
		else {
			brokenRule.managePlayers();
		}
	}
	
	public void setBrokenRule(GameRule rule) {
		if(rule.getParent() != null || brokenRule == null) {
			map.onRuleBroken(rule);
			rulesListener.onRuleBroken(rule);
		}
		
		brokenRule = rule;
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
		
		GameRule parent;
		GameRule[] innerRules;//Rules of the rule
		
		public GameRule(Rules rules, GameRule parent, String id, String name, String desc, GameMap map) {
			this.id = id;
			this.name = name;
			description = desc;
			this.map = map;
			this.rules = rules;
			this.parent = parent;
			
			actions = new Actions(map);
			innerRules = createInnerRules();
			
			createActions();
		}
		
		public abstract GameRule[] createInnerRules();
		
		public abstract void createActions();
		
		/**
		 * To see if the following rule is broken, this method should be called to which should check that for each rule
		 * @return true if the rule is broken
		 */
		public abstract boolean checkRule();
		
		public boolean arePlayersReady() {
			if(actions.isEmpty()) {
				if(parent == null)
					return true;
				
				return false;
			}
			return actions.getCurrentAction().isGameDependent();
		}
		
		/**
		 * Used after a rule is broken (when the players are acting on the broken rule)
		 */
		public void managePlayers() {
			if(actions.act()) {
				if(parent != null) {
					rules.setBrokenRule(parent);
					//return false;
				}
				else rules.clearBrokenRuleWRuleBreaker();
				
				return;
				//return true;
			}
			
			if(innerRules != null && map.isGameRunning())
				for(GameRule r : innerRules)
					if(r.checkRule()) {
						rules.setBrokenRule(r);
						actions.firstAction();
						//return false;
					}
			
			//return false;
		}
		
		public GameRule getParent() {
			return parent;
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
			
			public boolean isEmpty() {
				return firstAction == null;
			}
			
			public boolean isLastAction() {
				return currentAction.next == null;
			}
			
			/**
			 * 
			 * @return true if all the actions are completed
			 */
			public boolean act() {
				if(isEmpty())
					return true;
				
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
