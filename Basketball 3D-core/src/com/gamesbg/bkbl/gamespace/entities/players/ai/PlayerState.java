package com.gamesbg.bkbl.gamespace.entities.players.ai;

import java.util.ArrayList;
import java.util.HashMap;

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
		
		@Override
		public void update(Player player) {
			super.update(player);
			
			AIMemory mem = memory.get(player);
			
			if(!player.getStateMachine().isInState(BALL_IN_HAND) || mem.isBallJustShot())
				return;
			
			
			
			GameObject basket;
			if(player instanceof Teammate)
				basket = player.getMap().getAwayBasket();
			else basket = player.getMap().getHomeBasket();
			
			//Ball behavior mechanism
			if (mem.getAimingTime() > 0 && mem.getAimingTime() < 1.25f) {
				System.out.println("Shoot1");
				player.interactWithBallS();
				mem.setAimingTime(mem.getAimingTime() + Gdx.graphics.getDeltaTime());
			} else {
				if(mem.getAimingTime() >= 1.25f) {
					return;
				}
				
				if (player.isSurrounded() && player.getMap().getTeammates().size() > 1) {
					Vector3 tempAimVec = new Vector3().setToRandomDirection();
					Vector3 playerVec = player.getModelInstance().transform.getTranslation(new Vector3());
					
					if (player.isInAwayBasketZone()) {
						if(player.isBehindBasket())
							tempAimVec = new Vector3(); 
						else tempAimVec = basket.getModelInstance().transform.getTranslation(new Vector3());
					} else if(mem.getShootTime() > 20) {
						ArrayList<Player> tempTeam;
						if (player instanceof Teammate)
							tempTeam = player.getMap().getTeammates();
						else
							tempTeam = player.getMap().getOpponents();

						tempAimVec = getShortestDistance(playerVec, tempTeam).nor();
					}
					
					
					player.lookAt(tempAimVec);
					System.out.println("Shoot2");
					player.interactWithBallS();
					mem.setBallJustShot(true);
					mem.setAimingTime(mem.getAimingTime() + Gdx.graphics.getDeltaTime());
					
					
				}

				else {
					
					
					if (player.isEastSurround()) {
					player.turnY(210 * Gdx.graphics.getDeltaTime());
					
					if (mem.getAimingTime() == 0 && player.rightHolding() && mem.getSwitchHandTime() > 0.5f) {
						player.interactWithBallL();
						mem.setSwitchHandTime(0);
					}
				} else if (player.isWestSurround()) {
					player.turnY(-210 * Gdx.graphics.getDeltaTime());
					
					if (mem.getAimingTime() == 0 && player.leftHolding() && mem.getSwitchHandTime() > 0.5f) {
						player.interactWithBallR();
						mem.setSwitchHandTime(0);
					}
				} else if (mem.getAimingTime() == 0 && mem.getDribbleTime() > 0.75f) {
					if (player.leftHolding())
						player.interactWithBallL();
					else if (player.rightHolding())
						player.interactWithBallR();

					mem.setDribbleTime(0);
				}else
					mem.setDribbleTime(mem.getDribbleTime() + Gdx.graphics.getDeltaTime());
				}
			}
			
			mem.setSwitchHandTime(mem.getSwitchHandTime() + Gdx.graphics.getDeltaTime());
			
			//Walking & running mechanism
			//We use this if just for the aim & shoot starter. The if below continues it.
			if(player.isInAwayBasketZone() && mem.getAimingTime() == 0) {
				Vector3 tempAimVec = basket.calcTransformFromNodesTransform(basket.getMatrixes().get(3)).getTranslation(new Vector3());
				
				player.lookAt(tempAimVec);
				System.out.println("Shoot3");
				player.interactWithBallS();
				mem.setBallJustShot(true);
				mem.setAimingTime(mem.getAimingTime() + Gdx.graphics.getDeltaTime());
				
				return;
			}
			
			
			if (player.isNorthSurround()) {
				if (player.isWestSurround())
					mem.setDistDiff(mem.getDistDiff() - 60 * Gdx.graphics.getDeltaTime());
				else 
					mem.setDistDiff(mem.getDistDiff() + 60 * Gdx.graphics.getDeltaTime());
				
			}
			else if(mem.getResetTime() > 0.25f) {
				mem.setDistDiff(0);
				mem.setResetTime(0);
			}
			
			
			Vector3 dir = player.roamAround(basket.getMainTrans().cpy().trn(mem.getDistDiff(), 0, 0), null, 0, 5, false, mem.getAimingTime() > 0 || player.isShooting());
			player.lookAt(dir.sub(mem.getDistDiff(), 0, 0));
			
		}
		
		@Override
		public void exit(Player player) {
			//memory.get(player).setBallJustShot(true);
			memory.get(player).setShootTime(0);
			memory.get(player).setAimingTime(0);
		}
		
		@Override
		public void setSpecialBoolean(boolean b, Player player) {
			memory.get(player).setBallJustShot(b);
		}
		
		@Override
		public boolean isSpecialBoolean(Player player) {
			return memory.get(player).isBallJustShot();
		}
		
	},
	
	BALL_CHASING(){
		@Override
		public void update(Player player) {
			super.update(player);
			
			AIMemory mem = memory.get(player);
			
			Vector3 ballVec = player.getMap().getBall().getModelInstance().transform.getTranslation(new Vector3());
			ArrayList<Vector3> handVecs = new ArrayList<Vector3>();
			handVecs.add(player.getShoulderLTrans().getTranslation(new Vector3()));
			handVecs.add(player.getShoulderRTrans().getTranslation(new Vector3()));
			
			Vector3 tempHandVec = getShortestDistanceWVectors(ballVec, handVecs);
			
			if(!BALL_IN_HAND.isSpecialBoolean(player)) {
				if (player.isNorthSurround()) {
					if (player.isWestSurround())
						mem.setDistDiff(mem.getDistDiff() - 60 * Gdx.graphics.getDeltaTime());
					else 
						mem.setDistDiff(mem.getDistDiff() + 60 * Gdx.graphics.getDeltaTime());
					
				}
				else if(mem.getResetTime() > 0.25f) {
					mem.setDistDiff(0);
					mem.setResetTime(0);
				}
				
				if (tempHandVec.idt(handVecs.get(0)))
					player.interactWithBallL();
				else if (tempHandVec.idt(handVecs.get(1)))
					player.interactWithBallR();
				
				player.lookAt(player.roamAround(player.getMap().getBall().getModelInstance().transform.cpy().trn(mem.getDistDiff(), 0, 0), null, 0, 0, true, false));
			}
			else
				player.lookAt(player.roamAround(player.getMap().getBall().getModelInstance().transform, null, 0, 0, false, true));
		}
	},
	
	COOPERATIVE(){
		@Override
		public void enter(Player player) {
			BALL_IN_HAND.setSpecialBoolean(false, player);
		}
		
		@Override
		public void update(Player player) {
			super.update(player);
			
			if(!player.getStateMachine().isInState(COOPERATIVE))
				return;
			
			Player tempPlayer;
			Matrix4 tempPlayerTrans;
			if(player instanceof Teammate)
				tempPlayer = player.getMap().getTeammateHolding();
			else tempPlayer = player.getMap().getOpponentHolding();
			
			tempPlayerTrans = tempPlayer.getModelInstance().transform.cpy();
			
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
				if(player.getMap().getTeammates().size() == 2 && (tempPlayer.isAiming() || tempPlayer.isShooting()))
					dir = player.roamAround(tempPlayerTrans, blockTrans, 8, 8, false, false);
				else dir = player.roamAround(tempPlayerTrans, blockTrans, 3, -1, false, false);
				//if(newVel.x > 0 || newVel.z > 0)
					
				break;
			case 2:
				dir = player.roamAround(tempPlayerTrans, blockTrans, 5, 1, false, false);
				break;
				
			case 3:
				dir = player.roamAround(tempPlayerTrans, blockTrans, 12, 2, false, false);
			case 4:
				dir = player.roamAround(tempPlayerTrans, blockTrans, 0, 13, false, false);
			case 5:
				dir = player.roamAround(tempPlayerTrans, blockTrans, 4, 9, false, false);
				break;
			default: dir = new Vector3();
				break;
			}
			
			player.lookAt(dir);
		}
	},
	
	
	PLAYER_SURROUND(){
		@Override
		public void enter(Player player) {
			BALL_IN_HAND.setSpecialBoolean(false, player);
		}
		
		@Override
		public void update(Player player) {
			super.update(player);
			
			if(!player.getStateMachine().isInState(PLAYER_SURROUND))
				return;
			
			Player chased;
			if(player instanceof Teammate)
				chased = player.getMap().getOpponentHolding();
			else chased = player.getMap().getTeammateHolding();
			
			Matrix4 chasedTrans = chased.getModelInstance().transform.cpy();
			
			Vector3 chasedVec = new Vector3();
			chasedTrans.getTranslation(chasedVec);
			
			Vector3 result;
			if(player.getMap().getTeammates().size() == 1)
				result = player.roamAround(chasedTrans, null, 3, 1, false, false);
			else switch(player.getPlayerIndex()) {
			case 1:
				result = player.roamAround(chasedTrans, null, 0, 3, false, false);
				break;
			case 3:
				result = player.roamAround(chasedTrans, null, -1, 3, false, false);
				break;
			case 5:
				result = player.roamAround(chasedTrans, null, -3, 1, false, false);
				break;
				
			case 2:
				result = player.roamAround(chasedTrans, null, 2, 3, false, false);
				break;
			case 4: 
				result = player.roamAround(chasedTrans, null, 3, 1, false, false);
				break;
				default: 
					result = new Vector3();
					break;
			}
			
			player.lookAt(result);
			
			if(chased.isDribbling()) {
				Vector3 ballVec = player.getMap().getBall().getModelInstance().transform.getTranslation(new Vector3());
				ArrayList<Vector3> handVecs = new ArrayList<Vector3>();
				handVecs.add(player.getShoulderLTrans().getTranslation(new Vector3()));
				handVecs.add(player.getShoulderRTrans().getTranslation(new Vector3()));
				
				Vector3 tempHandVec = getShortestDistanceWVectors(ballVec, handVecs);
				
				if (tempHandVec.idt(handVecs.get(0)))
					player.interactWithBallL();
				else if (tempHandVec.idt(handVecs.get(1)))
					player.interactWithBallR();
			}
		}
	},
	
	IDLING() {
		
		@Override
		public void enter(Player player) {
			if(memory == null)
				memory = new HashMap<Player, AIMemory>();
			
			if(!memory.containsKey(player))
				memory.put(player, new AIMemory());
			
			BALL_IN_HAND.setSpecialBoolean(false, player);
		}
		
		@Override
		public void update(Player player) {
			super.update(player);
		}
	};

	//float dribbleTime, aimingTime, shootTime, switchHandTime;
	
	static HashMap<Player, AIMemory> memory;
	
	@Override
	public void enter(Player entity) {
		
		
	}

	@Override
	public void exit(Player entity) {
		
		
	}
	
	protected void setSpecialBoolean(boolean b, Player player) {}
	
	protected boolean isSpecialBoolean(Player player) { return false; }
	
	@Override
	public void update(Player player) {
		AIMemory mem = memory.get(player);
		
		mem.setShootTime(mem.getShootTime() + Gdx.graphics.getDeltaTime());
		mem.setResetTime(mem.getResetTime() + Gdx.graphics.getDeltaTime());
		
		if(player.getMap().isGameRunning()) {
			if(player.holdingBall())
				player.getStateMachine().changeState(BALL_IN_HAND);
			
			else {
				
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
