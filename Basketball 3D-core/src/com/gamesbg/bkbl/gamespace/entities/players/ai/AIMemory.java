package com.gamesbg.bkbl.gamespace.entities.players.ai;

import com.badlogic.gdx.math.Vector3;
import com.gamesbg.bkbl.gamespace.entities.Player;

/**
 * This class is used to store the player's brain's data, or in other words, this is the memory component of each brain of a player.
 * The values inside this class are ONLY used by the AI. That's why they exist here and not in the Player class.
 * @author User
 *
 */
public class AIMemory {
	
	//A player to get in front of to block it from catching a ball for example
	Player blockPlayer;
	
	//The targetVec represents the position of the target, while shootVec represents the point where the player has to shoot so that he can hit the target with the ball
	Vector3 shootVec, targetVec;
	
	float dribbleTime, aimingTime, shootTime, switchHandTime, catchTime;
	float distDiff, resetTime;
	
	boolean ballJustShot;
	boolean ballChaser; //Whether this player is chosen to chase the ball while in chase mode
	
	public AIMemory() {
		shootTime = 20;
	}

	public Player getBlockPlayer() {
		return blockPlayer;
	}

	public void setBlockPlayer(Player blockPlayer) {
		this.blockPlayer = blockPlayer;
	}

	public Vector3 getShootVec() {
		return shootVec;
	}


	public void setShootVec(Vector3 shootVec) {
		this.shootVec = shootVec;
	}

	public Vector3 getTargetVec() {
		return targetVec;
	}

	public void setTargetVec(Vector3 targetVec) {
		this.targetVec = targetVec;
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

	public boolean isBallChaser() {
		return ballChaser;
	}

	public void setBallChaser(boolean ballChaser) {
		this.ballChaser = ballChaser;
	}
	
	
}
