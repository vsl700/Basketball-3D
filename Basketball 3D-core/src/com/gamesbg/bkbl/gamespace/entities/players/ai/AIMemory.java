package com.gamesbg.bkbl.gamespace.entities.players.ai;

/**
 * This class is used to store the player's brain's data, or in other words, this is the memory component of each brain of a player.
 * @author User
 *
 */
public class AIMemory {
	float dribbleTime, aimingTime, shootTime, switchHandTime;
	float distDiff = 0, resetTime = 0;
	
	boolean ballJustShot;

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
