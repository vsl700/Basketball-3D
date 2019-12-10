package com.gamesbg.bkbl.gamespace.entities.players.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.steer.behaviors.CollisionAvoidance;
import com.badlogic.gdx.ai.steer.behaviors.LookWhereYouAreGoing;
import com.badlogic.gdx.ai.steer.behaviors.Pursue;
import com.badlogic.gdx.ai.steer.behaviors.RaycastObstacleAvoidance;
import com.badlogic.gdx.ai.steer.behaviors.Seek;
import com.badlogic.gdx.ai.steer.utils.RayConfiguration;
import com.badlogic.gdx.ai.steer.utils.rays.ParallelSideRayConfiguration;
import com.gamesbg.bkbl.gamespace.entities.Player;
import com.gamesbg.bkbl.gamespace.entities.players.Opponent;
import com.gamesbg.bkbl.gamespace.entities.players.Teammate;

/**
 * Each player needs a brain to think with, otherwise there will be no point of having a head on the players' models ;)
 * 
 * @author studi
 *
 */
public class Brain {

	//The thinking part
	StateMachine<Player, PlayerState> stateMachine;
	
	//The memory part (each brain needs memory to remember some properties, like player's targets or current strategy properties)
	AIMemory memory;
	
	//The player that uses the brain
	Player user;
	
	//Some player behaviors
	Seek<Vector3> pursue;
	LookWhereYouAreGoing<Vector3> lookAt;
	CollisionAvoidance<Vector3> collAvoid; //For players and basket stands
	RaycastObstacleAvoidance<Vector3> obstAvoid; //For invisible terrain walls
	
	RayConfiguration<Vector3> rayConfig;
	
	public Brain(Player user) {
		this.user = user;
		
		stateMachine = new DefaultStateMachine<Player, PlayerState>(user, PlayerState.IDLING);
		memory = new AIMemory();
		
		pursue = new Seek<Vector3>(user, user.getMap().getBall());
		lookAt = new LookWhereYouAreGoing<Vector3>(user);
		collAvoid = new CollisionAvoidance<Vector3>(user, user);
		
		rayConfig = new ParallelSideRayConfiguration<Vector3>(user, 3, 0.5f);
		obstAvoid = new RaycastObstacleAvoidance<Vector3>(user, rayConfig);
	}
	
	public void update() {
		memory.setShootTime(memory.getShootTime() + Gdx.graphics.getDeltaTime());
		//System.out.println(memory.getShootTime());
		memory.setResetTime(memory.getResetTime() + Gdx.graphics.getDeltaTime());
		
		//The state switcher
		if(user.getMap().isGameRunning()) {
			//If the current player is holding the ball
			if(user.holdingBall())
				stateMachine.changeState(PlayerState.BALL_IN_HAND);
			
			else {
				
				//If a teammate of the current player (either a teammate or an opponent to the main player) is holding the ball
				if(user instanceof Teammate && user.getMap().isBallInTeam() || user instanceof Opponent && user.getMap().isBallInOpp())
					stateMachine.changeState(PlayerState.COOPERATIVE);
				
				//If an opponent of the current player (either a teammate or an opponent to the main player) is holding the ball
				else if(user instanceof Teammate && user.getMap().isBallInOpp() || user instanceof Opponent && user.getMap().isBallInTeam())
					stateMachine.changeState(PlayerState.PLAYER_SURROUND);
			
				//If nobody is holding the ball
				else 
					stateMachine.changeState(PlayerState.BALL_CHASING);
			}
		}
		//If the game is actually not running (the startup timer is still counting or a game rule has been broken), just go to idling mode
		else 
			stateMachine.changeState(PlayerState.IDLING);
		
		//Regular state updating
		stateMachine.update();
	}

	public StateMachine<Player, PlayerState> getStateMachine() {
		return stateMachine;
	}

	public AIMemory getMemory() {
		return memory;
	}

	public Seek<Vector3> getPursue() {
		return pursue;
	}

	public LookWhereYouAreGoing<Vector3> getLookAt() {
		return lookAt;
	}

	public CollisionAvoidance<Vector3> getCollAvoid() {
		return collAvoid;
	}

	public RaycastObstacleAvoidance<Vector3> getObstAvoid() {
		return obstAvoid;
	}
	
}
