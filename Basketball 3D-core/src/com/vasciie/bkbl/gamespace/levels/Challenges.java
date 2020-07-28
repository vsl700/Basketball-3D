package com.vasciie.bkbl.gamespace.levels;

import com.badlogic.gdx.Gdx;
import com.vasciie.bkbl.GameMessageListener;
import com.vasciie.bkbl.gamespace.GameMap;

public class Challenges extends Levels {

	ChallengeLevel[] currentChallenges;
	
	
	public Challenges(GameMap map, GameMessageListener messageListener) {
		super(map, messageListener);
		
		gameLevels = new ChallengeLevel[] {
				new ChallengeLevel("shoot_power", "Constantly Low Shooting Power", map, messageListener) {
					int shootPower;
					
					
					@Override
					public void setup() {
						shootPower = 10 + challengeLevel;
					}

					@Override
					public boolean update() {
						/*if(map.getMainPlayer().getShootingPower() != shootPower)
							return true;*/
						
						map.getMainPlayer().setShootingPower(shootPower);
						
						return false;
					}

					@Override
					public String[] getChallengeLevelsNames() {
						
						return null;
					}					
				}, 
				
				new ChallengeLevel("time", "Playing Until Timer Runs Out", map, messageListener) {
					float time;
					
					
					@Override
					public void setup() {
						switch(challengeLevel) {
						case 0: time = 300; break;
						case 1: time = 210; break;
						case 2: time = 120; break;
						}
					}

					@Override
					public boolean update() {
						time -= Gdx.graphics.getDeltaTime();
						
						return time < 0;
					}

					@Override
					public String[] getChallengeLevelsNames() {
						
						return new String[] {"5:00 minutes", "3:30 minutes", "2:00 minutes"};
					}
					
				},
				
				new ChallengeLevel("opp_score", "Playing Without Letting The Red Team Score", map, messageListener) {
					@Override
					public void setup() {
						
					}

					@Override
					public boolean update() {
						return map.getOppScore() > 0;
					}

					@Override
					public String[] getChallengeLevelsNames() {
						
						return null;
					}					
				},
				
				new ChallengeLevel("no_fouls", "Playing Without Commiting Any Fouls", map, messageListener) {
					@Override
					public void setup() {
						
					}

					@Override
					public boolean update() {
						return map.isRuleTriggered();
					}

					@Override
					public String[] getChallengeLevelsNames() {
						
						return null;
					}
					
				},
				
				new ChallengeLevel("team_score", "Playing Until Blue Team Scores A Given Amount Of Points", map, messageListener) {
					int targetPoints;
					
					@Override
					public void setup() {
						switch(challengeLevel) {
						case 0:  targetPoints = 5; break;
						case 1:  targetPoints = 10; break;
						case 2:  targetPoints = 15; break;
						}
					}

					@Override
					public boolean update() {
						return map.getTeamScore() >= targetPoints;
					}

					@Override
					public String[] getChallengeLevelsNames() {
						
						return new String[] {"5 points", "10 points", "15 points"};
					}
					
				},
				
				new ChallengeLevel("one_hand", "Playing With Only One Hand", map, messageListener) {
					boolean left;
					
					@Override
					public void setup() {
						left = challengeLevel == 0;
					}

					@Override
					public boolean update() {
						return !left && map.getMainPlayer().isLeftHolding() || left && map.getMainPlayer().isRightHolding();
					}

					@Override
					public String[] getChallengeLevelsNames() {
						
						return new String[] {"Left hand", "Right hand"};
					}
					
				},
				
				new ChallengeLevel("no_running", "Playing Without Running (main player only)", map, messageListener) {
					
					@Override
					public void setup() {
						
					}

					@Override
					public boolean update() {
						return map.getMainPlayer().isCurrentlyRunning();
					}

					@Override
					public String[] getChallengeLevelsNames() {
						
						return null;
					}
					
				},
				
				new ChallengeLevel("alone", "Playing Alone In Your Team", map, messageListener) {
					
					@Override
					public void setup() {
						map.spawnPlayers(1, 0);
					}

					@Override
					public boolean update() {
						return false;
					}

					@Override
					public String[] getChallengeLevelsNames() {
						
						return null;
					}
					
				}
		};
	}
	
	public void setChallenge(ChallengeLevel[] chosenChallenges){
		currentChallenges = chosenChallenges;
		
		for(ChallengeLevel ch : chosenChallenges)
			ch.setup();
	}
	
	/**
	 * 
	 * @return a challenge level if it gets broken
	 */
	public ChallengeLevel update() {
		for(int i = 0; i < currentChallenges.length; i++) {
			if(currentChallenges[i].update())
				return currentChallenges[i];
		}
		
		return null;
	}/*
	
	public static abstract class RuledChallengeLevel extends ChallengeLevel{

		Rules customRules;
		
		public RuledChallengeLevel(String id, String name, GameMap map, GameMessageListener messageListener) {
			super(id, name, map, messageListener);
			
			customRules = new Rules(map, messageListener, createCustomRules());
		}
		
		*//**
		 * I did it with a method for more clear code
		 * @return the custom game rules included in the following challenge
		 *//*
		public abstract GameRule[] createCustomRules();
		
		public Rules getCustomRules() {
			return customRules;
		}
		
		public boolean update() {
			return customRules.update();
		}
		
	}*/
	
	public static abstract class ChallengeLevel extends GameLevel{
		int challengeLevel;
		
		public ChallengeLevel(String id, String name, GameMap map, GameMessageListener messageListener) {
			super(id, name, map, messageListener);
		}
		
		public abstract boolean update();
		
		public abstract void setup();
		
		public int getMaxChallengeLevels() {
			return getChallengeLevelsNames().length;
		}
		
		public boolean hasChallengeLevels() {
			return getChallengeLevelsNames() != null;
		}
		
		public void setChallengeLevel(int level) {
			challengeLevel = Math.max(0, Math.min(getMaxChallengeLevels(), level));
		}
		
		public abstract String[] getChallengeLevelsNames();
		
	}

}
