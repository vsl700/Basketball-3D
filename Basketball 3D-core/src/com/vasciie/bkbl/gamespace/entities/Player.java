package com.vasciie.bkbl.gamespace.entities;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationDesc;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationListener;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Array.ArrayIterator;
import com.vasciie.bkbl.gamespace.GameMap;
import com.vasciie.bkbl.gamespace.entities.players.Opponent;
import com.vasciie.bkbl.gamespace.entities.players.Teammate;
import com.vasciie.bkbl.gamespace.entities.players.ai.*;
import com.vasciie.bkbl.gamespace.objects.Basket;
import com.vasciie.bkbl.gamespace.tools.GameTools;
import com.vasciie.bkbl.gamespace.zones.Zones.Zone;

public abstract class Player extends Entity {

	static final float MAX_WALKING_VELOCITY = 4;
	static final float MAX_RUNNING_VELOCITY = 11;
	static final float MAX_SHOOTING_POWER = 12;
	static final float MIN_SHOOTING_POWER = 10;
	public static final float dribbleSpeed = 0.1f;
	//The node which should be followed by the camera
	Matrix4 camMatrix;
	
	//The recent player's movement (or linear acceleration in this mechanic)
	private final Vector3 moveVec = new Vector3();
	private final Vector3 prevMoveVec = new Vector3();
	
	//Collision objects maps for reaching an object just by calling its mostly used name
	HashMap<String, btRigidBody> bodiesMap;
	HashMap<String, btCollisionObject> collObjMap;
	
	//Animation controllers for each body part that has an animation
	AnimationController armLController;
	AnimationController armRController;
	AnimationController legLController;
	AnimationController legRController;
	AnimationController bodyController;
	
	//AI
	Brain brain;
	public static final SteeringAcceleration<Vector3> steering = new SteeringAcceleration<Vector3>(new Vector3()); //Not sure where exactly that should be - in the Brain class or in the Player one
	
	//Scaling properties
	public static final float scale1 = 0.5f;
	public static final float scale2 = 1;
	public static final float scale3 = 0.15f;
	public static final float scale4 = 0.23f;
	public static final float scale5 = 0.75f;
	public static final float scale6 = 0.35f;
	public static final float scale7 = 0.315f;
	public static final float handPercentage = 0.4f;
	private static final float dribbleDefaultTime = 0.1f;

	//Player's mechanics
	boolean walking, running, currentRunning, jumping;
	boolean leftHoldingBall, rightHoldingBall;
	boolean leftAimBall, rightAimBall, leftCurrentAim, rightCurrentAim;
	boolean leftThrowBall, rightThrowBall, readyBall;
	boolean leftPointBall, rightPointBall, leftCurrentPoint, rightCurrentPoint;
	boolean focus, avoidInterpose;
	boolean dribbleL, dribbleR;
	boolean ballColl, playerColl; 
	boolean leftHandBall, rightHandBall;
	boolean leftHandInWorld, rightHandInWorld;
	boolean downBody;
	//boolean updateBrain;
	
	int shootingPower = 10;
	//int cycleTimeout;
	
	float minRotateDegrees, maxRotateDegrees;
	
	float time, dribbleTimeOut;
	
	int playerIndex;
	
	int fouls, points;
	
	//Abilities
	boolean ableToRun;
	
	boolean isABot = true;
	
	@Override
	public void create(EntityType type, GameMap map, Vector3 pos) {
		super.create(type, map, pos);
		
		armLController = new AnimationController(modelInstance);
		armRController = new AnimationController(modelInstance);
		legLController = new AnimationController(modelInstance);
		legRController = new AnimationController(modelInstance);
		bodyController = new AnimationController(modelInstance);
		
		//Prepare the primary animations state of the player (staying on one place without moving) 
		animateArmL("stay");
		animateArmR("stay");
		
		stopLegsAnim();
		stopBodyAnim();
		
		minRotateDegrees = -1;
		maxRotateDegrees = -1;
		
		ableToRun = true;
		
		//if(!isMainPlayer())
		brain = new Brain(this);
		//boundRadius = 2;
		
		invTrans.set(modelInstance.transform);
			//stateMachine = new DefaultStateMachine<Player, PlayerState>(this, PlayerState.IDLING);
			//stateMachine.changeState(PlayerState.IDLING);
		
		

	}
	
	protected abstract Color getPlayerColor();
	
	protected void createModels(Vector3 pos) {
		super.createModels(pos);
		
		modelInstance = new ModelInstance(model, pos.add(0, getHeight(), 0));
		
		camMatrix = new Matrix4();
	}
	
	@Override
	protected void createCollisions() {
		Matrix4 tempHandL = modelInstance.getNode("handL").globalTransform;
		Matrix4 tempHandR = modelInstance.getNode("handR").globalTransform;
		
		Vector3 armVec = new Vector3(scale3 / 2, scale5 * (1 - handPercentage) / 2, scale4 / 2);
		Vector3 handVec = new Vector3(scale3 / 2, scale5 * handPercentage / 2, scale4 / 2).add(0.2f);
		
		collisionShapes.add(new btBoxShape(new Vector3(scale2 / 2, getHeight(), getDepth() / 2)));
		matrixes.add(modelInstance.transform);
		
		btCollisionShape shoulderShape = new btSphereShape(scale6 / 2);
		btCollisionShape armShape = new btBoxShape(armVec);
		btCollisionShape elbowShape = new btSphereShape(scale3 / 2);
		btCollisionShape handShape = new btBoxShape(handVec);
		
		collisionShapes.add(shoulderShape);
		matrixes.add(modelInstance.getNode("shoulderL").globalTransform);
		collisionShapes.add(armShape);
		matrixes.add(modelInstance.getNode("arm1L").globalTransform);
		collisionShapes.add(elbowShape);
		matrixes.add(modelInstance.getNode("elbowL").globalTransform);
		collisionShapes.add(armShape);
		matrixes.add(modelInstance.getNode("arm2L").globalTransform);
		collisionShapes.add(handShape);
		matrixes.add(tempHandL);
		
		collisionShapes.add(shoulderShape);
		matrixes.add(modelInstance.getNode("shoulderR").globalTransform);
		collisionShapes.add(armShape);
		matrixes.add(modelInstance.getNode("arm1R").globalTransform);
		collisionShapes.add(elbowShape);
		matrixes.add(modelInstance.getNode("elbowR").globalTransform);
		collisionShapes.add(armShape);
		matrixes.add(modelInstance.getNode("arm2R").globalTransform);
		collisionShapes.add(handShape);
		matrixes.add(tempHandR);
		
		
		Vector3 collHandVec = handVec.add(0.3f);
		invisCollShapes = new ArrayList<btCollisionShape>();
		invisCollShapes.add(new btBoxShape(collHandVec));
		invisCollShapes.add(new btBoxShape(collHandVec));
		invisCollShapes.add(new btBoxShape(new Vector3(scale2 / 2, getHeight(), getDepth() / 2)));
		
		matrixes.add(modelInstance.transform);
		matrixes.add(tempHandL);
		matrixes.add(tempHandR);
	}
	
	private void createCollisionObjects() {
		collisionObjects = new ArrayList<btCollisionObject>();
		collObjMap = new HashMap<String, btCollisionObject>();
		manualSetTransformsObj = new ArrayList<btCollisionObject>();
		
		btCollisionObject bodyObj = new btCollisionObject();
		bodyObj.setCollisionShape(invisCollShapes.get(2));
		collObjMap.put("bodyObj", bodyObj);
		collisionObjects.add(bodyObj);
		
		btCollisionObject handLObj = new btCollisionObject();
		handLObj.setCollisionShape(invisCollShapes.get(0));
		collObjMap.put("handL", handLObj);
		collisionObjects.add(handLObj);
		
		btCollisionObject handRObj = new btCollisionObject();
		handRObj.setCollisionShape(invisCollShapes.get(1));
		collObjMap.put("handR", handRObj);
		collisionObjects.add(handRObj);
	}
	
	private void removeCollisionCheckOnInternals() {
		ArrayList<btCollisionObject> tempObj = getAllCollObjects();
		
		for(int i = 0; i < tempObj.size() - 1; i++) {
			for(int j = i + 1; j < tempObj.size(); j++) {
				tempObj.get(i).setIgnoreCollisionCheck(tempObj.get(j), true);
			}
		}
	}
	
