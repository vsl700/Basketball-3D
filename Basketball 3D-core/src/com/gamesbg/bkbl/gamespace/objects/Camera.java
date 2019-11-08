package com.gamesbg.bkbl.gamespace.objects;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;

public class Camera extends GameObject {

	@Override
	protected void createModels() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void createCollisionShapes() {
		// TODO Auto-generated method stub
		visibleCollShapes.add(new btBoxShape(new Vector3(getWidth() * 2, getHeight() / 2, getDepth() * 2)));
	}

	@Override
	protected void manuallyRecalcCollisions() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void manuallySetObjects() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void manuallySetBodies() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void specialFunction() {
		// TODO Auto-generated method stub

	}

	@Override
	public float getWidth() {
		// TODO Auto-generated method stub
		return 0.5f;
	}

	@Override
	public float getHeight() {
		// TODO Auto-generated method stub
		return 0.5f;
	}

	@Override
	public float getDepth() {
		// TODO Auto-generated method stub
		return 0.5f;
	}

}
