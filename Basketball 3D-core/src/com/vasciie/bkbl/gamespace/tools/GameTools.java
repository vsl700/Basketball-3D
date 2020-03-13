/**
 * 
 */
package com.vasciie.bkbl.gamespace.tools;

import java.util.ArrayList;

import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector3;
import com.vasciie.bkbl.gamespace.entities.Player;

/**
 * A class containing useful functions like getting the closest to an object player
 * 
 * @author studi
 *
 */
public class GameTools {
	
	public static float getDistanceBetweenSteerables(Location<Vector3> st1, Location<Vector3> st2) {
		return st1.getPosition().dst(st2.getPosition());
	}
	
	/**
	 * 
	 * @param position
	 * @param positions
	 * @return the vector which is on the shortest distance of all other positions
	 *         from the given position
	 */
	public static Vector3 getShortestDistanceWVectors(Vector3 position, ArrayList<Vector3> positions) {
		Vector3 tempVec = positions.get(0);
		float dist = position.dst2(tempVec);
		for (int i = 1; i < positions.size(); i++) {
			// Matrix4 tempTrans2 =
			// position.get(i).getModelInstance().transform;

			Vector3 tempVec2 = positions.get(i);

			float dist2 = position.dst2(tempVec2);

			if (dist2 < dist) {
				dist = dist2;
				tempVec = tempVec2;
			}
		}

		return tempVec;
	}

	public static Vector3 getShortestDistance(Vector3 position, ArrayList<Player> players) {
		Vector3 tempVec = players.get(0).getPosition();
		float dist = position.dst2(tempVec);
		// Vector3 diff = position.cpy().sub(tempTeamVec);
		for (int i = 1; i < players.size(); i++) {
			Vector3 tempVec2 = players.get(i).getPosition();

			float dist2 = position.dst2(tempVec2);

			if (dist2 < dist) {
				dist = dist2;
				tempVec = tempVec2;
			}
		}

		return tempVec;
	}
	
	/**
	 * 
	 * @param position - The position of the player that wants to interact with the closest to it player
	 * @param players - The players for check for distances
	 * @param ignored - Players that should be ignored (for example if we are using this to see which player we should block from getting somewhere, the next player should ignore
	 * him and find someone else to block. For example in the co-op state)
	 * @return The player which is closest to the given position
	 */
	public static Player getClosestPlayer(Vector3 position, ArrayList<Player> players, ArrayList<Player> ignored) {
		int count = 0;
		Player tempPlayer = null;
		for(int i = 0; i < players.size(); i++) {
			if(ignored != null && ignored.contains(players.get(i))) //They can't be all players ignored, there's always someone left free
				continue;
			
			tempPlayer = players.get(i);
			count = i;
			break;
		}
		
		float dist = position.dst2(tempPlayer.getPosition());
		for (int i = count; i < players.size(); i++) {
			Player tempPlayer2 = players.get(i);
			if(ignored != null && ignored.contains(tempPlayer2))
				continue;

			float dist2 = position.dst2(tempPlayer2.getPosition());

			if (dist2 < dist) {
				dist = dist2;
				tempPlayer = tempPlayer2;
			}
		}
		
		return tempPlayer;
	}
	
	/*public static Vector3[] getRotatedPositionsGroup(Vector3[] group, float angle) {
		Matrix4 rectOfRotation = new Matrix4().rotate(0, 1, 0, angle);
		
		
		return group;
	}*/
	
}
