package com.gamesbg.bkbl.gamespace.entities;

import java.util.ArrayList;
import java.util.HashMap;

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

public abstract class Player extends Entity {

	static final float MAX_WALKING_VELOCITY = 4;
	static final float MAX_RUNNING_VELOCITY = 11;
	//static final float JUMPING_VELOCITY = 15;
	//static final float JUMPING_TIME = 1.5f;
	
	Node camNode;
	
	HashMap<String, btRigidBody> bodiesMap;
	
	AnimationController armLController;
	AnimationController armRController;
	AnimationController legLController;
	AnimationController legRController;
	AnimationController bodyController;
	
	static final float scale1 = 0.5f;
	static final float scale2 = 1;
	static final float scale3 = 0.15f;
	static final float scale4 = 0.23f;
	static final float scale5 = 0.75f;
	static final float scale6 = 0.35f;
	static final float scale7 = 0.315f;
	static final float handPercentage = 0.4f;
	
	boolean walking, running, jumping;
	boolean leftHoldingBall, rightHoldingBall;
	boolean leftAimBall, rightAimBall;
	boolean leftThrowBall, rightThrowBall, leftReadyBall, rightReadyBall;
	boolean dribbleL, dribbleR;
	boolean ballColl; 
	boolean leftHandBall, rightHandBall;
	boolean leftHandInWorld, rightHandInWorld;
	boolean downBody;
	
	int shootingPower = 10;
	int cycleTimeout;
	
	@Override
	public void create(EntityType type, GameMap map, Vector3 pos) {
		super.create(type, map, pos);
		
		armLController = new AnimationController(modelInstance);
		armRController = new AnimationController(modelInstance);
		legLController = new AnimationController(modelInstance);
		legRController = new AnimationController(modelInstance);
		bodyController = new AnimationController(modelInstance);
		//controller.setAnimation("walk",-1);
		armLController.setAnimation("stayArmL", -1, 1, new AnimationListener() {

			@Override
			public void onEnd(AnimationDesc animation) {
				
			}

			@Override
			public void onLoop(AnimationDesc animation) {
				
			}
			
		});
		
		armRController.setAnimation("stayArmR", -1, 1, new AnimationListener() {

			@Override
			public void onEnd(AnimationDesc animation) {
				
			}

			@Override
			public void onLoop(AnimationDesc animation) {
				
			}
			
		});
		
		stopLegsAnim();
		stopBodyAnim();

	}
	
