package com.gamesbg.bkbl.gamespace.entities.players.ai;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.gamesbg.bkbl.gamespace.entities.Player;
import com.gamesbg.bkbl.gamespace.entities.players.Opponent;
import com.gamesbg.bkbl.gamespace.entities.players.Teammate;
import com.gamesbg.bkbl.gamespace.objects.GameObject;

public enum PlayerState implements State<Player> {
	BALL_IN_HAND() {
		float distDiff = 0, resetTime = 0;
		
		boolean ballJustShot;
		
		
		@Override
		public void update(Player player) {
			super.update(player);
			
			if(!player.getStateMachine().isInState(BALL_IN_HAND) || player.isShooting())
				return;
			
			//Ball behavior mechanism
			if (aimingTime > 0 && aimingTime < 1.25f) {
				player.interactWithBallS();
				aimingTime += Gdx.graphics.getDeltaTime();
			} else {
				ballJustShot = true;
				
				if (player.isSurrounded() && player.getMap().getTeammates().size() > 1 && shootTime > 2) {
					Vector3 tempAimVec;
					Vector3 playerVec = player.getModelInstance().transform.getTranslation(new Vector3());
					
					if (player.isInAwayBasketZone()) {
						tempAimVec = player.getMap().getBall().getModelInstance().transform.getTranslation(new Vector3());
					} else {
						ArrayList<Player> tempTeam;
						if (player instanceof Teammate)
							tempTeam = player.getMap().getTeammates();
						else
							tempTeam = player.getMap().getOpponents();

						tempAimVec = getShortestDistance(playerVec, tempTeam).nor();
					}
					
					player.lookAt(tempAimVec);
					
					player.interactWithBallS();
					aimingTime += Gdx.graphics.getDeltaTime();
					shootTime = 0;
					
				}

				else {
					shootTime+= Gdx.graphics.getDeltaTime();
					
					if (player.isEastSurround()) {
					player.turnY(210 * Gdx.graphics.getDeltaTime());
					
					if (aimingTime == 0 && player.rightHolding() && switchHandTime > 0.5f) {
						player.interactWithBallL();
						switchHandTime = 0;
					}
				} else if (player.isWestSurround()) {
					player.turnY(-210 * Gdx.graphics.getDeltaTime());
					
					if (aimingTime == 0 && player.leftHolding() && switchHandTime > 0.5f) {
						player.interactWithBallR();
						switchHandTime = 0;
					}
				} else if (aimingTime == 0 && dribbleTime > 0.75f) {
					if (player.leftHolding())
						player.interactWithBallL();
					else if (player.rightHolding())
						player.interactWithBallR();

					dribbleTime = 0;
				}else
					dribbleTime += Gdx.graphics.getDeltaTime();
				}
			}
			
			switchHandTime+= Gdx.graphics.getDeltaTime();
			
			//Walking & running mechanism
			GameObject basket;
			if(player instanceof Teammate)
				basket = player.getMap().getAwayBasket();
			else basket = player.getMap().getHomeBasket();
			
			//We use this if just for the aim & shoot starter. The if below continues it.
			if(player.isInAwayBasketZone() && aimingTime == 0) {
				player.lookAt(basket.getMainTrans().getTranslation(new Vector3()));
				
				player.interactWithBallS();
				aimingTime+= Gdx.graphics.getDeltaTime();
				
				return;
			}
			
			
			if (player.isNorthSurround() && aimingTime == 0) {
				if (player.isWestSurround())
					distDiff -= 60 * Gdx.graphics.getDeltaTime();
				else 
					distDiff += 60 * Gdx.graphics.getDeltaTime();
				
			}
			else if(resetTime > 0.25f) {
				distDiff = 0;
				resetTime = 0;
			}
			else resetTime+= Gdx.graphics.getDeltaTime();
			
			Vector3 dir = player.roamAround(basket.getMainTrans().cpy().trn(distDiff, 0, 0), null, 0, 5, false, aimingTime > 0 || player.isShooting());
			player.lookAt(dir);
			
		}
		
		@Override
		public void setSpecialBoolean(boolean b) {
			ballJustShot = b;
		}
		
		@Override
		public boolean isSpecialBoolean() {
			return ballJustShot;
		}
		
	},
	
	BALL_CHASING(){
		@Override
		public void update(Player player) {
			super.update(player);
			
			//System.out.println("Chasing");
			
			Vector3 ballVec = player.getMap().getBall().getModelInstance().transform.getTranslation(new Vector3());
			ArrayList<Vector3> handVecs = new ArrayList<Vector3>();
			handVecs.add(player.getShoulderLTrans().getTranslation(new Vector3()));
			handVecs.add(player.getShoulderRTrans().getTranslation(new Vector3()));
			
			Vector3 tempHandVec = getShortestDistanceWVectors(ballVec, handVecs);
			
			if(!BALL_IN_HAND.isSpecialBoolean()) {
				if (tempHandVec.idt(handVecs.get(0)))
					player.interactWithBallL();
				else if (tempHandVec.idt(handVecs.get(1)))
					player.interactWithBallR();
				
				player.lookAt(player.roamAround(player.getMap().getBall().getModelInstance().transform, null, 0, 0, true, false));
			}
			else
				player.lookAt(player.roamAround(player.getMap().getBall().getModelInstance().transform, null, 0, 0, false, true));
		}
	},
	
