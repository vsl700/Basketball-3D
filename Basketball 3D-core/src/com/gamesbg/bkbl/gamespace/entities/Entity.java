package com.gamesbg.bkbl.gamespace.entities;

import java.util.ArrayList;

import com.badlogic.gdx.ai.steer.Proximity;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.gamesbg.bkbl.gamespace.GameMap;
import com.gamesbg.bkbl.gamespace.MotionState;
import com.gamesbg.bkbl.gamespace.objects.ObjectType;

public abstract class Entity implements Proximity<Vector3>, Steerable<Vector3> {
	
	protected EntityType type;
	protected GameMap map;
	protected ArrayList<MotionState> motionStates;
	protected ArrayList<MotionState> invisMotionStates;
	protected Model model;
	protected ModelInstance modelInstance;
	protected ArrayList<btCollisionShape> collisionShapes;
	protected ArrayList<btCollisionShape> invisCollShapes; //Used to hide collision shapes from the Entity's automatic adding method if we want to build bodies or objects manually
	protected ArrayList<btRigidBody> bodies;
	protected ArrayList<btCollisionObject> collisionObjects; //That idea came from the fact that the ball (a rigid body) cannot collide dynamically with a collision object (the basket's entering part)
	protected ArrayList<btRigidBody> invisBodies;
	protected ArrayList<btRigidBody.btRigidBodyConstructionInfo> constructionInfos;
	protected ArrayList<btRigidBody.btRigidBodyConstructionInfo> invisConstructionInfos;
	
	protected ArrayList<Matrix4> matrixes; //In case we have multiple collision parts we make this which will contain the transform for each collision object
	
	protected ArrayList<btCollisionObject> manualSetTransformsBody, manualSetTransformsInvBody, manualSetTransformsObj;
	
	protected ArrayList<btCollisionObject> outsideColliders;
	
	protected int mainBodyIndex;
	
	protected float maxLinAccel;
	protected float boundRadius;
	
	protected boolean grounded; //We need this because of the ball, which should be caught even if it collides with other parts of the body only when it's on ground!
	
	float timeout;
	
	
	public void create(EntityType type, GameMap map, Vector3 pos) {
		this.type = type;
		this.map = map;
		
		matrixes = new ArrayList<Matrix4>();
		
		outsideColliders = new ArrayList<btCollisionObject>();
		
		collisionShapes = new ArrayList<btCollisionShape>();
		bodies = new ArrayList<btRigidBody>();
		motionStates = new ArrayList<MotionState>();
		constructionInfos = new ArrayList<btRigidBody.btRigidBodyConstructionInfo>();
		
		createModels(pos);
		modelInstance.calculateTransforms();
		createCollisions();
		createCollisionObjectAndBodies();
		setCollisionTransform(true);
		
		removeCollCheckOnInternals();
		
		boundRadius = Math.max(getWidth() / 2, getDepth() / 2);
	}
	
	private void removeCollCheckOnInternals() {
		ArrayList<btRigidBody> tempBodies = new ArrayList<btRigidBody>();
		
		tempBodies.addAll(getBodies());
		
		if(invisBodies != null)
			tempBodies.addAll(getInvisBodies());
		
		for(int i = 0; i < tempBodies.size() - 1; i++) {
			for(int j = 0; j < tempBodies.size(); j++) {
				tempBodies.get(i).setIgnoreCollisionCheck(tempBodies.get(j), true);
			}
		}
	}
	
	public abstract void update(float delta);
	
	public void render(ModelBatch mBatch, Environment e) {
		mBatch.render(modelInstance, e);
	}
	
	public void dispose() {
		model.dispose();
		for(btCollisionObject o : collisionObjects)
			o.dispose();
		
		for(btRigidBody o : bodies)
			o.dispose();
		
		for(btCollisionShape s : collisionShapes)
			s.dispose();
		
		for(btCollisionShape s : invisCollShapes)
			s.dispose();
		
		for(btRigidBody.btRigidBodyConstructionInfo constInfo : constructionInfos)
			constInfo.dispose();
		
		for(MotionState ms : motionStates)
			ms.dispose();
	}
	
