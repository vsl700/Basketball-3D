package com.vasciie.bkbl.gamespace.entities.players.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.vasciie.bkbl.gamespace.entities.Player;
import com.vasciie.bkbl.gamespace.entities.players.Opponent;
import com.vasciie.bkbl.gamespace.entities.players.Teammate;
import com.vasciie.bkbl.gamespace.objects.Basket;
import com.vasciie.bkbl.gamespace.tools.GameTools;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteerableAdapter;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.ai.steer.behaviors.BlendedSteering;
import com.badlogic.gdx.ai.steer.behaviors.CollisionAvoidance;
import com.badlogic.gdx.ai.steer.behaviors.Interpose;
import com.badlogic.gdx.ai.steer.behaviors.LookWhereYouAreGoing;
import com.badlogic.gdx.ai.steer.behaviors.PrioritySteering;
import com.badlogic.gdx.ai.steer.behaviors.RaycastObstacleAvoidance;
import com.badlogic.gdx.ai.steer.behaviors.Separation;
import com.badlogic.gdx.ai.steer.utils.RayConfiguration;
import com.badlogic.gdx.ai.steer.utils.rays.ParallelSideRayConfiguration;
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
	Arrive<Vector3> pursue, pursueBallInHand, customPursue;
	LookWhereYouAreGoing<Vector3> lookAt;
	CollisionAvoidance<Vector3> collAvoid; //For players and basket stands
	Interpose<Vector3> interpose; //For blocking opponents from getting close to the player holding the ball in co-op state
	Separation<Vector3> ballSeparate, basketSeparate, playerSeparate, allPlayerSeparate; //For player and basket surroundings and ball distance keeping
	RaycastObstacleAvoidance<Vector3> obstAvoid; //For invisible user.getMap().getTerrain() walls
	PrioritySteering<Vector3> pSBallChasePart, pSCoop, pSSurround, pSCustom;
	BlendedSteering<Vector3> mSBallChase, mSBallInHand, mSSurround;	//Groups of behaviors for each state
	
	
	RayConfiguration<Vector3> rayConfig;
	
	public Brain(Player user) {
		this.user = user;
		
		stateMachine = new DefaultStateMachine<Player, PlayerState>(user, PlayerState.IDLING);
		memory = new AIMemory();
		
		pursue = new Arrive<Vector3>(user, user.getMap().getBall());
		pursue.setDecelerationRadius(user.getMap().getBall().getWidth() * 1.5f);
		
		pursueBallInHand = new Arrive<Vector3>(user, user.getTargetBasket());
		customPursue = new Arrive<Vector3>(user, null);
		customPursue.setArrivalTolerance(0);
		//pursue.setArrivalTolerance(0.1f);
		user.setMaxLinearAcceleration(1);
		lookAt = new LookWhereYouAreGoing<Vector3>(user);
		collAvoid = new CollisionAvoidance<Vector3>(user, user);
		interpose = new Interpose<Vector3>(user, null, null, 0.8f);
		ballSeparate = new Separation<Vector3>(user, user); //The differences between these behaviors are in the overrided findNeighbors void in the Player class
		basketSeparate = new Separation<Vector3>(user, user);//
		playerSeparate = new Separation<Vector3>(user, user);//
		allPlayerSeparate = new Separation<Vector3>(user, user);
		
		rayConfig = new ParallelSideRayConfiguration<Vector3>(user, 3, 0.5f);
		obstAvoid = new RaycastObstacleAvoidance<Vector3>(user, rayConfig, user.getMap());
		
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
		
		mSBallInHand = new BlendedSteering<Vector3>(user);
		mSBallInHand.add(collAvoid, 1.3f);
		mSBallInHand.add(basketSeparate, 0.6f);
		mSBallInHand.add(pursueBallInHand, 1.3f);
		
		pSCoop = new PrioritySteering<Vector3>(user);
		pSCoop.add(collAvoid);
		//mSCoop.add(pursue, 1);
		pSCoop.add(interpose);
		pSCoop.add(pursue);
		
		mSSurround = new BlendedSteering<Vector3>(user);
		mSSurround.add(playerSeparate, 1.3f);
		mSSurround.add(collAvoid, 1);
		mSSurround.add(pursue, 1.3f);
		
		pSSurround = new PrioritySteering<Vector3>(user);
		pSSurround.add(playerSeparate);
		pSSurround.add(collAvoid);
		pSSurround.add(pursue);
		
		pSCustom = new PrioritySteering<Vector3>(user);
		pSCustom.add(ballSeparate);
		pSCustom.add(allPlayerSeparate);
		pSCustom.add(collAvoid);
		pSCustom.add(customPursue);
		//mSCoop.add(playerSeparate, 0.9f);
	}
	
	public void update(boolean updateAI) {
		memory.setShootTime(memory.getShootTime() + Gdx.graphics.getDeltaTime());
		//System.out.println(memory.getShootTime());
		memory.setResetTime(memory.getResetTime() + Gdx.graphics.getDeltaTime());
		
		//The state switcher
		if(user.getMap().isGameRunning() && !user.getMap().isRuleBrokenActing()) {
			//If the current player is holding the ball
			if(user.isHoldingBall()) {
				if(!stateMachine.isInState(PlayerState.BALL_IN_HAND)) stateMachine.changeState(PlayerState.BALL_IN_HAND);
			
			}else {
				
				//If a teammate of the current player (either a teammate or an opponent to the main player) is holding the ball
				if(user instanceof Teammate && user.getMap().isBallInTeam() || user instanceof Opponent && user.getMap().isBallInOpp()) {
					if(!stateMachine.isInState(PlayerState.COOPERATIVE)) stateMachine.changeState(PlayerState.COOPERATIVE);
				}
				//If an opponent of the current player (either a teammate or an opponent to the main player) is holding the ball
				else if(user instanceof Teammate && user.getMap().isBallInOpp() || user instanceof Opponent && user.getMap().isBallInTeam()) {
					if(!stateMachine.isInState(PlayerState.PLAYER_SURROUND)) stateMachine.changeState(PlayerState.PLAYER_SURROUND);
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
	}
	
	public void performShooting(Vector3 tempAimVec) {
		memory.setTargetVec(tempAimVec);
		user.lookAt(tempAimVec, true);
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
	 * @param player - the shooting player
	 * @param targetVec - the target of the player
	 * @return
	 */
	private Vector3 calculateShootVector(Vector3 targetVec) {
		Vector3 distVec = targetVec.cpy().sub(user.getPosition()); //Distance between the target and the player
		
		float xzDist = distVec.cpy().scl(1, 0, 1).len(); //Calculate only xz distance. That's what we multiplied y by 0 for.
		float yDist = distVec.cpy().scl(0, 1, 0).len(); //Calculate only y distance. That's what we multiplied x and z by 0 for.
		
		Vector3 returnVec = targetVec.cpy().add(0, xzDist / 2 + yDist, 0);
		
		/*System.out.println(xzDist + "; " + yDist);
		System.out.print(player.getPosition());
		System.out.print(targetVec);
		System.out.println(returnVec);*/
		//Modify player shootPower
		
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
			else if (memory.getAimingTime() > maxShootingTime) {
				//System.out.println(memory.getShootVec());
				memory.setShootTime(0);
				memory.setCatchTime(0);
				memory.setAimingTime(0);
				memory.setTargetVec(null);
				memory.setShootVec(null);
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
		Vector3 dirVec = Vector3.Z.cpy();
		user.getModelInstance().transform.getRotation(new Quaternion()).transform(dirVec);
		
		avoidObstacles(user.getMap().getHomeBasket(), dirVec);
		avoidObstacles(user.getMap().getAwayBasket(), dirVec);
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
		Vector3 homeBasketPos = homeBasket.calcTransformFromNodesTransform(homeBasket.getMatrixes().get(3)).getTranslation(new Vector3());
		
		Basket awayBasket = user.getMap().getAwayBasket();
		Vector3 awayBasketPos = awayBasket.calcTransformFromNodesTransform(awayBasket.getMatrixes().get(3)).getTranslation(new Vector3());
		
		float checkConst = 0.001f;
		
		return targetVec.dst(homeBasketPos) <= checkConst || targetVec.dst(awayBasketPos) <= checkConst;
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
		
		memory.setTargetPosition(tempSteerable);
		customPursue.setTarget(tempSteerable);
		
		if(facing)
			memory.setTargetFacing(tempSteerable);
	}
	
	public void clearCustomTarget() {
		customPursue.setTarget(null);
		memory.setTargetPosition(null);
		memory.setTargetFacing(null);
	}
	
	public void clearCustomPS() {
		
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

	public Separation<Vector3> getAllPlayerSeparate() {
		return allPlayerSeparate;
	}

	public RaycastObstacleAvoidance<Vector3> getObstAvoid() {
		return obstAvoid;
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
	
	public PrioritySteering<Vector3> getPSCustom(){
		return pSCustom;
	}
	
}
