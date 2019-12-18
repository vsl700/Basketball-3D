package com.gamesbg.bkbl.gamespace.entities.players.ai;

import java.util.ArrayList;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.gamesbg.bkbl.gamespace.entities.Player;
import com.gamesbg.bkbl.gamespace.entities.players.Teammate;
import com.gamesbg.bkbl.gamespace.objects.GameObject;

public enum PlayerState implements State<Player> {
	BALL_IN_HAND() {

		private void performShooting(Player player, Vector3 tempAimVec) {
			AIMemory mem = player.getBrain().getMemory();

			mem.setShootVec(tempAimVec.nor());
			player.lookAt(tempAimVec);
			player.interactWithBallS();
			mem.setAimingTime(mem.getAimingTime() + Gdx.graphics.getDeltaTime());
		}

		@Override
		public void update(Player player) {

			AIMemory mem = player.getBrain().getMemory();

			GameObject basket;
			if (player instanceof Teammate)
				basket = player.getMap().getAwayBasket();
			else
				basket = player.getMap().getHomeBasket();

			// Ball behavior mechanism
			if (mem.getAimingTime() > 0 && mem.getAimingTime() <= 1.25f) {
				performShooting(player, mem.getShootVec());
			} else {
				if (mem.getAimingTime() > 1.25f) {
					mem.setShootTime(0);

					// player.throwBall(mem.getShootVec());
					mem.setBallJustShot(true);
					return;
				}

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
					} else
						mem.setDribbleTime(mem.getDribbleTime() + Gdx.graphics.getDeltaTime());
				}
			}

			mem.setSwitchHandTime(mem.getSwitchHandTime() + Gdx.graphics.getDeltaTime());

			// Walking & running mechanism

			// Setting the distances from the target (when a player gets in
			// front of this player)
			if (player.isNorthSurround()) {
				if (player.isWestSurround())
					mem.setDistDiff(mem.getDistDiff() - 60 * Gdx.graphics.getDeltaTime());
				else
					mem.setDistDiff(mem.getDistDiff() + 60 * Gdx.graphics.getDeltaTime());

			} else if (mem.getResetTime() > 0.25f) {
				mem.setDistDiff(0);
				mem.setResetTime(0);
			}

			System.out.println("Shoot time:" + mem.getShootTime());
			System.out.println("Aiming time:" + mem.getAimingTime());

