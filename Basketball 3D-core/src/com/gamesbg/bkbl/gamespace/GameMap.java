package com.gamesbg.bkbl.gamespace;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.ai.utils.Ray;
import com.badlogic.gdx.ai.utils.RaycastCollisionDetector;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.gamesbg.bkbl.gamespace.entities.Ball;
import com.gamesbg.bkbl.gamespace.entities.Entity;
import com.gamesbg.bkbl.gamespace.entities.EntityType;
import com.gamesbg.bkbl.gamespace.entities.Player;
import com.gamesbg.bkbl.gamespace.objects.GameObject;
import com.gamesbg.bkbl.gamespace.objects.ObjectType;
import com.gamesbg.bkbl.gamespace.tools.InputController;

public class GameMap implements RaycastCollisionDetector<Vector2> {
	
	final static float[][] spawnCoords = {{0, 0.1f, 4},
										  {5, 0.1f, 2},
										  {-5, 0.1f, 2},
										  {-5, 0.1f, 10},
										  {5, 0.1f, 10}}; //Use this for spawning players on the following positions
	
	//final static short PART_FLAG = 1 << 3; //The entity's part (they should not collide by themselves)
	public final static short CAMERA_FLAG = 1 << 3;
	//public final static short PLAYER_FLAG = 1 << 4;
	public final static short ENT_SPECIAL_FLAG = 1 << 5; //The special flag of an entity
	public final static short OBJECT_FLAG = 1 << 6;
	public final static short SPECIAL_FLAG = 1 << 7; //The special flag of an object
	public final static short GROUND_FLAG = 1 << 8;
	public final static short ENTITY_FLAG = 1 << 9;
	public final static short ALL_FLAG = -1;
	
	class ObjectContactListener extends ContactListener{
		@Override
		public boolean onContactAdded (int userValue0, int partId0, int index0, int userValue1, int partId1, int index1) {
			if(
			//objectsMap.get(userValue0).equals("teamNorth") || objectsMap.get(userValue0).equals("teamSouth") || objectsMap.get(userValue0).equals("teamEast") || objectsMap.get(userValue0).equals("teamWest") ||
			//objectsMap.get(userValue1).equals("teamNorth") || objectsMap.get(userValue1).equals("teamSouth") || objectsMap.get(userValue1).equals("teamEast") || objectsMap.get(userValue1).equals("teamWest")
					//|| 
					(objectsMap.get(userValue0).equals("oppNorth") || objectsMap.get(userValue0).equals("oppSouth") || objectsMap.get(userValue0).equals("oppEast") || objectsMap.get(userValue0).equals("oppWest") 
					) && objectsMap.get(userValue1).equals("team") || 
					(
							objectsMap.get(userValue1).equals("oppNorth") || objectsMap.get(userValue1).equals("oppSouth") || objectsMap.get(userValue1).equals("oppEast") || objectsMap.get(userValue1).equals("teamWest")
					) && objectsMap.get(userValue0).equals("team")
			)
				System.out.println(objectsMap.get(userValue0) + ";" + objectsMap.get(userValue1));
			
			btCollisionObject tempCollObj0 = collObjsValsMap.get(userValue0), tempCollObj1 = collObjsValsMap.get(userValue1);
			Entity temp0 = collObjsInEntityMap.get(tempCollObj0), temp1 = collObjsInEntityMap.get(tempCollObj1);
			
			if(temp0 != null) {
				temp0.collisionOccured(tempCollObj0, tempCollObj1);
			}
			if(temp1 != null) {
				temp1.collisionOccured(tempCollObj1, tempCollObj0);
			}
			
			return true;
		}
	}
	
	
	ArrayList<Player> teammates;
	ArrayList<Player> opponents;
	Player mainPlayer;
	Entity ball;
	
	GameObject terrain, basket1, basket2;
	GameObject camera;
	
