package com.gamesbg.bkbl.gamespace.objects;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.gamesbg.bkbl.gamespace.GameMap;
import com.gamesbg.bkbl.gamespace.MotionState;

public abstract class GameObject {

	protected ObjectType type;
	protected GameMap map;
	//protected MotionState motionState;
	protected Model model;
	protected ModelInstance modelInstance;
	protected ArrayList<Node> nodes;
	protected ArrayList<Matrix4> matrixes;
	protected ArrayList<btCollisionShape> visibleCollShapes;
	protected ArrayList<btCollisionShape> invisibleCollShapes;
	protected ArrayList<btCollisionObject> collisionObjects; //Used for invisible objects that should have collisions with others but should not change the forces of the objects
	protected ArrayList<btRigidBody> bodies;
	protected ArrayList<btRigidBody> invisBodies;
	protected ArrayList<MotionState> motionStates;
	protected ArrayList<MotionState> invisMotionStates;
	protected ArrayList<btRigidBody.btRigidBodyConstructionInfo> constructionInfos;
	protected ArrayList<btRigidBody.btRigidBodyConstructionInfo> invisConstructionInfos;
	protected static final Vector3 localInertia = new Vector3(0, 0, 0);
	
	protected float x, y, z;
	protected float rX, rY, rZ, rA;
	
	public void create(ObjectType type, GameMap map, float x, float y, float z) {
		this.type = type;
		this.map = map;
		this.x = x;
		this.y = y;
		this.z = z;
		
		visibleCollShapes = new ArrayList<btCollisionShape>();
		invisibleCollShapes = new ArrayList<btCollisionShape>();
		constructionInfos = new ArrayList<btRigidBody.btRigidBodyConstructionInfo>();
		invisConstructionInfos = new ArrayList<btRigidBody.btRigidBodyConstructionInfo>();
		motionStates = new ArrayList<MotionState>();
		invisMotionStates = new ArrayList<MotionState>();
		bodies = new ArrayList<btRigidBody>();
		//invisBodies = new ArrayList<btRigidBody>();
		//collisionObjects = new ArrayList<btCollisionObject>();
		
		nodes = new ArrayList<Node>();
		createModels();
		createCollisionShapes();
		
		//motionState = new MotionState();
		//motionState.transform = modelInstance.transform;
		
		setCollisions();
		//manuallySetCollisions();
		
	}
	
	public void create(ObjectType type, GameMap map, Matrix4 trans) {
		this.type = type;
		this.map = map;
		
		matrixes = new ArrayList<Matrix4>();
		matrixes.add(trans);
		
		visibleCollShapes = new ArrayList<btCollisionShape>();
		invisibleCollShapes = new ArrayList<btCollisionShape>();
		constructionInfos = new ArrayList<btRigidBody.btRigidBodyConstructionInfo>();
		invisConstructionInfos = new ArrayList<btRigidBody.btRigidBodyConstructionInfo>();
		motionStates = new ArrayList<MotionState>();
		invisMotionStates = new ArrayList<MotionState>();
		bodies = new ArrayList<btRigidBody>();
		//invisBodies = new ArrayList<btRigidBody>();
		//collisionObjects = new ArrayList<btCollisionObject>();
		
		nodes = new ArrayList<Node>();
		createModels();
		createCollisionShapes();
		
		//motionState = new MotionState();
		//motionState.transform = modelInstance.transform;
		
		setCollisions(trans);
		//manuallySetCollisions();
		
	}
	
	public void render(ModelBatch mBatch, Environment e) {
		mBatch.render(modelInstance, e);
	}
	
	public void dispose() {
		model.dispose();
		for(btRigidBody o : bodies)
			o.dispose();
		
		for(btRigidBody.btRigidBodyConstructionInfo constInfo : constructionInfos)
			constInfo.dispose();
		
		for(btCollisionShape s : visibleCollShapes)
			s.dispose();
		
		for(btCollisionShape s : invisibleCollShapes)
			s.dispose();
		
		for(MotionState ms : motionStates)
			ms.dispose();
	}
	
	protected abstract void createModels();
	
	protected abstract void createCollisionShapes();
	
	/**
	 * Used when the object has special places which the objects can collide with, but they dont have any models
	 */
	//protected abstract void manuallySetCollisions();
	
	/**
	 * Used when the object has special places which the objects can collide with, but they dont have any models
	 */
	protected abstract void manuallyRecalcCollisions();
	
	protected abstract void manuallySetObjects();
	
	protected abstract void manuallySetBodies(); //I couldnt make them with overriding cuz when the usual methods call these abstracted ones, they call the overriders first and then the normal ones
	
	/*protected void setObjects() {
		modelInstance.calculateTransforms();
		
		for(int i = 0; i < bodies.size(); i++)
			collisionObjects.get(i).setWorldTransform(calcBodiesAndObjectsTransform(nodes.get(i).globalTransform));
	}*/
	
	protected void setBodies() {
		modelInstance.calculateTransforms();
		
		for(int i = 0;i < bodies.size(); i++)
			bodies.get(i).proceedToTransform(calcTransformFromNodesTransform(nodes.get(i).globalTransform));
	}
	
	protected void setBodies(Matrix4 givenTrans) {
		bodies.get(0).proceedToTransform(givenTrans);
	}
	
