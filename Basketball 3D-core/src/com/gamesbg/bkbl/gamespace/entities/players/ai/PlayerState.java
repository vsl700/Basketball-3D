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

public enum PlayerState implements State<Player> {
	BALL_IN_HAND() {
		float time, aimingTime;

		@Override
		public void update(Player player) {
			super.update(player);
			
			if(!player.getStateMachine().isInState(BALL_IN_HAND) || player.isShooting())
				return;

			if (aimingTime > 0 && aimingTime < 1.25f) {
				player.interactWithBallS();
				aimingTime += Gdx.graphics.getDeltaTime();
			} else {

				aimingTime = 0;
				ballJustShot = true;
				int sides = 0;
				if (player.isNorthSurround())
					sides++;
				if (player.isSouthSurround())
					sides++;
				if (player.isEastSurround())
					sides++;
				if (player.isWestSurround())
					sides++;

				if (sides > 2) {
					ArrayList<Player> tempTeam;
					if (player instanceof Teammate)
						tempTeam = player.getMap().getTeammates();
					else
						tempTeam = player.getMap().getOpponents();

					Vector3 playerVec = player.getModelInstance().transform.getTranslation(new Vector3());

					Vector3 tempTeamVec = getShortestDistance(playerVec, tempTeam).nor();

					player.lookAt(tempTeamVec);

					//player.interactWithBallS();
					//aimingTime += Gdx.graphics.getDeltaTime();

				}

				else /*if (player.isNorthSurround()) {
					if (player.isEastSurround())
						player.turnY(210 * Gdx.graphics.getDeltaTime());
					else if (player.isWestSurround())
						player.turnY(-210 * Gdx.graphics.getDeltaTime());
				} else*/ if (player.isEastSurround()) {// We don't need south (which is the back of the player)
					player.turnY(210 * Gdx.graphics.getDeltaTime());
					
					if (player.rightHolding())
						player.interactWithBallL();
				} else if (player.isWestSurround()) {
					player.turnY(-210 * Gdx.graphics.getDeltaTime());
					
					if (player.leftHolding())
						player.interactWithBallR();
				} else if (time > 0.75f) {
					if (player.leftHolding())
						player.interactWithBallL();
					else if (player.rightHolding())
						player.interactWithBallR();

					time = 0;
				}else
					time += Gdx.graphics.getDeltaTime();
			}

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
			
			if(!ballJustShot) {
				if (tempHandVec.idt(handVecs.get(0)))
					player.interactWithBallL();
				else if (tempHandVec.idt(handVecs.get(1)))
					player.interactWithBallR();
				
				player.roamAround(player.getMap().getBall().getModelInstance().transform, null, 0, 0, true, false);
			}
			else
				player.roamAround(player.getMap().getBall().getModelInstance().transform, null, 0, 0, false, true);
		}
	},
	
	COOPERATIVE(){
		@Override
		public void update(Player player) {
			super.update(player);
			
			ballJustShot = false;
			
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
			
			
			switch(player.getPlayerIndex()) {
			case 1:
				player.roamAround(tempPlayer, blockTrans, 3, -1, false, false);
				//if(newVel.x > 0 || newVel.z > 0)
					
				break;
			case 2:
				player.roamAround(tempPlayer, blockTrans, 5, 1, false, false);
				break;
				
			case 3:
				player.roamAround(tempPlayer, blockTrans, 12, 2, false, false);
			case 4:
				player.roamAround(tempPlayer, blockTrans, 0, -13, false, false);
			case 5:
				player.roamAround(tempPlayer, blockTrans, 4, -9, false, false);
				break;
			}
		}
	},
	
	PLAYER_SURROUND(){
		@Override
		public void update(Player player) {
			super.update(player);
			
			ballJustShot = false;
		}
	},
	
	IDLING() {
		@Override
		public void update(Player player) {
			super.update(player);
			
			ballJustShot = false;
		}
	};

	boolean ballJustShot;
	
	@Override
	public void enter(Player entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exit(Player entity) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void update(Player player) {
		if(player.getMap().isGameRunning()) {
			if(player.holdingBall())
				player.getStateMachine().changeState(BALL_IN_HAND);
			
			else if(player instanceof Teammate && player.getMap().isBallInTeam() || player instanceof Opponent && player.getMap().isBallInOpp()) 
				player.getStateMachine().changeState(COOPERATIVE);
			
			else if(player instanceof Teammate && player.getMap().isBallInOpp() || player instanceof Opponent && player.getMap().isBallInTeam())
				player.getStateMachine().changeState(PLAYER_SURROUND);
			
			else 
				player.getStateMachine().changeState(BALL_CHASING);
			
		}else 
			player.getStateMachine().changeState(IDLING);
	}

	@Override
	public boolean onMessage(Player entity, Telegram telegram) {
		// TODO Auto-generated method stub
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
