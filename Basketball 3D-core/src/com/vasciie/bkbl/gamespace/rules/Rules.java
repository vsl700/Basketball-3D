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
import com.vasciie.bkbl.GameMessageListener;
import com.vasciie.bkbl.GameMessageSender;
import com.vasciie.bkbl.gamespace.GameMap;
import com.vasciie.bkbl.gamespace.entities.Ball;
import com.vasciie.bkbl.gamespace.entities.Entity;
import com.vasciie.bkbl.gamespace.entities.Player;
import com.vasciie.bkbl.gamespace.entities.players.Opponent;
import com.vasciie.bkbl.gamespace.entities.players.Teammate;
import com.vasciie.bkbl.gamespace.objects.Terrain;
import com.vasciie.bkbl.gamespace.rules.Actions.Action;
import com.vasciie.bkbl.gamespace.tools.GameTools;

/**
 * This class contains all the game rules and their actions if they go broken
 * 
 * @author studi
 *
 */
public class Rules implements GameMessageSender {
	
	final GameRule[] gameRules;
	GameRule triggeredRule;
	
	GameMap map;
	
	GameMessageListener rulesListener; //That's here because the GameScreen does not have any connection with the GameMap or Rules so I had to make the GameScreen an interface
	
	public Rules(GameMap map, GameMessageListener rulesListener) {
		this.map = map;
		this.rulesListener = rulesListener;
		
		gameRules = new GameRule[] {
				new GameRule(this, null, "ball_out", "Out Of Bounds!", map) {
					Player recentHolder, thrower;
					boolean justTouched, basketUpsideDown;
					
					
					@Override
					public boolean checkRule() {
						Player tempPlayer = map.getHoldingPlayer();
						
						if(thrower != null && !thrower.isBallFree())
							return false;
						
						if (tempPlayer == null && (recentHolder != null && recentHolder.isAbleToCatch() || recentHolder == null)) {//If there is currently no holding player
							if((map.getBall().isCollidedWTeamBasket() || map.getBall().isCollidedWOppBasket()) && map.getBall().getLinearVelocity().y > 0.5f && map.getBall().getPosition().y - map.getHomeBasket().getBasketTargetTrans().getTranslation(new Vector3()).y < 0) {
								ruleTriggerer = recentHolder;
								basketUpsideDown = true;
								setOccurPlace();
								return true;
							}
							
							for (btCollisionObject obj : map.getBall().getOutsideColliders()) {
								for (Player player : map.getAllPlayers()) {
									if (player.getAllCollObjects().contains(obj)) {
										recentHolder = player;// Make the recent holder a player that has recently just touched the ball
										justTouched = true;
									}
								}
							}
						}else if(tempPlayer != null) {
							recentHolder = tempPlayer;
							justTouched = false;
						}
						
						if(thrower != null && thrower.equals(recentHolder))
							return false;
						
						if (recentHolder != null) {//If the ball is still not ever touched, don't check for terrain bounds collision (CPU economy)
							if(!recentHolder.isBallFree()) {
								justTouched = false;
								//return false;
							}
							
							for (btCollisionObject obj : map.getBall().getOutsideColliders()) {
								if (map.getTerrain().getInvisBodies().contains(obj)) {
									ruleTriggerer = recentHolder;
									basketUpsideDown = false;
									
									ArrayList<Vector3> wallPositions = new ArrayList<Vector3>(8);
									Terrain terrain = map.getTerrain();
									
									for(int i = 1; i < 5; i++) {
										Vector3 wallPos = terrain.getMatrixes().get(i).getTranslation(new Vector3());
										wallPos.y = map.getBall().getPosition().y;
										
										float changer, compatibChange = map.getBall().getWidth() / 2 + Terrain.getWalldepth();
										
										if(wallPos.x == 0) {//Basewall
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
									
									occurPlace.set(GameTools.getShortestDistanceWVectors(map.getBall().getPosition(), wallPositions)).y = 0;
									
									/*if(!recentHolder.isCurrentlyAiming() && !recentHolder.isShooting())
										map.playerReleaseBall();*/
									
									return true;
								}
							}
						}
						
						return false;
					}
					
					private void setOccurPlace() {
						/*Vector3 ballPos = map.getBall().getPosition();*/
						Vector3 basketPos;
						/*float compatibChange = map.getBall().getWidth() / 2 + Terrain.getWalldepth();*/
						
						if(map.getBall().isCollidedWTeamBasket())
							basketPos = map.getAwayBasket().getPosition();
						else basketPos = map.getHomeBasket().getPosition();
						
						float setter = map.getTerrain().getWidth() / 4;
						if(map.getBall().getPosition().x < 0)
							setter = -setter;
						
						occurPlace.set(basketPos).x = setter;
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
								justTouched = false;
								
								map.playerReleaseBall();
								
								ArrayList<Player> allPlayers = map.getAllPlayers();
								
								if(ruleTriggerer instanceof Teammate) {
									thrower = GameTools.getClosestPlayer(map.getBall().getPosition(), map.getOpponents(), null);
								}else {
									thrower = GameTools.getClosestPlayer(map.getBall().getPosition(), map.getTeammates(), null);
								}
								//thrower = map.getMainPlayer();
								recentHolder = thrower;
								
								
								//Vector3 posGroupPos = occurPlace.cpy().nor().scl(5);
								// This is supposed to be a position right in front of
								// the thrower's sight, and then random coordinates from
								// -3 to 3 z-axis and -1.5 to 1.5 x-axis will be .mul()-ed by
								// this matrix, so that the other players get in random
								// positions in front of the thrower. Also the group
								// should be pointed at the thrower.
								//Matrix4 positionsCalc = new Matrix4().setToLookAt(posGroupPos, thrower.getPosition(), new Vector3(0, -1, 0)).trn(posGroupPos);
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
										p.getBrain().setCustomVecTarget(tempTrans.getTranslation(new Vector3()), true);
										//p.getBrain().getMemory().setTargetFacing(p.getBrain().getMemory().getTargetPosition());
										p.getBrain().getCustomPursue().setArrivalTolerance(thrower.getWidth() * 1.3f);
										p.getBrain().getMemory().setTargetPlayer(thrower);
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
									//recentHolder.getBrain().clearCustomTarget();
									
									if (Math.abs(occurPlace.z) >= 26) {
										float diff = 5;
										if (occurPlace.x < 0)
											occurPlace.x = Math.min(-diff, occurPlace.x);
										else
											occurPlace.x = Math.max(diff, occurPlace.x);
									}
									//map.setPlayerTargetPosition(occurPlace, recentHolder);
									recentHolder.getBrain().setCustomVecTarget(occurPlace, true);
									recentHolder.getBrain().getCustomPursue().setArrivalTolerance(recentHolder.getWidth());
									
									
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
								if(thrower instanceof Teammate && map.getTeammates().size() == 1 || thrower instanceof Opponent && map.getOpponents().size() == 1)
									return true;
								
								// We also check whether it is holding or not,
								// because when the action starts, the player
								// won't be aiming or shooting, which will
								// result in ending the action before it starts
								if(!thrower.isHoldingBall() && !thrower.isCurrentlyAiming() && !thrower.isShooting())
									return true;
								
								if (!thrower.isMainPlayer()) {
									thrower.focus(GameTools.playersOutOfZone(thrower, thrower.getAwayZone()), true);

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
										/*Vector3 throwerPos = thrower.getPosition();
										Vector3 occurPlaceCpy = occurPlace.cpy();*/
										
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
										
										//float checkConst = /*Terrain.getWalldepth() * 1.6f*/ 3;
										if(thrower.getPosition().dst(occurPlace) > 3.5f/* && throwerPos.dst(secondCloseWallPos) > checkConst*/) {
											parent.setRuleTriggerer(ruleTriggerer = thrower);
											//map.playerReleaseBall();
											return true;
										}
										
										
										return false;
									}

									@Override
									public String getDescription() {
										
										return "The Thrower Should Stay Around The Foul Occuring Place During Throw-in!";
									}

									@Override
									public void resetRule() {
										
										
									}
									
								},
								
								new GameRule(rules, this, "time_out", "Time Out!", map) {
									final float defaultTime = 5;
									float time = defaultTime;;
									
									
									@Override
									public void resetRule() {
										time = defaultTime;
										
									}

									@Override
									public GameRule[] createInnerRules() {
										
										return null;
									}

									@Override
									public void createActions() {
										
										
									}

									@Override
									public boolean checkRule() {
										if(time < 0) {
											parent.setRuleTriggerer(ruleTriggerer = thrower);
											map.playerReleaseBall();
											
											return true;
										}
										
										time -= Gdx.graphics.getDeltaTime();
											
										return false;
									}

									@Override
									public String getDescription() {
										
										return "You Cannot Hold The Ball For More Than 5 Seconds During Throw-in!";
									}
									
								},
								
								new GameRule(rules, this, "too_close", "Too Close!", map) {
									@Override
									public void resetRule() {
										
										
									}

									@Override
									public GameRule[] createInnerRules() {
										
										return null;
									}

									@Override
									public void createActions() {
										
										
									}

									@Override
									public boolean checkRule() {
										if(map.getHoldingPlayer() == null)
											return false;
										
										for(Player p : map.getAllPlayers()) {
											if(p.equals(map.getHoldingPlayer()))
												continue;
											
											if(p.getPosition().dst(map.getHoldingPlayer().getPosition()) < p.getWidth() + p.getArmHeight()) {
												parent.setRuleTriggerer(ruleTriggerer = p);
												return true;
											}
										}
											
										return false;
									}

									@Override
									public String getDescription() {
										
										return "Players Should Not Be Too Close To The Thrower-in!";
									}
									
								}
						};
						
						return gameRules;
					}

					@Override
					public String getDescription() {
						if(basketUpsideDown)
							return "The Ball Has Entered Into The Basket From Its Bottom!";
						
						String text = "The Ball Has Reached The Bounds Of The Terrain";
						if(justTouched) {
							String temp;
							if(ruleTriggerer instanceof Teammate)
								temp = "A Teammate";
							else temp = "An Opponent";
							
							return text + " After " + temp + " Touched The Ball!";
						}
						
						
						return text + "!";
					}

					@Override
					public void resetRule() {
						recentHolder = thrower = null;
						
						for(GameRule rule : innerRules)
							rule.resetRule();
					}
				},
				
				new GameRule(this, null, "incorrect_ball_steal", "Reached In!", map) {
					Player recentHolder;
					
					@Override
					public boolean checkRule() {
						Player temp = map.getHoldingPlayer();
						
						if (temp != null) {
							if(!temp.equals(recentHolder)) {
								recentHolder = temp;
								return false;
							}
							
							for (btCollisionObject obj : map.getBall().getOutsideColliders()) {
								// The checked entity would be always a player
								// as the players and the ball are the only
								// entities and an entity cannot collide with
								// its own collision objects
								Entity tempE = map.getCollObjsInEntityMap().get(obj);
								if(!(tempE instanceof Player)) continue;//Well sometimes crashes occur
								
								Player checked = (Player) tempE;

								if(checked instanceof Teammate && temp instanceof Teammate || checked instanceof Opponent && temp instanceof Opponent)
									continue;
								
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
									p.getBrain().getMemory().setTargetPlayer(holdingPlayer);
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

							private void unlockRunning(Player holdingPlayer) {
								for(Player p : map.getAllPlayers()) {
									if(p.equals(holdingPlayer))
										continue;
									
									p.setAbleToRun(true);
								}
							}
							
							@Override
							public boolean act() {
								Player holdingPlayer = map.getHoldingPlayer();
									
								/*if(holdingPlayer == null && (map.getRecentHolder() != null && map.getRecentHolder().getBrain().getMemory().isBallJustShot() || map.getRecentHolder() == null)) {
									unlockRunning(holdingPlayer);
									return true;
								}else */if(holdingPlayer == null) holdingPlayer = map.getRecentHolder();
								
								boolean tooCloseOrBehind = holdingPlayer.getBrain().tooCloseOrBehindBasket();
								
								if((!tooCloseOrBehind || holdingPlayer.getBrain().isShooting()) && holdingPlayer.isInAwayBasketZone()) {
									if(!holdingPlayer.getBrain().updateShooting(1.25f)) {
										holdingPlayer.getBrain().clearCustomTarget();
										holdingPlayer.getBrain().performShooting(holdingPlayer.getBrain().makeBasketTargetVec(holdingPlayer.getTargetBasket()));
									}else if(!holdingPlayer.getBrain().isShooting() && map.getRecentHolder().getBrain().getMemory().isBallJustShot()) {
										unlockRunning(holdingPlayer);
										
										return true;
									}
								}else if (tooCloseOrBehind) {
									holdingPlayer.getBrain().getPursueBallInHand2().setEnabled(true);
									holdingPlayer.getBrain().getCustomPursue().setEnabled(false);
									
									holdingPlayer.setRunning();
								}else {
									holdingPlayer.getBrain().getPursueBallInHand2().setEnabled(false);
									holdingPlayer.getBrain().getCustomPursue().setEnabled(true);
									
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

					@Override
					public void resetRule() {
						
						
					}
				},
				
				new GameRule(this, null, "stay_no_dribble", "Dribble Violation!", map) {
					Player recentHolder;
					
					final float defaultTime = 3;
					float timer = defaultTime;
					
					@Override
					public boolean checkRule() {
						Player temp = map.getHoldingPlayer();
						if(temp == null || !temp.equals(recentHolder)) {
							timer = defaultTime;
							
							if(temp != null)
								recentHolder = temp;
							
							return false;
						}
						
						if (!temp.isDribbling()) {
							if (timer <= 0) {
								timer = defaultTime;

								ruleTriggerer = temp;
								
								ArrayList<Vector3> wallPositions = new ArrayList<Vector3>(8);
								Terrain terrain = map.getTerrain();
								
								for(int i = 1; i < 5; i++) {
									Vector3 wallPos = terrain.getMatrixes().get(i).getTranslation(new Vector3());
									wallPos.y = temp.getPosition().y;
									
									float changer, compatibChange = map.getBall().getWidth() / 2 + Terrain.getWalldepth();
									
									if(wallPos.x == 0) {//Basewall
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
						switchRule.setRuleTriggerer(ruleTriggerer);
						
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

					@Override
					public void resetRule() {
						timer = defaultTime;
						
					}
				},
				
				new GameRule(this, null, "move_no_dribble", "Dribble Violation!", map) {
					Player recentHolder;
					
					final float defaultTime = 0.75f;
					float timer = defaultTime;
					
					@Override
					public boolean checkRule() {
						Player temp = map.getHoldingPlayer();
						if(temp == null || !temp.equals(recentHolder)) {
							timer = defaultTime;
							
							if(temp != null)
								recentHolder = temp;
							
							return false;
						}
						
						if (!temp.getPrevMoveVec().isZero() || !temp.getMoveVector().isZero()) {
							if (!temp.isDribbling() && !temp.isShooting() && !temp.isAiming() && !temp.isCurrentlyAiming()) {
								if (timer <= 0) {
									timer = defaultTime;

									ruleTriggerer = temp;
									
									ArrayList<Vector3> wallPositions = new ArrayList<Vector3>(8);
									Terrain terrain = map.getTerrain();
									
									for(int i = 1; i < 5; i++) {
										Vector3 wallPos = terrain.getMatrixes().get(i).getTranslation(new Vector3());
										wallPos.y = temp.getPosition().y;
										
										float changer, compatibChange = map.getBall().getWidth() / 2 + Terrain.getWalldepth();
										
										if(wallPos.x == 0) {//Basewall
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
						switchRule.setRuleTriggerer(ruleTriggerer);
						
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

					@Override
					public void resetRule() {
						timer = defaultTime;
						
					}
				},
				
				new GameRule(this, null, "backcourt_violation", "Backcourt Violation!", map) {
					Player recentHolder, prevHoldingPlayer, actor;
					boolean crossed, justTouched;
					
					@Override
					public boolean checkRule() {
						Player holdingPlayer = map.getHoldingPlayer();
						Ball ball = map.getBall();
						
						//System.out.println(holdingPlayer);
						if (holdingPlayer == null) {
							for (btCollisionObject obj : map.getBall().getOutsideColliders()) {
								for (Player player : map.getAllPlayers()) {
									if (player.getAllCollObjects().contains(obj)) {
										if(recentHolder != null && (recentHolder instanceof Teammate && player instanceof Opponent || recentHolder instanceof Opponent && player instanceof Teammate)) {
											crossed = false;
											/*System.out.println("Cross Cleared");
											System.out.println();*/
										}
											
										recentHolder = player;// Make the recent holder a player that has recently just touched the ball
										justTouched = true;
									}
								}
							}
						}else {
							if(prevHoldingPlayer == null || recentHolder instanceof Teammate && holdingPlayer instanceof Opponent || recentHolder instanceof Opponent && holdingPlayer instanceof Teammate) {
								crossed = holdingPlayer.isInAwayZone();
								/*System.out.println("Cross Reset");
								System.out.println(crossed);
								System.out.println();*/
							}
							
							recentHolder = holdingPlayer;
							justTouched = false;
						}
						
						prevHoldingPlayer = holdingPlayer;
						
						if(recentHolder == null)
							return false;
						
						if(crossed) {
							if((!recentHolder.isCurrentlyAiming() && !recentHolder.isShooting() || !recentHolder.getMoveVector().isZero()) && recentHolder.getHomeZone().checkZone(ball.getPosition(), ball.getDimensions()) && (recentHolder.getBrain().getMemory().isBallJustShot() && ball.getPosition().dst(recentHolder.getPosition()) >= recentHolder.getHeight() / 1.5f || !recentHolder.getBrain().getMemory().isBallJustShot())) {
								ruleTriggerer = recentHolder;
								occurPlace.set(ball.getPosition()).scl(1, 0, 0);
								
								if(!recentHolder.isCurrentlyAiming() && !recentHolder.isShooting())
									map.playerReleaseBall();
								
								return true;
							}
						}else/* if(recentHolder.getBrain().getMemory().isBallJustShot() && ball.getPosition().dst(recentHolder.getPosition()) >= recentHolder.getHeight() / 1.5f || !recentHolder.getBrain().getMemory().isBallJustShot())*/ {
							crossed = holdingPlayer != null && holdingPlayer.isInAwayZone() || holdingPlayer == null && recentHolder.getAwayZone().checkZone(ball.getPosition(), ball.getDimensions());
							/*if(crossed) {
								System.out.println("Cross Changed");
								System.out.println(holdingPlayer);
								System.out.println(recentHolder);
								System.out.println();
							}*/
						}
						
						//System.out.println(crossed);
						
						return false;
					}

					@Override
					public GameRule[] createInnerRules() {
						
						return null;
					}

					@Override
					public void createActions() {
						actions.addAction(new Action() {

							@Override
							public boolean act() {
								map.playerReleaseBall();
								
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
								if (actor == null) {
									if (ruleTriggerer instanceof Teammate)
										actor = GameTools.getClosestPlayer(map.getBall().getPosition(), map.getOpponents(), null);
									else
										actor = GameTools.getClosestPlayer(map.getBall().getPosition(), map.getTeammates(), null);
								}
								
								if(!actor.isHoldingBall() && actor.getBrain().getMemory().getTargetPosition() == null) {
									actor.getBrain().setCustomTarget(map.getBall());
									actor.getBrain().getMemory().setTargetFacing(map.getBall());
									actor.getBrain().getMemory().setCatchBall(true);
									actor.getBrain().getCustomPursue().setArrivalTolerance(0);
									
									for(Player p : map.getAllPlayers()) {
										if(p.equals(actor))
											continue;
										
										p.getBrain().getMemory().setTargetPlayer(actor);
									}
								}else if (actor.isHoldingBall()){
									if(actor.getPosition().dst(occurPlace) < 2.5f)
										return true;
									
									actor.getBrain().setCustomVecTarget(occurPlace, true);
									actor.getBrain().getCustomPursue().setArrivalTolerance(2);
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
								actor = null;
								
								return true;
							}

							@Override
							public boolean isGameDependent() {
								
								return true;
							}
							
						});
					}

					@Override
					public String getDescription() {
						String text = "The Team That Has The Ball Cannot Let The Ball Cross The Midcourt Line Once It Got In Their Opposite's Team Zone!";
						if(justTouched) {
							String temp;
							if(ruleTriggerer instanceof Teammate)
								temp = "A Teammate";
							else temp = "An Opponent";
							
							return text + " The Foul Occured " + " After " + temp + " Touched The Ball!";
						}
						
						
						return text;
					}

					@Override
					public void resetRule() {
						crossed = false;
						recentHolder = null;
						prevHoldingPlayer = null;
					}
				},

				new GameRule(this, null, "walk_shoot", "Walk Shooting!", map) {
					final float defaultTime = 1;
					float time = defaultTime;
					
					@Override
					public void resetRule() {
						time = defaultTime;
						
					}

					@Override
					public GameRule[] createInnerRules() {
						
						return null;
					}

					@Override
					public void managePlayers() {
						GameRule switchRule = rules.getGameRuleById("ball_out");
						switchRule.setRuleTriggerer(ruleTriggerer);
						
						rules.setTriggeredRule(switchRule);
					}
					
					@Override
					public void createActions() {
						
						
					}

					@Override
					public boolean checkRule() {
						Player holdingPlayer = map.getHoldingPlayer();
						if(holdingPlayer == null || !holdingPlayer.isCurrentlyAiming() && !holdingPlayer.isShooting()) {
							time = defaultTime;
							return false;
						}
						
						if(time < 0) {
							ruleTriggerer = holdingPlayer;
							
							ArrayList<Vector3> wallPositions = new ArrayList<Vector3>(8);
							Terrain terrain = map.getTerrain();
							
							for(int i = 1; i < 5; i++) {
								Vector3 wallPos = terrain.getMatrixes().get(i).getTranslation(new Vector3());
								wallPos.y = holdingPlayer.getPosition().y;
								
								float changer, compatibChange = map.getBall().getWidth() / 2 + Terrain.getWalldepth();
								
								if(wallPos.x == 0) {//Basewall
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
							
							occurPlace.set(GameTools.getShortestDistanceWVectors(holdingPlayer.getPosition(), wallPositions));
							
							return true;
						}else if(!holdingPlayer.getPrevMoveVec().isZero() || !holdingPlayer.getMoveVector().isZero()) {
							time -= Gdx.graphics.getDeltaTime();
						}
							
						
						return false;
					}

					@Override
					public String getDescription() {
						
						return "A Shooting Player Cannot Walk At The Same Time For More Than A Total Of 1 Second!";
					}
					
				},
				
				new GameRule(this, null, "free_throw", "Free Throw Violation!", map) {
					Player recentHolder;
					boolean inside;
					
					@Override
					public void resetRule() {
						inside = false;
						
					}
					
					@Override
					public void managePlayers() {
						GameRule switchRule = rules.getGameRuleById("ball_out");
						switchRule.setRuleTriggerer(ruleTriggerer);
						
						rules.setTriggeredRule(switchRule);
					}

					@Override
					public GameRule[] createInnerRules() {
						
						return null;
					}

					@Override
					public void createActions() {
						
						
					}

					@Override
					public boolean checkRule() {
						if(map.getDifficulty() < 2)
							return false;
						
						Player holdingPlayer = map.getHoldingPlayer();
						if(holdingPlayer == null && recentHolder == null)
							return false;
						
						if(holdingPlayer != null) {
							if(recentHolder != null && !recentHolder.equals(holdingPlayer))
								inside = false;
							
							recentHolder = holdingPlayer;
						}
						
						if(inside) {
							boolean isInZone = recentHolder.isInAwayBasketZone();
							
							if(!isInZone && !recentHolder.isPlayerColl()) {
								ruleTriggerer = recentHolder;
								setOccurPlace();
								return true;
							}else {
								if(recentHolder.isPlayerColl() && !isInZone) {
									inside = false;
									return false;
								}
								
								Ball ball = map.getBall();
								
								if(ball.getOutsideColliders().contains(recentHolder.getTargetBasket().getBasketRim())) {
									inside = false;
									recentHolder = null;
								}
							}
						}else
							inside = recentHolder.isInAwayBasketZone() && recentHolder.isCurrentlyAiming();
						
						return false;
					}
					
					private void setOccurPlace() {
						Vector3 basketPos;
						
						if(recentHolder instanceof Teammate)
							basketPos = map.getAwayBasket().getPosition();
						else basketPos = map.getHomeBasket().getPosition();
						
						float setter = map.getTerrain().getWidth() / 4;
						if(map.getBall().getPosition().x < 0)
							setter = -setter;
						
						occurPlace.set(basketPos).x = setter;
					}

					@Override
					public String getDescription() {
						
						return "Once A Player Starts Shooting From The Free-Throw Zone He Cannot Get Out Of It Until The Ball Touches The Rim Of The Basket Or Anyone Else Catches It!";
					}
					
				},
				
				new GameRule(this, null, "shoot_catch", "Catch Violation!", map) {
					Player recentHolder;
					boolean ballJustShot, currentlyHolding;
					
					@Override
					public void resetRule() {
						recentHolder = null;
						ballJustShot = currentlyHolding = false;
					}
					
					@Override
					public void managePlayers() {
						GameRule switchRule = rules.getGameRuleById("ball_out");
						switchRule.setRuleTriggerer(ruleTriggerer);
						
						rules.setTriggeredRule(switchRule);
					}

					@Override
					public GameRule[] createInnerRules() {
						
						return null;
					}

					@Override
					public void createActions() {
						
						
					}

					@Override
					public boolean checkRule() {
						Player holdingPlayer = map.getHoldingPlayer();
						if(holdingPlayer == null && recentHolder == null)
							return false;
						
						if(ballJustShot) {
							if(recentHolder != null && holdingPlayer != null) {
								if (holdingPlayer.equals(recentHolder)) {
									if(recentHolder instanceof Teammate && map.getTeammates().size() == 1 || recentHolder instanceof Opponent && map.getOpponents().size() == 1)
										return false;
									
									ruleTriggerer = recentHolder;

									ArrayList<Vector3> wallPositions = new ArrayList<Vector3>(8);
									Terrain terrain = map.getTerrain();

									for (int i = 1; i < 5; i++) {
										Vector3 wallPos = terrain.getMatrixes().get(i).getTranslation(new Vector3());
										wallPos.y = recentHolder.getPosition().y;

										float changer, compatibChange = map.getBall().getWidth() / 2 + Terrain.getWalldepth();

										if (wallPos.x == 0) {// Basewall
											changer = terrain.getWidth() / 4;

											if (wallPos.z < 0)
												compatibChange = -compatibChange;

											wallPositions.add(wallPos.cpy().add(changer, 0, -compatibChange));
											wallPositions.add(wallPos.sub(changer, 0, compatibChange));
										} else {// Sidewall
											changer = terrain.getDepth() / 4;

											if (wallPos.x < 0)
												compatibChange = -compatibChange;

											wallPositions.add(wallPos.cpy().add(-compatibChange, 0, changer));
											wallPositions.add(wallPos.sub(compatibChange, 0, changer));
										}
									}

									occurPlace.set(GameTools.getShortestDistanceWVectors(recentHolder.getPosition(), wallPositions));

									return true;
								} else {
									recentHolder = holdingPlayer;
									ballJustShot = false;
									currentlyHolding = false;
								}
							}
						}else {
							if(holdingPlayer != null)
								recentHolder = holdingPlayer;
							
							boolean temp = recentHolder.isHoldingBall();
							
							if(!temp && currentlyHolding)
								ballJustShot = true;
							
							currentlyHolding = temp;
						}
						
						return false;
					}

					@Override
					public String getDescription() {
						
						return "A Player That Has Just Shot The Ball Cannot Catch It Right Away Again!";
					}
					
				},
				
				new GameRule(this, null, "ball_moving", "Movement Violation!", map) {
					final float defaultTime = 0.35f;
					float time = defaultTime;
					
					
					@Override
					public void managePlayers() {
						GameRule switchRule = rules.getGameRuleById("ball_out");
						switchRule.setRuleTriggerer(ruleTriggerer);
						
						rules.setTriggeredRule(switchRule);
					}
					
					@Override
					public void resetRule() {
						time = defaultTime;
						
					}

					@Override
					public GameRule[] createInnerRules() {
						
						return null;
					}

					@Override
					public void createActions() {
						
						
					}

					@Override
					public boolean checkRule() {
						if(map.getHoldingPlayer() != null/* || !map.getBall().isGrounded()*/) {
							time = defaultTime;
							return false;
						}
						
						for (btCollisionObject obj : map.getBall().getOutsideColliders()) {
							for (Player player : map.getAllPlayers()) {
								if (player.getMainBody().equals(obj) && !player.getMoveVector().isZero()) {
									time -= Gdx.graphics.getDeltaTime();
									if (time < 0) {
										ruleTriggerer = player;

										ArrayList<Vector3> wallPositions = new ArrayList<Vector3>(4);
										Terrain terrain = map.getTerrain();

										for (int i = 1; i < 5; i++) {
											Vector3 wallPos = terrain.getMatrixes().get(i).getTranslation(new Vector3());
											wallPos.y = player.getPosition().y;

											float changer, compatibChange = map.getBall().getWidth() / 2 + Terrain.getWalldepth();

											if (wallPos.z == 0) {// Sidewall
												changer = terrain.getDepth() / 4;

												if (wallPos.x < 0)
													compatibChange = -compatibChange;

												wallPositions.add(wallPos.cpy().add(-compatibChange, 0, changer));
												wallPositions.add(wallPos.sub(compatibChange, 0, changer));

												if (wallPositions.size() == 4)
													break;
											}
										}

										occurPlace.set(GameTools.getShortestDistanceWVectors(player.getPosition(), wallPositions));

										return true;
									}

									break;
								}
							}
						}
						
						
						return false;
					}

					@Override
					public String getDescription() {
						
						return "Players Cannot Move The Ball By Not Holding It!";
					}
					
				},
				
				new GameRule(this, null, "bring_time", "Time Out!", map) {
					Player recentHolder;
					
					final float defaultTime = 6;
					float time = defaultTime;
					
					
					@Override
					public void resetRule() {
						time = defaultTime;
						
					}
					
					@Override
					public void managePlayers() {
						GameRule switchRule = rules.getGameRuleById("ball_out");
						switchRule.setRuleTriggerer(ruleTriggerer);
						
						rules.setTriggeredRule(switchRule);
					}

					@Override
					public GameRule[] createInnerRules() {
						
						return null;
					}

					@Override
					public void createActions() {
						
						
					}

					@Override
					public boolean checkRule() {
						if(map.getDifficulty() == 0)
							return false;
						
						Player holdingPlayer = map.getHoldingPlayer();
						Ball ball = map.getBall();
						
						Player newRecentHolder = null;
						if (holdingPlayer == null) {//If there is currently no holding player
							for (btCollisionObject obj : map.getBall().getOutsideColliders()) {
								for (Player player : map.getAllPlayers()) {
									if (player.getAllCollObjects().contains(obj)) {
										newRecentHolder = player;// Make the recent holder a player that has recently just touched the ball
									}
								}
							}
						}else {
							newRecentHolder = holdingPlayer;
						}
						
						if (newRecentHolder != null) {
							if (newRecentHolder instanceof Teammate && recentHolder instanceof Opponent || newRecentHolder instanceof Opponent && recentHolder instanceof Teammate) {
								time = defaultTime;
							}

							recentHolder = newRecentHolder;
						}
						
						if (time < 0) {
							if (recentHolder.getHomeZone().checkZone(ball.getPosition(), ball.getDimensions())) {
								ruleTriggerer = recentHolder;

								ArrayList<Vector3> wallPositions = new ArrayList<Vector3>(4);
								Terrain terrain = map.getTerrain();

								for (int i = 1; i < 5; i++) {
									Vector3 wallPos = terrain.getMatrixes().get(i).getTranslation(new Vector3());
									wallPos.y = recentHolder.getPosition().y;

									float changer, compatibChange = map.getBall().getWidth() / 2 + Terrain.getWalldepth();

									if (wallPos.z == 0) {// Sidewall
										changer = terrain.getDepth() / 4;

										if (wallPos.x < 0)
											compatibChange = -compatibChange;

										wallPositions.add(wallPos.cpy().add(-compatibChange, 0, changer));
										wallPositions.add(wallPos.sub(compatibChange, 0, changer));

										if (wallPositions.size() == 4)
											break;
									}
								}

								occurPlace.set(GameTools.getShortestDistanceWVectors(recentHolder.getPosition(), wallPositions));

								return true;
							}else time = defaultTime; //We do that just because we want to reduce unnecessary zone checks
						}else time -= Gdx.graphics.getDeltaTime();
						
						
						return false;
					}

					@Override
					public String getDescription() {
						
						return "The Ball Must Get Out Of The Holding Player's Home Zone Within 6 Seconds!";
					}
					
				},
				
				new GameRule(this, null, "basket_score", "SCORE!", map) {
					boolean holderInZone = false, holderInThreePoint = false;
					
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
						switchRule.setRuleTriggerer(ruleTriggerer);
						
						rules.setTriggeredRule(switchRule);
					}

					@Override
					public boolean checkRule() {
						if(map.getRecentHolder() != null && (map.getRecentHolder().isCurrentlyAiming() || map.getRecentHolder().isShooting())) {
							
							holderInZone = map.getRecentHolder().isInAwayBasketZone();
							if(!holderInZone)
								holderInThreePoint = map.getRecentHolder().isInAwayThreePointZone();
							else holderInThreePoint = true;
						}else if (map.getBall().getLinearVelocity().y < 0) {
							if (map.getBall().isCollidedWTeamBasket()) {
								teamScore = false;
								map.scoreOpp(!holderInZone, !holderInThreePoint, map.getRecentHolder());
								
								ruleTriggerer = map.getRecentHolder();
								setOccurPlace();
								return true;
							} else if (map.getBall().isCollidedWOppBasket()) {
								teamScore = true;
								map.scoreTeam(!holderInZone, !holderInThreePoint, map.getRecentHolder());

								ruleTriggerer = map.getRecentHolder();
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
						
						float setter = map.getTerrain().getWidth() / 4;
						if(map.getBall().getPosition().x < 0)
							setter = -setter;
						
						occurPlace.set(basketPos).x = setter;
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

					@Override
					public void resetRule() {
						
					}
					
				}
		};
		
	}
	
	public boolean update(GameRule[] rules) {
		for(GameRule rule : rules)
			if(rule.checkRule()) {
				triggeredRule = rule;
				
				for(GameRule rule1 : gameRules)
					rule1.resetRule();
				
				return true;
			}
		
		return false;
	}
	
	public boolean update() {
		if(triggeredRule == null) {
			for (GameRule rule : gameRules) {
				if (rule.checkRule()) {
					// A rule has been triggered
					triggeredRule = rule;
	
					for(GameRule rule1 : gameRules)
						rule1.resetRule();
					
					return true;
				}
			}

			//GameRule tempRule = gameRules[1];
			/*GameRule[] ruleTest = new GameRule[] {getGameRuleById("bring_time")};
			for (GameRule r : ruleTest)
				if (r.checkRule()) {
					setTriggeredRule(r);
					
					for(GameRule rule1 : gameRules)
						rule1.onRuleTrigger();
				}*/
		}
		else {
			triggeredRule.managePlayers();
		}
		
		return false;
	}
	
	public void setTriggeredRule(GameRule rule) {
		if(rule.getParent() != null || triggeredRule == null) {
			map.onRuleTriggered(rule);
			rulesListener.sendMessage(rule.getName(), rule.getDescription(), rule.getTextColor(), this, true, false);
		}
		
		triggeredRule = rule;
	}
	
	public void resetRules() {
		for(GameRule rule : gameRules)
			rule.resetRule();
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
		
		/**
		 * Whenever any rule of the rules system triggers (not the specified one)
		 */
		public abstract void resetRule();
		
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
						for(GameRule rule : innerRules)
							rule.resetRule();
						
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
		
		public void setRuleTriggerer(Player ruleBreaker) {
			this.ruleTriggerer = ruleBreaker;
		}
		
		public Player getRuleTriggerer() {
			return ruleTriggerer;
		}
		
		
	}

	@Override
	public void messageReceived() {
		map.onMessageContinue();
		
	}
}
