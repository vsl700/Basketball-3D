package com.gamesbg.bkbl.gamespace;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
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
import com.gamesbg.bkbl.gamespace.entities.Entity;
import com.gamesbg.bkbl.gamespace.entities.EntityType;
import com.gamesbg.bkbl.gamespace.entities.Player;
import com.gamesbg.bkbl.gamespace.objects.GameObject;
import com.gamesbg.bkbl.gamespace.objects.ObjectType;
import com.gamesbg.bkbl.gamespace.tools.InputController;

public class GameMap {
	
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
			//players.get(userValue0).setCollision(true);
			//players.get(userValue1).setCollision(true);
			//if(((userValue0 == 11 || userValue0 == 19) && userValue1 == 21) || ((userValue1 == 11 || userValue1 == 19) && userValue0 == 21))
				//System.out.println(userValue0 + " collided with " + userValue1);
			//if((objectsMap.get(userValue0).equals("ballObj") && objectsMap.get(userValue1).equals("teamObj")) || (objectsMap.get(userValue1).equals("ballObj") && objectsMap.get(userValue0).equals("teamObj")))
			//if(objectsMap.get(userValue0).equals("ball") || objectsMap.get(userValue0).equals("ballObj") || objectsMap.get(userValue1).equals("ball") || objectsMap.get(userValue1).equals("ballObj"))
			//if(objectsMap.get(userValue0).equals("cam") || objectsMap.get(userValue1).equals("cam"))
				//System.out.println(userValue0 + " (" + objectsMap.get(userValue0) + ")" + " with " + userValue1 + " (" + objectsMap.get(userValue1) + ")");
			//if(userValue0 == 23 || userValue1 == 23)
				//System.out.println(userValue0 + " and " + userValue1);
			//System.out.println(userValue0 + " and " + userValue1);
			
			Entity temp0 = null, temp1 = null;
			//GameObject tempObj = null;
			//FIXME I think there is a way to achieve the same thing only with one iteration. Think it again and try to shorten the operations. It would be great!
			ArrayList<Player> temp = new ArrayList<Player>();
			temp.addAll(teammates);
			temp.addAll(opponents);
			
			btCollisionObject collObj0 = null, collObj1 = null;
			collObj0 = getCollObjByUserValueAndEntity(ball, userValue0);
			if(collObj0 == null) {
				
				for(Player p : temp) {
					collObj0 = getCollObjByUserValueAndEntity(p, userValue0);
					if(collObj0 != null) {
						temp0 = p;
						break;
					}
						
				}
			}
			else temp0 = ball;
			
			collObj1 = getCollObjByUserValueAndEntity(ball, userValue1);
			if(collObj1 == null) {
				for(Player p : temp) {
					collObj1 = getCollObjByUserValueAndEntity(p, userValue1);
					if(collObj1 != null) {
						temp1 = p;
						break;
					}
				}
			}
			else temp1 = ball;
			
			if(collObj0 == null) {
				collObj0 = getCollObjByUserValueAndGameObj(terrain, userValue0);
				if(collObj0 == null) {
					collObj0 = getCollObjByUserValueAndGameObj(basket1, userValue0);
					if(collObj0 == null) {
						getCollObjByUserValueAndGameObj(basket2, userValue0);
						//tempObj = basket2;
					}
					//else tempObj = basket1;
				}
				
				//else tempObj = terrain;
			}
			
			else if(collObj1 == null) {
				collObj1 = getCollObjByUserValueAndGameObj(terrain, userValue1);
				if(collObj1 == null) {
					collObj1 = getCollObjByUserValueAndGameObj(basket1, userValue1);
					if(collObj1 == null) {
						getCollObjByUserValueAndGameObj(basket2, userValue1);
						//tempObj = basket2;
					}
					//else tempObj = basket1;
				}
				
				//else tempObj = terrain;
			}
			
			
			//Entity temp0 = getCollidingEntity(collObj0, userValue0), temp1 = getCollidingEntity(collObj1, userValue1);
			
			if (collObj0 != null && collObj1 != null) {
				if (temp0 != null)
					temp0.collisionOccured(collObj0, collObj1);

				if (temp1 != null)
					temp1.collisionOccured(collObj1, collObj0);
			}
			
