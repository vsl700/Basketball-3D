package com.vasciie.bkbl.gamespace.entities.players.ai;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.vasciie.bkbl.gamespace.entities.Ball;
import com.vasciie.bkbl.gamespace.entities.Player;
import com.vasciie.bkbl.gamespace.entities.players.Opponent;
import com.vasciie.bkbl.gamespace.entities.players.Teammate;
import com.vasciie.bkbl.gamespace.objects.Basket;
import com.vasciie.bkbl.gamespace.tools.GameTools;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteerableAdapter;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.ai.steer.behaviors.BlendedSteering;
import com.badlogic.gdx.ai.steer.behaviors.CollisionAvoidance;
import com.badlogic.gdx.ai.steer.behaviors.Interpose;
import com.badlogic.gdx.ai.steer.behaviors.LookWhereYouAreGoing;
import com.badlogic.gdx.ai.steer.behaviors.PrioritySteering;
import com.badlogic.gdx.ai.steer.behaviors.Separation;
import com.badlogic.gdx.ai.utils.Location;

/**
 * Each player needs a brain to think with, otherwise there will be no point of having a head on the players' models ;)
 * 
 * @author studi
 *
 */
public class Brain {

	//The thinking part
	StateMachine<Player, PlayerState> stateMachine;
	
	//The memory part (each brain needs memory to remember some properties, like player's targets or current strategy properties)
	AIMemory memory;
	
	//The player that uses the brain
	Player user;
	
	//Some player behaviors
	Arrive<Vector3> pursue, pursueBallInHand, pursueBallInHand2, customPursue;
	LookWhereYouAreGoing<Vector3> lookAt;
	CollisionAvoidance<Vector3> collAvoid; //For players and basket stands
	Interpose<Vector3> interpose; //For blocking opponents from getting close to the player holding the ball in co-op state
	Interpose<Vector3> playerBasketInterpose;
	Separation<Vector3> ballSeparate, basketSeparate, playerSeparate, allPlayerSeparate, targetPlayerSeparate; //For player and basket surroundings and ball distance keeping
	//RaycastObstacleAvoidance<Vector3> obstAvoid; //For invisible user.getMap().getTerrain() walls
	PrioritySteering<Vector3> pSBallChasePart, pSCoop, pSSurround, pSShooting, pSCustom, pSCustom2;
	BlendedSteering<Vector3> mSBallChase, mSBallInHand, mSSurround;	//Groups of behaviors for each state
	
	
	//RayConfiguration<Vector3> rayConfig;
	
