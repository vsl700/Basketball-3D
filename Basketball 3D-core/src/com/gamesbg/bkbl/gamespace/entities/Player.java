package com.gamesbg.bkbl.gamespace.entities;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationDesc;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationListener;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.SphereShapeBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.gamesbg.bkbl.gamespace.GameMap;
import com.gamesbg.bkbl.gamespace.tools.CustomAnimation;
import com.gamesbg.bkbl.gamespace.entities.players.Opponent;
import com.gamesbg.bkbl.gamespace.entities.players.Teammate;
import com.gamesbg.bkbl.gamespace.entities.players.ai.*;
import com.gamesbg.bkbl.gamespace.objects.ObjectType;

public abstract class Player extends Entity {

	static final float MAX_WALKING_VELOCITY = 4;
	static final float MAX_RUNNING_VELOCITY = 11;
	static final float dribbleSpeed = 0.1f;
	//The node which should be followed by the camera
	Matrix4 camMatrix;
	
	//The recent player's movement (or linear acceleration in this mechanic)
	Vector3 moveVec = new Vector3();
	
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
	static final float scale1 = 0.5f;
	static final float scale2 = 1;
	static final float scale3 = 0.15f;
	static final float scale4 = 0.23f;
	static final float scale5 = 0.75f;
	static final float scale6 = 0.35f;
	static final float scale7 = 0.315f;
	static final float handPercentage = 0.4f;
	static float poleScale;

	//Player's mechanics
	boolean walking, running, jumping;
	boolean leftHoldingBall, rightHoldingBall;
	boolean leftAimBall, rightAimBall;
	boolean leftThrowBall, rightThrowBall, readyBall;
	boolean leftPointBall, rightPointBall;
	boolean dribbleL, dribbleR;
	boolean ballColl; 
	boolean leftHandBall, rightHandBall;
	boolean leftHandInWorld, rightHandInWorld;
	boolean downBody;
	boolean northSurround, southSurround, eastSurround, westSurround;
	boolean northObstacle, southObstacle, eastObstacle, westObstacle;
	boolean inBasketZone;
	
	int shootingPower = 10;
	int cycleTimeout;
	
	int playerIndex;
	
	@Override
	public void create(EntityType type, GameMap map, Vector3 pos) {
		super.create(type, map, pos);
		
		poleScale = 2f;
		
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
		
		brain = new Brain(this);
		
			//stateMachine = new DefaultStateMachine<Player, PlayerState>(this, PlayerState.IDLING);
			//stateMachine.changeState(PlayerState.IDLING);

	}
	
