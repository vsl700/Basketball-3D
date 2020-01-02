package com.gamesbg.bkbl.gamespace.entities.players.ai;

import java.util.ArrayList;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.gamesbg.bkbl.gamespace.entities.Ball;
import com.gamesbg.bkbl.gamespace.entities.Player;
import com.gamesbg.bkbl.gamespace.entities.players.Opponent;
import com.gamesbg.bkbl.gamespace.entities.players.Teammate;
import com.gamesbg.bkbl.gamespace.objects.GameObject;

public enum PlayerState implements State<Player> {
	BALL_IN_HAND() {

		private void performShooting(Player player, Vector3 tempAimVec) {
			AIMemory mem = player.getBrain().getMemory();

			mem.setTargetVec(tempAimVec);
			player.lookAt(tempAimVec);
			player.interactWithBallS();
			mem.setAimingTime(mem.getAimingTime() + Gdx.graphics.getDeltaTime());
		}
		
		private void performShooting(Player player) {
			AIMemory mem = player.getBrain().getMemory();
			
			player.lookAt(mem.getShootVec());//When the calculation starts being correct turn shoot vec into target vec!!!
			player.interactWithBallS();
			mem.setAimingTime(mem.getAimingTime() + Gdx.graphics.getDeltaTime());
		}
		
		/**
		 * Calculates shooting vector according to the location of the player, shooting target
		 * and distance between the player and the target. It also modifies player's shooting 
		 * power.
		 * 
		 * @param player - the shooting player
		 * @param targetVec - the target of the player
		 * @return
		 */
		private Vector3 calculateShootVector(Player player, Vector3 targetVec) {
			Vector3 distVec = targetVec.cpy().sub(player.getPosition()); //Distance between the target and the player
			
			float xzDist = distVec.cpy().scl(1, 0, 1).len(); //Calculate only xz distance. That's what we multiplied y by 0 for.
			float yDist = distVec.cpy().scl(0, 1, 0).len(); //Calculate only y distance. That's what we multiplied x and z by 0 for.
			
			Vector3 returnVec = targetVec.cpy().add(0, xzDist / 2 + yDist, 0);
			
			System.out.println(xzDist + "; " + yDist);
			System.out.print(player.getPosition());
			System.out.print(targetVec);
			System.out.println(returnVec);
			//Modify player shootPower
			
			return returnVec;
		}

		@Override
		public void enter(Player player) {
			
		}
		
		@Override
		public void update(Player player) {

			AIMemory mem = player.getBrain().getMemory();

			GameObject basket = player.getTargetBasket();

			// Ball behavior mechanism
			if (mem.getAimingTime() > 0) {
				mem.setShootVec(calculateShootVector(player, mem.getTargetVec())); //As the player can move even while shooting, we update shootVec every time
				
				if(mem.getAimingTime() <= 1.25f) {
					performShooting(player);
				}
				else if (mem.getAimingTime() > 1.25f) {
					//System.out.println(mem.getShootVec());
					mem.setShootTime(0);
					mem.setCatchTime(0);
					// player.throwBall(mem.getShootVec());
					mem.setBallJustShot(true);
					return;
				}
			} else {
				if (/*player.isSurrounded() && player.getMap().getTeammates().size() > 1 || */player.isInAwayBasketZone()) {
					Vector3 playerVec = player.getModelInstance().transform.getTranslation(new Vector3());
					Vector3 tempAimVec = playerVec.cpy().sub(new Vector3());

					if (!player.isBehindBasket())
						tempAimVec = basket.calcTransformFromNodesTransform(basket.getMatrixes().get(3)).getTranslation(new Vector3());
					else if (mem.getShootTime() > 20 || !player.isInAwayBasketZone()) {
						ArrayList<Player> tempTeam;
						if (player instanceof Teammate)
							tempTeam = player.getMap().getTeammates();
						else
							tempTeam = player.getMap().getOpponents();

						tempAimVec = getShortestDistance(playerVec, tempTeam);
					}
					
					performShooting(player, tempAimVec);

				}

				else {

					if (player.isEastSurround()) {
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
					} else if (mem.getDribbleTime() > 0.75f) {
						if (player.leftHolding())
							player.interactWithBallL();
						else if (player.rightHolding())
							player.interactWithBallR();

						mem.setDribbleTime(0);
					} else
						mem.setDribbleTime(mem.getDribbleTime() + Gdx.graphics.getDeltaTime());
				}
			}

			mem.setSwitchHandTime(mem.getSwitchHandTime() + Gdx.graphics.getDeltaTime());

			// Walking & running mechanism
			if(player.isBehindBasket()) {
				
			}
			
			player.getBrain().getMSBallInHand().calculateSteering(Player.steering);
			player.getMoveVector().set(Player.steering.linear);
			
			player.setRunning();
			
			if(mem.getAimingTime() == 0)
				player.lookAt(basket.getPosition());

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
		}

	},

