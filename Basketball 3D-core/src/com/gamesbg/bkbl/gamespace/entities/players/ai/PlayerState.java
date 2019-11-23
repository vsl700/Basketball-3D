package com.gamesbg.bkbl.gamespace.entities.players.ai;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.math.Matrix4;
import com.gamesbg.bkbl.gamespace.entities.Player;

public enum PlayerState implements State<Player> {
	BALL_IN_HAND(){
		@Override
		public void update(Player player) {
			regularUpdate(player);
		}
	},
	
	BALL_CHASING(){
		@Override
		public void update(Player player) {
			regularUpdate(player);
		}
	},
	
	COOPERATIVE(){
		@Override
		public void update(Player player) {
			regularUpdate(player);
			
			Matrix4 targetTrans = new Matrix4();
			Matrix4 tempPlayer = player.getMap().getTeammateHolding().getModelInstance().transform.cpy();
			
			switch(player.getPlayerIndex()) {
			case 1:
			case 2:
				player.roamAround(tempPlayer);
				break;
				
			case 3:
			case 4:
			case 5:
				break;
			}
		}
	},
	
	PLAYER_SURROUND(){
		@Override
		public void update(Player player) {
			regularUpdate(player);
		}
	},
	
	IDLING() {
		@Override
		public void update(Player player) {
			regularUpdate(player);
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
	
	protected void regularUpdate(Player player) {
		if(player.getMap().isGameRunning()) {
			if(player.holdingBall())
				player.getStateMachine().changeState(BALL_IN_HAND);
			
			else if(player.getMap().isBallInTeam())
				player.getStateMachine().changeState(COOPERATIVE);
			
			else if(player.getMap().isBallInOpp())
				player.getStateMachine().changeState(PLAYER_SURROUND);
			
			else player.getStateMachine().changeState(BALL_CHASING);
		}else 
			player.getStateMachine().changeState(IDLING);
	}

	@Override
	public boolean onMessage(Player entity, Telegram telegram) {
		// TODO Auto-generated method stub
		return false;
	}

	
	
}
