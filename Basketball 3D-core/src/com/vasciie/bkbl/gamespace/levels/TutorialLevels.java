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
										
										actions.addAction(new TutorialAction("Ball Dribble!", "Try This Out And At The Same Time Run To The Red Basket! Make Sure You Dribble For 0.75 Seconds While Moving Or 3 While Not Moving! When Dribble Time Is About To Run Out You'll Get A Message To Dribble!", textColor));
										
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
										
										actions.addAction(new TutorialAction("Ball Shooting!", "You Can Now Try Putting The Ball In One Of The Baskets! Just Hold The CTRL Button And Release It After You Aim At The Basket! Scroll Wheel Or Q & E Keys Are For The Shooting Power Below!", textColor, false) {
											boolean shootPowerReg;
											
											@Override
											public void resetMessage() {
												super.resetMessage();
												
												shootPowerReg = false;
											}
											
											@Override
											protected void sendMessage() {
												messageListener.sendMessage(heading, desc, textColor, this, skippable, true);
											}
											
											@Override
											public boolean act() {
												if(!shootPowerReg)
													map.getMainPlayer().setShootingPower(10);
												
												shootPowerReg = true;
												
												return map.getBall().isCollidedWOppBasket() || map.getBall().isCollidedWTeamBasket();
											}
											
											@Override
											public boolean isGameDependent() {
												return true;
											}
											
										});
										
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
										
										actions.addAction(new TutorialAction("Passing & Catching", "First We Are Gonna Look Into Passing To Players And Also Catching Their Passes!", textColor));
										
										actions.addAction(new TutorialAction("Passing & Catching", "If You Were Practising In Tutorial Level 1 You Had Probably Noticed That When The Ball Is In The Air, You Can Only Catch It With The End Of Your Arm!", textColor));
										
										actions.addAction(new TutorialAction("Passing & Catching", "That's An Important Thing Not Only For Catching Passes (or any ball catching), But Also For Ball Stealing!", textColor));
										
										
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
										
										actions.addAction(new TutorialAction("Passing & Catching", "Now Go To The Red Basket By Passing The Ball With Your Teammate! For Moving For 0.75 Seconds Without Passing You'll Be Returned To The Blue Basket! Pass The Ball TO Your Teammate At Least 3 Times!", textColor));
										
										Action tempAction = new TutorialAction("0 Passes!", "", textColor, false) {
											final float defaultTime = 0.75f;
											float time = defaultTime;
											int passes;
											boolean wait;
											boolean opposite, checked;
											
											Player tempMain;//That's if the user gets out of the game during this action
											
											
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
												
												if(opposite && map.getMainPlayer().isHoldingBall()) {
													map.getTeammates().get(1).getBrain().getPlayerBasketInterpose().setAgentB(map.getHomeBasket());
												}
												
												if((map.getMainPlayer().isAimingOrShooting() || map.getTeammates().get(1).isAimingOrShooting()) && !wait || map.getHoldingPlayer() == null) {
													if (map.getMainPlayer().isAimingOrShooting()) {
														passes++;
														wait = true;
														
														messageListener.sendMessage(passes + (passes == 1 ? " Pass!" : " Passes!"), "", textColor, this, false, true);
													}
													time = defaultTime;
												}else if(!map.getMainPlayer().isAimingOrShooting() && wait)
													wait = false;
												
												if(!opposite && map.getMainPlayer().getAwayThreePointZone().checkZone(map.getBall().getPosition()) || opposite && map.getMainPlayer().getHomeThreePointZone().checkZone(map.getBall().getPosition())) {
													if(passes >= 3) {
														reset();
														
														checked = false;
														return true;
													}
													else 
														returnPlayers();
												}else if(time < 0) {
													returnPlayers();
												}else if(!wait && map.getHoldingPlayer() != null && !map.getHoldingPlayer().equals(map.getTeammates().get(1))) time -= Gdx.graphics.getDeltaTime();
												
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
												
												Vector3 temp = new Vector3(tempVec);
												if(opposite)
													temp.scl(1, 1, -1);
												
												map.getMainPlayer().setWorldTransform(tempMx.setToTranslation(temp));
												map.getTeammates().get(1).setWorldTransform(tempMx.setToTranslation(temp.scl(-1, 1, 1)));
												
												reset();
											}
											
											@Override
											public boolean isGameDependent() {
												return true;
											}
											
										};
										
										actions.addAction(tempAction);
										
										actions.addAction(new TutorialAction("Passing & Catching", "Good! Now The Same Way To The Blue Basket!", textColor));
										
										actions.addAction(new Action() {

											@Override
											public boolean act() {
												tempVec.y = map.getMainPlayer().getHeight() / 2;
												
												Vector3 temp = tempVec.cpy().scl(1, 1, -1);
												map.getMainPlayer().setWorldTransform(tempMx.setToTranslation(temp));
												map.getTeammates().get(1).setWorldTransform(tempMx.setToTranslation(temp.scl(-1, 1, 1)));
												
												map.setHoldingPlayer(map.getTeammates().get(1));
												
												map.getMainPlayer().lookAt(map.getTeammates().get(1).getPosition(), false);
												
												map.getTeammates().get(1).getBrain().getPursueBallInHand().setTarget(map.getHomeBasket());
												
												return true;
											}

											@Override
											public boolean isGameDependent() {
												
												return false;
											}
											
										});
										
										actions.addAction(tempAction.copyAction());
										
										actions.addAction(new TutorialAction("Passing & Catching", "Great! I Suppose You've Got The Mechanics You Needed!", textColor));
										
										actions.addAction(new TutorialAction("Passing & Catching", "Note That In The Real Games Players Pass Their Teammates According To The Game! I Mean If A Player's Pass Is Going To Trigger A Foul Or It's Going To Cause The Ball To Be Stolen, That Player Is Not Gonna Pass Anyone! Enough About That For Now!", textColor));
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
												if(map.getTeammates().size() == 2)
													map.removePlayer(map.getTeammates().get(1));
												
												return true;
											}

											@Override
											public boolean isGameDependent() {
												
												return false;
											}
											
										});
										
										actions.addAction(new TutorialAction("Preventing Ball Stealing!", "To Prevent Your Opponents From Stealing Your Ball You Can Either Pass The Ball To Someone Else, Or Just Run & Dribble, Which Is Used When Playing With Only 1 Opponent!", textColor));
										
										actions.addAction(new TutorialAction("Preventing Ball Stealing!", "This Tutorial Part Will Be Kinda Long, So Please Be Patient!", textColor));
										
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
										
										actions.addAction(new TutorialAction("Preventing Ball Stealing!", "If Your Opponent Steals Your Ball Or You Don't Dribble, You'll Be Returned Back To The Blue Basket! Dribble Messages Will Appear When Needed!", textColor));
										
										TutorialAction tempAction = new TutorialAction("", "", textColor) {
											final float defaultTime = 3, defaultTimeMove = 0.75f;
											float time = defaultTime, timeMove = defaultTimeMove;
											
											boolean opposite, checked;
											
											Player tempMain;//That's if the user gets out of the game during this action
											
											
											@Override
											protected void sendMessage() {
												messageListener.sendMessage(heading, desc, textColor, null, skippable, true);
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
										
										actions.addAction(new TutorialAction("Preventing Ball Stealing!", "If You Pass It Less Times Than You Should Or An Opponent Steals It You'll Be Returned Back To The Blue Basket! It Doesn't Matter How Long You Are Holding It!", textColor));
										
										TutorialAction tempAction2 = new TutorialAction("0 Passes!", "", textColor, false) {
											int passes;
											boolean wait;
											boolean opposite, checked;
											
											Player tempMain;//That's if the user gets out of the game during this action
											
											
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
												
												if(opposite) {
													map.getTeammates().get(1).getBrain().getPlayerBasketInterpose().setAgentB(map.getHomeBasket());
													map.getOpponents().get(0).getBrain().getPlayerBasketInterpose().setAgentB(map.getHomeBasket());
													map.getOpponents().get(1).getBrain().getPlayerBasketInterpose().setAgentB(map.getHomeBasket());
												}
												
												if(map.getHoldingPlayer() != null && map.getHoldingPlayer() instanceof Opponent) {
													returnPlayers();
												}else {
													if((map.getMainPlayer().isShooting()) && !wait) {
															passes++;
															wait = true;
															
															messageListener.sendMessage(passes + (passes == 1 ? " Pass!" : " Passes!"), "", textColor, this, false, true);
													}else if(!map.getMainPlayer().isShooting() && wait)
														wait = false;
													
													if(!opposite && map.getMainPlayer().getAwayThreePointZone().checkZone(map.getBall().getPosition()) || opposite && map.getMainPlayer().getHomeThreePointZone().checkZone(map.getBall().getPosition())) {
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
												//wait = false;
												
												messageListener.sendMessage("0 Passes!", "", textColor, this, false, true);
											}
											
											private void returnPlayers() {
												map.setHoldingPlayer(map.getMainPlayer());
												
												Vector3 temp = new Vector3(tempVec);
												if(opposite)
													temp.scl(1, 1, -1);
												
												map.getMainPlayer().setWorldTransform(tempMx.setToTranslation(temp));
												map.getTeammates().get(1).setWorldTransform(tempMx.setToTranslation(temp.cpy().sub(0, 0, 3)));
												map.getOpponents().get(0).setWorldTransform(tempMx.setToTranslation(temp.scl(-1, 1, 1)));
												map.getOpponents().get(1).setWorldTransform(tempMx.setToTranslation(temp.sub(0, 0, 3)));
												
												reset();
											}
											
											@Override
											public boolean isGameDependent() {
												return true;
											}
											
										};
										
										actions.addAction(tempAction2);
										
										actions.addAction(new TutorialAction("Preventing Ball Stealing!", "Excellent! Now To Ensure You've Learned It, Go Back To The Blue Basket! This Is The Last Part From Preventing Ball Stealing!", textColor));
										
										actions.addAction(new Action() {

											@Override
											public boolean act() {
												map.setHoldingPlayer(map.getMainPlayer());
												
												Vector3 temp = tempVec.cpy().scl(1, 1, -1);
												map.getMainPlayer().setWorldTransform(tempMx.setToTranslation(temp));
												map.getTeammates().get(1).setWorldTransform(tempMx.setToTranslation(temp.cpy().sub(0, 0, 3)));
												map.getOpponents().get(0).setWorldTransform(tempMx.setToTranslation(temp.scl(-1, 1, 1)));
												map.getOpponents().get(1).setWorldTransform(tempMx.setToTranslation(temp.sub(0, 0, 3)));
												
												map.getTeammates().get(1).getBrain().getPursueBallInHand().setTarget(map.getHomeBasket());
												map.getOpponents().get(0).getBrain().getPursueBallInHand().setTarget(map.getHomeBasket());
												map.getTeammates().get(1).getBrain().getPursueBallInHand().setTarget(map.getHomeBasket());
												
												return true;
											}

											@Override
											public boolean isGameDependent() {
												
												return false;
											}
											
										});
										
										actions.addAction(tempAction2.copyAction());
										
										actions.addAction(new TutorialAction("Preventing Ball Stealing!", "That Was It! You Can Either Repeat This Part Of Level 2 Or Continue To The Ball Stealing Part Where YOU'll Be Stealing The Ball From Opponents!", textColor));
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
