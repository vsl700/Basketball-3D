package com.vasciie.bkbl.gamespace.levels;

import com.badlogic.gdx.Gdx;
import com.vasciie.bkbl.GameMessageListener;
import com.vasciie.bkbl.gamespace.GameMap;
import com.vasciie.bkbl.gamespace.tools.GameTools;

public class Challenges extends Levels {

	ChallengeLevel[] currentChallenges;
	ChallengeLevel brokenChallenge;
	
	
	public Challenges(GameMap map, GameMessageListener messageListener) {
		super(map, messageListener);
		
		gameLevels = new ChallengeLevel[] {
				new ChallengeLevel("shoot_power", "Constant Shooting Power", map, messageListener) {
					int shootPower;
					
					
					@Override
					public void setup() {
						shootPower = 10 + challengeLevel;
					}

					@Override
					public boolean update() {
						/*if(map.getMainPlayer().getShootingPower() != shootPower)
							return true;*/
						
						map.getMainPlayer().setShootPower(shootPower);
						
						return false;
					}

					@Override
					public String[] getChallengeLevelsNames() {
						
						return new String[] {"Power of 10", "Power of 11", "Power of 12"};
					}

					@Override
					public boolean isLosable() {
						
						return false;
					}					
				}, 
				
				new ChallengeLevel("time", "Playing Until Timer Runs Out ", map, messageListener) {
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
						if(map.isGameRunning()) {
							time -= Gdx.graphics.getDeltaTime();
							
							messageListener.sendMinorMessage(GameTools.convertTimeToString(time));
						}
						
						return time < 0;
					}

					@Override
					public String[] getChallengeLevelsNames() {
						
						return new String[] {"5:00 minutes", "3:30 minutes", "2:00 minutes"};
					}

					@Override
					public boolean isLosable() {
						
						return false;
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

					@Override
					public boolean isLosable() {
						
						return true;
					}					
				},
				
				new ChallengeLevel("no_fouls", "Playing Without Commiting Any Fouls", map, messageListener) {
					@Override
					public void setup() {
						
					}

					@Override
					public boolean update() {
						return map.isRuleTriggered() && !map.getRules().getTriggeredRule().getId().equals("basket_score") && map.getRules().getTriggeredRule().getRuleTriggerer().equals(map.getMainPlayer());
					}

					@Override
					public String[] getChallengeLevelsNames() {
						
						return null;
					}

					@Override
					public boolean isLosable() {
						
						return true;
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

					@Override
					public boolean isLosable() {
						
						return false;
					}
					
				},
				
				new ChallengeLevel("one_hand", "Playing With Only One Hand", map, messageListener) {
					boolean left;
					boolean wait;
					
					@Override
					public void setup() {
						left = challengeLevel == 0;
						wait = false;
					}

					@Override
					public boolean update() {
						boolean temp  = !left && map.getMainPlayer().isLeftHolding() || left && map.getMainPlayer().isRightHolding();
						if(!wait) {
							if(!map.isGameRunning() && temp)
								wait = true;
						}else if(!temp) wait = false;
						
						return temp && !wait;
					}

					@Override
					public String[] getChallengeLevelsNames() {
						
						return new String[] {"Left hand", "Right hand"};
					}

					@Override
					public boolean isLosable() {
						
						return true;
					}
					
				},
				
				new ChallengeLevel("no_running", "Playing Without Running", map, messageListener) {
					boolean wait;
					
					@Override
					public void setup() {
						
					}

					@Override
					public boolean update() {
						if(!map.isGameRunning())
							wait = true;
						else if(!map.getMainPlayer().isCurrentlyRunning())
							wait = false;
						
						return !wait && map.getMainPlayer().isCurrentlyRunning();
					}

					@Override
					public String[] getChallengeLevelsNames() {
						
						return null;
					}

					@Override
					public boolean isLosable() {
						
						return true;
					}
					
				},
				
				new ChallengeLevel("alone", "Playing Alone In Your Team", map, messageListener) {
					
					@Override
					public void setup() {
						
					}

					@Override
					public boolean update() {
						return false;
					}

					@Override
					public String[] getChallengeLevelsNames() {
						
						return null;
					}

					@Override
					public boolean isLosable() {
						
						return false;
					}
					
				}
		};
	}
	
	public void setup() {
		brokenChallenge = null;
		
		for(ChallengeLevel ch : currentChallenges)
			ch.setup();
	}
	
	public void setChallenge(ChallengeLevel[] chosenChallenges){
		currentChallenges = chosenChallenges;
	}
	
	public boolean containsCurrentChallenge(String id) {
		for(ChallengeLevel lvl : currentChallenges)
			if(lvl.id.equals(id))
				return true;
		
		return false;
	}
	
	/*public void setChallenge(int[][] challenges) {
		currentChallenges = new ChallengeLevel[challenges.length];
		
		for(int i = 0; i < challenges.length; i++) {
			currentChallenges[i] = (ChallengeLevel) gameLevels[challenges[i][0]];
			
			if(challenges[i].length > 1)
				currentChallenges[i].setChallengeLevel(challenges[i][1]);
		}
	}*/
	
	/**
	 * 
	 * @return true if a challenge is broken
	 */
	public boolean update() {
		for(int i = 0; i < currentChallenges.length; i++) {
			if(currentChallenges[i].update()) {
				brokenChallenge = currentChallenges[i];
				return true;
			}
		}
		
		return false;
	}
	
	public void reset() {
		currentChallenges = null;
		brokenChallenge = null;
	}
	
	public ChallengeLevel getBrokenChallenge() {
		return brokenChallenge;
	}
	
	public boolean isAChallengeBroken() {
		return brokenChallenge != null;
	}
	
	public ChallengeLevel[] getCurrentChallenges() {
		return currentChallenges;
	}
	
	
	public static abstract class ChallengeLevel extends GameLevel{
		int challengeLevel;
		
		public ChallengeLevel(String id, String name, GameMap map, GameMessageListener messageListener) {
			super(id, name, map, messageListener);
		}
		
		public abstract boolean update();
		
		public abstract void setup();
		
		public abstract boolean isLosable();
		
		public int getChallengeLevelsAmount() {
			return getChallengeLevelsNames() != null ? getChallengeLevelsNames().length : 0;
		}
		
		public boolean hasChallengeLevels() {
			return getChallengeLevelsNames() != null;
		}
		
		public void setChallengeLevel(int level) {
			challengeLevel = Math.max(0, Math.min(getChallengeLevelsAmount(), level));
		}
		
		public int getChallengeLevel() {
			return challengeLevel;
		}
		
		public abstract String[] getChallengeLevelsNames();
		
	}

}
