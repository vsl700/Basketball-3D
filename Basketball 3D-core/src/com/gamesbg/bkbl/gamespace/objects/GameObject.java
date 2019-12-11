package com.gamesbg.bkbl.gamespace.objects;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.steer.Proximity;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.Proximity.ProximityCallback;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.gamesbg.bkbl.gamespace.GameMap;
import com.gamesbg.bkbl.gamespace.MotionState;
import com.gamesbg.bkbl.gamespace.entities.Player;

public abstract class GameObject implements Steerable<Vector2>, Proximity<Vector2> {

	protected ObjectType type;
	protected GameMap map;
	//protected MotionState motionState;
	protected Model model;
	protected ModelInstance modelInstance;
	//protected ArrayList<Node> nodes;
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
	protected Matrix4 mainTrans;
	protected static final Vector3 localInertia = new Vector3(0, 0, 0);
	
	protected int mainBodyIndex;
	
	protected float x, y, z;
	//protected float rX, rY, rZ, rA;
	
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
		
		//nodes = new ArrayList<Node>();
		matrixes = new ArrayList<Matrix4>();
		
		createModels();
		if(modelInstance != null)
			mainTrans = modelInstance.transform;
		else mainTrans = new Matrix4(); 
		createCollisionShapes();
		
		//motionState = new MotionState();
		//motionState.transform = modelInstance.transform;
		
		setCollisions();
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
		if(modelInstance != null)
			modelInstance.calculateTransforms();
		