	@Override
	protected void createCollisionObjectAndBodies() {
		super.createCollisionObjectAndBodies();
		
		//modelInstance.transform = getMainBody().getWorldTransform();
		//getMainBody().setLinearFactor(new Vector3(1, 0, 1));
		//getMainBody().setAngularFactor(new Vector3(0, 1, 0));
		
		leftHandInWorld = true;
		rightHandInWorld = true;
		
		createCollisionObjects();
		
		removeCollisionCheckOnInternals();
		
		bodiesMap = new HashMap<String, btRigidBody>();
		
		int i = 0;
		
		bodiesMap.put("model", bodies.get(i++));
		
		bodiesMap.put("shoulderL", bodies.get(i++));
		bodiesMap.put("arm1L", bodies.get(i++));
		bodiesMap.put("elbowL", bodies.get(i++));
		bodiesMap.put("arm2L", bodies.get(i++));
		bodiesMap.put("handL", bodies.get(i++));
		
		bodiesMap.put("shoulderR", bodies.get(i++));
		bodiesMap.put("arm1R", bodies.get(i++));
		bodiesMap.put("elbowR", bodies.get(i++));
		bodiesMap.put("arm2R", bodies.get(i++));
		bodiesMap.put("handR", bodies.get(i));
		
		/*bodiesMap.get("shoulderL").setDeactivationTime(1);
		bodiesMap.get("arm1L").setDeactivationTime(1);
		bodiesMap.get("elbowL").setDeactivationTime(1);
		bodiesMap.get("arm2L").setDeactivationTime(1);
		bodiesMap.get("handL").setDeactivationTime(1);
		
		bodiesMap.get("shoulderR").setDeactivationTime(1);
		bodiesMap.get("arm1R").setDeactivationTime(1);
		bodiesMap.get("elbowR").setDeactivationTime(1);
		bodiesMap.get("arm2R").setDeactivationTime(1);
		bodiesMap.get("handR").setDeactivationTime(1);*/
		
	}
	
	@Override
	public void setCollisionTransform(boolean updateMain) {
		super.setCollisionTransform(updateMain);
		
		/*collObjMap.get("poleNObj").setWorldTransform(calcTransformFromNodesTransform(new Matrix4().set(new Vector3(0, poleScale / 2, getDepth() / 2 + poleScale / 2), new Quaternion())));
		collObjMap.get("poleSObj").setWorldTransform(calcTransformFromNodesTransform(new Matrix4().set(new Vector3(0, poleScale / 2, -getDepth() / 2 - poleScale / 2), new Quaternion())));
		collObjMap.get("poleEObj").setWorldTransform(calcTransformFromNodesTransform(new Matrix4().set(new Vector3(-poleScale, poleScale / 2, 0), new Quaternion())));//W and E sizes are multiplied by 2
		collObjMap.get("poleWObj").setWorldTransform(calcTransformFromNodesTransform(new Matrix4().set(new Vector3(poleScale, poleScale / 2, 0), new Quaternion())));*/
	}
	
	/**
	 * Turns the modelInstance around the y-axis
	 * @param y - the y-axis
	 */
	public void turnY(float y) {
		if(rotDifference) {
			invTrans.rotate(0, 1, 0, y);
		}else {
			invTrans.set(modelInstance.transform.rotate(0, 1, 0, y));
			//setCollisionTransform(true);
		}
	}
	
	/**
	 * Turn the mainNode (in this case the head) around the x-axis. The mainNode is also followed by the camera as well as the modelInstance
	 * @param x - the x-axis
	 */
	public void turnX(float x) {
		float pitch = camMatrix.getRotation(new Quaternion()).getPitch();
		
		if(Math.abs(pitch + x) <= 38) {
			camMatrix.rotate(1, 0, 0, x);
		}
	}
	
	public void walk(Vector3 dir) {
		if(dir.isZero())
			return;
		
		if(running && !isAiming() && !isShooting()) {
			run(dir);
			return;
		}
		
		dir.x *= MAX_WALKING_VELOCITY;
		dir.z *= MAX_WALKING_VELOCITY;
		dir.y = 0;
		
		modelInstance.transform.trn(dir);
		//setCollisionTransform(true);
		
		walking = true;
		
		//if(isMainPlayer())
		//prevMoveVec.set(moveVec);
		moveVec.set(dir);
	}
	
	public void run(Vector3 dir) {
		if(!ableToRun) {
			running = false;
			walk(dir);
			return;
		}
		
		if(dir.isZero()) {
			running = false;//In case we use the setRunning method which is used by the AI when the players are obligated to run
			return;
		}
		
		if (!isAiming() && !isShooting()) {
			float multiplier = MAX_RUNNING_VELOCITY;
			if(isHoldingBall() && !map.isRuleTriggeredActing())
				multiplier -= 3;
			
			dir.x *= multiplier;
			dir.z *= multiplier;
			dir.y = 0;

			modelInstance.transform.trn(dir);
			//setCollisionTransform(true);

			running = currentRunning = true;
			
			//if(isMainPlayer())
			//prevMoveVec.set(moveVec);
			moveVec.set(dir);
		}
		else walk(dir);
	}
	
	private boolean isUnableToInteractWithHands() {
		return map.getHoldingPlayer() != null && !map.getHoldingPlayer().equals(this) && (this instanceof Teammate && map.getTeammateHolding() != null || this instanceof Opponent && map.getOpponentHolding() != null);
	}
	
	/**
	 * When left mouse button is pressed (or button for left hand)
	 */
	public void interactWithBallL() {
		if(isUnableToInteractWithHands())
			return;
		
		if (!isShooting() && !isDribbling()) {
			if (leftHoldingBall || rightHoldingBall) {
				//System.out.println(dribbleL);
				if(isAbleToDribble())
					dribbleL = true;
			} else if (!map.getBall().isGrounded()) {
				if (!running)
					leftPointBall = true;

				if (leftHandBall)
					catchBall(true);
			} else if (ballColl)
				catchBall(true);
		}
	}
	
	public void interactWithBallR() {
		if(isUnableToInteractWithHands())
			return;
		
		if (!isShooting() && !isDribbling()) {
			if (rightHoldingBall || leftHoldingBall) {
				if(isAbleToDribble())
					dribbleR = true;
			} else if (!map.getBall().isGrounded()) {
				if (!running)
					rightPointBall = true;

				if (rightHandBall)
					catchBall(false);
			} else if (ballColl)
				catchBall(false);
		}
	}
	
	public void interactWithBallA() {
		if (!isHoldingBall()) {
			Ball tempBall = map.getBall();

			Vector3 ballVec = tempBall.getPosition();
			ArrayList<Vector3> handVecs = new ArrayList<Vector3>();
			handVecs.add(getShoulderLTrans().getTranslation(new Vector3()));
			handVecs.add(getShoulderRTrans().getTranslation(new Vector3()));

			Vector3 tempHandVec = GameTools.getShortestDistanceWVectors(ballVec, handVecs);

			if (tempHandVec.idt(handVecs.get(0)))
				interactWithBallL();
			else
				interactWithBallR();
		}else if(leftHoldingBall)
			interactWithBallL();
		else if(rightHoldingBall)
			interactWithBallR();
	}
	
	public void focus(boolean special) {
		focus(null, special);
	}
	
	public void focus(Array<Player> players, boolean special) {
		focus = true;
		avoidInterpose = special;
		ignored = players;
	}
	
	public void switchDribble() {
		if(leftHoldingBall)
			interactWithBallR();
		else if(rightHoldingBall)
			interactWithBallL();
	}
	
	public void shootPowerScroll(float value) {
		if(value > 0)
			shootingPower = (int) Math.min(MAX_SHOOTING_POWER, shootingPower + value);
		
		else if(value < 0)
			shootingPower = (int) Math.max(MIN_SHOOTING_POWER, shootingPower + value);
	}
	
	public void setShootPower(float value) {
		shootingPower = (int) Math.min(MAX_SHOOTING_POWER, Math.max(MIN_SHOOTING_POWER, value));
	}
	
	public void catchBall(boolean left) {
		if(isAbleToCatch() || isDribbling())
		if ((map.getCurrentPlayerHoldTeam() == -1 && map.getCurrentPlayerHoldOpp() == -1) || 
				(map.getTeammateHolding() != null && map.getTeammateHolding().equals(this) || map.getOpponentHolding() != null && map.getOpponentHolding().equals(this)) || 
				((map.getTeammateHolding() != null && !map.getTeammateHolding().equals(this) && map.getTeammateHolding().isDribbling()) || (map.getOpponentHolding() != null && !map.getOpponentHolding().equals(this) && map.getOpponentHolding().isDribbling()))) {
			if (left) {
				leftHoldingBall = true;

				if (leftHandInWorld)
					disableHandDynColl(left);
			} else {
				rightHoldingBall = true;

				if (rightHandInWorld)
					disableHandDynColl(left);
			}
			
			leftThrowBall = rightThrowBall = false;
			readyBall = false;

			if (!downBody)
				disableUpperBodyDynColl();

			map.getBall().getMainBody().setGravity(new Vector3());

			if(!this.equals(map.getHoldingPlayer()))
				map.setHoldingPlayer(this);
			
			brain.getMemory().setCatchBall(false);
			
			dribbleL = dribbleR = false;
			readyBall = false;
			time = 0;
			dribbleTimeOut = dribbleDefaultTime;
		}
	}
	
