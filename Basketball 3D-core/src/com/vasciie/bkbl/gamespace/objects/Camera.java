package com.vasciie.bkbl.gamespace.objects;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;

public class Camera extends GameObject {

	@Override
	protected void createModels() {
		

	}

	@Override
	protected void createCollisionShapes() {
		
		visibleCollShapes.add(new btBoxShape(new Vector3(getWidth() * 2, getHeight() / 2, getDepth() * 2)));
	}

	@Override
	protected void manuallyRecalcCollisions() {
		

	}

	@Override
	protected void manuallySetObjects() {
		

	}

	@Override
	protected void manuallySetBodies() {
		

	}

	@Override
	public float getWidth() {
		
		return 0.5f;
	}

	@Override
	public float getHeight() {
		
		return 0.5f;
	}

	@Override
	public float getDepth() {
		
		return 0.5f;
	}

}
