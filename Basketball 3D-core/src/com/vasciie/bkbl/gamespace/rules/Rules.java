/**
 * 
 */
package com.vasciie.bkbl.gamespace.rules;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.vasciie.bkbl.gamespace.GameMap;
import com.vasciie.bkbl.gamespace.entities.Entity;
import com.vasciie.bkbl.gamespace.entities.Player;
import com.vasciie.bkbl.gamespace.entities.players.Teammate;
import com.vasciie.bkbl.gamespace.objects.Terrain;
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
	GameRule triggeredRule;
	
	GameMap map;
	
	RulesListener rulesListener; //That's here because the GameScreen does not have any connection with the GameMap or Rules so I had to make the GameScreen an interface
	
	public Rules(GameMap map, RulesListener rulesListener) {
		this.map = map;
		this.rulesListener = rulesListener;
		
		gameRules = new GameRule[] {
				new GameRule(this, null, "ball_out", "Out Of Bounds!", map) {
					Player recentHolder, thrower;
					
					
					@Override
					public boolean checkRule() {
						Player tempPlayer = map.getHoldingPlayer();
						
						if(thrower != null && !thrower.isBallFree())
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
						
						if (recentHolder != null) {//If the ball is still not ever touched, don't check for terrain bounds collision (CPU economy) 
							if(!recentHolder.isBallFree())
								return false;
							
							for (btCollisionObject obj : map.getBall().getOutsideColliders()) {
								if (map.getTerrain().getInvisBodies().contains(obj)) {
									ruleTriggerer = recentHolder;
									occurPlace.set(map.getBall().getPosition()).add(occurPlace.cpy().scl(-1).nor().scl(3)).y = recentHolder.getPosition().y;
									map.playerReleaseBall();
									return true;
								}
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
								
								if(ruleTriggerer instanceof Teammate) {
									thrower = GameTools.getClosestPlayer(map.getBall().getPosition(), map.getOpponents(), null);
								}else {
									thrower = GameTools.getClosestPlayer(map.getBall().getPosition(), map.getTeammates(), null);
								}
								
								//thrower = map.getMainPlayer();
								recentHolder = thrower;
								
								
								Vector3 posGroupPos = occurPlace.cpy().nor().scl(5);
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
								if(map.getTeammates().size() == 1)
									return true;
								
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
								new GameRule(rules, this, "move_zone", "Moved Out!", map) {

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
										if(!thrower.getPrevMoveVec().isZero() && throwerPos.dst(occurPlaceCpy) > checkConst/* && throwerPos.dst(secondCloseWallPos) > checkConst*/) {
											ruleTriggerer = thrower;
											return true;
										}
										
										
										return false;
									}

									@Override
									public String getDescription() {
										
										return "The Thrower Should Stay Around The Foul Occuring Plcae During Throw-in!";
									}
									
								}
						};
						
						return gameRules;
					}

					@Override
					public String getDescription() {
						
						return "The Ball Has Reached The Bounds Of The Terrain!";
					}
				},
				
				new GameRule(this, null, "incorrect_ball_steal", "Reached In!", map) {
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
								if(!(tempE instanceof Player)) continue;//Well sometimes crashes occur
								
								Player checked = (Player) tempE;

								if (checked != null && !checked.equals(temp) && checked.isCurrentlyPointing() && !temp.isDribbling()) {
									ruleTriggerer = checked;
									return true;
								}
							}
						}
						
						return false;
					}

					@Override
					public void createActions() {
						actions.addAction(new Action() {

							@Override
							public boolean act() {
								Player holdingPlayer = map.getHoldingPlayer();
								
								if (holdingPlayer == null) {
									Vector3 ballVec = map.getBall().getPosition();

									if (ruleTriggerer instanceof Teammate)
										holdingPlayer = GameTools.getClosestPlayer(ballVec, map.getOpponents(), null);
									else
										holdingPlayer = GameTools.getClosestPlayer(ballVec, map.getTeammates(), null);
									
									holdingPlayer.getBrain().setCustomTarget(map.getBall());
									holdingPlayer.getBrain().getMemory().setTargetFacing(map.getBall());
									holdingPlayer.getBrain().getMemory().setCatchBall(true);
								}
								
								for(Player p : map.getAllPlayers()) {
									if(p.equals(holdingPlayer))
										continue;
									
									p.setAbleToRun(false);
									
									p.getBrain().setCustomTarget(holdingPlayer);
									p.getBrain().getMemory().setTargetFacing(holdingPlayer);
									
									p.getBrain().getCustomPursue().setArrivalTolerance(10);//See here!
								}
								
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
								if(map.getHoldingPlayer() == null) {
									return false;
								}
								
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
								Player holdingPlayer = map.getHoldingPlayer();
									
								if(holdingPlayer.isInAwayBasketZone()) {
									if(!holdingPlayer.getBrain().updateShooting(1.25f)) {
										holdingPlayer.getBrain().clearCustomTarget();
										holdingPlayer.getBrain().performShooting(holdingPlayer.getBrain().makeBasketTargetVec(holdingPlayer.getTargetBasket()));
										System.out.println("Shoot performed");
									}else if(!holdingPlayer.getBrain().isShooting()) {
										for(Player p : map.getAllPlayers()) {
											if(p.equals(holdingPlayer))
												continue;
											
											p.setAbleToRun(true);
										}
										
										return true;
									}
								}else {
									holdingPlayer.getBrain().setCustomTarget(holdingPlayer.getTargetBasket());
									holdingPlayer.getBrain().getMemory().setTargetFacing(holdingPlayer.getTargetBasket());
									holdingPlayer.getBrain().getAllPlayerSeparate().setEnabled(false);
									
									holdingPlayer.setRunning();
								}
								
								return false;
							}
							
							@Override
							public boolean isGameDependent() {
								
								return false;
							}
							
						});
						
					}

					@Override
					public GameRule[] createInnerRules() {
						
						return null;
					}

					@Override
					public String getDescription() {
						
						return "The Ball Has Been Touched While The Holding Player Was Not Dribbling!";
					}
				},
				
				new GameRule(this, null, "stay_no_dribble", "Dribble Violation!", map) {
					final float defaultTime = 3;
					float timer = defaultTime;
					
					@Override
					public boolean checkRule() {
						Player temp = map.getHoldingPlayer();
						if(temp == null) {
							timer = defaultTime;
							return false;
						}
						
						if (!temp.isDribbling()) {
							if (timer <= 0) {
								timer = defaultTime;

								ruleTriggerer = temp;
								map.playerReleaseBall();
								
								ArrayList<Vector3> wallPositions = new ArrayList<Vector3>(8);
								Terrain terrain = map.getTerrain();
								
								for(int i = 1; i < 5; i++) {
									Vector3 wallPos = terrain.getMatrixes().get(i).getTranslation(new Vector3());
									wallPos.y = temp.getPosition().y;
									
									float changer, compatibChange = map.getBall().getWidth() / 2 + Terrain.getWalldepth();
									
									if(wallPos.x == 0) {//Basket-side wall
										changer = terrain.getWidth() / 4;
										
										if(wallPos.z < 0)
											compatibChange = -compatibChange;
										
										wallPositions.add(wallPos.cpy().add(changer, 0, -compatibChange));
										wallPositions.add(wallPos.sub(changer, 0, compatibChange));
									}else {//Sidewall
										changer = terrain.getDepth() / 4;
										
										if(wallPos.x < 0)
											compatibChange = -compatibChange;
										
										wallPositions.add(wallPos.cpy().add(-compatibChange, 0, changer));
										wallPositions.add(wallPos.sub(compatibChange, 0, changer));
									}
								}
								
								occurPlace.set(GameTools.getShortestDistanceWVectors(temp.getPosition(), wallPositions));
								
								return true;
							} else
								timer -= Gdx.graphics.getDeltaTime();
						}
						else timer = defaultTime;
						
						return false;
					}
					
					@Override
					public void managePlayers() {
						GameRule switchRule = rules.getGameRuleById("ball_out");
						switchRule.setRuleBreaker(ruleTriggerer);
						
						rules.setTriggeredRule(switchRule);
					}

					@Override
					public void createActions() {
						
						
					}

					@Override
					public GameRule[] createInnerRules() {
						
						return null;
					}

					@Override
					public String getDescription() {
						
						return "The Ball Has Not Been Dribbled For 3 Seconds!";
					}
				},
				
				new GameRule(this, null, "move_no_dribble", "Dribble Violation!", map) {
					final float defaultTime = 0.75f;
					float timer = defaultTime;
					
					@Override
					public boolean checkRule() {
						Player temp = map.getHoldingPlayer();
						if(temp == null) {
							timer = defaultTime;
							return false;
						}
						
						System.out.println(timer);
						
						if (!temp.getPrevMoveVec().isZero() || !temp.getMoveVector().isZero()) {
							if (!temp.isDribbling() && !temp.isShooting() && !temp.isAiming() && !temp.isCurrentlyAiming()) {
								if (timer <= 0) {
									timer = defaultTime;

									ruleTriggerer = temp;
									map.playerReleaseBall();
									
									ArrayList<Vector3> wallPositions = new ArrayList<Vector3>(8);
									Terrain terrain = map.getTerrain();
									
									for(int i = 1; i < 5; i++) {
										Vector3 wallPos = terrain.getMatrixes().get(i).getTranslation(new Vector3());
										wallPos.y = temp.getPosition().y;
										
										float changer, compatibChange = map.getBall().getWidth() / 2 + Terrain.getWalldepth();
										
										if(wallPos.x == 0) {//Basket-side wall
											changer = terrain.getWidth() / 4;
											
											if(wallPos.z < 0)
												compatibChange = -compatibChange;
											
											wallPositions.add(wallPos.cpy().add(changer, 0, -compatibChange));
											wallPositions.add(wallPos.sub(changer, 0, compatibChange));
										}else {//Sidewall
											changer = terrain.getDepth() / 4;
											
											if(wallPos.x < 0)
												compatibChange = -compatibChange;
											
											wallPositions.add(wallPos.cpy().add(-compatibChange, 0, changer));
											wallPositions.add(wallPos.sub(compatibChange, 0, changer));
										}
									}
									
									occurPlace.set(GameTools.getShortestDistanceWVectors(temp.getPosition(), wallPositions));
									
									return true;
								} else
									timer -= Gdx.graphics.getDeltaTime();
							} else
								timer = defaultTime;
						}
						
						return false;
					}
					
					@Override
					public void managePlayers() {
						GameRule switchRule = rules.getGameRuleById("ball_out");
						switchRule.setRuleBreaker(ruleTriggerer);
						
						rules.setTriggeredRule(switchRule);
					}

					@Override
					public void createActions() {
						
					}

					@Override
					public GameRule[] createInnerRules() {
						
						return null;
					}

					@Override
					public String getDescription() {
						
						return "The Player That Is Holding The Ball Is Moving Without Dribbling It For A Total Of 3/4 Of A Second!";
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
				
				new GameRule(this, null, "basket_score", "SCORE!", map) {
					Player recentHolder;
					boolean holderInZone = false;
					
					boolean teamScore = false;
					
					@Override
					public GameRule[] createInnerRules() {
						
						return null;
					}

					@Override
					public void createActions() {
						
						
					}
					
					@Override
					public void managePlayers() {
						GameRule switchRule = rules.getGameRuleById("ball_out");
						switchRule.setRuleBreaker(ruleTriggerer);
						
						rules.setTriggeredRule(switchRule);
					}

					@Override
					public boolean checkRule() {
						Player holdingPlayer = map.getHoldingPlayer();
						if(holdingPlayer != null) {
							recentHolder = holdingPlayer;
							
							holderInZone = holdingPlayer.isInAwayBasketZone();
						}
						
						if (map.getBall().getLinearVelocity().y < 0) {
							if (map.getBall().isCollidedWTeamBasket()) {
								teamScore = false;
								map.scoreOpp(!holderInZone);
								
								ruleTriggerer = recentHolder;
								setOccurPlace();
								return true;
							} else if (map.getBall().isCollidedWOppBasket()) {
								teamScore = true;
								map.scoreTeam(!holderInZone);

								ruleTriggerer = recentHolder;
								setOccurPlace();
								return true;
							}
						}
						
						
						return false;
					}
					
					private void setOccurPlace() {
						/*Vector3 ballPos = map.getBall().getPosition();*/
						Vector3 basketPos;
						/*float compatibChange = map.getBall().getWidth() / 2 + Terrain.getWalldepth();*/
						
						if(teamScore)
							basketPos = map.getAwayBasket().getPosition();
						else basketPos = map.getHomeBasket().getPosition();
						
						occurPlace.set(basketPos).x = map.getTerrain().getWidth() / 4;
					}
					
					@Override
					public Color getTextColor() {
						if(teamScore)
							return Color.BLUE;
						
						return Color.RED;
					}

					@Override
					public String getDescription() {
						if(teamScore)
							return "Your Team Has Just Scored!";
						
						return "The Opposite Team Has Just Scored!";
					}
					
				}
		};
		
	}
	
	public void update() {
		if(triggeredRule == null) {
			for (GameRule rule : gameRules) {
				if (rule.checkRule()) {
					// A rule has been broken
					triggeredRule = rule;
					map.onRuleTriggered(rule);
					rulesListener.onRuleTriggered(rule);
	
					break;
				}
			} // TODO Bring this back after you finish testing the rules!!!

			//GameRule tempRule = gameRules[1];
			/*GameRule[] ruleTest = new GameRule[] {getGameRuleById("basket_score")};
			for (GameRule r : ruleTest)
				if (r.checkRule()) {
					setTriggeredRule(r);
				}*/
		}
		else {
			triggeredRule.managePlayers();
		}
	}
	
	public void setTriggeredRule(GameRule rule) {
		if(rule.getParent() != null || triggeredRule == null) {
			map.onRuleTriggered(rule);
			rulesListener.onRuleTriggered(rule);
		}
		
		triggeredRule = rule;
	}
	
	public void clearTriggeredRule() {
		triggeredRule = null;
	}
	
	public void clearTriggeredRuleWRuleBreaker() {
		triggeredRule.clearRuleTriggerer();
		triggeredRule = null;
	}
	
	public GameRule getTriggeredRule() {
		return triggeredRule;
	}
	
	public GameRule getGameRuleById(String id) {
		for(GameRule r : gameRules) {
			if(r.getId().equals(id))
				return r;
		}
		
		return null;
	}
	
	public GameRule[] getGameRules() {
		return gameRules;
	}
	
	public static abstract class GameRule{
		String name;
		String id;
		GameMap map;
		
		Actions actions;
		
		Player ruleTriggerer;
		
		Rules rules;
		
		GameRule parent;
		GameRule[] innerRules;//Rules of the rule
		
		static final Vector3 occurPlace = new Vector3();
		
		public GameRule(Rules rules, GameRule parent, String id, String name, GameMap map) {
			this.id = id;
			this.name = name;
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
					rules.setTriggeredRule(parent);
					//return false;
				}
				else rules.clearTriggeredRuleWRuleBreaker();
				
				return;
				//return true;
			}
			
			if(innerRules != null && map.isGameRunning())
				for(GameRule r : innerRules)
					if(r.checkRule()) {
						rules.setTriggeredRule(r);
						actions.firstAction();
						//return false;
					}
			
			//return false;
		}
		
		public Actions getActions() {
			return actions;
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

		public abstract String getDescription();
		
		public Color getTextColor() {
			return Color.RED.cpy().sub(0.3f, 0, 0, 0);
		}

		/**
		 * Called when the game continues after the foul
		 */
		public void clearRuleTriggerer() {
			actions.firstAction();
			ruleTriggerer = null;
		}
		
		public void setRuleBreaker(Player ruleBreaker) {
			this.ruleTriggerer = ruleBreaker;
		}
		
		public Player getRuleBreaker() {
			return ruleTriggerer;
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
			
			public void copyActions(Actions actions) {
				while(actions.getCurrentAction() != null) {
					addAction(actions.getCurrentAction());
					
					actions.nextAction();
				}
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
				 * @return true if the action is completed
				 */
				public abstract boolean act();
				
				/**
				 * 
				 * @return true if the action is dependent on the game and the game should start (it doesn't clear the broken rule or rule breaker)
				 */
				public abstract boolean isGameDependent();
				
			}
		}
	}
	
	public interface RulesListener{
		
		public void onRuleTriggered(GameRule rule);
	}
}