	private void enableHandDynColl(boolean left) {
		if(left) {
			/*map.addRigidBody(bodiesMap.get("shoulderL"), bodiesMap.get("shoulderL").getContactCallbackFlag(), bodiesMap.get("shoulderL").getContactCallbackFilter());
			map.addRigidBody(bodiesMap.get("arm1L"), bodiesMap.get("arm1L").getContactCallbackFlag(), bodiesMap.get("arm1L").getContactCallbackFilter());
			map.addRigidBody(bodiesMap.get("elbowL"), bodiesMap.get("elbowL").getContactCallbackFlag(), bodiesMap.get("elbowL").getContactCallbackFilter());
			map.addRigidBody(bodiesMap.get("arm2L"), bodiesMap.get("arm2L").getContactCallbackFlag(), bodiesMap.get("arm2L").getContactCallbackFilter());
			map.addRigidBody(bodiesMap.get("handL"), bodiesMap.get("handL").getContactCallbackFlag(), bodiesMap.get("handL").getContactCallbackFilter());*/
			
			bodiesMap.get("shoulderL").setIgnoreCollisionCheck(map.getBall().getMainBody(), false);
			bodiesMap.get("arm1L").setIgnoreCollisionCheck(map.getBall().getMainBody(), false);
			bodiesMap.get("elbowL").setIgnoreCollisionCheck(map.getBall().getMainBody(), false);
			bodiesMap.get("arm2L").setIgnoreCollisionCheck(map.getBall().getMainBody(), false);
			bodiesMap.get("handL").setIgnoreCollisionCheck(map.getBall().getMainBody(), false);
			
			/*bodiesMap.get("shoulderL").activate(true);
			bodiesMap.get("arm1L").activate(true);
			bodiesMap.get("elbowL").activate(true);
			bodiesMap.get("arm2L").activate(true);
			bodiesMap.get("handL").activate(true);*/
			
			leftHandInWorld = true;
		}else {
			/*map.addRigidBody(bodiesMap.get("shoulderR"), bodiesMap.get("shoulderR").getContactCallbackFlag(), bodiesMap.get("shoulderR").getContactCallbackFilter());
			map.addRigidBody(bodiesMap.get("arm1R"), bodiesMap.get("arm1R").getContactCallbackFlag(), bodiesMap.get("arm1R").getContactCallbackFilter());
			map.addRigidBody(bodiesMap.get("elbowR"), bodiesMap.get("elbowR").getContactCallbackFlag(), bodiesMap.get("elbowR").getContactCallbackFilter());
			map.addRigidBody(bodiesMap.get("arm2R"), bodiesMap.get("arm2R").getContactCallbackFlag(), bodiesMap.get("arm2R").getContactCallbackFilter());
			map.addRigidBody(bodiesMap.get("handR"), bodiesMap.get("handR").getContactCallbackFlag(), bodiesMap.get("handR").getContactCallbackFilter());*/
			
			bodiesMap.get("shoulderR").setIgnoreCollisionCheck(map.getBall().getMainBody(), false);
			bodiesMap.get("arm1R").setIgnoreCollisionCheck(map.getBall().getMainBody(), false);
			bodiesMap.get("elbowR").setIgnoreCollisionCheck(map.getBall().getMainBody(), false);
			bodiesMap.get("arm2R").setIgnoreCollisionCheck(map.getBall().getMainBody(), false);
			bodiesMap.get("handR").setIgnoreCollisionCheck(map.getBall().getMainBody(), false);
			
			/*bodiesMap.get("shoulderR").activate(true);
			bodiesMap.get("arm1R").activate(true);
			bodiesMap.get("elbowR").activate(true);
			bodiesMap.get("arm2R").activate(true);
			bodiesMap.get("handR").activate(true);*/
			
			rightHandInWorld = true;
		}
	}
	
	/**
	 * disables the dynamic collisions for one of the player's hands
	 * @param left - if true, left hand dynamic collision will be stopped, otherwise - right hand collisions
	 */
	private void disableHandDynColl(boolean left) {
		if(left) {
			/*map.removeRigidBody(bodiesMap.get("shoulderL"));
			map.removeRigidBody(bodiesMap.get("arm1L"));
			map.removeRigidBody(bodiesMap.get("elbowL"));
			map.removeRigidBody(bodiesMap.get("arm2L"));
			map.removeRigidBody(bodiesMap.get("handL"));*/
			
			bodiesMap.get("shoulderL").setIgnoreCollisionCheck(map.getBall().getMainBody(), true);
			bodiesMap.get("arm1L").setIgnoreCollisionCheck(map.getBall().getMainBody(), true);
			bodiesMap.get("elbowL").setIgnoreCollisionCheck(map.getBall().getMainBody(), true);
			bodiesMap.get("arm2L").setIgnoreCollisionCheck(map.getBall().getMainBody(), true);
			bodiesMap.get("handL").setIgnoreCollisionCheck(map.getBall().getMainBody(), true);
			
			/*bodiesMap.get("shoulderL").updateDeactivation(1);
			bodiesMap.get("arm1L").updateDeactivation(1);
			bodiesMap.get("elbowL").updateDeactivation(1);
			bodiesMap.get("arm2L").updateDeactivation(1);
			bodiesMap.get("handL").updateDeactivation(1);*/
			
			leftHandInWorld = false;
		}else {
			/*map.removeRigidBody(bodiesMap.get("shoulderR"));
			map.removeRigidBody(bodiesMap.get("arm1R"));
			map.removeRigidBody(bodiesMap.get("elbowR"));
			map.removeRigidBody(bodiesMap.get("arm2R"));
			map.removeRigidBody(bodiesMap.get("handR"));*/
			
			bodiesMap.get("shoulderR").setIgnoreCollisionCheck(map.getBall().getMainBody(), true);
			bodiesMap.get("arm1R").setIgnoreCollisionCheck(map.getBall().getMainBody(), true);
			bodiesMap.get("elbowR").setIgnoreCollisionCheck(map.getBall().getMainBody(), true);
			bodiesMap.get("arm2R").setIgnoreCollisionCheck(map.getBall().getMainBody(), true);
			bodiesMap.get("handR").setIgnoreCollisionCheck(map.getBall().getMainBody(), true);
			
			/*bodiesMap.get("shoulderR").updateDeactivation(1);
			bodiesMap.get("arm1R").updateDeactivation(1);
			bodiesMap.get("elbowR").updateDeactivation(1);
			bodiesMap.get("arm2R").updateDeactivation(1);
			bodiesMap.get("handR").updateDeactivation(1);*/
			
			rightHandInWorld = false;
		}
	}
	
	private void disableUpperBodyDynColl() {
		map.getBall().getMainBody().setIgnoreCollisionCheck(getMainBody(), true);
		
		downBody = true;
	}
	
	private void enableUpperBodyDynColl() {
		map.getBall().getMainBody().setIgnoreCollisionCheck(getMainBody(), false);
		
		downBody = false;
	}
	
	/**
	 * When the button for shooting is pressed
	 */
	public void interactWithBallS() {
		if(leftHoldingBall) {
			leftAimBall = true;
		}
		else if(rightHoldingBall) {
			rightAimBall = true;
		}
	}
	
	private void stopBodyAnim() {
		bodyController.setAnimation("idle", -1);
	}
	
	private void stopLegsAnim() {
		legLController.setAnimation("idle", -1);
		
		legRController.setAnimation("idle", -1);
	}
	
	/**
	 * A shortcut to the animating methods
	 * @param id - the primary name of the animation (extensions like "ArmL", "LegR", etc. will be added automatically by the original methods)
	 * @param left - whether the animation should be applied to the left hand or the right hand
	 */
	private void animateArm(String id, boolean left) {
		if(left)
			animateArmL(id);
		else animateArmR(id);
	}
	
	private void animateArmL(String id) {
		armLController.setAnimation(id + "ArmL", -1);
	}
	
	private void animateArmR(String id) {
		armRController.setAnimation(id + "ArmR", -1);
	}

	private void animateLegL(String id) {
		legLController.setAnimation(id + "LegL", -1);
	}
	
	private void animateLegR(String id) {
		legRController.setAnimation(id + "LegR", -1);
	}
	
	private void animateBody(String id) {
		bodyController.setAnimation(id + "Body", -1);
	}
	
	private static final Vector3 targetDir = new Vector3();
	private static float thrownTime;
	private void throwBall() {
		Vector3 shootVec = brain.getMemory().getShootVec();
		if(shootVec != null) {
			throwBall(shootVec);
			return;
		}
		
		Quaternion dir = modelInstance.transform.getRotation(new Quaternion());
		Vector3 tempVec = new Vector3(0, 0, 1);
		tempVec.rotate(dir.getYaw(), 0, 1, 0);
		
		
		tempVec.y = (-camMatrix.getRotation(new Quaternion()).getPitch() / 100) * 2;
		
		float tempShootPower;
		if(!isABot())
			tempShootPower = shootingPower - 1;
		else tempShootPower = shootingPower;
		
		tempVec.scl(tempShootPower, tempShootPower * 1.4f, tempShootPower);
		
		if(leftThrowBall) {
			releaseBall(bodiesMap.get("handL").getWorldTransform().cpy());
			
			leftThrowBall = false;
		}
		else{
			releaseBall(bodiesMap.get("handR").getWorldTransform().cpy());
			
			rightThrowBall = false;
		}
		
		map.getBall().getMainBody().setLinearVelocity(tempVec);
		targetDir.set(tempVec);
		targetDir.y = (float) Math.floor(targetDir.y);
		thrownTime = 0.2f;
	}
	
	/**
	 * Used by the AI
	 * @param target
	 */
	public void throwBall(Vector3 target) {
		brain.getMemory().setShootTime(0);
		brain.getMemory().setCatchTime(0);
		brain.getMemory().setAimingTime(0);
		
		Vector3 tempVec = target.cpy().sub(map.getBall().getPosition()).nor();
		tempVec.x *= shootingPower;
		tempVec.y *= shootingPower * 1.4f;
		tempVec.z *= shootingPower;
		
		if(leftThrowBall) {
			releaseBall(bodiesMap.get("handL").getWorldTransform().cpy());
			
			leftThrowBall = false;
		}
		else{
			releaseBall(bodiesMap.get("handR").getWorldTransform().cpy());
			
			rightThrowBall = false;
		}
		
		map.getBall().getMainBody().setLinearVelocity(tempVec);
		targetDir.set(tempVec);
		targetDir.y = (float) Math.floor(targetDir.y);
		thrownTime = 0.2f;
	}
	