	COOPERATIVE(){
		@Override
		public void update(Player player) {
			super.update(player);
			
			BALL_IN_HAND.setSpecialBoolean(false);
			
			if(!player.getStateMachine().isInState(COOPERATIVE))
				return;
			
			Matrix4 tempPlayer;
			if(player instanceof Teammate)
				tempPlayer = player.getMap().getTeammateHolding().getModelInstance().transform.cpy();
			else tempPlayer = player.getMap().getOpponentHolding().getModelInstance().transform.cpy();
			
			Vector3 playerVec = player.getModelInstance().transform.getTranslation(new Vector3());
			
			ArrayList<Player> tempOpp;
			if(player instanceof Teammate) 
				tempOpp = player.getMap().getOpponents();
			else tempOpp = player.getMap().getTeammates();
			
			Matrix4 blockTrans = new Matrix4();
			
			Vector3 tempVec = getShortestDistance(playerVec, tempOpp);
				
			blockTrans.setToTranslation(tempVec);
			
			Vector3 dir;
			
			switch(player.getPlayerIndex()) {
			case 1:
				dir = player.roamAround(tempPlayer, blockTrans, 3, -1, false, false);
				//if(newVel.x > 0 || newVel.z > 0)
					
				break;
			case 2:
				dir = player.roamAround(tempPlayer, blockTrans, 5, 1, false, false);
				break;
				
			case 3:
				dir = player.roamAround(tempPlayer, blockTrans, 12, 2, false, false);
			case 4:
				dir = player.roamAround(tempPlayer, blockTrans, 0, -13, false, false);
			case 5:
				dir = player.roamAround(tempPlayer, blockTrans, 4, -9, false, false);
				break;
			default: dir = new Vector3();
				break;
			}
			
			player.lookAt(dir);
		}
	},
	
	PLAYER_SURROUND(){
		@Override
		public void update(Player player) {
			super.update(player);
			
			BALL_IN_HAND.setSpecialBoolean(false);
		}
	},
	
	IDLING() {
		@Override
		public void update(Player player) {
			super.update(player);
			
			BALL_IN_HAND.setSpecialBoolean(false);
		}
	};

	float dribbleTime, aimingTime, shootTime, switchHandTime;
	
	@Override
	public void enter(Player entity) {
		
		
	}

	@Override
	public void exit(Player entity) {
		
		
	}
	
	protected void setSpecialBoolean(boolean b) {
		
	}
	
	protected boolean isSpecialBoolean() {
		return false;
	}
	
	@Override
	public void update(Player player) {
		if(player.getMap().isGameRunning()) {
			if(player.holdingBall())
				player.getStateMachine().changeState(BALL_IN_HAND);
			
			else {
				aimingTime = 0;
				
				if(player instanceof Teammate && player.getMap().isBallInTeam() || player instanceof Opponent && player.getMap().isBallInOpp()) 
				player.getStateMachine().changeState(COOPERATIVE);
			
			else if(player instanceof Teammate && player.getMap().isBallInOpp() || player instanceof Opponent && player.getMap().isBallInTeam())
				player.getStateMachine().changeState(PLAYER_SURROUND);
			
			else 
				player.getStateMachine().changeState(BALL_CHASING);
			}
			
		}else 
			player.getStateMachine().changeState(IDLING);
	}

	@Override
	public boolean onMessage(Player entity, Telegram telegram) {
		
		return false;
	}
	
	protected boolean pointsCollision(float x11, float y11, float x12, float y12, float x21, float y21) {
		return x11 <= x21 && x12 >= x21 && y11 <= y21 && y12 >= y21;
	}
	
	protected Vector3 getShortestDistanceWVectors(Vector3 position, ArrayList<Vector3> positions) {
		Vector3 tempVec = positions.get(0);
		float dist = position.dst(tempVec);
		for(int i = 1; i < positions.size(); i++) {
			//Matrix4 tempTrans2 = position.get(i).getModelInstance().transform;
			
			Vector3 tempVec2 = positions.get(i);
			
			float dist2 = position.dst(tempVec2);

			if(dist2 < dist) {
				dist = dist2;
				tempVec = tempVec2;
			}
		}
		
		return tempVec;
	}

	protected Vector3 getShortestDistance(Vector3 position, ArrayList<Player> players) {
		Vector3 tempVec = players.get(0).getModelInstance().transform.getTranslation(new Vector3());
		float dist = position.dst(tempVec);
		//Vector3 diff = position.cpy().sub(tempTeamVec);
		for(int i = 1; i < players.size(); i++) {
			Matrix4 tempTrans2 = players.get(i).getModelInstance().transform;
			
			Vector3 tempVec2 = tempTrans2.getTranslation(new Vector3());
			
			float dist2 = position.dst(tempVec2);

			if(dist2 < dist) {
				dist = dist2;
				tempVec = tempVec2;
			}
		}
		
		return tempVec;
	}
	
}
