package com.gamesbg.bkbl.gamespace.objects;

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
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.gamesbg.bkbl.gamespace.MotionState;

public class Terrain extends GameObject {
	
	static final float wallDepth = 5f;

	@Override
	protected void createModels() {
		ModelBuilder mb = new ModelBuilder();
		
		Texture court = new Texture(Gdx.files.internal("game/basketball_court.jpg"));
		court.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
		court.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		
		Material material = new Material(TextureAttribute.createDiffuse(court));
		long attribs = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates;
		
		model = mb.createBox(getWidth(), getHeight(), getDepth(), material, attribs);
		model.manageDisposable(court);
		
		modelInstance = new ModelInstance(model, x, y - getHeight() / 2, z);
		
		matrixes.add(modelInstance.transform);
	}

	@Override
	protected void createCollisionShapes() {
		
		visibleCollShapes.add(new btBoxShape(new Vector3(getWidth() * 2, getHeight() / 2, getDepth() * 2)));
		
		invisibleCollShapes.add(new btBoxShape(new Vector3(wallDepth, 10000, getDepth()*5)));
		invisibleCollShapes.add(new btBoxShape(new Vector3(getWidth()*5, 10000, wallDepth)));
		invisibleCollShapes.add(new btBoxShape(new Vector3(wallDepth, 10000, getDepth()*5)));
		invisibleCollShapes.add(new btBoxShape(new Vector3(getWidth()*5, 10000, wallDepth)));
		
		//invisibleCollShapes.add(new btBoxShape(new Vector3(getWidth() * 2, 10000, 0.1f)));//Midcourt lane
	}

	@Override
	protected void specialFunction() {
		

	}

	@Override
	public float getWidth() {
		
		return 30;
	}

	@Override
	public float getHeight() {
		
		return 3f;
	}

	@Override
	public float getDepth() {
		
		return 60;
	}

	@Override
	protected void setCollisions() {
		super.setCollisions();
		
		bodies.get(0).setRestitution(0.5f);
		bodies.get(0).setFriction(1.0f);
		
		invisBodies = new ArrayList<btRigidBody>();
		
		for(btCollisionShape shape : invisibleCollShapes) {
			btRigidBody.btRigidBodyConstructionInfo constInfo = new btRigidBody.btRigidBodyConstructionInfo(0, null, shape, localInertia);
			invisConstructionInfos.add(constInfo);
			invisBodies.add(new btRigidBody(constInfo));
		}
		
		manuallySetBodies();
		
		for(int i = 0; i < invisBodies.size(); i++) {
			invisMotionStates.add(new MotionState());
			invisMotionStates.get(i).transform = invisBodies.get(i).getWorldTransform();
			invisBodies.get(i).setMotionState(invisMotionStates.get(i));
			invisBodies.get(i).setFriction(1.0f);
			invisBodies.get(i).setRestitution(0.1f);
		}
		
		ArrayList<btCollisionShape> laneShapes = new ArrayList<btCollisionShape>();
		laneShapes.add(new btBoxShape(new Vector3(getWidth() * 2, 10000, 0.1f)));
		
		for(int i = 0; i < laneShapes.size(); i++) {
			collisionObjects.add(new btCollisionObject());
			collisionObjects.get(i).setCollisionShape(laneShapes.get(i));
		}
		
		manuallySetObjects();
	}

	@Override
	protected void manuallyRecalcCollisions() {
		manuallySetBodies();
		
		for(int i = 0; i < invisBodies.size(); i++) {
			invisMotionStates.get(i).transform = invisBodies.get(i).getWorldTransform();
			invisBodies.get(i).setMotionState(invisMotionStates.get(i));
		}
	}

	@Override
	protected void manuallySetObjects() {
		collisionObjects.get(0).setWorldTransform(getMainTrans());
	}

	@Override
	protected void manuallySetBodies() {
		modelInstance.calculateTransforms();
		
		invisBodies.get(0).proceedToTransform(getMainTrans().cpy().mul(new Matrix4().setToTranslation(x - getWidth() / 2 - wallDepth, y, z)));
		invisBodies.get(1).proceedToTransform(getMainTrans().cpy().mul(new Matrix4().setToTranslation(x, y, z - getDepth() / 2 - wallDepth)));
		invisBodies.get(2).proceedToTransform(getMainTrans().cpy().mul(new Matrix4().setToTranslation(x + getWidth() / 2 + wallDepth, y, z)));
		invisBodies.get(3).proceedToTransform(getMainTrans().cpy().mul(new Matrix4().setToTranslation(x, y, z + getDepth() / 2 + wallDepth)));
	}

}