	public void releaseBall() {
		if(leftHoldingBall)
			releaseBall(bodiesMap.get("handL").getWorldTransform().cpy());
		else if(rightHoldingBall) releaseBall(bodiesMap.get("handR").getWorldTransform().cpy());
		
		leftHandInWorld = true;
		rightHandInWorld = true;
		downBody = false;
	}
	
	private void releaseBall(Matrix4 trans) {
		map.getBall().getMainBody().setGravity(map.getDynamicsWorld().getGravity());
		map.getBall().getMainBody().activate();
		map.getBall().setCollisionTransform(true);
		map.getBall().setWorldTransform(trans);
		
		leftHoldingBall = rightHoldingBall = false;
		
		if(!isDribbling())
			map.playerReleaseBall();
		
		enableUpperBodyDynColl();
		
		if (!isDribbling()) {
			if(!isShooting()) {
				enableHandDynColl(true);
				enableHandDynColl(false);
			}
			
			shootingPower = 10;
		}
	}
	
	private Vector3 makeBallDribbleVelocity(boolean left) {
		Vector3 tempVec = new Vector3(0, 0, -1);
		modelInstance.transform.getRotation(new Quaternion()).transform(tempVec);
		if(left)
			tempVec.rotate(-90, 0, 1, 0);
		else tempVec.rotate(90, 0, 1, 0);
		
		tempVec.scl(3);
		
		return tempVec;
	}
	
	/**
	 * The transform of the player in the previous frame. Used for dribble hand switching when the ball has to follow the player and also dynamically follow its other hand.
	 */
	private final Matrix4 prevTrans = new Matrix4();
	
	/**
	 * This method helps modifying the dribble function for both of the hands at the same time
	 * @param left - true for left dribble command, false for right
	 */
	private void dribble(float delta, final boolean left) {
		final String primaryId, oppositeId;
		final AnimationController primary, opposite;
		
		final boolean handBall;
		final boolean holdingBall, oppositeHolding;
		
		if(left) {
			primaryId = "L";
			oppositeId = "R";
			
			primary = armLController;
			opposite = armRController;
			
			handBall = leftHandBall;
			
			holdingBall = leftHoldingBall;
			oppositeHolding = rightHoldingBall;
		}else {
			primaryId = "R";
			oppositeId = "L";
			
			primary = armRController;
			opposite = armLController;
			
			handBall = rightHandBall;
			
			holdingBall = rightHoldingBall;
			oppositeHolding = leftHoldingBall;
		}
		
		
		if(oppositeHolding) {
			if(!opposite.current.animation.id.equals("dribblePhase1Arm" + oppositeId)) {
				opposite.setAnimation("dribblePhase1Arm" + oppositeId, 1, new AnimationListener() {

					@Override
					public void onEnd(AnimationDesc animation) {
						releaseBall(bodiesMap.get("hand" + oppositeId).getWorldTransform().cpy());

						map.getBall().getMainBody().setLinearVelocity(makeBallDribbleVelocity(left).add(0, -10, 0));
						disableHandDynColl(left);
						enableHandDynColl(!left);
						//animateArmL("stayL");
						animateArm("stay", !left);
					}

					@Override
					public void onLoop(AnimationDesc animation) {
						
					}
					
				});
				
				primary.animate("dribbleIdle1Arm" + primaryId, dribbleSpeed);
				primary.setAnimation("dribbleIdle1Arm" + primaryId, 1);
				
				
			}
			else map.getBall().setWorldTransform(bodiesMap.get("hand" + oppositeId).getWorldTransform().cpy());
		}
		else if(!primary.current.animation.id.equals("dribblePhase1Arm" + primaryId) && !primary.current.animation.id.equals("dribbleIdle1Arm" + primaryId)) {
			primary.setAnimation("dribblePhase1Arm" + primaryId, 1, new AnimationListener() {

				@Override
				public void onEnd(AnimationDesc animation) {
					releaseBall(bodiesMap.get("hand" + primaryId).getWorldTransform().cpy());
					
					//disableHandDynColl(true);
					
					map.getBall().getMainBody().setLinearVelocity(new Vector3(0, -10, 0));
					
				}

				@Override
				public void onLoop(AnimationDesc animation) {
					
				}
				
			});
		}
		else {
			if (!holdingBall) {
				if (handBall && readyBall) { //If the ball is being touched by the player's hand after some time (to make sure it will jump to the ground) it should be caught and the dribble should be over.
					if (!primary.current.animation.id.equals("dribblePhase2Arm" + primaryId)) {
						primary.setAnimation("dribblePhase2Arm" + primaryId, 1, new AnimationListener() {

							@Override
							public void onEnd(AnimationDesc animation) {
								// armLController.animate("dribbleIdleArmL",
								// 0.15f);
								animateArm("dribbleIdle", left);
							}

							@Override
							public void onLoop(AnimationDesc animation) {

							}

						});

						catchBall(left);
						dribbleL = false;
						dribbleR = false;
						readyBall = false;
						time = 0;
						dribbleTimeOut = dribbleDefaultTime;
					}

				} else {
					//The current translation of the ball
					Matrix4 ballTrans = map.getBall().getMainBody().getWorldTransform();
					Vector3 tempBallVec = new Vector3();
					ballTrans.getTranslation(tempBallVec);
					
					if(primary.current.animation.id.equals("dribbleIdle1Arm" + primaryId)) {
						//The player's previous translation
						Vector3 tempPlayerVec = new Vector3();
						prevTrans.getTranslation(tempPlayerVec);
						
						//The difference between the player's previous translation (we use the ball for that)
						Vector3 ballSubPlayer = tempBallVec.cpy().sub(tempPlayerVec);
						//Getting the player's previous rotation
						Quaternion tempVecRot = new Quaternion();
						prevTrans.getRotation(tempVecRot);
						//Rotating the difference vector so that we get the difference when the player's y-rotation is zero and multiply it with the matrix without getting different mul results
						ballSubPlayer.rotate(-tempVecRot.getYaw(), 0, 1, 0);
						
						//Creating the new ball matrix
						Matrix4 newBallTrans = modelInstance.transform.cpy().mul(new Matrix4().set(ballSubPlayer, ballTrans.getRotation(new Quaternion())));
						
						//Finally setting the world transform of the ball
						map.getBall().setWorldTransform(new Matrix4(newBallTrans));
						
						
						//Setting the ball's velocity
						//float yVel = map.getBall().getMainBody().getLinearVelocity().y;
						Vector3 tempVelVec = makeBallDribbleVelocity(left);
						tempVelVec.y = map.getBall().getMainBody().getLinearVelocity().y;
						
						map.getBall().getMainBody().setLinearVelocity(tempVelVec);
						
					}else {
						//Matrix4 temp = map.getBall().getMainBody().getWorldTransform();
						//Vector3 tempVec = new Vector3();
						//temp.getTranslation(tempVec);

						
						//Vector3 tempVec1 = new Vector3();
						Vector3 tempHandVec = new Vector3();
						Matrix4 tempHand = bodiesMap.get("hand" + primaryId).getWorldTransform().cpy();
						tempHand.getTranslation(tempHandVec);
						
						map.getBall().setWorldTransform(new Matrix4().set(new Vector3(tempHandVec.x, tempBallVec.y, tempHandVec.z), map.getBall().getMainBody().getWorldTransform().getRotation(new Quaternion())));
					}
					//System.out.println(map.getBall().getMainBody().getLinearVelocity().y);
					int difficulty = map.getDifficulty();
					if(time > 0.85f) {
						dribbleL = false;
						dribbleR = false;
						readyBall = false;
						time = 0;
					}
					else if (!readyBall && (time > 0.15f && difficulty > 0 || time > 0.4f)) {
						readyBall = true;
					} else {
						time += delta;
					}
				}
			}
			else
				map.getBall().setWorldTransform(bodiesMap.get("hand" + primaryId).getWorldTransform().cpy());
		}
		
	}
	
