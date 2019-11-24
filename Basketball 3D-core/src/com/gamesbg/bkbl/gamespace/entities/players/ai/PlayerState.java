package com.gamesbg.bkbl.gamespace.entities.players.ai;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.gamesbg.bkbl.gamespace.entities.Player;
import com.gamesbg.bkbl.gamespace.entities.players.Opponent;
import com.gamesbg.bkbl.gamespace.entities.players.Teammate;

public enum PlayerState implements State<Player> {
	BALL_IN_HAND(){
		float time;
		
		@Override
		public void update(Player player) {
			super.update(player);

			ArrayList<Player> tempOpp;
			if (player instanceof Teammate)
				tempOpp = player.getMap().getOpponents();
			else
				tempOpp = player.getMap().getTeammates();

			ArrayList<Integer> sides = poleSurround(player, tempOpp);
			if (sides.size() > 2) {
				ArrayList<Player> tempTeam;
				if (player instanceof Teammate)
					tempTeam = player.getMap().getTeammates();
				else
					tempTeam = player.getMap().getOpponents();
				
				Vector3 playerVec = player.getModelInstance().transform.getTranslation(new Vector3());
				
				Vector3 tempTeamVec = getShortestDistance(playerVec, tempTeam).nor().scl(Gdx.graphics.getDeltaTime());
				
				player.turnY(tempTeamVec.x + tempTeamVec.z);
				
			} else if(sides.contains(0)) {
				
			}if(sides.contains(2)) {//We don't need south (which is the back of the player)
				if(player.rightHolding())
					player.interactWithBallL();
			}if(sides.contains(3)) {
				if(player.leftHolding())
					player.interactWithBallR();
			} else if ((time += Gdx.graphics.getDeltaTime()) > 0.75f) {
				player.interactWithBallL();

				time = 0;
			}

		}
		
		
	},
	
	BALL_CHASING(){
		@Override
		public void update(Player player) {
			super.update(player);
			
			//System.out.println("Chasing");
			
			player.interactWithBallL();
			player.roamAround(player.getMap().getBall().getModelInstance().transform, null, 0, 0, true);
		}
	},
	
	COOPERATIVE(){
		@Override
		public void update(Player player) {
			super.update(player);
			
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
				player.roamAround(tempPlayer, blockTrans, 3, -1, false);
				//if(newVel.x > 0 || newVel.z > 0)
					
				break;
			case 2:
				player.roamAround(tempPlayer, blockTrans, 5, 1, false);
				break;
				
			case 3:
				player.roamAround(tempPlayer, blockTrans, 12, 2, false);
			case 4:
				player.roamAround(tempPlayer, blockTrans, 0, -13, false);
			case 5:
				player.roamAround(tempPlayer, blockTrans, 4, -9, false);
				break;
			}
		}
	},
	
	PLAYER_SURROUND(){
		@Override
		public void update(Player player) {
			super.update(player);
		}
	},
	
	IDLING() {
		@Override
		public void update(Player player) {
			super.update(player);
		}
	};

	
	
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
	
	/**
	 * Returns the sides of the player in which he's surrounded
	 * @param player
	 * @return an arraylist with the sides (0 - N, 1 - S, 2 - E, 3 - W)
	 */
	protected ArrayList<Integer> poleSurround(Player player, ArrayList<Player> tempOpp) {
		ArrayList<Integer> sides = new ArrayList<Integer>();
		
		//Vector3 playerVec = player.getModelInstance().transform.getTranslation(new Vector3());
		Matrix4 playerTrans = player.getModelInstance().transform;
		
		final float rectLength = 5;
		Vector3 north = playerTrans.cpy().mul(new Matrix4().setToTranslation(-rectLength / 2, 0, player.getDepth() / 2)).getTranslation(new Vector3());
		Vector3 south = playerTrans.cpy().mul(new Matrix4().setToTranslation(-rectLength / 2, 0, -player.getDepth() / 2 - rectLength)).getTranslation(new Vector3());
		Vector3 east = playerTrans.cpy().mul(new Matrix4().setToTranslation(player.getWidth() / 2, 0, -rectLength / 2)).getTranslation(new Vector3());
		Vector3 west = playerTrans.cpy().mul(new Matrix4().setToTranslation(-player.getWidth() / 2 - rectLength, 0, -rectLength / 2)).getTranslation(new Vector3());
		
		Rectangle northRect = new Rectangle(north.x, north.z, rectLength, rectLength);
		Rectangle southRect = new Rectangle(south.x, south.z, rectLength, rectLength);
		Rectangle eastRect = new Rectangle(east.x, east.z, rectLength, rectLength);
		Rectangle westRect = new Rectangle(west.x, west.z, rectLength, rectLength);
		
		
		
		for(Player p : tempOpp) {
			Vector3 tempVec = p.getModelInstance().transform.getTranslation(new Vector3());
			
			if(northRect.contains(tempVec.x, tempVec.z)) {
				//if(!sides.contains(0)) 
					sides.add(0);
			}
			if(southRect.contains(tempVec.x, tempVec.z)) {
				//if(!sides.contains(1)) 
					sides.add(1);
			}
			if(eastRect.contains(tempVec.x, tempVec.z)) {
				//if(!sides.contains(2)) 
					sides.add(2);
			}
			if(westRect.contains(tempVec.x, tempVec.z)) {
				//if(!sides.contains(3)) 
					sides.add(3);
			}
		}
		
		return sides;
	}

	protected Vector3 getShortestDistance(Vector3 position, ArrayList<Player> players) {
		Vector3 tempTeamVec = players.get(0).getModelInstance().transform.getTranslation(new Vector3());
		Vector3 diff = position.cpy().sub(tempTeamVec);
		for(int i = 1; i < players.size(); i++) {
			Matrix4 tempTrans2 = players.get(i).getModelInstance().transform;
			
			Vector3 tempVec2 = tempTrans2.getTranslation(new Vector3());
			
			Vector3 diff2 = position.cpy().sub(tempVec2);
			
			if(diff2.x + diff2.z < diff.x + diff.z) {
				tempTeamVec = tempVec2;
				diff = diff2;
			}
		}
		
		return tempTeamVec;
	}
	
}