	BALL_CHASING() {
		
		//To prevent from crowds when the AI tries to catch the ball we will choose only one player from a team to be catching the ball
		boolean teamBallCatcher, oppBallCatcher; //Flags
		
		@Override
		public void enter(Player player) {
			if(!teamBallCatcher && !player.getBrain().getMemory().isBallJustShot() && player instanceof Teammate) {
				teamBallCatcher = true;
				player.getBrain().getMemory().setBallChaser(true);
			}else if(!oppBallCatcher && !player.getBrain().getMemory().isBallJustShot() && player instanceof Opponent) {
				oppBallCatcher = true;
				player.getBrain().getMemory().setBallChaser(true);
			}
		}
		
		@Override
		public void exit(Player player) {
			// memory.get(player).setBallJustShot(true);
			// memory.get(player).setShootTime(0);
			// memory.get(player).setAimingTime(0);
			player.getBrain().getMemory().setBallJustShot(false);
			player.getBrain().getBallSeparate().setEnabled(true);//Reset
			player.getBrain().getCollAvoid().setEnabled(true);
			
			player.getBrain().getMemory().setBallChaser(false);
			
			teamBallCatcher = oppBallCatcher = false;
		}
		
		@Override
		public void update(Player player) {

			AIMemory mem = player.getBrain().getMemory();

			Ball tempBall = player.getMap().getBall();
			
			Vector3 ballVec = tempBall.getModelInstance().transform.getTranslation(new Vector3());
			ArrayList<Vector3> handVecs = new ArrayList<Vector3>();
			handVecs.add(player.getShoulderLTrans().getTranslation(new Vector3()));
			handVecs.add(player.getShoulderRTrans().getTranslation(new Vector3()));

			Vector3 tempHandVec = getShortestDistanceWVectors(ballVec, handVecs);

			// player.getBrain().lookAt.setTarget(player.getMap().getBall());
			// player.getBrain().lookAt.calculateSteering(Player.steering);
			

			//If the following player hadn't just thrown the ball or the amount of players per team is 1 (if the fight is 1v1 the player will be always chasing the ball and trying to catch it)
			if (player.getBrain().getMemory().isBallChaser()) {
				player.getBrain().getPursue().setArrivalTolerance(0.1f);
				player.getBrain().getPlayerSeparate().setEnabled(false);
				//player.getBrain().getBallSeparate().setEnabled(false);
				//player.getBrain().getPursue().calculateSteering(Player.steering);
				
				
				// player.getBrain().obstAvoid.calculateSteering(Player.steering);
				if(player.getMap().getTeammates().size() > 1) {
					//player.getBrain().getCollAvoid().calculateSteering(Player.steering);
					player.getBrain().getCollAvoid().setEnabled(true);
					//player.getMoveVector().add(Player.steering.linear.cpy().scl(0.9f));
				}
				else player.getBrain().getCollAvoid().setEnabled(false);
				
				if(player.getMap().getBall().getPosition().y > player.getHeight()) {
					player.getBrain().getBallSeparate().setEnabled(true);
					//player.getBrain().getBallSeparate().calculateSteering(Player.steering);
					//player.getMoveVector().nor().add(Player.steering.linear.cpy().scl(2.5f));
				}else player.getBrain().getBallSeparate().setEnabled(false);
				
				player.getBrain().getMSBallChase().calculateSteering(Player.steering);
				
				player.getMoveVector().set(Player.steering.linear);
				
				//Vector3 tempAvg = player.getPrevMoveVec().cpy().add(player.getMoveVector()).scl(0.5f); //Just to increase measurement accuracy (average of previous movement and current movement vec)
				
				//System.out.println(tempAvg.x + " ; " + tempAvg.y + " ; " + tempAvg.z);
				//if(Math.abs(tempAvg.x) + Math.abs(tempAvg.z) > 1.5f || Math.abs(tempBall.getLinearVelocity().x) + Math.abs(tempBall.getLinearVelocity().z) > 3.5f)
					player.setRunning(); //RUUUN! GO CATCH THAT BALL!
				
				
				//if (mem.getCatchTime() > 0.5f) {
					if (tempHandVec.idt(handVecs.get(0)))
						player.interactWithBallL();
					else
						player.interactWithBallR();
				//}
			} else {
				//player.getBrain().getPursue().setArrivalTolerance(6);
				player.getBrain().getPlayerSeparate().setEnabled(true);
				player.getBrain().getBallSeparate().setEnabled(true);
				player.getBrain().getCollAvoid().setEnabled(false);

				player.getBrain().getMSBallChase().calculateSteering(Player.steering);
				// System.out.println(Player.steering.linear.cpy().x);
				player.getMoveVector().set(Player.steering.linear);

				//We still have to chase the ball, but we have to do it slowly and also we have to keep some distance so that other players can catch it
				//player.getBrain().getBallSeparate().calculateSteering(Player.steering);
				//player.getMoveVector().add(Player.steering.linear.cpy().scl(1.6f));
				//player.setMoveVector(Player.steering.linear.cpy());
			}
			
			if(player.isShooting()) {
				//Player.steering.linear.set(player.getMoveVector());
				//player.getBrain().getLookAt().calculateSteering(Player.steering);
				
				//player.lookAt(player.angleToVector(new Vector3(), Player.steering.angular));
				player.lookAt(mem.getShootVec());//Just in case the mechanics drop it and the player accidentally looks at the ball instead of the target while shooting
			}else
				player.lookAt(ballVec);
			
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
			//ArrayList<Player> tempTeam;
			//Matrix4 tempPlayerTrans;
			if (player instanceof Teammate) {
				holdingPlayer = player.getMap().getTeammateHolding();
				tempOpp = player.getMap().getOpponents();
				//tempTeam = player.getMap().getTeammates();
			}else {
				holdingPlayer = player.getMap().getOpponentHolding();
				tempOpp = player.getMap().getTeammates();
				//tempTeam = player.getMap().getOpponents();
			}
			//tempPlayerTrans = holdingPlayer.getModelInstance().transform.cpy();

			//Vector3 playerVec = player.getModelInstance().transform.getTranslation(new Vector3());

			Ball tempBall = player.getMap().getBall();
			
			//Matrix4 blockTrans = new Matrix4();
			//if(!player.isAiming() && !player.isShooting() || !player.isInAwayBasketZone()) {
				if (mem.getBlockPlayer() == null) {
					Player targetToBlock = getClosestPlayer(tempBall.getPosition(), tempOpp, ignored);
					mem.setBlockPlayer(targetToBlock);
					ignored.add(targetToBlock);

					//brain.getInterpose().setEnabled(true);
					brain.getInterpose().setAgentA(tempBall);
					brain.getInterpose().setAgentB(targetToBlock);
					
					//player.lookAt(targetToBlock.getPosition());
				}//else player.lookAt(holdingPlayer.getPosition());
			//}else {
				
				//brain.getInterpose().setEnabled(false);
			//}
			
			player.lookAt(holdingPlayer.getPosition());
			/*else if(mem.getBlockPlayer() != null) {
					ignored.remove(mem.getBlockPlayer());
					mem.setBlockPlayer(null);
			}*/
			brain.getMSCoop().calculateSteering(Player.steering);
			player.getMoveVector().set(Player.steering.linear);
			
			if(player.getPosition().dst(tempBall.getPosition()) > 6 || player.getMoveVector().len() > 6)
				player.setRunning();
			
			//Vector3 tempVec = getShortestDistance(player.getPosition(), tempOpp);

			/*blockTrans.setToTranslation(tempVec);

			Vector3 dir;

			switch (player.getPlayerIndex()) {
			case 1:
				if (player.getMap().getTeammates().size() == 2 && (holdingPlayer.isAiming() || holdingPlayer.isShooting()))
					dir = player.roamAround(tempPlayerTrans, blockTrans, 8, 8, false, false);
				else
					dir = player.roamAround(tempPlayerTrans, blockTrans, 3, -1, false, false);
				// if(newVel.x > 0 || newVel.z > 0)

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
			default:
				dir = new Vector3();
				break;
			}

			player.lookAt(dir);*/
		}
		
		@Override
		public void exit(Player player) {
			ignored.remove(player.getBrain().getMemory().getBlockPlayer());
			player.getBrain().getMemory().setBlockPlayer(null);
		}
	},