	private void updateHoldingHand(final boolean left) {
		final AnimationController primaryArmController;
		AnimationController secondaryArmController;
		
		String primaryId, secondaryId;
		
		boolean aimBall;
		
		if(left) {
			primaryArmController = armLController;
			secondaryArmController = armRController;
			primaryId = "L";
			secondaryId = "R";
			aimBall = leftCurrentAim = leftAimBall;
		}else {
			primaryArmController = armRController;
			secondaryArmController = armLController;
			primaryId = "R";
			secondaryId = "L";
			aimBall = rightCurrentAim = rightAimBall;
		}
		
		map.getBall().setWorldTransform(bodiesMap.get("hand" + primaryId).getWorldTransform().cpy());
		
		if(leftThrowBall || rightThrowBall)
			return;
		
		if(focus)
			lookAtClosestToViewPlayer();
		
		if(aimBall) {
			float transistion = 0.25f;
			if (!primaryArmController.current.animation.id.equals("aim" + primaryId + "Arm" + primaryId)) {
				primaryArmController.animate("aim" + primaryId + "Arm" + primaryId, transistion);
				primaryArmController.setAnimation("aim" + primaryId + "Arm" + primaryId, -1, new AnimationListener() {

					@Override
					public void onEnd(AnimationDesc animation) {}

					@Override
					public void onLoop(AnimationDesc animation) {
						if (primaryArmController.transitionCurrentTime >= primaryArmController.transitionTargetTime)
							readyBall = true;
					}

				});
			}
			
			if (!secondaryArmController.current.animation.id.equals("aim" + primaryId + "Arm" + secondaryId)) {
				secondaryArmController.animate("aim" + primaryId + "Arm" + secondaryId, transistion);
				if(left)
					animateArmR("aim" + primaryId);
				else animateArmL("aim" + primaryId);
			}

			if (!bodyController.current.animation.id.equals("aim" + primaryId + "Body")) {
				bodyController.animate("aim" + primaryId + "Body", transistion);
				animateBody("aim" + primaryId);
			}
		}else if (readyBall) {
			if(left)
				leftThrowBall = true;
			else rightThrowBall = true;
			readyBall = false;

			primaryArmController.animate("throw" + primaryId + "Arm" + primaryId, 0.25f);
			primaryArmController.setAnimation("throw" + primaryId + "Arm" + primaryId, 1, new AnimationListener() {

				@Override
				public void onEnd(AnimationDesc animation) {
					throwBall();

					if(left) {
						//leftThrowBall = false;
						leftHoldingBall = false;
					}else {
						//rightThrowBall = false;
						rightHoldingBall = false;
					}
				}

				@Override
				public void onLoop(AnimationDesc animation) {
				}

			});

			secondaryArmController.animate("throw" + primaryId + "Arm" + secondaryId, 0.25f);
			secondaryArmController.setAnimation("throw" + primaryId + "Arm" + secondaryId, 1);

			bodyController.animate("throw" + primaryId + "Body", 0.25f);
			bodyController.setAnimation("throw" + primaryId + "Body", 1);
		}

		else if ((!leftThrowBall && !rightThrowBall) && !primaryArmController.current.animation.id.equals("dribbleIdleArm" + primaryId) && !primaryArmController.current.animation.id.equals("aim" + primaryId + "Arm" + primaryId)) {
			primaryArmController.animate("dribbleIdleArm" + primaryId, 0.15f);
			if(left)
				animateArmL("dribbleIdle");
			else animateArmR("dribbleIdle");
		}

	}
	
	final Quaternion tempHandRot = new Quaternion();
 	private void point(final boolean left) {
		AnimationController primary;
		
		String id;
		
		if(left) {
			primary = armLController;
			
			id = "L";
		}
		else {
			primary = armRController;
			
			id = "R";
		}
		
		if(!primary.current.animation.id.equals("idle"))
			primary.setAnimation("idle", -1);
		
		Matrix4 tempBall = map.getBall().getMainBody().getWorldTransform();
		Vector3 tempBallTrans = new Vector3();
		tempBall.getTranslation(tempBallTrans);
		
		Matrix4 tempHand = calcTransformFromNodesTransform(modelInstance.getNode("shoulder" + id).globalTransform);
		Vector3 tempHandVec = new Vector3();
		tempHand.getTranslation(tempHandVec);
		
		//Vector3 tempHandRot = new Vector3(0, 0, -1);
		//modelInstance.transform.getRotation(new Quaternion()).transform(tempHandRot);
		
		Vector3 newHandRotVec = tempBallTrans.cpy().sub(tempHandVec).unrotate(modelInstance.transform).nor().add(new Vector3(0, -1, 0));
		
		//System.out.print(newHandRotVec.x + "; ");
		//if(newHandRotVec.z > 0)
			//newHandRotVec.z = Math.min(1, newHandRotVec.z);
		//else newHandRotVec.z = Math.min(-1, newHandRotVec.z);
		
		
		//System.out.println(newHandRotVec.z);
		
		tempHandRot.setFromAxis(newHandRotVec, 180).setEulerAngles(tempHandRot.getYaw(), Math.max(0, tempHandRot.getPitch()), tempHandRot.getRoll());
		//System.out.println(Math.min(0, newHandRot.getRoll()));
		
		
		
		if(left)
			leftCurrentPoint = true;
		else rightCurrentPoint = true;
		
		
		//setCollisionTransform();
	}
	
	/**
	 * Gets the distance between this player and the target vector
	 * @param target
	 * @return
	 */
	/*private Vector3 distance(Vector3 target) {
		return target.cpy().sub(modelInstance.transform.getTranslation(new Vector3()));
	}*/
	
	/**
	 * Points the player's view (rotation) at a target
	 * @param target - the target this player should point at
	 */
	public void lookAt(Vector3 target, boolean rotateHead) {
		Vector3 rotVec = target.cpy().sub(getPosition());
		
		//Calculated lookAt transform
		Matrix4 calcTrans = new Matrix4().setToLookAt(rotVec, new Vector3(0, -1, 0));
		
		Quaternion quat = new Quaternion();
		calcTrans.getRotation(quat);
		quat.setEulerAngles(quat.getYaw(), 0, 0);
		
		modelInstance.transform.set(getPosition(), quat).rotate(0, 1, 0, 180);
		
		if(!rotateHead)
			return;
			
		Vector3 camRotVec = target.cpy().sub(calcTransformFromNodesTransform(camMatrix).getTranslation(new Vector3()));
		Matrix4 camTrans = new Matrix4().setToLookAt(camRotVec, new Vector3(0, -1, 0));
		
		Quaternion quat2 = new Quaternion();
		camTrans.getRotation(quat2);
		float degrees, limit = 15;
		if(quat2.getPitch() > limit)
			degrees = limit;
		else if(quat2.getPitch() < -limit)
			degrees = -limit;
		else degrees = quat2.getPitch();
		
		quat2.setEulerAngles(0, degrees, 0);
		
		Vector3 camVec = camMatrix.getTranslation(new Vector3());
		camMatrix.set(camVec, quat2);
		
		//setCollisionTransform();
	}
	
	private final Matrix4 invTrans = new Matrix4();
	private Player focusTarget;
	private boolean rotDifference;
	private Array<Player> ignored;
	private void lookAtClosestToViewPlayer() {
		ArrayList<Player> tempPlayers;
		
		if(this instanceof Teammate)
			tempPlayers = map.getTeammates();
		else
			tempPlayers = map.getOpponents();
		
		if(tempPlayers.size() == 1)
			return;
		
		
		Vector3 direction = Vector3.Z.cpy();
		Quaternion currentRot = invTrans.setTranslation(new Vector3()).getRotation(new Quaternion());
		currentRot.transform(direction).nor().y = 0;
		
		Player prevTarget = focusTarget;
		Player startingPlayer = null;
		
		if(tempPlayers.size() > 2)
		for(Player p : tempPlayers) {
			if(!p.equals(this) && (ignored != null && !ignored.contains(p, false) || ignored == null))
				startingPlayer = focusTarget = p;
		}
		
		if (startingPlayer == null) {
			if (!tempPlayers.get(0).equals(this))
				startingPlayer = focusTarget = tempPlayers.get(0);
			else
				startingPlayer = focusTarget = tempPlayers.get(1);
		}
		
		Vector3 closestPos = startingPlayer.getPosition();
		Vector3 tempClosestDir = closestPos.cpy().sub(getPosition()).nor();
		tempClosestDir.y = 0;
		float minDist = direction.dst2(tempClosestDir);
		
		Player tempTarget = startingPlayer;//Used to store the closest player while there's still not found any unblocked player (if any at all)
		boolean change = avoidInterpose || ignored != null && ignored.contains(tempTarget, false);//If change is true, the system below will keep changing the player no matter he is being blocked or not. Otherwise, it checks for blockings
		for(Player p : tempPlayers) {//The players this player should choose from for pointing at
			if(p.equals(this) || p.equals(startingPlayer) && !avoidInterpose || ignored != null && ignored.contains(p, false))
				continue;
			
			Vector3 tempPos = p.getPosition();
			Vector3 tempDir = tempPos.cpy().sub(getPosition()).nor();
			tempDir.y = 0;
			float dist = direction.dst2(tempDir);
			
			boolean closer = dist < minDist;
			if(closer || change) {
				if(avoidInterpose) {//Usually AI would use this
					boolean block = false;//Whether the chosen player gets blocked
					ArrayList<Location<Vector3>> locations = new ArrayList<Location<Vector3>>();
					locations.addAll(map.getAllPlayers());
					locations.add(map.getHomeBasket());
					locations.add(map.getAwayBasket());
					
					for(Location<Vector3> p1 : locations) {//The players that may block the chosen player 
						if(p1.equals(this) || p1.equals(p))
							continue;
						
						Vector3 tempPos1 = p1.getPosition();
						Vector3 tempDir1 = tempPos1.cpy().sub(getPosition()).nor();
						tempDir1.y = 0;
						
						float dirDist = tempDir.dst(tempDir1);
						float checkConst = getWidth() / dist * 3;
						if(dirDist <= checkConst) {
							float posDist = tempPos1.dst2(getPosition()) - tempPos.dst2(getPosition());
							if(posDist < 0) { //If the eventual blocker (tempPos1) is in front of the chosen one (tempPos)
								block = true;
								break;
							}
						}
					}
					
					if(block) {
						if (change && closer) {
							tempTarget = p;
							closestPos = tempPos;
							minDist = dist;
						}
						
						continue;
					}else if (closer || change) {
						change = false;
						closestPos = tempPos;
						minDist = dist;
						focusTarget = p;
					}
				} else {
					closestPos = tempPos;
					minDist = dist;
					focusTarget = p;
				}
			}
		}
		
		if(change)//If there's no unblocked players
			lookAt(tempTarget.getPosition(), false);
		else
			lookAt(closestPos, false);
		
		setCollisionTransform(true);
		
		if(!rotDifference || !focusTarget.equals(prevTarget)/* || change && !tempTarget.equals(prevTarget)*/) {
			invTrans.set(modelInstance.transform);
			rotDifference = true;
		}
	}
	
