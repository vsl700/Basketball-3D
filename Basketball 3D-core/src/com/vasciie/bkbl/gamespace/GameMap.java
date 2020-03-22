package com.vasciie.bkbl.gamespace;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.ai.steer.SteerableAdapter;
import com.badlogic.gdx.ai.utils.Ray;
import com.badlogic.gdx.ai.utils.RaycastCollisionDetector;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
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
import com.vasciie.bkbl.gamespace.entities.Ball;
import com.vasciie.bkbl.gamespace.entities.Entity;
import com.vasciie.bkbl.gamespace.entities.EntityType;
import com.vasciie.bkbl.gamespace.entities.Player;
import com.vasciie.bkbl.gamespace.objects.Basket;
import com.vasciie.bkbl.gamespace.objects.Camera;
import com.vasciie.bkbl.gamespace.objects.GameObject;
import com.vasciie.bkbl.gamespace.objects.ObjectType;
import com.vasciie.bkbl.gamespace.objects.Terrain;
import com.vasciie.bkbl.gamespace.rules.Rules;
import com.vasciie.bkbl.gamespace.rules.Rules.GameRule;
import com.vasciie.bkbl.gamespace.rules.Rules.RulesListener;
import com.vasciie.bkbl.gamespace.tools.InputController;

public class GameMap implements RaycastCollisionDetector<Vector3> {
	
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
			//if(objectsMap.get(userValue0).equals(ObjectType.TERRAIN.getId() + "Team") || objectsMap.get(userValue1).equals(ObjectType.TERRAIN.getId() + "Team"))
				//System.out.println(objectsMap.get(userValue0) + ";" + objectsMap.get(userValue1));
			
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
	
	Rules rules;
	
	ArrayList<Player> teammates;
	ArrayList<Player> opponents;
	Player mainPlayer;
	Ball ball;
	
	Terrain terrain;
	Basket basket1, basket2;
	Camera camera;
	
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
    boolean ruleTriggered; 
    boolean ruleTriggeredActing;//Whether the players are currently acting like after a broken rule (for example during a throw-in, until the thrower throws the ball and another player catches it, this boolean stays true)
    boolean playersReady; //Whether the players are in positions
    
    int index = 0;
	
	public GameMap(RulesListener rulesListener) {
		inputs = new InputController();
		
		rules = new Rules(this, rulesListener);
		
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
		
		terrain = (Terrain) ObjectType.createGameObject(ObjectType.TERRAIN.getId(), this, 0, 0, 0);
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
		
		createTerrainLanes();
		
		camera = (Camera) ObjectType.createGameObject(ObjectType.CAMERA.getId(), this, 0, 0, 0);
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
			
		basket1 = (Basket) ObjectType.createGameObject(ObjectType.HOMEBASKET.getId(), this, 0.1f, 0, 27);
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
		
		basket2 = (Basket) ObjectType.createGameObject(ObjectType.AWAYBASKET.getId(), this, 0.1f, 0, -27);
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
		
		//There is a separate function in player AI which should separate the player enough from the ball so that he can catch it
		//ball.setBoundingRadius(ball.getBoundingRadius() + mainPlayer.getWidth() / 2); 
		
		Gdx.input.setInputProcessor(inputs);
		
		startTimer = -1;//6
		playersReady = true;
	}
	
