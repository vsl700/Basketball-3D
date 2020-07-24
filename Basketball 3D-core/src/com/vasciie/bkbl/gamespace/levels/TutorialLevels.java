package com.vasciie.bkbl.gamespace.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.vasciie.bkbl.GameMessageListener;
import com.vasciie.bkbl.GameMessageSender;
import com.vasciie.bkbl.gamespace.GameMap;
import com.vasciie.bkbl.gamespace.entities.Player;
import com.vasciie.bkbl.gamespace.entities.players.Opponent;
import com.vasciie.bkbl.gamespace.rules.Actions;
import com.vasciie.bkbl.gamespace.rules.Actions.Action;
import com.vasciie.bkbl.gamespace.rules.Rules.GameRule;

public class TutorialLevels extends Levels {
	
	private static final Color textColor = Color.BLUE;

	public TutorialLevels(GameMap map, GameMessageListener messageListener) {
		super(map, messageListener);
		
		final Matrix4 tempMx = new Matrix4();
		gameLevels = new TutorialLevel[] {
				new TutorialLevel("basics", "Level 1: Basics", map, messageListener) {

					@Override
					protected LevelPart[] createLevelParts() {
						
						return new LevelPart[] {
								new LevelPart("movement", "Movement", map, messageListener) {

									@Override
									protected void createActions() {
										actions.addAction(new TutorialAction("Hello There!", "Welcome To The Basketball-3D's tutorial levels!", textColor));
										
										actions.addAction(new TutorialAction("Hello There!", "In This Level We Are Gonna Head Into The Basics Of The Game! Make Sure To Read Every Signle Instruction I'll Show You!", textColor));
										
										actions.addAction(new TutorialAction("Walking 'n Running!", "First I'm Gonna Teach You Some Walking And Mostly Running Basics Of This Game!", textColor));
										
										actions.addAction(new TutorialAction("Walking 'n Running!", "Walking Is Veeeery Very Simple! Just Use The WASD Buttons On The Keyboard And Keep Walking For Around 1 Second!", textColor, false) {
											final float defaultTime = 1.5f;
											float time = defaultTime;
											
											@Override
											public void setup() {
												super.setup();
												
												map.getMainPlayer().setAbleToRun(false);
											}
											
											@Override
											public boolean act() {
												if(time < 0) {
													time = defaultTime;
													map.getMainPlayer().setAbleToRun(true);
													return true;
												}else if(!map.getMainPlayer().getPrevMoveVec().isZero())
													time -= Gdx.graphics.getDeltaTime();
												
												return false;
											}
											
											@Override
											public boolean isGameDependent() {
												return true;
											}
											
										});
										
										actions.addAction(new TutorialAction("Walking 'n Running!", "Very Good! Now Running Is Also Simple But At The Same Time It Has Some Specific Things You Need To Know!", textColor));
										
										actions.addAction(new TutorialAction("Walking 'n Running!", "When Holding The Shift Button (Which Is For Running) You Can ONLY Go Forwards! If You Try To Strafe, The Player Will TURN Left & Right Instead!", textColor, false) {
											final float defaultTime = 3.5f;
											float time = defaultTime;
											
											
											@Override
											public boolean act() {
												if(time < 0) {
													time = defaultTime;
													return true;
												}else if(map.getMainPlayer().isCurrentlyRunning())
													time -= Gdx.graphics.getDeltaTime();
												
												return false;
											}
											
											@Override
											public boolean isGameDependent() {
												return true;
											}
											
										});
										
										actions.addAction(new TutorialAction("Walking 'n Running!", "Excellent! I Think You've Got The Basics Of Movement In Basketball-3D! Let's Get Going To The Next Steps!", textColor));
										
									}

									@Override
									public boolean updatePlayersNormalAI() {
										
										return false;
									}

									@Override
									public boolean usesOriginalRules() {
										
										return false;
									}

									@Override
									public boolean showPower() {
										
										return false;
									}
									
								},
								
								new LevelPart("ball_taking", "Ball Taking", map, messageListener) {

									@Override
									protected void createActions() {
										actions.addAction(new TutorialAction("Ball Interacting!", "Interacting With The Ball Is As Important As Movement, So I Shall Teach You Taking, Shooting And Also Dribbling The Ball!", textColor));
										
										actions.addAction(new TutorialAction("Ball Taking!", "To Take The Ball Which Is Sitted In The Center Of The Terrain, Just Go Next To It, Make Sure You Are Colliding With It And Press One Of The Mouse Buttons!", textColor));
										
										actions.addAction(new TutorialAction("Ball Taking!", "Left Mouse Button Controls Left Hand While Right Button Controls Right Hand! Controlling A Separate Hand Will Make The Player Take The Ball With That Hand!", textColor, false) {
											
											@Override
											public boolean act() {
												return map.getMainPlayer().isHoldingBall();
											}
											
											@Override
											public boolean isGameDependent() {
												return true;
											}
											
										});
										
										actions.addAction(new TutorialAction("Ball Taking!", "Well Done! Now I Just Want To See That Once Again! Go To The Blue Basket And Take The Ball!", textColor));
										
										actions.addAction(new Action() {

											@Override
											public boolean act() {
												map.playerReleaseBall();
												
												map.getBall().setWorldTransform(tempMx.setToTranslation(0, 0.5f, 25));
												
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
												
												return map.getMainPlayer().isHoldingBall();
											}

											@Override
											public boolean isGameDependent() {
												return true;
											}
											
										});
										
										actions.addAction(new TutorialAction("Ball Taking!", "Great! Now I Guess It's Time To Jump Into Ball Dribbling!", textColor));
										
									}

									@Override
									public boolean updatePlayersNormalAI() {
										return false;
									}

									@Override
									public boolean usesOriginalRules() {
										return false;
									}

									@Override
									public boolean showPower() {
										
										return false;
									}
									
								},
								
								new LevelPart("ball_dribble", "Ball Dribbling", map, messageListener) {

									@Override
									protected void createActions() {
										actions.addAction(new TutorialAction("Ball Dribble!", "You'll Probably Find Out That In EASY And HARD Gamemodes There's An Auto-dribble! Well Yes, But That Doesn't Prevent Other Players From Stealing The Ball From You!!!", textColor));
										
										actions.addAction(new TutorialAction("Ball Dribble!", "For This Reason You Should Learn Dribbling The Ball Yourself!", textColor));
										
										actions.addAction(new TutorialAction("Ball Dribble!", "First, Run To The Red Basket While Dribbling The Ball! Like I Said, Left Mouse Button Controls Left Hand While Right Button Controls Right Hand!", textColor));
										
										actions.addAction(new TutorialAction("Ball Dribble!", "Controlling The Hand You Are Not Holding The Ball With Makes The Player To Dribble The Ball TO His Other Hand (Or Hand Switching)! Otherwise, He Does A Normal Dribble!", textColor));
										
										actions.addAction(new TutorialAction("Ball Dribble!", "Try This Out And At The Same Time Run To The Red Basket! Make Sure You Dribble On Each 0.75 Seconds While Moving Or 3 While Not Moving! When Dribble Time Is About To Run Out You'll Get A Message To Dribble!", textColor));
										
										actions.addAction(new TutorialAction("", "", textColor) {
											final float defaultTime = 3, defaultTimeMove = 0.75f;
											float time = defaultTime, timeMove = defaultTimeMove;
											
											
											@Override
											protected void sendMessage() {
												messageListener.sendMessage(heading, desc, textColor, null, skippable, false);
											}
											
											@Override
											public boolean act() {
												if(map.getMainPlayer().isDribbling()) {
													time = defaultTime;
													timeMove = defaultTimeMove;
													
													messageListener.sendMessage("", "", textColor, null, skippable, false);
												}else if((time < 0 || timeMove < 0) && map.getMainPlayer().isHoldingBall()) {
													map.getMainPlayer().setWorldTransform(tempMx.setToTranslation(0, map.getMainPlayer().getHeight() / 1.5f, 25));
													time = defaultTime;
													timeMove = defaultTimeMove;
													
													messageListener.sendMessage("", "", textColor, null, skippable, false);
												}else if(!map.getMainPlayer().getPrevMoveVec().isZero() || !map.getMainPlayer().getMoveVector().isZero()) {
													timeMove -= Gdx.graphics.getDeltaTime();
													
													if(timeMove < 0.5f && map.getMainPlayer().isHoldingBall())
														messageListener.sendMessage("Dribble!", "", textColor, this, skippable, false);
												}else {
													time -= Gdx.graphics.getDeltaTime();
													
													if(time < 0.5f && map.getMainPlayer().isHoldingBall())
														messageListener.sendMessage("Dribble!", "", textColor, this, skippable, false);
												}
												
												if(map.getMainPlayer().isInAwayThreePointZone()) {
													time = defaultTime;
													timeMove = defaultTimeMove;
													return true;
												}
												
												return false;
											}
											
											@Override
											public boolean isGameDependent() {
												return true;
											}
											
										});
										
										actions.addAction(new TutorialAction("Ball Dribble!", "Perfect! Now The Same Way You Should Walk Or Run Back To The Blue Basket By Dribbling The Ball! I Won't Help You With Messages This Time!", textColor));
										
										actions.addAction(new Action() {
											final float defaultTime = 3, defaultTimeMove = 0.75f;
											float time = defaultTime, timeMove = defaultTimeMove;
											
											@Override
											public boolean act() {
												if(map.getMainPlayer().isDribbling()) {
													time = defaultTime;
													timeMove = defaultTimeMove;
												}else if((time < 0 || timeMove < 0) && map.getMainPlayer().isHoldingBall()) {
													map.getMainPlayer().setWorldTransform(tempMx.setToTranslation(0, map.getMainPlayer().getHeight() / 1.5f, -25));
													time = defaultTime;
													timeMove = defaultTimeMove;
												}else if(!map.getMainPlayer().getPrevMoveVec().isZero() || !map.getMainPlayer().getMoveVector().isZero())
													timeMove -= Gdx.graphics.getDeltaTime();
												else time -= Gdx.graphics.getDeltaTime();
												
												if(map.getMainPlayer().isInHomeThreePointZone()) {
													time = defaultTime;
													timeMove = defaultTimeMove;
													return true;
												}
												
												return false;
											}

											@Override
											public boolean isGameDependent() {
												return true;
											}
											
										});
										
										actions.addAction(new TutorialAction("Ball Dribble!", "Excellent! I Think You've Got It With Dribbling! Let's Go To The Last Step!", textColor));
										
									}

									@Override
									public boolean updatePlayersNormalAI() {
										return false;
									}

									@Override
									public boolean usesOriginalRules() {
										return false;
									}

									@Override
									public boolean showPower() {
										
										return false;
									}
									
								}, 
								
								new LevelPart("ball_shoot", "Ball Shooting", map, messageListener) {

									@Override
									protected void createActions() {
										actions.addAction(new TutorialAction("Ball Shooting!", "The Last Important Thing You Can Do With The Ball Is Shooting It No Matter To The Basket Or To A Player!", textColor));
										
										actions.addAction(new TutorialAction("Ball Shooting!", "For Now You Will Just Learn How To Shoot The Ball To A Basket!", textColor));
										
										actions.addAction(new TutorialAction("Ball Shooting!", "It's Important That You Measure The Shot According To THE HAND You Hold The Ball With!", textColor));
										
										actions.addAction(new TutorialAction("Ball Shooting!", "You Can Now Try Putting The Ball In The Blue Basket! Just Hold The CTRL Button And Release It After You Aim At The Basket! Scroll Wheel Or Q & E Keys Are For The Shooting Power! Score At Least 3 Points On The Basket!", textColor));
										
										TutorialAction tempAction = new TutorialAction("0 Scores!", "", textColor, false) {
											int scores;
											boolean shootPowerReg;
											boolean wait;
											boolean opposite, checked;
											
											Player tempMain;
											
											
											@Override
											protected void sendMessage() {
												messageListener.sendMessage(heading, desc, textColor, this, skippable, true);
											}
											
											@Override
											public boolean act() {
												if (!checked || tempMain != null && !tempMain.equals(map.getMainPlayer())) {
													if (map.getMainPlayer().getPosition().z < 0)
														opposite = true;
													else
														opposite = false;
													
													tempMain = map.getMainPlayer();
													
													checked = true;
												}
												
												if(!shootPowerReg)
													map.getMainPlayer().setShootingPower(10);
												
												shootPowerReg = true;
												
												if (!wait) {
													if (!opposite && map.getBall().isCollidedWTeamBasket() || opposite && map.getBall().isCollidedWOppBasket()) {
														if(map.getBall().getLinearVelocity().y < 0) {
															if (scores == 2 || opposite && scores == 1) {
																scores = 0;
																shootPowerReg = false;
																checked = false;

																return true;
															}

															scores++;
															messageListener.sendMessage(scores + (scores == 1 ? " Score!" : " Scores!"), "", textColor, this, skippable, true);
														}
														
														wait = true;
													}
												} else if (map.getMainPlayer().isHoldingBall())
													wait = false;
												
												return false;
											}
											
											@Override
											public boolean isGameDependent() {
												return true;
											}
											
										};
										actions.addAction(tempAction);
										
										actions.addAction(new TutorialAction("Ball Shooting!", "Great! Now Score At Least 2 Points On The Red Basket!", textColor));
										
										actions.addAction(new Action() {

											@Override
											public boolean act() {
												map.getMainPlayer().setWorldTransform(tempMx.setToTranslation(new Vector3(0, map.getMainPlayer().getHeight() / 2, -25)));
												map.setHoldingPlayer(map.getMainPlayer());
												
												return true;
											}

											@Override
											public boolean isGameDependent() {
												
												return false;
											}
											
										});
										
										actions.addAction(tempAction.copyAction());
										
										actions.addAction(new TutorialAction("Excellent!", "Well Done! You've Learned The Basics Of Basketball-3D! Now You Can Either Remain In This Level To Practise The Way You Want To Or Get Out And Start The Next One!", textColor));
										
										actions.addAction(new TutorialAction("Practising!", "Remember! From This Level You Should Know How To Move And How To Take, Dribble And Shoot The Ball! I Recommend You To Practise Those Things A Bit Cuz You'll Need Them A Lot!", textColor));
										
									}

									@Override
									public boolean updatePlayersNormalAI() {
										
										return false;
									}

									@Override
									public boolean usesOriginalRules() {
										
										return false;
									}

									@Override
									public boolean showPower() {
										
										return true;
									}
									
								},
								
								new LevelPart("practising", "Level Practising", map, messageListener) {

									@Override
									protected void createActions() {
										actions.addAction(new Action() {

											@Override
											public boolean act() {
												//In practice level the actions should never end as it is endless
												return false;
											}

											@Override
											public boolean isGameDependent() {
												
												return true;
											}
											
										});
										
									}

									@Override
									public boolean updatePlayersNormalAI() {
										
										return false;
									}

									@Override
									public boolean usesOriginalRules() {
										
										return false;
									}

									@Override
									public boolean showPower() {
										
										return true;
									}
									
								}
						};
					}

					@Override
					public void setup() {
						map.spawnPlayers(1, 0);
					}
					
				}, 
				
				new TutorialLevel("player_interact", "Level 2: Interacting With Players", map, messageListener) {
					final Vector3 tempVec = new Vector3(7, 0, 25);
					
					@Override
					protected LevelPart[] createLevelParts() {
						
						return new LevelPart[] {
								new LevelPart("pass_catch", "Passing & Catching", map, messageListener) {
									
									
									@Override
									protected void createActions() {
										actions.addAction(new TutorialAction("Interacting With Players!", "In This Level We're Gonna Look Into Important Things You Should Be Able To Do With Other Players!", textColor));
										
										actions.addAction(new TutorialAction("Passing & Catching!", "First We Are Gonna Look Into Passing To Players And Also Catching Their Passes!", textColor));
										
										actions.addAction(new TutorialAction("Passing & Catching!", "If You Were Practising In Tutorial Level 1 You Had Probably Noticed That When The Ball Is In The Air, You Can Only Catch It With The End Of Your Arm! That's Why The Player Points To The Ball!", textColor));
										
										actions.addAction(new TutorialAction("Passing & Catching!", "That's An Important Thing Not Only For Catching Passes (or any ball catching), But Also For Ball Stealing!", textColor));
										
										
										actions.addAction(new Action() {

											@Override
											public boolean act() {
												map.spawnPlayers(1, 0);
												
												Player teammate = map.getTeammates().get(1);
												map.setHoldingPlayer(teammate);
												
												tempVec.y = map.getMainPlayer().getHeight() / 2;
												map.getMainPlayer().setWorldTransform(tempMx.setToTranslation(tempVec));
												teammate.setWorldTransform(tempMx.setToTranslation(tempVec.cpy().scl(-1, 1, 1)));
												
												map.getMainPlayer().lookAt(teammate.getPosition(), false);
												
												teammate.getBrain().getMemory().setCheckZones(false);
												
												map.setDifficulty(1);
												
												/*teammate.getBrain().addCustomBHV(teammate.getBrain().getPursue());
												teammate.getBrain().addCustomBHV(teammate.getBrain().getPursueBallInHand());
												teammate.getBrain().addCustomBHV(teammate.getBrain().getPlayerBasketInterpose());
												teammate.getBrain().getPlayerBasketInterpose().setAgentA(map.getMainPlayer());
												teammate.getBrain().getPlayerBasketInterpose().setAgentB(map.getAwayBasket());*/
												
												return true;
											}

											@Override
											public boolean isGameDependent() {
												
												return false;
											}
											
										});
										
										actions.addAction(new TutorialAction("Passing & Catching!", "Now Go To The Red Basket By Passing The Ball With Your Teammate! For Moving For 0.75 Seconds Without Passing Or 1 Second While Shooting You'll Be Returned To The Blue Basket! Pass The Ball TO Your Teammate At Least 3 Times!", textColor));
										
										Action tempAction = new TutorialAction("0 Passes!", "", textColor, false) {
											final float defaultTime = 0.75f;
											float time = defaultTime;
											int passes;
											boolean wait;
											
											
											@Override
											protected void sendMessage() {
												messageListener.sendMessage(heading, desc, textColor, this, skippable, true);
											}
											
											@Override
											public boolean act() {
												if (map.getRules().update()) {
													GameRule rule = map.getRules().getTriggeredRule();
													map.getRules().clearTriggeredRuleWRuleBreaker();
													
													if (map.getTeammates().get(1).getBrain().getMemory().isCheckZones() && rule.getId().equals("backcourt_violation") || rule.getId().equals("walk_shoot") || rule.getId().equals("shoot_catch")) {
														returnPlayers();
														return false;
													}
												}
												
												if((map.getMainPlayer().isShooting() || map.getTeammates().get(1).isShooting()) && !wait || map.getHoldingPlayer() == null) {
													if (map.getMainPlayer().isShooting()) {
														passes++;
														wait = true;
														
														messageListener.sendMessage(passes + (passes == 1 ? " Pass!" : " Passes!"), "", textColor, this, false, true);
													}
													time = defaultTime;
												}else if(!map.getMainPlayer().isShooting() && wait)
													wait = false;
												
												if(map.getMainPlayer().getAwayThreePointZone().checkZone(map.getBall().getPosition())) {
													if(passes >= 3) {
														reset();
														
														return true;
													}
													else 
														returnPlayers();
												}else if(time < 0) {
													returnPlayers();
												}else if(!wait && map.getHoldingPlayer() != null && !map.getMainPlayer().isHoldingBall() && map.getMainPlayer().isAimingOrShooting()) time -= Gdx.graphics.getDeltaTime();
												
												return false;
											}
											
											private void reset() {
												passes = 0;
												wait = false;
												time = defaultTime;
												
												messageListener.sendMessage("0 Passes!", "", textColor, this, false, true);
											}
											
											private void returnPlayers() {
												map.setHoldingPlayer(map.getTeammates().get(1));
												
												map.getMainPlayer().setWorldTransform(tempMx.setToTranslation(tempVec));
												map.getTeammates().get(1).setWorldTransform(tempMx.setToTranslation(tempVec.cpy().scl(-1, 1, 1)));
												
												reset();
											}
											
											@Override
											public boolean isGameDependent() {
												return true;
											}
											
										};
										
										actions.addAction(tempAction);
										
										actions.addAction(new TutorialAction("Passing & Catching!", "Good! Now The Same Way To The Red Basket Again! This Time We'll Enable The Backcourt Violation Rule!", textColor));
										
										actions.addAction(new TutorialAction("Passing & Catching!", "Backcourt Means That Once The Ball Crosses The Midcourt Line Of The Terrain And Gets Into The Opposite Team's Zone, It Cannot Cross The Midcourt Line Again Before The Other Team Catches The Ball! Otherwise The Rule Triggers And The Ball Is Awarded To The Opposite Team!", textColor));
										
										actions.addAction(new TutorialAction("Passing & Catching!", "At This Logic, If Your Teammate Has Crossed The Midcourt Line, But You Still Haven't, Your Teammate Will Not Pass You!", textColor));
										
										actions.addAction(new Action() {

											@Override
											public boolean act() {
												tempVec.y = map.getMainPlayer().getHeight() / 2;
												
												map.getMainPlayer().setWorldTransform(tempMx.setToTranslation(tempVec));
												map.getTeammates().get(1).setWorldTransform(tempMx.setToTranslation(tempVec.cpy().scl(-1, 1, 1)));
												
												map.setHoldingPlayer(map.getTeammates().get(1));
												
												map.getMainPlayer().lookAt(map.getTeammates().get(1).getPosition(), false);
												
												map.getTeammates().get(1).getBrain().getMemory().setCheckZones(true);
												
												return true;
											}

											@Override
											public boolean isGameDependent() {
												
												return false;
											}
											
										});
										
										actions.addAction(tempAction.copyAction());
										
										actions.addAction(new TutorialAction("Passing & Catching!", "Great! I Suppose You've Got The Mechanics You Needed!", textColor));
										
										actions.addAction(new TutorialAction("Passing & Catching!", "Note That Players Always Pass Their Teammates According To The Game! I Mean If A Player's Pass Is Going To Trigger A Foul Or It's Going To Cause The Ball To Be Stolen, That Player Is Not Gonna Pass Anyone! Enough About That For Now!", textColor));
									}

									@Override
									public boolean updatePlayersNormalAI() {
										
										return true;
									}

									@Override
									public boolean usesOriginalRules() {
										
										return false;
									}

									@Override
									public boolean showPower() {
										
										return true;
									}
									
								},
								
								new LevelPart("ball_steal_prevent", "Preventing Ball Stealing", map, messageListener) {

									@Override
									protected void createActions() {
										actions.addAction(new Action() {

											@Override
											public boolean act() {
												if(map.getTeammates().size() == 2) {
													map.playerReleaseBall();
													map.removePlayer(map.getTeammates().get(1));
												}
												
												map.setDifficulty(2);
												
												return true;
											}

											@Override
											public boolean isGameDependent() {
												
												return false;
											}
											
										});
										
										actions.addAction(new TutorialAction("Preventing Ball Stealing!", "To Prevent Your Opponents From Stealing Your Ball You Can Either Pass The Ball To Someone Else, Or Just Run & Dribble, Which Is Used When Playing With Only 1 Opponent!", textColor));
										
										actions.addAction(new TutorialAction("Preventing Ball Stealing!", "If A Player Is Just Holding The Ball, It Can Be Stolen Only When That Player's Opponent Points At It And Touches It With The End Of Its Arm When That Player Is Dribbling!", textColor));
										
										actions.addAction(new Action() {

											@Override
											public boolean act() {
												map.spawnPlayers(0, 1);
												
												Player opponent = map.getOpponents().get(0);
												if(map.getHoldingPlayer() == null || !map.getHoldingPlayer().equals(map.getMainPlayer()))
													map.setHoldingPlayer(map.getMainPlayer());
												
												tempVec.y = map.getMainPlayer().getHeight() / 2;
												map.getMainPlayer().setWorldTransform(tempMx.setToTranslation(tempVec));
												opponent.setWorldTransform(tempMx.setToTranslation(tempVec.cpy().scl(-1, 1, 1)));
												
												map.getMainPlayer().lookAt(opponent.getPosition(), false);
												
												opponent.getBrain().getMemory().setCheckZones(false);
												
												return true;
											}

											@Override
											public boolean isGameDependent() {
												
												return false;
											}
											
										});
										
										actions.addAction(new TutorialAction("Preventing Ball Stealing!", "First I'm Gonna Teach You Preventing The Ball From Stealing Without Any Passes! In This Case You'll Be Playing With Only One Opponent!", textColor));
										
										actions.addAction(new TutorialAction("Preventing Ball Stealing!", "First You'll Have To Go To The Red Basket Without The Ball Being Stolen! Remember That You Can Switch Hands Any Time!", textColor));
										
										actions.addAction(new TutorialAction("Preventing Ball Stealing!", "Note That When A Player Is Holding The Ball He Walks With Normal Speed, But He Runs A Bit Slower Which Makes It Easy For Its Opponents To Reach Him!", textColor));
										
										actions.addAction(new TutorialAction("Preventing Ball Stealing!", "If Your Opponent Steals Your Ball Or You Don't Dribble, You'll Be Returned Back To The Blue Basket! Dribble Messages Will Appear When Needed!", textColor));
										
										TutorialAction tempAction = new TutorialAction("", "", textColor) {
											final float defaultTime = 3, defaultTimeMove = 0.75f;
											float time = defaultTime, timeMove = defaultTimeMove;
											
											boolean opposite, checked;
											
											Player tempMain;//That's if the user gets out of the game during this action
											
											
											@Override
											protected void sendMessage() {
												messageListener.sendMessage(heading, desc, textColor, null, skippable, false);
											}
											
											@Override
											public boolean act() {
												if (!checked || tempMain != null && !tempMain.equals(map.getMainPlayer())) {
													if (map.getMainPlayer().getPosition().z < 0)
														opposite = true;
													else
														opposite = false;
													
													tempMain = map.getMainPlayer();
													
													checked = true;
												}
												
												if(time < 0 || timeMove < 0 || !map.getMainPlayer().isHoldingBall()) {
													returnPlayers();
												}else {
													if(!opposite && map.getMainPlayer().isInAwayThreePointZone() || opposite && map.getMainPlayer().isInHomeThreePointZone()) {
														reset();
														return true;
													}else if(map.getMainPlayer().isDribbling()) {
														reset();
													} else {
														if (map.getMainPlayer().getPrevMoveVec().isZero() && map.getMainPlayer().getMoveVector().isZero())
															time -= Gdx.graphics.getDeltaTime();
														else
															timeMove -= Gdx.graphics.getDeltaTime();
														
														if(time < 0.5f || timeMove < 0.5f)
															messageListener.sendMessage("Dribble!", "", textColor, this, skippable, false);
													}
												}
												
												return false;
											}
											
											private void reset() {
												time = defaultTime;
												timeMove = defaultTimeMove;
												
												messageListener.sendMessage("", "", textColor, null, skippable, false);
											}
											
											private void returnPlayers() {
												map.setHoldingPlayer(map.getMainPlayer());
												
												Vector3 temp = new Vector3(tempVec);
												if(opposite)
													temp.scl(1, 1, -1);
												
												map.getMainPlayer().setWorldTransform(tempMx.setToTranslation(temp));
												map.getOpponents().get(0).setWorldTransform(tempMx.setToTranslation(temp.scl(-1, 1, 1)));
												
												reset();
											}
											
											@Override
											public boolean isGameDependent() {
												return true;
											}
											
										};
										
										actions.addAction(tempAction);
										
										actions.addAction(new TutorialAction("Preventing Ball Stealing!", "Kinda Tough Thing, huh? Don't Worry! You'll Learn It! That's Why You'll Have To Go Back To The Blue Basket Now!", textColor));
										
										actions.addAction(new Action() {

											@Override
											public boolean act() {
												Player opponent = map.getOpponents().get(0);
												if(map.getHoldingPlayer() == null || !map.getHoldingPlayer().equals(map.getMainPlayer()))
													map.setHoldingPlayer(map.getMainPlayer());
												
												Vector3 temp = tempVec.cpy().scl(1, 1, -1);
												map.getMainPlayer().setWorldTransform(tempMx.setToTranslation(temp));
												opponent.setWorldTransform(tempMx.setToTranslation(temp.scl(-1, 1, 1)));
												
												map.getMainPlayer().lookAt(opponent.getPosition(), false);
												
												opponent.getBrain().getMemory().setCheckZones(false);
												
												return true;
											}

											@Override
											public boolean isGameDependent() {
												
												return false;
											}
											
										});
										
										actions.addAction(tempAction.copyAction());
										
										actions.addAction(new TutorialAction("Preventing Ball Stealing!", "You Sick From This Tutorial Level Part? Don't Worry! It's Almost Over! Just A Little Bit More!", textColor));
										
										actions.addAction(new TutorialAction("Preventing Ball Stealing!", "Let Me Calm You Down! In EASY Gamemode Of The Normal Game The Chance For Opponents To Steal Any Ball Is Much Much LOWER Than Now!", textColor));
										
										actions.addAction(new TutorialAction("Preventing Ball Stealing!", "I Just Want To Teach You Comprehend The Hard Parts Of The Game First So That You Easily Play The Rest And Easily Adapt To The Game!", textColor));
										
										actions.addAction(new Action() {

											@Override
											public boolean act() {
												map.spawnPlayers(1, 1);
												
												map.setHoldingPlayer(map.getMainPlayer());
												
												map.getMainPlayer().setWorldTransform(tempMx.setToTranslation(tempVec));
												map.getTeammates().get(1).setWorldTransform(tempMx.setToTranslation(tempVec.cpy().sub(0, 0, 3)));
												map.getOpponents().get(0).setWorldTransform(tempMx.setToTranslation(tempVec.cpy().scl(-1, 1, 1)));
												map.getOpponents().get(1).setWorldTransform(tempMx.setToTranslation(tempVec.cpy().scl(-1, 1, 1).sub(0, 0, 3)));
												
												map.getTeammates().get(1).getBrain().getMemory().setCheckZones(false);
												
												return true;
											}

											@Override
											public boolean isGameDependent() {
												
												return false;
											}
											
										});
										
										actions.addAction(new TutorialAction("Preventing Ball Stealing!", "Now I Spawned You 1 More Opponent And 1 Teammate So That You Learn Keeping Ball Unstolen By Passing It With Your Teammate! Pass It At Least 3 Times!", textColor));
										
										actions.addAction(new TutorialAction("Preventing Ball Stealing!", "If You Pass It Less Times Than You Should, An Opponent Steals It Or You Walk For 1 Sec During Shooting You'll Be Returned Back To The Blue Basket! It Doesn't Matter How Long You Are Holding It Without Dribbling It!", textColor));
										
										actions.addAction(new TutorialAction("Preventing Ball Stealing!", "Note That When A Player's Opponent Tries To Interpose Between It And Its Target, The Shooting Player Shoots The Ball Much Higher Than Needed In Order To Prevent Its Opponent From Stealing!", textColor));
										
										actions.addAction(new TutorialAction("Preventing Ball Stealing!", "For Example If An Opponent Gets Between You And Your Teammate When Your Teammate Is Passing You, The Teammate Is Gonna Shoot The Ball Higher To Prevent The Opponent From Stealing!", textColor));
										
										actions.addAction(new TutorialAction("Preventing Ball Stealing!", "Ok, You Can Go Now!", textColor));
										
										TutorialAction tempAction2 = new TutorialAction("0 Passes!", "", textColor, false) {
											int passes;
											boolean wait;
											
											
											@Override
											protected void sendMessage() {
												messageListener.sendMessage(heading, desc, textColor, this, skippable, true);
											}
											
											@Override
											public boolean act() {
												if(map.getHoldingPlayer() != null && map.getHoldingPlayer() instanceof Opponent) {
													returnPlayers();
												}else {
													if(map.getRules().update()) {
														GameRule rule = map.getRules().getTriggeredRule();
														map.getRules().clearTriggeredRuleWRuleBreaker();
														
														if (rule.getId().equals("walk_shoot") || rule.getId().equals("shoot_catch")) {
															returnPlayers();
															return false;
														}
													}
													
													if((map.getMainPlayer().isShooting()) && !wait) {
															passes++;
															wait = true;
															
															messageListener.sendMessage(passes + (passes == 1 ? " Pass!" : " Passes!"), "", textColor, this, false, true);
													}else if(!map.getMainPlayer().isShooting() && wait)
														wait = false;
													
													if(map.getMainPlayer().getAwayThreePointZone().checkZone(map.getBall().getPosition())) {
														if(passes >= 3) {
															passes = 0;
															return true;
														}
														
														returnPlayers();
													}
												}
												
												return false;
											}
											
											private void reset() {
												passes = 0;
												wait = false;
												
												messageListener.sendMessage("0 Passes!", "", textColor, this, false, true);
											}
											
											private void returnPlayers() {
												map.setHoldingPlayer(map.getMainPlayer());
												
												map.getMainPlayer().setWorldTransform(tempMx.setToTranslation(tempVec));
												map.getTeammates().get(1).setWorldTransform(tempMx.setToTranslation(tempVec.cpy().sub(0, 0, 3)));
												map.getOpponents().get(0).setWorldTransform(tempMx.setToTranslation(tempVec.cpy().scl(-1, 1, 1)));
												map.getOpponents().get(1).setWorldTransform(tempMx.setToTranslation(tempVec.cpy().scl(-1, 1, 1).sub(0, 0, 3)));
												
												reset();
											}
											
											@Override
											public boolean isGameDependent() {
												return true;
											}
											
										};
										
										actions.addAction(tempAction2);
										
										actions.addAction(new TutorialAction("Preventing Ball Stealing!", "Excellent! Now To Ensure You've Learned It, Go To The Red Basket Again!", textColor));
										
										actions.addAction(new Action() {

											@Override
											public boolean act() {
												map.setHoldingPlayer(map.getMainPlayer());
												
												Vector3 temp = new Vector3(tempVec);
												map.getMainPlayer().setWorldTransform(tempMx.setToTranslation(temp));
												map.getTeammates().get(1).setWorldTransform(tempMx.setToTranslation(temp.cpy().sub(0, 0, 3)));
												map.getOpponents().get(0).setWorldTransform(tempMx.setToTranslation(temp.scl(-1, 1, 1)));
												map.getOpponents().get(1).setWorldTransform(tempMx.setToTranslation(temp.sub(0, 0, 3)));
												
												return true;
											}

											@Override
											public boolean isGameDependent() {
												
												return false;
											}
											
										});
										
										actions.addAction(tempAction2.copyAction());
										
										actions.addAction(new TutorialAction("Preventing Ball Stealing!", "That Was It! If You Think You Need To Train Something More You Can Just Go Back To The Step You Want To Train And Then Continue To The Ball Stealing Part Where YOU'll Be Stealing The Ball From Opponents!", textColor));
									}

									@Override
									public boolean updatePlayersNormalAI() {
										
										return true;
									}

									@Override
									public boolean usesOriginalRules() {
										
										return false;
									}

									@Override
									public boolean showPower() {
										
										return true;
									}
									
								},
								
								new LevelPart("ball_steal", "Ball Stealing", map, messageListener) {

									@Override
									protected void createActions() {
										actions.addAction(new TutorialAction("Ball Stealing!", "Ball Stealing Requires Players To Track Whether Their Opponents Are Supposed To Make A Dribble Or Catch The Ball Before It Gets Caught By The Opponent's Teammate!", textColor));
										
										actions.addAction(new TutorialAction("Ball Stealing!", "Simply In This Tutorial You And Your Opponents Will Switch Places! They'll Try To Dribble Or Pass The Ball And You'll Try To Steal It!", textColor));
										
										actions.addAction(new Action() {

											@Override
											public boolean act() {
												if(map.getOpponents().size() == 0)
													map.spawnPlayers(0, 2);
												
												if(map.getTeammates().size() > 1)
													map.removePlayer(map.getTeammates().get(1));
												
												map.setHoldingPlayer(map.getOpponents().get(0));
												
												map.getMainPlayer().setWorldTransform(tempMx.setToTranslation(tempVec));
												map.getOpponents().get(0).setWorldTransform(tempMx.setToTranslation(tempVec.cpy().scl(-1, 1, 1)));
												map.getOpponents().get(1).setWorldTransform(tempMx.setToTranslation(tempVec.cpy().scl(-1, 1, 1).sub(0, 0, 3)));
												
												map.getOpponents().get(0).getBrain().getMemory().setCheckZones(false);
												map.getOpponents().get(1).getBrain().getMemory().setCheckZones(false);
												
												map.getOpponents().get(0).getBrain().getPursueBallInHand().setTarget(map.getAwayBasket());
												map.getOpponents().get(1).getBrain().getPursueBallInHand().setTarget(map.getAwayBasket());
												
												return true;
											}

											@Override
											public boolean isGameDependent() {
												
												return false;
											}
											
										});
										
										actions.addAction(new TutorialAction("Ball Stealing!", "Let's Start With Stealing The Ball From A Pass! As Soon As An Opponent Shoots Go And Steal The Ball Before The Other Opponent Catches It!", textColor));
										
										actions.addAction(new TutorialAction("Ball Stealing!", "Now You Can Try Stealing The Ball And If The Ball Gets In The Red Basket Zone Or You Touch The Ball While It's Still In An Opponent's Hand (Reached In rule) You'll Be Returned To The Blue Basket!", textColor));
										
										
										Action tempAction = new Action() {
											
											boolean opposite, checked, mainZeroX;
											
											Player tempMain;
											
											
											@Override
											public boolean act() {
												if (!checked || tempMain != null && !tempMain.equals(map.getMainPlayer())) {
													if (map.getMainPlayer().getPosition().z < 0)
														opposite = true;
													else
														opposite = false;
													
													if(map.getMainPlayer().getPosition().x == 0)
														mainZeroX = true;
													else mainZeroX = false;
													
													tempMain = map.getMainPlayer();
													
													checked = true;
												}
												
												if(!opposite) {
													map.getOpponents().get(0).getBrain().getPlayerBasketInterpose().setAgentB(map.getAwayBasket());
													if(map.getOpponents().size() > 1)
														map.getOpponents().get(1).getBrain().getPlayerBasketInterpose().setAgentB(map.getAwayBasket());
												}
												
												if(map.getRules().update()) {
													if(map.getRules().getTriggeredRule().getId().equals("incorrect_ball_steal"))
														returnPlayers();
													
													map.getRules().clearTriggeredRuleWRuleBreaker();
													return false;
												}else if(!opposite && map.getMainPlayer().getAwayThreePointZone().checkZone(map.getBall().getPosition()) || opposite && map.getMainPlayer().getHomeThreePointZone().checkZone(map.getBall().getPosition()))
													returnPlayers();
												
												if(map.getMainPlayer().isHoldingBall()) {
													checked = false;
													
													return true;
												}
												
												return false;
											}
											
											private void returnPlayers() {
												map.setHoldingPlayer(map.getOpponents().get(0));
												
												Vector3 temp = new Vector3(tempVec);
												if(opposite)
													temp.scl(1, 1, -1);
												
												map.getMainPlayer().setWorldTransform(tempMx.setToTranslation(temp.scl(mainZeroX ? 0 : 1, 1, 1)));
												map.getOpponents().get(0).setWorldTransform(tempMx.setToTranslation(temp.scl(-1, 1, 1)));
												
												if(map.getOpponents().size() > 1)
													map.getOpponents().get(1).setWorldTransform(tempMx.setToTranslation(temp.sub(0, 0, 3)));
											}

											@Override
											public boolean isGameDependent() {
												
												return true;
											}
											
										};
										
										actions.addAction(tempAction);
										
										actions.addAction(new TutorialAction("Ball Stealing!", "Well Done! Now You Should Do The Same Thing Before The Ball Gets To The Blue Basket!", textColor));
										
										actions.addAction(new Action() {

											@Override
											public boolean act() {
												map.setHoldingPlayer(map.getOpponents().get(0));
												
												
												Vector3 temp = new Vector3(tempVec).scl(1, 1, -1);
												
												map.getMainPlayer().setWorldTransform(tempMx.setToTranslation(temp));
												map.getOpponents().get(0).setWorldTransform(tempMx.setToTranslation(temp.scl(-1, 1, 1)));
												map.getOpponents().get(1).setWorldTransform(tempMx.setToTranslation(temp.sub(0, 0, 3)));
												
												map.getOpponents().get(0).getBrain().getPursueBallInHand().setTarget(map.getHomeBasket());
												map.getOpponents().get(1).getBrain().getPursueBallInHand().setTarget(map.getHomeBasket());
												
												return true;
											}

											@Override
											public boolean isGameDependent() {
												
												return false;
											}
											
										});
										
										actions.addAction(tempAction.copyAction());
										
										actions.addAction(new TutorialAction("Ball Stealing!", "Perfect! We're Almost Done With The Tutorial!", textColor));
										
										actions.addAction(new TutorialAction("Ball Stealing!", "The Last Thing You're About To Exercise Is To Steal The Ball From A Dribbling Opponent!", textColor));
										
										actions.addAction(new TutorialAction("Ball Stealing!", "It Is Done By Pointing Your Arm (with the mouse button hand controls) To The Ball And Making The End Of The Arm Collide With The Ball While Your Opponent Is Dribbling!", textColor));
										
										actions.addAction(new TutorialAction("Ball Stealing!", "However, If You Point Your Arm To And Collide Its End With The Ball While The Opponent Isn't Dribbling, It Counts As Reach-In And The Penalty Would Be An Opponent To Go To The Basket And Try To Score!", textColor));
										
										actions.addAction(new Action() {

											@Override
											public boolean act() {
												map.removePlayer(map.getOpponents().get(1));
												map.setHoldingPlayer(map.getOpponents().get(0));
												
												map.getMainPlayer().setWorldTransform(tempMx.setToTranslation(tempVec.cpy().scl(0, 1, 1)));
												map.getOpponents().get(0).setWorldTransform(tempMx.setToTranslation(tempVec.cpy().scl(-1, 1, 1)));
												
												map.getOpponents().get(0).getBrain().getPursueBallInHand().setTarget(map.getAwayBasket());
												
												map.setDifficulty(0);
												
												return true;
											}

											@Override
											public boolean isGameDependent() {
												
												return false;
											}
											
										});
										
										actions.addAction(new TutorialAction("Ball Stealing!", "Now Try To Steal Your Opponent's Ball While He's Dribbling! If The Ball Reaches The Red Basket, You'll Be Returned Back Here!", textColor));
										
										actions.addAction(tempAction.copyAction());
										
										actions.addAction(new TutorialAction("Ball Stealing!", "Cool! Now To Ensure You've Learned It, Do It Again Before The Ball Reaches The Blue Basket! Note That The Opponent Will Dribble Faster Now (like in HARD and VERY HARD gamemodes)!", textColor));
										
										actions.addAction(new Action() {

											@Override
											public boolean act() {
												map.setHoldingPlayer(map.getOpponents().get(0));
												
												Vector3 temp = new Vector3(tempVec).scl(1, 1, -1);
												
												map.getMainPlayer().setWorldTransform(tempMx.setToTranslation(temp.cpy().scl(0, 1, 1)));
												map.getOpponents().get(0).setWorldTransform(tempMx.setToTranslation(temp.scl(-1, 1, 1)));
												
												map.getOpponents().get(0).getBrain().getPursueBallInHand().setTarget(map.getHomeBasket());
												
												map.setDifficulty(2);
												
												return true;
											}

											@Override
											public boolean isGameDependent() {
												
												return false;
											}
											
										});
										
										actions.addAction(tempAction.copyAction());
										
										actions.addAction(new TutorialAction("The End!", "Aaaand That Was All Of It! Unfortunately I Will Not Be Able To Train You Anymore!", textColor));
										
										actions.addAction(new TutorialAction("The End!", "I Mean I Won't Be Able To Train You Play According To The Rules Of The Game, So You Should Adapt To Them By The Game Itself!", textColor));
										
										actions.addAction(new TutorialAction("The End!", "But I Can Reveal All Of Them! Meet Me In Tutorial Level 3 Where I'll Just Explain All Of Them!", textColor));
										
										actions.addAction(new TutorialAction("/", "main", textColor) {
											
											@Override
											protected void sendMessage() {
												messageListener.sendMessage(heading, desc, textColor, null, skippable, true);
											}
											
										});
									}

									@Override
									public boolean updatePlayersNormalAI() {
										
										return true;
									}

									@Override
									public boolean usesOriginalRules() {
										
										return false;
									}

									@Override
									public boolean showPower() {
										
										return false;
									}
									
								}
						};
					}

					@Override
					public void setup() {
						map.spawnPlayers(1, 0);
						map.setDifficulty(2);
					}
					
				},
				
				new TutorialLevel("rules", "The Game Rules", map, messageListener) {

					@Override
					protected LevelPart[] createLevelParts() {
						
						return new LevelPart[] {
								new LevelPart("reveal", "All The Rules In This Game", map, messageListener) {

									@Override
									protected void createActions() {
										actions.addAction(new TutorialAction("The Rules!", "Like I Said, I'm Just Gonna Explain The Rules You Should Obey While Playing!", textColor));
										
										actions.addAction(new TutorialAction("1. Out Of Bounds", "Like You Probably Assume, This Rule Triggers When The Ball Gets Out Of The Terrain's Borders (the lines you see at the ends of the terrain)!", textColor));
										
										actions.addAction(new TutorialAction("1. Out Of Bounds", "And Not Only Then! The Rule Also Triggers If The Ball Goes Through The Basket As It Enters From Its Bottom!", textColor));
										
										actions.addAction(new TutorialAction("1. Out Of Bounds", "The Penalty Of This Rule Is A Throw-in, In Which A Player From The Opposite Of The Rule Triggerer's Team Throws The Ball Inside The Terrain, As He Tries To Pass It To One Of His Teammates!", textColor));
										
										actions.addAction(new TutorialAction("1. Out Of Bounds", "This Rule Has Some Inner Rules Which Work Only During The Throw-in!", textColor));
										
										actions.addAction(new TutorialAction("1.1. Moved Out", "This Inner Rule Triggers When The Thrower Moves For A Total Of 1 Second During The Throw-in!", textColor));
										
										actions.addAction(new TutorialAction("1.2. Time Out", "This Inner Rule Triggers When The Thrower Doesn't Manage To Release The Ball Within 5 Seconds!", textColor));
										
										actions.addAction(new TutorialAction("2. Reached In", "This Rule Triggers When A Player Tries To Steal The Ball From A Player That's Holding The Ball, But It's Not Currently Dribbling It!", textColor));
										
										actions.addAction(new TutorialAction("2. Reached In", "The Penalty Of This Rule Is A Rule Triggerer's Opponent To Go To The Rule Triggerer's Basket And Try To Score!", textColor));
										
										actions.addAction(new TutorialAction("3. Dribble Violation", "", textColor));
									}

									@Override
									public boolean updatePlayersNormalAI() {
										
										return false;
									}

									@Override
									public boolean usesOriginalRules() {
										
										return false;
									}

									@Override
									public boolean showPower() {
										
										return false;
									}
									
								}
						};
					}

					@Override
					public void setup() {
						map.spawnPlayers(1, 0);
						
					}
					
				}
		};
	}
	
