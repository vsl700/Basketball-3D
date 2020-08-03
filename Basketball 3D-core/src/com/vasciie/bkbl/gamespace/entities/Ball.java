package com.vasciie.bkbl.gamespace.entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.vasciie.bkbl.gamespace.GameMap;
import com.vasciie.bkbl.gamespace.tools.GameTools;

public class Ball extends Entity {
	
	ArrayList<Player> neighborPlayers;
	
	boolean collidedWTeamBasket, collidedWOppBasket;
	
	public void create(EntityType type, GameMap map, Vector3 pos) {
		super.create(type, map, pos);
		
		boundRadius = 5.5f;
		neighborPlayers = new ArrayList<Player>();
	}
	
	protected void createModels(Vector3 pos) {
		super.createModels(pos);
		
		modelInstance = new ModelInstance(model, pos.add(0, 0.5f, 0));
	}
	
	@Override
	public void update(float delta) {
		//System.out.println(getWidth() + " ball radius");
		modelInstance.transform.set(getMainBody().getWorldTransform());
		manuallySetCollTransform();
		//getMainBody().getWorldTransform(); //For some reason I had to call this to make everything work :| (ball's modelInstance teleports into the main player's stomach)
	}
	
	@Override
	public void render(ModelBatch mBatch, Environment e, PerspectiveCamera pCam) {
		if(GameTools.isObjectVisibleToScreen(pCam, modelInstance, getWidth()))
			mBatch.render(modelInstance, e);
	}
	
	@Override
	public void collisionOccured(btCollisionObject objInside, btCollisionObject objOutside) {
		super.collisionOccured(objInside, objOutside);
		
		if(objInside.equals(collisionObjects.get(1))) {
			if(map.getHomeBasket().getCollisionObjects().get(0).equals(objOutside))
				collidedWTeamBasket = true;
			else if(map.getAwayBasket().getCollisionObjects().get(0).equals(objOutside))
				collidedWOppBasket = true;
		}
	}

	@Override
	protected void createCollisions() {
		collisionShapes.add(new btSphereShape(0.5f));
		matrixes.add(modelInstance.transform);
		
		invisCollShapes = new ArrayList<btCollisionShape>();
		/*
		 * This is the main coll obj. It is used to detect collisions between the ball from its outside part to its center and the player's hands. IT CANNOT BE USED FOR BASKETS!
		 */
		invisCollShapes.add(new btSphereShape(0.5f));
		matrixes.add(matrixes.get(0));
		
		//invisCollShapes = new ArrayList<btCollisionShape>();
		/*
		 * When the ball gets around a basket, with its normal collision object the game would detect a collision even if the ball doesnt get in. This coll obj will 
		 * detect if the center of the ball gets inside a basket.
		 */
		invisCollShapes.add(new btSphereShape(0.05f)); 
		matrixes.add(matrixes.get(0));
	}
	
	@Override
	public void setWorldTransform(Matrix4 trans) {
		getMainBody().setWorldTransform(trans);
		manuallySetCollTransform();
	}
	
	/**
	 * Try without this method (without method for repeatedly setting the world transform)
	 */
	public void manuallySetCollTransform() {
		collisionObjects.get(0).setWorldTransform(matrixes.get(1));
		collisionObjects.get(1).setWorldTransform(matrixes.get(2));
	}
	
	public void resetRigidBody() {
		bodies.get(0).dispose();
		bodies.clear();
		
		createCollisionObjectAndBodies();
	}
	
	@Override
	protected void createCollisionObjectAndBodies() {
		super.createCollisionObjectAndBodies();
		
		//if(collisionObjects == null)//If we are only resetting rigid body
		modelInstance.transform = getMainBody().getWorldTransform();
		
		collisionObjects = new ArrayList<btCollisionObject>();
		
		collisionObjects.add(new btCollisionObject());
		collisionObjects.get(0).setCollisionShape(invisCollShapes.get(0));
		
		collisionObjects.add(new btCollisionObject());
		collisionObjects.get(1).setCollisionShape(invisCollShapes.get(1));
		
		manuallySetCollTransform();
		
	}
	
	@Override
	public void dispose() {
		super.dispose();
		
		neighborPlayers.clear();
		neighborPlayers = null;
	}

	@Override
	public float getWidth() {
		return model.nodes.get(0).scale.x;
	}

	@Override
	public float getHeight() {
		return model.nodes.get(0).scale.y;
	}

	@Override
	public float getDepth() {
		return model.nodes.get(0).scale.z;
	}
	
	@Override
	public void onCycleEnd() {
		super.onCycleEnd();
		
		collidedWTeamBasket = false;
		collidedWOppBasket = false;
		//modelInstance.transform.set(getMainBody().getWorldTransform());
	}
	
	@Override
	public int findNeighbors(ProximityCallback<Vector3> callback) {
		neighborPlayers.clear();
		
		for(Player p : map.getTeammates())
			if(callback.reportNeighbor(p))
				neighborPlayers.add(p);
		
		for(Player p : map.getOpponents())
			if(callback.reportNeighbor(p))
				neighborPlayers.add(p);
		
		return neighborPlayers.size();
	}

	public ArrayList<Player> getNeighborPlayers() {
		return neighborPlayers;
	}

	public boolean isCollidedWTeamBasket() {
		return collidedWTeamBasket;
	}

	public boolean isCollidedWOppBasket() {
		return collidedWOppBasket;
	}
	
	
}
