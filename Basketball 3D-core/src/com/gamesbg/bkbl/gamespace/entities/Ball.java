package com.gamesbg.bkbl.gamespace.entities;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.gamesbg.bkbl.gamespace.GameMap;
import com.gamesbg.bkbl.gamespace.objects.ObjectType;

public class Ball extends Entity {
	
	public void create(EntityType type, GameMap map, Vector3 pos) {
		super.create(type, map, pos);
	}
	
	protected void createModels(Vector3 pos) {
		ModelBuilder mb = new ModelBuilder();
		//MeshPartBuilder meshBuilder;
		
		Texture ballTexture = new Texture(Gdx.files.internal("game/basketball_3d_texture.jpg"));
		ballTexture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
		ballTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		
		Material material = new Material(TextureAttribute.createDiffuse(ballTexture));
		
		model = mb.createSphere(1, 1, 1, 25, 25, material, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
		model.manageDisposable(ballTexture);
		
		modelInstance = new ModelInstance(model, pos.add(0, 0.5f, 0));
	}
	
	@Override
	public void update(float delta) {
		manuallySetCollTransform();
		getMainBody().getWorldTransform(); //For some reason I had to call this to make everything work :| (ball's modelInstance teleports into the main player's stomach)
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
		invisCollShapes.add(new btSphereShape(0.15f)); 
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
	private void manuallySetCollTransform() {
		collisionObjects.get(0).setWorldTransform(matrixes.get(1));
	}
	
	@Override
	protected void createCollisionObjectAndBodies() {
		super.createCollisionObjectAndBodies();
		modelInstance.transform = getMainBody().getWorldTransform();
		
		collisionObjects = new ArrayList<btCollisionObject>();
		
		collisionObjects.add(new btCollisionObject());
		collisionObjects.get(0).setCollisionShape(invisCollShapes.get(0));
		
		manuallySetCollTransform();
		
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
	
	public void addListener() {
		
	}

	@Override
	public void collisionOccured(btCollisionObject objInside, btCollisionObject objOutside) {
		super.collisionOccured(objInside, objOutside);
		
		if(map.getObjectsMap().get(objOutside.getUserValue()).equals(ObjectType.TERRAIN.getId() + "Inv")) {
			getMainBody().setLinearFactor(new Vector3());
		}
	}

	
	
}