	/**
	 * This method should be called when the update cycle is ending to clear the booleans and get them ready for the next cycle
	 */
	public void onCycleEnd() {
		if(isTimeoutOver()) {
			grounded = false;
			timeout = 0;
		}
		
		outsideColliders.clear();
	}
	
	/**
	 * This void should be called when a collision between an object from this entity and an object from another one occurs
	 */
	public void collisionOccured(btCollisionObject objInside, btCollisionObject objOutside) {
		if(map.getObjectsMap().get(objOutside.getUserValue()).equals(ObjectType.TERRAIN.getId())) {
			grounded = true;
		}
		
		outsideColliders.add(objOutside);
	}
	
	/**
	 * The modelInstance does not calculate global transforms of nodes according to its own one so I had to create this method that does this thing
	 * @param nodeTrans - The transform of the node
	 * @return the transform of the given node according to the transform of the model instance
	 */
	public Matrix4 calcTransformFromNodesTransform(Matrix4 nodeTrans) {
		return modelInstance.transform.cpy().mul(nodeTrans);
	}
	
	protected abstract void createModels(Vector3 pos);
	
	protected abstract void createCollisions();
	
	protected void createCollisionObjectAndBodies() {
		for (int i = 0; i < collisionShapes.size(); i++) {
			Vector3 localInertia = new Vector3();
			
			if(i == mainBodyIndex)
				collisionShapes.get(i).calculateLocalInertia(type.getMass(), localInertia);
			else collisionShapes.get(i).calculateLocalInertia(0, localInertia);
			
			motionStates.add(new MotionState());
			
			if(i == mainBodyIndex)
				motionStates.get(i).transform = matrixes.get(i);
			else motionStates.get(i).transform = calcTransformFromNodesTransform(matrixes.get(i));
			
			if(i == mainBodyIndex)
				constructionInfos.add(new btRigidBody.btRigidBodyConstructionInfo(type.getMass(), motionStates.get(i), collisionShapes.get(i), localInertia));
			else constructionInfos.add(new btRigidBody.btRigidBodyConstructionInfo(0, motionStates.get(i), collisionShapes.get(i), localInertia));
			constructionInfos.get(i).setRestitution(1.0f);
			constructionInfos.get(i).setFriction(0.3f);
			
			bodies.add(new btRigidBody(constructionInfos.get(i)));
		}
		
	}
	
	public void setCollisionTransform(boolean updateMain) {
		
		int j = 0;
		
		for(int i = 0; i < bodies.size(); i++) {
			if (manualSetTransformsBody != null) {
				if (!manualSetTransformsBody.contains(bodies.get(i))) {
					if (j == mainBodyIndex && updateMain)
						bodies.get(i).proceedToTransform(matrixes.get(j));
					else
						bodies.get(i).setWorldTransform((calcTransformFromNodesTransform(matrixes.get(j))));
				}
			} else {
				if (j == mainBodyIndex && updateMain)
					bodies.get(i).proceedToTransform(matrixes.get(j));
				else
					bodies.get(i).setWorldTransform((calcTransformFromNodesTransform(matrixes.get(j))));
			}
			
			j++;
		}
		
		if (invisBodies != null)
			for (int i = 0; i < invisBodies.size(); i++) {
				if (manualSetTransformsInvBody == null) {
					if (!manualSetTransformsInvBody.contains(invisBodies.get(i)))
						invisBodies.get(i).setWorldTransform(calcTransformFromNodesTransform(matrixes.get(j)));
				}
				else {
					invisBodies.get(i).setWorldTransform(calcTransformFromNodesTransform(matrixes.get(j)));
				}
				
				j++;
			}
		
		if(collisionObjects != null)
			for(int i = 0; i < collisionObjects.size(); i++) {
				if (manualSetTransformsObj != null) {
					if (!manualSetTransformsObj.contains(collisionObjects.get(i))) {
						collisionObjects.get(i).setWorldTransform(calcTransformFromNodesTransform(matrixes.get(j)));
						
						j++;
					}
				}
				else {
					collisionObjects.get(i).setWorldTransform(calcTransformFromNodesTransform(matrixes.get(j)));
					
					j++;
				}
			}
		
	}
	
