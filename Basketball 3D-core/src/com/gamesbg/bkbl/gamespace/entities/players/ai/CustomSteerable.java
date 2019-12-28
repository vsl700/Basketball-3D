package com.gamesbg.bkbl.gamespace.entities.players.ai;

import com.badlogic.gdx.ai.steer.Proximity;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector3;

/**
 * Used as a custom target for the steering behaviors. It only contains position
 * 
 * @author User
 *
 */
public class CustomSteerable implements Steerable<Vector3>, Proximity<Vector3> {
	
	Vector3 position;
	
	public CustomSteerable(Vector3 pos) {
		position = pos;
	}

	@Override
	public Vector3 getPosition() {
		
		return position;
	}

	@Override
	public float getOrientation() {
		
		return 0;
	}

	@Override
	public void setOrientation(float orientation) {
		
		
	}

	@Override
	public float vectorToAngle(Vector3 vector) {
		
		return 0;
	}

	@Override
	public Vector3 angleToVector(Vector3 outVector, float angle) {
		
		return null;
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
		
		return 0;
	}

	@Override
	public void setMaxLinearSpeed(float maxLinearSpeed) {
		
		
	}

	@Override
	public float getMaxLinearAcceleration() {
		
		return 0;
	}

	@Override
	public void setMaxLinearAcceleration(float maxLinearAcceleration) {
		
		
	}

	@Override
	public float getMaxAngularSpeed() {
		
		return 0;
	}

	@Override
	public void setMaxAngularSpeed(float maxAngularSpeed) {
		
		
	}

	@Override
	public float getMaxAngularAcceleration() {
		
		return 0;
	}

	@Override
	public void setMaxAngularAcceleration(float maxAngularAcceleration) {
		
		
	}

	@Override
	public Steerable<Vector3> getOwner() {
		
		return this;
	}

	@Override
	public void setOwner(Steerable<Vector3> owner) {
		
		
	}

	@Override
	public int findNeighbors(ProximityCallback<Vector3> callback) {
		
		return 0;
	}

	@Override
	public Vector3 getLinearVelocity() {
		
		return null;
	}

	@Override
	public float getAngularVelocity() {
		
		return 0;
	}

	@Override
	public float getBoundingRadius() {
		
		return 0;
	}

	@Override
	public boolean isTagged() {
		
		return false;
	}

	@Override
	public void setTagged(boolean tagged) {
		
		
	}

}
