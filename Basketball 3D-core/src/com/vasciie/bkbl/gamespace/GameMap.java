package com.vasciie.bkbl.gamespace;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.ai.steer.SteerableAdapter;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelCache;
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
import com.vasciie.bkbl.MyGdxGame;
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
import com.vasciie.bkbl.gamespace.tools.VEThread;
import com.vasciie.bkbl.gamespace.zones.Zones;
import com.vasciie.bkbl.gui.GUIRenderer;
import com.vasciie.bkbl.screens.SettingsScreen;

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

    private class ObjectContactListener extends ContactListener {
        @Override
        public boolean onContactAdded(int userValue0, int partId0, int index0, int userValue1, int partId1, int index1) {
            //if(objectsMap.get(userValue0).equals(ObjectType.TERRAIN.getId() + "Team") || objectsMap.get(userValue1).equals(ObjectType.TERRAIN.getId() + "Team"))
            //System.out.println(objectsMap.get(userValue0) + ";" + objectsMap.get(userValue1));

            btCollisionObject tempCollObj0 = collObjsValsMap.get(userValue0), tempCollObj1 = collObjsValsMap.get(userValue1);
            Entity temp0 = collObjsInEntityMap.get(tempCollObj0), temp1 = collObjsInEntityMap.get(tempCollObj1);

            if (temp0 != null) {
                temp0.collisionOccured(tempCollObj0, tempCollObj1);
            }
            if (temp1 != null) {
                temp1.collisionOccured(tempCollObj1, tempCollObj0);
            }

            return true;
        }
    }

    private VEThread physicsThread;
    Runnable dynamicsWorldRunnable;

    Rules rules;
    Zones zones;

    ArrayList<Player> teammates;
    ArrayList<Player> opponents;
    Player mainPlayer;
    Ball ball;

    Terrain terrain;
    Basket basket1, basket2;
    Camera camera;
    
    ModelCache mCache;

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

    int difficulty = 0;

    float startTimer;
    
    float gameSpeed = 1;

    boolean challenge;
    
    boolean gameRunning;//Whether or not the players can play
    boolean ruleTriggered;
    boolean ruleTriggeredActing;//Whether the players are currently acting like after a broken rule (for example during a throw-in, until the thrower throws the ball and another player catches it, this boolean stays true)
    boolean playersReady; //Whether the players are in positions

    int index = 0, lastIndex, ballIndex, lastBallIndex;
    
    boolean interrupted = false;
    
    boolean firstShown;

    public GameMap(RulesListener rulesListener, GUIRenderer guiRenderer) {
		if (!MyGdxGame.TESTING) {
			dynamicsWorldRunnable = new Runnable() {

				@Override
				public void run() {
					dynamicsWorld.stepSimulation(Gdx.graphics.getDeltaTime() * gameSpeed, 1, 1f / 60f);
				}

			};

			physicsThread = new VEThread(dynamicsWorldRunnable);
		}
		
        inputs = new InputController(guiRenderer);

        rules = new Rules(this, rulesListener);
        
        zones = new Zones(this);

        mCache = new ModelCache();
        
        Bullet.init();
        
		if (!MyGdxGame.TESTING) {
			createPhysics();
		

			objectsMap = new HashMap<Integer, String>();
			collObjsInEntityMap = new HashMap<btCollisionObject, Entity>();
			collObjsInObjectMap = new HashMap<btCollisionObject, GameObject>();
			collObjsValsMap = new HashMap<Integer, btCollisionObject>();
		}
		
        createMap();

        createBall();

        lastIndex = index - 1;

        teammates = new ArrayList<Player>(5);
        opponents = new ArrayList<Player>(5);
    }
    
    private void createPhysics() {
    	dynCollConfig = new btDefaultCollisionConfiguration();
        dynDispatcher = new btCollisionDispatcher(dynCollConfig);
        dynBroadphase = new btDbvtBroadphase();
        constraintSolver = new btSequentialImpulseConstraintSolver();
        dynamicsWorld = new btDiscreteDynamicsWorld(dynDispatcher, dynBroadphase, constraintSolver, dynCollConfig);
        dynamicsWorld.setGravity(new Vector3(0, -9.8f, 0));
        contactListener = new ObjectContactListener();
    }

    private void addBasketsCollObjects() {
        for (btRigidBody co : basket1.getBodies()) {
            co.setUserValue(index);
            dynamicsWorld.addRigidBody(co, OBJECT_FLAG, ALL_FLAG);
            co.setContactCallbackFlag(OBJECT_FLAG);
            co.setContactCallbackFilter(ENTITY_FLAG);

            
            if(co.getUserIndex() == 1)
            	objectsMap.put(index, ObjectType.HOMEBASKET.getId() + "Rim");
            else objectsMap.put(index, ObjectType.HOMEBASKET.getId());
            collObjsInObjectMap.put(co, basket1);
            collObjsValsMap.put(index, co);

            index++;
        }

        for (btCollisionObject co : basket1.getCollisionObjects()) {
            co.setUserValue(index);
            co.setCollisionFlags(co.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);

            dynamicsWorld.addCollisionObject(co, SPECIAL_FLAG, ENT_SPECIAL_FLAG);
            co.setContactCallbackFlag(SPECIAL_FLAG);
            co.setContactCallbackFilter(ENT_SPECIAL_FLAG);
            objectsMap.put(index, ObjectType.HOMEBASKET.getId() + "Obj");

            collObjsInObjectMap.put(co, basket1);
            collObjsValsMap.put(index, co);

            index++;
        }

        for (btRigidBody co : basket2.getBodies()) {
            co.setUserValue(index);
            dynamicsWorld.addRigidBody(co, OBJECT_FLAG, ALL_FLAG);
            co.setContactCallbackFlag(OBJECT_FLAG);
            co.setContactCallbackFilter(ENTITY_FLAG);

            if(co.getUserIndex() == 1)
            	objectsMap.put(index, ObjectType.AWAYBASKET.getId() + "Rim");
            else objectsMap.put(index, ObjectType.AWAYBASKET.getId());
            collObjsInObjectMap.put(co, basket2);
            collObjsValsMap.put(index, co);

            index++;
        }

        for (btCollisionObject co : basket2.getCollisionObjects()) {
            co.setUserValue(index);
            co.setCollisionFlags(co.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);

            dynamicsWorld.addCollisionObject(co, SPECIAL_FLAG, ENT_SPECIAL_FLAG);
            co.setContactCallbackFlag(SPECIAL_FLAG);
            co.setContactCallbackFilter(ENT_SPECIAL_FLAG);
            objectsMap.put(index, ObjectType.AWAYBASKET.getId() + "Obj");

            collObjsInObjectMap.put(co, basket2);
            collObjsValsMap.put(index, co);

            index++;
        }
    }

    private void addCameraCollObjects() {
        for (btRigidBody co : camera.getBodies()) {
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
    }

    private void addTerrainCollObjects() {
        for (btRigidBody co : terrain.getBodies()) {
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

        for (btRigidBody co : terrain.getInvisBodies()) {
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
    }

    private void createMap() {
        terrain = (Terrain) ObjectType.createGameObject(ObjectType.TERRAIN.getId(), this, 0, 0, 0);
        if(!MyGdxGame.TESTING)
        	addTerrainCollObjects();

		if (!MyGdxGame.TESTING) {
			camera = (Camera) ObjectType.createGameObject(ObjectType.CAMERA.getId(), this, 0, 0, 0);
			addCameraCollObjects();
		}

        basket1 = ObjectType.createBasket(ObjectType.HOMEBASKET.getId(), this, 0.1f, 0, 27);

        basket2 = ObjectType.createBasket(ObjectType.AWAYBASKET.getId(), this, 0.1f, 0, -27);
        basket2.setRotation(0, 1, 0, 180);

        if(!MyGdxGame.TESTING)
        	addBasketsCollObjects();
        
		if (MyGdxGame.TESTING) {
			terrain.createTheme();
			
			if(terrain.getTheme() != null)
				MyGdxGame.setColor(terrain.getTheme().getThemeColor());
		}
        createCache();
    }
    
    private void createCache() {
    	mCache.begin();
        terrain.render(mCache);
        basket1.render(mCache);
        basket2.render(mCache);
        mCache.end();
    }

    public void spawnPlayers(int count) {
        int index2 = index;
        int playerIndex = 1;

        for (int i = 0; i < count; i++) {
            Player teammate = EntityType.createPlayer(EntityType.TEAMMATE.getId(), this, new Vector3(spawnCoords[i][0], spawnCoords[i][1], spawnCoords[i][2]));
            for (btRigidBody co : teammate.getBodies()) {
                co.setUserValue(index2);
                co.setCollisionFlags(co.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);

                dynamicsWorld.addRigidBody(co, ENTITY_FLAG, ALL_FLAG);
                co.setContactCallbackFlag(ENTITY_FLAG);
                co.setContactCallbackFilter(ALL_FLAG);
                if (teammate.getMainBody().equals(co)) {
                    objectsMap.put(index2, EntityType.TEAMMATE.getId());
                } else {
                    objectsMap.put(index2, EntityType.TEAMMATE.getId() + "Hand");
                }

                collObjsInEntityMap.put(co, teammate);
                collObjsValsMap.put(index2, co);

                index2++;
            }

            for (btCollisionObject co : teammate.getCollisionObjects()) {
                co.setUserValue(index2);
                co.setCollisionFlags(co.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);

                dynamicsWorld.addCollisionObject(co, SPECIAL_FLAG, ENT_SPECIAL_FLAG);
                co.setContactCallbackFlag(SPECIAL_FLAG);
                co.setContactCallbackFilter(ENT_SPECIAL_FLAG);

                objectsMap.put(index2, EntityType.TEAMMATE.getId() + "Obj");

                collObjsInEntityMap.put(co, teammate);
                collObjsValsMap.put(index2, co);

                index2++;
            }

            teammate.turnY(180);
            teammate.setCollisionTransform(true);

            teammates.add(teammate);

            teammate.setPlayerIndex(playerIndex);
            playerIndex++;
        }

        playerIndex = 1;
        for (int i = 0; i < count; i++) {
            Player opponent = EntityType.createPlayer(EntityType.OPPONENT.getId(), this, new Vector3(spawnCoords[i][0], spawnCoords[i][1], -spawnCoords[i][2]));
            for (btRigidBody co : opponent.getBodies()) {
                co.setUserValue(index2);
                co.setCollisionFlags(co.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
                dynamicsWorld.addRigidBody(co, ENTITY_FLAG, ALL_FLAG);
                co.setContactCallbackFlag(ENTITY_FLAG);
                co.setContactCallbackFilter(ALL_FLAG);
                if (opponent.getMainBody().equals(co)) {
                    objectsMap.put(index2, EntityType.OPPONENT.getId());
                } else {
                    objectsMap.put(index2, EntityType.OPPONENT.getId() + "Hand");
                }

                collObjsInEntityMap.put(co, opponent);
                collObjsValsMap.put(index2, co);

                index2++;
            }

            for (btCollisionObject co : opponent.getCollisionObjects()) {
                co.setUserValue(index2);
                co.setCollisionFlags(co.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);

                dynamicsWorld.addCollisionObject(co, SPECIAL_FLAG, ENT_SPECIAL_FLAG);
                co.setContactCallbackFlag(SPECIAL_FLAG);
                co.setContactCallbackFilter(ENT_SPECIAL_FLAG);

                objectsMap.put(index2, EntityType.OPPONENT.getId() + "Obj");

                collObjsInEntityMap.put(co, opponent);
                collObjsValsMap.put(index2, co);

                index2++;
            }

            opponents.add(opponent);


            opponent.setPlayerIndex(playerIndex);
            playerIndex++;
        }

        lastIndex = index2 - 1;

        currentPlayerHoldTeam = -1;
        currentPlayerHoldOpp = -1;

        mainPlayer = teammates.get(0);

        for (btCollisionObject co : getCollObjectsOfAll()) {
            for (btRigidBody bo : getBodiesOfAll())
                bo.setIgnoreCollisionCheck(co, true);

        }

        //There is a separate function in player AI which should separate the player enough from the ball so that he can catch it
        //ball.setBoundingRadius(ball.getBoundingRadius() + mainPlayer.getWidth() / 2);

        Gdx.input.setInputProcessor(inputs);

        //TODO When using Android Studio, take the comment marks out of the lines below! Eclipse gives an error on this line!
        //if (Gdx.app.getType().equals(Application.ApplicationType.Android))
            //Gdx.input.setCatchKey(com.badlogic.gdx.Input.Keys.BACK, true);

        startTimer = 5.5f;
        playersReady = true;
        
        terrain.createTheme();
        if(terrain.getTheme() != null)
        	MyGdxGame.setColor(terrain.getTheme().getThemeColor());
        
        createCache();
        
        firstShown = false;
    }

    private void createBall() {
        ballIndex = index;

        /*if(ball == null)*/
        ball = (Ball) EntityType.createEntity(EntityType.BALL.getId(), this, new Vector3(0, 0, 0));
		/*else {
			Matrix4 temp = new Matrix4().setToTranslation(new Vector3(0, ball.getWidth() / 2, 0));
			
			ball.getModelInstance().transform.set(temp);
			ball.resetRigidBody();
			ball.setWorldTransform(temp);
			ball.setCollisionTransform(true);
			ball.manuallySetCollTransform();
		}*/
        
        lastBallIndex = index - 1;
        
        if(MyGdxGame.TESTING)
        	return;
        
        
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

        for (btCollisionObject co : ball.getCollisionObjects()) {
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
    	
    	if(!physicsThread.getState().equals(State.NEW)) {
    		physicsThread.interrupt();
    		interrupted = true;
    	}
    	
        disposePlayers();
        teammates.clear();
        opponents.clear();
        
        //terrain.clearTheme();
        createCache();
        
        MyGdxGame.clearColor();

        teamScore = oppScore = 0;

        if (rules.getTriggeredRule() != null) {
            rules.getTriggeredRule().clearRuleTriggerer();
            rules.clearTriggeredRule();
        }
        rules.resetRules();
        
        ruleTriggered = false;

        mainPlayer = null;

        /*for (int i = 0; i <= lastIndex; i++) {
            objectsMap.remove(i);

            btCollisionObject tempObj = collObjsValsMap.get(i);

            collObjsInEntityMap.remove(tempObj);
            collObjsInObjectMap.remove(tempObj);
            collObjsValsMap.remove(i);
        }*/
        
        objectsMap.clear();
        collObjsInEntityMap.clear();
        collObjsInObjectMap.clear();
        collObjsValsMap.clear();

        disposePhysics();
        createPhysics();
        
        index = 0;
        addTerrainCollObjects();
        addCameraCollObjects();
        addBasketsCollObjects();

        //index = ballIndex;
        createBall();
        
        if(interrupted) {
        	physicsThread = new VEThread(dynamicsWorldRunnable);
        	interrupted = false;
        }

        gameRunning = false;
        ruleTriggeredActing = false;

        Gdx.input.setInputProcessor(null);

        //TODO When using Android Studio, take the comment marks out of the lines below! Eclipse gives an error on this line!
        //if (Gdx.app.getType().equals(Application.ApplicationType.Android))
            //Gdx.input.setCatchKey(com.badlogic.gdx.Input.Keys.BACK, false);
    }

    private void updatePhysics() {
    	if(SettingsScreen.multithreadOption)
    		physicsThread.start();
    }
    
    public void updatePlayerAnimations(float delta) {
    	for(Player p : getAllPlayers())
    		p.updateAnimations(delta);
    }
    
    public void update(float delta) {
        //camera.setWorldTransform(new Matrix4(mainPlayer.getModelInstance().transform).mul(mainPlayer.getCamMatrix()).mul(new Matrix4().setToTranslation(0, mainPlayer.getHeight(), -10)));
    	float delta2 = delta * gameSpeed;
        //float delta2 = Math.min(1f / 30f, delta);
    	if(SettingsScreen.multithreadOption)
    		physicsThread.waitToFinish();
    	else dynamicsWorldRunnable.run();

        if (!gameRunning) {
            turnPlayer(delta);
            updateInputs();
        } else controlPlayer(delta);
        if (playersReady && !gameRunning) {
            if (!ruleTriggered) {
                if (startTimer <= 0)
                    gameRunning = true;
                else if(firstShown)
                    startTimer -= delta;
            }
        }
        if (ruleTriggeredActing) {
            updateFullGame(delta2);

            if (rules.getTriggeredRule() == null) {
                actionOver();

                if (!gameRunning) {
                    playersReady = true;
                    gameRunning = true;
                }
                //rules.clearBrokenRuleWRuleBreaker();
                updatePhysics();
                return;
            }

            playersReady = rules.getTriggeredRule().arePlayersReady();

            updatePhysics();
            return;
        } else if (gameRunning)
            updateFullGame(delta2);
        else updateGameEnvironment(delta2);

        
        if (Gdx.app.getType().equals(Application.ApplicationType.Android)) {
        	Player currentHolder = getHoldingPlayer();
            if (currentHolder != null && getHoldingPlayer() != null && !currentHolder.equals(getHoldingPlayer()) ||
                    currentHolder != null && getHoldingPlayer() == null ||
                    currentHolder == null && getHoldingPlayer() != null)
                inputs.reset();//To prevent from some rare glitches over the controller
        }
        
        updatePhysics();

        firstShown = true;
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

        for (Player e : getAllPlayers()) {
            e.update(delta);
        }

        updatePlayerAnimations(delta);
    }

    private void endPlayersCycle() {
        for (Player e : getAllPlayers())
            e.onCycleEnd();
    }

    public void render(ModelBatch mBatch, Environment environment, PerspectiveCamera pCam) {
    	mBatch.begin(pCam);
    	mBatch.render(mCache, environment);
        ball.render(mBatch, environment, pCam);

        for(Player e : getAllPlayers()) {
            mBatch.flush();
            e.render(mBatch, environment, pCam);
        }

        mBatch.end();
        
        //System.out.println(zones.isInZone("red-zone", pCam.position));
    }

    public void updateController(){
        if(gameRunning || !ruleTriggeredActing)
            inputs.update(this);
    }

    public void renderController(){
        inputs.render();
    }

    public void addRigidBody(btRigidBody body, int group, int mask) {
        dynamicsWorld.addRigidBody(body, (short) group, (short) mask);
    }

    public void removeRigidBody(btRigidBody body) {
        dynamicsWorld.removeRigidBody(body);
    }
    
    public void removePlayer(Player player) {
    	teammates.remove(player);
    	opponents.remove(player);
    	
    	for(btRigidBody obj : player.getBodies()) {
    		dynamicsWorld.removeRigidBody(obj);
    		obj.dispose();
    	}
    	
    	for(btCollisionObject obj : player.getCollisionObjects()) {
    		dynamicsWorld.removeCollisionObject(obj);
    		obj.dispose();
    	}
    }

    public void onRuleTriggered(GameRule rule) {
        //The game will just stop here
        gameRunning = false;
        ruleTriggered = true;
        playersReady = false;
        ruleTriggeredActing = false;

        if(Gdx.app.getType().equals(Application.ApplicationType.Android))
            inputs.reset();
        
        if(difficulty > 0) {
        	if(!rule.getId().equals("basket_score"))
	        	rule.getRuleTriggerer().addFoul();
        }
    }

    public void onRuleBrokenContinue() {
        ruleTriggered = false;
        ruleTriggeredActing = true;
        startTimer = 0.9f;
        
        if(difficulty > 0) {
        	Player triggerer = rules.getTriggeredRule().getRuleTriggerer();
        	if(triggerer.getFouls() == 7)
        		removePlayer(triggerer);
        }
    }

    /**
     * Sets a new target position of a player. I created this method because of the Rules system. Read the note inside the {@link GameMap#onRuleBrokenContinue()} method
     *
     * @param pos - the target position
     */
    public void setPlayerTargetPosition(final Vector3 pos, Player player) {
        // I decided to put the target setter outside of the rules's methods
        // because I'm using a SteerableAdapter to set the targets and inside
        // the adapter I'm also setting the bounding radius and if I have to
        // change it (or add something), I'll have to do it everywhere on
        // all game rules.

        if (pos.x < 0)
            pos.x = 0;
        else if (pos.x > terrain.getWidth() / 2)
            pos.x = terrain.getWidth() / 2;

        if (pos.z < 0)
            pos.z = 0;
        else if (pos.z > terrain.getDepth() / 2)
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
        
        disposePhysics();

        disposeMap();

        System.gc();
    }
    
    private void disposePhysics() {
    	if(dynamicsWorld == null)
    		return;
    	
    	if(!interrupted) {
    		physicsThread.interrupt();
    		interrupted = true;
    	}
        physicsThread = null;
    	
    	dynamicsWorld.dispose();
        dynamicsWorld = null;
        
        dynDispatcher.dispose();
        dynDispatcher = null;

        dynCollConfig.dispose();
        dynCollConfig = null;

        dynBroadphase.dispose();
        dynBroadphase = null;

        constraintSolver.dispose();
        constraintSolver = null;

        contactListener.dispose();
        contactListener = null;
    }

    private void disposeMap() {
    	ball.dispose();
        ball = null;

        terrain.dispose();
        terrain = null;

        basket1.dispose();
        basket2.dispose();
        basket1 = basket2 = null;

        mCache.dispose();
        mCache = null;

        rules = null;
    }

    private void disposePlayers() {
        for (Player e : getAllPlayers()) {
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
        ball.getMainBody().setLinearVelocity(Vector3.Zero);
    }

    private void controlPlayer(float delta) {
        movePlayer(delta);

        turnPlayer(delta);

        if (!Gdx.app.getType().equals(Application.ApplicationType.Android))
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

        if (inputs.isSprintPressed()) {
            if (inputs.isForwardPressed()) {
                mainPlayer.run(new Vector3(dirX * delta, 0, dirZ * delta));
            } else if (Gdx.app.getType().equals(Application.ApplicationType.Android)) {
                mainPlayer.run(inputs.getMovementVec().rotate(dir.getYaw(), 0, 1, 0).nor().scl(delta));
            }

            if (inputs.isStrLeftPressed())
                mainPlayer.turnY(delta * 90);

            if (inputs.isStrRightPressed())
                mainPlayer.turnY(-delta * 90);
        } else if (Gdx.app.getType().equals(Application.ApplicationType.Android)) {
            if (!inputs.getMovementVec().isZero())
                mainPlayer.walk(inputs.getMovementVec().scl(-1, 0, 1).rotate(dir.getYaw(), 0, 1, 0).scl(delta));
        } else {
            if (inputs.isForwardPressed()) {
                mainPlayer.walk(new Vector3(dirX * delta, 0, dirZ * delta));
            } else if (inputs.isBackwardPressed())
                mainPlayer.walk(new Vector3(dirX * -delta, 0, dirZ * -delta));

            if (inputs.isStrLeftPressed())
                mainPlayer.walk(new Vector3(dirZ * delta, 0, dirX * -delta));
            else if (inputs.isStrRightPressed())
                mainPlayer.walk(new Vector3(dirZ * -delta, 0, dirX * delta));
        }

        mainPlayer.shootPowerScroll(inputs.getScroll());

        if (inputs.isFocusPressed())
            mainPlayer.focus(false);

        if (inputs.isShootPressed()) {
            mainPlayer.interactWithBallS();
        } else if (inputs.isDribbleLPressed()) {
            mainPlayer.interactWithBallL();
        } else if (inputs.isDribbleRPressed()) {
            mainPlayer.interactWithBallR();
        }
    }

    private void turnPlayer(float delta) {
        if (Gdx.app.getType().equals(Application.ApplicationType.Android) && !gameRunning)
            inputs.updateRotation();

        float turnY = inputs.getDeltaX(); //Around the Y-axis
        float turnX = inputs.getDeltaY(); //Around the X-axis

        if (Gdx.app.getType().equals(Application.ApplicationType.Android)) {
            mainPlayer.turnX(turnX / 2);//We lower the sensitivity on mobile version
            mainPlayer.turnY(turnY / 2);

            return;
        }
        if (Math.abs(turnY) < Gdx.graphics.getWidth() / 4) {
            mainPlayer.turnY(turnY * delta * 9);
        }

        if (Math.abs(turnX) < Gdx.graphics.getHeight() / 4) {
            mainPlayer.turnX(turnX * delta * 9);
        }

        int substractor = 140;

        if (Gdx.input.getX() > Gdx.graphics.getWidth() - substractor)
            Gdx.input.setCursorPosition(substractor, Gdx.input.getY());
        else if (Gdx.input.getX() < substractor)
            Gdx.input.setCursorPosition(Gdx.graphics.getWidth() - substractor, Gdx.input.getY());

        if (Gdx.input.getY() > Gdx.graphics.getHeight() - substractor)
            Gdx.input.setCursorPosition(Gdx.input.getX(), substractor);
        else if (Gdx.input.getY() < substractor)
            Gdx.input.setCursorPosition(Gdx.input.getX(), Gdx.graphics.getHeight() - substractor);
    }

    public void scoreTeam(boolean doub, boolean triple) {
        if (triple)
            teamScore += 3;
        else if(doub) teamScore += 2;
        else teamScore++;
    }

    public void scoreOpp(boolean doub, boolean triple) {
        if (triple)
            oppScore += 3;
        else if(doub) oppScore += 2;
        else oppScore++;
    }

    public btDynamicsWorld getDynamicsWorld() {
        return dynamicsWorld;
    }

    public HashMap<Integer, String> getObjectsMap() {
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
    
    public Zones getZones() {
    	return zones;
    }

    public Rules getRules() {
		return rules;
	}

	public Player getMainPlayer() {
        return mainPlayer;
    }

    public Player getTeammateHolding() {
        if (currentPlayerHoldTeam > -1)
            return teammates.get(currentPlayerHoldTeam);

        return null;
    }

    public Player getOpponentHolding() {
        if (currentPlayerHoldOpp > -1)
            return opponents.get(currentPlayerHoldOpp);

        return null;
    }

    public Player getHoldingPlayer() {
        Player temp = getTeammateHolding();

        return temp != null ? temp : getOpponentHolding();
    }

    public ArrayList<Player> getTeammates() {
        return teammates;
    }

    public ArrayList<Player> getOpponents() {
        return opponents;
    }

    public ArrayList<Player> getAllPlayers() {
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

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
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

    public boolean isChallenge() {
		return challenge;
	}

	public void setChallenge(boolean challenge) {
		this.challenge = challenge;
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

    public ArrayList<btRigidBody> getBodiesOfAll() {
        ArrayList<btRigidBody> tempObj = new ArrayList<btRigidBody>();

        if (terrain.getBodies() != null)
            tempObj.addAll(terrain.getBodies());

        if (basket1.getBodies() != null)
            tempObj.addAll(basket1.getBodies());

        if (basket2.getBodies() != null)
            tempObj.addAll(basket2.getBodies());

        if (ball.getBodies() != null)
            tempObj.addAll(ball.getBodies());

        for (Player player : getAllPlayers())
            if (player.getBodies() != null)
                tempObj.addAll(player.getBodies());


        if (terrain.getInvisBodies() != null)
            tempObj.addAll(terrain.getInvisBodies());

        if (basket1.getInvisBodies() != null)
            tempObj.addAll(basket1.getInvisBodies());

        if (basket2.getInvisBodies() != null)
            tempObj.addAll(basket2.getInvisBodies());

        if (ball.getInvisBodies() != null)
            tempObj.addAll(ball.getInvisBodies());

        for (Player player : teammates)
            if (player.getInvisBodies() != null)
                tempObj.addAll(player.getInvisBodies());

        for (Player player : opponents)
            if (player.getInvisBodies() != null)
                tempObj.addAll(player.getInvisBodies());

        return tempObj;
    }

    public ArrayList<btCollisionObject> getCollObjectsOfAll() {
        ArrayList<btCollisionObject> tempObj = new ArrayList<btCollisionObject>();

        if (terrain.getCollisionObjects() != null)
            tempObj.addAll(terrain.getCollisionObjects());

        if (basket1.getCollisionObjects() != null)
            tempObj.addAll(basket1.getCollisionObjects());

        if (basket2.getCollisionObjects() != null)
            tempObj.addAll(basket2.getCollisionObjects());

        if (ball.getCollisionObjects() != null)
            tempObj.addAll(ball.getCollisionObjects());

        for (Player player : getAllPlayers())
            if (player.getCollisionObjects() != null)
                tempObj.addAll(player.getCollisionObjects());


        return tempObj;
    }

}