	private void animateModels() {
		CustomAnimation custom = new CustomAnimation(model);
		
		//We needed an idle animation for all of the body parts that don't act in any way and also to animate transitions between animation and idle staying for legs and body
		custom.addAnimation("idle", 0);
		custom.addNodeAnimation("spine2");
		custom.addRotationKeyFrame(0, 0, 0, 0, 0);
		
		
		custom.addAnimation("stayArmL", 4);
		
		custom.addNodeAnimation("shoulderL");
		custom.addRotationKeyFrame(0, 0, 0, 0, 0);
		custom.addRotationKeyFrame(-1, 0, 1, 3, 2);
		custom.addRotationKeyFrame(0, 0, 0, 0, 4);
		
		
		custom.addAnimation("stayArmR", 4);
		
		custom.addNodeAnimation("shoulderR");
		custom.addRotationKeyFrame(-1, 0, 0, 5, 0);
		custom.addRotationKeyFrame(0, 0, -1, 3, 2);
		custom.addRotationKeyFrame(-1, 0, 0, 5, 4);
		
		
		//Aimings are going to be animated by the controller's fading effect
		custom.addAnimation("aimLArmL", 0.01f);
		
		custom.addNodeAnimation("shoulderL");
		custom.addRotationKeyFrame(0, 1.2f, 1.4f, 90, 0);
		//custom.addRotationKeyFrame(1, 0, 0, 30, 0);
		custom.addRotationKeyFrame(0, 1.2f, 1.4f, 90, 0.01f);
		//custom.addRotationKeyFrame(0, 1, 0, 110, 0);
		//custom.addRotationKeyFrame(1, 0, 0, 30, 0);
		//custom.addRotationKeyFrame(0, 1, 0, 110, 1);
		
		custom.addNodeAnimation("elbowL");
		custom.addRotationKeyFrame(1, 0, 0, -60, 0);
		custom.addRotationKeyFrame(1, 0, 0, -60, 0.01f);
		
		
		custom.addAnimation("aimLArmR", 0.01f);
		
		custom.addNodeAnimation("shoulderR");
		custom.addRotationsKeyFrame(new float[][]{{1, 0, 0, -140}, {0, 1, 0, 110}, {1, 0, 1, 70}}, 0);
		
		
		custom.addAnimation("aimLBody", 0.01f);
		
		custom.addNodeAnimation("spine2");
		custom.addRotationKeyFrame(0, 1, 0, 30, 0);
		custom.addRotationKeyFrame(0, 1, 0, 30, 0.01f);
		
		custom.addNodeAnimation("spine1");
		custom.addRotationKeyFrame(0, 1, 0, 30, 0);
		custom.addRotationKeyFrame(0, 1, 0, 30, 0.01f);
		
		
		custom.addAnimation("throwLArmL", 0.25f);
		
		custom.addNodeAnimation("shoulderL");
		custom.addRotationsKeyFrame(new float[][]{{1, 0, 0, -140}, {0, 1, 0, -110}, {1, 0, 1, 70}}, 0);
		custom.addRotationsKeyFrame(new float[][]{{1, 0, 0, -140}, {0, 1, 0, -110}, {1, 0, 1, 70}}, 0.25f);
		
		custom.addNodeAnimation("elbowL");
		custom.addRotationKeyFrame(1, 0, 0, 0, 0);
		custom.addRotationKeyFrame(1, 0, 0, 0, 0.25f);
		
		
		custom.addAnimation("throwLArmR", 0.25f);
		
		custom.addNodeAnimation("shoulderR");
		custom.addRotationsKeyFrame(new float[][]{{1, 0, 0, 40}, {0, 1, 0, 110}, {1, 0, 1, 50}}, 0);
		custom.addRotationsKeyFrame(new float[][]{{1, 0, 0, 40}, {0, 1, 0, 110}, {1, 0, 1, 50}}, 0.25f);
		
		
		custom.addAnimation("throwLBody", 0.25f);
		
		custom.addNodeAnimation("spine1");
		custom.addRotationKeyFrame(0, 1, 0, -30, 0);
		custom.addRotationKeyFrame(0, 1, 0, -30, 0.25f);
		
		custom.addNodeAnimation("spine2");
		custom.addRotationKeyFrame(0, 1, 0, -30, 0);
		custom.addRotationKeyFrame(0, 1, 0, -30, 0.25f);
		
		
		custom.addAnimation("aimRArmL", 0.01f);
		
		custom.addNodeAnimation("shoulderL");
		custom.addRotationsKeyFrame(new float[][]{{0, 1, 0, -70}, {1, 0, 0, 40}, {1, 0, 1, 70}, {0, 1, 0, 90}}, 0);
		
		
		custom.addAnimation("aimRArmR", 0.01f);
		
		custom.addNodeAnimation("shoulderR");
		custom.addRotationKeyFrame(0, 1.2f, 1.4f, -90, 0);
		custom.addRotationKeyFrame(0, 1.2f, 1.4f, -90, 0.01f);
		
		custom.addNodeAnimation("elbowR");
		custom.addRotationKeyFrame(1, 0, 0, -60, 0);
		custom.addRotationKeyFrame(1, 0, 0, -60, 0.01f);
		
		
		custom.addAnimation("aimRBody", 0.01f);
		
		custom.addNodeAnimation("spine2");
		custom.addRotationKeyFrame(0, 1, 0, -30, 0);
		custom.addRotationKeyFrame(0, 1, 0, -30, 0.01f);
		
		custom.addNodeAnimation("spine1");
		custom.addRotationKeyFrame(0, 1, 0, -30, 0);
		custom.addRotationKeyFrame(0, 1, 0, -30, 0.01f);
		
		
		custom.addAnimation("throwRArmL", 0.25f);
		
		custom.addNodeAnimation("shoulderL");
		custom.addRotationsKeyFrame(new float[][]{{1, 0, 0, 40}, {0, 1, 0, 110}, {1, 0, 1, 50}}, 0);
		custom.addRotationsKeyFrame(new float[][]{{1, 0, 0, 40}, {0, 1, 0, 110}, {1, 0, 1, 50}}, 0.25f);
		
		custom.addAnimation("throwRArmR", 0.25f);
		custom.addNodeAnimation("shoulderR");
		custom.addRotationsKeyFrame(new float[][]{{1, 0, 0, -140}, {0, 1, 0, 110}, {1, 0, 1, 70}, {0, 0, 1, -60}}, 0);
		custom.addRotationsKeyFrame(new float[][]{{1, 0, 0, -140}, {0, 1, 0, 110}, {1, 0, 1, 70}, {0, 0, 1, -60}}, 0.25f);
		
		custom.addNodeAnimation("elbowR");
		custom.addRotationKeyFrame(1, 0, 0, 0, 0);
		custom.addRotationKeyFrame(1, 0, 0, 0, 0.25f);
		
		
		custom.addAnimation("throwRBody", 0.25f);
		
		custom.addNodeAnimation("spine1");
		custom.addRotationKeyFrame(0, 1, 0, 30, 0);
		custom.addRotationKeyFrame(0, 1, 0, 30, 0.25f);
		
		custom.addNodeAnimation("spine2");
		custom.addRotationKeyFrame(0, 1, 0, 30, 0);
		custom.addRotationKeyFrame(0, 1, 0, 30, 0.25f);
		
		
		custom.addAnimation("dribbleIdleArmL", 1);
		
		custom.addNodeAnimation("shoulderL");
		custom.addRotationKeyFrame(1, 0, 0, -10, 0);
		custom.addRotationKeyFrame(1, 0, 0, -10, 1);
		
		custom.addNodeAnimation("elbowL");
		custom.addRotationKeyFrame(1, 0, 0, -80, 0);
		custom.addRotationKeyFrame(1, 0, 0, -80, 1);
		
		
		custom.addAnimation("dribblePhase1ArmL", dribbleSpeed);
		
		custom.addNodeAnimation("shoulderL");
		custom.addRotationKeyFrame(1, 0, 0, -10, 0);
		custom.addRotationKeyFrame(1, 0, 0, -10, dribbleSpeed);
		
		custom.addNodeAnimation("elbowL");
		custom.addRotationKeyFrame(1, 0, 0, -80, 0);
		custom.addRotationKeyFrame(1, 0, 0, -50, dribbleSpeed);
		
		
		custom.addAnimation("dribbleIdle1ArmL", 1);
		
		custom.addNodeAnimation("shoulderL");
		custom.addRotationKeyFrame(1, 0, 0, -10, 0);
		custom.addRotationKeyFrame(1, 0, 0, -10, 1);
		
		custom.addNodeAnimation("elbowL");
		custom.addRotationKeyFrame(1, 0, 0, -50, 0);
		custom.addRotationKeyFrame(1, 0, 0, -50, 1);
		
		
		custom.addAnimation("dribblePhase2ArmL", dribbleSpeed);
		
		custom.addNodeAnimation("shoulderL");
		custom.addRotationKeyFrame(1, 0, 0, -10, 0);
		custom.addRotationKeyFrame(1, 0, 0, -10, dribbleSpeed);
		
		custom.addNodeAnimation("elbowL");
		custom.addRotationKeyFrame(1, 0, 0, -50, 0);
		custom.addRotationKeyFrame(1, 0, 0, -80, dribbleSpeed);
		
		
		custom.addAnimation("dribbleIdleArmR", 1);
		
		custom.addNodeAnimation("shoulderR");
		custom.addRotationKeyFrame(1, 0, 0, -10, 0);
		custom.addRotationKeyFrame(1, 0, 0, -10, 1);
		
		custom.addNodeAnimation("elbowR");
		custom.addRotationKeyFrame(1, 0, 0, -80, 0);
		custom.addRotationKeyFrame(1, 0, 0, -80, 1);
		
		
		custom.addAnimation("dribblePhase1ArmR", dribbleSpeed);
		
		custom.addNodeAnimation("shoulderR");
		custom.addRotationKeyFrame(1, 0, 0, -10, 0);
		custom.addRotationKeyFrame(1, 0, 0, -10, dribbleSpeed);
		
		custom.addNodeAnimation("elbowR");
		custom.addRotationKeyFrame(1, 0, 0, -80, 0);
		custom.addRotationKeyFrame(1, 0, 0, -50, dribbleSpeed);
		
		
		custom.addAnimation("dribbleIdle1ArmR", 1);
		
		custom.addNodeAnimation("shoulderR");
		custom.addRotationKeyFrame(1, 0, 0, -10, 0);
		custom.addRotationKeyFrame(1, 0, 0, -10, 1);
		
		custom.addNodeAnimation("elbowR");
		custom.addRotationKeyFrame(1, 0, 0, -50, 0);
		custom.addRotationKeyFrame(1, 0, 0, -50, 1);
		
		
		custom.addAnimation("dribblePhase2ArmR", dribbleSpeed);
		
		custom.addNodeAnimation("shoulderR");
		custom.addRotationKeyFrame(1, 0, 0, -10, 0);
		custom.addRotationKeyFrame(1, 0, 0, -10, dribbleSpeed);
		
		custom.addNodeAnimation("elbowR");
		custom.addRotationKeyFrame(1, 0, 0, -50, 0);
		custom.addRotationKeyFrame(1, 0, 0, -80, dribbleSpeed);
		
		
		custom.addAnimation("runBody", 0.75f);
		
		custom.addNodeAnimation("spine3");
		custom.addRotationKeyFrame(1, 0, 0, 20, 0);
		custom.addRotationKeyFrame(1, 0, 0, 20, 0.75f);
		
		custom.addAnimation("runArmL", 0.75f);
		
		custom.addNodeAnimation("shoulderL");
		custom.addRotationKeyFrame(0, 0, 0, 0, 0);
		custom.addRotationKeyFrame(-1, 0, 0, 50, 0.15f);
		custom.addRotationKeyFrame(1, 0, 0, 40, 0.6f);
		custom.addRotationKeyFrame(0, 0, 0, 0, 0.75f);
		
		custom.addNodeAnimation("elbowL");
		custom.addRotationKeyFrame(-1, 0, 0, 90, 0);
		custom.addRotationKeyFrame(-1, 0, 0, 90, 0.75f);
		
		custom.addAnimation("runArmR", 0.75f);
		
		custom.addNodeAnimation("shoulderR");
		custom.addRotationKeyFrame(0, 0, 0, 0, 0);
		custom.addRotationKeyFrame(1, 0, 0, 40, 0.25f);
		custom.addRotationKeyFrame(-1, 0, 0, 50, 0.6f);
		custom.addRotationKeyFrame(0, 0, 0, 0, 0.75f);
		
		custom.addNodeAnimation("elbowR");
		custom.addRotationKeyFrame(-1, 0, 0, 90, 0);
		custom.addRotationKeyFrame(-1, 0, 0, 90, 0.75f);
		
		custom.addAnimation("runLegL", 0.75f);
		
		custom.addNodeAnimation("hipL");
		custom.addRotationKeyFrame(-1, 0, 0, 20, 0);
		custom.addRotationKeyFrame(1, 0, 0, 50, 0.15f);
		custom.addRotationKeyFrame(-1, 0, 0, 90, 0.6f);
		custom.addRotationKeyFrame(-1, 0, 0, 20, 0.75f);
		
		custom.addNodeAnimation("kneeL");
		custom.addRotationKeyFrame(0, 0, 0, 0, 0);
		custom.addRotationKeyFrame(0, 0, 0, 0, 0.15f);
		custom.addRotationKeyFrame(1, 0, 0, 90, 0.6f);
		custom.addRotationKeyFrame(0, 0, 0, 0, 0.75f);
		
		custom.addAnimation("runLegR", 0.75f);
		
		custom.addNodeAnimation("hipR");
		custom.addRotationKeyFrame(-1, 0, 0, 20, 0);
		custom.addRotationKeyFrame(-1, 0, 0, 90, 0.15f);
		custom.addRotationKeyFrame(1, 0, 0, 50, 0.6f);
		custom.addRotationKeyFrame(-1, 0, 0, 20, 0.75f);
		
		custom.addNodeAnimation("kneeR");
		custom.addRotationKeyFrame(0, 0, 0, 0, 0);
		custom.addRotationKeyFrame(1, 0, 0, 90, 0.15f);
		custom.addRotationKeyFrame(1, 0, 0, 0, 0.6f);
		custom.addRotationKeyFrame(0, 0, 0, 00, 0.75f);
		
		
		custom.addAnimation("walkArmL", 1);
		
		custom.addNodeAnimation("shoulderL");
		custom.addRotationKeyFrame(0, 0, 0, 0, 0);
		custom.addRotationKeyFrame(0.5f, 0, 0, 30, 0.25f);
		custom.addRotationKeyFrame(0, 0, 0, 50, 0.5f);
		custom.addRotationKeyFrame(-0.5f, 0, 0, 30, 0.75f);
		custom.addRotationKeyFrame(0, 0, 0, 50, 1);
		
		custom.addNodeAnimation("elbowL");
		custom.addRotationKeyFrame(0, 0, 0, 0, 0);
		custom.addRotationKeyFrame(0, 0, 0, 0, 0.5f);
		custom.addRotationKeyFrame(-0.25f, 0, 0, 60, 0.75f);
		custom.addRotationKeyFrame(0, 0, 0, 60, 1);
		
		custom.addAnimation("walkArmR", 1);
		
		custom.addNodeAnimation("shoulderR");
		custom.addRotationKeyFrame(0, 0, 0, 0, 0);
		custom.addRotationKeyFrame(-0.5f, 0, 0, 30, 0.25f);
		custom.addRotationKeyFrame(0, 0, 0, 50, 0.5f);
		custom.addRotationKeyFrame(0.5f, 0, 0, 30, 0.75f);
		custom.addRotationKeyFrame(0, 0, 0, 50, 1);
		
		custom.addNodeAnimation("elbowR");
		custom.addRotationKeyFrame(0, 0, 0, 0, 0);
		custom.addRotationKeyFrame(-0.25f, 0, 0, 60, 0.25f);
		custom.addRotationKeyFrame(0, 0, 0, 60, 0.5f);
		custom.addRotationKeyFrame(0, 0, 0, 0, 1);
		
		custom.addAnimation("walkLegL", 1);
		
		custom.addNodeAnimation("hipL");
		custom.addRotationKeyFrame(0, 0, 0, 0, 0);
		custom.addRotationKeyFrame(-0.5f, 0, 0, 30, 0.25f);
		custom.addRotationKeyFrame(0, 0, 0, 50, 0.5f);
		custom.addRotationKeyFrame(0.5f, 0, 0, 30, 0.75f);
		custom.addRotationKeyFrame(0, 0, 0, 50, 1);
		
		custom.addNodeAnimation("kneeL");
		custom.addRotationKeyFrame(0, 0, 0, 0, 0);
		custom.addRotationKeyFrame(0.25f, 0, 0, 20, 0.25f);
		custom.addRotationKeyFrame(0, 0, 0, 20, 0.5f);
		custom.addRotationKeyFrame(0, 0, 0, 0, 1);
		
		custom.addAnimation("walkLegR", 1);
		
		custom.addNodeAnimation("hipR");
		custom.addRotationKeyFrame(0, 0, 0, 0, 0);
		custom.addRotationKeyFrame(0.5f, 0, 0, 30, 0.25f);
		custom.addRotationKeyFrame(0, 0, 0, 50, 0.5f);
		custom.addRotationKeyFrame(-0.5f, 0, 0, 30, 0.75f);
		custom.addRotationKeyFrame(0, 0, 0, 50, 1);


		custom.addNodeAnimation("kneeR");
		custom.addRotationKeyFrame(0, 0, 0, 0, 0);
		custom.addRotationKeyFrame(0, 0, 0, 0, 0.5f);
		custom.addRotationKeyFrame(0.25f, 0, 0, 20, 0.75f);
		custom.addRotationKeyFrame(0, 0, 0, 20, 1);
		
	}
	