	public Brain(final Player user) {
		this.user = user;
		
		stateMachine = new DefaultStateMachine<Player, PlayerState>(user, PlayerState.IDLING);
		memory = new AIMemory();
		
		SteerableAdapter<Vector3> tempSteerable = new SteerableAdapter<Vector3>() {
			@Override
			public Vector3 getPosition() {
				return new Vector3(GameTools.toVector3(user.getAwayBasketZone().getPositions()[0], new Vector3()).scl(0, 0, 1));
			}
		};
		
		pursue = new Arrive<Vector3>(user, user.getMap().getBall());
		pursue.setDecelerationRadius(user.getMap().getBall().getWidth() * 1.5f);
		
		pursueBallInHand = new Arrive<Vector3>(user, user.getTargetBasket());
		pursueBallInHand2 = new Arrive<Vector3>(user, tempSteerable);
		pursueBallInHand2.setEnabled(false);
		
		customPursue = new Arrive<Vector3>(user, null);
		customPursue.setArrivalTolerance(0);
		//pursue.setArrivalTolerance(0.1f);
		user.setMaxLinearAcceleration(1);
		lookAt = new LookWhereYouAreGoing<Vector3>(user);
		collAvoid = new CollisionAvoidance<Vector3>(user, user);
		interpose = new Interpose<Vector3>(user, null, null, 0.8f);
		playerBasketInterpose = new Interpose<Vector3>(user, null, null, 0.5f);
		//playerBasketInterpose.setArrivalTolerance(1.5f);
		ballSeparate = new Separation<Vector3>(user, user); //The differences between these behaviors are in the overrided findNeighbors void in the Player class
		basketSeparate = new Separation<Vector3>(user, user);//
		playerSeparate = new Separation<Vector3>(user, user);//
		allPlayerSeparate = new Separation<Vector3>(user, user);
		targetPlayerSeparate = new Separation<Vector3>(user, user);
		
		//rayConfig = new ParallelSideRayConfiguration<Vector3>(user, 3, 0.5f);
		//obstAvoid = new RaycastObstacleAvoidance<Vector3>(user, rayConfig, user.getMap());
		
		//In this steering mechanism if the player needs to ride off a player or players (if playerSeparate returns different from 0) it will ride off instead of getting closer to the ball. 
		//Otherwise it will just follow it. In other words it follows the ball but also keeps distance from other player so that they can catch it.
		pSBallChasePart = new PrioritySteering<Vector3>(user);
		pSBallChasePart.add(ballSeparate);
		pSBallChasePart.add(playerSeparate);
		pSBallChasePart.add(pursue);
		
		mSBallChase = new BlendedSteering<Vector3>(user);//FIXME Unexpected separation behaviors (see findNeighbors in Player)
		mSBallChase.add(collAvoid, 0.9f);
		//mSBallChase.add(playerSeparate, 2.4f);
		//mSBallChase.add(ballSeparate, 2.4f);
		mSBallChase.add(pSBallChasePart, 1f);
		//mSBallChase.add(pursue, 1.2f);
		
		PrioritySteering<Vector3> pSBallInHandPart = new PrioritySteering<Vector3>(user);
		pSBallInHandPart.add(collAvoid);
		pSBallInHandPart.add(basketSeparate);
		pSBallInHandPart.add(allPlayerSeparate);
		
		mSBallInHand = new BlendedSteering<Vector3>(user);
		/*mSBallInHand.add(collAvoid, 1.6f);
		mSBallInHand.add(basketSeparate, 0.6f);
		mSBallInHand.add(allPlayerSeparate, 1f);*/
		mSBallInHand.add(pSBallInHandPart, 1.5f);
		mSBallInHand.add(pursueBallInHand, 1.3f);
		mSBallInHand.add(pursueBallInHand2, 1.3f);
		
		pSCoop = new PrioritySteering<Vector3>(user);
		pSCoop.add(targetPlayerSeparate);
		pSCoop.add(collAvoid);
		pSCoop.add(allPlayerSeparate);
		pSCoop.add(playerBasketInterpose);
		
		mSSurround = new BlendedSteering<Vector3>(user);
		mSSurround.add(allPlayerSeparate, 1.5f);
		mSSurround.add(playerSeparate, 1.3f);
		mSSurround.add(collAvoid, 1);
		mSSurround.add(pursue, 1.3f);
		
		pSSurround = new PrioritySteering<Vector3>(user);
		pSSurround.add(allPlayerSeparate);
		pSSurround.add(playerSeparate);
		pSSurround.add(collAvoid);
		pSSurround.add(pursue);
		
		pSShooting = new PrioritySteering<Vector3>(user);
		pSShooting.add(allPlayerSeparate);
		pSShooting.add(playerBasketInterpose);
		pSShooting.add(collAvoid);
		
		pSCustom = new PrioritySteering<Vector3>(user);
		pSCustom.add(targetPlayerSeparate);
		pSCustom.add(collAvoid);
		pSCustom.add(ballSeparate);
		//pSCustom.add(basketSeparate);
		pSCustom.add(allPlayerSeparate);
		pSCustom.add(pursueBallInHand2);
		pSCustom.add(customPursue);
		//mSCoop.add(playerSeparate, 0.9f);
	}
	