	public void setWorldTransform(Matrix4 trans) {
		matrixes.get(mainBodyIndex).set(trans);
		setCollisionTransform(true);
	}
	
	/**
	 * Set the entity's transform to a copy of the given one
	 * @param trans - the transform to be copied to the entity's one
	 */
	public void setCopyTransform(Matrix4 trans) {
		matrixes.get(mainBodyIndex).set(trans.val);
		setCollisionTransform(true);
	}
	
	private boolean isTimeoutOver() {
		if(timeout > 13) {
			return true;
		}
		else {
			timeout++;
			return false;
		}
	}
	
	public boolean isProximityColliding(Steerable<Vector3> other) {
		float dst = getPosition().dst(other.getPosition());
		return dst <= getBoundingRadius() || dst <= other.getBoundingRadius();
	}

	@Override
	public Vector3 getLinearVelocity() {
		return getMainBody().getLinearVelocity();
	}

	@Override
	public float getAngularVelocity() {
		
		return getMainBody().getAngularVelocity().y;
	}

	@Override
	public float getBoundingRadius() {
		
		return boundRadius;
	}
	
	public void setBoundingRadius(float bound) {
		boundRadius = bound;
	}

	@Override
	public Steerable<Vector3> getOwner() {
		
		return this;
	}

	@Override
	public int findNeighbors (ProximityCallback<Vector3> callback) {
		int count = 0;
		for(Player p : map.getTeammates())
			if(!p.equals(this) && callback.reportNeighbor(p))
				count++;
		
		for(Player p : map.getOpponents())
			if(!p.equals(this) && callback.reportNeighbor(p))
				count++;
		
		if(callback.reportNeighbor(map.getHomeBasket()) || callback.reportNeighbor(map.getAwayBasket()))
			count++;
		
		return count;
	}

	@Override
	public Vector3 getPosition() {
		return modelInstance.transform.getTranslation(new Vector3());
	}

	@Override
	public float getOrientation() {
		
		return modelInstance.transform.getRotation(new Quaternion()).getYawRad();
	}

	@Override
	public void setOrientation(float orientation) {
		
		modelInstance.transform.set(getPosition(), new Quaternion().setEulerAnglesRad(orientation, 0, 0));
		
		setCollisionTransform(true);
	}

	@Override
	public float vectorToAngle(Vector3 vector) {
		return (float)Math.atan2(-vector.x, vector.z);
	}

	@Override
	public Vector3 angleToVector(Vector3 outVector, float angle) {
		outVector.x = -(float)Math.sin(angle);
		outVector.z = (float)Math.cos(angle);
		return outVector;
	}

	@Override
	public Location<Vector3> newLocation() {
		
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
		
		return maxLinAccel;
	}

	@Override
	public void setMaxLinearAcceleration(float maxLinearAcceleration) {
		
		maxLinAccel = maxLinearAcceleration;
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
	public void setOwner(Steerable<Vector3> owner) {
		
		
	}

	@Override
	public boolean isTagged() {
		
		return false;
	}

	@Override
	public void setTagged(boolean tagged) {
		
		
	}
	
	public EntityType getType() {
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
	
	public btRigidBody getMainBody() {
		return bodies.get(mainBodyIndex);
	}
	
	public ArrayList<btRigidBody> getBodies(){
		return bodies;
	}
	
	public ArrayList<btRigidBody> getInvisBodies(){
		return invisBodies;
	}
	
	public ArrayList<btCollisionObject> getCollisionObjects(){
		return collisionObjects;
	}
	
	public ArrayList<btCollisionObject> getAllCollObjects(){
		ArrayList<btCollisionObject> tempObj = new ArrayList<btCollisionObject>();
		tempObj.addAll(bodies);
		
		if(invisBodies != null)
			tempObj.addAll(invisBodies);
		
		if(collisionObjects != null)
			tempObj.addAll(collisionObjects);
		
		
		return tempObj;
	}
	
	public ArrayList<btCollisionObject> getOutsideColliders(){
		return outsideColliders;
	}
	
	public boolean isGrounded() {
		return grounded;
	}

	public abstract float getWidth();
	
	public abstract float getHeight();
	
	public abstract float getDepth();
	
}
