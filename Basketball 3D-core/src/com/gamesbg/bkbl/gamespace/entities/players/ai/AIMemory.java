package com.gamesbg.bkbl.gamespace.entities.players.ai;

import com.badlogic.gdx.math.Vector3;

/**
 * This class is used to store the player's brain's data, or in other words, this is the memory component of each brain of a player.
 * @author User
 *
 */
public class AIMemory {
	
	Vector3 shootVec;
	
	//Just some player AI's properties, or in other words, imitating some strategy
	float dribbleTime, aimingTime, shootTime, switchHandTime, catchTime;
	float distDiff, resetTime;
	
	boolean ballJustShot;
	
	public AIMemory() {
		shootTime = 20;
	}

	public Vector3 getShootVec() {
		return shootVec;
	}


	public void setShootVec(Vector3 shootVec) {
		this.shootVec = shootVec;
	}


	public float getDribbleTime() {
		return dribbleTime;
	}

	public void setDribbleTime(float dribbleTime) {
		this.dribbleTime = dribbleTime;
	}

	public float getAimingTime() {
		return aimingTime;
	}

	public void setAimingTime(float aimingTime) {
		this.aimingTime = aimingTime;
	}

	public float getShootTime() {
		return shootTime;
	}

	public void setShootTime(float shootTime) {
		this.shootTime = shootTime;
	}

	public float getSwitchHandTime() {
		return switchHandTime;
	}

	public void setSwitchHandTime(float switchHandTime) {
		this.switchHandTime = switchHandTime;
	}

	public float getCatchTime() {
		return catchTime;
	}

	public void setCatchTime(float catchTime) {
		this.catchTime = catchTime;
	}

	public float getDistDiff() {
		return distDiff;
	}

	public void setDistDiff(float distDiff) {
		this.distDiff = distDiff;
	}

	public float getResetTime() {
		return resetTime;
	}

	public void setResetTime(float resetTime) {
		this.resetTime = resetTime;
	}

	public boolean isBallJustShot() {
		return ballJustShot;
	}

	public void setBallJustShot(boolean ballJustShot) {
		this.ballJustShot = ballJustShot;
	}
	
	
}