			/*System.out.println(temp0);
			System.out.println(temp1);*/
			//System.out.println(collObj0);
			//System.out.println(collObj1);
			//System.out.println();
			
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
    
    //Current game properties
    int currentPlayerHoldTeam, currentPlayerHoldOpp;
    
    int teamScore, oppScore;
    
    boolean gameRunning = true;//Whether or not the players can play
    
    
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
		
		terrain = ObjectType.createGameObject(ObjectType.TERRAIN.getId(), this, 0, 0, 0);
		for(btRigidBody co : terrain.getBodies()) {
			co.setUserValue(index);
			
			co.setCollisionFlags(co.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);
			dynamicsWorld.addRigidBody(co, GROUND_FLAG, ALL_FLAG);
			co.setContactCallbackFlag(GROUND_FLAG);
			co.setContactCallbackFilter(ENTITY_FLAG);
			co.setActivationState(Collision.DISABLE_DEACTIVATION);
			
			objectsMap.put(index, ObjectType.TERRAIN.getId());
			
			index++;
		}
		
		for(btRigidBody co : terrain.getInvisBodies()) {
			co.setUserValue(index);
			//co.setCollisionFlags(co.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
			dynamicsWorld.addRigidBody(co, GROUND_FLAG, ALL_FLAG);
			//System.out.println(co.getUserValue());
			co.setContactCallbackFlag(GROUND_FLAG);
			co.setContactCallbackFilter(ENTITY_FLAG);
			////System.out.println(co.getUserValue());
			//co.setActivationState(Collision.WANTS_DEACTIVATION);
			
			objectsMap.put(index, ObjectType.TERRAIN.getId() + "Inv");
			
			index++;
		}
		
