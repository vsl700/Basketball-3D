package com.vasciie.bkbl.gamespace.entities.players.ai;

import java.util.ArrayList;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.vasciie.bkbl.gamespace.entities.Ball;
import com.vasciie.bkbl.gamespace.entities.Entity;
import com.vasciie.bkbl.gamespace.entities.Player;
import com.vasciie.bkbl.gamespace.entities.players.Teammate;
import com.vasciie.bkbl.gamespace.objects.Basket;
import com.vasciie.bkbl.gamespace.tools.GameTools;

public enum PlayerState implements State<Player> {
	BALL_IN_HAND() {

		@Override
		public void enter(Player player) {
			
		}
		
		@Override
		public void update(Player player) {
			Brain brain = player.getBrain();
			AIMemory mem = brain.getMemory();

			Basket basket = player.getTargetBasket();

			// Ball behavior mechanism
			if (!brain.updateShooting(1.25f)) {
				if (/*player.isSurrounded() && player.getMap().getTeammates().size() > 1 || */player.isInAwayBasketZone()) {
					Vector3 playerVec = player.getModelInstance().transform.getTranslation(new Vector3());
					Vector3 tempAimVec;

					if (!player.isBehindBasket())
						tempAimVec = brain.makeBasketTargetVec(player.getTargetBasket());
					else {
						ArrayList<Player> tempTeam;
						if (player instanceof Teammate)
							tempTeam = player.getMap().getTeammates();
						else
							tempTeam = player.getMap().getOpponents();

						tempAimVec = GameTools.getShortestDistance(playerVec, tempTeam);
					}
					
					brain.performShooting(tempAimVec);

				}

				else {

					/*if (player.isEastSurround()) {
						player.turnY(210 * Gdx.graphics.getDeltaTime());

						if (player.rightHolding() && mem.getSwitchHandTime() > 0.5f) {
							player.interactWithBallL();
							mem.setSwitchHandTime(0);
						}
					} else if (player.isWestSurround()) {
						player.turnY(-210 * Gdx.graphics.getDeltaTime());

						if (player.leftHolding() && mem.getSwitchHandTime() > 0.5f) {
							player.interactWithBallR();
							mem.setSwitchHandTime(0);
						}
					} else */if (mem.getDribbleTime() > 0.75f) {
						if (player.isLeftHolding())
							player.interactWithBallL();
						else if (player.isRightHolding())
							player.interactWithBallR();

						mem.setDribbleTime(0);
					} else
						mem.setDribbleTime(mem.getDribbleTime() + Gdx.graphics.getDeltaTime());
				}
			}

			mem.setSwitchHandTime(mem.getSwitchHandTime() + Gdx.graphics.getDeltaTime());

			// Walking & running mechanism
			/*if(player.isBehindBasket()) {
				
			}*/
			
			if(player.isInAwayBasketZone())
				brain.getPursueBallInHand().setEnabled(false);
			else {
				brain.getPursueBallInHand().setEnabled(true);
				player.setRunning();
			}
			
			player.getBrain().getMSBallInHand().calculateSteering(Player.steering);
			player.setMoveVector(Player.steering.linear);
			
			if(!brain.isShooting() && !mem.isBallJustShot())
				player.lookAt(basket.getPosition(), false);

			// Setting the distances from the target (when a player gets in
			// front of this player)
			/*if (player.isNorthSurround()) {
				if (player.isWestSurround())
					mem.setDistDiff(mem.getDistDiff() - 60 * Gdx.graphics.getDeltaTime());
				else
					mem.setDistDiff(mem.getDistDiff() + 60 * Gdx.graphics.getDeltaTime());

			} else if (mem.getResetTime() > 0.25f) {
				mem.setDistDiff(0);
				mem.setResetTime(0);
			}

			//System.out.println("Shoot time:" + mem.getShootTime());
			//System.out.println("Aiming time:" + mem.getAimingTime());

			Vector3 dir = player.roamAround(basket.getMainTrans().cpy().trn(mem.getDistDiff(), 0, 0), null, 0, 5, false, mem.getAimingTime() > 0 || player.isShooting());
			if(mem.getAimingTime() == 0)
				player.lookAt(dir.sub(mem.getDistDiff(), 0, 0));*/

		}

		@Override
		public void exit(Player player) {
			// memory.get(player).setBallJustShot(true);
			// memory.get(player).setShootTime(0);
			// memory.get(player).setAimingTime(0);
			player.getBrain().getMemory().setAimingTime(0);
			
			player.getBrain().getBallSeparate().setEnabled(true);
			
			player.getBrain().getMemory().setTargetVec(null);
			player.getBrain().getMemory().setShootVec(null);
		}

	},

