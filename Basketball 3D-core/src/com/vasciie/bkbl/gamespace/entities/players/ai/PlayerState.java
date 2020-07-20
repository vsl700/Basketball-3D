package com.vasciie.bkbl.gamespace.entities.players.ai;

import java.util.ArrayList;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.vasciie.bkbl.gamespace.entities.Ball;
import com.vasciie.bkbl.gamespace.entities.Entity;
import com.vasciie.bkbl.gamespace.entities.Player;
import com.vasciie.bkbl.gamespace.entities.players.Opponent;
import com.vasciie.bkbl.gamespace.entities.players.Teammate;
import com.vasciie.bkbl.gamespace.objects.Basket;
import com.vasciie.bkbl.gamespace.tools.GameTools;

public enum PlayerState implements State<Player> {
	BALL_IN_HAND() {

		@Override
		public void enter(Player player) {
			
		}
		
		private boolean isAnOpponentClose(Player player) {
			ArrayList<Player> tempOpp;
			if (player instanceof Teammate)
				tempOpp = player.getMap().getTeammates();
			else
				tempOpp = player.getMap().getOpponents();
			
			
			for(Player p : tempOpp)
				if(p.getPosition().dst(player/*.getMap().getBall()*/.getPosition()) <= 0.85f)
					return true;
			
			return false;
		}
		
		/*private Array<Player> playersFurtherFromBasket(Player player){
			Array<Player> players = new Array<Player>(player.getMap().getTeammates().size());//Heard that libGDX's Array works faster than Java's ArrayList...
			
			ArrayList<Player> tempTeam;
			if(player instanceof Teammate)
				tempTeam = player.getMap().getTeammates();
			else tempTeam = player.getMap().getOpponents();
			
			Basket targetBasket = player.getTargetBasket();
			
			for(Player p : tempTeam) {
				if(!p.equals(player) && p.getPosition().dst(targetBasket.getPosition()) > player.getPosition().dst(targetBasket.getPosition()))
					players.add(p);
			}
			
			return players;
		}*/
		
		/*private Array<Player> playersInZone(Player player, String zoneId){
			Array<Player> players = new Array<Player>(player.getMap().getTeammates().size());//Heard that libGDX's Array works faster than Java's ArrayList...
			
			ArrayList<Player> tempTeam;
			if(player instanceof Teammate)
				tempTeam = player.getMap().getTeammates();
			else tempTeam = player.getMap().getOpponents();
			
			Zone zone = player.getMap().getZones().getZone(zoneId);
			
			for(Player p : tempTeam) {
				if(zone.checkZone(GameTools.toVector2(p.getPosition(), tempVec1), GameTools.toVector2(p.getDimensions(), tempVec2)))
					players.add(p);
			}
			
			return players;
		}*/
		
		private boolean checkPlayerPointsDiff(Player player) {
			float scoreDiff = 4;
			
			return player instanceof Teammate && player.getMap().getOppScore() - player.getMap().getTeamScore() >= scoreDiff ||
					player instanceof Opponent && player.getMap().getTeamScore() - player.getMap().getOppScore() >= scoreDiff;
		}
		
		@Override
		public void update(Player player) {
			Brain brain = player.getBrain();
			AIMemory mem = brain.getMemory();

			Basket basket = player.getTargetBasket();
			Ball ball = player.getMap().getBall();
			
			int difficulty = player.getMap().getDifficulty();
			if(mem.isRandomFoulTime()) {
				if (difficulty == 0 && player instanceof Opponent && MathUtils.random(0, 100) <= 60) {
					mem.setDribbleTime(mem.getDribbleTime() - Gdx.graphics.getDeltaTime() * 2.1f);
				}else if(difficulty == 2 && player instanceof Teammate && MathUtils.random(0, 100) <= 60) {
					mem.setDribbleTime(mem.getDribbleTime() - Gdx.graphics.getDeltaTime() * 2.1f);
				}
				
				mem.setRandomFoulTime(0);
			}
			
			mem.setRandomFoulTime(mem.getRandomFoulTime() + Gdx.graphics.getDeltaTime());

			Array<Player> tempPlayers = null;
			
			if(player.isFocusing() || player.getMap().getTeammates().size() > 1 && 
					(player.isInHomeThreePointZone() || player.isBehindBasket() || (mem.getDribbleTime() > 0.7f && !player.isInAwayThreePointZone()) && isAnOpponentClose(player) && 
							(player.getAwayZone().checkZone(ball.getPosition(), ball.getDimensions()) && GameTools.playersInZone(player, player.getAwayZone()).size > 0))) {
				
				/*Array<Player> tempPlayers = playersFurtherFromBasket(player);
				
				ArrayList<Player> tempTeam;
				if(player instanceof Teammate)
					tempTeam = player.getMap().getTeammates();
				else tempTeam = player.getMap().getOpponents();
				
				if(tempPlayers.size >= tempTeam.size() - 1 && player.getAwayZone().checkZone(player.getPosition(), player.getDimensions()))*///We have to avoid Backcourt Violation
				tempPlayers = GameTools.playersOutOfZone(player, player.getAwayZone());
				
				player.focus(tempPlayers, true);
				
				Player focusedPlayer = player.getFocusedPlayer();
				if(focusedPlayer == null)
					return;
				
				brain.getMemory().setTargetPlayer(focusedPlayer);
				
				if(brain.isShooting())
					brain.getMemory().setTargetVec(focusedPlayer.getPosition());
			}else if (brain.tooCloseOrBehindBasket()) {
				brain.getPursueBallInHand().setEnabled(false);
				brain.getPursueBallInHand2().setEnabled(true);
				
				player.setRunning();
			}else {
				brain.getPursueBallInHand().setEnabled(true);
				brain.getPursueBallInHand2().setEnabled(false);
			}
			
			
			
			// Ball behavior mechanism
			float time;
			if(player.isFocusing()) {
				if(player.isInHomeThreePointZone())
					time = 1.3f;
				else time = 0.7f;
				
				if(tempPlayers != null && (player instanceof Teammate && tempPlayers.size == player.getMap().getTeammates().size() || player instanceof Opponent && tempPlayers.size == player.getMap().getOpponents().size()))
					mem.setAimingTime(mem.getAimingTime() - Gdx.graphics.getDeltaTime());
			}else time = 1.25f;
			
			if (!brain.updateShooting(time)) {
				Vector3 tempAimVec = null;

				if (player.isFocusing())
					tempAimVec = brain.getMemory().getTargetPlayer().getPosition();
				else if ((player.isInAwayBasketZone() || player.isInAwayThreePointZone() && checkPlayerPointsDiff(player)) && !brain.tooCloseOrBehindBasket())
					tempAimVec = brain.makeBasketTargetVec(player.getTargetBasket());
				

				if (tempAimVec != null)
					brain.performShooting(tempAimVec);
				else {
					if (mem.getDribbleTime() > 0.7f) {
						if (player.getMap().getOpponents().size() > 0) {
							ArrayList<Player> tempOpp;
							if (player instanceof Teammate)
								tempOpp = player.getMap().getOpponents();
							else
								tempOpp = player.getMap().getTeammates();

							Player closestPlayer = GameTools.getClosestPlayer(player.getPosition(), tempOpp, null);

							Vector3 leftHandPos = player.calcTransformFromNodesTransform(player.getModelInstance().getNode("handL").globalTransform).getTranslation(new Vector3());
							Vector3 rightHandPos = player.calcTransformFromNodesTransform(player.getModelInstance().getNode("handR").globalTransform).getTranslation(new Vector3());

							if (closestPlayer.getPosition().dst(leftHandPos) >= closestPlayer.getPosition().dst(rightHandPos))
								player.interactWithBallL();
							else
								player.interactWithBallR();
						}
						
						mem.setDribbleTime(0);
					} else
						mem.setDribbleTime(mem.getDribbleTime() + Gdx.graphics.getDeltaTime());
				}
			}

			//mem.setSwitchHandTime(mem.getSwitchHandTime() + Gdx.graphics.getDeltaTime());

			// Walking & running mechanism
			/*if(player.isBehindBasket()) {
				
			}*/
			
			if(player.isFocusing())
				return;
				
			
			
			if(player.isInAwayBasketZone() || brain.isShooting() && (!player.getHomeZone().checkZone(ball.getPosition(), ball.getDimensions()) && player.isInAwayZone()))
				brain.getPursueBallInHand().setEnabled(false);
			else {
				brain.getPursueBallInHand().setEnabled(true);
				
				if(!brain.isShooting())
					player.setRunning();
			}
			
			player.getBrain().getMSBallInHand().calculateSteering(Player.steering);
			
			if(player.getMap().getDifficulty() < 2 || !brain.isShooting())
				player.setMoveVector(Player.steering.linear);
			
			if(!brain.isShooting() && !mem.isBallJustShot())
				player.lookAt(brain.getPursueBallInHand().getTarget().getPosition(), false);

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
			player.getBrain().getMemory().setDribbleTime(0);
			player.getBrain().getMemory().setRandomFoulTime(0);
			
			player.getBrain().getBallSeparate().setEnabled(true);
			
			player.getBrain().getMemory().setTargetVec(null);
			player.getBrain().getMemory().setShootVec(null);
			player.getBrain().getMemory().setTargetPlayer(null);
			
			player.getBrain().getPursueBallInHand2().setEnabled(false);
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
		
		private boolean willStayInZone(Player player) {
			return player.isInAwayBasketZone() && player.getAwayBasketZone().checkZone(player.getPosition().add(Player.steering.linear.cpy().scl(3))) || !player.isInAwayBasketZone() || !player.getBrain().getMemory().isBallJustShot();
		}
		
		@Override
		public void update(Player player) {
			AIMemory mem = player.getBrain().getMemory();

			Ball tempBall = player.getMap().getBall();
			
			Vector3 ballVec = tempBall.getPosition();

			// player.getBrain().lookAt.setTarget(player.getMap().getBall());
			// player.getBrain().lookAt.calculateSteering(Player.steering);
			

			//If the following player hadn't just thrown the ball
			if (player instanceof Teammate && player.getMap().getTeammates().size() == 1 || player instanceof Opponent && player.getMap().getOpponents().size() == 1 || !player.getBrain().getMemory().isBallJustShot()) {
				if(tempBall.getLinearVelocity().y < 0 && player.getMap().getBall().getPosition().y > player.getHeight() * 2 && player.isProximityColliding(player.getMap().getBall()) || player.getPosition().dst(ballVec) < tempBall.getWidth() * 1.25f) {
					player.getBrain().getBallSeparate().setEnabled(true);
					//player.getBrain().getBallSeparate().calculateSteering(Player.steering);
					//player.getMoveVector().nor().add(Player.steering.linear.cpy().scl(2.5f));
				}else player.getBrain().getBallSeparate().setEnabled(false);
				
				if(player.getPosition().dst(ballVec) > 2.5f)
					player.getBrain().getPlayerSeparate().setEnabled(true);
				else player.getBrain().getPlayerSeparate().setEnabled(false);
				
				player.getBrain().getMSBallChase().calculateSteering(Player.steering);
				
				if(player.getMap().getDifficulty() < 2 || willStayInZone(player)) {
					player.setMoveVector(Player.steering.linear);
					
					//Vector3 tempAvg = player.getPrevMoveVec().cpy().add(player.getMoveVector()).scl(0.5f); //Just to increase measurement accuracy (average of previous movement and current movement vec)
					
					//System.out.println(tempAvg.x + " ; " + tempAvg.y + " ; " + tempAvg.z);
					//if(Math.abs(tempAvg.x) + Math.abs(tempAvg.z) > 1.5f || Math.abs(tempBall.getLinearVelocity().x) + Math.abs(tempBall.getLinearVelocity().z) > 3.5f)
					if(!player.getBrain().shouldStopToCatch())
						player.setRunning(); //RUUUN! GO CATCH THAT BALL!
				}
				
				
				//if (mem.getCatchTime() > 0.5f) {
					player.interactWithBallA();
				//}
			} else{
				player.getBrain().getMSBallChase().calculateSteering(Player.steering);
				// System.out.println(Player.steering.linear.cpy().x);
				if(player.getMap().getDifficulty() < 2 || willStayInZone(player))
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
			Brain brain = player.getBrain();
			AIMemory mem = brain.getMemory();
			Player holdingPlayer = player.getMap().getHoldingPlayer();
			
			//brain.getPlayerBasketInterpose().setInterpositionRatio(0.3f);
			
			brain.getPlayerBasketInterpose().setAgentA(holdingPlayer);
			if(brain.getPlayerBasketInterpose().getAgentB() == null)
				brain.getPlayerBasketInterpose().setAgentB(holdingPlayer.getTargetBasket());
			
			mem.setTargetPlayer(holdingPlayer);
		}

		@Override
		public void update(Player player) {
			Brain brain = player.getBrain();
			Player holdingPlayer = brain.getMemory().getTargetPlayer();
			
			boolean check = holdingPlayer.getPosition().dst(player.getPosition()) <= holdingPlayer.getPosition().dst(holdingPlayer.getTargetBasket().getPosition()) * brain.getPlayerBasketInterpose().getInterpositionRatio() - 3;
			boolean check2 = check && player.getPosition().dst(brain.getPlayerBasketInterpose().getInternalTargetPosition()) > 3;
			
			if(player.isInAwayThreePointZone()) {
				brain.getPlayerBasketInterpose().setEnabled(false);
				
				brain.getAllPlayerSeparate().setEnabled(true);
				brain.getTargetPlayerSeparate().setEnabled(true);
			}else {
				brain.getPlayerBasketInterpose().setEnabled(true);
				brain.getTargetPlayerSeparate().setEnabled(false);
				
				if(check)
					brain.getAllPlayerSeparate().setEnabled(false);
				else brain.getAllPlayerSeparate().setEnabled(true);
			}
			
			if(player.isInAwayZone())
				brain.getPlayerBasketInterpose().setInterpositionRatio(0.5f);
			else brain.getPlayerBasketInterpose().setInterpositionRatio(0.3f);
			
			if(player.getPosition().dst(holdingPlayer.getPosition()) < 1.5f)
				brain.getTargetPlayerSeparate().setEnabled(true);
			
			
			player.lookAt(holdingPlayer.getPosition(), false);
			
			brain.getPSCoop().calculateSteering(Player.steering);
			player.setMoveVector(Player.steering.linear);
			
			if(check2)
				player.setRunning();
			
		}
		
		@Override
		public void exit(Player player) {
			Brain brain = player.getBrain();
			
			brain.getMemory().setTargetPlayer(null);
			
			brain.getPlayerBasketInterpose().setAgentA(null);
			brain.getPlayerBasketInterpose().setAgentB(null);
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
			
			player.getBrain().getMemory().setRandomFoulTime(0);
			
			player.getBrain().getMemory().setMissStealing(false);
		}
		
		private boolean shouldMakeReachin(Player player, Player holdingPlayer, Ball tempBall) {
			return !holdingPlayer.isAiming() && !holdingPlayer.isCurrentlyAiming() && !holdingPlayer.isShooting() && player.getPosition().dst(tempBall.getPosition()) <= 2;
		}

		@Override
		public void update(Player player) {
			Brain brain = player.getBrain();
			AIMemory mem = brain.getMemory();
			Player chased = mem.getTargetPlayer();
			
			Ball tempBall = player.getMap().getBall();
			
			int difficulty = player.getMap().getDifficulty();
			
			boolean point = false;
			if(mem.isRandomFoulTime()) {
				Player holdingPlayer = player.getMap().getHoldingPlayer();
				if(difficulty == 0 && player instanceof Opponent) {
					if (shouldMakeReachin(player, holdingPlayer, tempBall) && MathUtils.random(1, 100) <= 75) {
						point = true;
					}
				}else if(difficulty == 2 && player instanceof Teammate) {
					if (shouldMakeReachin(player, holdingPlayer, tempBall) && MathUtils.random(1, 100) <= 75) {
						point = true;
					}
				}
					
				mem.setRandomFoulTime(0);
			}
			
			mem.setRandomFoulTime(mem.getRandomFoulTime() + Gdx.graphics.getDeltaTime());
			
			//Movement
			if (player.getPosition().dst(tempBall.getPosition()) > 4.5f || player.getMoveVector().len() > 6) {
				

				brain.getPlayerSeparate().setEnabled(true);

			} else
				brain.getPlayerSeparate().setEnabled(false);
			
			if(player instanceof Teammate && player.getMap().getTeammates().size() == 1 || player instanceof Opponent && player.getMap().getOpponents().size() == 1) {
				if(!player.isCurrentlyRunning() && player.getPosition().dst(chased.getPosition()) > 3 || player.isCurrentlyRunning() && player.getPosition().dst(chased.getPosition()) > 1.78f)
					player.setRunning();
			}else player.setRunning();
			
			brain.getMSSurround().calculateSteering(Player.steering);
			player.setMoveVector(Player.steering.linear);
			
			player.lookAt(chased.getPosition(), false);
			
			//Additional controls
			if (chased.isDribbling() && !mem.isMissStealing() || point) {
				if(difficulty == 0 && !point) {
					if(MathUtils.random(1, 100) <= 60) {
						mem.setMissStealing(true);
						return;
					}
				}
				
				Vector3 ballVec = player.getMap().getBall().getModelInstance().transform.getTranslation(new Vector3());
				ArrayList<Vector3> handVecs = new ArrayList<Vector3>();
				handVecs.add(player.getShoulderLTrans().getTranslation(new Vector3()));
				handVecs.add(player.getShoulderRTrans().getTranslation(new Vector3()));

				Vector3 tempHandVec = GameTools.getShortestDistanceWVectors(ballVec, handVecs);

				if (tempHandVec.idt(handVecs.get(0)))
					player.interactWithBallL();
				else if (tempHandVec.idt(handVecs.get(1)))
					player.interactWithBallR();
			}else if(!chased.isDribbling() && !point) mem.setMissStealing(false);
		}
	},
	
	HOLDING_PLAYER_SHOOTING{
		
		@Override
		public void enter(Player player) {
			Player targetPlayer = player.getMap().getHoldingPlayer();
			Brain brain = player.getBrain();
			
			brain.getMemory().setTargetPlayer(targetPlayer);
			
			brain.getPlayerBasketInterpose().setAgentA(targetPlayer);
			brain.getPlayerBasketInterpose().setAgentB(targetPlayer.getTargetBasket());
			
			brain.getPlayerBasketInterpose().setEnabled(true);
			brain.getAllPlayerSeparate().setEnabled(true);
			
			player.getBrain().getPlayerBasketInterpose().setInterpositionRatio(0.5f);
		}
		
		@Override
		public void exit(Player player) {
			Brain brain = player.getBrain();
			
			brain.getPlayerBasketInterpose().setAgentA(null);
			brain.getPlayerBasketInterpose().setAgentB(null);
			
			brain.getMemory().setTargetPlayer(null);
		}

		@Override
		public void update(Player player) {
			Brain brain = player.getBrain();
			AIMemory mem = brain.getMemory();
			Player chased = mem.getTargetPlayer();
			

			if (chased.getPosition().dst(player.getPosition()) <= chased.getPosition().dst(chased.getTargetBasket().getPosition()) / 2 - 3)
				player.setRunning();
			
			brain.getPSShooting().calculateSteering(Player.steering);
			player.setMoveVector(Player.steering.linear);
			
			player.lookAt(chased.getPosition(), false);
			
		}
		
	},

	IDLING() {

		@Override
		public void enter(Player player) {
			Brain brain = player.getBrain();
			//brain.getCustomPursue().setArrivalTolerance(brain.getMemory().getTargetPosition().getBoundingRadius());
			//brain.getCustomPursue().setTarget(brain.getMemory().getTargetPosition());
			//player.getBrain().getBasketSeparate().setEnabled(true);
			
			brain.getCollAvoid().setEnabled(true);
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
				//brain.getCollAvoid().setEnabled(false);
			}else {
				brain.getCustomPursue().setEnabled(true);
				
			}
			
			
			
			//float checkConst = 2;
			//if(player.getMap().getHomeBasket().getPosition().dst(player.getPosition()) <= checkConst || player.getMap().getAwayBasket().getPosition().dst(player.getPosition()) <= checkConst)
			
			//else brain.getBasketSeparate().setEnabled(false);
			
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
			
			player.getBrain().getPursueBallInHand2().setEnabled(false);
		}
	};

	// float dribbleTime, aimingTime, shootTime, switchHandTime;

	//protected final ArrayList<Player> ignored = new ArrayList<Player>();
	
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