	public void updateAnimations(float delta) {
		armLController.update(delta);
		armRController.update(delta);
		legLController.update(delta);
		legRController.update(delta);
		bodyController.update(delta);
		
		if(leftCurrentPoint) {
			Matrix4 tempLocal = modelInstance.getNode("shoulderL").localTransform;
			modelInstance.getNode("shoulderL").isAnimated = true;
			
			tempLocal.set(tempLocal.getTranslation(new Vector3()), tempHandRot).rotate(0, 1, 0, 180);
			modelInstance.calculateTransforms();
		}else if(rightCurrentPoint) {
			Matrix4 tempLocal = modelInstance.getNode("shoulderR").localTransform;
			modelInstance.getNode("shoulderR").isAnimated = true;
			
			tempLocal.set(tempLocal.getTranslation(new Vector3()), tempHandRot).rotate(0, 1, 0, 180);
			modelInstance.calculateTransforms();
		}
	}
	
	@Override
	public void update(float delta) {
		if(dribbleTimeOut > 0)
			dribbleTimeOut -= delta;
		
		if(!isHoldingBall() && !isShooting()) {
			readyBall = false;
			dribbleL = dribbleR = false;
			leftHoldingBall = rightHoldingBall = false;
			map.getBall().getMainBody().setGravity(map.getDynamicsWorld().getGravity());
		}
		
		else if(!leftHoldingBall && !rightHoldingBall && !isDribbling()) {
			map.playerReleaseBall();
			map.getBall().getMainBody().setGravity(map.getDynamicsWorld().getGravity());
		}
		
		if(!leftHoldingBall && !rightHoldingBall || !leftAimBall && !rightAimBall) {
			leftCurrentAim = rightCurrentAim = false;
			
			if(map.getHoldingPlayer() != null && !map.getHoldingPlayer().equals(this) || map.isTutorialMode() || map.getMultiplayer().isMultiplayer())
				thrownTime = 0;
			else if(thrownTime > 0) {
				map.getBall().getMainBody().setLinearVelocity(targetDir);
				thrownTime -= delta;
			}
			//leftThrowBall = rightThrowBall = false;
		}
		
		
		
		boolean mainPlayerBrainUpdate = isMainPlayer() && map.isRuleTriggeredActing() && !map.isGameRunning();
		if(mainPlayerBrainUpdate /*updateBrain && map.isRuleBrokenActing()*/ || isABot && !isMainPlayer() && !map.isRuleTriggered()) {
			brain.update(true);
			//Vector3 tempVec = moveVec.add(new Vector3(steering.linear.cpy().x, 0, steering.linear.cpy().y)).scl(0.5f);
			//float tempAng = steering.angular;
			moveVec.y = 0;
			//prevMoveVec.set(moveVec);
			
			moveVec.nor().scl(Math.min(1, Gdx.graphics.getDeltaTime()));
			//System.out.println(moveVec.x + " ; " + moveVec.y + " ; " + moveVec.z);
			//float len = Math.abs(moveVec.x) + Math.abs(moveVec.z);
			//System.out.println(len);
			//if(len >= 0.01f)
				walk(moveVec);
				//setCollisionTransform(true);
				/*System.out.println(getPosition() + "pos after update");
				System.out.println();*/
				
			//else {
				//running = false;
			//}
			//System.out.println(getWidth() * getDepth());
		}else if(isMainPlayer() && !map.isRuleTriggered()) {
			brain.update(false);//It's important that we control the state machine
			
			//updateBrain = false;
		}
		
		prevMoveVec.set(moveVec);
		
		if(!ableToRun)
			running = false;
		
		if(running == false)
			currentRunning = false;
		
		lockRotationAndRandomFloating(true);
		
		String prevIdArmL = armLController.current.animation.id;
		String prevIdArmR = armRController.current.animation.id;
		
		//if(Math.abs(getMainBody().getLinearVelocity().x) < 0.0008f && Math.abs(getMainBody().getLinearVelocity().z) < 0.0008f) {
			//walking = running = false;
		//}
		
		if(isHoldingBall())
			brain.getMemory().setCatchBall(false);
		
		if (dribbleL) {
			dribble(delta, true);
		} else if (dribbleR) {
			dribble(delta, false);
		}
		else if (leftHoldingBall) {
			updateHoldingHand(true);
		}
		
		else if(rightHoldingBall) {
			updateHoldingHand(false);
		}
		
		else if (!ballColl) {
			if (!leftHandInWorld) {
				if (time > 0.5f) {
					enableHandDynColl(true);
					enableUpperBodyDynColl();
					
					time = 0;
				}
				else
					time+= delta;
			}

			else if (!rightHandInWorld) {
				if (time > 0.5f) {
					enableHandDynColl(false);
					enableUpperBodyDynColl();
					
					time = 0;
				}
				else
					time+= delta;
			}
			readyBall = false;
		}
		else if(!leftHandInWorld || !rightHandInWorld)
			time = 0;
		
		if((!focus || !isHoldingBall()) && rotDifference && !mainPlayerBrainUpdate) {
			invTrans.set(modelInstance.transform);
			rotDifference = false;
			focusTarget = null;
		}else if(mainPlayerBrainUpdate) rotDifference = true;
		
		
		if(leftPointBall)
			point(true);
		else if(rightPointBall)
			point(false);
		else leftCurrentPoint = rightCurrentPoint = false;
		
		
		float prevTime = armLController.current.time;
		if (walking && !moveVec.isZero()) {
			if (!leftThrowBall && !rightThrowBall && !armLController.current.animation.id.equals("aimLArmL") && !armLController.current.animation.id.equals("aimRArmL")) {
				if (!leftHoldingBall && !dribbleL && !armLController.current.animation.id.equals("walkArmL") && !leftPointBall) {
					animateArmL("walk");

					if (prevIdArmL.equals("stayArmL") && prevTime < modelInstance.getAnimation("stayArmL").duration / 2)
						armLController.current.time = 0.5f;
					else if( armRController.current.animation.id.equals("walkArmR"))
						armLController.current.time = armRController.current.time;
				}

				if (!rightHoldingBall && !dribbleR && !armRController.current.animation.id.equals("walkArmR") && !rightPointBall) {
					animateArmR("walk");

					if (prevIdArmR.equals("stayArmR") && prevTime < modelInstance.getAnimation("stayArmR").duration / 2)
						armRController.current.time = 0.5f;
					else if(armLController.current.animation.id.equals("walkArmL"))
						armRController.current.time = armLController.current.time;
				}
			}
			if (!legLController.current.animation.id.equals("walkLegL")) {
				animateLegL("walk");

				if (prevTime < modelInstance.getAnimation("stayArmL").duration / 2)
					legLController.current.time = 0.5f;
			}

			if (!legRController.current.animation.id.equals("walkLegR")) {
				animateLegR("walk");

				if (prevTime < modelInstance.getAnimation("stayArmR").duration / 2)
					legRController.current.time = 0.5f;
			}

			if (!bodyController.current.animation.id.equals("aimLBody") && !bodyController.current.animation.id.equals("aimRBody"))
				stopBodyAnim();
		}
		
		else if(running && !moveVec.isZero()) {
			if (!armLController.current.animation.id.equals("runArmL") && !leftHoldingBall && !dribbleL && !leftPointBall) {
				animateArmL("run");

				if (prevIdArmL.equals("stayArmL") && prevTime < modelInstance.getAnimation("stayArmL").duration / 2)
					armLController.current.time = 0.375f;
				else if(armRController.current.animation.id.equals("runArmR"))
					armLController.current.time = armRController.current.time;
			}
			
			if (!armRController.current.animation.id.equals("runArmR") && !rightHoldingBall && !dribbleR && !rightPointBall) {
				animateArmR("run");

				if (prevIdArmR.equals("stayArmR") && prevTime < modelInstance.getAnimation("stayArmR").duration / 2)
					armRController.current.time = 0.375f;
				else if(armLController.current.animation.id.equals("runArmL"))
					armRController.current.time = armLController.current.time;
			}
			
			if (!legLController.current.animation.id.equals("runLegL")) {
				animateLegL("run");

				if (prevTime < modelInstance.getAnimation("stayArmL").duration / 2)
					legLController.current.time = 0.375f;
			}
			
			if (!legRController.current.animation.id.equals("runLegR")) {
				animateLegR("run");

				if (prevTime < modelInstance.getAnimation("stayArmR").duration / 2)
					legRController.current.time = 0.375f;
			}
			
			if (!bodyController.current.animation.id.equals("runBody")) {
				animateBody("run");
			}
		}
		
		else {
			if (!prevIdArmL.equals("aimLArmL") && !prevIdArmL.equals("aimRArmL") || !isCurrentlyAiming() && !isHoldingBall()) {
				
				if (!leftHoldingBall && !prevIdArmL.equals("stayArmL") && !dribbleL && !prevIdArmL.contains("dribble") && !leftPointBall) {
					armLController.animate("stayArmL", 0.25f);
					armLController.setAnimation("stayArmL", -1, 1, new AnimationListener() {

						@Override
						public void onEnd(AnimationDesc animation) {}

						@Override
						public void onLoop(AnimationDesc animation) {}

					});

					if (!leftThrowBall && prevTime > modelInstance.getAnimation(prevIdArmL).duration / 2) {
						armLController.current.time = modelInstance.getAnimation("stayArmL").duration / 2;
					}
				}
				
				if (!rightHoldingBall && !armRController.current.animation.id.equals("stayArmR") && !dribbleR && !armRController.current.animation.id.contains("dribble") && !rightPointBall) {
					armRController.animate("stayArmR", 0.25f);
					armRController.setAnimation("stayArmR", -1, 1, new AnimationListener() {

						@Override
						public void onEnd(AnimationDesc animation) {}

						@Override
						public void onLoop(AnimationDesc animation) {}

					});

					if (!rightThrowBall && prevTime > modelInstance.getAnimation(prevIdArmL).duration / 2) {
						armRController.current.time = modelInstance.getAnimation("stayArmR").duration / 2;
					}
				}
				stopBodyAnim();
			}
			
			legLController.animate("idle", 0.15f);
			legRController.animate("idle", 0.15f);
			stopLegsAnim();
		}
		
		prevTrans.set(modelInstance.transform);
		
		//if(this instanceof Opponent && (northSurround || southSurround || eastSurround || westSurround))
			//System.out.println("Surround" + northSurround + ";" + southSurround + ";" + eastSurround + ";" + westSurround);
	}
	
