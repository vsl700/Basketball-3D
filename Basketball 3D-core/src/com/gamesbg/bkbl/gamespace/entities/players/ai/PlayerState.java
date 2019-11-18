package com.gamesbg.bkbl.gamespace.entities.players.ai;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.gamesbg.bkbl.gamespace.entities.Player;

public enum PlayerState implements State<Player> {
	BALL_CHASE(){
		@Override
		public void update(Player player) {
			
		}
	},
	
	PLAYER_SURROUND(){
		@Override
		public void update(Player player) {
			
		}
	},
	
	IDLING() {
		@Override
		public void update(Player player) {
			if(player.getMap().isGameRunning())
				player.getStateMachine().changeState(BALL_CHASE);
		}
	};

	@Override
	public void enter(Player entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Player entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exit(Player entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onMessage(Player entity, Telegram telegram) {
		// TODO Auto-generated method stub
		return false;
	}

	
	
}