	BALL_CHASING() {
		
		@Override
		public void enter(Player player) {
			
		}
		
		@Override
		public void exit(Player player) {
			// memory.get(player).setBallJustShot(true);
			// memory.get(player).setShootTime(0);
			// memory.get(player).setAimingTime(0);
			player.getBrain().getMemory().setBallJustShot(false);
			player.getBrain().getBallSeparate().setEnabled(true);//Reset
			player.getBrain().getCollAvoid().setEnabled(true);
			player.getBrain().getPlayerSeparate().setEnabled(true);
		}
		
		@Override
		public void update(Player player) {

			AIMemory mem = player.getBrain().getMemory();

			Ball tempBall = player.getMap().getBall();
			
			Vector3 ballVec = tempBall.getPosition();

			// player.getBrain().lookAt.setTarget(player.getMap().getBall());
			// player.getBrain().lookAt.calculateSteering(Player.steering);
			

			//If the following player hadn't just thrown the ball
			if (player.getMap().getTeammates().size() == 1 || !player.getBrain().getMemory().isBallJustShot()) {
				if(player.getMap().getBall().getPosition().y > player.getHeight() * 2 && player.isProximityColliding(player.getMap().getBall())) {
					player.getBrain().getBallSeparate().setEnabled(true);
					//player.getBrain().getBallSeparate().calculateSteering(Player.steering);
					//player.getMoveVector().nor().add(Player.steering.linear.cpy().scl(2.5f));
				}else player.getBrain().getBallSeparate().setEnabled(false);
				
				if(player.getPosition().dst(ballVec) > 2.5f)
					player.getBrain().getPlayerSeparate().setEnabled(true);
				else player.getBrain().getPlayerSeparate().setEnabled(false);
				
				player.getBrain().getMSBallChase().calculateSteering(Player.steering);
				
				player.setMoveVector(Player.steering.linear);
				
				//Vector3 tempAvg = player.getPrevMoveVec().cpy().add(player.getMoveVector()).scl(0.5f); //Just to increase measurement accuracy (average of previous movement and current movement vec)
				
				//System.out.println(tempAvg.x + " ; " + tempAvg.y + " ; " + tempAvg.z);
				//if(Math.abs(tempAvg.x) + Math.abs(tempAvg.z) > 1.5f || Math.abs(tempBall.getLinearVelocity().x) + Math.abs(tempBall.getLinearVelocity().z) > 3.5f)
				if(!player.getBrain().shouldStopToCatch())
					player.setRunning(); //RUUUN! GO CATCH THAT BALL!
				
				
				//if (mem.getCatchTime() > 0.5f) {
					player.interactWithBallA();
				//}
			} else {
				player.getBrain().getMSBallChase().calculateSteering(Player.steering);
				// System.out.println(Player.steering.linear.cpy().x);
				player.setMoveVector(Player.steering.linear);

				//We still have to chase the ball, but we have to do it slowly and also we have to keep some distance so that other players can catch it
				//player.getBrain().getBallSeparate().calculateSteering(Player.steering);
				//player.getMoveVector().add(Player.steering.linear.cpy().scl(1.6f));
				//player.setMoveVector(Player.steering.linear.cpy());
			}
			
			if(player.getBrain().isShooting()) {
				//Player.steering.linear.set(player.getMoveVector());
				//player.getBrain().getLookAt().calculateSteering(Player.steering);
				
				//player.lookAt(player.angleToVector(new Vector3(), Player.steering.angular));
				player.lookAt(mem.getShootVec(), true);//Just in case the mechanics drop it and the player accidentally looks at the ball instead of the target while shooting
			}else
				player.lookAt(ballVec, false);
			
			mem.setCatchTime(mem.getCatchTime() + Gdx.graphics.getDeltaTime());
		}
	},