	public void clear() {
		stateMachine = null;
		memory.clear();
		memory = null;
		
		user = null;
		
		pursue.setTarget(null);
		pursue.setOwner(null);
		pursueBallInHand.setTarget(null);
		pursueBallInHand.setOwner(null);
		customPursue.setTarget(null);
		customPursue.setOwner(null);
		pursue = pursueBallInHand = customPursue = null;
		
		lookAt.setTarget(null);
		lookAt.setOwner(null);
		lookAt = null;
		
		collAvoid.setOwner(null);
		collAvoid = null;
		
		interpose.setTarget(null);
		interpose.setOwner(null);
		interpose.setAgentA(null);
		interpose.setAgentB(null);
		interpose = null;
		
		ballSeparate = basketSeparate = playerSeparate = allPlayerSeparate = null;
		//obstAvoid = null;
		pSBallChasePart = pSCoop = pSSurround = pSCustom = null;
		mSBallChase = mSBallInHand = mSSurround = null;
	}
	
	public void update(boolean updateAI) {
		memory.setShootTime(memory.getShootTime() + Gdx.graphics.getDeltaTime());
		//System.out.println(memory.getShootTime());
		memory.setResetTime(memory.getResetTime() + Gdx.graphics.getDeltaTime());
		
		//The state switcher
		if(pSCustom2 == null && ((user.getMap().isTutorialMode() && user.getMap().getCurrentTutorialLevel().getCurrentPart().updatePlayersNormalAI() || !user.getMap().isTutorialMode()) && user.getMap().isGameRunning() && !user.getMap().isRuleTriggeredActing())) {
			//If the current player is holding the ball
			if(user.isHoldingBall()) {
				if(!stateMachine.isInState(PlayerState.BALL_IN_HAND)) stateMachine.changeState(PlayerState.BALL_IN_HAND);
			
			}else {
				
				//If a teammate of the current player (either a teammate or an opponent to the main player) is holding the ball
				if(user instanceof Teammate && user.getMap().isBallInTeam() || user instanceof Opponent && user.getMap().isBallInOpp()) {
					if(user.getMap().getHoldingPlayer().isCurrentlyAiming() || user.getMap().getHoldingPlayer().isShooting()) {
						if(!stateMachine.isInState(PlayerState.HOLDING_PLAYER_SHOOTING)) stateMachine.changeState(PlayerState.HOLDING_PLAYER_SHOOTING);
					}
					else if(!stateMachine.isInState(PlayerState.COOPERATIVE)) stateMachine.changeState(PlayerState.COOPERATIVE);
				}
				//If an opponent of the current player (either a teammate or an opponent to the main player) is holding the ball
				else if(user instanceof Teammate && user.getMap().isBallInOpp() || user instanceof Opponent && user.getMap().isBallInTeam()) {
					if(user.getMap().getHoldingPlayer().isCurrentlyAiming() || user.getMap().getHoldingPlayer().isShooting()) {
						if(!stateMachine.isInState(PlayerState.HOLDING_PLAYER_SHOOTING)) stateMachine.changeState(PlayerState.HOLDING_PLAYER_SHOOTING);
					}
					else if(!stateMachine.isInState(PlayerState.PLAYER_SURROUND)) stateMachine.changeState(PlayerState.PLAYER_SURROUND);
				}
				//If nobody is holding the ball
				else 
					if(!stateMachine.isInState(PlayerState.BALL_CHASING)) stateMachine.changeState(PlayerState.BALL_CHASING);
			}
		}
		//If the game is actually not running (the startup timer is still counting or a game rule has been broken), just go to idling mode
		else
			if(!stateMachine.isInState(PlayerState.IDLING)) stateMachine.changeState(PlayerState.IDLING);
		
		//Regular state updating
		if(updateAI)
			stateMachine.update();
		
		if (!user.isHoldingBall()) {
			Player temp = user.getMap().getRecentHolder();
			//System.out.println(GameTools.getDistanceBetweenLocations(temp = GameTools.getClosestPlayer(user.getPosition(), user.getMap().getAllPlayers(), null), user));
			if (temp != null && !temp.equals(user) && (!temp.isHoldingBall() || temp.isAimingOrShooting() || temp.isInAwayThreePointZone()) && Player.steering.linear.cpy().nor().scl(Gdx.graphics.getDeltaTime() * 11).add(user.getPosition()).dst(temp.getPosition()) <= user.getWidth() + 0.8f) {
				Player tempPlayer = memory.getTargetPlayer();
				memory.setTargetPlayer(temp);
				
				boolean enabled = targetPlayerSeparate.isEnabled();
				targetPlayerSeparate.setEnabled(true);
				
				targetPlayerSeparate.calculateSteering(Player.steering);
				
				if(canMove())
					user.setMoveVector(Player.steering.linear);

				memory.setTargetPlayer(tempPlayer);
				targetPlayerSeparate.setEnabled(enabled);
			}
		}
		
		int difficulty = user.getMap().getDifficulty();
		if(difficulty < 2 && user.isMainPlayer() && !user.isDribbling() && !user.isAiming() && !user.isShooting() && !stateMachine.isInState(PlayerState.IDLING)) {
			float dribbleTime = memory.getDribbleTime();
			if (user.isHoldingBall()) {
				if (user.isMainPlayer() && dribbleTime >= 0.6f) {
					if (user.isLeftHolding())
						user.interactWithBallL();
					else
						user.interactWithBallR();

					memory.setDribbleTime(0);
				}else
					memory.setDribbleTime(dribbleTime + Gdx.graphics.getDeltaTime());
			}else memory.setDribbleTime(0);
		}else if(user.isMainPlayer()) memory.setDribbleTime(0);
	}
	
