package com.vasciie.bkbl.gamespace.objects;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.vasciie.bkbl.gamespace.MotionState;

public class Terrain extends GameObject {
	
	ModelInstance customPlate;
	
	TerrainThemes theme;
	
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
		matrixes.add(new Matrix4());//Walls
		matrixes.add(new Matrix4());
		matrixes.add(new Matrix4());
		matrixes.add(new Matrix4());
	}
	
	public void createTheme() {
		theme = chooseTheme();
		
		if(theme != null) {
			theme.createModels(this);
			
			customPlate = theme.getCustomTerrainModelInstance();
		}
	}
	
	public void clearTheme() {
		theme.dispose();
		theme = null;
		
		customPlate = null;
	}
	
	private TerrainThemes chooseTheme() {
		switch(map.getDifficulty()) {
			case 0: return TerrainThemes.EASY;
			case 1: return TerrainThemes.HARD;
			case 2: return TerrainThemes.VERYHARD;
			default: return null;
		}
	}
	
	@Override
	public void render(ModelCache mCache) {
		if(theme != null) {
			for(ModelInstance m : TerrainThemes.modelInstances) {
				mCache.add(m);
			}
			
			if(customPlate != null)
				mCache.add(customPlate);
			else mCache.add(modelInstance);
		}else mCache.add(modelInstance);
	}

	@Override
	protected void createCollisionShapes() {
		
		visibleCollShapes.add(new btBoxShape(new Vector3(getWidth() * 2, getHeight() / 2, getDepth() * 2)));
		
		invisibleCollShapes.add(new btBoxShape(new Vector3(wallDepth, 10000, getDepth()*5)));//Wide wall
		invisibleCollShapes.add(new btBoxShape(new Vector3(getWidth()*5, 10000, wallDepth)));//Short wall
		invisibleCollShapes.add(new btBoxShape(new Vector3(wallDepth, 10000, getDepth()*5)));
		invisibleCollShapes.add(new btBoxShape(new Vector3(getWidth()*5, 10000, wallDepth)));
		
		//invisibleCollShapes.add(new btBoxShape(new Vector3(getWidth() * 2, 10000, 0.1f)));//Midcourt lane
	}

	@Override
	protected void specialFunction() {
		

	}
	
	@Override 
	public void dispose() {
		super.dispose();
		
		if(theme != null)
			theme.dispose();
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

	public static float getWalldepth() {
		return wallDepth;
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
		
		collisionObjects = new ArrayList<btCollisionObject>();
		
		ArrayList<btCollisionShape> zoneShapes = new ArrayList<btCollisionShape>();
		zoneShapes.add(new btBoxShape(new Vector3(getWidth() * 2, 10000, getDepth() / 2)));
		
		for(int i = 0; i < zoneShapes.size(); i++) {
			collisionObjects.add(new btCollisionObject());
			collisionObjects.get(i).setCollisionShape(zoneShapes.get(i));
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
		collisionObjects.get(0).setWorldTransform(getMainTrans().cpy().trn(0, 0, getDepth() / 2 + 2.5f)); //Sends the zone to Teammates' zone
	}

	@Override
	protected void manuallySetBodies() {
		modelInstance.calculateTransforms();
		
		int index = 1;
		int index1 = 0;
		matrixes.get(index).set(getMainTrans().cpy().mul(new Matrix4().setToTranslation(x - getWidth() / 2 - wallDepth, y, z)));
		invisBodies.get(index1++).proceedToTransform(matrixes.get(index++));
		
		matrixes.get(index).set(getMainTrans().cpy().mul(new Matrix4().setToTranslation(x, y, z - getDepth() / 2 - wallDepth)));
		invisBodies.get(index1++).proceedToTransform(matrixes.get(index++));
		
		matrixes.get(index).set(getMainTrans().cpy().mul(new Matrix4().setToTranslation(x + getWidth() / 2 + wallDepth, y, z)));
		invisBodies.get(index1++).proceedToTransform(matrixes.get(index++));
		
		matrixes.get(index).set(getMainTrans().cpy().mul(new Matrix4().setToTranslation(x, y, z + getDepth() / 2 + wallDepth)));
		invisBodies.get(index1++).proceedToTransform(matrixes.get(index++));
	}
	
	public btCollisionObject getTeamzone() {
		return collisionObjects.get(0);
	}
	
	public TerrainThemes getTheme() {
		return theme;
	}

}
