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
		//bodies.get(0).setDamping(-3, -3);
		//createCollisions();
	}
	
	protected void createModels(Vector3 pos) {
		ModelBuilder mb = new ModelBuilder();
		//MeshPartBuilder meshBuilder;
		
		Texture ballTexture = new Texture(Gdx.files.internal("game/basketball_3d_texture.jpg"));
		ballTexture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
		ballTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		
		Material material = new Material(TextureAttribute.createDiffuse(ballTexture));
		
		//mb.begin();
		
		//meshBuilder = mb.part("ball", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates, material);
		
		//SphereShapeBuilder.build(meshBuilder, 1, 1, 1, 25, 25);
		
		//mb.createSphere(1, 1, 1, 25, 25, material, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
		
		//model = mb.end();
		
		model = mb.createSphere(1, 1, 1, 25, 25, material, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
		model.manageDisposable(ballTexture);
		
		modelInstance = new ModelInstance(model, pos.add(0, 0.5f, 0));
		
		//modelInstance.transform.setToTranslation(x, y, z);
	}
	
	/*private void printBallTrans() {
		Vector3 vec = new Vector3();
		
		collisionObjects.get(0).getWorldTransform().getTranslation(vec);
		System.out.println("Ball Obj: " + vec.x + "; " + vec.y + "; " + vec.z);
		
		getMainBody().getWorldTransform().getTranslation(vec);
		System.out.println("Ball Body: " + vec.x + "; " + vec.y + "; " + vec.z);
	}*/
	
	@Override
	public void update(float delta) {
		manuallySetCollTransform();
		//System.out.println(getMainBody().getWorldTransform());
		getMainBody().getWorldTransform(); //For some reason I had to call this to make everything work :| (ball's modelInstance teleports into the main player's stomach)
		//printBallTrans();
		//System.out.println(grounded);
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
		
		//invisCollShapes.add(new btSphereShape(0.25f));
		//matrixes.add(matrixes.get(0));
		//matrixes.add(matrixes.get(0));
	}
	
	@Override
	public void setWorldTransform(Matrix4 trans) {
		//modelInstance.transform.set(trans.val);
		//getMainBody().proceedToTransform(modelInstance.transform);
		getMainBody().setWorldTransform(trans);
		//modelInstance.transform = getMainBody().getWorldTransform();
		manuallySetCollTransform();
	}
	
	/**
	 * Try without this method (without method for repeatedly setting the world transform)
	 */
	private void manuallySetCollTransform() {
		collisionObjects.get(0).setWorldTransform(matrixes.get(1));
		//collisionObjects.get(1).setWorldTransform(matrixes.get(2));
	}
	
	@Override
	protected void createCollisionObjectAndBodies() {
		super.createCollisionObjectAndBodies();
		
		//getMainBody().setRestitution(50);
		//getMainBody().setFriction(1);
		/*softBodies = new ArrayList<btSoftBody>();
		
		MeshPart meshPart = modelInstance.nodes.get(0).parts.get(0).meshPart;
		
		btSoftBody softBody = new btSoftBody(map.getSoftWorld().getWorldInfo(), meshPart.mesh.getVerticesBuffer(), meshPart.mesh.getVertexSize(), meshPart.mesh.getVertexAttribute(Usage.Position).offset, meshPart.mesh.getVertexAttribute(Usage.Normal).offset, meshPart.mesh.getIndicesBuffer(), meshPart.offset, meshPart.size, BufferUtils.newShortBuffer(meshPart.size), 0);
		softBody.setMass(0, 0);
		com.badlogic.gdx.physics.bullet.softbody.btSoftBody.Material pm = softBody.appendMaterial();
		pm.setKLST(0.2f);
		pm.setFlags(0);
		softBody.generateBendingConstraints(2, pm);
		softBody.setConfig_piterations(1);
		softBody.setConfig_kDF(0.2f);
		softBody.randomizeConstraints();
		softBody.setTotalMass(type.getMass());
		
		softBodies.add(softBody);*/
		//softBodies.get(0).setCollisionShape(collisionShapes.get(0));
		modelInstance.transform = getMainBody().getWorldTransform();
		
		collisionObjects = new ArrayList<btCollisionObject>();
		
		//invisConstructionInfo = new btRigidBody.btRigidBodyConstructionInfo(type.getMass(), null, collisionShape, localInertia);
		//btCollisionObject temp = new btCollisionObject();
		//temp.setCollisionShape(invisCollShapes.get(0));
		//collisionObjects.add(temp);
		collisionObjects.add(new btCollisionObject());
		collisionObjects.get(0).setCollisionShape(invisCollShapes.get(0));
		
		//collisionObjects.get(0).setIgnoreCollisionCheck(getMainBody(), true);
		
		//for(btCollisionObject co : map.getCollObjectsOfAll())
			//getMainBody().setIgnoreCollisionCheck(co, true);
		
		//collisionObjects.add(new btCollisionObject());
		//collisionObjects.get(1).setCollisionShape(invisCollShapes.get(1));
		
		//collisionObjects.get(1).setIgnoreCollisionCheck(getMainBody(), true);
		manuallySetCollTransform();
		
		/*motionState = new GameMap.MotionState();
		motionState.transform = modelInstance.transform;
		collisionObjects.get(0).setMotionState(motionState);*/
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