	public boolean act(TutorialLevel level) {
		return level.act();
	}
	
	public void resetLevels() {
		for(GameLevel level : gameLevels)
			((TutorialLevel) level).reset();
	}
	
	public int getSize() {
		return gameLevels.length;
	}
	
	@Override
	public GameLevel getGameLevel(int i) {
		resetLevels();
		
		return super.getGameLevel(i);
	}
	
	public abstract class TutorialLevel extends GameLevel {
		TutorialActions actions;
		LevelPart[] parts;
		int part;
		
		
		public TutorialLevel(String id, String name, GameMap map, GameMessageListener messageListener) {
			super(id, name, map, messageListener);
			actions = new TutorialActions(map);
			
			//createActions();
			parts = createLevelParts();
		}
		
		protected abstract LevelPart[] createLevelParts();
		
		public LevelPart getCurrentPart() {
			return parts[part];
		}
		
		public boolean act() {
			LevelPart level = getCurrentPart();
			
			if(level.getCurrentAction().isGameDependent())
				map.resumeGame();
			else map.stopGame();
			
			boolean temp = level.act();
			if(temp) {
				part++;
			}
			
			return temp;
		}
		
		public abstract void setup();
		
		public void reset() {
			for(LevelPart level : parts)
				level.reset();
		}
		