	private void createTerrainLanes() {
		btCollisionObject teamZone = terrain.getCollisionObjects().get(0);
		teamZone.setUserValue(index);
		teamZone.setCollisionFlags(teamZone.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
		
		dynamicsWorld.addCollisionObject(teamZone, OBJECT_FLAG, ENT_SPECIAL_FLAG);//The ball might collide which would cause this object to behave like an invisible wall
		teamZone.setContactCallbackFlag(OBJECT_FLAG);
		teamZone.setContactCallbackFilter(ENT_SPECIAL_FLAG);
		
		objectsMap.put(index, ObjectType.TERRAIN.getId() + "Team");
		collObjsInObjectMap.put(teamZone, terrain);
		collObjsValsMap.put(index, teamZone);
		
		index++;
	}
	
	private void createBall() {
		ball = (Ball) EntityType.createEntity(EntityType.BALL.getId(), this, new Vector3(0, 0, 0));
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
		
		if(rules.getTriggeredRule() != null) {
			rules.getTriggeredRule().clearRuleTriggerer();
			rules.clearTriggeredRule();
		}
		ruleTriggered = false;
		
		mainPlayer = null;
		
		createBall();
		
		gameRunning = false;
		ruleTriggeredActing = false;
	}
	
	public void update(float delta) {
		
		//camera.setWorldTransform(new Matrix4(mainPlayer.getModelInstance().transform).mul(mainPlayer.getCamMatrix()).mul(new Matrix4().setToTranslation(0, mainPlayer.getHeight(), -10)));
		
		// We're leaving dynamics world outside of the check below
		// because the AI might sometimes make mistakes and if 
		// players go one through another that wouldn't be very funny (for me)
		//float delta2 = Math.min(1f / 30f, delta);
		dynamicsWorld.stepSimulation(delta, 5, 1f / 60f);
			
		//if(gameRunning)
		if(!gameRunning) {
			turnPlayer(delta);
			updateInputs();
		}else controlPlayer(delta);
		if(playersReady && !gameRunning){
			if (!ruleTriggered) {
				if (startTimer <= 0)
					gameRunning = true;
				else
					startTimer -= delta;
			}
		}
		if(ruleTriggeredActing){
			updateFullGame(delta);
			
			/*//If players are not ready it means they are not in their target positions. So we should go through each one and check
			ArrayList<Player> allPlayers = getAllPlayers();
			boolean flag = true;
			for(Player p : allPlayers) {
				if(!p.getMoveVector().isZero()) { //We use player velocities for checking if they are in their places
					flag = false;
					break;
				}
			}
			
			if(flag) {
				playersReady = true;
				actionOver();
				rules.clearBrokenRuleWRuleBreaker();
			}
			
			return;*/

			if(rules.getTriggeredRule() == null) {
				actionOver();
				
				if(!gameRunning) {
					playersReady = true;
					gameRunning = true;
				}
				//rules.clearBrokenRuleWRuleBreaker();
				
				return;
			}
			
			playersReady = rules.getTriggeredRule().arePlayersReady();
			
			return;
		}
		
		if(gameRunning || ruleTriggeredActing)
			updateFullGame(delta);
		else updateGameEnvironment(delta);
		
	}
	
	private void updateGameEnvironment(float delta) {
		ball.update(delta);
		
		updatePlayers(delta);
		
		ball.onCycleEnd();
		
		endPlayersCycle();
	}
	
	private void updateFullGame(float delta) {
		ball.update(delta);
		
		rules.update();
		
		updatePlayers(delta);
		
		ball.onCycleEnd();
		
		endPlayersCycle();
	}
	
	private void updatePlayers(float delta) {
		GdxAI.getTimepiece().update(delta);
		
		for(Player e : getAllPlayers())
			e.update(delta);
	}
	
	private void endPlayersCycle() {
		for(Player e : getAllPlayers())
			e.onCycleEnd();
	}
	
	public void render(ModelBatch mBatch, Environment environment) {
		for(Player e : getAllPlayers())
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
	
	public void onRuleTriggered(GameRule rule) {
		//The game will just stop here
		gameRunning = false;
		ruleTriggered = true;
		playersReady = false;
		ruleTriggeredActing = false;
	}
	
	public void onRuleBrokenContinue() {
		// When the player clicks a specified (or any key), the players will go
		// to their specified by the rule places
		//
		// So this method will just give the players target positions according
		// to the broken rule. (Update void) After the players are ready
		// (moveVecs == 0 && !AIMemory(only one will be enough to show the
		// current state).target.isZero()) start the timer (brokenRule == false)
		// and clear the broken rule from Rule
		ruleTriggered = false;
		ruleTriggeredActing = true;
		startTimer = 0.9f;
		System.out.println("On rule broken continue");
		// Finally, after a quick timeout the game will continue
	}
	
	/**
	 * Sets a new target position of a player. I created this method because of the Rules system. Read the note inside the {@link GameMap#onRuleBrokenContinue()} method
	 * @param pos - the target position
	 * @param index - the index of the player
	 * @param radius - the bounding radius of the target
	 */
	public void setPlayerTargetPosition(final Vector3 pos, Player player) {
		// I decided to put the target setter outside of the rules's methods
		// because I'm using a SteerableAdapter to set the targets and inside
		// the adapter I'm also setting the bounding radius and if I have to
		// change it (or add something), I'll have to do it everywhere on
		// all game rules.
		
		if(pos.x < 0)
			pos.x = 0;
		else if(pos.x > terrain.getWidth() / 2)
			pos.x = terrain.getWidth() / 2;
		
		if(pos.z < 0)
			pos.z = 0;
		else if(pos.z > terrain.getDepth() / 2)
			pos.z = terrain.getDepth() / 2;
		
		player.getBrain().getMemory().setTargetPosition(new SteerableAdapter<Vector3>() {
			@Override
			public Vector3 getPosition() {
				return pos;
			}
		});
	}
	
	public void actionOver() {
		ruleTriggeredActing = false;
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
		for(Player e : getAllPlayers()) {
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
		movePlayer(delta);
		
		turnPlayer(delta);
		
		updateInputs();
	}
	
	private void updateInputs() {
		inputs.update(mainPlayer.isHoldingBall());
	}
	
	private void movePlayer(float delta) {
		Matrix4 playerM = new Matrix4().set(mainPlayer.getMainBody().getWorldTransform().val);
		
		Quaternion dir = playerM.getRotation(new Quaternion());
		
		Vector3 tempVec = new Vector3(0, 0, 1);
		tempVec.rotate(dir.getYaw(), 0, 1, 0);
		
		float dirX = tempVec.x;
		float dirZ = tempVec.z;
		
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
		
		if(inputs.isFocusPressed())
			mainPlayer.focus(false);
		
		if(inputs.isShootPressed()) {
			mainPlayer.interactWithBallS();
		}
		else if(inputs.isDribbleLPressed()) {
			mainPlayer.interactWithBallL();
		}
		else if(inputs.isDribbleRPressed()) {
			mainPlayer.interactWithBallR();
		}
	}
	
	private void turnPlayer(float delta) {
		float turnY = Gdx.input.getDeltaX(); //Around the Y-axis
		float turnX = Gdx.input.getDeltaY(); //Around the X-axis
		
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
	}
	
	public void scoreTeam(boolean triple) {
		if(triple)
			teamScore+= 3;
		else teamScore+= 2;
	}
	
	public void scoreOpp(boolean triple) {
		if(triple)
			oppScore+= 3;
		else oppScore+= 2;
	}
	
	public btDynamicsWorld getDynamicsWorld() {
		return dynamicsWorld;
	}
	
	public HashMap<Integer, String> getObjectsMap(){
		return objectsMap;
	}
	
	public HashMap<btCollisionObject, Entity> getCollObjsInEntityMap() {
		return collObjsInEntityMap;
	}

	public HashMap<btCollisionObject, GameObject> getCollObjsInObjectMap() {
		return collObjsInObjectMap;
	}

	public HashMap<Integer, btCollisionObject> getCollObjsValsMap() {
		return collObjsValsMap;
	}

	public Player getMainPlayer() {
		return mainPlayer;
	}
	
	public Player getTeammateHolding() {
		if(currentPlayerHoldTeam > -1)
			return teammates.get(currentPlayerHoldTeam);
		
		return null;
	}
	
	public Player getOpponentHolding() {
		if(currentPlayerHoldOpp > -1)
			return opponents.get(currentPlayerHoldOpp);
		
		return null;
	}
	
	public Player getHoldingPlayer() {
		Player temp = getTeammateHolding();
		
		return temp != null ? temp : getOpponentHolding();
	}
	
	public ArrayList<Player> getTeammates(){
		return teammates;
	}
	
	public ArrayList<Player> getOpponents(){
		return opponents;
	}
	
	public ArrayList<Player> getAllPlayers(){
		ArrayList<Player> temp = new ArrayList<Player>();
		temp.addAll(teammates);
		temp.addAll(opponents);
		
		return temp;
	}
	
	public Vector3 getMainPlayerTranslation() {
		return mainPlayer.getMainBody().getWorldTransform().getTranslation(new Vector3());
	}
	
	public Quaternion getMainPlayerRotation() {
		return mainPlayer.getMainBody().getWorldTransform().getRotation(new Quaternion());
	}
	
	public Ball getBall() {
		return ball;
	}
	
	public Terrain getTerrain() {
		return terrain;
	}
	
	public Basket getHomeBasket() {
		return basket1;
	}
	
	public Basket getAwayBasket() {
		return basket2;
	}
	
	public Camera getCamera() {
		return camera;
	}
	
	public int getCurrentPlayerHoldTeam() {
		return currentPlayerHoldTeam;
	}

	public int getCurrentPlayerHoldOpp() {
		return currentPlayerHoldOpp;
	}

	public int getTeamScore() {
		return teamScore;
	}
	
	public int getOppScore() {
		return oppScore;
	}
	
	public float getTimer() {
		return startTimer;
	}
	
	public boolean isGameRunning() {
		return gameRunning;
	}
	
	public boolean isRuleTriggered() {
		return ruleTriggered;
	}

	public boolean isRuleTriggeredActing() {
		return ruleTriggeredActing;
	}

	public boolean isPlayersReady() {
		return playersReady;
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
		
		for(Player player : getAllPlayers())
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
		
		for(Player player : getAllPlayers())
			if(player.getCollisionObjects() != null)
				tempObj.addAll(player.getCollisionObjects());
		
		
		return tempObj;
	}

	@Override
	public boolean collides(Ray<Vector3> ray) {
		
		return false;
	}

	@Override
	public boolean findCollision(com.badlogic.gdx.ai.utils.Collision<Vector3> outputCollision, Ray<Vector3> inputRay) {
		
		return false;
	}
	
}