	protected Matrix4 calcTransformFromNodesTransform(Matrix4 nodeTrans) {
		return new Matrix4().set(nodeTrans.cpy().setToTranslation(nodeTrans.getTranslation(new Vector3()).add(modelInstance.transform.getTranslation(new Vector3()))).getTranslation(new Vector3()), new Quaternion().setFromAxis(rX, rY, rZ, rA));
	}
	
	/*protected Matrix4 calcBodiesAndObjectsTransform(Matrix4 nodeTrans) {
		return new Matrix4().set(nodeTrans.cpy().translate(x, y, z));
	}*/
	
	private void constructBodies() {
		for(btCollisionShape shape : visibleCollShapes) {
			btRigidBody.btRigidBodyConstructionInfo constInfo = new btRigidBody.btRigidBodyConstructionInfo(0, null, shape, localInertia);
			//constInfo.setRestitution(0.5f);
			//constInfo.setFriction(1.0f);
			constructionInfos.add(constInfo);
			bodies.add(new btRigidBody(constInfo));
		}
	}
	
	protected void setCollisions() {
		constructBodies();
		
		setBodies();
		
		for(int i = 0; i < visibleCollShapes.size(); i++) {
			/*Matrix4 temp = new Matrix4();
			//temp.set(nodes.get(i).translation.cpy().add(x, y, z), nodes.get(i).rotation.cpy().setFromAxis(rX, rY, rZ, rA));
			Matrix4 glTr = nodes.get(i).globalTransform;
			temp.set(glTr.cpy().setToTranslation(glTr.getTranslation(new Vector3()).add(x, y, z)).getTranslation(new Vector3()), new Quaternion().setFromAxis(rX, rY, rZ, rA));*/
			//System.out.println(temp.getTranslation(new Vector3()).z);
			//System.out.println(nodes.get(i).id);
			motionStates.add(new MotionState());
			if(nodes.size() > i && nodes.get(i) != null)
				motionStates.get(i).transform = calcTransformFromNodesTransform(nodes.get(i).globalTransform);
			else if(matrixes != null && matrixes.size() > i && matrixes.get(i) != null)
				motionStates.get(i).transform = matrixes.get(i);
			else motionStates.get(i).transform = new Matrix4();
			bodies.get(i).setMotionState(motionStates.get(i));
		}
	}
	
	protected void setCollisions(Matrix4 givenTrans) {
		constructBodies();
		
		setBodies(givenTrans);
		
		for(int i = 0; i < visibleCollShapes.size(); i++) {
			/*Matrix4 temp = new Matrix4();
			//temp.set(nodes.get(i).translation.cpy().add(x, y, z), nodes.get(i).rotation.cpy().setFromAxis(rX, rY, rZ, rA));
			Matrix4 glTr = nodes.get(i).globalTransform;
			temp.set(glTr.cpy().setToTranslation(glTr.getTranslation(new Vector3()).add(x, y, z)).getTranslation(new Vector3()), new Quaternion().setFromAxis(rX, rY, rZ, rA));*/
			//System.out.println(temp.getTranslation(new Vector3()).z);
			//System.out.println(nodes.get(i).id);
			motionStates.add(new MotionState());
			//if(nodes.size() > i && nodes.get(i) != null)
				//motionStates.get(i).transform = calcTransformFromNodesTransform(nodes.get(i).globalTransform);
			//else if(matrixes != null && matrixes.size() > i && matrixes.get(i) != null)
			motionStates.get(i).transform = givenTrans;
			//else motionStates.get(i).transform = new Matrix4();
			//bodies.get(i).setMotionState(motionStates.get(i));
		}
	}
	
	protected void recalcCollisionsTransform() {
		setBodies();
		
		for(int i = 0; i < visibleCollShapes.size(); i++) {
			/*Matrix4 temp = new Matrix4();
			Matrix4 glTr = nodes.get(i).globalTransform;
			temp.set(glTr.cpy().setToTranslation(glTr.getTranslation(new Vector3()).add(x, y, z)).getTranslation(new Vector3()), new Quaternion().setFromAxis(rX, rY, rZ, rA));*/
			motionStates.get(i).transform = calcTransformFromNodesTransform(nodes.get(i).globalTransform);
			bodies.get(i).setMotionState(motionStates.get(i));
		}
	}
	
	/**
	 * Contains functions which are done when a collision at a specified place occurs
	 */
	protected abstract void specialFunction();
		
	public ObjectType getType() {
		return type;
	}

	public GameMap getMap() {
		return map;
	}

	public Model getModel() {
		return model;
	}

	public ModelInstance getModelInstance() {
		return modelInstance;
	}
	
	public ArrayList<btRigidBody> getBodies() {
		return bodies;
	}
	
	public ArrayList<btRigidBody> getInvisBodies(){
		return invisBodies;
	}
	
	public ArrayList<btCollisionObject> getCollisionObjects(){
		return collisionObjects;
	}
	
	public void setRotation(float x, float y, float z, float angle) {
		modelInstance.transform.rotate(x, y, z, angle);
		
		rX = x;
		rY = y;
		rZ = z;
		rA = angle;
		
		setCollisions();
		
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZ() {
		return z;
	}

	public abstract float getWidth();
	
	public abstract float getHeight();
	
	public abstract float getDepth();
	
	
}