	public void performShooting(Vector3 tempAimVec) {
		memory.setTargetVec(tempAimVec);
		user.interactWithBallS();
		memory.setAimingTime(memory.getAimingTime() + Gdx.graphics.getDeltaTime());
	}
	
	private void performShooting() {
		user.lookAt(memory.getShootVec(), true);//When the calculation starts being correct turn shoot vec into target vec!!!
		user.interactWithBallS();
		memory.setAimingTime(memory.getAimingTime() + Gdx.graphics.getDeltaTime());
	}
	
	/**
	 * Calculates shooting vector according to the location of the player, shooting target
	 * and distance between the player and the target. It also modifies player's shooting 
	 * power.
	 *
	 * @param targetVec - the target of the player
	 * @return
	 */
	private Vector3 calculateShootVector(Vector3 targetVec) {
		/*Vector3 distVec = targetVec.cpy().sub(user.getPosition()); //Distance between the target and the player
		
		float xzDist = distVec.cpy().scl(1, 0, 1).len(); //Calculate only xz distance. That's what we multiplied y by 0 for.
		float yDist = distVec.cpy().scl(0, 1, 0).len(); //Calculate only y distance. That's what we multiplied x and z by 0 for.
		
		Vector3 returnVec = targetVec.cpy().add(0, xzDist / 2 + yDist, 0);*/
		
		Vector3 returnVec = targetVec.cpy();
		/*if(memory.getTargetPlayer() != null)
			returnVec.add(memory.getTargetPlayer().getMoveVector());*/
		
		//Vector3 tempTargetVec;
		
		//returnVec.scl(0.5f, 1, 0.5f);
		
		/*Vector3 changeVec = returnVec.cpy();
		float changer = user.getWidth() / 2;
		if(user.isLeftHolding()) {
			changeVec.rotate(-90, 0, 1, 0).nor().scl(changer).y = 0;
		}else if(user.isRightHolding()){
			changeVec.rotate(90, 0, 1, 0).nor().scl(changer).y = 0;
		}
		
		returnVec.add(changeVec);*/
		
		//System.out.println();
		
		Vector3 pos = user.getMap().getBall().getPosition();
		
		boolean targetCheck = targetVec.y - 0.9f > user.getPosition().y;
		
		/*if(targetCheck)
			System.out.println("Basket shooting (targetCheck)");*/
		
		float near = 11;
		
		float dst = pos.dst(targetVec);
		
		float farRotation = dst / 1.41f;
		float nearRotation = (1 / dst) * 90;
		float rotation;
		if(dst < near && targetCheck)
			rotation = nearRotation;
		else rotation = farRotation;
		
		//if(targetVec.cpy().sub(user.getPosition()).z > 0)
			//rotation = -rotation;
		
		/*if(targetCheck)
			rotation+= 30;
		else */rotation-= 3;
		
		//System.out.println(rotation);
		
		//returnVec.rotate(rotation, 1, 0, 0);
		returnVec.y += rotation;
		
		/*System.out.println(xzDist + "; " + yDist);
		System.out.print(player.getPosition());
		System.out.print(targetVec);
		System.out.println(returnVec);*/
		//Modify player shootPower
		float farShootPower = dst * 0.53f;
		float nearShootPower = (20 - dst) / 8.9f;
		float shootPower;
		if(dst < near) {
			shootPower = nearShootPower;
			
			//System.out.println("NEAR SHOOTING");
		}else {
			shootPower = farShootPower;
			//System.out.println("FAR SHOOTING");
		}
		
		//System.out.println(shootPower);
		
		user.setShootPower(shootPower);
		//System.out.println(farShootPower + " ; " + nearShootPower + ": " + shootPower + "; dst: " + dst);
		
		return returnVec;
	}
	
