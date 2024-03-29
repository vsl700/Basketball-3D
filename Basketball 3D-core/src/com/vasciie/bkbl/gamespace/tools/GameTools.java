/**
 * 
 */
package com.vasciie.bkbl.gamespace.tools;

import java.util.ArrayList;

import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.vasciie.bkbl.gamespace.entities.Player;
import com.vasciie.bkbl.gamespace.entities.players.Teammate;
import com.vasciie.bkbl.gamespace.zones.Zones.Zone;

/**
 * A class containing useful functions like getting the closest to an object player
 * 
 * @author studi
 *
 */
public final class GameTools {
	
	public static float[] beautyX1(int totalBtns, int screenW, float btnSpace, float btnWidth) {
		return beautyX1(totalBtns, 1, screenW, btnSpace, btnWidth)[0];
	}
	
	public static float[][] beautyX1(int totalBtns, int split, int screenW, float btnSpace, float btnWidth) {
		int parts = (int) Math.ceil(totalBtns * 1.0 / split);
		float[][] temp = new float[split][];
		
		int tempTotal = totalBtns;
		for(int i = 0; i < split; i++) {
			if(i + 1 == split)
				temp[i] = new float[tempTotal];
			else {
				temp[i] = new float[parts];
				tempTotal -= parts;
			}
		}
		
		for (int i = 0; i < split; i++) {
			for (int j = 0; j < temp[i].length; j++) {
				if(i + 1 == split && split > 1)
					temp[i][j] = getBeautyX1(totalBtns - parts, screenW, btnSpace, btnWidth, j);
				else temp[i][j] = getBeautyX1(parts, screenW, btnSpace, btnWidth, j);
			}
		}
		
		return temp;
	}
	
	private static float getBeautyX1(int totalBtns, int screenW, float btnSpace, float btnWidth, int i) {
		return screenW / 2 + btnSpace / 2 - (totalBtns / 2.0f - i) * (btnWidth + btnSpace);
	}
	
	private static final StringBuilder sb = new StringBuilder();
	public static String convertTimeToString(float time) {
		sb.append((int) time / 60);
		sb.append(":");
		int secs = ((int) time - ((int) time / 60) * 60);
		if(secs < 10)
			sb.append(0);
		sb.append(secs);
		
		String temp = sb.toString();
		sb.delete(0, sb.length());
		
		return temp;
	}
	
	private static final Vector3 tempVec = new Vector3();
	public static boolean isObjectVisibleToScreen(Camera cam, ModelInstance instance, Vector3 dimensions) {
		instance.transform.getTranslation(tempVec);
		return cam.frustum.boundsInFrustum(tempVec, dimensions);
	}
	
	public static boolean isObjectVisibleToScreen(Camera cam, ModelInstance instance, float radius) {
		instance.transform.getTranslation(tempVec);
		return cam.frustum.sphereInFrustum(tempVec, radius);
	}
	
	public static float getDistanceBetweenLocations(Location<Vector3> st1, Location<Vector3> st2) {
		if(st1 == null || st2 == null)
			return 0;
		
		return st1.getPosition().dst(st2.getPosition());
	}
	
	public static Vector2 toVector2(Vector3 vec, Vector2 input) {
		return input.set(vec.x, vec.z);
	}
	
	public static Vector3 toVector3(Vector2 vec, Vector3 input) {
		return input.set(vec.x, 0, vec.y);
	}
	
	public static Array<Player> playersInZone(Player player, Zone zone){
		Array<Player> players = new Array<Player>(player.getMap().getTeammates().size());//Heard that libGDX's Array works faster than Java's ArrayList...
		
		ArrayList<Player> tempTeam;
		if(player instanceof Teammate)
			tempTeam = player.getMap().getTeammates();
		else tempTeam = player.getMap().getOpponents();
		
		for(Player p : tempTeam) {
			if(!p.equals(player) && zone.checkZone(p.getPosition()/*, p.getDimensions()*/))
				players.add(p);
		}
		
		return players;
	}
	
	public static Array<Player> playersOutOfZone(Player player, Zone zone){
		Array<Player> players = new Array<Player>(player.getMap().getTeammates().size());//Heard that libGDX's Array works faster than Java's ArrayList...
		
		ArrayList<Player> tempTeam;
		if(player instanceof Teammate)
			tempTeam = player.getMap().getTeammates();
		else tempTeam = player.getMap().getOpponents();
		
		for(Player p : tempTeam) {
			if(!p.equals(player) && !zone.checkZone(p.getPosition()/*, p.getDimensions()*/))
				players.add(p);
		}
		
		return players;
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