	btCollisionConfiguration dynCollConfig;
    btDispatcher dynDispatcher;
    btBroadphaseInterface dynBroadphase;
	btDynamicsWorld dynamicsWorld;
    btConstraintSolver constraintSolver;
    
    ObjectContactListener contactListener;
    
    InputController inputs;
    
    HashMap<Integer, String> objectsMap;
    HashMap<btCollisionObject, Entity> collObjsInEntityMap;
    HashMap<btCollisionObject, GameObject> collObjsInObjectMap;
    HashMap<Integer, btCollisionObject> collObjsValsMap;
    
    //Current game properties
    int currentPlayerHoldTeam, currentPlayerHoldOpp;
    
    int teamScore, oppScore;
    
    float startTimer;
    
    boolean gameRunning;//Whether or not the players can play
    
    
    int index = 0;
	
	public GameMap() {
		inputs = new InputController();
		
		Bullet.init();
        dynCollConfig = new btDefaultCollisionConfiguration();
        dynDispatcher = new btCollisionDispatcher(dynCollConfig);
        dynBroadphase = new btDbvtBroadphase();
        constraintSolver = new btSequentialImpulseConstraintSolver();
        dynamicsWorld = new btDiscreteDynamicsWorld(dynDispatcher, dynBroadphase, constraintSolver, dynCollConfig);
        dynamicsWorld.setGravity(new Vector3(0, -9.8f, 0));
		contactListener = new ObjectContactListener();
		
		objectsMap = new HashMap<Integer, String>();
		collObjsInEntityMap = new HashMap<btCollisionObject, Entity>();
		collObjsInObjectMap = new HashMap<btCollisionObject, GameObject>();
		collObjsValsMap = new HashMap<Integer, btCollisionObject>();		
		
		terrain = ObjectType.createGameObject(ObjectType.TERRAIN.getId(), this, 0, 0, 0);
		for(btRigidBody co : terrain.getBodies()) {
			co.setUserValue(index);
			
			co.setCollisionFlags(co.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);
			dynamicsWorld.addRigidBody(co, GROUND_FLAG, ALL_FLAG);
			co.setContactCallbackFlag(GROUND_FLAG);
			co.setContactCallbackFilter(ENTITY_FLAG);
			co.setActivationState(Collision.DISABLE_DEACTIVATION);
			
			objectsMap.put(index, ObjectType.TERRAIN.getId());
			collObjsInObjectMap.put(co, terrain);
			collObjsValsMap.put(index, co);
			
			index++;
		}
		
		for(btRigidBody co : terrain.getInvisBodies()) {
			co.setUserValue(index);
			co.setCollisionFlags(co.getCollisionFlags());
			dynamicsWorld.addRigidBody(co, GROUND_FLAG, ALL_FLAG);
			co.setContactCallbackFlag(GROUND_FLAG);
			co.setContactCallbackFilter(ENTITY_FLAG);
			co.setActivationState(Collision.DISABLE_DEACTIVATION);
			
			objectsMap.put(index, ObjectType.TERRAIN.getId());
			collObjsInObjectMap.put(co, terrain);
			collObjsValsMap.put(index, co);
			
			index++;
		}
		
		camera = ObjectType.createGameObject(ObjectType.CAMERA.getId(), this, 0, 0, 0);
		for(btRigidBody co : camera.getBodies()) {
			co.setUserValue(index);
			co.setCollisionFlags(co.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
			dynamicsWorld.addRigidBody(co, CAMERA_FLAG, GROUND_FLAG);
			co.setContactCallbackFlag(CAMERA_FLAG);
			co.setContactCallbackFilter(GROUND_FLAG);
			
			objectsMap.put(index, ObjectType.CAMERA.getId());
			collObjsInObjectMap.put(co, camera);
			collObjsValsMap.put(index, co);
			
			index++;
		}
			
		basket1 = ObjectType.createGameObject(ObjectType.HOMEBASKET.getId(), this, 0.1f, 0, 27);
		for(btRigidBody co : basket1.getBodies()) {
			co.setUserValue(index);
			dynamicsWorld.addRigidBody(co, OBJECT_FLAG, ALL_FLAG);
			co.setContactCallbackFlag(OBJECT_FLAG);
			co.setContactCallbackFilter(ENTITY_FLAG);
			
			objectsMap.put(index, ObjectType.HOMEBASKET.getId());
			collObjsInObjectMap.put(co, basket1);
			collObjsValsMap.put(index, co);
			
			index++;
		}
		
		for(btCollisionObject co : basket1.getCollisionObjects()) {
			co.setUserValue(index);
			co.setCollisionFlags(co.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
			if(co.getUserIndex() == 2) {
				dynamicsWorld.addCollisionObject(co, ENT_SPECIAL_FLAG, SPECIAL_FLAG);
				co.setContactCallbackFlag(ENT_SPECIAL_FLAG);
				co.setContactCallbackFilter(SPECIAL_FLAG);
				objectsMap.put(index, ObjectType.HOMEBASKET.getId() + "Zone");
			}else {
				dynamicsWorld.addCollisionObject(co, SPECIAL_FLAG, ENT_SPECIAL_FLAG);
				co.setContactCallbackFlag(SPECIAL_FLAG);
				co.setContactCallbackFilter(ENT_SPECIAL_FLAG);
				objectsMap.put(index, ObjectType.HOMEBASKET.getId() + "Obj");
			}
			
			collObjsInObjectMap.put(co, basket1);
			collObjsValsMap.put(index, co);
			
			index++;
		}
		
		basket2 = ObjectType.createGameObject(ObjectType.AWAYBASKET.getId(), this, 0.1f, 0, -27);
		basket2.setRotation(0, 1, 0, 180);
		for(btRigidBody co : basket2.getBodies()) {
			co.setUserValue(index);
			dynamicsWorld.addRigidBody(co, OBJECT_FLAG, ALL_FLAG);
			co.setContactCallbackFlag(OBJECT_FLAG);
			co.setContactCallbackFilter(ENTITY_FLAG);
			
			objectsMap.put(index, ObjectType.AWAYBASKET.getId());
			collObjsInObjectMap.put(co, basket2);
			collObjsValsMap.put(index, co);
			
			index++;
		}
		
		for(btCollisionObject co : basket2.getCollisionObjects()) {
			co.setUserValue(index);
			co.setCollisionFlags(co.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
			if(co.getUserIndex() == 2) {
				dynamicsWorld.addCollisionObject(co, ENT_SPECIAL_FLAG, SPECIAL_FLAG);
				co.setContactCallbackFlag(ENT_SPECIAL_FLAG);
				co.setContactCallbackFilter(SPECIAL_FLAG);
				objectsMap.put(index, ObjectType.AWAYBASKET.getId() + "Zone");
			}else {
				dynamicsWorld.addCollisionObject(co, SPECIAL_FLAG, ENT_SPECIAL_FLAG);
				co.setContactCallbackFlag(SPECIAL_FLAG);
				co.setContactCallbackFilter(ENT_SPECIAL_FLAG);
				objectsMap.put(index, ObjectType.AWAYBASKET.getId() + "Obj");
			}
			
			collObjsInObjectMap.put(co, basket2);
			collObjsValsMap.put(index, co);
			
			index++;
		}
		
		createBall();
		
		
		teammates = new ArrayList<Player>(5);
		opponents = new ArrayList<Player>(5);
	}
	
	public void spawnPlayers(int count) {
		int index2 = index;
		int playerIndex = 1;
		
		for(int i = 0; i < count; i++) {
			Player teammate = EntityType.createPlayer(EntityType.TEAMMATE.getId(), this, new Vector3(spawnCoords[i][0], spawnCoords[i][1], spawnCoords[i][2]));
			for (btRigidBody co : teammate.getBodies()) {
				co.setUserValue(index2);
				co.setCollisionFlags(co.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
				
				dynamicsWorld.addRigidBody(co, ENTITY_FLAG, ALL_FLAG);
				co.setContactCallbackFlag(ENTITY_FLAG);
				co.setContactCallbackFilter(ALL_FLAG);
					if(teammate.getMainBody().equals(co)) {
						objectsMap.put(index2, EntityType.TEAMMATE.getId());
					}
					else {
						objectsMap.put(index2, EntityType.TEAMMATE.getId() + "Hand");
					}
					
					collObjsInEntityMap.put(co, teammate);
					collObjsValsMap.put(index2, co);
				
				index2++;
			}
			
			for (btCollisionObject co : teammate.getCollisionObjects()) {
				co.setUserValue(index2);
				co.setCollisionFlags(co.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
				
					if(co.getUserIndex() == 10) {
						dynamicsWorld.addCollisionObject(co, ENT_SPECIAL_FLAG, ALL_FLAG);
						co.setContactCallbackFlag(ENT_SPECIAL_FLAG);
						co.setContactCallbackFilter(ALL_FLAG);
						
						objectsMap.put(index2, EntityType.TEAMMATE.getId() + "North");
					}
					else if(co.getUserIndex() == 20) {
						dynamicsWorld.addCollisionObject(co, ENT_SPECIAL_FLAG, ALL_FLAG);
						co.setContactCallbackFlag(ENT_SPECIAL_FLAG);
						co.setContactCallbackFilter(ALL_FLAG);
					
						objectsMap.put(index2, EntityType.TEAMMATE.getId() + "South");
					}
					else if(co.getUserIndex() == 30) {
						dynamicsWorld.addCollisionObject(co, ENT_SPECIAL_FLAG, ALL_FLAG);
						co.setContactCallbackFlag(ENT_SPECIAL_FLAG);
						co.setContactCallbackFilter(ALL_FLAG);
						
						objectsMap.put(index2, EntityType.TEAMMATE.getId() + "East");
					}
					else if(co.getUserIndex() == 40) {
						dynamicsWorld.addCollisionObject(co, ENT_SPECIAL_FLAG, ALL_FLAG);
						co.setContactCallbackFlag(ENT_SPECIAL_FLAG);
						co.setContactCallbackFilter(ALL_FLAG);
						
						objectsMap.put(index2, EntityType.TEAMMATE.getId() + "West");
					}
					else {
						dynamicsWorld.addCollisionObject(co, SPECIAL_FLAG, ENT_SPECIAL_FLAG);
						co.setContactCallbackFlag(SPECIAL_FLAG);
						co.setContactCallbackFilter(ENT_SPECIAL_FLAG);
						
						objectsMap.put(index2, EntityType.TEAMMATE.getId() + "Obj");
					}
					
					collObjsInEntityMap.put(co, teammate);
					collObjsValsMap.put(index2, co);
				
				index2++;
			}
			
			teammate.turnY(180);
			
			teammates.add(teammate);
			
			teammate.setPlayerIndex(playerIndex);
			playerIndex++;
		}
		
		playerIndex = 1;
		for(int i = 0; i < count; i++) {
			Player opponent = EntityType.createPlayer(EntityType.OPPONENT.getId(), this, new Vector3(spawnCoords[i][0], spawnCoords[i][1], -spawnCoords[i][2]));
			for (btRigidBody co : opponent.getBodies()) {
				co.setUserValue(index2);
				co.setCollisionFlags(co.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
				dynamicsWorld.addRigidBody(co, ENTITY_FLAG, ALL_FLAG);
				co.setContactCallbackFlag(ENTITY_FLAG);
				co.setContactCallbackFilter(ALL_FLAG);
					if(opponent.getMainBody().equals(co)) {
						objectsMap.put(index2, EntityType.OPPONENT.getId());
					}
					else {
						objectsMap.put(index2, EntityType.OPPONENT.getId() + "Hand");
					}
					
					collObjsInEntityMap.put(co, opponent);
					collObjsValsMap.put(index2, co);
				
				index2++;
			}
			
			for (btCollisionObject co : opponent.getCollisionObjects()) {
				co.setUserValue(index2);
				co.setCollisionFlags(co.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
					if(co.getUserIndex() == 10) {
						dynamicsWorld.addCollisionObject(co, ENT_SPECIAL_FLAG, ALL_FLAG);
						co.setContactCallbackFlag(ENT_SPECIAL_FLAG);
						co.setContactCallbackFilter(ALL_FLAG);
						
						objectsMap.put(index2, EntityType.OPPONENT.getId() + "North");
					}
					else if(co.getUserIndex() == 20) {
						dynamicsWorld.addCollisionObject(co, ENT_SPECIAL_FLAG, ALL_FLAG);
						co.setContactCallbackFlag(ENT_SPECIAL_FLAG);
						co.setContactCallbackFilter(ALL_FLAG);
					
						objectsMap.put(index2, EntityType.OPPONENT.getId() + "South");
					}
					else if(co.getUserIndex() == 30) {
						dynamicsWorld.addCollisionObject(co, ENT_SPECIAL_FLAG, ALL_FLAG);
						co.setContactCallbackFlag(ENT_SPECIAL_FLAG);
						co.setContactCallbackFilter(ALL_FLAG);
						
						objectsMap.put(index2, EntityType.OPPONENT.getId() + "East");
					}
					else if(co.getUserIndex() == 40) {
						dynamicsWorld.addCollisionObject(co, ENT_SPECIAL_FLAG, ALL_FLAG);
						co.setContactCallbackFlag(ENT_SPECIAL_FLAG);
						co.setContactCallbackFilter(ALL_FLAG);
						
						objectsMap.put(index2, EntityType.OPPONENT.getId() + "West");
					}
					else {
						dynamicsWorld.addCollisionObject(co, SPECIAL_FLAG, ENT_SPECIAL_FLAG);
						co.setContactCallbackFlag(SPECIAL_FLAG);
						co.setContactCallbackFilter(ENT_SPECIAL_FLAG);
						
						objectsMap.put(index2, EntityType.OPPONENT.getId() + "Obj");
					}
				
					collObjsInEntityMap.put(co, opponent);
					collObjsValsMap.put(index2, co);
				
				index2++;
			}
			
			opponents.add(opponent);
			
			
			opponent.setPlayerIndex(playerIndex);
			playerIndex++;
		}
		
		currentPlayerHoldTeam = -1;
		currentPlayerHoldOpp = -1;
		
		mainPlayer = teammates.get(0);
		
		for(btCollisionObject co : getCollObjectsOfAll()) {
			for(btRigidBody bo : getBodiesOfAll())
				bo.setIgnoreCollisionCheck(co, true);
			
		}
			
		
		Gdx.input.setInputProcessor(inputs);
		
		startTimer = -1;//6
	}
	
	private void createBall() {
		ball = EntityType.createEntity(EntityType.BALL.getId(), this, new Vector3(0, 0, 0));
		for (btRigidBody co : ball.getBodies()) {
			co.setUserValue(index);
			co.setCollisionFlags(co.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
			
			dynamicsWorld.addRigidBody(co, ENTITY_FLAG, ALL_FLAG);
			co.setContactCallbackFlag(ENTITY_FLAG);
			co.setContactCallbackFilter(ALL_FLAG);
			
			objectsMap.put(index, EntityType.BALL.getId());
			collObjsInEntityMap.put(co, ball);
			collObjsValsMap.put(index, co);

			index++;
		}
		
		for(btCollisionObject co : ball.getCollisionObjects()) {
			co.setUserValue(index);
			co.setCollisionFlags(co.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
			
			dynamicsWorld.addCollisionObject(co, ENT_SPECIAL_FLAG, SPECIAL_FLAG);
			co.setContactCallbackFlag(ENT_SPECIAL_FLAG);
			co.setContactCallbackFilter(SPECIAL_FLAG);
			objectsMap.put(index, EntityType.BALL.getId() + "Obj");
			collObjsInEntityMap.put(co, ball);
			collObjsValsMap.put(index, co);
			
			index++;
		}
	}
	
	public void clear() {
		disposePlayers();
		teammates.clear();
		opponents.clear();
		
		createBall();
		
		gameRunning = false;
	}
	
	public void update(float delta) {
		
		camera.setWorldTransform(new Matrix4(mainPlayer.getModelInstance().transform).mul(mainPlayer.getCamMatrix()).mul(new Matrix4().setToTranslation(0, mainPlayer.getHeight(), -10)));
		
		float delta2 = Math.min(1f / 30f, delta);
		for(int i = 0; i < 6; i++)
			dynamicsWorld.stepSimulation(delta2/6, 15, 1f / 60f);
		if(gameRunning)
			controlPlayer(delta);
		else {
			if(startTimer <= 0)
				gameRunning = true;
			else startTimer-= delta;
		}
		
		GdxAI.getTimepiece().update(Gdx.graphics.getDeltaTime());
		
		ball.update(delta);
		
		for(Player e : teammates)
			e.update(delta);
		
		for(Player e : opponents)
			e.update(delta);
		
		ball.onCycleEnd();
		
		for(Player e : teammates)
			e.onCycleEnd();
		
		for(Player e : opponents)
			e.onCycleEnd();
	}
	
	public void render(ModelBatch mBatch, Environment environment) {
		for(Player e : teammates)
			e.render(mBatch, environment);
		
		for(Player e : opponents)
			e.render(mBatch, environment);
		
		ball.render(mBatch, environment);
		
		terrain.render(mBatch, environment);
		basket1.render(mBatch, environment);
		basket2.render(mBatch, environment);
	}
	
	public void addRigidBody(btRigidBody body, int group, int mask) {
		dynamicsWorld.addRigidBody(body, (short) group, (short) mask);
	}
	
	public void removeRigidBody(btRigidBody body) {
		dynamicsWorld.removeRigidBody(body);
	}
	
	public void dispose() {
		disposePlayers();
		
		disposeMap();
	}
	
	private void disposeMap() {
		ball.dispose();
		
		terrain.dispose();
		
		basket1.dispose();
		basket2.dispose();
		
        dynamicsWorld.dispose();		
		dynDispatcher.dispose();
        dynCollConfig.dispose();
        dynBroadphase.dispose();
        constraintSolver.dispose();
        
        contactListener.dispose();
	}
	
	private void disposePlayers() {
		for(Player e : teammates) {
			for(btRigidBody co : e.getBodies()) {
				dynamicsWorld.removeRigidBody(co);
				collObjsInEntityMap.remove(co);
			}
			
			for(btCollisionObject co : e.getCollisionObjects()) {
				dynamicsWorld.removeCollisionObject(co);
				collObjsInEntityMap.remove(co);
			}
			
			e.dispose();
		}
		
		for (Player e : opponents) {
			for (btRigidBody co : e.getBodies()) {
				dynamicsWorld.removeRigidBody(co);
				collObjsInEntityMap.remove(co);
			}

			for (btCollisionObject co : e.getCollisionObjects()) {
				dynamicsWorld.removeCollisionObject(co);
				collObjsInEntityMap.remove(co);
			}

			e.dispose();
		}
	}
	
	public void setCameraTrans(Matrix4 trans) {
		camera.getBodies().get(0).proceedToTransform(trans);
		
	}
	
	public void setHoldingPlayer(Player player) {
		currentPlayerHoldTeam = teammates.indexOf(player);
		currentPlayerHoldOpp = opponents.indexOf(player);
	}
	
	public void playerReleaseBall() {
		currentPlayerHoldTeam = currentPlayerHoldOpp = -1;
	}
	
	private void controlPlayer(float delta) {
		Matrix4 playerM = new Matrix4().set(mainPlayer.getMainBody().getWorldTransform().val);
		
		Quaternion dir = playerM.getRotation(new Quaternion());
		
		Vector3 tempVec = new Vector3(0, 0, 1);
		tempVec.rotate(dir.getYaw(), 0, 1, 0);
		
		float dirX = tempVec.x;
		float dirZ = tempVec.z;
		
		float turnY = Gdx.input.getDeltaX(); //Around the Y-axis
		float turnX = Gdx.input.getDeltaY(); //Around the X-axis
		
		if(inputs.isSprintPressed()) {
			if (inputs.isForwardPressed()) {
				mainPlayer.run(new Vector3(dirX * delta, 0, dirZ * delta));
			}
			
			if(inputs.isStrLeftPressed())
				mainPlayer.turnY(delta * 90);
			
			if(inputs.isStrRightPressed())
				mainPlayer.turnY(-delta * 90);
		}
		else {
			if (inputs.isForwardPressed()) {
				mainPlayer.walk(new Vector3(dirX * delta, 0, dirZ * delta));
			}
			else if (inputs.isBackwardPressed())
				mainPlayer.walk(new Vector3(dirX * -delta, 0, dirZ * -delta));

			if (inputs.isStrLeftPressed())
				mainPlayer.walk(new Vector3(dirZ * delta, 0, dirX * -delta));
			else if (inputs.isStrRightPressed())
				mainPlayer.walk(new Vector3(dirZ * -delta, 0, dirX * delta));
		}
		
		mainPlayer.shootPowerScroll(inputs.getScroll());
		
		if(inputs.isShootPressed()) {
			mainPlayer.interactWithBallS();
		}
		
		else if(inputs.isDribbleLPressed()) {
			mainPlayer.interactWithBallL();
		}
		else if(inputs.isDribbleRPressed()) {
			mainPlayer.interactWithBallR();
		}
		
		
		
		if(Math.abs(turnY) < Gdx.graphics.getWidth() / 4) {
			mainPlayer.turnY(turnY * delta * 9);
		}
		
		if(Math.abs(turnX) < Gdx.graphics.getHeight() / 4) {
			mainPlayer.turnX(turnX * delta * 9);
		}
		
		int substractor = 40;
		
		if(Gdx.input.getX() > Gdx.graphics.getWidth() - substractor)
			Gdx.input.setCursorPosition(substractor, Gdx.input.getY());
		else if(Gdx.input.getX() < substractor)
			Gdx.input.setCursorPosition(Gdx.graphics.getWidth() - substractor, Gdx.input.getY());
		
		if(Gdx.input.getY() > Gdx.graphics.getHeight() - substractor)
			Gdx.input.setCursorPosition(Gdx.input.getX(), substractor);
		else if(Gdx.input.getY() < substractor)
			Gdx.input.setCursorPosition(Gdx.input.getX(), Gdx.graphics.getHeight() - substractor);
		
		inputs.update(mainPlayer.holdingBall());
		
		
	}
	
	public btDynamicsWorld getDynamicsWorld() {
		return dynamicsWorld;
	}
	
	public HashMap<Integer, String> getObjectsMap(){
		return objectsMap;
	}
	
	public Player getMainPlayer() {
		return mainPlayer;
	}
	
	public Player getTeammateHolding() {
		if(currentPlayerHoldTeam > -1)
		for(Player player : teammates)
			if(player.holdingBall())
				return player;
		
		return null;
	}
	
	public Player getOpponentHolding() {
		if(currentPlayerHoldOpp > -1)
		for(Player player : opponents)
			if(player.holdingBall())
				return player;
		
		return null;
	}
	
	public ArrayList<Player> getTeammates(){
		return teammates;
	}
	
	public ArrayList<Player> getOpponents(){
		return opponents;
	}
	
	public Vector3 getMainPlayerTranslation() {
		return mainPlayer.getMainBody().getWorldTransform().getTranslation(new Vector3());
	}
	
	public Quaternion getMainPlayerRotation() {
		return mainPlayer.getMainBody().getWorldTransform().getRotation(new Quaternion());
	}
	
	public Ball getBall() {
		return (Ball) ball;
	}
	
	public GameObject getTerrain() {
		return terrain;
	}
	
	public GameObject getHomeBasket() {
		return basket1;
	}
	
	public GameObject getAwayBasket() {
		return basket2;
	}
	
	public GameObject getCamera() {
		return camera;
	}
	
	public int getCurrentPlayerHoldTeam() {
		return currentPlayerHoldTeam;
	}

	public int getCurrentPlayerHoldOpp() {
		return currentPlayerHoldOpp;
	}

	public int teamScore() {
		return teamScore;
	}
	
	public int oppScore() {
		return oppScore;
	}
	
	public float getTimer() {
		return startTimer;
	}
	
	public boolean isGameRunning() {
		return gameRunning;
	}
	
	public boolean isBallInTeam() {
		return currentPlayerHoldTeam > -1;
	}
	
	public boolean isBallInOpp() {
		return currentPlayerHoldOpp > -1;
	}
	
	public ArrayList<btRigidBody> getBodiesOfAll(){
		ArrayList<btRigidBody> tempObj = new ArrayList<btRigidBody>();
		
		if(terrain.getBodies() != null)
			tempObj.addAll(terrain.getBodies());
		
		if(basket1.getBodies() != null)
			tempObj.addAll(basket1.getBodies());
		
		if(basket2.getBodies() != null)
			tempObj.addAll(basket2.getBodies());
		
		if(ball.getBodies() != null)
			tempObj.addAll(ball.getBodies());
		
		for(Player player : teammates)
			if(player.getBodies() != null)
				tempObj.addAll(player.getBodies());
		
		for(Player player : opponents)
			if(player.getBodies() != null)
				tempObj.addAll(player.getBodies());
		
		if(terrain.getInvisBodies() != null)
			tempObj.addAll(terrain.getInvisBodies());
		
		if(basket1.getInvisBodies() != null)
			tempObj.addAll(basket1.getInvisBodies());
		
		if(basket2.getInvisBodies() != null)
			tempObj.addAll(basket2.getInvisBodies());
		
		if(ball.getInvisBodies() != null)
			tempObj.addAll(ball.getInvisBodies());
		
		for(Player player : teammates)
			if(player.getInvisBodies() != null)
				tempObj.addAll(player.getInvisBodies());
		
		for(Player player : opponents)
			if(player.getInvisBodies() != null)
				tempObj.addAll(player.getInvisBodies());
		
		return tempObj;
	}
	
	public ArrayList<btCollisionObject> getCollObjectsOfAll(){
		ArrayList<btCollisionObject> tempObj = new ArrayList<btCollisionObject>();
		
		if(terrain.getCollisionObjects() != null)
			tempObj.addAll(terrain.getCollisionObjects());
		
		if(basket1.getCollisionObjects() != null)
			tempObj.addAll(basket1.getCollisionObjects());
		
		if(basket2.getCollisionObjects() != null)
			tempObj.addAll(basket2.getCollisionObjects());
		
		if(ball.getCollisionObjects() != null)
			tempObj.addAll(ball.getCollisionObjects());
		
		for(Player player : teammates)
			if(player.getCollisionObjects() != null)
				tempObj.addAll(player.getCollisionObjects());
		
		for(Player player : opponents)
			if(player.getCollisionObjects() != null)
				tempObj.addAll(player.getCollisionObjects());
		
		return tempObj;
	}

	@Override
	public boolean collides(Ray<Vector2> ray) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean findCollision(com.badlogic.gdx.ai.utils.Collision<Vector2> outputCollision, Ray<Vector2> inputRay) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