	COOPERATIVE() {
		@Override
		public void enter(Player player) {
			
		}

		@Override
		public void update(Player player) {
			Brain brain = player.getBrain();
			AIMemory mem = brain.getMemory();
			
			Player holdingPlayer;
			ArrayList<Player> tempOpp;
			if (player instanceof Teammate) {
				holdingPlayer = player.getMap().getTeammateHolding();
				tempOpp = player.getMap().getOpponents();
			}else {
				holdingPlayer = player.getMap().getOpponentHolding();
				tempOpp = player.getMap().getTeammates();
			}

			Ball tempBall = player.getMap().getBall();

			if (mem.getTargetPlayer() == null) {
				Player targetToBlock = GameTools.getClosestPlayer(tempBall.getPosition(), tempOpp, ignored);
				mem.setTargetPlayer(targetToBlock);
				ignored.add(targetToBlock);

				brain.getInterpose().setAgentA(tempBall);
				brain.getInterpose().setAgentB(targetToBlock);

			}else if(holdingPlayer.getPosition().dst(mem.getTargetPlayer().getPosition()) > 5)
				brain.getInterpose().setEnabled(false);
			else brain.getInterpose().setEnabled(true);
			
			player.lookAt(holdingPlayer.getPosition(), false);
			
			brain.getPSCoop().calculateSteering(Player.steering);
			player.setMoveVector(Player.steering.linear);
			
			if(player.getPosition().dst(tempBall.getPosition()) > 6 || player.getMoveVector().len() > 6)
				player.setRunning();
			
		}
		
		@Override
		public void exit(Player player) {
			ignored.remove(player.getBrain().getMemory().getTargetPlayer());
			player.getBrain().getMemory().setTargetPlayer(null);
		}
	},