	private void animateModels() {
		CustomAnimation custom = new CustomAnimation(model);
		
		//We needed an idle animation for all of the body parts that don't act in any way and also to animate transitions between animation and idle staying
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
		
		//float tempX = 1, tempY = 1, tempA = 210;
		custom.addNodeAnimation("shoulderR");
		custom.addRotationsKeyFrame(new float[][]{{1, 0, 0, -140}, {0, 1, 0, 110}, {1, 0, 1, 70}}, 0);
		//custom.addRotationsKeyFrame(new float[][]{{1, 0, 0, -90}, {0, 1, 0, 90}, {0, 0, 1, 30}}, 1);
		//custom.addRotationKeyFrame(-2.5f, -3.5f, 0, 180, 0);
		//custom.addRotationKeyFrame(-2.5f, -3.5f, 0, 180, 1);
		//custom.addRotationKeyFrame(tempX, tempY, 0, tempA, 0);
		//custom.addRotationKeyFrame(tempX, tempY, 0, tempA, 1);
		
		
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
		//custom.addRotationsKeyFrame(new float[][]{{1, 0, 0, -140}, {0, 1, 0, -110}, {1, 0, 1, -70}}, 0.01f);
		//custom.addRotationKeyFrame(0, 1, 0, 110, 0);
		//custom.addRotationKeyFrame(1, 0, 0, 30, 0);
		//custom.addRotationKeyFrame(0, 1, 0, 110, 1);
		
		
		custom.addAnimation("aimRArmR", 0.01f);
		
		//float tempX = 1, tempY = 1, tempA = 210;
		custom.addNodeAnimation("shoulderR");
		custom.addRotationKeyFrame(0, 1.2f, 1.4f, -90, 0);
		//custom.addRotationKeyFrame(1, 0, 0, 30, 0);
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
		
		
		custom.addAnimation("dribblePhase1ArmL", 0.25f);
		
		custom.addNodeAnimation("shoulderL");
		custom.addRotationKeyFrame(1, 0, 0, -10, 0);
		custom.addRotationKeyFrame(1, 0, 0, -10, 0.25f);
		
		custom.addNodeAnimation("elbowL");
		custom.addRotationKeyFrame(1, 0, 0, -80, 0);
		custom.addRotationKeyFrame(1, 0, 0, -50, 0.25f);
		
		
		custom.addAnimation("dribbleIdle1ArmL", 1);
		
		custom.addNodeAnimation("shoulderL");
		custom.addRotationKeyFrame(1, 0, 0, -10, 0);
		custom.addRotationKeyFrame(1, 0, 0, -10, 1);
		
		custom.addNodeAnimation("elbowL");
		custom.addRotationKeyFrame(1, 0, 0, -50, 0);
		custom.addRotationKeyFrame(1, 0, 0, -50, 1);
		
		
		custom.addAnimation("dribblePhase2ArmL", 0.25f);
		
		custom.addNodeAnimation("shoulderL");
		custom.addRotationKeyFrame(1, 0, 0, -10, 0);
		custom.addRotationKeyFrame(1, 0, 0, -10, 0.25f);
		
		custom.addNodeAnimation("elbowL");
		custom.addRotationKeyFrame(1, 0, 0, -50, 0);
		custom.addRotationKeyFrame(1, 0, 0, -80, 0.25f);
		
		
		custom.addAnimation("dribbleIdleArmR", 1);
		
		custom.addNodeAnimation("shoulderR");
		custom.addRotationKeyFrame(1, 0, 0, -10, 0);
		custom.addRotationKeyFrame(1, 0, 0, -10, 1);
		
		custom.addNodeAnimation("elbowR");
		custom.addRotationKeyFrame(1, 0, 0, -80, 0);
		custom.addRotationKeyFrame(1, 0, 0, -80, 1);
		
		
		custom.addAnimation("dribblePhase1ArmR", 0.25f);
		
		custom.addNodeAnimation("shoulderR");
		custom.addRotationKeyFrame(1, 0, 0, -10, 0);
		custom.addRotationKeyFrame(1, 0, 0, -10, 0.25f);
		
		custom.addNodeAnimation("elbowR");
		custom.addRotationKeyFrame(1, 0, 0, -80, 0);
		custom.addRotationKeyFrame(1, 0, 0, -50, 0.25f);
		
		
		custom.addAnimation("dribbleIdle1ArmR", 1);
		
		custom.addNodeAnimation("shoulderR");
		custom.addRotationKeyFrame(1, 0, 0, -10, 0);
		custom.addRotationKeyFrame(1, 0, 0, -10, 1);
		
		custom.addNodeAnimation("elbowR");
		custom.addRotationKeyFrame(1, 0, 0, -50, 0);
		custom.addRotationKeyFrame(1, 0, 0, -50, 1);
		
		
		custom.addAnimation("dribblePhase2ArmR", 0.25f);
		
		custom.addNodeAnimation("shoulderR");
		custom.addRotationKeyFrame(1, 0, 0, -10, 0);
		custom.addRotationKeyFrame(1, 0, 0, -10, 0.25f);
		
		custom.addNodeAnimation("elbowR");
		custom.addRotationKeyFrame(1, 0, 0, -50, 0);
		custom.addRotationKeyFrame(1, 0, 0, -80, 0.25f);
		
		/*custom.addAnimation("jump", 1.5f);
		
		custom.addNodeAnimation("spine3");
		custom.addRotationKeyFrame(0, 0, 0, 0, 0);
		custom.addTranslationKeyFrame(0, 0, 0, 0);
		custom.addRotationKeyFrame(1, 0, 0, 40, 0.2f);
		custom.addTranslationKeyFrame(0, -0.67f, 0, 0.2f);
		custom.addRotationKeyFrame(1, 0, 0, 0, 0.55f);
		custom.addTranslationKeyFrame(0, 0, 0, 0.55f);
		custom.addRotationKeyFrame(1, 0, 0, 0, 0.75f);
		custom.addTranslationKeyFrame(0, 0, 0, 0.75f);
		custom.addRotationKeyFrame(1, 0, 0, 40, 1.25f);
		custom.addTranslationKeyFrame(0, -0.36f, 0, 1.25f);
		custom.addRotationKeyFrame(1, 0, 0, 0, 1.5f);
		custom.addTranslationKeyFrame(0, 0, 0, 1.5f);
		
		custom.addNodeAnimation("shoulderL");
		custom.addRotationKeyFrame(0, 0, 0, 0, 0);
		custom.addRotationKeyFrame(1, 0, 0, 30, 0.2f);
		custom.addRotationKeyFrame(-1, 0, 0, 150, 0.75f);
		custom.addRotationKeyFrame(1, 0, 0, 30, 1.25f);
		custom.addRotationKeyFrame(0, 0, 0, 0, 1.5f);
		
		custom.addNodeAnimation("shoulderR");
		custom.addRotationKeyFrame(0, 0, 0, 0, 0);
		custom.addRotationKeyFrame(1, 0, 0, 30, 0.2f);
		custom.addRotationKeyFrame(-1, 0, 0, 150, 0.75f);
		custom.addRotationKeyFrame(1, 0, 0, 30, 1.25f);
		custom.addRotationKeyFrame(0, 0, 0, 0, 1.5f);
		
		custom.addNodeAnimation("elbowL");
		custom.addRotationKeyFrame(0, 0, 0, 0, 0);
		custom.addRotationKeyFrame(-1, 0, 0, 40, 0.2f);
		custom.addRotationKeyFrame(-1, 0, 0, 90, 0.75f);
		custom.addRotationKeyFrame(-1, 0, 0, 90, 1.25f);
		custom.addRotationKeyFrame(0, 0, 0, 0, 1.5f);
		
		custom.addNodeAnimation("elbowR");
		custom.addRotationKeyFrame(0, 0, 0, 0, 0);
		custom.addRotationKeyFrame(-1, 0, 0, 40, 0.2f);
		custom.addRotationKeyFrame(-1, 0, 0, 90, 0.75f);
		custom.addRotationKeyFrame(-1, 0, 0, 90, 1.25f);
		custom.addRotationKeyFrame(0, 0, 0, 0, 1.5f);
		
		custom.addNodeAnimation("hipL");
		custom.addRotationKeyFrame(0, 0, 0, 0, 0);
		custom.addRotationKeyFrame(-1, 0, 0, 110, 0.2f);
		custom.addRotationKeyFrame(0, 0, 0, 0, 0.55f);
		custom.addRotationKeyFrame(0, 0, 0, 0, 0.75f);
		custom.addRotationKeyFrame(-1, 0, 0, 80, 1.25f);
		custom.addRotationKeyFrame(0, 0, 0, 0, 1.5f);
		
		custom.addNodeAnimation("hipR");
		custom.addRotationKeyFrame(0, 0, 0, 0, 0);
		custom.addRotationKeyFrame(-1, 0, 0, 110, 0.2f);
		custom.addRotationKeyFrame(0, 0, 0, 0, 0.55f);
		custom.addRotationKeyFrame(0, 0, 0, 0, 0.75f);
		custom.addRotationKeyFrame(-1, 0, 0, 80, 1.25f);
		custom.addRotationKeyFrame(0, 0, 0, 0, 1.5f);
		
		custom.addNodeAnimation("kneeL");
		custom.addRotationKeyFrame(0, 0, 0, 0, 0);
		custom.addRotationKeyFrame(1, 0, 0, 70, 0.2f);
		custom.addRotationKeyFrame(0, 0, 0, 0, 0.55f);
		custom.addRotationKeyFrame(0, 0, 0, 0, 0.75f);
		custom.addRotationKeyFrame(1, 0, 0, 40, 1.25f);
		custom.addRotationKeyFrame(0, 0, 0, 0, 1.5f);
		
		custom.addNodeAnimation("kneeR");
		custom.addRotationKeyFrame(0, 0, 0, 0, 0);
		custom.addRotationKeyFrame(1, 0, 0, 70, 0.2f);
		custom.addRotationKeyFrame(0, 0, 0, 0, 0.55f);
		custom.addRotationKeyFrame(0, 0, 0, 0, 0.75f);
		custom.addRotationKeyFrame(1, 0, 0, 40, 1.25f);
		custom.addRotationKeyFrame(0, 0, 0, 0, 1.5f);*/
		
		
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
		//custom.addRotationKeyFrame(1, 0, 0, 0, 0.15f);
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
		//custom.addRotationKeyFrame(1, 0, 0, 0, 0.6f);
		custom.addRotationKeyFrame(-1, 0, 0, 20, 0.75f);
		
		custom.addNodeAnimation("kneeR");
		custom.addRotationKeyFrame(0, 0, 0, 0, 0);
		custom.addRotationKeyFrame(1, 0, 0, 90, 0.15f);
		custom.addRotationKeyFrame(1, 0, 0, 0, 0.6f);
		custom.addRotationKeyFrame(0, 0, 0, 00, 0.75f);
		
		
		custom.addAnimation("walkArmL", 1);
		
		custom.addNodeAnimation("shoulderL");
		/*custom.addTranslationKeyFrame(1, 1, 1);
		custom.addTranslationKeyFrame(2, 0, 2);
		custom.addTranslationKeyFrame(3, 3, 3);*/
		custom.addRotationKeyFrame(0, 0, 0, 0, 0);
		custom.addRotationKeyFrame(0.5f, 0, 0, 30, 0.25f);
		custom.addRotationKeyFrame(0, 0, 0, 50, 0.5f);
		custom.addRotationKeyFrame(-0.5f, 0, 0, 30, 0.75f);
		custom.addRotationKeyFrame(0, 0, 0, 50, 1);
		
		custom.addNodeAnimation("elbowL");
		custom.addRotationKeyFrame(0, 0, 0, 0, 0);
		//custom.addRotationKeyFrame(0, 0, 0, 50, 0.75f);
		custom.addRotationKeyFrame(0, 0, 0, 0, 0.5f);
		custom.addRotationKeyFrame(-0.25f, 0, 0, 60, 0.75f);
		//custom.addRotationKeyFrame(-0.25f, 0, 0, 50, 2.25f);
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
		int divisionU = 20;
		int divisionV = 20;
		
		ModelBuilder mb = new ModelBuilder();
		ModelBuilder childMB = new ModelBuilder();
		Material material = new Material(ColorAttribute.createDiffuse(getPlayerColor()));
		
		//Matrix4 temp = new Matrix4();
		//matrixes.add(temp);
		
		//final float scale2 = 1;
		
		mb.begin();
		childMB.begin();
		
		Node head = childMB.node();
		head.id = "head";
		//mb.part("sphere", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.GREEN))).sphere(1f, 1f, 1f, 10, 10);
		//mb.createSphere(0.5f, 0.5f, 0.5f, 25, 25, new Material(ColorAttribute.createDiffuse(Color.RED)), VertexAttributes.Usage.Position|VertexAttributes.Usage.Normal);
		//head.translation.set(x, y + scale5 + scale5 / 2 + 0.07f * 2 + scale5 / 2 + scale3 * 7 + scale1, z);
		head.translation.set(0, scale1 / 2 + scale3 / 2 + 0.1f, 0);
		SphereShapeBuilder.build(childMB.part(head.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale1, scale1, scale1, divisionU, divisionV);
		camNode = head;
		
		Node spine1 = childMB.node();
		spine1.id = "spine1";
		//spine1.translation.set(x, y + scale5 + scale5 / 2 + 0.07f * 2 + scale5 / 2 + scale3 * 7, z);
		spine1.translation.set(0, scale3 * 3.5f, 0);
		//spine1.rotation.set(0, 0, 0, 0);
		//mb.createSphere(1.5f, 0.15f, 0.23f, 25, 25, new Material(ColorAttribute.createDiffuse(Color.RED)), VertexAttributes.Usage.Position|VertexAttributes.Usage.Normal);
		BoxShapeBuilder.build(childMB.part(spine1.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale2, scale3, scale4);
		spine1.addChild(head);
		
		Node spine2 = childMB.node();
		spine2.id = "spine2";
		//spine2.translation.set(x, y + scale5 + scale5 / 2 + 0.07f * 2 + scale5 / 2 + scale3 * 4, z);
		spine2.translation.set(0, scale3 * 3.5f, 0);
		BoxShapeBuilder.build(childMB.part(spine2.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale2, scale3, scale4);
		spine2.addChild(spine1);
		
		Node spine3 = mb.node();
		spine3.id = "spine3";
		//spine3.translation.set(0, scale5 + scale5 / 2 + 0.07f * 2 + scale5 / 2 + scale3, 0);
		BoxShapeBuilder.build(mb.part(spine3.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale2, scale3, scale4);
		spine3.addChild(spine2);
		
		Node shoulderL = childMB.node();
		shoulderL.id = "shoulderL";
		//shoulderL.translation.set(x + scale2 / 2 + scale3 / 2 + 0.09f, y + scale5 + scale5 / 2 + 0.07f * 2 + scale5 / 2 + scale3 * 7, z);
		shoulderL.translation.set(scale2 / 2 + scale6 / 2, 0, 0);
		SphereShapeBuilder.build(childMB.part(shoulderL.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale6, scale6, scale6, divisionU, divisionV);
		spine1.addChild(shoulderL);
		
		Node arm1L = childMB.node();
		arm1L.id = "arm1L";
		shoulderL.addChild(arm1L);
		//arm1L.translation.set(x + scale2 / 2 + scale3 / 2 + 0.09f, y + scale5 + 0.07f * 2 + scale5 / 2 + scale3 * 7 + scale3 / 2, z);
		arm1L.translation.set(0, -scale5 / 2, 0);
		BoxShapeBuilder.build(childMB.part(arm1L.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale5, scale4);
		
		Node elbowL = childMB.node();
		elbowL.id = "elbowL";
		arm1L.addChild(elbowL);
		//elbowL.translation.set(x + scale2 / 2 + scale3 / 2 + 0.09f, y + scale5 + 0.07f * 2 + scale3 * 7, z - scale3 * scale5);
		elbowL.translation.set(0, -scale5 / 2 - scale3 / 2, -scale3 * scale5);
		SphereShapeBuilder.build(childMB.part(elbowL.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale3, scale3, divisionU, divisionV);
		
		//float handPercentage = 0.4f;
		
		Node arm2L = childMB.node();
		arm2L.id = "arm2L";
		elbowL.addChild(arm2L);
		//arm2L.translation.set(x + scale2 / 2 + scale3 / 2 + 0.09f, y + scale5 + 0.07f * 2 + scale3 * 4, z);
		arm2L.translation.set(0, -scale5 * (1 - handPercentage) / 2 - scale3 / 2, scale3 * scale5);
		BoxShapeBuilder.build(childMB.part(arm2L.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale5 * (1 - handPercentage), scale4);
		
		Node handL = childMB.node();
		handL.id = "handL";
		arm2L.addChild(handL);
		//arm2L.translation.set(x + scale2 / 2 + scale3 / 2 + 0.09f, y + scale5 + 0.07f * 2 + scale3 * 4, z);
		handL.translation.set(0, -scale5 / 2, 0);
		BoxShapeBuilder.build(childMB.part(handL.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale5 * handPercentage, scale4);
		
		/*Node handL = mb.node();
		handL.id = "handL";
		handL.translation.set(x + 1.5f / 2 + scale3 / 2 + 0.09f, y + 0.75f + 0.07f * 2 + 0.15f * 4 - 0.75f / 2 - scale3, z);
		BoxShapeBuilder.build(mb.part(handL.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale3, 0.23f);*/
		
		Node shoulderR = childMB.node();
		shoulderR.id = "shoulderR";
		//shoulderR.translation.set(x - scale2 / 2 - scale3 / 2 - 0.09f, y + scale5 + scale5 / 2 + 0.07f * 2 + scale5 / 2 + scale3 * 7, z);
		shoulderR.translation.set(-scale2 / 2 - scale6 / 2, 0, 0);
		SphereShapeBuilder.build(childMB.part(shoulderR.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale6, scale6, scale6, divisionU, divisionV);
		spine1.addChild(shoulderR);
		
		Node arm1R = childMB.node();
		arm1R.id = "arm1R";
		shoulderR.addChild(arm1R);
		//arm1R.translation.set(x - scale2 / 2 - scale3 / 2 - 0.09f, y + scale5 + 0.07f * 2 + scale5 / 2 + scale3 * 7 + scale3 / 2, z);
		arm1R.translation.set(0, -scale5 / 2, 0);
		BoxShapeBuilder.build(childMB.part(arm1R.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale5, scale4);
		
		Node elbowR = childMB.node();
		elbowR.id = "elbowR";
		arm1R.addChild(elbowR);
		//elbowR.translation.set(x - scale2 / 2 - scale3 / 2 - 0.09f, y + scale5 + 0.07f * 2 + scale3 * 7, z - scale3 * scale5);
		elbowR.translation.set(0, -scale5 / 2 - scale3 / 2, -scale3 * scale5);
		SphereShapeBuilder.build(childMB.part(elbowR.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale3, scale3, divisionU, divisionV);
		
		Node arm2R = childMB.node();
		arm2R.id = "arm2R";
		elbowR.addChild(arm2R);
		//arm2R.translation.set(x - scale2 / 2 - scale3 / 2 - 0.09f, y + scale5 + 0.07f * 2 + scale3 * 4, z);
		arm2R.translation.set(0, -scale5 * (1 - handPercentage) / 2 - scale3 / 2, scale3 * scale5);
		BoxShapeBuilder.build(childMB.part(arm2R.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale5 * (1 - handPercentage), scale4);
		
		Node handR = childMB.node();
		handR.id = "handR";
		arm2R.addChild(handR);
		//arm2L.translation.set(x + scale2 / 2 + scale3 / 2 + 0.09f, y + scale5 + 0.07f * 2 + scale3 * 4, z);
		handR.translation.set(0, -scale5 / 2, 0);
		BoxShapeBuilder.build(childMB.part(handR.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale5 * handPercentage, scale4);
		/*Node handR = mb.node();
		handR.id = "handR";
		BoxShapeBuilder.build(mb.part(handR.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale3, scale3);*/
		
		Node hipL = childMB.node();
		hipL.id = "hipL";
		//hipL.translation.set(x + scale2 / 2 - scale3 / 2, y + scale5 + scale5 / 2 + 0.07f * 2 + scale5 / 2 + scale3, z);
		hipL.translation.set(scale2 / 2 - scale3 / 2, 0, 0);
		SphereShapeBuilder.build(childMB.part(hipL.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale7, scale7, scale7, divisionU, divisionV);
		spine3.addChild(hipL);
		
		Node leg1L = childMB.node();
		leg1L.id = "leg1L";
		hipL.addChild(leg1L);
		//leg1L.translation.set(x + scale2 / 2 - scale3 / 2, y + scale5 + scale5 / 2 + 0.07f * 2, z);
		leg1L.translation.set(0, -scale5 / 2 - scale7 / 3, 0);
		BoxShapeBuilder.build(childMB.part(leg1L.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale5, scale4);
		
		Node kneeL = childMB.node();
		kneeL.id = "kneeL";
		leg1L.addChild(kneeL);
		//kneeL.translation.set(x + scale2 / 2 - scale3 / 2, y + scale5 + scale3 / 2, z + scale3 * scale5);
		kneeL.translation.set(0, -scale5 / 2 - scale3 / 2, scale3 * scale5);
		SphereShapeBuilder.build(childMB.part(kneeL.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale3, scale3, divisionU, divisionV);
		
		Node leg2L = childMB.node();
		leg2L.id = "leg2L";
		kneeL.addChild(leg2L);
		//leg2L.translation.set(x + scale2 / 2 - scale3 / 2, y + scale5 / 2, z);
		leg2L.translation.set(0, -scale5 / 2 - scale3 / 2, -scale3 * scale5);
		//leg2L.rotation.setFromAxis(0, 3, 0, 15);
		BoxShapeBuilder.build(childMB.part(leg2L.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale5, scale4);
		
		Node hipR = childMB.node();
		hipR.id = "hipR";
		//hipR.translation.set(x - scale2 / 2 + scale3 / 2, y + scale5 + scale5 / 2 + 0.07f * 2 + scale5 / 2 + scale3, z);
		hipR.translation.set(-scale2 / 2 + scale3 / 2, 0, 0);
		SphereShapeBuilder.build(childMB.part(hipR.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale7, scale7, scale7, divisionU, divisionV);
		spine3.addChild(hipR);
		
		Node leg1R = childMB.node();
		leg1R.id = "leg1R";
		hipR.addChild(leg1R);
		//leg1R.translation.set(x - scale2 / 2 + scale3 / 2, y + scale5 + scale5 / 2 + 0.07f * 2, z);
		leg1R.translation.set(0, -scale5 / 2 - scale7 / 3, 0);
		BoxShapeBuilder.build(childMB.part(leg1R.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale5, scale4);
		
		Node kneeR = childMB.node();
		kneeR.id = "kneeR";
		leg1R.addChild(kneeR);
		//kneeR.translation.set(x - scale2 / 2 + scale3 / 2, y + scale5 + scale3 / 2, z + scale3 * scale5);
		kneeR.translation.set(0, -scale5 / 2 - scale3 / 2, scale3 * scale5);
		SphereShapeBuilder.build(childMB.part(kneeR.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale3, scale3, divisionU, divisionV);
		
		Node leg2R = childMB.node();
		leg2R.id = "leg2R";
		kneeR.addChild(leg2R);
		//leg2R.translation.set(x - scale2 / 2 + scale3 / 2, y + scale5 / 2, z);
		leg2R.translation.set(0, -scale5 / 2 - scale3 / 2, -scale3 * scale5);
		BoxShapeBuilder.build(childMB.part(leg2R.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale5, scale4);
		
		childMB.end();
		model = mb.end();
		
		animateModels();
		modelInstance = new ModelInstance(model, pos);
		//temp = modelInstance.transform;
		//modelInstance.calculateTransforms();
		//modelInstance = new ModelInstance(model);
		
		/*Animation animation = new Animation();
		animation.id = "testAnim";
		animation.duration = 2;
		
		NodeAnimation nodeAnim = new NodeAnimation();
		
		nodeAnim.node = model.getNode("arm1L");
		nodeAnim.translation = new Array<NodeKeyframe<Vector3>>();
		nodeAnim.translation.add(new NodeKeyframe<Vector3>(0, new Vector3(1, 1, 1)));
		nodeAnim.translation.add(new NodeKeyframe<Vector3>(1, new Vector3(2, 0, 2)));
		nodeAnim.translation.add(new NodeKeyframe<Vector3>(2, new Vector3(3, 3, 3)));
		
		animation.nodeAnimations.add(nodeAnim);
		
		model.animations.add(animation);*/
		
		
		//custom.addRotationKeyFrame(-1, 0, 0, 45, 4);
	}
	
	@Override
	protected void createCollisions() {
		//System.out.println(getWidth() + "; " + getHeight() + "; " + getDepth());
		Matrix4 tempHandL = modelInstance.getNode("handL").globalTransform;
		Matrix4 tempHandR = modelInstance.getNode("handR").globalTransform;
		
		Vector3 armVec = new Vector3(scale3 / 2, scale5 * (1 - handPercentage) / 2, scale4 / 2);
		Vector3 handVec = new Vector3(scale3 / 2, scale5 * handPercentage / 2, scale4 / 2);
		
		collisionShapes.add(new btBoxShape(new Vector3(scale2 / 2, getHeight(), getDepth() / 2)));
		matrixes.add(modelInstance.transform);
		
		collisionShapes.add(new btSphereShape(scale6 / 2));
		matrixes.add(modelInstance.getNode("shoulderL").globalTransform);
		collisionShapes.add(new btBoxShape(armVec));
		matrixes.add(modelInstance.getNode("arm1L").globalTransform);
		collisionShapes.add(new btSphereShape(scale3 / 2));
		matrixes.add(modelInstance.getNode("elbowL").globalTransform);
		collisionShapes.add(new btBoxShape(armVec));
		matrixes.add(modelInstance.getNode("arm2L").globalTransform);
		collisionShapes.add(new btBoxShape(handVec));
		matrixes.add(tempHandL);
		
		collisionShapes.add(new btSphereShape(scale6 / 2));
		matrixes.add(modelInstance.getNode("shoulderR").globalTransform);
		collisionShapes.add(new btBoxShape(armVec));
		matrixes.add(modelInstance.getNode("arm1R").globalTransform);
		collisionShapes.add(new btSphereShape(scale3 / 2));
		matrixes.add(modelInstance.getNode("elbowR").globalTransform);
		collisionShapes.add(new btBoxShape(armVec));
		matrixes.add(modelInstance.getNode("arm2R").globalTransform);
		collisionShapes.add(new btBoxShape(handVec));
		matrixes.add(tempHandR);
		
		invisCollShapes = new ArrayList<btCollisionShape>();
		invisCollShapes.add(new btBoxShape(handVec));
		matrixes.add(tempHandL);
		invisCollShapes.add(new btBoxShape(handVec));
		matrixes.add(tempHandR);
		//invisCollShapes.add(new btBoxShape(new Vector3(scale2 / 2, getHeight() / 2, getDepth() / 2)));
		//matrixes.add(matrixes.get(0));
		//invisCollisionShape = new btBoxShape(new Vector3(getWidth(), 0.001f, getDepth()));
		//System.out.println(getWidth() + "; " + getHeight() + "; " + getDepth());
	}
	
	/*private void createDownModelBody() {
		invisBodies = new ArrayList<btRigidBody>();
		
		Vector3 localInertia = new Vector3();
		
		btCollisionShape tempShape = invisCollShapes.get(2);
		
		tempShape.calculateLocalInertia(type.getMass(), localInertia);
		
		MotionState mt = new MotionState();
		mt.transform = modelInstance.transform;
		
		btRigidBody.btRigidBodyConstructionInfo cst = new btRigidBody.btRigidBodyConstructionInfo(type.getMass(), mt, tempShape, localInertia);
		
		invisBodies.add(new btRigidBody(cst));
	}*/
	
	private void createCollisionObjects() {
		collisionObjects = new ArrayList<btCollisionObject>();
		
		collisionObjects.add(new btCollisionObject());
		collisionObjects.get(0).setCollisionShape(invisCollShapes.get(0));
		
		collisionObjects.add(new btCollisionObject());
		collisionObjects.get(1).setCollisionShape(invisCollShapes.get(1));
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
		
		//createDownModelBody();
		
		createCollisionObjects();
		
		removeCollisionCheckOnInternals();
		//manualSetTransforms = new ArrayList<Integer>();
		
		bodiesMap = new HashMap<String, btRigidBody>();
		
		int i = 0;
		
		bodiesMap.put("model", bodies.get(i++));
		
		//manualSetTransforms.add(i);
		//bodiesMap.put("modelDown", invisBodies.get(0));
		
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
	
	/*@Override
	protected void createCollisionObjectAndBodies() {
		super.createCollisionObjectAndBodies();
		
		/*for(int i = 0; i < bodies.size(); i++)
			for(int j = i + 1; j < bodies.size(); j++)
				bodies.get(i).setIgnoreCollisionCheck(bodies.get(j), true);
		//body.setMotionState(motionState);
		
		/*invisBodies = new ArrayList<btRigidBody>();
		invisConstructionInfos = new ArrayList<btRigidBody.btRigidBodyConstructionInfo>();
		invisMotionStates = new ArrayList<GameMap.MotionState>();
		
		btRigidBody.btRigidBodyConstructionInfo constInfo = new btRigidBody.btRigidBodyConstructionInfo(0, null, invisCollisionShape, localInertia);
		invisConstructionInfos.add(constInfo);
		invisBodies.add(new btRigidBody(constInfo));
		
		manuallySetCollTransform();
		
		invisMotionStates.add(new GameMap.MotionState());
		invisMotionStates.get(0).transform = invisBodies.get(0).getWorldTransform();
		invisBodies.get(0).setMotionState(invisMotionStates.get(0));
		//body.setCenterOfMassTransform(new Matrix4().setTranslation(0, 0, 0));
	}*/
	
	/**
	 * Turns the modelInstance around the y-axis
	 * @param y - the y-axis
	 */
	public void turnY(float y) {
		//Quaternion spineRotation = modelInstance.getNode("spine3").rotation; 
		//spineRotation.setFromAxis(0, 1 * (x / Math.abs(x)), 0, spineRotation.getAngle() + Math.abs(x));
		modelInstance.transform.rotate(0, 1, 0, y);
		
		//modelInstance.calculateTransforms();
		//float deg = modelInstance.transform.getRotation(new Quaternion()).getAngle();
		//float dump = 1 * (x / Math.abs(x));
		//System.out.println(deg + " ; " + dump);
		//modelInstance.transform.set(modelInstance.transform.getTranslation(new Vector3()), new Quaternion().setFromAxis(0, 1, 0, modelInstance.transform.getRotation(new Quaternion()).getAngle() + x));
		setCollisionTransform();
	}
	
	/**
	 * Turn the mainNode (in this case the head) around the x-axis. The mainNode is also followed by the camera as well as the modelInstance
	 * @param x - the x-axis
	 */
	public void turnX(float x) {
		Matrix4 mainNodeTrans = camNode.globalTransform;
		float pitch = mainNodeTrans.getRotation(new Quaternion()).getPitch();
		//System.out.println(pitch + x);
		
		if(Math.abs(pitch + x) < 38)
			mainNodeTrans.rotate(1, 0, 0, x);
	}
	
	public void walk(Vector3 dir) {
		dir.x *= MAX_WALKING_VELOCITY;
		dir.z *= MAX_WALKING_VELOCITY;
		
		modelInstance.transform.trn(dir);
		setCollisionTransform();
		
		walking = true;
	}
	
	public void run(Vector3 dir) {
		if (!armLController.current.animation.id.equals("aimLArmL") && !armLController.current.animation.id.equals("throwLArmL")) {
			dir.x *= MAX_RUNNING_VELOCITY;
			dir.z *= MAX_RUNNING_VELOCITY;

			modelInstance.transform.trn(dir);
			setCollisionTransform();

			running = true;
		}
		else walk(dir);
	}
	
	/**
	 * When left mouse button is pressed (or button for left hand)
	 */
	public void interactWithBallL() {
		if(leftHoldingBall || rightHoldingBall) {
			dribbleL = true;
		}
	}
	
	public void interactWithBallR() {
		if(rightHoldingBall || leftHoldingBall) {
			dribbleR = true;
		}
	}
	
	public void interactWithBallE() {
		if (ballColl) {
			/*if(!handLBallColl) {
				if(map.getBall().isGrounded())
					leftHoldingBall = true;
			}else leftHoldingBall = true;*/
			//if (!leftHoldingBall) {
				if (!map.getBall().isGrounded()) {
					if (leftHandBall && !leftHoldingBall) {
						catchBall(true);
						// bodiesMap.get("handL").setContactCallbackFilter(0);
						// bodiesMap.get("arm2L").setContactCallbackFilter(0);
						// bodiesMap.get("elbowL").setContactCallbackFilter(0);
						// bodiesMap.get("arm1L").setContactCallbackFilter(0);//
						// map.getBall().setCopyTransform(bodiesMap.get("handL").getWorldTransform());
					}else if (rightHandBall && !rightHoldingBall) {
						catchBall(false);
						// bodiesMap.get("handL").setContactCallbackFilter(0);
						// bodiesMap.get("arm2L").setContactCallbackFilter(0);
						// bodiesMap.get("elbowL").setContactCallbackFilter(0);
						// bodiesMap.get("arm1L").setContactCallbackFilter(0);//
						// map.getBall().setCopyTransform(bodiesMap.get("handL").getWorldTransform());
					}
				} else {
					catchBall(false);
					// map.getBall().setCopyTransform(bodiesMap.get("handL").getWorldTransform());
				}
			//}
			//else if(!rightHoldingBall) {
				
			//}
		}
		
		//throwBall();
	}
	
	public void shootPowerScroll(float value) {
		if(value > 0)
			shootingPower = (int) Math.min(20, shootingPower + value);
		
		else if(value < 0)
			shootingPower = (int) Math.max(10, shootingPower + value);
	}
	
	private void catchBall(boolean left) {
		if(left) {
			leftHoldingBall = true;
			
			if(leftHandInWorld)
				disableHandDynColl(left);
		}
		else {
			rightHoldingBall = true;
			
			if(rightHandInWorld)
				disableHandDynColl(left);
		}
		
		if(!downBody)
			disableUpperBodyDynColl();
		
		//map.removeRigidBody(map.getBall().getMainBody());
		//map.addRigidBody(map.getBall().getMainBody(), 1, 0);
		//map.getBall().getMainBody().setMassProps(0, map.getBall().getMainBody().getLocalInertia());
		map.getBall().getMainBody().setGravity(new Vector3());
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
		//map.removeRigidBody(bodiesMap.get("model"));
		//map.addRigidBody(bodiesMap.get("modelDown"));
		//bodiesMap.get("model").setIgnoreCollisionCheck(map.getBall().getMainBody(), true);
		
		/*ArrayList<btCollisionObject> tempObj = getAllCollObjects();
		
		for(btCollisionObject obj : tempObj) {
			map.getBall().getMainBody().setIgnoreCollisionCheck(obj, true);
		}*/
		
		map.getBall().getMainBody().setIgnoreCollisionCheck(getMainBody(), true);
		
		downBody = true;
	}
	
	private void enableUpperBodyDynColl() {
		//map.removeRigidBody(bodiesMap.get("modelDown"));
		//map.addRigidBody(bodiesMap.get("model"));
		//bodiesMap.get("model").setIgnoreCollisionCheck(map.getBall().getMainBody(), false);
		//map.getBall().getMainBody().setIgnoreCollisionCheck(bodiesMap.get("model"), false);
		//bodiesMap.get("model").activate();
		
		/*ArrayList<btCollisionObject> tempObj = getAllCollObjects();
		
		for(btCollisionObject obj : tempObj) {
			map.getBall().getMainBody().setIgnoreCollisionCheck(obj, false);
		}*/
		
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
		/*else {
			if (ballColl) {
				/*if(!handLBallColl) {
					if(map.getBall().isGrounded())
						leftHoldingBall = true;
				}else leftHoldingBall = true;

				if (!map.getBall().isGrounded()) {
					if (leftHandBall)
						leftHoldingBall = true;
				} else
					leftHoldingBall = true;
			}
		}*/
		
		//System.out.println(map.getBall().isGrounded());
		
		//if(leftHoldingBall)
			//System.out.println("Ball Collided");
	}
	
	/*public void jump() {
		handLController.setAnimation("jump", 1, new AnimationListener() {

			//This listener might not be necessary!
			
			@Override
			public void onEnd(AnimationDesc animation) {
				
			}

			@Override
			public void onLoop(AnimationDesc animation) {
				
			}

		});
	}*/
	
	private void stopBodyAnim() {
		bodyController.setAnimation("idle", -1, 1, new AnimationListener() {

			@Override
			public void onEnd(AnimationDesc animation) {
				
			}

			@Override
			public void onLoop(AnimationDesc animation) {
				
			}
			
		});
	}
	
	private void stopLegsAnim() {
		legLController.setAnimation("idle", -1, 1, new AnimationListener() {

			@Override
			public void onEnd(AnimationDesc animation) {
				
			}

			@Override
			public void onLoop(AnimationDesc animation) {
				
			}
			
		});
		
		legRController.setAnimation("idle", -1, 1, new AnimationListener() {

			@Override
			public void onEnd(AnimationDesc animation) {
				
			}

			@Override
			public void onLoop(AnimationDesc animation) {
				
			}
			
		});
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
		//System.out.println(dir.getPitch() + "; " + dir.getYaw() + "; " + dir.getRoll());
		Vector3 tempVec = new Vector3(0, 0, 1);
		tempVec.rotate(dir.getYaw(), 0, 1, 0);
		//tempVec.rotate(-camNode.globalTransform.getRotation(new Quaternion()).getPitch(), 1, 0, 0);
		//tempVec.y = -tempVec.y;
		tempVec.y = (-camNode.globalTransform.getRotation(new Quaternion()).getPitch() / 100) * 2;
		tempVec.x *= shootingPower;
		tempVec.y *= shootingPower * 1.4f;
		tempVec.z *= shootingPower;
		//tempVec.scl(shootingPower);
		//tempVec.y *= 2;
		//tempVec.x = tempVec.x;
		//tempVec.z = tempVec.z;
		//System.out.println(camNode.globalTransform.getRotation(new Quaternion()).getPitch());
		System.out.println(tempVec.x + "; " + tempVec.y + "; " + tempVec.z);
		//System.out.println(map.getBall().getMainBody().getInterpolationLinearVelocity().x + "; " + map.getBall().getMainBody().getInterpolationLinearVelocity().y + "; " + map.getBall().getMainBody().getInterpolationLinearVelocity().z);
		//map.getBall().getMainBody().clearForces();
		//map.getBall().getMainBody().applyCentralForce(map.getBall().getMainBody().getInterpolationLinearVelocity().scl(-1));
		//map.getBall().getMainBody().setLinearVelocity(new Vector3());
		//map.getBall().getMainBody().setLinearVelocity(map.getBall().getMainBody().getInterpolationLinearVelocity());
		//map.removeRigidBody(map.getBall().getMainBody());
		//map.addRigidBody(map.getBall().getMainBody(), map.getBall().getMainBody().getContactCallbackFlag(), map.getBall().getMainBody().getContactCallbackFilter());
		
		//System.out.println(map.getBall().getMainBody().getContactCallbackFlag() + "; " + map.getBall().getMainBody().getContactCallbackFilter());
		//System.out.println(map.getObjectsMap().get(map.getBall().getMainBody().getUserValue()));
		
		//map.getBall().getModelInstance().transform = map.getBall().getMainBody().getWorldTransform();
		//map.getBall().getMainBody().setMassProps(map.getBall().getType().getMass(), map.getBall().getMainBody().getLocalInertia());
		/*map.getBall().getMainBody().setGravity(map.getDynamicsWorld().getGravity());
		map.getBall().getMainBody().activate();
		map.getBall().setCollisionTransform();
		if(leftThrowBall)
			map.getBall().setWorldTransform(bodiesMap.get("handL").getWorldTransform().cpy());
		else map.getBall().setWorldTransform(bodiesMap.get("handR").getWorldTransform().cpy());*/
		if(leftThrowBall) {
			releaseBall(bodiesMap.get("handL").getWorldTransform().cpy());
			
			leftThrowBall = false;
		}
		else{
			releaseBall(bodiesMap.get("handR").getWorldTransform().cpy());
			
			rightThrowBall = false;
		}
		
		map.getBall().getMainBody().setLinearVelocity(tempVec);
		
		
		//map.getBall().getMainBody().getInterpolationLinearVelocity();
		//map.getBall().getMainBody().applyCentralForce(tempVec);
		
	}
	
	private void releaseBall(Matrix4 trans) {
		map.getBall().getMainBody().setGravity(map.getDynamicsWorld().getGravity());
		map.getBall().getMainBody().activate();
		map.getBall().setCollisionTransform();
		map.getBall().setWorldTransform(trans);
		
		leftHoldingBall = rightHoldingBall = false;
	}
	
	private Vector3 makeBallDribbleVelocity(boolean left) {
		Vector3 tempVec = new Vector3(0, 0, 1);
		modelInstance.transform.getRotation(new Quaternion()).transform(tempVec);
		if(left)
			tempVec.rotate(90, 0, 1, 0);
		else tempVec.rotate(-90, 0, 1, 0);
		
		tempVec.x *= 3;
		tempVec.z *= 3;
		
		//if(tempVec.y > 0)
			//tempVec.y = 10;
		//else tempVec.y = -10;
		
		return tempVec;
	}
	
	/*private void setBodiesTransform() {
		Matrix4 temp = new Matrix4(modelInstance.transform.val);
		bodiesMap.get("model").setWorldTransform(temp.translate(0, -getHeight() / 2, 0));
	}*/
	
	public void update(float delta) {
		//System.out.println("x: " + modelInstance.animations.get(0).nodeAnimations.get(0).node.globalTransform.getScaleX() + " y: " + modelInstance.animations.get(0).nodeAnimations.get(0).node.globalTransform.getScaleX() + " z: " + modelInstance.animations.get(0).nodeAnimations.get(0).node.globalTransform.getScaleX());
		//System.out.println();
		//super.update(delta);
		//System.out.println(map.getBall().getMainBody().getTotalForce().x + "; " + map.getBall().getMainBody().getTotalForce().y + "; " + map.getBall().getMainBody().getTotalForce().z);
		/*if(controller.current.animation.id.equals("jump"))
			lockRotationAndRandomFloating(false);
		else lockRotationAndRandomFloating(true);*/
		
		//setBodiesTransform();
		//System.out.println(shootingPower);
		lockRotationAndRandomFloating(true);
		
		if (dribbleL) {
			if(rightHoldingBall) {
				if (!armRController.current.animation.id.equals("dribblePhase1ArmR")) {
					armRController.setAnimation("dribblePhase1ArmR", 1, new AnimationListener() {

						@Override
						public void onEnd(AnimationDesc animation) {
							releaseBall(bodiesMap.get("handR").getWorldTransform().cpy());

							map.getBall().getMainBody().setLinearVelocity(makeBallDribbleVelocity(true).add(0, -10, 0));
							disableHandDynColl(true);
							//animateArmL("stayL");
							animateArmR("stay");
						}

						@Override
						public void onLoop(AnimationDesc animation) {

						}

					});

					armLController.animate("dribbleIdle1ArmL", 0.25f);
					armLController.setAnimation("dribbleIdle1ArmL", 1);
				}
				else map.getBall().setWorldTransform(bodiesMap.get("handR").getWorldTransform().cpy());
					
			}
			else if (!armLController.current.animation.id.equals("dribblePhase1ArmL") && !armLController.current.animation.id.equals("dribbleIdle1ArmL")) {
				armLController.setAnimation("dribblePhase1ArmL", 1, new AnimationListener() {

					@Override
					public void onEnd(AnimationDesc animation) {
						releaseBall(bodiesMap.get("handL").getWorldTransform().cpy());
						
						//disableHandDynColl(true);
						
						map.getBall().getMainBody().setLinearVelocity(new Vector3(0, -10, 0));
					}

					@Override
					public void onLoop(AnimationDesc animation) {
						
					}
					
				});
			}
			else {
				if (!leftHoldingBall) {
					if (leftHandBall && leftReadyBall) {
						if (!armLController.current.animation.id.equals("dribblePhase2ArmL")) {
							armLController.setAnimation("dribblePhase2ArmL", 1, new AnimationListener() {

								@Override
								public void onEnd(AnimationDesc animation) {
									// armLController.animate("dribbleIdleArmL",
									// 0.15f);
									animateArmL("dribbleIdle");
								}

								@Override
								public void onLoop(AnimationDesc animation) {

								}

							});

							catchBall(true);
							dribbleL = false;
							leftReadyBall = false;
							cycleTimeout = 0;
						}

					} else {
						if(!armLController.current.animation.id.equals("dribbleIdle1ArmL")) {
							Matrix4 temp = map.getBall().getMainBody().getWorldTransform();
							Vector3 tempVec = new Vector3();
							temp.getTranslation(tempVec);

							Matrix4 temp1 = bodiesMap.get("handL").getWorldTransform().cpy();
							Vector3 tempVec1 = new Vector3();
							temp1.getTranslation(tempVec1);
							
							map.getBall().setWorldTransform(new Matrix4().set(new Vector3(tempVec1.x, tempVec.y, tempVec1.z), map.getBall().getMainBody().getWorldTransform().getRotation(new Quaternion())));
						}else {
							Vector3 tempVelVec = makeBallDribbleVelocity(true);
							tempVelVec.y = map.getBall().getMainBody().getLinearVelocity().y;
							
							map.getBall().getMainBody().setLinearVelocity(tempVelVec);
						}
						System.out.println(map.getBall().getMainBody().getLinearVelocity().y);
						if(cycleTimeout * delta > 0.75f) {
							dribbleL = false;
							leftReadyBall = false;
							cycleTimeout = 0;
						}
						else if (cycleTimeout > 5 && !leftReadyBall) {
							leftReadyBall = true;
						} else {
							cycleTimeout++;
						}
					}
				}
				else
					map.getBall().setWorldTransform(bodiesMap.get("handL").getWorldTransform().cpy());
			}
		} else if (dribbleR) {
			if (!armRController.current.animation.id.equals("dribblePhase1ArmR")) {
				armRController.setAnimation("dribblePhase1ArmR", 1, 1, new AnimationListener() {

					@Override
					public void onEnd(AnimationDesc animation) {
						releaseBall(bodiesMap.get("handR").getWorldTransform().cpy());
						
						map.getBall().getMainBody().setLinearVelocity(new Vector3(0, -10, 0));
					}

					@Override
					public void onLoop(AnimationDesc animation) {
						
					}
					
				});
			}
			else {
				if (!rightHoldingBall) {
					if (ballColl && rightReadyBall) {
						if (!armRController.current.animation.id.equals("dribblePhase2ArmR")) {
							armRController.setAnimation("dribblePhase2ArmR", 1, 1, new AnimationListener() {

								@Override
								public void onEnd(AnimationDesc animation) {
									// armLController.animate("dribbleIdleArmL",
									// 0.15f);
									animateArmR("dribbleIdle");
								}

								@Override
								public void onLoop(AnimationDesc animation) {

								}

							});

							catchBall(false);
							dribbleR = false;
							rightReadyBall = false;
							cycleTimeout = 0;
						}
					} else {
						Matrix4 temp = map.getBall().getMainBody().getWorldTransform();
						Vector3 tempVec = new Vector3();
						temp.getTranslation(tempVec);

						Matrix4 temp1 = bodiesMap.get("handR").getWorldTransform().cpy();
						Vector3 tempVec1 = new Vector3();
						temp1.getTranslation(tempVec1);

						map.getBall().setWorldTransform(new Matrix4().set(new Vector3(tempVec1.x, tempVec.y, tempVec1.z), map.getBall().getMainBody().getWorldTransform().getRotation(new Quaternion())));
						
						if(cycleTimeout * delta > 0.75f) {
							dribbleR = false;
							rightReadyBall = false;
							cycleTimeout = 0;
						}
						else if (cycleTimeout > 5 && !rightReadyBall) {
							rightReadyBall = true;
						} else {
							cycleTimeout++;
						}
					}
				}
				else
					map.getBall().setWorldTransform(bodiesMap.get("handR").getWorldTransform().cpy());
			}
		}
		else if (leftHoldingBall) {
			// map.getBall().setCopyTransform(bodiesMap.get("handL").getWorldTransform());
			map.getBall().setWorldTransform(bodiesMap.get("handL").getWorldTransform().cpy());

			// map.getBall().setWorldTransform(bodiesMap.get("handL").getWorldTransform().cpy());

			if (leftAimBall) {
				float transistion = 0.25f;
				if (!armLController.current.animation.id.equals("aimLArmL")) {
					armLController.animate("aimLArmL", transistion);
					// animateArmL("aimL");
					// System.out.println("Animated aimL");
					armLController.setAnimation("aimLArmL", -1, new AnimationListener() {

						@Override
						public void onEnd(AnimationDesc animation) {
							// controller.queue("testAnim", loopCount,
							// speed,
							// listener, transitionTime)

						}

						@Override
						public void onLoop(AnimationDesc animation) {
							if (armLController.transitionCurrentTime >= armLController.transitionTargetTime)
								leftReadyBall = true;
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

			else if (leftReadyBall) {

				// if
				// (armLController.current.animation.id.equals("throwLArmL"))
				// {
				// leftReadyBall = false;

				// }
				// else {
				leftThrowBall = true;
				leftReadyBall = false;
				// System.out.println("Animated throwL");
				armLController.animate("throwLArmL", 0.25f);
				armLController.setAnimation("throwLArmL", 1, new AnimationListener() {

					@Override
					public void onEnd(AnimationDesc animation) {
						// System.out.println("Animation Ended");
						throwBall();

						leftThrowBall = false;
						leftHoldingBall = false;
					}

					@Override
					public void onLoop(AnimationDesc animation) {
						// System.out.println("Animation Ended");
						// leftThrowBall = false;
						// leftHoldingBall = false;

					}

				});

				armRController.animate("throwLArmR", 0.25f);
				armRController.setAnimation("throwLArmR", 1);

				bodyController.animate("throwLBody", 0.25f);
				bodyController.setAnimation("throwLBody", 1);
				// }
			}

			else if (!leftThrowBall && !armLController.current.animation.id.equals("dribbleIdleArmL") && !armLController.current.animation.id.equals("aimLArmL")) {
				armLController.animate("dribbleIdleArmL", 0.15f);
				animateArmL("dribbleIdle");
			}
		}
		
		else if(rightHoldingBall) {
			map.getBall().setWorldTransform(bodiesMap.get("handR").getWorldTransform().cpy());

			// map.getBall().setWorldTransform(bodiesMap.get("handL").getWorldTransform().cpy());

			if (rightAimBall) {
				float transistion = 0.25f;
				if (!armLController.current.animation.id.equals("aimRArmL")) {
					armLController.animate("aimRArmL", transistion);
					animateArmL("aimR");
					// System.out.println("Animated aimL");
					/*armLController.setAnimation("aimRArmL", -1, new AnimationListener() {

						@Override
						public void onEnd(AnimationDesc animation) {
							// controller.queue("testAnim", loopCount,
							// speed,
							// listener, transitionTime)

						}

						@Override
						public void onLoop(AnimationDesc animation) {
							if (armLController.transitionCurrentTime >= armLController.transitionTargetTime)
								rightReadyBall = true;
						}

					});*/
				}

				if (!armRController.current.animation.id.equals("aimRArmR")) {
					armRController.animate("aimRArmR", transistion);
					armRController.setAnimation("aimRArmR", -1, new AnimationListener() {

						@Override
						public void onEnd(AnimationDesc animation) {
							// controller.queue("testAnim", loopCount,
							// speed,
							// listener, transitionTime)

						}

						@Override
						public void onLoop(AnimationDesc animation) {
							if (armRController.transitionCurrentTime >= armRController.transitionTargetTime)
								rightReadyBall = true;
						}

					});
				}

				if (!bodyController.current.animation.id.equals("aimRBody")) {
					bodyController.animate("aimRBody", transistion);
					animateBody("aimR");
				}
			}

			else if (rightReadyBall) {

				// if
				// (armLController.current.animation.id.equals("throwLArmL"))
				// {
				// leftReadyBall = false;

				// }
				// else {
				rightThrowBall = true;
				rightReadyBall = false;
				// System.out.println("Animated throwL");
				armLController.animate("throwRArmL", 0.25f);
				armLController.setAnimation("throwRArmL", 1);

				armRController.animate("throwRArmR", 0.25f);
				armRController.setAnimation("throwRArmR", 1, new AnimationListener() {

					@Override
					public void onEnd(AnimationDesc animation) {
						throwBall();

						rightThrowBall = false;
						rightHoldingBall = false;
					}

					@Override
					public void onLoop(AnimationDesc animation) {
						
					}

				});

				bodyController.animate("throwRBody", 0.25f);
				bodyController.setAnimation("throwRBody", 1);
				// }
			}

			else if (!rightThrowBall && !armRController.current.animation.id.equals("dribbleIdleArmR") && !armRController.current.animation.id.equals("aimRArmR")) {
				armRController.animate("dribbleIdleArmR", 0.15f);
				animateArmR("dribbleIdle");
			}
		}
		
		else if (!ballColl) {
			if (!leftHandInWorld && !dribbleL) {
				if (cycleTimeout > 5) {
					enableHandDynColl(true);
					enableUpperBodyDynColl();

					//map.getBall().getMainBody().setGravity(map.getDynamicsWorld().getGravity());
					
					cycleTimeout = 0;
				}
				else
					cycleTimeout++;
			}

			else if (!rightHandInWorld && !dribbleR) {
				if (cycleTimeout > 5) {
					enableHandDynColl(false);
					enableUpperBodyDynColl();

					//map.getBall().getMainBody().setGravity(map.getDynamicsWorld().getGravity());
					
					cycleTimeout = 0;
				}
				else
					cycleTimeout++;
			}
		}
		else if(!leftHandInWorld || !rightHandInWorld)
			cycleTimeout = 0;
		
		
		
		
		float prevTime = armLController.current.time;
		//else
		if (walking) {
			// controller.animate("walk", 0.25f);
			if (!leftThrowBall && !rightThrowBall && !armLController.current.animation.id.equals("aimLArmL") && !armLController.current.animation.id.equals("aimRArmL")) {
				if (!leftHoldingBall && !dribbleL && !armLController.current.animation.id.equals("walkArmL")) {
					animateArmL("walk");

					if (prevTime < modelInstance.getAnimation("stayArmL").duration / 2)
						armLController.current.time = 0.5f;
				}

				if (!rightHoldingBall && !dribbleR && !armRController.current.animation.id.equals("walkArmR")) {
					animateArmR("walk");

					if (prevTime < modelInstance.getAnimation("stayArmR").duration / 2)
						armRController.current.time = 0.5f;
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
		
		
		/*else if(jumping) {
			controller.setAnimation("jump", 1, 1, new AnimationListener() {

				@Override
				public void onEnd(AnimationDesc animation) {
					//controller.queue("testAnim", loopCount, speed, listener, transitionTime)
					controller.setAnimation("stay", -1);
				}

				@Override
				public void onLoop(AnimationDesc animation) {
					
				}
				
			});
		}*/
		
		else if(running) {
			if (!armLController.current.animation.id.equals("runArmL") && !leftHoldingBall && !dribbleL) {
				animateArmL("run");

				if (prevTime < modelInstance.getAnimation("stayArmL").duration / 2)
					armLController.current.time = 0.375f;
			}
			
			if (!armRController.current.animation.id.equals("runArmR") && !rightHoldingBall && !dribbleR) {
				animateArmR("run");

				if (prevTime < modelInstance.getAnimation("stayArmR").duration / 2)
					armRController.current.time = 0.375f;
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
			/*if(armLController.current.animation.id.equals("jump")) {
				if(prevTime >= 0.2f) {
					bodies.get(mainBodyIndex).setLinearVelocity(new Vector3(0, JUMPING_VELOCITY, 0));
				}
			}*/
			
			String id = armLController.current.animation.id;
			
			if (!id.equals("aimLArmL") && !id.equals("aimRArmL")) {
				
				if (!leftHoldingBall && !id.equals("stayArmL") && !dribbleL && !id.contains("dribble")) {
					armLController.animate("stayArmL", 0.25f);
					armLController.setAnimation("stayArmL", -1, 1, new AnimationListener() {

						@Override
						public void onEnd(AnimationDesc animation) {

						}

						@Override
						public void onLoop(AnimationDesc animation) {
							
						}

					});

					/*if (id.equals("runArmL") && prevTime > modelInstance.getAnimation(id).duration / 2) {
						armLController.current.time = modelInstance.getAnimation("stayArmL").duration / 2;
					}
					else if (id.equals("walkArmL") && prevTime > modelInstance.getAnimation(id).duration / 2) {
						armLController.current.time = modelInstance.getAnimation("stayArmL").duration / 2;
					}*/

					if (prevTime > modelInstance.getAnimation(id).duration / 2) {
						armLController.current.time = modelInstance.getAnimation("stayArmL").duration / 2;
					}
				}
				
				if (!rightHoldingBall && !armRController.current.animation.id.equals("stayArmR") && !dribbleR && !armRController.current.animation.id.contains("dribble")) {
					armRController.animate("stayArmR", 0.25f);
					armRController.setAnimation("stayArmR", -1, 1, new AnimationListener() {

						@Override
						public void onEnd(AnimationDesc animation) {
							//System.out.println("stayR Ended");
						}

						@Override
						public void onLoop(AnimationDesc animation) {

						}

					});

					/*if (id.equals("runArmL") && prevTime > modelInstance.getAnimation(id).duration / 2) {
						armLController.current.time = modelInstance.getAnimation("stayArmL").duration / 2;
					}
					else if (id.equals("walkArmL") && prevTime > modelInstance.getAnimation(id).duration / 2) {
						armLController.current.time = modelInstance.getAnimation("stayArmL").duration / 2;
					}*/

					if (prevTime > modelInstance.getAnimation(id).duration / 2) {
						armRController.current.time = modelInstance.getAnimation("stayArmR").duration / 2;
					}
				}
				stopBodyAnim();
			}
			/*else if(armLController.current.animation.id.equals("stayArmL")) {
				if (!ballColl) {
					if (!leftHandInWorld && !leftHoldingBall)
						enableHandDynColl(true);

					else if (!rightHandInWorld && !rightHoldingBall)
						enableHandDynColl(false);
				}
			}*/
			
			legLController.animate("idle", 0.15f);
			legRController.animate("idle", 0.15f);
			stopLegsAnim();
		}
		
		armLController.update(delta);
		armRController.update(delta);
		legLController.update(delta);
		legRController.update(delta);
		bodyController.update(delta);
		//System.out.println(armRController.current.animation.id);
		/*walking = false;
		running = false;
		leftHandBall = false;
		rightHandBall = false;
		leftHoldingBall = false;
		leftHoldingBall = false;*/
		super.update(delta);
		//System.out.println(getHeight());
	}
	
	/*protected void setCollisionTransform() {
		/*modelInstances.get(0).transform.setToTranslation(x, y + 2, z);
		modelInstances.get(1).transform.setToTranslation(x, y + 1.5f, z);
		modelInstances.get(2).transform.setToTranslation(x, y + 1.25f, z);
		modelInstances.get(3).transform.setToTranslation(x, y + 1, z);
		modelInstances.get(4).transform.setToTranslation(x + 1, y + 1.15f, z);
		
		
	}*/

	public Node getCamNode() {
		return camNode;
	}

	@Override
	public float getWidth() {
		//return model.getNode("shoulderL").scale.x + model.getNode("spine1").scale.x;
		return scale6 * 2 + scale2;
	}

	@Override
	public float getHeight() {
		//return model.getNode("head").translation.y + model.getNode("head").scale.y / 2 + model.getNode("spine1").translation.y * 2 + model.getNode("spine3").translation.y + model.getNode("leg1L").translation.y + model.getNode("kneeL").translation.y + model.getNode("leg2L").translation.y + model.getNode("leg2L").scale.y / 2;
		//System.out.println(modelInstance.getNode("head").translation.y);
		//System.out.println("GetHeight");
		return model.getNode("head").translation.y + scale1 - model.getNode("leg2L").translation.y + scale5 / 2;
	}

	@Override
	public float getDepth() {
		//return model.getNode("head").scale.z;
		return scale1;
	}

	/*@Override
	protected void setCollisionTransform() {
		//body.setWorldTransform(modelInstance.transform);
		//Matrix4 temp = new Matrix4().set(invisBodies.get(0).getWorldTransform());
		//temp.trn(0, -getHeight() / 2, 0);
		super.setCollisionTransform();
		
		/*bodies.get(1).proceedToTransform(modelInstance.getNode("shoulderL").globalTransform);
		bodies.get(2).proceedToTransform(modelInstance.getNode("arm1L").globalTransform);
		bodies.get(3).proceedToTransform(modelInstance.getNode("elbowL").globalTransform);
		bodies.get(4).proceedToTransform(modelInstance.getNode("arm2L").globalTransform);
		bodies.get(5).proceedToTransform(modelInstance.getNode("handL").globalTransform);
		bodies.get(6).proceedToTransform(modelInstance.getNode("shoulderR").globalTransform);
		bodies.get(7).proceedToTransform(modelInstance.getNode("arm1R").globalTransform);
		bodies.get(8).proceedToTransform(modelInstance.getNode("elbowR").globalTransform);
		bodies.get(9).proceedToTransform(modelInstance.getNode("arm2R").globalTransform);
		bodies.get(10).proceedToTransform(modelInstance.getNode("handR").globalTransform);
		
		
	}*/
	
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
		//temp.nor();
		//float axis = 1;
		//float y = temp.getYaw();
		//float y2 = temp.getAngleAround(0, 1, 0);
		//System.out.println(angleY);
		//if(angleY != 0)
			//temp.setFromAxis(0, 1 * (angleY / Math.abs(angleY)), 0, temp.getAngleAround(0, 1, 0));
		//else temp.setFromAxis(0, 1 * (angleY / Math.abs(angleY)), 0, temp.getAngleAround(0, 1, 0));
		//if(temp.getPitch() != 0 || temp.getRoll() != 0) {
			//temp.setEulerAngles(angleY, 0, 0);
		
		//temp.setFromAxis(1, 0, 1, 0);
		//temp.setFromAxis(0, y, 0, 1);
		
		//resetQuaternionPitchAndRoll(temp);
		
		//if(y2 != 0)
			//System.out.println(y + " ; " + y2 + " ; " + posOrNegAccToPosOrNegToGiven(y, y2));
		
		temp.x = 0;
		temp.z = 0;
		modelInstTrans.set(tempVec, temp);
		//modelInstTrans.rotate(1, 0, 0, -temp.getPitch());
		//modelInstTrans.rotate(0, 0, 1, -temp.getRoll());
		//modelInstTrans.rotate(1, 0, 1, 0);
		//bodies.get(mainBodyIndex).setWorldTransform(modelInstTrans);
		//invisBodies.get(0).setWorldTransform(modelInstTrans);
		
		//bodies.get(mainBodyIndex).setWorldTransform(new Matrix4().setFromEulerAngles(y, 0, 0).trn(tempVec));
		//modelInstance.transform.setFromEulerAngles(y, 0, 0).trn(tempVec);
		
		setCollisionTransform();
		//}
	}
	
	@Override
	public void collisionOccured(btCollisionObject objInside, btCollisionObject objOutside) {
		super.collisionOccured(objInside, objOutside);
		//;if(map.getMainPlayer().equals(this))
			//System.out.println(map.getBall().isGrounded());
		//if(map.getObjectsMap().get(objOutside.getUserValue()).equals(EntityType.BALL.getId()) && ((map.getBall().isGrounded() && objInside.getWorldTransform().val.equals(modelInstance.getNode("handL").globalTransform.val)) ^ (map.getObjectsMap().get(objInside.getUserValue()).equals(type.getId())))) {
		if(map.getObjectsMap().get(objOutside.getUserValue()).equals(EntityType.BALL.getId()) || map.getObjectsMap().get(objOutside.getUserValue()).equals(EntityType.BALL.getId() + "Obj")) {	
			ballColl = true;
			if(objInside.equals(collisionObjects.get(0)))
				leftHandBall = true;
			//System.out.println("YES");
			//if(leftHandBall)
				//System.out.println("LeftHandBall");
			//if(map.getObjectsMap().get(objInside.getUserValue()).equals("teamObj"))
			//System.out.println(map.getObjectsMap().get(objInside.getUserValue()) + "(" + objInside.getUserValue() + ")" + " with " + map.getObjectsMap().get(objOutside.getUserValue()) + "(" + objOutside.getUserValue() + ")");
		}
		
		/*if(objInside.getUserValue() == bodies.get(0).getUserValue() && objOutside.getUserValue() == map.getBall().getMainBody().getUserValue()) {
			leftHandBall = true;
			System.out.println("YES");
		}*/
	}
	
	@Override
	public void onCycleEnd() {
		super.onCycleEnd();
		
		walking = false;
		running = false;
		//jumping = false;
		ballColl = false;
		rightHandBall = false;
		//leftHoldingBall = false;
		//rightHoldingBall = false;
		leftHandBall = false;
		leftAimBall = false;
		rightAimBall = false;
	}
	
	/*private float posOrNegAccToPosOrNegToGiven(float givenNum, float givenNum2) {
		if(givenNum < 0)
			return -givenNum2;
		
		return givenNum2;	
	}*/
	
}