	/**
	 * Updates AI shooting, if it has ever started of course
	 * @return true if the shooting has ever started and it has been updated
	 */
	public boolean updateShooting(float maxShootingTime) {
		if (isShooting()) {
			memory.setShootVec(calculateShootVector(memory.getTargetVec())); //As the player can move even while shooting, we update shootVec every time
			
			if(!isShootTargetABasket())
				checkForObstacles();
			
			if(memory.getAimingTime() <= maxShootingTime) {
				performShooting();
			}
			else {
				//System.out.println(memory.getShootVec());
				memory.setShootTime(0);
				memory.setCatchTime(0);
				memory.setAimingTime(0);
				// user.throwBall(memory.getShootVec());
				memory.setBallJustShot(true);
				
			}
			
			return true;
		}
		
		if(memory.isBallJustShot())//Practically, if the ball has just been shot, there has been a shooting initialized, so we return true (also there'are shooting problems without this check)
			return true;
		
		return false;
	}
	
	private void checkForObstacles() {
		Vector3 targetVec = memory.getTargetVec();
		Vector3 pos = user.getPosition();
		Vector3 dirVec = targetVec.cpy().sub(pos).nor();
		
		avoidObstacles(user.getMap().getHomeBasket(), dirVec);
		avoidObstacles(user.getMap().getAwayBasket(), dirVec);
		
		ArrayList<Player> players = user.getMap().getAllPlayers();
		
		Player closestPlayer = null;
		float maxDist = 0;
		
		float dst = pos.dst(targetVec);
		
		float targetConst = user.getDepth() / 2;
		float checkConst = user.getWidth() / dst * 3;
		for(int i = 0; i < players.size(); i++) {
			if(players.get(i).equals(user) || players.get(i).getPosition().dst(targetVec) <= targetConst)
				continue;
			
			Vector3 tempPos = players.get(i).getPosition();
			float tempDist = tempPos.dst(pos);
			
			if(maxDist < tempDist && dst > tempPos.dst(pos)) {
				Vector3 tempDir = tempPos.cpy().sub(pos).nor();
				
				if(tempDir.dst(dirVec) <= checkConst) {
					maxDist = tempDist;
					closestPlayer = players.get(i);
				}
			}
		}
		
		if(closestPlayer == null || closestPlayer.getPosition().dst(targetVec) <= targetConst || closestPlayer.getPosition().cpy().sub(pos).nor().dst(dirVec) > checkConst)
			return;
		
		//System.out.println("Orientation modified");
		
		Vector3 shootVec = memory.getShootVec();
		float rotation = 1 / maxDist * 130;
		
		//if(shootVec.cpy().sub(user.getPosition()).z > 0)
			//rotation = -rotation;
		
		//System.out.println(rotation);
		
		shootVec.y += rotation;
		
		/*float max = 15;
		if(shootVec.y > max)
			shootVec.y = max;*/
	}
	