	PLAYER_SURROUND() {
		@Override
		public void enter(Player player) {
			if (player instanceof Teammate)
				player.getBrain().getMemory().setTargetPlayer(player.getMap().getOpponentHolding());
			else
				player.getBrain().getMemory().setTargetPlayer(player.getMap().getTeammateHolding());
			
			player.getBrain().getPursue().setArrivalTolerance(2f);
		}
		
		@Override
		public void exit(Player player) {
			player.getBrain().getMemory().setTargetPlayer(null);
			player.getBrain().getPursue().setArrivalTolerance(0);
			player.getBrain().getPlayerSeparate().setEnabled(true);
			
			player.getBrain().getMemory().setRandomPointTime(0);
		}

		@Override
		public void update(Player player) {
			Brain brain = player.getBrain();
			Player chased = brain.getMemory().getTargetPlayer();
			
			Ball tempBall = player.getMap().getBall();
			
			int difficulty = player.getMap().getDifficulty();
			
			boolean point = false;
			if(brain.getMemory().isRandomPointTime()) {
				if(!player.getMap().getHoldingPlayer().isAiming() && !player.getMap().getHoldingPlayer().isShooting() && player.getPosition().dst(tempBall.getPosition()) <= 2 && (difficulty == 0 && MathUtils.random(1, 100) <= 10 || difficulty == 1 && MathUtils.random(1, 100) <= 25)) {
					point = true;
				}
				
				brain.getMemory().setRandomPointTime(0);
			}
			
			brain.getMemory().setRandomPointTime(brain.getMemory().getRandomPointTime() + Gdx.graphics.getDeltaTime());
			
			//Movement
			if(player.getPosition().dst(tempBall.getPosition()) > 4.5f || player.getMoveVector().len() > 6) {
				player.setRunning();
				
				brain.getPlayerSeparate().setEnabled(true);
			}else brain.getPlayerSeparate().setEnabled(false);
			
			brain.getMSSurround().calculateSteering(Player.steering);
			player.setMoveVector(Player.steering.linear);
			
			player.lookAt(chased.getPosition(), false);
			
			//Additional controls
			if (chased.isDribbling() || point) {
				Vector3 ballVec = player.getMap().getBall().getModelInstance().transform.getTranslation(new Vector3());
				ArrayList<Vector3> handVecs = new ArrayList<Vector3>();
				handVecs.add(player.getShoulderLTrans().getTranslation(new Vector3()));
				handVecs.add(player.getShoulderRTrans().getTranslation(new Vector3()));

				Vector3 tempHandVec = GameTools.getShortestDistanceWVectors(ballVec, handVecs);

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
			//Brain brain = player.getBrain();
			//brain.getCustomPursue().setArrivalTolerance(brain.getMemory().getTargetPosition().getBoundingRadius());
			//brain.getCustomPursue().setTarget(brain.getMemory().getTargetPosition());
			
		}

		@Override
		public void update(Player player) {
			Brain brain = player.getBrain();
			AIMemory memory = brain.getMemory();
			
			
			if(!player.isHoldingBall() && player.getMap().getBall().getPosition().y > player.getHeight() * 2) {
				player.getBrain().getBallSeparate().setEnabled(true);
				//player.getBrain().getBallSeparate().calculateSteering(Player.steering);
				//player.getMoveVector().nor().add(Player.steering.linear.cpy().scl(2.5f));
			}else player.getBrain().getBallSeparate().setEnabled(false);
			
			Location<Vector3> tempTarget = brain.getCustomPursue().getTarget();
			if(tempTarget == null) {
				brain.getCustomPursue().setEnabled(false);
				brain.getCollAvoid().setEnabled(false);
			}else {
				brain.getCustomPursue().setEnabled(true);
				brain.getCollAvoid().setEnabled(true);
			}
			
			brain.getPSCustom().calculateSteering(Player.steering);
			// System.out.println("Idling movement");

			player.setMoveVector(Player.steering.linear);

			if(!(memory.isCatchBall() && brain.shouldStopToCatch()))//If there's no reason to stop and catch the ball
				if (!player.isRunning() && tempTarget != null && (tempTarget instanceof Entity && !((Entity) tempTarget).getLinearVelocity().isZero(0.1f) || GameTools.getDistanceBetweenLocations(tempTarget, player) >= 3))
					player.setRunning();

			if (memory.getTargetFacing() != null)
				player.lookAt(memory.getTargetFacing().getPosition(), false);
			/*else if(memory.getTargetVec() != null)
			player.lookAt(memory.getTargetVec(), false);*/

			if(player.isHoldingBall())
				memory.setCatchBall(false);
			else if (memory.isCatchBall())
				player.interactWithBallA();
			
		}
		
		@Override
		public void exit(Player player) {
			Brain brain = player.getBrain();
			
			if(brain.getCustomPursue().getTarget() != null) {
				brain.getCustomPursue().setTarget(null);
				brain.getMemory().setTargetPosition(null);
			}
			
			brain.clearCustomTarget();
			
			brain.getMemory().setCatchBall(false);
			
			brain.getCustomPursue().setArrivalTolerance(0);
			
			brain.getCollAvoid().setEnabled(true);
			brain.getBallSeparate().setEnabled(true);
			brain.getAllPlayerSeparate().setEnabled(true);
		}
	};

	// float dribbleTime, aimingTime, shootTime, switchHandTime;

	protected final ArrayList<Player> ignored = new ArrayList<Player>();
	
	@Override
	public void enter(Player entity) {

	}

	@Override
	public void exit(Player entity) {

	}

	@Override
	public boolean onMessage(Player entity, Telegram telegram) {

		return false;
	}
	
	/*protected boolean ignorePlayer(Player player) {
		for (Player p : player.getMap().getTeammates()) {
			Player tempP = p.getBrain().getMemory().getBlockPlayer();
			if (tempP != null && tempP.equals(player))
				return true;
		}
		
		for (Player p : player.getMap().getOpponents()) {
			Player tempP = p.getBrain().getMemory().getBlockPlayer();
			if (tempP != null && tempP.equals(player))
				return true;
		}
		
		return false;
	}*/

	

}