	@Override
	public Vector3 getLinearVelocity() {
		return moveVec;
	}
	
	@Override
	public int findNeighbors(ProximityCallback<Vector3> callback) {
		if(callback.equals(brain.getBallSeparate())) {
			//System.out.println("Ball separate invoked");
			if (isProximityColliding(map.getBall())) {
				callback.reportNeighbor(map.getBall());
				//System.out.println("Ball separate worked");
			}
			
			return 0;
		}else if(callback.equals(brain.getBasketSeparate())) {
			//System.out.println("Basket separate invoked");
			float diff = 4;
			if (/*isProximityColliding(getTargetBasket())*/ getPosition().dst(map.getHomeBasket().getPosition()) <= diff || getPosition().dst(map.getAwayBasket().getPosition()) <= diff) {
				callback.reportNeighbor(getTargetBasket());
				//System.out.println("Basket separate worked");
			}
			
			return 0;
		}else if(callback.equals(brain.getPlayerSeparate())) {
			if(this instanceof Teammate) {
				for(Player p : map.getTeammates())
					if(!p.equals(this) && isProximityColliding(p)) 
						callback.reportNeighbor(p);
			}else
				for(Player p : map.getOpponents())
					if(!p.equals(this) && isProximityColliding(p)) 
						callback.reportNeighbor(p);
			//System.out.println("Player separate invoked");
			return 0;
		}else if(callback.equals(brain.getTargetPlayerSeparate())) {
			Player targetPlayer = brain.getMemory().getTargetPlayer();
			
			if(targetPlayer != null && !targetPlayer.equals(this))
				callback.reportNeighbor(targetPlayer);
		}
		else if(callback.equals(brain.getAllPlayerSeparate())) {
			for(Player p : map.getAllPlayers())
				if(!p.equals(this) && isProximityColliding(p)) 
					callback.reportNeighbor(p);
			
			return 0;
		}else if(callback.equals(brain.getCollAvoid())) {
			int count = 0;

			for (Player p : map.getTeammates())
				if (collAvoidCheck(p)/* && (brain.getMemory().getTargetPlayer() != null && !p.equals(brain.getMemory().getTargetPlayer()) || brain.getMemory().getTargetPlayer() == null)*/ && callback.reportNeighbor(p))
					count++;

			for (Player p : map.getOpponents())
				if (collAvoidCheck(p)/* && (brain.getMemory().getTargetPlayer() != null && !p.equals(brain.getMemory().getTargetPlayer()) || brain.getMemory().getTargetPlayer() == null)*/ && callback.reportNeighbor(p))
					count++;
	
			//if (!isHoldingBall()) {
				if (isProximityColliding(map.getHomeBasket()) && callback.reportNeighbor(map.getHomeBasket()))
					count++;

				else if (isProximityColliding(map.getAwayBasket()) && callback.reportNeighbor(map.getAwayBasket()))
					count++;
			//}
			
			return count;
		}
		
		return super.findNeighbors(callback);
	}
	
	private boolean collAvoidCheck(Player p) {
		return !p.equals(this) &&
				GameTools.getDistanceBetweenLocations(p, this) < 4 && 
				//((p.isMainPlayer() && !p.getBrain().getStateMachine().isInState(PlayerState.BALL_CHASING)) || !p.getBrain().getMemory().isBallChaser()) && 
				(!p.isHoldingBall() && //For example it can be used for co-op mode when the players are trying to interpose
				!p.getBrain().getStateMachine().isInState(PlayerState.PLAYER_SURROUND) && brain.getStateMachine().isInState(PlayerState.PLAYER_SURROUND) ||
				brain.getStateMachine().isInState(PlayerState.BALL_CHASING) || brain.getStateMachine().isInState(PlayerState.IDLING));
	}
	
	public Matrix4 getFocusTransform() {
		Matrix4 returnTrans;
		
		if(rotDifference) {
			returnTrans = invTrans.cpy().mul(camMatrix).setTranslation(getPosition());
		}else returnTrans = modelInstance.transform.cpy().mul(camMatrix);
		
		return returnTrans;
	}

	public Matrix4 getCamMatrix() {
		return camMatrix;
	}
	
	public void setMoveVector(Vector3 move) {
		moveVec.set(move);
	}
	
	public Vector3 getMoveVector() {
		return moveVec;
	}
	
	public Vector3 getPrevMoveVec() {
		return prevMoveVec;
	}
	
	/**
	 * A player from one team should return the basket of the other team as a target
	 */
	public abstract Basket getTargetBasket();
	
	public void setRunning() {
		running = currentRunning = true;
	}
	
	public Brain getBrain() {
		return brain;
	}
	
	@Override
	public void dispose() {
		super.dispose();
		
		brain.clear();
	}

	@Override
	public float getWidth() {
		return scale6 * 2 + scale2;
	}

	@Override
	public float getHeight() {
		return model.getNode("head").translation.y + scale1 - model.getNode("leg2L").translation.y + scale5 / 2;
	}
	
	public float getArmHeight() {
		return scale6 / 2 + 2 * scale5 + scale3;
	}

	@Override
	public float getDepth() {
		return scale1;
	}
	
	/**
	 * Ehh... It locks the player to y-axis rotation and prevents it from ehhhhh... floating
	 */
	private void lockRotationAndRandomFloating(boolean blockFloating) {
		Matrix4 modelInstTrans = modelInstance.transform;
		
		Vector3 tempVec = new Vector3();
		modelInstTrans.getTranslation(tempVec);
		if(blockFloating)
			tempVec.y = getHeight();
		Quaternion temp = modelInstTrans.getRotation(new Quaternion());
		
		temp.x = 0;
		temp.z = 0;
		modelInstTrans.set(tempVec, temp);
		
		setCollisionTransform(true);
	}
	
