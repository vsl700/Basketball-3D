package com.vasciie.bkbl.gamespace.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Matrix4;
import com.vasciie.bkbl.GameMessageListener;
import com.vasciie.bkbl.GameMessageSender;
import com.vasciie.bkbl.gamespace.GameMap;
import com.vasciie.bkbl.gamespace.rules.Actions;
import com.vasciie.bkbl.gamespace.rules.Actions.Action;

public class TutorialLevels extends Levels {

	public TutorialLevels(GameMap map, GameMessageListener messageListener) {
		super(map, messageListener);
		
		gameLevels = new TutorialLevel[] {
				new TutorialLevel("basics", map, messageListener) {
					
					@Override
					protected void createActions() {
						Color textColor = Color.BLUE;
						
						actions.addAction(new TutorialAction("Hello There!", "Welcome To The Basketball-3D's tutorial levels!", textColor));
						
						actions.addAction(new TutorialAction("Hello There!", "In This Level We Are Gonna Head Into The Basics Of The Game!", textColor));
						
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
						
						actions.addAction(new TutorialAction("Ball Interacting!", "Interacting With The Ball Is As Important As Movement, So I Shall Teach You Catching, Shooting And Also Dribbling The Ball!", textColor));
						
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
						
						actions.addAction(new TutorialAction("Ball Taking!", "Well Done! Now I Just Would Like To See That Once Again! Go To The Blue Basket And Take The Ball!", textColor));
						
						actions.addAction(new Action() {

							@Override
							public boolean act() {
								map.playerReleaseBall();
								
								map.getBall().setWorldTransform(new Matrix4().setToTranslation(0, 0.5f, 25));
								
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
						
						actions.addAction(new TutorialAction("Ball Dribble!", "Great! Now I Guess It's Time To Jump Into Ball Dribbling!", textColor));
						
						actions.addAction(new TutorialAction("Ball Dribble!", "You'll Probably Find Out That In EASY And HARD Gamemodes There's An Auto-dribble! Well Yes, But That Doesn't Prevent Other Players From Stealing The Ball From You!!!", textColor));
						
						actions.addAction(new TutorialAction("Ball Dribble!", "For This Reason You Should Learn Dribbling The Ball Yourself!", textColor));
						
						actions.addAction(new TutorialAction("Ball Dribble!", "First, Run To The Red Basket While Dribbling The Ball! Like I Said, Left Mouse Button Controls Left Hand While Right Button Controls Right Hand!", textColor));
						
						actions.addAction(new TutorialAction("Ball Dribble!", "Controlling The Hand You Are Not Holding The Ball With Makes The Player To Dribble The Ball TO His Other Hand (Or Hand Switching)!", textColor));
						
						actions.addAction(new TutorialAction("Ball Dribble!", "Try This Out And At The Same Time Run To The Red Basket! Make Sure You Dribble For 0.75 Seconds While Moving Or 3 While Not Moving!", textColor, false) {
							final float defaultTime = 3, defaultTimeMove = 0.75f;
							float time = defaultTime, timeMove = defaultTimeMove;
							
							
							@Override
							public boolean act() {
								if(map.getMainPlayer().isDribbling()) {
									time = defaultTime;
									timeMove = defaultTimeMove;
								}else if((time < 0 || timeMove < 0) && map.getMainPlayer().isHoldingBall()) {
									map.getMainPlayer().setWorldTransform(new Matrix4().setToTranslation(0, map.getMainPlayer().getHeight() / 1.5f, 25));
									time = defaultTime;
									timeMove = defaultTimeMove;
								}else if(!map.getMainPlayer().getPrevMoveVec().isZero() || !map.getMainPlayer().getMoveVector().isZero())
									timeMove -= Gdx.graphics.getDeltaTime();
								else time -= Gdx.graphics.getDeltaTime();
								
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
						
						actions.addAction(new TutorialAction("Ball Dribble!", "Perfect! Now The Same Way You Should Walk Or Run Back To The Blue Basket By Dribbling The Ball!", textColor));
						
						actions.addAction(new Action() {
							final float defaultTime = 3, defaultTimeMove = 0.75f;
							float time = defaultTime, timeMove = defaultTimeMove;
							
							@Override
							public boolean act() {
								if(map.getMainPlayer().isDribbling()) {
									time = defaultTime;
									timeMove = defaultTimeMove;
								}else if((time < 0 || timeMove < 0) && map.getMainPlayer().isHoldingBall()) {
									map.getMainPlayer().setWorldTransform(new Matrix4().setToTranslation(0, map.getMainPlayer().getHeight() / 1.5f, -25));
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
					
				}
		};
	}
	
	public boolean act(TutorialLevel level) {
		if(level.getCurrentAction().isGameDependent())
			map.resumeGame();
		else map.stopGame();
		
		return level.act();
	}
	
	public void resetLevels() {
		for(GameLevel level : gameLevels)
			((TutorialLevel) level).reset();
	}
	
	public abstract class TutorialLevel extends GameLevel {
		TutorialActions actions;
		
		
		public TutorialLevel(String id, GameMap map, GameMessageListener messageListener) {
			super(id, map, messageListener);
			actions = new TutorialActions(map);
			
			createActions();
		}
		
		protected abstract void createActions();
		
		public abstract boolean updatePlayersNormalAI();
		
		public abstract boolean usesOriginalRules();
		
		public Action getCurrentAction() {
			return actions.getCurrentAction();
		}
		
		public boolean act() {
			return actions.act();
		}
		
		public void reset() {
			actions.firstAction();
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
				messageListener.sendMessage(heading, desc, textColor, this, skippable);
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