	protected abstract Color getPlayerColor();
	
	protected void createModels(Vector3 pos) {
		int divisionU = 10;
		int divisionV = 10;
		
		ModelBuilder mb = new ModelBuilder();
		ModelBuilder childMB = new ModelBuilder();
		Material material = new Material(ColorAttribute.createDiffuse(getPlayerColor()));
		
		mb.begin();
		childMB.begin();
		
		Node head = childMB.node();
		head.id = "head";
		head.translation.set(0, scale1 / 2 + scale3 / 2 + 0.1f, 0);
		SphereShapeBuilder.build(childMB.part(head.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale1, scale1, scale1, divisionU, divisionV);
		camMatrix = head.globalTransform;
		
		Node spine1 = childMB.node();
		spine1.id = "spine1";
		spine1.translation.set(0, scale3 * 3.5f, 0);
		BoxShapeBuilder.build(childMB.part(spine1.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale2, scale3, scale4);
		spine1.addChild(head);
		
		Node spine2 = childMB.node();
		spine2.id = "spine2";
		spine2.translation.set(0, scale3 * 3.5f, 0);
		BoxShapeBuilder.build(childMB.part(spine2.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale2, scale3, scale4);
		spine2.addChild(spine1);
		
		Node spine3 = mb.node();
		spine3.id = "spine3";
		BoxShapeBuilder.build(mb.part(spine3.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale2, scale3, scale4);
		spine3.addChild(spine2);
		
		Node shoulderL = childMB.node();
		shoulderL.id = "shoulderL";
		shoulderL.translation.set(scale2 / 2 + scale6 / 2, 0, 0);
		SphereShapeBuilder.build(childMB.part(shoulderL.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale6, scale6, scale6, divisionU, divisionV);
		spine1.addChild(shoulderL);
		
		Node arm1L = childMB.node();
		arm1L.id = "arm1L";
		shoulderL.addChild(arm1L);
		arm1L.translation.set(0, -scale5 / 2, 0);
		BoxShapeBuilder.build(childMB.part(arm1L.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale5, scale4);
		
		Node elbowL = childMB.node();
		elbowL.id = "elbowL";
		arm1L.addChild(elbowL);
		elbowL.translation.set(0, -scale5 / 2 - scale3 / 2, -scale3 * scale5);
		SphereShapeBuilder.build(childMB.part(elbowL.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale3, scale3, divisionU, divisionV);
		
		Node arm2L = childMB.node();
		arm2L.id = "arm2L";
		elbowL.addChild(arm2L);
		arm2L.translation.set(0, -scale5 * (1 - handPercentage) / 2 - scale3 / 2, scale3 * scale5);
		BoxShapeBuilder.build(childMB.part(arm2L.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale5 * (1 - handPercentage), scale4);
		
		Node handL = childMB.node();
		handL.id = "handL";
		arm2L.addChild(handL);
		handL.translation.set(0, -scale5 / 2, 0);
		BoxShapeBuilder.build(childMB.part(handL.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale5 * handPercentage, scale4);
		
		Node shoulderR = childMB.node();
		shoulderR.id = "shoulderR";
		shoulderR.translation.set(-scale2 / 2 - scale6 / 2, 0, 0);
		SphereShapeBuilder.build(childMB.part(shoulderR.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale6, scale6, scale6, divisionU, divisionV);
		spine1.addChild(shoulderR);
		
		Node arm1R = childMB.node();
		arm1R.id = "arm1R";
		shoulderR.addChild(arm1R);
		arm1R.translation.set(0, -scale5 / 2, 0);
		BoxShapeBuilder.build(childMB.part(arm1R.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale5, scale4);
		
		Node elbowR = childMB.node();
		elbowR.id = "elbowR";
		arm1R.addChild(elbowR);
		elbowR.translation.set(0, -scale5 / 2 - scale3 / 2, -scale3 * scale5);
		SphereShapeBuilder.build(childMB.part(elbowR.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale3, scale3, divisionU, divisionV);
		
		Node arm2R = childMB.node();
		arm2R.id = "arm2R";
		elbowR.addChild(arm2R);
		arm2R.translation.set(0, -scale5 * (1 - handPercentage) / 2 - scale3 / 2, scale3 * scale5);
		BoxShapeBuilder.build(childMB.part(arm2R.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale5 * (1 - handPercentage), scale4);
		
		Node handR = childMB.node();
		handR.id = "handR";
		arm2R.addChild(handR);
		handR.translation.set(0, -scale5 / 2, 0);
		BoxShapeBuilder.build(childMB.part(handR.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale5 * handPercentage, scale4);
		
		Node hipL = childMB.node();
		hipL.id = "hipL";
		hipL.translation.set(scale2 / 2 - scale3 / 2, 0, 0);
		SphereShapeBuilder.build(childMB.part(hipL.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale7, scale7, scale7, divisionU, divisionV);
		spine3.addChild(hipL);
		
		Node leg1L = childMB.node();
		leg1L.id = "leg1L";
		hipL.addChild(leg1L);
		leg1L.translation.set(0, -scale5 / 2 - scale7 / 3, 0);
		BoxShapeBuilder.build(childMB.part(leg1L.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale5, scale4);
		
		Node kneeL = childMB.node();
		kneeL.id = "kneeL";
		leg1L.addChild(kneeL);
		kneeL.translation.set(0, -scale5 / 2 - scale3 / 2, scale3 * scale5);
		SphereShapeBuilder.build(childMB.part(kneeL.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale3, scale3, divisionU, divisionV);
		
		Node leg2L = childMB.node();
		leg2L.id = "leg2L";
		kneeL.addChild(leg2L);
		leg2L.translation.set(0, -scale5 / 2 - scale3 / 2, -scale3 * scale5);
		BoxShapeBuilder.build(childMB.part(leg2L.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale5, scale4);
		
		Node hipR = childMB.node();
		hipR.id = "hipR";
		hipR.translation.set(-scale2 / 2 + scale3 / 2, 0, 0);
		SphereShapeBuilder.build(childMB.part(hipR.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale7, scale7, scale7, divisionU, divisionV);
		spine3.addChild(hipR);
		
		Node leg1R = childMB.node();
		leg1R.id = "leg1R";
		hipR.addChild(leg1R);
		leg1R.translation.set(0, -scale5 / 2 - scale7 / 3, 0);
		BoxShapeBuilder.build(childMB.part(leg1R.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale5, scale4);
		
		Node kneeR = childMB.node();
		kneeR.id = "kneeR";
		leg1R.addChild(kneeR);
		kneeR.translation.set(0, -scale5 / 2 - scale3 / 2, scale3 * scale5);
		SphereShapeBuilder.build(childMB.part(kneeR.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale3, scale3, divisionU, divisionV);
		
		Node leg2R = childMB.node();
		leg2R.id = "leg2R";
		kneeR.addChild(leg2R);
		leg2R.translation.set(0, -scale5 / 2 - scale3 / 2, -scale3 * scale5);
		BoxShapeBuilder.build(childMB.part(leg2R.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale5, scale4);
		
		childMB.end();
		model = mb.end();
		
		animateModels();
		modelInstance = new ModelInstance(model, pos);
	}
	
	@Override
	protected void createCollisions() {
		Matrix4 tempHandL = modelInstance.getNode("handL").globalTransform;
		Matrix4 tempHandR = modelInstance.getNode("handR").globalTransform;
		
		Vector3 armVec = new Vector3(scale3 / 2, scale5 * (1 - handPercentage) / 2, scale4 / 2);
		Vector3 handVec = new Vector3(scale3 / 2, scale5 * handPercentage / 2, scale4 / 2);
		
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
		
		invisCollShapes = new ArrayList<btCollisionShape>();
		invisCollShapes.add(new btBoxShape(handVec));
		invisCollShapes.add(new btBoxShape(handVec));
		
		matrixes.add(modelInstance.transform);
		matrixes.add(tempHandL);
		matrixes.add(tempHandR);
		
		Vector3 poleVec = new Vector3(poleScale, 10, poleScale);
		
		invisCollShapes.add(new btBoxShape(poleVec));
		invisCollShapes.add(new btBoxShape(poleVec));
		invisCollShapes.add(new btBoxShape(poleVec));
		invisCollShapes.add(new btBoxShape(poleVec));
		invisCollShapes.add(new btBoxShape(new Vector3(scale2 / 2, getHeight(), getDepth() / 2)));
	}
	
	private void createCollisionObjects() {
		collisionObjects = new ArrayList<btCollisionObject>();
		collObjMap = new HashMap<String, btCollisionObject>();
		manualSetTransformsObj = new ArrayList<btCollisionObject>();
		
		btCollisionObject bodyObj = new btCollisionObject();
		bodyObj.setCollisionShape(invisCollShapes.get(6));
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
		
		btCollisionObject poleNObj = new btCollisionObject();
		poleNObj.setCollisionShape(invisCollShapes.get(2));
		collObjMap.put("poleNObj", poleNObj);
		collisionObjects.add(poleNObj);
		manualSetTransformsObj.add(poleNObj);
		poleNObj.setUserIndex(10);
		
		btCollisionObject poleSObj = new btCollisionObject();
		poleSObj.setCollisionShape(invisCollShapes.get(3));
		collObjMap.put("poleSObj", poleSObj);
		collisionObjects.add(poleSObj);
		manualSetTransformsObj.add(poleSObj);
		poleSObj.setUserIndex(20);
		
		btCollisionObject poleEObj = new btCollisionObject();
		poleEObj.setCollisionShape(invisCollShapes.get(4));
		collObjMap.put("poleEObj", poleEObj);
		collisionObjects.add(poleEObj);
		manualSetTransformsObj.add(poleEObj);
		poleEObj.setUserIndex(30);
		
		btCollisionObject poleWObj = new btCollisionObject();
		poleWObj.setCollisionShape(invisCollShapes.get(5));
		collObjMap.put("poleWObj", poleWObj);
		collisionObjects.add(poleWObj);
		manualSetTransformsObj.add(poleWObj);
		poleWObj.setUserIndex(40);
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
		
	}
	
	@Override
	public void setCollisionTransform(boolean updateMain) {
		super.setCollisionTransform(updateMain);
		
		collObjMap.get("poleNObj").setWorldTransform(calcTransformFromNodesTransform(new Matrix4().set(new Vector3(0, poleScale / 2, getDepth() / 2 + poleScale / 2), new Quaternion())));
		collObjMap.get("poleSObj").setWorldTransform(calcTransformFromNodesTransform(new Matrix4().set(new Vector3(0, poleScale / 2, -getDepth() / 2 - poleScale / 2), new Quaternion())));
		collObjMap.get("poleEObj").setWorldTransform(calcTransformFromNodesTransform(new Matrix4().set(new Vector3(-poleScale, poleScale / 2, 0), new Quaternion())));//W and E sizes are multiplied by 2
		collObjMap.get("poleWObj").setWorldTransform(calcTransformFromNodesTransform(new Matrix4().set(new Vector3(poleScale, poleScale / 2, 0), new Quaternion())));
	}
	
	/**
	 * Turns the modelInstance around the y-axis
	 * @param y - the y-axis
	 */
	public void turnY(float y) {
		modelInstance.transform.rotate(0, 1, 0, y);
		
		setCollisionTransform(true);
	}
	
	/**
	 * Turn the mainNode (in this case the head) around the x-axis. The mainNode is also followed by the camera as well as the modelInstance
	 * @param x - the x-axis
	 */
	public void turnX(float x) {
		float pitch = camMatrix.getRotation(new Quaternion()).getPitch();
		
		if(Math.abs(pitch + x) < 38)
			camMatrix.rotate(1, 0, 0, x);
	}
	
	public void walk(Vector3 dir) {
		if(running && !isAiming() && !isShooting()) {
			run(dir);
			return;
		}
		
		dir.x *= MAX_WALKING_VELOCITY;
		dir.z *= MAX_WALKING_VELOCITY;
		dir.y = 0;
		
		modelInstance.transform.trn(dir);
		setCollisionTransform(true);
		
		walking = true;
		
		if(isMainPlayer())
			moveVec = dir;
	}
	
	public void run(Vector3 dir) {
		if (!isAiming() && !isShooting()) {
			dir.x *= MAX_RUNNING_VELOCITY;
			dir.z *= MAX_RUNNING_VELOCITY;
			dir.y = 0;

			modelInstance.transform.trn(dir);
			setCollisionTransform(true);

			running = true;
			
			if(isMainPlayer())
				moveVec = dir;
		}
		else walk(dir);
	}
	
	/**
	 * When left mouse button is pressed (or button for left hand)
	 */
	public void interactWithBallL() {
		if (!isShooting()) {
			if (leftHoldingBall || rightHoldingBall) {
				dribbleL = true;
			} else if (!map.getBall().isGrounded()) {
				if (!running)
					leftPointBall = true;

				if (!leftHoldingBall && leftHandBall)
					catchBall(true);
			} else if (ballColl)
				catchBall(true);
		}
	}
	
	public void interactWithBallR() {
		if (!isShooting()) {
			if (rightHoldingBall || leftHoldingBall) {
				dribbleR = true;
			} else if (!map.getBall().isGrounded()) {
				if (!running)
					rightPointBall = true;

				if (!rightHoldingBall && rightHandBall)
					catchBall(false);
			} else if (ballColl)
				catchBall(false);
		}
	}
	
	public void switchDribble() {
		if(leftHoldingBall)
			interactWithBallR();
		else if(rightHoldingBall)
			interactWithBallL();
	}
	
	public void shootPowerScroll(float value) {
		if(value > 0)
			shootingPower = (int) Math.min(20, shootingPower + value);
		
		else if(value < 0)
			shootingPower = (int) Math.max(10, shootingPower + value);
	}
	
	private void catchBall(boolean left) {
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

			if (!downBody)
				disableUpperBodyDynColl();

			map.getBall().getMainBody().setGravity(new Vector3());

			map.setHoldingPlayer(this);
		}
	}
	
	private void enableHandDynColl(boolean left) {
		if(left) {
			map.addRigidBody(bodiesMap.get("shoulderL"), bodiesMap.get("shoulderL").getContactCallbackFlag(), bodiesMap.get("shoulderL").getContactCallbackFilter());
			map.addRigidBody(bodiesMap.get("arm1L"), bodiesMap.get("arm1L").getContactCallbackFlag(), bodiesMap.get("arm1L").getContactCallbackFilter());
			map.addRigidBody(bodiesMap.get("elbowL"), bodiesMap.get("elbowL").getContactCallbackFlag(), bodiesMap.get("elbowL").getContactCallbackFilter());
			map.addRigidBody(bodiesMap.get("arm2L"), bodiesMap.get("arm2L").getContactCallbackFlag(), bodiesMap.get("arm2L").getContactCallbackFilter());
			map.addRigidBody(bodiesMap.get("handL"), bodiesMap.get("handL").getContactCallbackFlag(), bodiesMap.get("handL").getContactCallbackFilter());
			
			leftHandInWorld = true;
		}else {
			map.addRigidBody(bodiesMap.get("shoulderR"), bodiesMap.get("shoulderR").getContactCallbackFlag(), bodiesMap.get("shoulderR").getContactCallbackFilter());
			map.addRigidBody(bodiesMap.get("arm1R"), bodiesMap.get("arm1R").getContactCallbackFlag(), bodiesMap.get("arm1R").getContactCallbackFilter());
			map.addRigidBody(bodiesMap.get("elbowR"), bodiesMap.get("elbowR").getContactCallbackFlag(), bodiesMap.get("elbowR").getContactCallbackFilter());
			map.addRigidBody(bodiesMap.get("arm2R"), bodiesMap.get("arm2R").getContactCallbackFlag(), bodiesMap.get("arm2R").getContactCallbackFilter());
			map.addRigidBody(bodiesMap.get("handR"), bodiesMap.get("handR").getContactCallbackFlag(), bodiesMap.get("handR").getContactCallbackFilter());
			
			rightHandInWorld = true;
		}
	}
	
	/**
	 * disables the dynamic collisions for one of the player's hands
	 * @param left - if true, left hand dynamic collision will be stopped, otherwise - right hand collisions
	 */
	private void disableHandDynColl(boolean left) {
		if(left) {
			map.removeRigidBody(bodiesMap.get("shoulderL"));
			map.removeRigidBody(bodiesMap.get("arm1L"));
			map.removeRigidBody(bodiesMap.get("elbowL"));
			map.removeRigidBody(bodiesMap.get("arm2L"));
			map.removeRigidBody(bodiesMap.get("handL"));
			
			leftHandInWorld = false;
		}else {
			map.removeRigidBody(bodiesMap.get("shoulderR"));
			map.removeRigidBody(bodiesMap.get("arm1R"));
			map.removeRigidBody(bodiesMap.get("elbowR"));
			map.removeRigidBody(bodiesMap.get("arm2R"));
			map.removeRigidBody(bodiesMap.get("handR"));
			
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
	
	private void throwBall() {
		Quaternion dir = modelInstance.transform.getRotation(new Quaternion());
		Vector3 tempVec = new Vector3(0, 0, 1);
		tempVec.rotate(dir.getYaw(), 0, 1, 0);

		tempVec.y = (-camMatrix.getRotation(new Quaternion()).getPitch() / 100) * 2;
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
	}
	
	/**
	 * Used by the AI
	 * @param target
	 */
	public void throwBall(Vector3 target) {
		Vector3 tempVec = target.cpy();
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
	}
	
	private void releaseBall(Matrix4 trans) {
		map.getBall().getMainBody().setGravity(map.getDynamicsWorld().getGravity());
		map.getBall().getMainBody().activate();
		map.getBall().setCollisionTransform(true);
		map.getBall().setWorldTransform(trans);
		
		leftHoldingBall = rightHoldingBall = false;
		
		if(!isDribbling())
			map.playerReleaseBall();
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
	private Matrix4 prevTrans;
	
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
						cycleTimeout = 0;
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
						
						//The difference between the player's previous translation
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
					if(cycleTimeout * delta > 0.85f) {
						dribbleL = false;
						dribbleR = false;
						readyBall = false;
						cycleTimeout = 0;
					}
					else if (!readyBall && cycleTimeout * delta > 0.15f) {
						readyBall = true;
					} else {
						cycleTimeout++;
					}
				}
			}
			else
				map.getBall().setWorldTransform(bodiesMap.get("hand" + primaryId).getWorldTransform().cpy());
		}
		
	}
	
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
		
		Quaternion newHandRot = new Quaternion().setFromAxis(newHandRotVec, 180);
		//System.out.println(Math.min(0, newHandRot.getRoll()));
		
		newHandRot.setEulerAngles(newHandRot.getYaw(), Math.max(0, newHandRot.getPitch()), newHandRot.getRoll());
		
		Matrix4 tempLocal = modelInstance.getNode("shoulder" + id).localTransform;
		modelInstance.getNode("shoulder" + id).isAnimated = true;
		tempLocal.set(tempLocal.getTranslation(new Vector3()), newHandRot).rotate(0, 1, 0, 180);
		
		modelInstance.calculateTransforms();
		
		//setCollisionTransform();
	}
	
	/**
	 * Gets the distance between this player and the target vector
	 * @param target
	 * @return
	 */
	private Vector3 distance(Vector3 target) {
		return target.cpy().sub(modelInstance.transform.getTranslation(new Vector3()));
	}
	
	public void lookAt(Vector3 target) {
		Vector3 thisVec = modelInstance.transform.getTranslation(new Vector3());
		
		Vector3 rotVec = target.cpy().sub(thisVec).nor();
		
		Matrix4 calcTrans = modelInstance.transform.cpy().setToLookAt(rotVec, new Vector3(0, -1, 0));
		
		Quaternion quat = new Quaternion();
		calcTrans.getRotation(quat);
		quat.setEulerAngles(quat.getYaw(), 0, 0);
		
		modelInstance.transform.set(thisVec, quat).rotate(0, 1, 0, 180);
		
		Quaternion quat2 = new Quaternion();
		calcTrans.getRotation(quat2);
		quat2.setEulerAngles(0, quat2.getPitch(), 0);
		
		Vector3 camVec = camMatrix.getTranslation(new Vector3());
		camMatrix.set(camVec, quat2);
		
		//setCollisionTransform();
	}
	
	/**
	 * Moves the player to the according to the given arguments using the "run" and "walk" commands.
	 * 
	 * @param target - the location which the player should follow
	 * @param block
	 * @param xDist - what distance on x-axis the player should keep from the target
	 * @param zDist - what distance on z-axis the player should keep from the target
	 * @param forceSprint - when true, the player will sprint to the target no matter how far away it is from it
	 * @param forceWalk - when true, the player will only walk to the target no matter how far away it is from it
	 * @return The calculated target point including the x and z distance. It is used to be put inside the lookAt method as it is NOT being called by this one.
	 */
	public Vector3 roamAround(Matrix4 target, Matrix4 block, float xDist, float zDist, boolean forceSprint, boolean forceWalk) {
		//The point's trans where the player should go to
		Matrix4 temp = target.cpy().mul(new Matrix4().setToTranslation(xDist, 0, zDist));
		
		//The translation of the point
		Vector3 tempVec = new Vector3();
		temp.getTranslation(tempVec);
		
		//The translation of the target
		Vector3 targetVec = new Vector3();
		target.getTranslation(targetVec);
		
		//The vector used in players walking + distance measurment between the point and the player
		Vector3 diffWalk = distance(tempVec);
		
		//Distance between this player and the target one
		Vector3 diffDist = distance(targetVec);
		
		float xDiff = diffDist.x;
		float zDiff = diffDist.z;
		diffWalk.nor().scl(Gdx.graphics.getDeltaTime());
		if(!forceWalk && (forceSprint || (Math.abs(xDiff) + Math.abs(zDiff) > xDist + zDist + 6)))
			run(diffWalk);
		else if(Math.abs(xDiff) > Math.abs(xDist) + 0.5f || Math.abs(zDiff) > Math.abs(zDist) + 0.5f)
			walk(diffWalk);
			
		return targetVec;
	}
	
	@Override
	public void update(float delta) {
		lockRotationAndRandomFloating(true);
		
		if(!isMainPlayer())
			moveVec.setZero();
		
		if(!holdingBall()) {
			dribbleL = dribbleR = false;
			leftHoldingBall = rightHoldingBall = false;
		}
		
		else if(!leftHoldingBall && !rightHoldingBall && !isDribbling()) {
			map.playerReleaseBall();
			map.getBall().getMainBody().setGravity(map.getDynamicsWorld().getGravity());
		}
		
		if(!isMainPlayer()) {
			steering.setZero();
			brain.update();
			
			//Vector3 tempVec = moveVec.add(new Vector3(steering.linear.cpy().x, 0, steering.linear.cpy().y)).scl(0.5f);
			//float tempAng = steering.angular;
			
			
			System.out.println(moveVec.x + " ; " + moveVec.y + " ; " + moveVec.z);
			if(!moveVec.isZero())
				walk(moveVec);
			
			//System.out.println(getWidth() * getDepth());
		}
		
		String prevIdArmL = armLController.current.animation.id;
		String prevIdArmR = armRController.current.animation.id;
		
		//if(Math.abs(getMainBody().getLinearVelocity().x) < 0.0008f && Math.abs(getMainBody().getLinearVelocity().z) < 0.0008f) {
			//walking = running = false;
		//}
		
		if (dribbleL) {
			dribble(delta, true);
		} else if (dribbleR) {
			dribble(delta, false);
		}
		else if (leftHoldingBall) {
			map.getBall().setWorldTransform(bodiesMap.get("handL").getWorldTransform().cpy());

			if (leftAimBall) {
				float transistion = 0.25f;
				if (!armLController.current.animation.id.equals("aimLArmL")) {
					armLController.animate("aimLArmL", transistion);
					armLController.setAnimation("aimLArmL", -1, new AnimationListener() {

						@Override
						public void onEnd(AnimationDesc animation) {}

						@Override
						public void onLoop(AnimationDesc animation) {
							if (armLController.transitionCurrentTime >= armLController.transitionTargetTime)
								readyBall = true;
						}

					});
				}

				if (!armRController.current.animation.id.equals("aimLArmR")) {
					armRController.animate("aimLArmR", transistion);
					animateArmR("aimL");
				}

				if (!bodyController.current.animation.id.equals("aimLBody")) {
					bodyController.animate("aimLBody", transistion);
					animateBody("aimL");
				}
			}

			else if (readyBall) {
				leftThrowBall = true;
				readyBall = false;
				
				armLController.animate("throwLArmL", 0.25f);
				armLController.setAnimation("throwLArmL", 1, new AnimationListener() {

					@Override
					public void onEnd(AnimationDesc animation) {
						if(isMainPlayer())
							throwBall();

						leftThrowBall = false;
						leftHoldingBall = false;
					}

					@Override
					public void onLoop(AnimationDesc animation) {}

				});

				armRController.animate("throwLArmR", 0.25f);
				armRController.setAnimation("throwLArmR", 1);

				bodyController.animate("throwLBody", 0.25f);
				bodyController.setAnimation("throwLBody", 1);
			}

			else if (!leftThrowBall && !armLController.current.animation.id.equals("dribbleIdleArmL") && !armLController.current.animation.id.equals("aimLArmL")) {
				armLController.animate("dribbleIdleArmL", 0.15f);
				animateArmL("dribbleIdle");
			}
		}
		
		else if(rightHoldingBall) {
			map.getBall().setWorldTransform(bodiesMap.get("handR").getWorldTransform().cpy());

			if (rightAimBall) {
				float transistion = 0.25f;
				if (!armLController.current.animation.id.equals("aimRArmL")) {
					armLController.animate("aimRArmL", transistion);
					animateArmL("aimR");
				}

				if (!armRController.current.animation.id.equals("aimRArmR")) {
					armRController.animate("aimRArmR", transistion);
					armRController.setAnimation("aimRArmR", -1, new AnimationListener() {

						@Override
						public void onEnd(AnimationDesc animation) {}

						@Override
						public void onLoop(AnimationDesc animation) {
							if (armRController.transitionCurrentTime >= armRController.transitionTargetTime)
								readyBall = true;
						}

					});
				}

				if (!bodyController.current.animation.id.equals("aimRBody")) {
					bodyController.animate("aimRBody", transistion);
					animateBody("aimR");
				}
			}

			else if (readyBall) {
				rightThrowBall = true;
				readyBall = false;
				
				armLController.animate("throwRArmL", 0.25f);
				armLController.setAnimation("throwRArmL", 1);

				armRController.animate("throwRArmR", 0.25f);
				armRController.setAnimation("throwRArmR", 1, new AnimationListener() {

					@Override
					public void onEnd(AnimationDesc animation) {
						if(isMainPlayer())
							throwBall();

						rightThrowBall = false;
						rightHoldingBall = false;
					}

					@Override
					public void onLoop(AnimationDesc animation) {}

				});

				bodyController.animate("throwRBody", 0.25f);
				bodyController.setAnimation("throwRBody", 1);
			}

			else if (!rightThrowBall && !armRController.current.animation.id.equals("dribbleIdleArmR") && !armRController.current.animation.id.equals("aimRArmR")) {
				armRController.animate("dribbleIdleArmR", 0.15f);
				animateArmR("dribbleIdle");
			}
		}
		
		else if (!ballColl) {
			if (!leftHandInWorld) {
				if (cycleTimeout > 5) {
					enableHandDynColl(true);
					enableUpperBodyDynColl();
					
					cycleTimeout = 0;
				}
				else
					cycleTimeout++;
			}

			else if (!rightHandInWorld) {
				if (cycleTimeout > 5) {
					enableHandDynColl(false);
					enableUpperBodyDynColl();
					
					cycleTimeout = 0;
				}
				else
					cycleTimeout++;
			}
		}
		else if(!leftHandInWorld || !rightHandInWorld)
			cycleTimeout = 0;
		
		
		if(leftPointBall)
			point(true);
		
		if(rightPointBall)
			point(false);
		
		
		float prevTime = armLController.current.time;
		if (walking) {
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
		
		else if(running) {
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
			if (!prevIdArmL.equals("aimLArmL") && !prevIdArmL.equals("aimRArmL")) {
				
				if (!leftHoldingBall && !prevIdArmL.equals("stayArmL") && !dribbleL && !prevIdArmL.contains("dribble") && !leftPointBall) {
					armLController.animate("stayArmL", 0.25f);
					armLController.setAnimation("stayArmL", -1, 1, new AnimationListener() {

						@Override
						public void onEnd(AnimationDesc animation) {}

						@Override
						public void onLoop(AnimationDesc animation) {}

					});

					if (prevTime > modelInstance.getAnimation(prevIdArmL).duration / 2) {
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

					if (prevTime > modelInstance.getAnimation(prevIdArmL).duration / 2) {
						armRController.current.time = modelInstance.getAnimation("stayArmR").duration / 2;
					}
				}
				stopBodyAnim();
			}
			
			legLController.animate("idle", 0.15f);
			legRController.animate("idle", 0.15f);
			stopLegsAnim();
		}
		
		armLController.update(delta);
		armRController.update(delta);
		legLController.update(delta);
		legRController.update(delta);
		bodyController.update(delta);
		
		prevTrans = modelInstance.transform.cpy();
		
		//if(this instanceof Opponent && (northSurround || southSurround || eastSurround || westSurround))
			//System.out.println("Surround" + northSurround + ";" + southSurround + ";" + eastSurround + ";" + westSurround);
	}
	
	@Override
	public Vector3 getLinearVelocity() {
		return moveVec;
	}
	
	@Override
	public int findNeighbors(ProximityCallback<Vector3> callback) {
		if(callback.equals(brain.getSeparate())) {
			if(callback.reportNeighbor(map.getBall()))
				return 1;
			else return 0;
		}
		
		return super.findNeighbors(callback);
	}

	public Matrix4 getCamMatrix() {
		return camMatrix;
	}
	
	public void setMoveVector(Vector3 move) {
		moveVec = move;
	}
	
	public Vector3 getMoveVector() {
		return moveVec;
	}
	
	public void setRunning() {
		running = true;
	}
	
	public Brain getBrain() {
		return brain;
	}

	@Override
	public float getWidth() {
		return scale6 * 2 + scale2;
	}

	@Override
	public float getHeight() {
		return model.getNode("head").translation.y + scale1 - model.getNode("leg2L").translation.y + scale5 / 2;
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
		}else if(outId.equals(EntityType.TEAMMATE.getId() + "Obj") || outId.equals(EntityType.OPPONENT.getId() + "Obj")) {
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
				westObstacle = true;
		}else if(this instanceof Opponent && outId.equals(ObjectType.HOMEBASKET.getId() + "Zone") || this instanceof Teammate && outId.equals(ObjectType.AWAYBASKET.getId() + "Zone")) {
			inBasketZone = true;
		}
	}
	
	
	//All the commands and indicators should be cleared.
	@Override
	public void onCycleEnd() {
		super.onCycleEnd();
		
		walking = false;
		running = false;
		//jumping = false;
		ballColl = false;
		rightHandBall = false;
		leftHandBall = false;
		leftAimBall = false;
		rightAimBall = false;
		leftPointBall = false;
		rightPointBall = false;
		northSurround = false;
		southSurround = false;
		eastSurround = false;
		westSurround = false;
		northObstacle = false;
		southObstacle = false;
		eastObstacle = false;
		westObstacle = false;
		inBasketZone = false;
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
	
	public void setPlayerIndex(int playerIndex) {
		this.playerIndex = playerIndex;
	}
	
	public int getPlayerIndex() {
		return playerIndex;
	}
	
	public int getShootingPower() {
		return shootingPower;
	}
	
	public boolean holdingBall() {
		
		//System.out.println(map.getCurrentPlayerHoldTeam());
		//System.out.println("Not holding");
		return //(dribbleL || dribbleR) || 
				((this instanceof Teammate && map.getTeammates().indexOf(this) == map.getCurrentPlayerHoldTeam()) || 
						(this instanceof Opponent && map.getOpponents().indexOf(this) == map.getCurrentPlayerHoldOpp()));
	}
	
	public boolean isDribbling() {
		return dribbleL || dribbleR;
	}
	
	public boolean isAiming() {
		return leftAimBall || rightAimBall;
	}
	
	public boolean isShooting() {
		return leftThrowBall || rightThrowBall;
	}
	
	public boolean isPointing() {
		return leftPointBall || rightPointBall;
	}
	
	public boolean leftHolding() {
		return leftHoldingBall;
	}
	
	public boolean rightHolding() {
		return rightHoldingBall;
	}
	
	public boolean isNorthSurround() {
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
	}
	
	public boolean isInAwayBasketZone() {
		return inBasketZone;
	}
	
	public boolean isBehindBasket() {
		return Math.abs(modelInstance.transform.getTranslation(new Vector3()).z) - Math.abs(map.getHomeBasket().getModelInstance().transform.getTranslation(new Vector3()).z) >= -0.5f;
	}

	public boolean isMainPlayer() {
		return map.getMainPlayer().equals(this);
	}
	
}