	PLAYER_SURROUND() {
		@Override
		public void enter(Player player) {
			
		}

		@Override
		public void update(Player player) {
			Player chased;
			if (player instanceof Teammate)
				chased = player.getMap().getOpponentHolding();
			else
				chased = player.getMap().getTeammateHolding();

			Matrix4 chasedTrans = chased.getModelInstance().transform.cpy();

			Matrix4 ballTrans = chased.getMap().getBall().getModelInstance().transform.cpy();

			Vector3 chasedVec = new Vector3();
			chasedTrans.getTranslation(chasedVec);

			Vector3 result;
			if (player.getMap().getTeammates().size() == 1)
				result = player.roamAround(ballTrans, null, 3, 1, false, false);
			else
				switch (player.getPlayerIndex()) {
				case 1:
					result = player.roamAround(ballTrans, null, 0, 3, false, false);
					break;
				case 3:
					result = player.roamAround(ballTrans, null, -1, 3, false, false);
					break;
				case 5:
					result = player.roamAround(ballTrans, null, -3, 1, false, false);
					break;

				case 2:
					result = player.roamAround(ballTrans, null, 1, 3, false, false);
					break;
				case 4:
					result = player.roamAround(ballTrans, null, 3, 1, false, false);
					break;
				default:
					result = new Vector3();
					break;
				}

			player.lookAt(result);

			if (chased.isDribbling()) {
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
			
		}

		@Override
		public void update(Player player) {
		}
	};

	// float dribbleTime, aimingTime, shootTime, switchHandTime;

	ArrayList<Player> ignored = new ArrayList<Player>();
	
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

	/**
	 * 
	 * @param position
	 * @param positions
	 * @return the vector which on the shortest distance of all other positions
	 *         from the given position
	 */
	protected Vector3 getShortestDistanceWVectors(Vector3 position, ArrayList<Vector3> positions) {
		Vector3 tempVec = positions.get(0);
		float dist = position.dst2(tempVec);
		for (int i = 1; i < positions.size(); i++) {
			// Matrix4 tempTrans2 =
			// position.get(i).getModelInstance().transform;

			Vector3 tempVec2 = positions.get(i);

			float dist2 = position.dst2(tempVec2);

			if (dist2 < dist) {
				dist = dist2;
				tempVec = tempVec2;
			}
		}

		return tempVec;
	}

	protected Vector3 getShortestDistance(Vector3 position, ArrayList<Player> players) {
		Vector3 tempVec = players.get(0).getPosition();
		float dist = position.dst2(tempVec);
		// Vector3 diff = position.cpy().sub(tempTeamVec);
		for (int i = 1; i < players.size(); i++) {
			Vector3 tempVec2 = players.get(i).getPosition();

			float dist2 = position.dst2(tempVec2);

			if (dist2 < dist) {
				dist = dist2;
				tempVec = tempVec2;
			}
		}

		return tempVec;
	}
	
	/**
	 * 
	 * @param position - The position of the player that wants to interact with the closest to it player
	 * @param players - The players for check for distances
	 * @param ignored - Players that should be ignored (for example if we are using this to see which player we should block from getting somewhere, the next player should ignore
	 * him and find someone else to ignore. For example in the co-op state)
	 * @return The player which is closest to the given position
	 */
	protected Player getClosestPlayer(Vector3 position, ArrayList<Player> players, ArrayList<Player> ignored) {
		int count = 0;
		Player tempPlayer = null;
		for(int i = 0; i < players.size(); i++) {
			if(ignored.contains(players.get(i))) //They can't be all players ignored, there's always someone left free
				continue;
			
			tempPlayer = players.get(i);
			count = i;
		}
		
		float dist = position.dst2(tempPlayer.getPosition());
		for (int i = count; i < players.size(); i++) {
			Player tempPlayer2 = players.get(i);
			if(ignored.contains(tempPlayer2))
				continue;

			float dist2 = position.dst2(tempPlayer2.getPosition());

			if (dist2 < dist) {
				dist = dist2;
				tempPlayer = tempPlayer2;
			}
		}
		
		return tempPlayer;
	}

}