	@Override
	public void collisionOccured(btCollisionObject objInside, btCollisionObject objOutside) {
		super.collisionOccured(objInside, objOutside);
		
		String outId = map.getObjectsMap().get(objOutside.getUserValue());
		
		if(outId.equals(EntityType.BALL.getId()) || outId.equals(EntityType.BALL.getId() + "Obj")) {	
			ballColl = true;
			if(objInside.equals(collObjMap.get("handL")))
				leftHandBall = true;
			else if(objInside.equals(collObjMap.get("handR")))
				rightHandBall = true;
			
			//System.out.println(leftHandBall);
			//if(objInside.equals(getMainBody())) {
				//if(map.getObjectsMap().get(objOutside.getUserValue()).equals(ObjectType.TERRAIN.getId() + "Inv"))
					//walking = running = false;
			//}
		/*}else if(outId.equals(EntityType.TEAMMATE.getId() + "Obj") || outId.equals(EntityType.OPPONENT.getId() + "Obj")) {
			if(objInside.equals(collObjMap.get("poleNObj")))
				northSurround = true;
			else if(objInside.equals(collObjMap.get("poleSObj")))
				southSurround = true;
			else if(objInside.equals(collObjMap.get("poleEObj")))
				eastSurround = true;
			else if(objInside.equals(collObjMap.get("poleWObj")))
				westSurround = true;
		}
		else if(outId.equals(ObjectType.HOMEBASKET.getId()) || outId.equals(ObjectType.AWAYBASKET.getId()) || objOutside instanceof btRigidBody && (outId.contains(EntityType.TEAMMATE.getId()) || outId.contains(EntityType.OPPONENT.getId()))){
			if(objInside.equals(collObjMap.get("poleNObj")))
				northObstacle = true;
			else if(objInside.equals(collObjMap.get("poleSObj")))
				southObstacle = true;
			else if(objInside.equals(collObjMap.get("poleEObj")))
				eastObstacle = true;
			else if(objInside.equals(collObjMap.get("poleWObj")))
				westObstacle = true;*/
		}else if(outId.contains(EntityType.TEAMMATE.getId()) || outId.contains(EntityType.OPPONENT.getId()))
			playerColl = true;
	}
	
	
	//All the commands and indicators should be cleared.
	@Override
	public void onCycleEnd() {
		super.onCycleEnd();
		
		walking = false;
		running = false;
		//jumping = false;
		ballColl = false;
		playerColl = false;
		rightHandBall = false;
		leftHandBall = false;
		leftAimBall = false;
		rightAimBall = false;
		leftPointBall = false;
		rightPointBall = false;
		focus = false;
		
		/*if(!leftHoldingBall)
			leftThrowBall = false;
		else if(!rightHoldingBall)
			rightThrowBall = false;*/
		
		moveVec.setZero();
	}
	
	private final Array<Matrix4> nodesTransforms = new Array<Matrix4>();
	public Array<Matrix4> getNodesTransforms(){
		nodesTransforms.clear();
		
		nodesTransforms.add(camMatrix);
		
		for(Object node : modelInstance.nodes.items) {
			if(node == null)
				break;
			
			getNodesTrans((Node) node);
		}
		
		return nodesTransforms;
	}
	
	private void getNodesTrans(Node currentNode) {
		ArrayIterator<Node> tempIterable = new ArrayIterator<Node>((Array<Node>) currentNode.getChildren());
		
		for(Node child : tempIterable) {
			getNodesTrans(child);
		}
		
		nodesTransforms.add(currentNode.globalTransform);
	}
	
	private static int index;
	public void setNodesTransforms(Array<Matrix4> nodesTrans) {
		index = 0;
		
		camMatrix.set(nodesTrans.get(index));
		index++;
		
		for(Object node : modelInstance.nodes.items) {
			if(node == null)
				break;
			
			setNodesTrans((Node) node, nodesTrans);
		}
	}
	
	private void setNodesTrans(Node currentNode, Array<Matrix4> nodesTrans) {
		ArrayIterator<Node> tempIterable = new ArrayIterator<Node>((Array<Node>) currentNode.getChildren());
		for(Node child : tempIterable) {
			setNodesTrans(child, nodesTrans);
		}
		
		currentNode.globalTransform.set(nodesTrans.get(index));
		/*if(currentNode.id.equals("head"))
			camMatrix.set(currentNode.globalTransform);*/
		
		index++;
	}
	
	public Matrix4 getShoulderLTrans() {
		return calcTransformFromNodesTransform(modelInstance.getNode("shoulderL").globalTransform);
	}
	
	public Matrix4 getShoulderRTrans() {
		return calcTransformFromNodesTransform(modelInstance.getNode("shoulderR").globalTransform);
	}
	
	public HashMap<String, btRigidBody> getBodiesMap() {
		return bodiesMap;
	}
	
	public Player getFocusedPlayer() {
		return focusTarget;
	}
	
	public void addFoul() {
		fouls++;
	}
	
	public int getFouls() {
		return fouls;
	}
	
	public int getPoints() {
		return points;
	}

	public void addPoints(int points) {
		this.points += points;
	}

	public void setPlayerIndex(int playerIndex) {
		this.playerIndex = playerIndex;
	}
	
	public int getPlayerIndex() {
		return playerIndex;
	}
	
	public int getShootingPower() {
		return shootingPower;
	}
	
	/*public void setUpdateBrain(boolean updateBrain) {
		this.updateBrain = updateBrain;
	}
	
	public boolean isUpdateBrain() {
		return updateBrain;
	}*/
	
	//TODO If you don't need those limiters, delete them and their property values above!
	public float getMinRotateDegrees() {
		return minRotateDegrees;
	}

	public void setMinRotateDegrees(float minRotateDegrees) {
		this.minRotateDegrees = minRotateDegrees;
	}

	public float getMaxRotateDegrees() {
		return maxRotateDegrees;
	}

	public void setMaxRotateDegrees(float maxRotateDegrees) {
		this.maxRotateDegrees = maxRotateDegrees;
	}

	public boolean isAbleToMove() {
		return ableToRun;
	}

	public void setAbleToRun(boolean ableToRun) {
		this.ableToRun = ableToRun;
	}

	public boolean isAbleToCatch() {
		return leftHandInWorld && rightHandInWorld;
	}

	public boolean isAbleToDribble(){
		return dribbleTimeOut <= 0 && time <= 0 && (leftHoldingBall && armLController.current.animation.id.equals("dribbleIdleArmL") ||
				rightHoldingBall && armRController.current.animation.id.equals("dribbleIdleArmR"));
	}

	public boolean isHoldingBall() {
		
		//System.out.println(map.getCurrentPlayerHoldTeam());
		//System.out.println("Not holding");
		return //(dribbleL || dribbleR) || 
				((this instanceof Teammate && map.getTeammates().indexOf(this) == map.getCurrentPlayerHoldTeam()) || 
						(this instanceof Opponent && map.getOpponents().indexOf(this) == map.getCurrentPlayerHoldOpp()));
	}
	
	public boolean isDribbling() {
		return dribbleL || dribbleR;
	}
	
	public boolean isFocusing() {
		return focus || focusTarget != null;
	}
	
	public boolean isAiming() {
		return leftAimBall || rightAimBall;
	}
	
	public boolean isCurrentlyAiming() {
		return leftCurrentAim || rightCurrentAim;
	}
	
	public boolean isShooting() {
		return leftThrowBall || rightThrowBall;
	}
	
	public boolean isShootingAchieved() {
		return thrownTime <= 0;
	}
	
	public boolean isAimingOrShooting() {
		return isCurrentlyAiming() || isShooting();
	}
	
	public boolean isPointing() {
		return leftPointBall || rightPointBall;
	}
	
	public boolean isCurrentlyPointing() {
		return leftCurrentPoint || rightCurrentPoint;
	}
	
	public boolean isLeftHolding() {
		return leftHoldingBall || dribbleL || leftThrowBall;
	}
	
	public boolean isRightHolding() {
		return rightHoldingBall || dribbleR || rightThrowBall;
	}
	
	public boolean isDataBallHolding() {
		return isLeftHolding() || isRightHolding();
	}
	
	/**
	 * 
	 * @return true if the ball is out of player's hands range (after it has been thrown for example)
	 */
	public boolean isBallFree() {
		return !downBody;
	}
	
	public boolean isPlayerColl() {
		return playerColl;
	}

	public boolean isRunning() {
		return running;
	}
	
	public boolean isCurrentlyRunning() {
		return currentRunning;
	}
	
	/*public boolean isNorthSurround() {
		return northSurround;
	}

	public boolean isSouthSurround() {
		return southSurround;
	}

	public boolean isEastSurround() {
		return eastSurround;
	}
	
	public boolean isSurrounded() {
		int sides = 0;
		
		if(northSurround)
			sides++;
		if(eastSurround)
			sides++;
		if(westSurround)
			sides++;
		
		return sides > 2;
	}

	public boolean isWestSurround() {
		return westSurround;
	}
	
	public boolean isNorthObstacle() {
		return northObstacle;
	}

	public boolean isSouthObstacle() {
		return southObstacle;
	}
	
	public boolean isEastObstacle() {
		return eastObstacle;
	}
	
	public boolean isWestObstacle() {
		return westObstacle;
	}*/
	
	public boolean isABot() {
		return isABot;
	}

	public void setABot(boolean isABot) {
		this.isABot = isABot;
	}

	public abstract Zone getHomeZone();
	
	public abstract Zone getAwayZone();
	
	public abstract Zone getHomeBasketZone();
	
	public abstract Zone getHomeThreePointZone();
	
	public abstract Zone getAwayBasketZone();
	
	public abstract Zone getAwayThreePointZone();
	
	public boolean isInHomeBasketZone() {
		return getHomeBasketZone().checkZone(getPosition());
	}
	
	public boolean isInHomeThreePointZone() {
		return getHomeThreePointZone().checkZone(getPosition());
	}
	
	public boolean isInHomeZone() {
		return getHomeZone().checkZone(getPosition());
	}
	
	public boolean isInAwayZone() {
		return getAwayZone().checkZone(getPosition());
	}
	
	public boolean isInAwayBasketZone() {
		return getAwayBasketZone().checkZone(getPosition());
	}
	
	public boolean isInAwayThreePointZone() {
		return getAwayThreePointZone().checkZone(getPosition());
	}
	
	public boolean isBehindBasket() {
		return Math.abs(getPosition().z) - Math.abs(map.getHomeBasket().getPosition().z) >= 1.25f;
	}

	public boolean isMainPlayer() {
		return map.getMainPlayer().equals(this);
	}
	
}