		public int getParts() {
			return parts.length;
		}
		
		public LevelPart getPart(int i) {
			return parts[i];
		}
		
		public int getPart() {
			return part;
		}
		
		public void setLevelPart(int part) {
			parts[this.part].reset();
			
			this.part = part;
		}
		
		public abstract class LevelPart{
			String id, name;
			GameMap map;
			GameMessageListener messageListener;
			
			TutorialActions actions;
			
			
			public LevelPart(String id, String name, GameMap map, GameMessageListener messageListener){
				this.map = map;
				this.id = id;
				this.name = name;
				this.messageListener = messageListener;
				
				actions = new TutorialActions(map);
				
				createActions();
			}
			
			protected abstract void createActions();
			
			public abstract boolean updatePlayersNormalAI();
			
			/**
			 * Makes the GameMap update and manage the rules itself and prints messages for any triggered rule
			 * @return
			 */
			public abstract boolean usesOriginalRules();
			
			public abstract boolean showPower();
			
			public Action getCurrentAction() {
				return actions.getCurrentAction();
			}
			
			public boolean act() {
				return actions.act();
			}
			
			public void reset() {
				actions.firstAction();
			}
			
			public String getName() {
				return name;
			}
			
		}
		
		public class TutorialActions extends Actions{

			public TutorialActions(GameMap map) {
				super(map);
			}
			