	private void avoidObstacles(Location<Vector3> obstacle, Vector3 currentDir) {
		if (GameTools.getDistanceBetweenLocations(user, obstacle) < 2) {
			Vector3 tempPos = obstacle.getPosition();
			Vector3 tempVec = tempPos.nor();
			
			Vector3 handPos;
			if(user.isLeftHolding()) {
				handPos = user.calcTransformFromNodesTransform(user.getModelInstance().getNode("handL").globalTransform).getTranslation(new Vector3());
				
				float checkConst = user.getWidth();
				if (currentDir.cpy().sub(handPos.cpy().nor()).dst(tempVec) <= checkConst) {
					float delta = Gdx.graphics.getDeltaTime();

					user.walk(new Vector3(-1, 0, 0).scl(delta));
				}
				
			}else {
				handPos = user.calcTransformFromNodesTransform(user.getModelInstance().getNode("handR").globalTransform).getTranslation(new Vector3());
				
				float checkConst = user.getWidth();
				if (currentDir.cpy().sub(handPos.cpy().nor()).dst(tempVec) <= checkConst) {
					float delta = Gdx.graphics.getDeltaTime();

					user.walk(new Vector3(1, 0, 0).scl(delta));
				}
			}
			
			
		}
	}
	
	public boolean isShooting() {
		return memory.getAimingTime() > 0;
	}
	
	private boolean isShootTargetABasket() {
		Vector3 targetVec = memory.getTargetVec();
		
		Basket homeBasket = user.getMap().getHomeBasket();
		Vector3 homeBasketPos = makeBasketTargetVec(homeBasket);
		
		Basket awayBasket = user.getMap().getAwayBasket();
		Vector3 awayBasketPos = makeBasketTargetVec(awayBasket);
		
		float checkConst = 0.1f;
		
		return targetVec.dst(homeBasketPos) <= checkConst || targetVec.dst(awayBasketPos) <= checkConst;
	}
	
	public boolean canMove() {
		float add = 11;
		Vector3 vec1 = user.getPosition();
		Vector3 vec2 = Player.steering.linear.cpy().nor().scl(Math.min(1, Gdx.graphics.getDeltaTime())).scl(add);
		
		return !memory.isBallJustShot() || user.isInAwayBasketZone() && user.getAwayBasketZone().checkZone(vec1.add(vec2)) || !user.isInAwayBasketZone();
	}
	
	public boolean shouldStopToCatch() {
		Ball tempBall = user.getMap().getBall();
		
		return !(tempBall.getPosition().y < user.getHeight() * 1.5f || tempBall.getPosition().cpy().scl(1, 0, 1).dst(user.getPosition().cpy().scl(1, 0, 1)) > tempBall.getWidth() * 2.25f) || (tempBall.getPosition().dst(user.getPosition()) <= user.getDepth() + tempBall.getDepth() / 2 + 0.7f && !tempBall.isGrounded());
	}
	
	public boolean tooCloseOrBehindBasket() {
		return user.isBehindBasket() || user.getPosition().dst(user.getTargetBasket().getPosition()) <= 9;
	}
	
	/*public boolean isAbleToShoot() {
		return memory.getShootTime() > 20;
	}*/
	
	public void setCustomTarget(Steerable<Vector3> steerable) {
		memory.setTargetPosition(steerable);
		customPursue.setTarget(steerable);
	}
	