			Vector3 dir = player.roamAround(basket.getMainTrans().cpy().trn(mem.getDistDiff(), 0, 0), null, 0, 5, false, mem.getAimingTime() > 0 || player.isShooting() || true);
			if(mem.getAimingTime() == 0)
				player.lookAt(dir.sub(mem.getDistDiff(), 0, 0));

		}

		@Override
		public void exit(Player player) {
			// memory.get(player).setBallJustShot(true);
			// memory.get(player).setShootTime(0);
			// memory.get(player).setAimingTime(0);
			player.getBrain().getMemory().setAimingTime(0);
		}

	},

	BALL_CHASING() {
		@Override
		public void update(Player player) {

			AIMemory mem = player.getBrain().getMemory();

			Vector3 ballVec = player.getMap().getBall().getModelInstance().transform.getTranslation(new Vector3());
			ArrayList<Vector3> handVecs = new ArrayList<Vector3>();
			handVecs.add(player.getShoulderLTrans().getTranslation(new Vector3()));
			handVecs.add(player.getShoulderRTrans().getTranslation(new Vector3()));

			Vector3 tempHandVec = getShortestDistanceWVectors(ballVec, handVecs);

			// player.getBrain().lookAt.setTarget(player.getMap().getBall());
			// player.getBrain().lookAt.calculateSteering(Player.steering);
			if(!player.isShooting())
				player.lookAt(ballVec);

			//If the following player hadn't just thrown the ball or the amount of players per team is 1 (if the fight is 1v1 the player will be always chasing the ball and try to catch it)
			if (!mem.isBallJustShot() || player.getMap().getTeammates().size() == 1) {
				player.getBrain().pursue.setArrivalTolerance(0);

				player.getBrain().pursue.calculateSteering(Player.steering);
				// System.out.println(Player.steering.linear.cpy().x);
				player.setMoveVector(Player.steering.linear.cpy());

				// player.getBrain().obstAvoid.calculateSteering(Player.steering);
				player.getBrain().collAvoid.calculateSteering(Player.steering);
				player.getMoveVector().add(Player.steering.linear);

				//RUUUN! GO CATCH THAT BALL!
				player.setRunning();

				if (tempHandVec.idt(handVecs.get(0)))
					player.interactWithBallL();
				else if (tempHandVec.idt(handVecs.get(1)))
					player.interactWithBallR();
			} else {
				player.getBrain().pursue.setArrivalTolerance(1);

				player.getBrain().pursue.calculateSteering(Player.steering);
				// System.out.println(Player.steering.linear.cpy().x);
				player.setMoveVector(Player.steering.linear.cpy().scl(0.1f));

				//We still have to chase the ball, but we have to do it slowly and also we have to keep some distance so that other players can catch it
				player.getBrain().getSeparate().calculateSteering(Player.steering);
				player.getMoveVector().add(Player.steering.linear);
				// player.setMoveVector(Player.steering.linear.cpy());
			}

			player.getMoveVector().nor().scl(Gdx.graphics.getDeltaTime());
		}
	},

	COOPERATIVE() {
		@Override
		public void enter(Player player) {
			player.getBrain().getMemory().setBallJustShot(false);
		}

		@Override
		public void update(Player player) {
			Player tempPlayer;
			Matrix4 tempPlayerTrans;
			if (player instanceof Teammate)
				tempPlayer = player.getMap().getTeammateHolding();
			else
				tempPlayer = player.getMap().getOpponentHolding();

			tempPlayerTrans = tempPlayer.getModelInstance().transform.cpy();

			Vector3 playerVec = player.getModelInstance().transform.getTranslation(new Vector3());

			ArrayList<Player> tempOpp;
			if (player instanceof Teammate)
				tempOpp = player.getMap().getOpponents();
			else
				tempOpp = player.getMap().getTeammates();

			Matrix4 blockTrans = new Matrix4();

			Vector3 tempVec = getShortestDistance(playerVec, tempOpp);

			blockTrans.setToTranslation(tempVec);

			Vector3 dir;

			switch (player.getPlayerIndex()) {
			case 1:
				if (player.getMap().getTeammates().size() == 2 && (tempPlayer.isAiming() || tempPlayer.isShooting()))
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

			player.lookAt(dir);
		}
	},

	PLAYER_SURROUND() {
		@Override
		public void enter(Player player) {
			player.getBrain().getMemory().setBallJustShot(false);
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
			player.getBrain().getMemory().setBallJustShot(false);
		}

		@Override
		public void update(Player player) {
		}
	};

	// float dribbleTime, aimingTime, shootTime, switchHandTime;

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

	/**
	 * 
	 * @param position
	 * @param positions
	 * @return the vector which on the shortest distance of all other positions
	 *         from the given position
	 */
	protected Vector3 getShortestDistanceWVectors(Vector3 position, ArrayList<Vector3> positions) {
		Vector3 tempVec = positions.get(0);
		float dist = position.dst(tempVec);
		for (int i = 1; i < positions.size(); i++) {
			// Matrix4 tempTrans2 =
			// position.get(i).getModelInstance().transform;

			Vector3 tempVec2 = positions.get(i);

			float dist2 = position.dst(tempVec2);

			if (dist2 < dist) {
				dist = dist2;
				tempVec = tempVec2;
			}
		}

		return tempVec;
	}

	protected Vector3 getShortestDistance(Vector3 position, ArrayList<Player> players) {
		Vector3 tempVec = players.get(0).getModelInstance().transform.getTranslation(new Vector3());
		float dist = position.dst(tempVec);
		// Vector3 diff = position.cpy().sub(tempTeamVec);
		for (int i = 1; i < players.size(); i++) {
			Matrix4 tempTrans2 = players.get(i).getModelInstance().transform;

			Vector3 tempVec2 = tempTrans2.getTranslation(new Vector3());

			float dist2 = position.dst(tempVec2);

			if (dist2 < dist) {
				dist = dist2;
				tempVec = tempVec2;
			}
		}

		return tempVec;
	}

}