		//if(nodes != null)
		for(int i = 0; i < bodies.size() && modelInstance != null; i++)
			if(i == mainBodyIndex)
				bodies.get(i).proceedToTransform(mainTrans);
			else bodies.get(i).proceedToTransform(calcTransformFromNodesTransform(matrixes.get(i)));
	}
	
	public Matrix4 calcTransformFromNodesTransform(Matrix4 nodeTrans) {
		return modelInstance.transform.cpy().mul(nodeTrans);
	}
	
	/*protected Matrix4 calcBodiesAndObjectsTransform(Matrix4 nodeTrans) {
		return new Matrix4().set(nodeTrans.cpy().translate(x, y, z));
	}*/
	
	protected void setCollisions() {
		for(btCollisionShape shape : visibleCollShapes) {
			btRigidBody.btRigidBodyConstructionInfo constInfo = new btRigidBody.btRigidBodyConstructionInfo(0, null, shape, localInertia);
			//constInfo.setRestitution(0.5f);
			//constInfo.setFriction(1.0f);
			constructionInfos.add(constInfo);
			bodies.add(new btRigidBody(constInfo));
		}
		
		setBodies();
		
		for(int i = 0; i < visibleCollShapes.size(); i++) {
			/*Matrix4 temp = new Matrix4();
			//temp.set(nodes.get(i).translation.cpy().add(x, y, z), nodes.get(i).rotation.cpy().setFromAxis(rX, rY, rZ, rA));
			Matrix4 glTr = nodes.get(i).globalTransform;
			temp.set(glTr.cpy().setToTranslation(glTr.getTranslation(new Vector3()).add(x, y, z)).getTranslation(new Vector3()), new Quaternion().setFromAxis(rX, rY, rZ, rA));*/
			//System.out.println(temp.getTranslation(new Vector3()).z);
			//System.out.println(nodes.get(i).id);
			motionStates.add(new MotionState());
			/*if(nodes.size() > i && nodes.get(i) != null)
				motionStates.get(i).transform = calcTransformFromNodesTransform(nodes.get(i).globalTransform);
			else motionStates.get(i).transform = new Matrix4();*/
			if(i == mainBodyIndex)
				motionStates.get(i).transform = mainTrans;
			else motionStates.get(i).transform = calcTransformFromNodesTransform(matrixes.get(i));
			bodies.get(i).setMotionState(motionStates.get(i));
		}
	}
	
	protected void recalcCollisionsTransform() {
		setBodies();
		
		for(int i = 0; i < visibleCollShapes.size(); i++) {
			/*Matrix4 temp = new Matrix4();
			Matrix4 glTr = nodes.get(i).globalTransform;
			temp.set(glTr.cpy().setToTranslation(glTr.getTranslation(new Vector3()).add(x, y, z)).getTranslation(new Vector3()), new Quaternion().setFromAxis(rX, rY, rZ, rA));*/
			motionStates.get(i).transform = calcTransformFromNodesTransform(matrixes.get(i));
			bodies.get(i).setMotionState(motionStates.get(i));
		}
	}
	
	/**
	 * Contains functions which are done when a collision at a specified place occurs
	 */
	protected abstract void specialFunction();
		
	public void setWorldTransform(Matrix4 trans) {
		mainTrans = trans;
		
		//setBodies();
	}
	
	@Override
	public Vector2 getLinearVelocity() {
		Vector3 tempVel = getMainBody().getLinearVelocity();
		Vector2 tempVec = new Vector2(tempVel.x, tempVel.z);
		
		return tempVec;
	}

	@Override
	public float getAngularVelocity() {
		
		return getMainBody().getAngularVelocity().y;
	}

	@Override
	public float getBoundingRadius() {
		
		return Math.max(getWidth(), getDepth()) * 1.5f;
	}

	@Override
	public Steerable<Vector2> getOwner() {
		
		return this;
	}

	@Override
	public int findNeighbors (ProximityCallback<Vector2> callback) {
		
		return 0;
	}

	@Override
	public Vector2 getPosition() {
		Vector3 tempPos = modelInstance.transform.getTranslation(new Vector3());
		Vector2 tempVec = new Vector2(tempPos.x, tempPos.z);
		
		return tempVec;
	}

	@Override
	public float getOrientation() {
		
		return modelInstance.transform.getRotation(new Quaternion()).getYaw();
	}

	@Override
	public void setOrientation(float orientation) {
		
		
	}

	@Override
	public float vectorToAngle(Vector2 vector) {
		return (float)Math.atan2(-vector.x, vector.y);
	}

	@Override
	public Vector2 angleToVector(Vector2 outVector, float angle) {
		outVector.x = -(float)Math.sin(angle);
		outVector.y = (float)Math.cos(angle);
		return outVector;
	}

	@Override
	public Location<Vector2> newLocation() {
		
		return null;
	}

	@Override
	public float getZeroLinearSpeedThreshold() {
		
		return 0;
	}

	@Override
	public void setZeroLinearSpeedThreshold(float value) {
		
		
	}

	@Override
	public float getMaxLinearSpeed() {
		
		return 1;
	}

	@Override
	public void setMaxLinearSpeed(float maxLinearSpeed) {
		
		
	}

	@Override
	public float getMaxLinearAcceleration() {
		
		return Gdx.graphics.getDeltaTime() * 2;
	}

	@Override
	public void setMaxLinearAcceleration(float maxLinearAcceleration) {
		
		
	}

	@Override
	public float getMaxAngularSpeed() {
		
		return 1;
	}

	@Override
	public void setMaxAngularSpeed(float maxAngularSpeed) {
		
		
	}

	@Override
	public float getMaxAngularAcceleration() {
		
		return 1;
	}

	@Override
	public void setMaxAngularAcceleration(float maxAngularAcceleration) {
		
		
	}

	@Override
	public void setOwner(Steerable<Vector2> owner) {
		
		
	}

	@Override
	public boolean isTagged() {
		
		return false;
	}

	@Override
	public void setTagged(boolean tagged) {
		
		
	}
	
	public btRigidBody getMainBody() {
		return bodies.get(mainBodyIndex);
	}
	
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
	
	public Matrix4 getMainTrans() {
		return mainTrans;
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
	
	public ArrayList<Matrix4> getMatrixes() {
		return matrixes;
	}

	public void setRotation(float x, float y, float z, float angle) {
		modelInstance.transform.rotate(x, y, z, angle);
		
		//setCollisions();
		
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