		camera = ObjectType.createGameObject(ObjectType.CAMERA.getId(), this, 0, 0, 0);
		for(btRigidBody co : camera.getBodies()) {
			co.setUserValue(index);
			co.setCollisionFlags(co.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
			dynamicsWorld.addRigidBody(co, CAMERA_FLAG, GROUND_FLAG);
			//System.out.println(co.getUserValue());
			co.setContactCallbackFlag(CAMERA_FLAG);
			co.setContactCallbackFilter(GROUND_FLAG);
			
			objectsMap.put(index, ObjectType.CAMERA.getId());
			
			index++;
		}
			
		basket1 = ObjectType.createGameObject(ObjectType.HOMEBASKET.getId(), this, 0.1f, 0, 27);
		for(btRigidBody co : basket1.getBodies()) {
			co.setUserValue(index);
			//co.setCollisionFlags(co.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
			dynamicsWorld.addRigidBody(co, OBJECT_FLAG, ALL_FLAG);
			//System.out.println(co.getUserValue());
			co.setContactCallbackFlag(OBJECT_FLAG);
			co.setContactCallbackFilter(ENTITY_FLAG);
			
			objectsMap.put(index, ObjectType.HOMEBASKET.getId());
			
			index++;
		}
		
		for(btCollisionObject co : basket1.getCollisionObjects()) {
			co.setUserValue(index);
			co.setCollisionFlags(co.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
			//System.out.println(co.getUserValue());
			dynamicsWorld.addCollisionObject(co, SPECIAL_FLAG, ENT_SPECIAL_FLAG);
			co.setContactCallbackFlag(SPECIAL_FLAG);
			co.setContactCallbackFilter(ENT_SPECIAL_FLAG);

			//System.out.println(co.getContactCallbackFlag());
			//System.out.println(co.getContactCallbackFilter());
			//System.out.println(co.getUserValue());
			objectsMap.put(index, ObjectType.HOMEBASKET.getId() + "Obj");
			
			index++;
			//dynamicsWorld.addRigidBody(co);
		}
		
		basket2 = ObjectType.createGameObject(ObjectType.AWAYBASKET.getId(), this, 0.1f, 0, -27);
		basket2.setRotation(0, 1, 0, 180);
		for(btRigidBody co : basket2.getBodies()) {
			co.setUserValue(index);
			//co.setCollisionFlags(co.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
			//System.out.println(co.getUserValue());
			dynamicsWorld.addRigidBody(co, OBJECT_FLAG, ALL_FLAG);
			co.setContactCallbackFlag(OBJECT_FLAG);
			co.setContactCallbackFilter(ENTITY_FLAG);
			
			objectsMap.put(index, ObjectType.AWAYBASKET.getId());
			
			index++;
		}
		
		for(btCollisionObject co : basket2.getCollisionObjects()) {
			co.setUserValue(index);
			co.setCollisionFlags(co.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
			//System.out.println(co.getUserValue());
			dynamicsWorld.addCollisionObject(co, SPECIAL_FLAG, ENT_SPECIAL_FLAG);
			co.setContactCallbackFlag(SPECIAL_FLAG);
			co.setContactCallbackFilter(ENT_SPECIAL_FLAG);
			////System.out.println(co.getUserValue());
			objectsMap.put(index, ObjectType.AWAYBASKET.getId() + "Obj");
			
			index++;
			//dynamicsWorld.addRigidBody(co);
		}
		
		ball = EntityType.createEntity(EntityType.BALL.getId(), this, new Vector3(0, 0, 0));
		for (btRigidBody co : ball.getBodies()) {
			co.setUserValue(index);
			// ball.getBody().setCollisionFlags(ball.getBody().getCollisionFlags()
			// | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
			co.setCollisionFlags(co.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
			//co.applyCentralForce(new Vector3(0, 550, 0));
			//co.applyCentralForce(new Vector3());
			//dynamicsWorld.addRigidBody(co);
			//co.setActivationState(Collision.WANTS_DEACTIVATION);
			//System.out.println(co.getUserValue());
			dynamicsWorld.addRigidBody(co, ENTITY_FLAG, ALL_FLAG);
			co.setContactCallbackFlag(ENTITY_FLAG);
			co.setContactCallbackFilter(ALL_FLAG);
			
			//System.out.println(co.getContactCallbackFlag());
			//System.out.println(co.getContactCallbackFilter());
			
			objectsMap.put(index, EntityType.BALL.getId());

			index++;
		}
		
		for(btCollisionObject co : ball.getCollisionObjects()) {
			co.setUserValue(index);
			co.setCollisionFlags(co.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
			
			dynamicsWorld.addCollisionObject(co, ENT_SPECIAL_FLAG, SPECIAL_FLAG);
			co.setContactCallbackFlag(ENT_SPECIAL_FLAG);
			co.setContactCallbackFilter(SPECIAL_FLAG);

			//System.out.println(co.getContactCallbackFlag());
			//System.out.println(co.getContactCallbackFilter());
			//System.out.println(co.getUserValue());
			objectsMap.put(index, EntityType.BALL.getId() + "Obj");
			
			index++;
		}
		
		
		teammates = new ArrayList<Player>(5);
		opponents = new ArrayList<Player>(5);
	}
	
	public void spawnPlayers(int count) {
		int index2 = index;
		int playerIndex = 0;
		
		for(int i = 0; i < count; i++) {
			Player teammate = EntityType.createPlayer(EntityType.TEAMMATE.getId(), this, new Vector3(spawnCoords[i][0], spawnCoords[i][1], spawnCoords[i][2]));
			//players.add(EntityType.createEntity("team", this, 0, 0, 0));
			for (btRigidBody co : teammate.getBodies()) {
				co.setUserValue(index2);
				co.setCollisionFlags(co.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
				
				//if(co.equals(opponent.getMainBody())) {
					
					////System.out.println(co.getUserValue());
					if(teammate.getMainBody().equals(co)) {
						dynamicsWorld.addRigidBody(co, ENTITY_FLAG, ALL_FLAG);
						co.setContactCallbackFlag(ENTITY_FLAG);
						co.setContactCallbackFilter(ALL_FLAG);
						
						objectsMap.put(index2, EntityType.TEAMMATE.getId());
					}
					else {
						dynamicsWorld.addRigidBody(co, ENTITY_FLAG, ALL_FLAG);
						co.setContactCallbackFlag(ENTITY_FLAG);
						co.setContactCallbackFilter(ALL_FLAG);
						
						objectsMap.put(index2, EntityType.TEAMMATE.getId() + "Hand");
					}
					//co.setContactCallbackFlag(PLAYER_FLAG);
					//co.setContactCallbackFilter(ALL_FLAG);
				//}else {
					//dynamicsWorld.addRigidBody(co, PART_FLAG, PLAYER_FLAG);
					//co.setContactCallbackFlag(ENTITY_FLAG);
					//co.setContactCallbackFilter(PLAYER_FLAG);
				//}
				
				index2++;
			}
			
			for (btCollisionObject co : teammate.getCollisionObjects()) {
				co.setUserValue(index2);
				co.setCollisionFlags(co.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
				
				//if(co.equals(opponent.getMainBody())) {
					dynamicsWorld.addCollisionObject(co, SPECIAL_FLAG, ENT_SPECIAL_FLAG);
					co.setContactCallbackFlag(SPECIAL_FLAG);
					co.setContactCallbackFilter(ENT_SPECIAL_FLAG);
					
					////System.out.println(co.getUserValue());
					objectsMap.put(index2, EntityType.TEAMMATE.getId() + "Obj");
					//co.setContactCallbackFlag(PLAYER_FLAG);
					//co.setContactCallbackFilter(ALL_FLAG);
				//}else {
					//dynamicsWorld.addRigidBody(co, PART_FLAG, PLAYER_FLAG);
					//co.setContactCallbackFlag(ENTITY_FLAG);
					//co.setContactCallbackFilter(PLAYER_FLAG);
				//}
				
				index2++;
			}
			/*for(btRigidBody co : opponent.getInvisBodies()) {
				co.setUserValue(index2);
				co.setCollisionFlags(co.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
				//dynamicsWorld.addRigidBody(co);
				co.setContactCallbackFlag(ENT_SPECIAL_FLAG);
				co.setContactCallbackFilter(GROUND_FLAG);
				
				index2++;
			}*/
			teammate.turnY(180);
			/*for(int j = 0; j < opponent.getBodies().size(); j++)
				for(int k = j + 1; k < opponent.getBodies().size(); k++)
					opponent.getBodies().get(j).setIgnoreCollisionCheck(opponent.getBodies().get(k), true);*/
			
			teammates.add(teammate);
			
			//Skipping the main player
			if(playerIndex > 0)
				teammate.setPlayerIndex(playerIndex);
			playerIndex++;
		}
		
		for(int i = 0; i < count; i++) {
			Player opponent = EntityType.createPlayer(EntityType.OPPONENT.getId(), this, new Vector3(spawnCoords[i][0], spawnCoords[i][1], -spawnCoords[i][2]));
			//players.add(EntityType.createEntity("team", this, 0, 0, 0));
			for (btRigidBody co : opponent.getBodies()) {
				co.setUserValue(index2);
				co.setCollisionFlags(co.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
				
				//if(co.equals(opponent.getMainBody())) {
					
					////System.out.println(co.getUserValue());
					if(opponent.getMainBody().equals(co)) {
						dynamicsWorld.addRigidBody(co, ENTITY_FLAG, ALL_FLAG);
						co.setContactCallbackFlag(ENTITY_FLAG);
						co.setContactCallbackFilter(ALL_FLAG);
						
						objectsMap.put(index2, EntityType.OPPONENT.getId());
					}
					else {
						dynamicsWorld.addRigidBody(co, ENTITY_FLAG, ALL_FLAG);
						co.setContactCallbackFlag(ENTITY_FLAG);
						co.setContactCallbackFilter(ALL_FLAG);
						
						objectsMap.put(index2, EntityType.OPPONENT.getId() + "Hand");
					}
					//co.setContactCallbackFlag(PLAYER_FLAG);
					//co.setContactCallbackFilter(ALL_FLAG);
				//}else {
					//dynamicsWorld.addRigidBody(co, PART_FLAG, PLAYER_FLAG);
					//co.setContactCallbackFlag(ENTITY_FLAG);
					//co.setContactCallbackFilter(PLAYER_FLAG);
				//}
				
				index2++;
			}
			
			for (btCollisionObject co : opponent.getCollisionObjects()) {
				co.setUserValue(index2);
				co.setCollisionFlags(co.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
				
				//if(co.equals(opponent.getMainBody())) {
					dynamicsWorld.addCollisionObject(co, SPECIAL_FLAG, ENT_SPECIAL_FLAG);
					co.setContactCallbackFlag(SPECIAL_FLAG);
					co.setContactCallbackFilter(ENT_SPECIAL_FLAG);
					
					////System.out.println(co.getUserValue());
					objectsMap.put(index2, EntityType.OPPONENT.getId() + "Obj");
					//co.setContactCallbackFlag(PLAYER_FLAG);
					//co.setContactCallbackFilter(ALL_FLAG);
				//}else {
					//dynamicsWorld.addRigidBody(co, PART_FLAG, PLAYER_FLAG);
					//co.setContactCallbackFlag(ENTITY_FLAG);
					//co.setContactCallbackFilter(PLAYER_FLAG);
				//}
				
				index2++;
			}
			/*for(btRigidBody co : opponent.getInvisBodies()) {
				co.setUserValue(index2);
				co.setCollisionFlags(co.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
				//dynamicsWorld.addRigidBody(co);
				co.setContactCallbackFlag(ENT_SPECIAL_FLAG);
				co.setContactCallbackFilter(GROUND_FLAG);
				
				index2++;
			}*/
			/*for(int j = 0; j < opponent.getBodies().size(); j++)
				for(int k = j + 1; k < opponent.getBodies().size(); k++)
					opponent.getBodies().get(j).setIgnoreCollisionCheck(opponent.getBodies().get(k), true);*/
			
			opponents.add(opponent);
			
			//Skipping the main player
			if(playerIndex > 0)
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
	}
	
	public void clear() {
		disposePlayers();
		teammates.clear();
		opponents.clear();
	}
	
	public void update(float delta) {
		/*for (int i = 0; i < players.size(); i++) 
			for (int j = i + 1; j < players.size(); j++)
				for (btCollisionObject c0 : players.get(i).getCollisionObject()) 
					for(btCollisionObject c1 : players.get(j).getCollisionObject()) 
						checkCollision(c0, c1);*/
		
		//if(!Gdx.input.isButtonPressed(Buttons.LEFT))
		
		//System.out.println("UPDATE");
		
		camera.setWorldTransform(new Matrix4(mainPlayer.getModelInstance().transform).mul(mainPlayer.getCamMatrix()).mul(new Matrix4().setToTranslation(0, mainPlayer.getHeight(), -10)));
		
		float delta2 = Math.min(1f / 30f, delta);
		dynamicsWorld.stepSimulation(delta2, 15, 1f / 60f);
		/*ball.getBody().getWorldTransform(ball.getModelInstance().transform);
		for(Entity e : players) {
			e.getBody().getWorldTransform(e.getModelInstance().transform);
		}*/
		
		//mainPlayer.turn(delta * -38);
		
		//System.out.println(delta * 38 + " ; " + getMainPlayerRotation().getYaw());
		//System.out.println(ball.getMainBody().checkCollideWith(terrain.getBodies().get(0)) + " from GameMap");
		//collisionWorld.performDiscreteCollisionDetection();
		
		controlPlayer(delta);
		
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
		//System.out.println(body.getUserValue());
		dynamicsWorld.addRigidBody(body, (short) group, (short) mask);
	}
	
	public void removeRigidBody(btRigidBody body) {
		//System.out.println(body.getUserValue());
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
		
		//dispatcher.dispose();
        //collisionConfig.dispose();
        //collisionWorld.dispose();
		//broadphase.dispose();
        dynamicsWorld.dispose();		
		dynDispatcher.dispose();
        dynCollConfig.dispose();
        dynBroadphase.dispose();
        constraintSolver.dispose();
        
        /*softDispatcher.dispose();
        softCollConfig.dispose();
        softBroadphase.dispose();
        softConstSolver.dispose();
        softWorld.dispose();*/
        
        contactListener.dispose();
	}
	
	private void disposePlayers() {
		//if(teammates != null)
		for(Player e : teammates) {
			for(btRigidBody co : e.getBodies())
			//dynamicsWorld.removeCollisionObject(co);
			dynamicsWorld.removeRigidBody(co);
			
			for(btCollisionObject co : e.getCollisionObjects())
				dynamicsWorld.removeCollisionObject(co);
			
			e.dispose();
		}
		
		//if(opponents != null)
			for(Player e : opponents) {
				for(btRigidBody co : e.getBodies())
				//dynamicsWorld.removeCollisionObject(co);
				dynamicsWorld.removeRigidBody(co);
				
				for(btCollisionObject co : e.getCollisionObjects())
					dynamicsWorld.removeCollisionObject(co);
				
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

		//System.out.println(dirX + " ; " + dirZ);
		//System.out.print(dirX * delta * multiplier + "; " + dirZ * delta * multiplier + "; ");
		
		//if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) && Gdx.input.isKeyPressed(Keys.W)) {
			//if (Gdx.input.isKeyPressed(Keys.W))
				// mainPlayer.getBody().setLinearVelocity(new Vector3(dirX * delta * multiplier, 0, dirZ * delta * multiplier));
				//mainPlayer.run(new Vector3(dirX * delta, 0, dirZ * delta));
			/*else if (Gdx.input.isKeyPressed(Keys.S))
				// mainPlayer.getBody().setLinearVelocity(new Vector3(dirX * -delta * multiplier, 0, dirZ * -delta * multiplier));
				mainPlayer.run(new Vector3(dirX * -delta, 0, dirZ * -delta));
			// else mainPlayer.getBody().setLinearVelocity(new Vector3());

			if (Gdx.input.isKeyPressed(Keys.A))
				// mainPlayer.getBody().setLinearVelocity(new Vector3(dirZ * delta * multiplier, 0, dirX * -delta * multiplier));
				mainPlayer.run(new Vector3(dirZ * delta, 0, dirX * -delta));
			else if (Gdx.input.isKeyPressed(Keys.D))
				// mainPlayer.getBody().setLinearVelocity(new Vector3(dirZ * -delta * multiplier, 0, dirX * delta * multiplier));
				mainPlayer.run(new Vector3(dirZ * -delta, 0, dirX * delta));*/
		//}
		//else {
		if(inputs.isSprintPressed()) {
			if (inputs.isForwardPressed()) {
				// mainPlayer.getBody().setLinearVelocity(new Vector3(dirX * delta * multiplier, 0, dirZ * delta * multiplier));
				mainPlayer.run(new Vector3(dirX * delta, 0, dirZ * delta));
				
				/*if (Gdx.input.isKeyPressed(Keys.A))
					// mainPlayer.getBody().setLinearVelocity(new Vector3(dirZ * delta * multiplier, 0, dirX * -delta * multiplier));
					mainPlayer.run(new Vector3(dirZ * delta, 0, dirX * -delta));
				else if (Gdx.input.isKeyPressed(Keys.D))
					// mainPlayer.getBody().setLinearVelocity(new Vector3(dirZ * -delta * multiplier, 0, dirX * delta * multiplier));
					mainPlayer.run(new Vector3(dirZ * -delta, 0, dirX * delta));*/
			}
		}
		else {
			if (inputs.isForwardPressed()) {
				// mainPlayer.getBody().setLinearVelocity(new Vector3(dirX * delta * multiplier, 0, dirZ * delta * multiplier));
				mainPlayer.walk(new Vector3(dirX * delta, 0, dirZ * delta));
			}
			else if (inputs.isBackwardPressed())
				// mainPlayer.getBody().setLinearVelocity(new Vector3(dirX * -delta * multiplier, 0, dirZ * -delta * multiplier));
				mainPlayer.walk(new Vector3(dirX * -delta, 0, dirZ * -delta));
			// else mainPlayer.getBody().setLinearVelocity(new Vector3());

			if (inputs.isStrLeftPressed())
				// mainPlayer.getBody().setLinearVelocity(new Vector3(dirZ * delta * multiplier, 0, dirX * -delta * multiplier));
				mainPlayer.walk(new Vector3(dirZ * delta, 0, dirX * -delta));
			else if (inputs.isStrRightPressed())
				// mainPlayer.getBody().setLinearVelocity(new Vector3(dirZ * -delta * multiplier, 0, dirX * delta * multiplier));
				mainPlayer.walk(new Vector3(dirZ * -delta, 0, dirX * delta));
		}
		
		mainPlayer.shootPowerScroll(-inputs.GetScroll());
		
		if(inputs.isShootPressed()) {
			mainPlayer.interactWithBallS();
		}
		
		else if(inputs.isDribbleLPressed()) {
			mainPlayer.interactWithBallL();
		}
		else if(inputs.isDribbleRPressed()) {
			mainPlayer.interactWithBallR();
		}
		
		//if(Gdx.input.isKeyJustPressed(Keys.SPACE))
			//mainPlayer.jump();
		
			//}
		
		float turnY = Gdx.input.getDeltaX(); //Around the Y-axis
		float turnX = Gdx.input.getDeltaY(); //Around the X-axis
		
		if(Math.abs(turnY) < Gdx.graphics.getWidth() / 4) {
			mainPlayer.turnY(turnY * delta * 9);
			
			//if(turnY != 0.f)
				//System.out.println(Math.abs(turnY) + " ; " + Gdx.graphics.getWidth() / 4);
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
		//Gdx.input.setCursorPosition(0, 0);
		//else mainPlayer.getBody().setLinearVelocity(new Vector3());
		/*if (Gdx.input.isKeyPressed(Keys.W))
			mainPlayer.getBody().applyCentralForce(new Vector3(dirX * delta + multiplier, 0, dirZ * delta + multiplier));
		else if (Gdx.input.isKeyPressed(Keys.S))
			mainPlayer.getBody().applyCentralForce(new Vector3(dirX * delta + multiplier, 0, dirZ * delta + multiplier));
		else mainPlayer.getBody().applyCentralForce(new Vector3(0, 0, 0));
		
		if (Gdx.input.isKeyPressed(Keys.A))
			mainPlayer.getBody().applyCentralForce(new Vector3(dirZ * delta + multiplier, 0, dirX * -delta + multiplier));
		else if (Gdx.input.isKeyPressed(Keys.D))
			mainPlayer.getBody().applyCentralForce(new Vector3(dirZ * -delta + multiplier, 0, dirX * delta + multiplier));
		else mainPlayer.getBody().applyCentralForce(new Vector3(0, 0, 0));*/
		
		//float a = Gdx.input.getDeltaY();
		//float b = Gdx.input.getDeltaX();

		//mainPlayer.getBody().setAngularVelocity(new Vector3(-a, -b, 0));
		
		//playerM.rotate(new Vector3(0.1f, 0, 0), -a / 6);
		//playerM.rotate(new Vector3(0, 0.1f, 0), -b / 6);
		
		//System.out.println(playerM.getTranslation(new Vector3()).x + "; " + playerM.getTranslation(new Vector3()).y + "; " + playerM.getTranslation(new Vector3()).z + "; " + delta);
		
		/*mainPlayer.getModelInstance().transform = playerM;
		mainPlayer.getBody().setWorldTransform(playerM);*/
		
		//mainPlayer.moved(playerM);
		
	}
	
	/*private Entity getCollidingEntity(btCollisionObject collObj, int userValue) {
		//collObj = getCollObjByUserValueAndEntity(ball, userValue);
		if(getCollObjByUserValueAndEntity(ball, userValue) != null)
			return ball;
		else {
			for(Entity e : players) {
				//collObj = getCollObjByUserValueAndEntity(e, userValue);
				if(getCollObjByUserValueAndEntity(e, userValue) != null)
					return e;
			}
		}
		
		return null;
	}*/
	
	private btCollisionObject getCollObjByUserValueAndGameObj(GameObject e, int userValue) {
		for(btCollisionObject obj : e.getBodies()) {
			if(obj.getUserValue() == userValue) {
				//System.out.println("Returned " + userValue);
				return obj;
			}
		}
		
		if(e.getCollisionObjects() != null)
		for(btCollisionObject obj : e.getCollisionObjects()) {
			if(obj.getUserValue() == userValue) {
				//System.out.println("Returned " + userValue);
				return obj;
			}
		}
		
		return null;
	}
	
	private btCollisionObject getCollObjByUserValueAndEntity(Entity e, int userValue) {
		for(btCollisionObject obj : e.getBodies()) {
			if(obj.getUserValue() == userValue) {
				//System.out.println("Returned " + userValue);
				return obj;
			}
		}
		
		if(e.getCollisionObjects() != null)
		for(btCollisionObject obj : e.getCollisionObjects()) {
			if(obj.getUserValue() == userValue) {
				//System.out.println("Returned " + userValue);
				return obj;
			}
		}
		
		return null;
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
	
	public Entity getBall() {
		return ball;
	}
	
	public GameObject getTerrain() {
		return terrain;
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
	
}