			@Override
			public void firstAction() {
				resetActionMessage();
				
				super.firstAction();
			}
			
			@Override
			public void nextAction() {
				resetActionMessage();
				
				super.nextAction();
			}
			
			private void resetActionMessage() {
				if (getCurrentAction() instanceof TutorialAction) {
					TutorialAction action = (TutorialAction) getCurrentAction();

					action.resetMessage();
				}
			}
			
			@Override
			public boolean act() {
				if (getCurrentAction() instanceof TutorialAction) {
					TutorialAction action = (TutorialAction) getCurrentAction();
					if (!action.isMessageSent())
						action.setup();
				}
				
				return super.act();
			}
			
		}
		
		public class TutorialAction extends Action implements GameMessageSender {
			String heading, desc;
			Color textColor;
			boolean skippable;
			boolean received, sent;
			
			public TutorialAction(String heading, String desc, Color textColor) {
				this(heading, desc, textColor, true);
			}
			
			public TutorialAction(String heading, String desc, Color textColor, boolean skippable) {
				this.heading = heading;
				this.desc = desc;
				this.textColor = textColor;
				this.skippable = skippable;
			}
			
			public void setup() {
				received = false;
				sent = true;
				sendMessage();
			}
			
			protected void sendMessage() {
				messageListener.sendMessage(heading, desc, textColor, this, skippable, false);
			}
			
			@Override
			public boolean act() {
				return received;
			}
			
			@Override
			public boolean isGameDependent() {
				
				return false;
			}
			
			@Override
			public void messageReceived() {
				received = true;
			}
			
			public boolean isMessageReceived() {
				return received;
			}
			
			public void resetMessage() {
				sent = false;
			}
			
			public boolean isMessageSent() {
				return sent;
			}
			
		}
	}

	

}