	public void setCustomVecTarget(final Vector3 target, boolean facing) {
		float terrainWidth = user.getMap().getTerrain().getWidth();
		float terrainDepth = user.getMap().getTerrain().getDepth();
		
		if(target.x < -terrainWidth / 2)
			target.x = -terrainWidth / 2;
		else if(target.x > terrainWidth / 2)
			target.x = terrainWidth / 2;
		
		if(target.z < -terrainDepth / 2)
			target.z = -terrainDepth / 2;
		else if(target.z > terrainDepth / 2)
			target.z = terrainDepth / 2;
		
		SteerableAdapter<Vector3> tempSteerable = new SteerableAdapter<Vector3>() {
			@Override
			public Vector3 getPosition() {
				return target;
			}
		};
		
		setCustomTarget(tempSteerable);
		
		if(facing)
			memory.setTargetFacing(tempSteerable);
	}
	
	public void clearCustomTarget() {
		customPursue.setTarget(null);
		memory.setTargetPosition(null);
		memory.setTargetFacing(null);
	}
	
	public Vector3 makeBasketTargetVec(Basket targetBasket) {
		Vector3 ballPos = user.getMap().getBall().getPosition();
		
		if(ballPos.x > targetBasket.getWidth() / 2)
			return targetBasket.getTabCenterPartTrans(user.getPosition().z > 0 ? 3 : 2).getTranslation(new Vector3());
		else if(ballPos.x < -targetBasket.getWidth() / 2)
			return targetBasket.getTabCenterPartTrans(user.getPosition().z > 0 ? 2 : 3).getTranslation(new Vector3());
		else 
			return targetBasket.getTabCenterPartTrans(1).getTranslation(new Vector3());
	}
	
	public void addCustomBHV(SteeringBehavior<Vector3> behavior) {
		if(pSCustom2 == null)
			pSCustom2 = new PrioritySteering<Vector3>(user);
		
		pSCustom2.add(behavior);
	}
	
	/**
	 * USE IT BEFORE ANY NEW USAGE OF THE CUSTOM BEHAVIOR!
	 */
	public void clearCustomBHV() {
		pSCustom2 = null;
	}

	public StateMachine<Player, PlayerState> getStateMachine() {
		return stateMachine;
	}

	public AIMemory getMemory() {
		return memory;
	}

	public Arrive<Vector3> getPursue() {
		return pursue;
	}
	
	public Arrive<Vector3> getPursueBallInHand() {
		return pursueBallInHand;
	}
	
	public Arrive<Vector3> getPursueBallInHand2() {
		return pursueBallInHand2;
	}
	
	public Arrive<Vector3> getCustomPursue() {
		return customPursue;
	}

	public LookWhereYouAreGoing<Vector3> getLookAt() {
		return lookAt;
	}

	public CollisionAvoidance<Vector3> getCollAvoid() {
		return collAvoid;
	}

	public Interpose<Vector3> getInterpose() {
		return interpose;
	}

	public Interpose<Vector3> getPlayerBasketInterpose() {
		return playerBasketInterpose;
	}

	public PrioritySteering<Vector3> getPSCoop() {
		return pSCoop;
	}

	public Separation<Vector3> getBallSeparate() {
		return ballSeparate;
	}
	
	public Separation<Vector3> getBasketSeparate() {
		return basketSeparate;
	}

	public Separation<Vector3> getPlayerSeparate() {
		return playerSeparate;
	}

	public Separation<Vector3> getTargetPlayerSeparate() {
		return targetPlayerSeparate;
	}

	public Separation<Vector3> getAllPlayerSeparate() {
		return allPlayerSeparate;
	}

	public BlendedSteering<Vector3> getMSBallChase() {
		return mSBallChase;
	}
	
	public BlendedSteering<Vector3> getMSBallInHand() {
		return mSBallInHand;
	}
	
	public BlendedSteering<Vector3> getMSSurround() {
		return mSSurround;
	}

	public PrioritySteering<Vector3> getPSSurround() {
		return pSSurround;
	}
	
	public PrioritySteering<Vector3> getPSShooting() {
		return pSShooting;
	}

	public PrioritySteering<Vector3> getPSCustom(){
		return pSCustom;
	}
	
	public PrioritySteering<Vector3> getPSCustom2(){
		return pSCustom2;
	}
	
}
